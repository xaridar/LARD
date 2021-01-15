package jav.basic.results;

import jav.basic.Error;
import jav.basic.nodes.Node;

public class ParseResult {

    private Error error;
    private Node node;
    private int advanceCount;
    private int toReverseCount;

    public ParseResult() {
        error = null;
        node = null;
        advanceCount = 0;
        toReverseCount = 0;
    }

    public Node register(ParseResult res) {
        advanceCount += res.getAdvanceCount();
        if (res.hasError())
            error = res.getError();
        return res.getNode();
    }

    public void registerAdvancement() {
        advanceCount++;
    }

    public ParseResult success(Node node) {
        this.node = node;
        return this;
    }

    public ParseResult failure(Error error) {
        if (!hasError() || advanceCount == 0) this.error = error;
        return this;
    }

    public Error getError() {
        return error;
    }

    public boolean hasError() {
        return error != null;
    }

    public Node getNode() {
        return node;
    }

    public int getAdvanceCount() {
        return advanceCount;
    }

    public Node tryRegister(ParseResult res) {
        if (res.hasError()) {
            toReverseCount = res.advanceCount;
            return null;
        }
        return register(res);
    }

    public int getToReverseCount() {
        return toReverseCount;
    }
}
