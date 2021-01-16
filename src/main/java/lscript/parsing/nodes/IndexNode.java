package lscript.parsing.nodes;

/**
 * A simple Node representing an attempt to index an object.
 */
public class IndexNode extends Node {
    private final Node left;
    private final Node index;

    public IndexNode(Node left, Node index) {
        super(left.getPosStart(), index.getPosEnd());
        this.left = left;
        this.index = index;
    }

    /**
     * @return the Node representing the value to index.
     */
    public Node getLeft() {
        return left;
    }

    /**
     * @return the Node representing the desired index.
     */
    public Node getIndex() {
        return index;
    }

    /**
     * @return a String representation of the Node for debugging.
     */
    @Override
    public String toString() {
        return left.toString() + "[" + index.toString() + "]";
    }
}
