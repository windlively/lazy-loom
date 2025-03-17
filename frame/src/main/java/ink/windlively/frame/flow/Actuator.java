package ink.windlively.frame.flow;

import ink.windlively.frame.flow.context.FlowContext;
import ink.windlively.frame.flow.context.NodeContext;

public interface Actuator {

    String type();

    Object execute(FlowContext context, NodeContext nodeContext);

}
