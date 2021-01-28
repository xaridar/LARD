package xaridar.lscript.interpreting.types.builtins;

import xaridar.lscript.Shell;
import xaridar.lscript.Tuple;
import xaridar.lscript.errors.Error;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.interpreting.ModifierList;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.types.BuiltInFunction;
import xaridar.lscript.interpreting.types.NullType;
import xaridar.lscript.interpreting.types.LString;

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
    public RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        RunTimeResult res = new RunTimeResult();
        Tuple<Context, Error> resCtx = Shell.runInternal(fun.getPosStart().getFn(), ((LString) execCtx.getSymbolTable().get("text")).getValue(), false);
        if (resCtx.getRight() != null) return res.failure(resCtx.getRight());
        resCtx.getLeft().getSymbolTable().getSymbols().forEach(symbol -> {
            if (!Shell.GLOBAL_SYMBOL_TABLE.hasVar(symbol.getName())) {
                ModifierList modifierList = new ModifierList();
                if (symbol.isAccessible()) modifierList.addModByString("pub");
                else modifierList.addModByStringHarsh("priv");
                if (symbol.isImmutable()) modifierList.addModByString("fin");
                if (symbol.isStatic()) modifierList.addModByString("stat");
                execCtx.getParent().getSymbolTable().set(symbol.getType(), symbol.getType(), symbol.getValue(), modifierList);
            }
        });
        return res.success(NullType.Void);
    }
}
