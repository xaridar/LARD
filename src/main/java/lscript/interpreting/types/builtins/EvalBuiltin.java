package lscript.interpreting.types.builtins;

import lscript.Shell;
import lscript.Tuple;
import lscript.errors.Error;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.NullType;
import lscript.interpreting.types.LString;

import java.util.Collections;
import java.util.List;

public class EvalBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Collections.singletonList(Collections.singletonList(Tuple.of("str", "text")));
    }

    @Override
    public String getName() {
        return "eval";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        RTResult res = new RTResult();
        Tuple<Context, Error> resCtx = Shell.runInternal(fun.getPosStart().getFn(), ((LString) execCtx.getSymbolTable().get("text")).getValue(), false);
        if (resCtx.getRight() != null) return res.failure(resCtx.getRight());
        resCtx.getLeft().getSymbolTable().getSymbols().forEach(symbol -> {
            if (!Shell.GLOBAL_SYMBOL_TABLE.hasVar(symbol.getName())) {
                execCtx.getParent().getSymbolTable().set(symbol.getType(), symbol.getType(), symbol.getValue(), !symbol.canEdit());
            }
        });
        return res.success(NullType.Void);
    }
}
