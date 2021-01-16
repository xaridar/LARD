package lscript.parsing.nodes;

/**
 * A simple Node representing an attempt to set the index of an object to a value.
 */
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

    /**
     * @return the Node to be indexed.
     */
    public Node getLeft() {
        return left;
    }

    /**
     * @return the Node on the right side of this binary operation.
     */
    public Node getIndex() {
        return index;
    }

    /**
     * @return the Node on the right side of this binary operation.
     */
    public Node getVal() {
        return val;
    }
}
