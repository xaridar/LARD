package xaridar.lscript.interpreting.types.builtins.random;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.Tuple;
import xaridar.lscript.errors.Error;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.types.BuiltInFunction;
import xaridar.lscript.interpreting.types.LInt;
import xaridar.lscript.interpreting.types.builtins.IExecutable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandIntBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Arrays.asList(Collections.emptyList(), Collections.singletonList(Tuple.of("int", "bound")), Arrays.asList(Tuple.of("int", "lowerBound"), Tuple.of("int", "upperBound")));
    }

    @Override
    public String getName() {
        return "randint";
    }

    @Override
    public RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        RunTimeResult res = new RunTimeResult();
        switch (execNum) {
            case 0:
                return res.success(new LInt(new Random().nextInt()).setContext(fun.getContext()).setPos(fun.getPosStart(), fun.getPosEnd()));
            case 1:
                LInt bound = (LInt) execCtx.getSymbolTable().get("bound");
                if (bound.getValue() < 0) {
                    return res.failure(new Error.ArgumentError(bound.getPosStart(), bound.getPosEnd(), "Invalid argument: Bound must be greater than 0.", execCtx));
                }
                return res.success(new LInt(new Random().nextInt(bound.getValue())).setContext(fun.getContext()).setPos(fun.getPosStart(), fun.getPosEnd()));
            case 2:
                LInt upperBound = (LInt) execCtx.getSymbolTable().get("upperBound");
                LInt lowerBound = (LInt) execCtx.getSymbolTable().get("lowerBound");
                if (upperBound.getValue() < lowerBound.getValue()) {
                    return res.failure(new Error.RunTimeError(lowerBound.getPosStart(), upperBound.getPosEnd(), "Invalid argument: Upper bound must be greater than lower bound.", execCtx));
                }
                return res.success(new LInt(new Random().nextInt(upperBound.getValue() - lowerBound.getValue() + 1) + lowerBound.getValue()).setContext(fun.getContext()).setPos(fun.getPosStart(), fun.getPosEnd()));
            default:
                return null;
        }
    }
}
