package lscript.parsing.nodes;

public class SetIndexNode extends Node {
    private final Node left;
    private final Node index;
    private final Node val;

    public SetIndexNode(Node left, Node index, Node val) {
        super(left.getPosStart(), index.getPosEnd());
        this.left = left;
        this.index = index;
        this.val = val;
    }

    public Node getLeft() {
        return left;
    }

    public Node getIndex() {
        return index;
    }

    public Node getVal() {
        return val;
    }
}
