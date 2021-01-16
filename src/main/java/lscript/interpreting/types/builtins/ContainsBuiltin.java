package lscript.interpreting.types.builtins;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.*;
import lscript.interpreting.types.Boolean;

import java.util.List;

public class ContainsBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("str", "container"), Tuple.of("str", "element")), List.of(Tuple.of("list", "container"), Tuple.of("var", "element")), List.of(Tuple.of("map", "container"), Tuple.of("var", "element")));
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
                Str str = (Str) execCtx.getSymbolTable().get("container");
                contains = str.getValue().contains(((Str) element).getValue());
                break;
            case 1:
                lscript.interpreting.types.List l = (lscript.interpreting.types.List) execCtx.getSymbolTable().get("container");
                contains = l.getValue().stream().anyMatch(val -> val.equalTo(element).isTrue());
                break;
            case 2:
                Map m = (Map) execCtx.getSymbolTable().get("container");
                contains = m.getValue().keySet().stream().anyMatch(val -> val.equalTo(element).isTrue());
                break;
        }
        return new RTResult().success(new Boolean(contains));
    }
}
