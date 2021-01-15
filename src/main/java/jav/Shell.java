package jav;

import jav.basic.Error;
import jav.basic.*;
import jav.basic.results.ParseResult;
import jav.basic.results.RTResult;
import jav.basic.types.Boolean;
import jav.basic.types.BuiltInFunction;
import jav.basic.types.NullType;
import jav.basic.types.builtins.IExecutable;
import jav.basic.types.builtins.math.MathConstants;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Shell {
    public static SymbolTable global_symbol_table = new SymbolTable();
    public static void main(String[] args) throws IOException {

        global_symbol_table.set("nullType", "null", NullType.Null, true);
        global_symbol_table.set("bool", "true", Boolean.True, true);
        global_symbol_table.set("bool", "false", Boolean.False, true);
        global_symbol_table.set("float", "pi", MathConstants.Pi, true);
        for (IExecutable func : IExecutable.builtins) {
            global_symbol_table.set("function", func.getName(), new BuiltInFunction(func.getName()), true);
        }
        InputStream in;
        String fn;
        if (args.length == 0) {
            in = System.in;
            fn = "<stdin>";
            Scanner scanner = new Scanner(in);
            while (true) {
                System.out.print("LScript > ");
                String text = scanner.nextLine();
                if (text.strip().equals("")) continue;
                Tuple<Object, Error> result = run(fn, text);

                if (result.right != null) {
                    System.out.println(result.right.toString());
                }
                else if (result.left != null) {
                    if (((jav.basic.types.List) result.left).getElements().size() == 1) {
                        if (((jav.basic.types.List) result.left).getElements().get(0) != NullType.Void)
                            System.out.println(((jav.basic.types.List) result.left).getElements().get(0));
                    } else System.out.println(result.left);
                }
            }
        } else {
            if (args.length != 1) {
                System.out.println("Commandline arguments are not currently supported for LScript.");
                System.exit(0);
            }
            Path p = Path.of(/*System.getProperty("user.dir"),*/ args[0]);
            if (!Files.exists(p)) {
                System.out.println("File does not exist.");
                System.exit(0);
            }
            fn = args[0];
            String text = Files.readString(p);
            if (text.strip().equals("")) System.exit(0);
            Tuple<Object, Error> result = run(fn, text);

            if (result.right != null) {
                System.out.println(result.right.toString());
            }
        }

    }

    public static Tuple<Object, Error> run(String fn, String text) {
        Context context = new Context("<module>", null, null);
        context.setSymbolTable(global_symbol_table);
        Lexer lexer = new Lexer(fn, text, context);
        Tuple<List<Token>, Error> tkns = lexer.make_tokens();
        if (tkns.getRight() != null)
            return Tuple.of(null, tkns.getRight());
        List<Token> tokens = tkns.getLeft();

        Parser parser = new Parser(tokens);
        ParseResult ast = parser.parse();
        if (ast.hasError()) return Tuple.of(null, ast.getError());

        Interpreter interpreter = Interpreter.getInstance();
        RTResult result = interpreter.visit(ast.getNode(), context);
        if (result.hasError()) return Tuple.of(null, result.getError());

        return Tuple.of(result.getValue(), null);
    }
}
