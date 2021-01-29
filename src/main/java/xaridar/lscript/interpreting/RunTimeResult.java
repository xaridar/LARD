package xaridar.lscript.interpreting;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.1.0
 * @author Xaridar
 */

import xaridar.lscript.errors.Error;
import xaridar.lscript.interpreting.types.Value;

/**
 * Manages results from interpreting, and holds a success Value, return Value from a function, break, continue, or Error as a result.
 */
public class RunTimeResult {

    private Value funcRetVal;
    private Error error;
    private Value value;
    private boolean loopCont;
    private boolean loopBreak;

    public RunTimeResult() {
        reset();
    }

    /**
     * Sets all of the RTResult variables to default values.
     */
    public void reset() {
        error = null;
        value = null;
        funcRetVal = null;
        loopBreak = false;
        loopCont = false;
    }

    /**
     * Registers another RTResult into the current RTResult, taking its Error, return Value, and booleans for breaking and continuing.
     * @param res - the RTResult to register
     * @return the registered RTResult's Value.
     */
    public Value register(RunTimeResult res) {
        error = res.getError();
        funcRetVal = res.getFuncRetVal();
        loopCont = res.isLoopCont();
        loopBreak = res.isLoopBreak();
        return res.getValue();
    }

    /**
     * Registers a Value and returns itself for registration.
     * @param value - The Value to register.
     * @return this RTResult, which can be registered later.
     */
    public RunTimeResult success(Value value) {
        reset();
        this.value = value;
        return this;
    }

    /**
     * Registers a return Value and returns itself for registration.
     * @param value - The Value to register.
     * @return this RTResult, which can be registered later.
     */
    public RunTimeResult successRet(Value value) {
        reset();
        this.funcRetVal = value;
        return this;
    }

    /**
     * Registers a continue block and returns itself for registration.
     * @return this RTResult, which can be registered later.
     */
    public RunTimeResult successCont() {
        reset();
        this.loopCont = true;
        return this;
    }

    /**
     * Registers a break block and returns itself for registration.
     * @return this RTResult, which can be registered later.
     */
    public RunTimeResult successBreak() {
        reset();
        this.loopBreak = true;
        return this;
    }

    /**
     * Determines whether a method shopuld return this RTResult.
     * @return True if this RTResult has a value and should be returned.
     */
    public boolean shouldReturn() {
        return error != null || funcRetVal != null || loopBreak || loopCont;
    }

    /**
     * Registers an Error and returns itself for registration.
     * @param error - The Error to register.
     * @return this ParseResult, which can be registered later.
     */
    public RunTimeResult failure(Error error) {
        reset();
        this.error = error;
        return this;
    }

    /**
     * @return This RTResult's stored Error. Can be null.
     */
    public Error getError() {
        return error;
    }

    /**
     * @return a boolean representing whether this RTResult has an Error value or not.
     */
    public boolean hasError() {
        return error != null;
    }

    /**
     * @return This RTResult's stored Value. Can be null.z
     */
    public Value getValue() {
        return value;
    }

    /**
     * @return This RTResult's stored return Value. Can be null.
     */
    public Value getFuncRetVal() {
        return funcRetVal;
    }

    /**
     * @return a boolean representing whether the RTResult should break.
     */
    public boolean isLoopBreak() {
        return loopBreak;
    }

    /**
     * @return a boolean representing whether the RTResult should continue.
     */
    public boolean isLoopCont() {
        return loopCont;
    }
}
