package lscript.interpreting.types.builtins.math;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.Float;
import lscript.interpreting.types.Int;
import lscript.interpreting.types.Number;
import lscript.interpreting.types.builtins.IExecutable;

import java.util.List;

public class MinBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("int", "val1"), Tuple.of("int", "val2")), List.of(Tuple.of("float", "val1"), Tuple.of("float", "val2")), List.of(Tuple.of("int", "val1"), Tuple.of("float", "val2")), List.of(Tuple.of("float", "val1"), Tuple.of("int", "val2")));
    }

    @Override
    public String getName() {
        return "min";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        Number value;
        if (execNum == 0) {
            value = new Int(Math.min(((Int) execCtx.getSymbolTable().get("val1")).getValue(), ((Int) execCtx.getSymbolTable().get("val2")).getValue()));
        } else {
            value = new Float(Math.min(((Number) execCtx.getSymbolTable().get("val1")).getValue().floatValue(), ((Number) execCtx.getSymbolTable().get("val2")).getValue().floatValue()));
        }
        return new RTResult().success(value);
    }
}
