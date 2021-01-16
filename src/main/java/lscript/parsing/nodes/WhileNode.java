package lscript.parsing.nodes;

/**
 * A simple Node representing a while loop.
 */
public class WhileNode extends Node {
    private final Node conditionNode;
    private final Node bodyNode;

    public WhileNode(Node conditionNode, Node bodyNode) {
        super(conditionNode.getPosStart(), bodyNode.getPosEnd());
        this.conditionNode = conditionNode;
        this.bodyNode = bodyNode;
    }

    public Node getConditionNode() {
        return conditionNode;
    }

    public Node getBodyNode() {
        return bodyNode;
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
