package xaridar.lscript.parsing.nodes;

import xaridar.lscript.lexing.Position;

/**
 * A datatype that holds two Positions and various other Nodes and Tokens for use in interpretation.
 */
public class Node {

    protected Position posStart;
    protected Position posEnd;

    /**
     * Default constructor for Node.
     * @param posStart - The start position of the node.
     * @param posEnd - The end position of the node.
     */
    public Node(Position posStart, Position posEnd) {
        this.posStart = posStart;
        this.posEnd = posEnd;
    }

    /**
     * @return the start Position of the Node.
     */
    public Position getPosEnd() {
        return posEnd;
    }

    /**
     * @return the end position of the Node.
     */
    public Position getPosStart() {
        return posStart;
    }

    /**
     * @return a boolean determining whether this statement must end with a semicolon.
     */
    public boolean requiresSemicolon() {
        return true;
    }
}
