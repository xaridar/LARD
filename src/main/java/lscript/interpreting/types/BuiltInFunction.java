package lscript.interpreting.types;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.errors.Error;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.builtins.IExecutable;

import java.util.List;

public class BuiltInFunction extends BaseFunction {

    public BuiltInFunction(String name) {
        super(name);
    }

    @Override
    public RTResult execute(List<Value> args) {
        RTResult res = new RTResult();
        Context execCtx = generateExecContext();
        IExecutable builtin = IExecutable.builtins.stream().filter(func -> func.getName().equals(name)).findFirst().orElse(null);
        if (builtin == null) {
            return res.failure(new Error.RunTimeError(getPosStart(), getPosEnd(), "No built-in function with name '" + name + "' found", getContext()));
        }
        int execNum = -1;
        List<List<Tuple<String, String>>> argNames = builtin.getArgNames();
        for (int i = 0, argNamesSize = argNames.size(); i < argNamesSize; i++) {
            res = new RTResult();
            List<Tuple<String, String>> list = argNames.get(i);

            res.register(checkAndPopArgs(list, args, execCtx));
            if (!res.shouldReturn()) {
                execNum = i;
                break;
            }
        }
        if (execNum == -1) return res;

        Value ret = res.register(builtin.execute(execCtx, execNum, this));
        if (res.shouldReturn()) return res;

        return res.success(ret);
    }

    @Override
    public Value copy() {
        return new BuiltInFunction(getName()).setContext(getContext()).setPos(getPosStart(), getPosEnd());
    }

    @Override
    public String toString() {
        return String.format("<built-in function %s>", getName());
    }
}
