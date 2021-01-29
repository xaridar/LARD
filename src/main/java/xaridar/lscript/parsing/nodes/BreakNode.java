package xaridar.lscript.parsing.nodes;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.1.0
 * @author Xaridar
 */

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
