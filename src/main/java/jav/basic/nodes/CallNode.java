package jav.basic.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CallNode extends Node {

    private final VarAccessNode nodeToCall;
    private final List<Node> argNodes;

    public CallNode(VarAccessNode nodeToCall, List<Node> argNodes) {
        super(nodeToCall.getPosStart(), argNodes.size() > 0 ? argNodes.get(argNodes.size() - 1).getPosEnd() : nodeToCall.getPosEnd());
        this.nodeToCall = nodeToCall;
        this.argNodes = argNodes;
    }

    public List<Node> getArgNodes() {
        return argNodes;
    }

    public VarAccessNode getNodeToCall() {
        return nodeToCall;
    }

    @Override
    public String toString() {
        return String.format("(%s(%s))", nodeToCall.getToken().toString(), argNodes.stream().map(Object::toString).collect(Collectors.joining(", ")));
    }
}
