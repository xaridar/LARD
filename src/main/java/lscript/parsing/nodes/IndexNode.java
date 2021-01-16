package lscript.parsing.nodes;

public class IndexNode extends Node {
    private final Node left;
    private final Node index;

    public IndexNode(Node left, Node index) {
        super(left.getPosStart(), index.getPosEnd());
        this.left = left;
        this.index = index;
    }

    public Node getLeft() {
        return left;
    }

    public Node getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return left.toString() + "[" + index.toString() + "]";
    }
}
