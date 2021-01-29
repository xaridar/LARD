package xaridar.lscript.interpreting.types.builtins.lists;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.1.0
 * @author Xaridar
 */

import xaridar.lscript.Tuple;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.types.*;
import xaridar.lscript.interpreting.types.LBoolean;
import xaridar.lscript.interpreting.types.builtins.IExecutable;

import java.util.Arrays;
import java.util.List;

public class ContainsBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Arrays.asList(Arrays.asList(Tuple.of("str", "container"), Tuple.of("str", "element")), Arrays.asList(Tuple.of("list", "container"), Tuple.of("var", "element")), Arrays.asList(Tuple.of("map", "container"), Tuple.of("var", "element")));
    }

    @Override
    public String getName() {
        return "contains";
    }

    @Override
    public RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        boolean contains = false;
        Value element = execCtx.getSymbolTable().get("element");
        switch (execNum) {
            case 0:
                LString str = (LString) execCtx.getSymbolTable().get("container");
                contains = str.getValue().contains(((LString) element).getValue());
                break;
            case 1:
                LList l = (LList) execCtx.getSymbolTable().get("container");
                contains = l.getValue().stream().anyMatch(val -> val.equalTo(element).isTrue());
                break;
            case 2:
                LMap m = (LMap) execCtx.getSymbolTable().get("container");
                contains = m.getValue().keySet().stream().anyMatch(val -> val.equalTo(element).isTrue());
                break;
        }
        return new RunTimeResult().success(new LBoolean(contains).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
