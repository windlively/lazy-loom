package ink.windlively.frame.flow.context;

import lombok.Data;

import java.util.Map;

@Data
public class FlowContext {

    private String currentNodeId;

    private Map<String, Object> flowVariable;

    private Map<String, Object> systemVariable;

    private Map<String, NodeContext> nodeContext;

}
