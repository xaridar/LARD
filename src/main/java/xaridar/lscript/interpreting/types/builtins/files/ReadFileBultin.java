package xaridar.lscript.interpreting.types.builtins.files;

/*
 * LScript is an interpreted scripting language with static typing, written in Java by Xaridar.
 *
 * @version 2.1.0
 * @author Xaridar
 */

import xaridar.lscript.Tuple;
import xaridar.lscript.interpreting.Context;
import xaridar.lscript.errors.Error;
import xaridar.lscript.interpreting.RunTimeResult;
import xaridar.lscript.interpreting.types.*;
import xaridar.lscript.interpreting.types.builtins.IExecutable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReadFileBultin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Collections.singletonList(Collections.singletonList(Tuple.of("file", "f")));
    }

    @Override
    public String getName() {
        return "readfile";
    }

    @Override
    public RunTimeResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        LFile f = (LFile) execCtx.getSymbolTable().get("f");
        if (!f.canRead()) {
            return new RunTimeResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot read a file in '" + f.getAccessModes() + "' mode.", execCtx));
        }
        try {
            if (!f.binaryAccess()) {
                String s = String.join("\n", Files.readAllLines(Paths.get(f.getPath()), StandardCharsets.UTF_8));
                return new RunTimeResult().success(new LString(s).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
            }
            else {
                byte[] s = Files.readAllBytes(Paths.get(f.getPath()));
                Byte[] bytes = new Byte[s.length];
                for (int i = 0; i < s.length; i++) {
                    bytes[i] = Byte.valueOf(s[i]);
                }
                return new RunTimeResult().success(new LList(Arrays.stream(bytes).map(LByte::new).collect(Collectors.toList())));
            }
        } catch (IOException e) {
            return new RunTimeResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot find file '" + f.getPath() + "'", execCtx));
        }
    }
}
