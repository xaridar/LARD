package xaridar.lscript;

/*
 * This is a project written by Elliot Topper called LScript.
 * It is an interpreted scripting language with static typing.
 *
 * @version 2.0.0
 * @author Xaridar
 */

import xaridar.lscript.errors.Error;
import xaridar.lscript.interpreting.*;
import xaridar.lscript.interpreting.types.BuiltInFunction;
import xaridar.lscript.interpreting.types.LBoolean;
import xaridar.lscript.interpreting.types.LList;
import xaridar.lscript.interpreting.types.NullType;
import xaridar.lscript.interpreting.types.builtins.IExecutable;
import xaridar.lscript.interpreting.types.builtins.math.MathConstants;
import xaridar.lscript.lexing.Lexer;
import xaridar.lscript.lexing.Token;
import xaridar.lscript.parsing.ParseResult;
import xaridar.lscript.parsing.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Main class, which takes input from cmd or a file and interprets it.
 */
public class Shell {
    public static SymbolTable GLOBAL_SYMBOL_TABLE = new SymbolTable();
    public static String baseDir = "";

    /**
     * @param args - cmd line arguments - if none are passed, it reads from cmd; otherwise, it looks for a file to read with the specified path
     * @throws IOException when no file is found for commandline url
     */
    public static void main(String[] args) throws IOException {

        GLOBAL_SYMBOL_TABLE.set("nullType", "null", NullType.Null, ModifierList.getDefault());
        GLOBAL_SYMBOL_TABLE.set("bool", "true", LBoolean.True, ModifierList.getDefault());
        GLOBAL_SYMBOL_TABLE.set("bool", "false", LBoolean.False, ModifierList.getDefault());
        GLOBAL_SYMBOL_TABLE.set("float", "pi", MathConstants.Pi, ModifierList.getDefault());
        for (IExecutable func : IExecutable.builtins) {
            GLOBAL_SYMBOL_TABLE.set("function", func.getName(), new BuiltInFunction(func.getName()), ModifierList.getDefault());
        }
        InputStream in;
        String fn;
        if (args.length == 0) {
            baseDir = System.getProperty("user.dir");
        } else if (Files.isDirectory(Paths.get(args[0]))) {
            baseDir = args[0];
        }
        if (!baseDir.equals("")) {
            in = System.in;
            fn = "<stdin>";
            Scanner scanner = new Scanner(in);
            boolean listening = true;
            Context context = new Context(fn, null, null);
            context.setSymbolTable(GLOBAL_SYMBOL_TABLE);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    System.out.println("\nClosing LScript...");
                    Thread.sleep(200);
                } catch (InterruptedException exc) {
                    Thread.currentThread().interrupt();
                }
            }));
            while (listening) {
                System.out.print("LScript > ");
                String text = "";
                try {
                    text = scanner.nextLine();
                } catch (NoSuchElementException ignored) {
                    listening = false;
                }
                if (text.trim().equals("")) continue;
                Tuple<Object, Error> result = run(fn, text, context);

                if (result.right != null) {
                    System.out.println(result.right.toString());
                }
                else if (result.left != null) {
                    if (((LList) result.left).getElements().size() == 1) {
                        if (((LList) result.left).getElements().get(0) != NullType.Void)
                            System.out.println(((LList) result.left).getElements().get(0));
                    } else if (((LList) result.left).getElements().size() != 0) System.out.println(result.left);
                }
            }
        } else {
            if (args.length != 1) {
                System.out.println("Commandline arguments are not currently supported for LScript.");
                System.exit(0);
            }
            Path p = Paths.get(args[0]);
            if (!Files.exists(p)) {
                System.out.println("File does not exist.");
                System.exit(0);
            }
            PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.ls");
            if (!pathMatcher.matches(p.getFileName())) {
                System.out.println("File is of an incorrect format. Please use LScript source files (*.ls).");
                System.exit(0);
            }
            fn = args[0];
            baseDir = "";
            if (fn.contains("/") || fn.contains("\\"))
                baseDir = fn.replace("/", "\\").substring(0, fn.replace("/", "\\").lastIndexOf("\\"));
            String text = String.join("\n", Files.readAllLines(p));
            if (text.trim().equals("")) System.exit(0);
            Context context = new Context(fn, null, null);
            context.setSymbolTable(GLOBAL_SYMBOL_TABLE);
            Tuple<Object, Error> result = run(fn, text, context);

            if (result.right != null) {
                System.out.println(result.right.toString());
            }
        }

    }

    /**
     * Runs the interpreter on text as a String
     * @param fn - the name of the input file
     * @param text - the input text
     * @return a Tuple which holds the results of the interpretation
     */
    public static Tuple<Object, Error> run(String fn, String text, Context context) {
        Lexer lexer = new Lexer(fn, text);
        Tuple<List<Token>, Error> tkns = lexer.makeTokens();
        if (tkns.getRight() != null)
            return Tuple.of(null, tkns.getRight());
        List<Token> tokens = tkns.getLeft();

        Parser parser = new Parser(tokens);
        ParseResult ast = parser.parse();
        if (ast.hasError()) return Tuple.of(null, ast.getError());

        Interpreter interpreter = Interpreter.getInstance();
        Interpreter.setOnlySymbols(false);
        RunTimeResult result = interpreter.visit(ast.getNode(), context);
        if (result.hasError()) return Tuple.of(null, result.getError());

        return Tuple.of(result.getValue(), null);
    }

    /**
     * Runs the interpreter on text as a String, for internal functions
     * @param fn - the name of the input file
     * @param text - the input text
     * @param setOnlySymbols - A parameter for interpreting, which tells the Interpreter whether to skip scripts and only interpret variables and functions.
     * @return a Tuple which holds any Error result of the interpretation and the main Context
     */
    public static Tuple<Context, Error> runInternal(String fn, String text, boolean setOnlySymbols) {
        Context context = new Context(fn, null, null);
        context.setSymbolTable(new SymbolTable(GLOBAL_SYMBOL_TABLE));
        Lexer lexer = new Lexer(fn, text);
        Tuple<List<Token>, Error> tkns = lexer.makeTokens();
        if (tkns.getRight() != null)
            return Tuple.of(null, tkns.getRight());
        List<Token> tokens = tkns.getLeft();

        Parser parser = new Parser(tokens);
        ParseResult ast = parser.parse();
        if (ast.hasError()) return Tuple.of(null, ast.getError());

        Interpreter interpreter = Interpreter.getInstance();
        Interpreter.setOnlySymbols(setOnlySymbols);
        RunTimeResult result = interpreter.visit(ast.getNode(), context);
        Interpreter.setOnlySymbols(false);
        if (result.hasError()) return Tuple.of(null, result.getError());

        return Tuple.of(context, null);
    }
}
