package jav.basic.types.builtins;

import jav.Tuple;
import jav.basic.Context;
import jav.basic.results.RTResult;
import jav.basic.types.BasicType;
import jav.basic.types.BuiltInFunction;

import java.util.List;

public class AppendBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("list", "container"), Tuple.of("var", "value")));
    }

    @Override
    public String getName() {
        return "append";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        return new RTResult().success(((BasicType) execCtx.getSymbolTable().get("container")).addedTo(((BasicType) execCtx.getSymbolTable().get("value"))).getLeft());
    }
}
