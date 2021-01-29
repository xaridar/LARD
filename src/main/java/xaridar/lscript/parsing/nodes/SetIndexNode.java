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
 * A simple Node representing an attempt to set the index of an object to a value.
 */
public class SetIndexNode extends Node {
    private final Node left;
    private final Node startIndex;
    private final Node endIndex;
    private final Node val;

    /**
     * @param left - a Node that contains the value to be indexed.
     * @param startIndex - a Node that contains the start index.
     * @param endIndex - a Node that contains the end index.
     * @param val - a Node that contains the value to set to the index.
     */
    public SetIndexNode(Node left, Node startIndex, Node endIndex, Node val) {
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
        this.val = val;
    }

    /**
     * @return the Node to be indexed.
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
     * @return A Node containing the value to set to the given index.
     */
    public Node getVal() {
        return val;
    }
}
