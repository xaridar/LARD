package xaridar.lscript.interpreting.types.builtins.lists;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.Tuple;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.errors.Error;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.types.*;
import xaridar.lscript.interpreting.types.builtins.IExecutable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class IndexBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Arrays.asList(
                Arrays.asList(Tuple.of("str", "toIndex"), Tuple.of("str", "val")),
                Arrays.asList(Tuple.of("list", "toIndex"), Tuple.of("var", "val")),
                Arrays.asList(Tuple.of("map", "toIndex"), Tuple.of("var", "val")),
                Arrays.asList(Tuple.of("str", "toIndex"), Tuple.of("str", "val"), Tuple.of("int", "startIndex")),
                Arrays.asList(Tuple.of("list", "toIndex"), Tuple.of("var", "val"), Tuple.of("int", "startIndex"))
        );
    }

    @Override
    public String getName() {
        return "indexof";
    }

    @Override
    public RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        RunTimeResult res = new RunTimeResult();
        Value toIndex = execCtx.getSymbolTable().get("toIndex");
        Value val = execCtx.getSymbolTable().get("val");
        LInt startIndex = null;
        if (execNum > 2) {
            startIndex = (LInt) execCtx.getSymbolTable().get("startIndex");
            if (startIndex.getValue() == null) {
                return res.failure(new Error.ArgumentError(startIndex.getPosStart(), startIndex.getPosEnd(), "Expected int, got null.", execCtx));
            }
            if (startIndex.getValue() < 0) {
                return res.failure(new Error.IndexOutOfBoundsError(startIndex.getPosStart(), startIndex.getPosEnd(), "Cannot pass negative start value into indexof", fun.getContext()));
            }
        }
        int index = -1;
        if (execNum == 0) {
            String s = ((LString) toIndex).getValue();
            index = s.indexOf(((LString) val).getValue());
        } else if (execNum == 1) {
            List<Value> l = ((LList) toIndex).getValue();
            Optional<Value> v = l.stream().filter(value -> val.equalTo(value).isTrue()).findFirst();
            if (v.isPresent())
                index = l.indexOf(v.get());
        } else if (execNum == 2) {
            Map<Value, Value> m = ((LMap) toIndex).getValue();
            Optional<Value> v = m.values().stream().filter(value -> val.equalTo(value).isTrue()).findFirst();
            if (v.isPresent()) {
                Value key = m.keySet().stream().filter(k -> m.get(k) == v.get()).findFirst().orElse(NullType.Null);
                return res.success(key);
            }
        } else if (execNum == 3) {
            String s = ((LString) toIndex).getValue();
            if (startIndex.getValue() - 1 >= s.length()) return res.failure(new Error.IndexOutOfBoundsError(startIndex.getPosStart(), startIndex.getPosEnd(), "Index " + startIndex.getValue() + " too large for str of len " + s.length(), fun.getContext()));
            index = s.indexOf(((LString) val).getValue(), startIndex.getValue());
        } else if (execNum == 4) {
            List<Value> l = ((LList) toIndex).getValue();
            if (startIndex.getValue() - 1 >= l.size()) return res.failure(new Error.IndexOutOfBoundsError(startIndex.getPosStart(), startIndex.getPosEnd(), "Index " + startIndex.getValue() + " too large for list of len " + l.size(), fun.getContext()));
            Optional<Value> v = l.subList(startIndex.getValue(), l.size()).stream().filter(value -> val.equalTo(value).isTrue()).findFirst();
            if (v.isPresent())
                index = l.indexOf(v.get());
        } else {
            return null;
        }
        return res.success(new LInt(index).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
