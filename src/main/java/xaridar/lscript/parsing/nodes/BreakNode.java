package xaridar.lscript.parsing.nodes;

import xaridar.lscript.lexing.Position;

/**
 * A simple Node representing a 'break' statement.
 */
public class BreakNode extends Node {
    /**
     * @param posStart - the start Position of this Node.
     * @param posEnd - the end position of this Node.
     */
    public BreakNode(Position posStart, Position posEnd) {
        super(posStart, posEnd);
    }
}
