package xaridar.lscript.interpreting.types.builtins.files;

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
import xaridar.lscript.interpreting.types.builtins.IExecutable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WriteFileBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Arrays.asList(Arrays.asList(Tuple.of("file", "f"), Tuple.of("str", "text")), Arrays.asList(Tuple.of("file", "f"), Tuple.of("list", "bytes")));
    }

    @Override
    public String getName() {
        return "writefile";
    }

    @Override
    public RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        LFile f = (LFile) execCtx.getSymbolTable().get("f");
        try {
            OpenOption option;
            if (!f.shouldOverwrite()) {
                option = StandardOpenOption.APPEND;
            } else {
                option = StandardOpenOption.TRUNCATE_EXISTING;
            }
            if (execNum == 0) {
                LString text = (LString) execCtx.getSymbolTable().get("text");
                if (!f.canWrite()) {
                    return new RunTimeResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot write to a file in '" + f.getAccessModes() + "' mode.", execCtx));
                }
                if (f.binaryAccess()) {
                    return new RunTimeResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot write a string to a file in '" + f.getAccessModes() + "' mode.", execCtx));
                }
                Files.write(Paths.get(f.getPath()), Arrays.asList(text.getValue().split("\n")), option);
            } else if (execNum == 1) {
                LList bytes = (LList) execCtx.getSymbolTable().get("bytes");
                if (!f.canWrite()) {
                    return new RunTimeResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot write to a file in '" + f.getAccessModes() + "' mode.", execCtx));
                }
                if (!f.binaryAccess()) {
                    return new RunTimeResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot write a byte array to a file in '" + f.getAccessModes() + "' mode.", execCtx));
                }
                if (bytes.getElements().stream().anyMatch(value -> !value.getType().equals("int"))) {
                    return new RunTimeResult().failure(new Error.FileAccessError(bytes.getPosStart(), bytes.getPosEnd(), "Cannot write a list except a byte array to a file.", execCtx));
                }
                List<Byte> toWrite = new ArrayList<>();
                for (Value val : bytes.getElements()) {
                    RunTimeResult res = LByte.from(val);
                    if (res.hasError()) return res;
                    toWrite.add(((LByte) res.getValue()).getValue());
                }
                byte[] asBytes = new byte[toWrite.size()];
                for (int i = 0; i < toWrite.size(); i++) {
                    asBytes[i] = toWrite.get(i);
                }
                Files.write(Paths.get(f.getPath()), asBytes, option);
            }
            return new RunTimeResult().success(NullType.Void);
        } catch (IOException e) {
            return new RunTimeResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot find file '" + f.getPath() + "'", execCtx));
        }

    }
}
