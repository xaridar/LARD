package lscript.parsing;

import lscript.errors.Error;
import lscript.parsing.nodes.Node;

/**
 * Manages results from parsing, and holds a success Node or Error as a result.
 */
public class ParseResult {

    private Error error;
    private Node node;
    private int advanceCount;
    private int toReverseCount;

    /**
     * Default constructor. Sets default variables.
     */
    public ParseResult() {
        error = null;
        node = null;
        advanceCount = 0;
        toReverseCount = 0;
    }

    /**
     * Registers another ParseResult into the current ParseResult, adding its advanceCount to its own and taking its error if it has one.
     * @param res - the ParseResult to register
     * @return the registered ParseResult's Node.
     */
    public Node register(ParseResult res) {
        advanceCount += res.getAdvanceCount();
        if (res.hasError())
            error = res.getError();
        return res.getNode();
    }

    /**
     * Advances the advanceCount to keep track of advancements in the Parser.
     */
    public void registerAdvancement() {
        advanceCount++;
    }

    /**
     * Registers a Node and returns itself for registration.
     * @param node - The Node to register.
     * @return this ParseResult, which can be registered later.
     */
    public ParseResult success(Node node) {
        this.node = node;
        return this;
    }

    /**
     * Registers an Error if it doesn't already have one and returns itself for registration.
     * @param error - The Error to register.
     * @return this ParseResult, which can be registered later.
     */
    public ParseResult failure(Error error) {
        if (!hasError() || advanceCount == 0) this.error = error;
        return this;
    }

    /**
     * @return this ParseResult's stored Error.
     */
    public Error getError() {
        return error;
    }

    /**
     * @return true if the ParseReult contains an error; otherwise false.
     */
    public boolean hasError() {
        return error != null;
    }

    /**
     * @return this ParseResult's stored Node.
     */
    public Node getNode() {
        return node;
    }

    /**
     * @return the value storing how many times the Parser has advanced.
     */
    public int getAdvanceCount() {
        return advanceCount;
    }

    /**
     * Tries to register another ParseResult, but if it find an error, sets the reverseCount.
     * @param res - the ParseResult to register.
     * @return either null if it cannot register the ParseResult, or the result of this.register(res) (a Node)
     */
    public Node tryRegister(ParseResult res) {
        if (res.hasError()) {
            toReverseCount = res.advanceCount;
            return null;
        }
        return register(res);
    }

    /**
     * @return this PareResult's stored value to reverse in the Parser.
     */
    public int getToReverseCount() {
        return toReverseCount;
    }
}
