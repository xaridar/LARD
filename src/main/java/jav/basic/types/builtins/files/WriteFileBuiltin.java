package jav.basic.types.builtins.files;

import jav.Tuple;
import jav.basic.Context;
import jav.basic.Error;
import jav.basic.results.RTResult;
import jav.basic.types.BuiltInFunction;
import jav.basic.types.File;
import jav.basic.types.NullType;
import jav.basic.types.Str;
import jav.basic.types.builtins.IExecutable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class WriteFileBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("file", "f"), Tuple.of("str", "text")));
    }

    @Override
    public String getName() {
        return "writefile";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        File f = (File) execCtx.getSymbolTable().get("f");
        Str text = (Str) execCtx.getSymbolTable().get("text");
        if (!f.canWrite()) {
            return new RTResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot write to a file in '" + f.getAccessMode() + "' mode.", execCtx));
        }
        try {
            OpenOption option;
            if (!f.shouldOverwrite()) {
                option = StandardOpenOption.APPEND;
            } else {
                option = StandardOpenOption.WRITE;
            }
            Files.writeString(Path.of(f.getPath()), text.getValue(), option);
            return new RTResult().success(NullType.Void);
        } catch (IOException e) {
            return new RTResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot find file '" + f.getPath() + "'", execCtx));
        }
    }
}
