package ink.windlively.tools.parallelprocessor;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

/**
 * 任务流
 */
@FunctionalInterface
public interface ExecutionFlow extends Function<ExecutionContext, ExecutionContext> {

    Logger log = LoggerFactory.getLogger(ExecutionFlow.class);
//
//    @Override
//    default ExecutionContext apply(ExecutionContext executionContext) {
//        return apply(executionContext, null, null);
//    }
//
//    ExecutionContext apply(ExecutionContext ctx,
//                           Consumer<ExecutionContext> preAction,
//                           Consumer<ExecutionContext> finishAction);

    static ExecutionFlow buildFlow(@NotNull ExecutionGraph executionGraph) {
        return buildFlow(executionGraph, new FlowConfiguration());
    }

    /**
     * 生成一个执行任务，返回的是一个新函数
     *
     * @param executionGraph    执行任务图
     * @param flowConfiguration 执行参数
     *
     * @return
     */
    static ExecutionFlow buildFlow(@NotNull ExecutionGraph executionGraph, @NotNull FlowConfiguration flowConfiguration) {
        SpelExpressionParser expressionParser = new SpelExpressionParser();
        return ctx -> {
            long startTime = System.currentTimeMillis();
            List<List<ExecutionNode>> sortedNodes = executionGraph.getSortedNodes();
            Map<String, CompletableFuture<ExecutionContext>> futureMap = new HashMap<>(16);
            // 按图序构建CompleteFuture
            for (List<ExecutionNode> nodes : sortedNodes) {
                // 同一层级之间的节点没有依赖，且依赖的CompletableFuture一定已经构建完成
                for (ExecutionNode node : nodes) {
                    String currNodeName = node.getName();
                    CompletableFuture<ExecutionContext> future = CompletableFuture.supplyAsync(() -> {
                        try {
                            long t1 = System.currentTimeMillis();
                            // 前置操作
                            ofNullable(flowConfiguration.getPreAction()).ifPresent(c -> c.accept(ctx));
                            String[] depend = node.getDepend();
                            // 遍历依赖节点，确保依赖节点已经执行完成
                            for (String s : depend) {
                                try {
                                    // 阻塞等待
                                    futureMap.get(s).get();
                                    log.info("current node [{}] waiting node [{}] done ({}ms)", currNodeName, s, System.currentTimeMillis() - t1);
                                } catch (InterruptedException | ExecutionException e) {
                                    throw new IllegalStateException("current node [" + currNodeName + "] waiting node [" + s + "] failed", e);
                                }
                            }
                            long t2 = System.currentTimeMillis();
                            log.info("current node [{}] start", currNodeName);

                            // 前置表达式参数检查
                            if (StringUtils.isNotBlank(node.getBeforeCheckExpression()) &&
                                    !expressionParser.parseExpression(node.getBeforeCheckExpression()).getValue(ctx.getCtx(), boolean.class)) {
                                throw new IllegalStateException(String.format("before check failed by expression [%s]", node.getBeforeCheckExpression()));
                            }
                            // 执行节点
                            ExecutionContext ret = node.getExecution().apply(ctx);
                            // 用于标识节点是否正确执行
                            ctx.setVar("$" + currNodeName + "_success", true);

                            // 后置表达式参数检查
                            if (StringUtils.isNotBlank(node.getAfterCheckExpression()) &&
                                    !expressionParser.parseExpression(node.getAfterCheckExpression()).getValue(ctx.getCtx(), boolean.class)) {
                                throw new IllegalStateException(String.format("after check failed by expression [%s]", node.getAfterCheckExpression()));
                            }
                            long tl = System.currentTimeMillis();
                            log.info("current node [{}] execute done (total: {}ms, execution: {}ms)", currNodeName, tl - t1, tl - t2);
                            return ret;
                        } catch (Throwable e) {
                            log.warn("current node [{}] execute failed: {}, skip exception={}", currNodeName, e.getMessage(), node.isSkipOnFail(), e);
                            if (node.isSkipOnFail()) {
                                return ctx;
                            }
                            throw e;
                        } finally {
                            // 后置操作
                            ofNullable(flowConfiguration.getFinalAction()).ifPresent(c -> c.accept(ctx));
                        }
                    }, flowConfiguration.getExecutor() == null ? ForkJoinPool.commonPool() : flowConfiguration.getExecutor());
                    // 将构建好的CompletableFuture放入Map
                    futureMap.put(node.getName(), future);
                }
            }
            try {
                // 组合所有的Future，等待所有节点行完毕
                CompletableFuture<Void> completableFuture = CompletableFuture.allOf(futureMap.values().toArray(new CompletableFuture[0]));
                log.info("starting flow execution down, ttl={}", flowConfiguration.getTimeout());
                if(flowConfiguration.getTimeout() > 0) {
                    completableFuture.get(flowConfiguration.getTimeout(), TimeUnit.SECONDS);
                } else {
                    completableFuture.get();
                }
                log.info("execution flow success ({}ms)", System.currentTimeMillis() - startTime);
                return ctx;
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.warn("execution flow failed: {} ({}ms)", e.getMessage(), System.currentTimeMillis() - startTime);
                throw new IllegalStateException(e);
            }
        };
    }
}
