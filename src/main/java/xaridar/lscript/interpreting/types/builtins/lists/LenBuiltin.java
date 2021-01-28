package lscript.interpreting.types.builtins.lists;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.*;
import lscript.interpreting.types.builtins.IExecutable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LenBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
            return Arrays.asList(Collections.singletonList(Tuple.of("str", "string")), Collections.singletonList(Tuple.of("list", "collection")), Collections.singletonList(Tuple.of("map", "collection")));
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
                len = ((LString) execCtx.getSymbolTable().get("string")).getValue().length();
                break;
            case 1:
                len = ((LList) execCtx.getSymbolTable().get("collection")).getElements().size();
                break;
            case 2:
                len = ((LMap) execCtx.getSymbolTable().get("collection")).getMap().size();
                break;
        }
        return new RTResult().success(new LInt(len).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
