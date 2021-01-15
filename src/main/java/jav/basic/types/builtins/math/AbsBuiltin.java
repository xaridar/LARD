package jav.basic.types.builtins.math;

import jav.Tuple;
import jav.basic.Context;
import jav.basic.results.RTResult;
import jav.basic.types.BuiltInFunction;
import jav.basic.types.Float;
import jav.basic.types.Int;
import jav.basic.types.builtins.IExecutable;

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
            return new RTResult().success(new Int(Math.abs(((Int) execCtx.getSymbolTable().get("val")).getValue())));
        } else if (execNum == 1) {
            return new RTResult().success(new Float(Math.abs(((Float) execCtx.getSymbolTable().get("val")).getValue())));
        }
        return null;
    }
}
