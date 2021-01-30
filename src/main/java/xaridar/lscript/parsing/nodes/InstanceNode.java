package xaridar.lscript.parsing.nodes;

import xaridar.lscript.lexing.Position;
import xaridar.lscript.lexing.Token;

import java.util.List;

public class InstanceNode extends Node {
    private final Token cls;
    private final List<Node> argNodes;

    /**
     * @param cls - A Token containing the name of the class to instantiate.
     * @param argNodes - A list of Nodes containing parameters to the class's constructor.
     */
    public InstanceNode(Token cls, List<Node> argNodes) {
        super(cls.getPosStart(), argNodes.size() == 0 ? cls.getPosEnd() : argNodes.get(argNodes.size() - 1).getPosEnd());
        this.cls = cls;
        this.argNodes = argNodes;
    }

    public Token getCls() {
        return cls;
    }

    public List<Node> getArgNodes() {
        return argNodes;
    }
}
