package lscript.parsing.nodes;

import lscript.lexing.Position;

/**
 * A simple Node representing a 'continue' statement.
 */
public class ContinueNode extends Node {
    /**
     * @param posStart - the start Position of this Node.
     * @param posEnd - the end position of this Node.
     */
    public ContinueNode(Position posStart, Position posEnd) {
        super(posStart, posEnd);
    }
}
