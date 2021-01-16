package jav.basic.nodes;

import jav.Tuple;
import jav.basic.Token;

import java.util.List;

public class FuncDefNode extends Node {

    private final Token varNameToken;
    private final List<Tuple<Token, Token>> argNameTokens;
    private final List<String> returnTypes;
    private final Node bodyNode;

    public FuncDefNode(Token varNameToken, List<Tuple<Token, Token>> argNameTokens, List<String> returnTypes, Node bodyNode) {
        super(varNameToken != null ? varNameToken.getPosStart() : argNameTokens.size() > 0 ? argNameTokens.get(0).getLeft().getPosStart() : bodyNode.getPosStart(), bodyNode.getPosEnd());
        this.varNameToken = varNameToken;
        this.argNameTokens = argNameTokens;
        this.returnTypes = returnTypes;
        this.bodyNode = bodyNode;
    }

    public Token getVarNameToken() {
        return varNameToken;
    }

    public List<Tuple<Token, Token>> getArgNameTokens() {
        return argNameTokens;
    }

    public Node getBodyNode() {
        return bodyNode;
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }

    public List<String> getReturnTypes() {
        return returnTypes;
    }
}