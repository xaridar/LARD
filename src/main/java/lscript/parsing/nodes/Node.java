package lscript.parsing.nodes;

import lscript.lexing.Position;

public class Node {

    protected Position posStart;
    protected Position posEnd;

    public Node(Position posStart, Position posEnd) {
        this.posStart = posStart;
        this.posEnd = posEnd;
    }

    public Position getPosEnd() {
        return posEnd;
    }

    public Position getPosStart() {
        return posStart;
    }

    public boolean requiresSemicolon() {
        return true;
    }
}
