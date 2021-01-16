package lscript.parsing.nodes;

import java.util.List;
import java.util.stream.Collectors;

public class MultiLineNode extends Node {
    private final List<Node> nodes;

    public MultiLineNode(List<Node> nodes) {
        super(nodes.get(0).getPosStart(), nodes.get(nodes.size() - 1).getPosEnd());
        this.nodes = nodes;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        return "[" + nodes.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
    }
}
