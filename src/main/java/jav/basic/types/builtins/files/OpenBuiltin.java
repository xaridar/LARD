package jav.basic.types.builtins.files;

import jav.Tuple;
import jav.basic.Context;
import jav.basic.Error;
import jav.basic.results.RTResult;
import jav.basic.types.BuiltInFunction;
import jav.basic.types.Str;
import jav.basic.types.builtins.IExecutable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class OpenBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("str", "path"), Tuple.of("str", "mode")));
    }

    @Override
    public String getName() {
        return "open";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        Str path = (Str) execCtx.getSymbolTable().get("path");
        Str mode = (Str) execCtx.getSymbolTable().get("mode");
        jav.basic.types.File f = null;
        File file = new File(path.toString());
        switch (mode.getValue()) {
            case "a":
            case "r":
                if (!Files.exists(file.toPath())) {
                    return new RTResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot open file '" + path.getValue() + "' - does not exist", execCtx));
                }
                break;
            case "w":
                if (!Files.exists(file.toPath())) {
                    try {
                        Files.createFile(file.toPath());
                    } catch (IOException e) {
                        return new RTResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot create file '" + path.getValue() + "'", execCtx));
                    }
                }
                break;
            default:
                return new RTResult().failure(new Error.InvalidSyntaxError(fun.getPosStart(), fun.getPosEnd(), "Expected file mode: either 'a', 'r', or 'w'"));
        }
        f = new jav.basic.types.File(Paths.get(path.getValue()).toAbsolutePath().toString(), mode.getValue());
        return new RTResult().success(f);
    }
}
