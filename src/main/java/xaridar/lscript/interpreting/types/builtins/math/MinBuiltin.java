package xaridar.lscript.interpreting.types.builtins.math;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.1.0
 * @author Xaridar
 */

import xaridar.lscript.Tuple;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.types.BuiltInFunction;
import xaridar.lscript.interpreting.types.LFloat;
import xaridar.lscript.interpreting.types.LInt;
import xaridar.lscript.interpreting.types.LNumber;
import xaridar.lscript.interpreting.types.builtins.IExecutable;

import java.util.Arrays;
import java.util.List;

public class MinBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Arrays.asList(Arrays.asList(Tuple.of("int", "val1"), Tuple.of("int", "val2")), Arrays.asList(Tuple.of("float", "val1"), Tuple.of("float", "val2")), Arrays.asList(Tuple.of("int", "val1"), Tuple.of("float", "val2")), Arrays.asList(Tuple.of("float", "val1"), Tuple.of("int", "val2")));
    }

    @Override
    public String getName() {
        return "min";
    }

    @Override
    public RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        LNumber value;
        if (execNum == 0) {
            value = new LInt(Math.min(((LInt) execCtx.getSymbolTable().get("val1")).getValue(), ((LInt) execCtx.getSymbolTable().get("val2")).getValue()));
        } else {
            value = new LFloat(Math.min(((LNumber) execCtx.getSymbolTable().get("val1")).getValue().floatValue(), ((LNumber) execCtx.getSymbolTable().get("val2")).getValue().floatValue()));
        }
        return new RunTimeResult().success(value.setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
