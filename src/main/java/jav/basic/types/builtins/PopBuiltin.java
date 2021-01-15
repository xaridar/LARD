package jav.basic.types.builtins;

import jav.Tuple;
import jav.basic.Context;
import jav.basic.results.RTResult;
import jav.basic.types.BuiltInFunction;
import jav.basic.types.Int;

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
        jav.basic.types.List list = (jav.basic.types.List) execCtx.getSymbolTable().get("container");
        return new RTResult().success(list.getElements().remove(((Int) execCtx.getSymbolTable().get("index")).getValue().intValue()));
    }
}
