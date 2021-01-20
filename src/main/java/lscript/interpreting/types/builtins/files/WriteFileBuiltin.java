package lscript.interpreting.types.builtins.files;

import lscript.Tuple;
import lscript.errors.Error;
import lscript.interpreting.Context;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.*;
import lscript.interpreting.types.builtins.IExecutable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class WriteFileBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("file", "f"), Tuple.of("str", "text")), List.of(Tuple.of("file", "f"), Tuple.of("list", "bytes")));
    }

    @Override
    public String getName() {
        return "writefile";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        LFile f = (LFile) execCtx.getSymbolTable().get("f");
        try {
            OpenOption option;
            if (!f.shouldOverwrite()) {
                option = StandardOpenOption.APPEND;
            } else {
                option = StandardOpenOption.WRITE;
            }
            if (execNum == 0) {
                LString text = (LString) execCtx.getSymbolTable().get("text");
                if (!f.canWrite()) {
                    return new RTResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot write to a file in '" + f.getAccessMode() + "' mode.", execCtx));
                }
                if (f.binaryAccess()) {
                    return new RTResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot write a string to a file in '" + f.getAccessMode() + "' mode.", execCtx));
                }
                Files.writeString(Paths.get(f.getPath()), text.getValue());
            } else if (execNum == 1) {
                LList bytes = (LList) execCtx.getSymbolTable().get("bytes");
                if (!f.canWrite()) {
                    return new RTResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot write to a file in '" + f.getAccessMode() + "' mode.", execCtx));
                }
                if (!f.binaryAccess()) {
                    return new RTResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot write a byte array to a file in '" + f.getAccessMode() + "' mode.", execCtx));
                }
                if (bytes.getElements().stream().anyMatch(value -> !value.getType().equals("int"))) {
                    return new RTResult().failure(new Error.FileAccessError(bytes.getPosStart(), bytes.getPosEnd(), "Cannot write a list except a byte array to a file.", execCtx));
                }
                List<Byte> toWrite = new ArrayList<>();
                for (Value val : bytes.getElements()) {
                    RTResult res = LByte.from(val);
                    if (res.hasError()) return res;
                    toWrite.add(((LByte) res.getValue()).getValue());
                }
                byte[] asBytes = new byte[toWrite.size()];
                for (int i = 0; i < toWrite.size(); i++) {
                    asBytes[i] = toWrite.get(i);
                }
                Files.write(Paths.get(f.getPath()), asBytes, option);
            }
            return new RTResult().success(NullType.Void);
        } catch (IOException e) {
            return new RTResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot find file '" + f.getPath() + "'", execCtx));
        }

    }
}
