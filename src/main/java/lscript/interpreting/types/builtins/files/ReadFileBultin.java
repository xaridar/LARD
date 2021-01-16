package lscript.interpreting.types.builtins.files;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.errors.Error;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.LFile;
import lscript.interpreting.types.LString;
import lscript.interpreting.types.builtins.IExecutable;

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
        LFile f = (LFile) execCtx.getSymbolTable().get("f");
        if (!f.canRead()) {
            return new RTResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot read a file in '" + f.getAccessMode() + "' mode.", execCtx));
        }
        try {
            StringBuilder s = new StringBuilder();
            Files.lines(Path.of(f.getPath()), StandardCharsets.UTF_8).forEach(s::append);
            return new RTResult().success(new LString(s.toString()).setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
        } catch (IOException e) {
            return new RTResult().failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot find file '" + f.getPath() + "'", execCtx));
        }
    }
}
