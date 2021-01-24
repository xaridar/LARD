package lscript.interpreting.types.builtins.files;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.errors.Error;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.LFile;
import lscript.interpreting.types.LString;
import lscript.interpreting.types.builtins.IExecutable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OpenBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Collections.singletonList(Arrays.asList(Tuple.of("str", "path"), Tuple.of("str", "mode")));
    }

    @Override
    public String getName() {
        return "open";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        LString path = (LString) execCtx.getSymbolTable().get("path");
        LString mode = (LString) execCtx.getSymbolTable().get("mode");
        LFile f;
        File file = new File(path.toString());
        switch (mode.getValue()) {
            case "a":
            case "ab":
            case "r":
            case "rb":
                if (!Files.exists(file.toPath())) {
                    return new RTResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot open file '" + path.getValue() + "' - does not exist", execCtx));
                }
                break;
            case "wb":
            case "w":
            case "+":
            case "b+":
                if (!Files.exists(file.toPath())) {
                    try {
                        Files.createFile(file.toPath());
                    } catch (IOException e) {
                        return new RTResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot create file '" + path.getValue() + "'", execCtx));
                    }
                }
                break;
            default:
                return new RTResult().failure(new Error.InvalidSyntaxError(fun.getPosStart(), fun.getPosEnd(), "Expected file mode: either 'a', 'r', 'w', 'ab', 'rb', 'wb', '+', or 'b+'"));
        }
        f = new LFile(Paths.get(path.getValue()).toAbsolutePath().toString(), mode.getValue());
        return new RTResult().success(f.setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
