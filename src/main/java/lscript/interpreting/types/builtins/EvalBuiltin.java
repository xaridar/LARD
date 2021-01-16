package lscript.interpreting.types.builtins;

import lscript.Shell;
import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.NullType;
import lscript.interpreting.types.LString;

import java.util.List;

public class EvalBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("str", "text")));
    }

    @Override
    public String getName() {
        return "eval";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        Shell.run(fun.getPosStart().getFn(), ((LString) execCtx.getSymbolTable().get("text")).getValue());
        return new RTResult().success(NullType.Void);
    }
}
