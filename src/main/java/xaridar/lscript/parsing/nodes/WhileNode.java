package xaridar.lscript.parsing.nodes;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.1.0
 * @author Xaridar
 */

/**
 * A simple Node representing a while loop.
 */
public class WhileNode extends Node {
    private final Node conditionNode;
    private final Node bodyNode;

    /**
     * @param conditionNode - A Node containing the condition for the loop.
     * @param bodyNode - A Node to be called during each iteration of the loop.
     */
    public WhileNode(Node conditionNode, Node bodyNode) {
        super(conditionNode.getPosStart(), bodyNode.getPosEnd());
        this.conditionNode = conditionNode;
        this.bodyNode = bodyNode;
    }

    /**
     * @return a Node containing the condition for the loop.
     */
    public Node getConditionNode() {
        return conditionNode;
    }

    /**
     * @return a Node to call for each instance of the loop.
     */
    public Node getBodyNode() {
        return bodyNode;
    }

    /**
     * Override of the requiresSemicolon() method in Node.
     * @return false.
     */
    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
