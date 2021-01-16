package lscript.interpreting.types.builtins.math;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.LFloat;
import lscript.interpreting.types.LInt;
import lscript.interpreting.types.builtins.IExecutable;

import java.util.List;

public class AbsBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("int", "val")), List.of(Tuple.of("float", "val")));
    }

    @Override
    public String getName() {
        return "abs";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        if (execNum == 0) {
            return new RTResult().success(new LInt(Math.abs(((LInt) execCtx.getSymbolTable().get("val")).getValue())).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
        } else if (execNum == 1) {
            return new RTResult().success(new LFloat(Math.abs(((LFloat) execCtx.getSymbolTable().get("val")).getValue())).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
        }
        return null;
    }
}
