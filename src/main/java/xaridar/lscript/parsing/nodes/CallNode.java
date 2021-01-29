package xaridar.lscript.parsing.nodes;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.1.0
 * @author Xaridar
 */

import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple Node representing a function call.
 */
public class CallNode extends Node {

    private final VarAccessNode nodeToCall;
    private final List<Node> argNodes;

    /**
     * @param nodeToCall - A VarAccessNode representing the function to call.
     * @param argNodes - A list of Nodes containing parameters to the function.
     */
    public CallNode(VarAccessNode nodeToCall, List<Node> argNodes) {
        super(nodeToCall.getPosStart(), argNodes.size() > 0 ? argNodes.get(argNodes.size() - 1).getPosEnd() : nodeToCall.getPosEnd());
        this.nodeToCall = nodeToCall;
        this.argNodes = argNodes;
    }

    /**
     * @return a list of Nodes containing parameters to the function.
     */
    public List<Node> getArgNodes() {
        return argNodes;
    }

    /**
     * @return the VarAccessNode representing the function to call.
     */
    public VarAccessNode getNodeToCall() {
        return nodeToCall;
    }

    /**
     * @return a String representation of the Node for debugging.
     */
    @Override
    public String toString() {
        return String.format("(%s(%s))", nodeToCall.getToken().toString(), argNodes.stream().map(Object::toString).collect(Collectors.joining(", ")));
    }
}
