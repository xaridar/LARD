package lscript.parsing.nodes;

import lscript.lexing.Position;

public class BreakNode extends Node {
    public BreakNode(Position posStart, Position posEnd) {
        super(posStart, posEnd);
    }
}
