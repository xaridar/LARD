package xaridar.lscript.interpreting.types.builtins.lists;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.Tuple;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.types.*;
import xaridar.lscript.interpreting.types.builtins.IExecutable;

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
    public RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
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
        return new RunTimeResult().success(new LInt(len).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
