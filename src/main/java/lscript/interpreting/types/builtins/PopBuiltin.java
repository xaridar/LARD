package lscript.interpreting.types.builtins;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.Int;

import java.util.List;

public class PopBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("list", "container"), Tuple.of("int", "index")));
    }

    @Override
    public String getName() {
        return "pop";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        lscript.interpreting.types.List list = (lscript.interpreting.types.List) execCtx.getSymbolTable().get("container");
        return new RTResult().success(list.getElements().remove(((Int) execCtx.getSymbolTable().get("index")).getValue().intValue()));
    }
}
