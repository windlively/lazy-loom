package ink.windlively.frame.flow;

import ink.windlively.frame.flow.context.FlowContext;
import lombok.Data;

import java.util.function.Predicate;

@Data
public class Link {

    private String linkNodeId;

    private Predicate<FlowContext> condition;

}
