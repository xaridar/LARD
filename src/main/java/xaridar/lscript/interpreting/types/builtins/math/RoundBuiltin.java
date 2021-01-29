package xaridar.lscript.interpreting.types.builtins.math;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.Tuple;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.types.BuiltInFunction;
import xaridar.lscript.interpreting.types.LInt;
import xaridar.lscript.interpreting.types.LNumber;
import xaridar.lscript.interpreting.types.builtins.IExecutable;

import java.util.Collections;
import java.util.List;

public class RoundBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Collections.singletonList(Collections.singletonList(Tuple.of("num", "val")));
    }

    @Override
    public String getName() {
        return "round";
    }

    @Override
    public RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        return new RunTimeResult().success(new LInt((int) Math.round(((LNumber) execCtx.getSymbolTable().get("val")).getValue().doubleValue())).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
