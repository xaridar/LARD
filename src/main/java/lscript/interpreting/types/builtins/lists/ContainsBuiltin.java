package lscript.interpreting.types.builtins.lists;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.*;
import lscript.interpreting.types.LBoolean;
import lscript.interpreting.types.builtins.IExecutable;

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
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
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
        return new RTResult().success(new LBoolean(contains).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
