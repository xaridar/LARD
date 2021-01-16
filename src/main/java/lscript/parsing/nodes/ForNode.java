package lscript.parsing.nodes;

import lscript.lexing.Token;

/**
 * A simple Node representing a for loop.
 */
public class ForNode extends Node {
    private final Token varTypeToken;
    private final Token varNameToken;
    private final Node startValueNode;
    private final Node endValueNode;
    private final Node stepNode;
    private final Node bodyNode;

    public ForNode(Token varTypeToken, Token varNameToken, Node startValueNode, Node endValueNode, Node stepNode, Node bodyNode) {
        super(varTypeToken.getPosStart(), bodyNode.getPosEnd());
        this.varTypeToken = varTypeToken;
        this.varNameToken = varNameToken;
        this.startValueNode = startValueNode;
        this.endValueNode = endValueNode;
        this.stepNode = stepNode;
        this.bodyNode = bodyNode;
    }

    /**
     * @return the Node representing the value the instance variable of the loop should reach before exiting the loop.
     */
    public Node getEndValueNode() {
        return endValueNode;
    }

    /**
     * @return the Node representing the number of steps the instance variable of the loop should increase for each iteration of the loop.
     */
    public Node getStepNode() {
        return stepNode;
    }

    /**
     * @return the Node to be called during each iteration of the loop.
     */
    public Node getBodyNode() {
        return bodyNode;
    }

    /**
     * @return the Node representing the value the instance variable of the loop should start at.
     */
    public Node getStartValueNode() {
        return startValueNode;
    }

    /**
     * @return the Token representing the name of the instance variable of the loop.
     */
    public Token getVarNameToken() {
        return varNameToken;
    }

    /**
     * @return the Token representing the type of the instance variable of the loop.
     */
    public Token getVarTypeToken() {
        return varTypeToken;
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
