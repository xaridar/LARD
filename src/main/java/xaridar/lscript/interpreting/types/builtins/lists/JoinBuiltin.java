package xaridar.lscript.interpreting.types.builtins.lists;

import xaridar.lscript.Tuple;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.types.BuiltInFunction;
import xaridar.lscript.interpreting.types.LList;
import xaridar.lscript.interpreting.types.LString;
import xaridar.lscript.interpreting.types.Value;
import xaridar.lscript.interpreting.types.builtins.IExecutable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JoinBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Collections.singletonList(Arrays.asList(Tuple.of("list", "toJoin"), Tuple.of("str", "delimiter")));
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        LString delimiter = (LString) execCtx.getSymbolTable().get("delimiter");
        LList list = (LList) execCtx.getSymbolTable().get("toJoin");
        StringBuilder builder = new StringBuilder();
        for (int i = 0, elementsSize = list.getElements().size(); i < elementsSize; i++) {
            if (i != 0) builder.append(delimiter.getValue());
            Value val = list.getElements().get(i);
            builder.append(val.toString());
        }
        return new RunTimeResult().success(new LString(builder.toString()).setContext(fun.getContext()).setPos(fun.getPosStart(), fun.getPosEnd()));
    }
}
