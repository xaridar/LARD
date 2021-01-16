package lscript.parsing.nodes;

import lscript.lexing.Token;

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

    public Node getEndValueNode() {
        return endValueNode;
    }

    public Node getStepNode() {
        return stepNode;
    }

    public Node getBodyNode() {
        return bodyNode;
    }

    public Node getStartValueNode() {
        return startValueNode;
    }

    public Token getVarNameToken() {
        return varNameToken;
    }

    public Token getVarTypeToken() {
        return varTypeToken;
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
