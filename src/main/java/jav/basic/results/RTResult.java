package jav.basic.results;

import jav.basic.Error;
import jav.basic.types.Value;

public class RTResult {

    private Value funcRetVal;
    private Error error;
    private Value value;
    private boolean loopCont;
    private boolean loopBreak;

    public RTResult() {
        reset();
    }

    public void reset() {
        error = null;
        value = null;
        funcRetVal = null;
        loopBreak = false;
        loopCont = false;
    }

    public Value register(RTResult res) {
        error = res.getError();
        funcRetVal = res.getFuncRetVal();
        loopCont = res.isLoopCont();
        loopBreak = res.isLoopBreak();
        return res.getValue();
    }

    public RTResult success(Value value) {
        reset();
        this.value = value;
        return this;
    }

    public RTResult successRet(Value value) {
        reset();
        this.funcRetVal = value;
        return this;
    }

    public RTResult successCont() {
        reset();
        this.loopCont = true;
        return this;
    }

    public RTResult successBreak() {
        reset();
        this.loopBreak = true;
        return this;
    }

    public boolean shouldReturn() {
        return error != null || funcRetVal != null || loopBreak || loopCont;
    }

    public RTResult failure(Error error) {
        reset();
        this.error = error;
        return this;
    }

    public Error getError() {
        return error;
    }

    public boolean hasError() {
        return error != null;
    }

    public Value getValue() {
        return value;
    }

    public Value getFuncRetVal() {
        return funcRetVal;
    }

    public boolean isLoopBreak() {
        return loopBreak;
    }

    public boolean isLoopCont() {
        return loopCont;
    }
}
