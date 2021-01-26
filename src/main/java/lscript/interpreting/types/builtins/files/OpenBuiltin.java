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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenBuiltin implements IExecutable {
    @Override
    public List<List<Tuple<String, String>>> getArgNames() {
        return Collections.singletonList(Arrays.asList(Tuple.of("str", "path"), Tuple.of("str", "modes")));
    }

    @Override
    public String getName() {
        return "open";
    }

    @Override
    public RTResult execute(Context execCtx, int execNum, BuiltInFunction fun) {
        RTResult res = new RTResult();
        LString path = (LString) execCtx.getSymbolTable().get("path");
        LString modes = (LString) execCtx.getSymbolTable().get("modes");
        LFile f;
        String modeStr = modes.getValue();
        File file = new File(path.toString());
        if (!modeStr.matches("[wrabt+]+")) return res.failure(new Error.InvalidSyntaxError(fun.getPosStart(), fun.getPosEnd(), "Invalid mode set; Expected some combination of 'a', 'r', 'w', 't', 'b', and/or '+'"));
        StringBuilder found = new StringBuilder();
        for (int i = 0; i < modeStr.length(); i++) {
            if (found.toString().contains(Character.toString(modeStr.charAt(i)))) {
                return res.failure(new Error.InvalidSyntaxError(fun.getPosStart(), fun.getPosEnd(), "Repeat character: " + modeStr.charAt(i)));
            }
            found.append(modeStr.charAt(i));
        }

        Matcher accessMatcher = Pattern.compile("[arw]").matcher(modeStr);
        int accessModes = 0;
        while (accessMatcher.find()) {
            accessModes++;
        }
        if (accessModes > 1) return res.failure(new Error.InvalidSyntaxError(fun.getPosStart(), fun.getPosEnd(), "Cannot use more than 1 of: 'a', 'r', 'w'. If you require both read and write access, use '+'."));
        Matcher typeMatcher = Pattern.compile("[tb]").matcher(modeStr);
        int typeModes = 0;
        while (typeMatcher.find()) {
            typeModes++;
        }
        if (typeModes > 1) return res.failure(new Error.InvalidSyntaxError(fun.getPosStart(), fun.getPosEnd(), "Cannot use more than 1 of: 't', 'b'."));
        if (modeStr.contains("w")) {
            if (!Files.exists(file.toPath())) {
                try {
                    Files.createFile(file.toPath());
                } catch (IOException e) {
                    return res.failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosEnd(), "Cannot create file '" + path.getValue() + "'", execCtx));
                }
            }
        } else {
            if (!Files.exists(file.toPath())) {
                return res.failure(new Error.FileAccessError(fun.getPosStart(), fun.getPosStart(), "File not found. Cannot create file in '" + modeStr + "' mode.", execCtx));
            }
        }
        f = new LFile(Paths.get(path.getValue()).toAbsolutePath().toString(), modeStr);
        return res.success(f.setPos(fun.getPosStart(), fun.getPosEnd()).setContext(fun.getContext()));
    }
}
