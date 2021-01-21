package lscript.interpreting.types.builtins;

import lscript.Tuple;
import lscript.errors.Error;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.*;
import lscript.parsing.Parser;

import java.util.ArrayList;
import java.util.List;

public class GetBytesBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("str", "string")), List.of(Tuple.of("num", "number")), List.of(Tuple.of("list", "l")));
    }

    @Override
    public String getName() {
        return "getbytes";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        if (execNum == 0 || execNum == 1) {
            byte[] bytes;
            if (execNum == 0) {
                bytes = getBytes(execCtx.getSymbolTable().get("string"));
            } else {
                bytes = getBytes(execCtx.getSymbolTable().get("number"));
            }
            LList retList = (LList) new LList(new ArrayList<>()).setContext(fun.getContext()).setPos(fun.getPosStart(), fun.getPosEnd());
            for (byte aByte : bytes) {
                Tuple<BasicType, Error> tup = retList.addedTo(new LByte(aByte));
                if (tup.getRight() != null) return new RTResult().failure(tup.getRight());
                retList = (LList) tup.getLeft();
            }
            return new RTResult().success(retList);
        } else if (execNum == 2) {
            LList list = (LList) execCtx.getSymbolTable().get("l");
            List<Value> bytes = new ArrayList<>();
            for (Value val : list.getElements()) {
                byte[] currBytes = getBytes(val);
                if (currBytes == null)
                    return new RTResult().failure(new Error.RunTimeError(val.getPosStart(), val.getPosEnd(), "Cannot get bytes of type '" + val.getType() + "'", execCtx));
                LList l = new LList(new ArrayList<>());
                for (byte aByte : currBytes) {
                    Tuple<BasicType, Error> tup = l.addedTo(new LByte(aByte));
                    if (tup.getRight() != null) return new RTResult().failure(tup.getRight());
                    l = (LList) tup.getLeft();
                }
                bytes.add(l);
            }
            return new RTResult().success(new LList(bytes).setContext(fun.getContext()).setPos(fun.getPosStart(), fun.getPosEnd()));
        }
        return null;
    }

    private byte[] getBytes(Value val) {
        if (val instanceof LString) {
            return ((LString) val).getValue().getBytes();
        } else if (val instanceof LNumber) {
            return new byte[]{((LNumber) val).getValue().byteValue()};
        } else if (val instanceof LByte) {
            return new byte[]{((LByte) val).getValue()};
        }
        return null;
    }
}
