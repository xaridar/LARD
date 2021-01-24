package lscript.interpreting.types.builtins.lists;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BasicType;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.builtins.IExecutable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AppendBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Collections.singletonList(Arrays.asList(Tuple.of("list", "container"), Tuple.of("var", "value")));
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
