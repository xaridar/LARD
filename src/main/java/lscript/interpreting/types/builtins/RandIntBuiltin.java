package lscript.interpreting.types.builtins;

import lscript.Tuple;
import lscript.errors.Error;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.LInt;

import java.util.List;
import java.util.Random;

public class RandIntBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(), List.of(Tuple.of("int", "bound")), List.of(Tuple.of("int", "lowerBound"), Tuple.of("int", "upperBound")));
    }

    @Override
    public String getName() {
        return "randint";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        RTResult res = new RTResult();
        switch (execNum) {
            case 0:
                return res.success(new LInt(new Random().nextInt()).setContext(fun.getContext()).setPos(fun.getPosStart(), fun.getPosEnd()));
            case 1:
                LInt bound = (LInt) execCtx.getSymbolTable().get("bound");
                if (bound.getValue() < 0) {
                    return res.failure(new Error.RunTimeError(bound.getPosStart(), bound.getPosEnd(), "Invalid argument: Bound must be greater than 0.", execCtx));
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
