package ink.windlively.tools.parallelprocessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ink.windlively.tools.parallelprocessor.ExecutionGraph.createGraph;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();

        Map<String, Function<ExecutionContext, ExecutionContext>> functionMap = Arrays.stream("A,B,C,D,E,F,G,H,I,J,K,L".split(",")).collect(Collectors.toMap(
                e -> e,
                e -> ctx -> {
                    try {
                        int i = 500 + 500 * random.nextInt(5);
                        Thread.sleep(i);
                        ctx.setVar(e, i);
                        switch (e){
                            case "A":
                                ctx.setVar("a", 1);
                                break;
                            case "B":
                                ctx.setVar("a", 0);
                                break;
                            case "C":
                                ctx.setVar("c", 3);
                                break;
                            case "G":
                                ctx.setVar("g", 9);
                                ctx.setVar("a", 1);
                                break;
                        }
                    } catch (InterruptedException ex) {

                    }
                    return ctx;
                }
        ));

        ExecutionGraph graph = createGraph(new ArrayList<>() {{
            add("A");
            add("B[A](beforeCheck([a] == 1))");
            add("C");
            add("D");
            add("E[A,B](beforeCheck([a]==2); skipOnFail(true))");
            add("F[A,E]");
            add("G[C]");
            add("H[B,C]");
            add("I[G,H]");
            add("J[I](beforeCheck( T(java.util.Arrays).asList(1,2,3).contains([c]) and [g] == [c]*[c] and [c] != null); afterCheck([c] >= 1))");
            add("K[D,E]");
            add("L[E,H]");
        }}, functionMap);

//        System.out.println(graph);
        // ExecutorService executorService = Executors.newCachedThreadPool();
        ExecutionFlow executionFlow = ExecutionFlow.buildFlow(graph);
        executionFlow.apply(new ExecutionContext());
//        System.out.println(context);
//        System.out.println("========================");
//        executionFlow.apply(new ExecutionContext());
//        System.out.println(context1);
    }

}
