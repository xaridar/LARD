package jav.basic.types.builtins.files;

import jav.Tuple;
import jav.basic.Context;
import jav.basic.Error;
import jav.basic.results.RTResult;
import jav.basic.types.BuiltInFunction;
import jav.basic.types.File;
import jav.basic.types.Str;
import jav.basic.types.builtins.IExecutable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ReadFileBultin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return List.of(List.of(Tuple.of("file", "f")));
    }

    @Override
    public String getName() {
        return "readfile";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        File f = (File) execCtx.getSymbolTable().get("f");
        if (!f.canRead()) {
            return new RTResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot read a file in '" + f.getAccessMode() + "' mode.", execCtx));
        }
        try {
            StringBuilder s = new StringBuilder();
            Files.lines(Path.of(f.getPath()), StandardCharsets.UTF_8).forEach(s::append);
            return new RTResult().success(new Str(s.toString()));
        } catch (IOException e) {
            return new RTResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot find file '" + f.getPath() + "'", execCtx));
        }
    }
}
