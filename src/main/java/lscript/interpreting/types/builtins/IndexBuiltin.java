package lscript.interpreting.types.builtins;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.errors.Error;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.*;

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
        Int startIndex = null;
        if (execNum > 2) {
            startIndex = (Int) execCtx.getSymbolTable().get("val");
            if (startIndex.getValue() == null) {
                return res.failure(new Error.RunTimeError(startIndex.getPosStart(), startIndex.getPosEnd(), "Expected int, got null.", execCtx));
            }
        }
        int index = -1;
        if (execNum == 0) {
            String s = ((Str) toIndex).getValue();
            index = s.indexOf(((Str) val).getValue());
        } else if (execNum == 1) {
            List<Value> l = ((lscript.interpreting.types.List) toIndex).getValue();
            Optional<Value> v = l.stream().filter(value -> val.equalTo(value).isTrue()).findFirst();
            if (v.isPresent())
                index = l.indexOf(v.get());
        } else if (execNum == 2) {
            Map<Value, Value> m = ((lscript.interpreting.types.Map) toIndex).getValue();
            Optional<Value> v = m.values().stream().filter(value -> val.equalTo(value).isTrue()).findFirst();
            if (v.isPresent()) {
                Value key = m.keySet().stream().filter(k -> m.get(k) == v.get()).findFirst().orElse(NullType.Null);
                return res.success(key);
            }
        } else if (execNum == 3) {
            String s = ((Str) toIndex).getValue();
            index = s.indexOf(((Str) val).getValue(), startIndex.getValue());
        } else if (execNum == 4) {
            List<Value> l = ((lscript.interpreting.types.List) toIndex).getValue();
            Optional<Value> v = l.subList(startIndex.getValue(), l.size()).stream().filter(value -> val.equalTo(value).isTrue()).findFirst();
            if (v.isPresent())
                index = l.indexOf(v.get());
        } else {
            return null;
        }
        return res.success(new Int(index));
    }
}
