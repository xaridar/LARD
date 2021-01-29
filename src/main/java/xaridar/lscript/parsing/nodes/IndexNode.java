package xaridar.lscript.parsing.nodes;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.1.0
 * @author Xaridar
 */

import xaridar.lscript.TokenEnum;
import xaridar.lscript.lexing.Token;

/**
 * A simple Node representing an attempt to index an object.
 */
public class IndexNode extends Node {
    private final Node left;
    private final Node startIndex;
    private final Node endIndex;

    /**
     * @param left - The Node to index.
     * @param startIndex - A Node containing the starting index to access. Can be null.
     * @param endIndex - A Node containing the ending index to access. Can be null.
     */
    public IndexNode(Node left, Node startIndex, Node endIndex) {
        super(left.getPosStart(), endIndex != null ? endIndex.getPosEnd() : startIndex != null ? startIndex.getPosEnd() : left.getPosEnd());
        this.left = left;
        if (startIndex == null && endIndex != null) {
            this.startIndex = new NumberNode(new Token(TokenEnum.TT_INT, Integer.valueOf(0), endIndex.getPosStart(), endIndex.getPosEnd(), null));
            this.endIndex = endIndex;
        } else if (startIndex != null && endIndex == null) {
            this.startIndex = startIndex;
            this.endIndex = new NumberNode(new Token(TokenEnum.TT_INT, Integer.valueOf(-1), startIndex.getPosStart(), startIndex.getPosEnd(), null));
        } else {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }
    }

    /**
     * @return the Node representing the value to index.
     */
    public Node getLeft() {
        return left;
    }

    /**
     * @return A Node containing the starting index to access.
     */
    public Node getStartIndex() {
        return startIndex;
    }

    /**
     * @return A Node containing the ending index to access.
     */
    public Node getEndIndex() {
        return endIndex;
    }

    /**
     * @return a String representation of the Node for debugging.
     */
    @Override
    public String toString() {
        return left.toString() + "[" + startIndex.toString() + ":" + endIndex.toString() + "]";
    }
}
