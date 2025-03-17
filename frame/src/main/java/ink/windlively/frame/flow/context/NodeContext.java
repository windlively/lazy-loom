package ink.windlively.frame.flow.context;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class NodeContext {

    private Object result;

    private Map<String, Object> var;

    private NodeContext parentNodeContext;

    private AtomicInteger loopIndex = new AtomicInteger(0);

    private int loopCount = -1;

}
