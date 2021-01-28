package lscript.interpreting.types.builtins;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.LString;

import java.util.Collections;
import java.util.List;

public class StrBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Collections.singletonList(Collections.singletonList(Tuple.of("var", "val")));
    }

    @Override
    public String getName() {
        return "tostring";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        return new RTResult().success(new LString(execCtx.getSymbolTable().get("val").toString()).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
