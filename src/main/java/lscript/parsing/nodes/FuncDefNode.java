package lscript.parsing.nodes;

import lscript.Tuple;
import lscript.lexing.Token;

import java.util.List;

/**
 * A simple Node representing a function definition.
 */
public class FuncDefNode extends Node {

    private final Token varNameToken;
    private final List<Tuple<Token, Token>> argTokens;
    private final List<String> returnTypes;
    private final Node bodyNode;

    /**
     * @param varNameToken - A Token representing the name of the instance variable of the loop.
     * @param argTokens - A list of tokens representing the arguments of the function.
     * @param returnTypes - A list of Strings containing the return types of the function.
     * @param bodyNode - A Node to be called when the function is called.
     */
    public FuncDefNode(Token varNameToken, List<Tuple<Token, Token>> argTokens, List<String> returnTypes, Node bodyNode) {
        super(varNameToken != null ? varNameToken.getPosStart() : argTokens.size() > 0 ? argTokens.get(0).getLeft().getPosStart() : bodyNode.getPosStart(), bodyNode.getPosEnd());
        this.varNameToken = varNameToken;
        this.argTokens = argTokens;
        this.returnTypes = returnTypes;
        this.bodyNode = bodyNode;
    }

    /**
     * @return a Token representing the name of the function.
     */
    public Token getVarNameToken() {
        return varNameToken;
    }

    /**
     * @return a List of Tuples of types and names for all arguments to the function.
     */
    public List<Tuple<Token, Token>> getArgTokens() {
        return argTokens;
    }

    /**
     * @return the Node to be called when the function is called.
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

    /**
     * @return a List of return types declared by the function.
     */
    public List<String> getReturnTypes() {
        return returnTypes;
    }
}