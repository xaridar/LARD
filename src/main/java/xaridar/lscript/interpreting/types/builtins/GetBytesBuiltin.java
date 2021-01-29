package xaridar.lscript.interpreting.types.builtins;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.Tuple;
import xaridar.lscript.errors.Error;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.types.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GetBytesBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Arrays.asList(Collections.singletonList(Tuple.of("str", "string")), Collections.singletonList(Tuple.of("num", "number")), Collections.singletonList(Tuple.of("list", "l")));
    }

    @Override
    public String getName() {
        return "getbytes";
    }

    @Override
    public RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
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
                if (tup.getRight() != null) return new RunTimeResult().failure(tup.getRight());
                retList = (LList) tup.getLeft();
            }
            return new RunTimeResult().success(retList);
        } else if (execNum == 2) {
            LList list = (LList) execCtx.getSymbolTable().get("l");
            List<Value> bytes = new ArrayList<>();
            for (Value val : list.getElements()) {
                byte[] currBytes = getBytes(val);
                if (currBytes == null)
                    return new RunTimeResult().failure(new Error.ArgumentError(val.getPosStart(), val.getPosEnd(), "Cannot get bytes of type '" + val.getType() + "'", execCtx));
                LList l = new LList(new ArrayList<>());
                for (byte aByte : currBytes) {
                    Tuple<BasicType, Error> tup = l.addedTo(new LByte(aByte));
                    if (tup.getRight() != null) return new RunTimeResult().failure(tup.getRight());
                    l = (LList) tup.getLeft();
                }
                bytes.add(l);
            }
            return new RunTimeResult().success(new LList(bytes).setContext(fun.getContext()).setPos(fun.getPosStart(), fun.getPosEnd()));
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
