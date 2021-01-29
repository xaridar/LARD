package xaridar.lscript.interpreting.types;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.Tuple;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.errors.Error;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.types.builtins.IExecutable;

import java.util.List;
import java.util.stream.Collectors;

public class BuiltInFunction extends BaseFunction {

    public BuiltInFunction(String name) {
        super(name);
    }

    @Override
    public RunTimeResult execute(List<Value> args) {
        RunTimeResult res = new RunTimeResult();
        Context execCtx = generateExecContext();
        IExecutable builtin = IExecutable.builtins.stream().filter(func -> func.getName().equals(name)).findFirst().orElse(null);
        if (builtin == null) {
            return res.failure(new Error.RunTimeError(getPosStart(), getPosEnd(), "No built-in function with name '" + name + "' found", getContext()));
        }
        int execNum = -1;
        List<List<Tuple<String, String>>> argNames = builtin.getArgNames();
        for (int i = 0, argNamesSize = argNames.size(); i < argNamesSize; i++) {
            res = new RunTimeResult();
            List<Tuple<String, String>> list = argNames.get(i);

            res.register(checkAndPopArgs(list, args, execCtx));
            if (!res.shouldReturn()) {
                execNum = i;
                break;
            }
            execCtx.getSymbolTable().removeAll(list.stream().map(Tuple::getRight).collect(Collectors.toList()));
        }
        if (execNum == -1) return res.failure(new Error.ArgumentError(posStart, posEnd, "Unexpected arguments", context));

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
