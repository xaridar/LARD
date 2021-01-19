package lscript.interpreting.types.builtins.lists;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.errors.Error;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.*;
import lscript.interpreting.types.builtins.IExecutable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class IndexBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(
                List.of(Tuple.of("str", "toIndex"), Tuple.of("str", "val")),
                List.of(Tuple.of("list", "toIndex"), Tuple.of("var", "val")),
                List.of(Tuple.of("map", "toIndex"), Tuple.of("var", "val")),
                List.of(Tuple.of("str", "toIndex"), Tuple.of("str", "val"), Tuple.of("int", "startIndex")),
                List.of(Tuple.of("list", "toIndex"), Tuple.of("var", "val"), Tuple.of("int", "startIndex"))
        );
    }

    @Override
    public String getName() {
        return "indexof";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        RTResult res = new RTResult();
        Value toIndex = execCtx.getSymbolTable().get("toIndex");
        Value val = execCtx.getSymbolTable().get("val");
        LInt startIndex = null;
        if (execNum > 2) {
            startIndex = (LInt) execCtx.getSymbolTable().get("val");
            if (startIndex.getValue() == null) {
                return res.failure(new Error.RunTimeError(startIndex.getPosStart(), startIndex.getPosEnd(), "Expected int, got null.", execCtx));
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
            index = s.indexOf(((LString) val).getValue(), startIndex.getValue());
        } else if (execNum == 4) {
            List<Value> l = ((LList) toIndex).getValue();
            Optional<Value> v = l.subList(startIndex.getValue(), l.size()).stream().filter(value -> val.equalTo(value).isTrue()).findFirst();
            if (v.isPresent())
                index = l.indexOf(v.get());
        } else {
            return null;
        }
        return res.success(new LInt(index).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
