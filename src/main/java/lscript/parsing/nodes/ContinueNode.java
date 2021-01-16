package lscript.parsing.nodes;

import lscript.lexing.Position;

public class ContinueNode extends Node {
    public ContinueNode(Position posStart, Position posEnd) {
        super(posStart, posEnd);
    }
}
