package lscript.interpreting.types.builtins.files;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.errors.Error;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.*;
import lscript.interpreting.types.builtins.IExecutable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
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
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        LFile f = (LFile) execCtx.getSymbolTable().get("f");
        if (!f.canRead()) {
            return new RTResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot read a file in '" + f.getAccessMode() + "' mode.", execCtx));
        }
        try {
            if (!f.binaryAccess()) {
                String s = String.join("\n", Files.readAllLines(Paths.get(f.getPath()), StandardCharsets.UTF_8));
                return new RTResult().success(new LString(s).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
            }
            else {
                byte[] s = Files.readAllBytes(Paths.get(f.getPath()));
                Byte[] bytes = new Byte[s.length];
                for (int i = 0; i < s.length; i++) {
                    bytes[i] = Byte.valueOf(s[i]);
                }
                return new RTResult().success(new LList(Arrays.stream(bytes).map(LByte::new).collect(Collectors.toList())));
            }
        } catch (IOException e) {
            return new RTResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot find file '" + f.getPath() + "'", execCtx));
        }
    }
}
