package jav.basic.types.builtins;

import jav.Tuple;
import jav.basic.Context;
import jav.basic.results.RTResult;
import jav.basic.types.BuiltInFunction;
import jav.basic.types.Int;
import jav.basic.types.Map;
import jav.basic.types.Str;

import java.util.List;

public class LenBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("str", "string")), List.of(Tuple.of("list", "collection")), List.of(Tuple.of("map", "collection")));
    }

    @Override
    public String getName() {
        return "len";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        int len = 0;
        switch (execNum) {
            case 0:
                len = ((Str) execCtx.getSymbolTable().get("string")).getValue().length();
                break;
            case 1:
                len = ((jav.basic.types.List) execCtx.getSymbolTable().get("collection")).getElements().size();
                break;
            case 2:
                len = ((Map) execCtx.getSymbolTable().get("collection")).getMap().size();
                break;
        }
        return new RTResult().success(new Int(len));
    }
}
