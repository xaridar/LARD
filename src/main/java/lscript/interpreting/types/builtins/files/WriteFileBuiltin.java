package lscript.interpreting.types.builtins.files;

import lscript.Tuple;
import lscript.interpreting.Context;
import lscript.errors.Error;
import lscript.interpreting.RTResult;
import lscript.interpreting.types.BuiltInFunction;
import lscript.interpreting.types.LFile;
import lscript.interpreting.types.NullType;
import lscript.interpreting.types.LString;
import lscript.interpreting.types.builtins.IExecutable;

import java.io.IOException;
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
        LFile f = (LFile) execCtx.getSymbolTable().get("f");
        LString text = (LString) execCtx.getSymbolTable().get("text");
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
