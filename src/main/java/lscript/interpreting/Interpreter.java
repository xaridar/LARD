package lscript.interpreting;

import lscript.Constants;
import lscript.Shell;
import lscript.Tuple;
import lscript.errors.Error;
import lscript.lexing.Token;
import lscript.parsing.nodes.*;
import lscript.interpreting.types.*;
import lscript.interpreting.types.LFloat;

import java.io.IOException;
import java.lang.Boolean;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static lscript.TokenEnum.*;

/**
 * Singleton class, which recursively visits all nodes contained in a nested node and calls operations, functions, and other capabilities of the language.
 */
@SuppressWarnings("unused")
public class Interpreter {
    public static boolean setOnlySymbols = false;

    private static Interpreter INSTANCE;

    /**
     * Singleton instance variable retrieval.
     * @return the single Interpreter instance.
     */
    public static Interpreter getInstance() {
        if (INSTANCE == null) INSTANCE = new Interpreter();
        return INSTANCE;
    }

    /**
     * Sets the Interpreter to SetOnlySymbols mode, in which it will not run scripts, but only create Symbols.
     * @param setOnlySymbols - Toggle for SetOnlySymbolsMode.
     */
    public static void setOnlySymbols(boolean setOnlySymbols) {
        Interpreter.setOnlySymbols = setOnlySymbols;
    }

    /**
     * Recursively visits a Node and all of its nested Nodes.
     * @param node - The Node to interpret.
     * @param context - The Context of the provided Node.
     * @return an RTResult, containing either a Value or an Error.
     */
    public RTResult visit(Node node, Context context) {
        try {
            Method method = getClass().getMethod("visit" + node.getClass().getSimpleName(), node.getClass(), Context.class);
            return (RTResult) method.invoke(this, node, context);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    /**
     * All of the following methods use java.lang.reflect API and are called on specific types of Nodes.
     * Each of them takes two parameters: A node of their specified type to interpret, and the Context of said Node.
     * They all return an RTResult, containing either a Value or an Error.
     */

    public RTResult visitNumberNode(NumberNode node, Context context) {
        if (node.getToken().getType().equals(TT_INT))
            return new RTResult().success(new LInt((int) node.getToken().getValue()).setContext(context).setPos(node.getPosStart(), node.getPosEnd()));
        else if (node.getToken().getType().equals(TT_FLOAT))
            return new RTResult().success(new LFloat((float) node.getToken().getValue()).setContext(context).setPos(node.getPosStart(), node.getPosEnd()));
        return new RTResult().failure(new Error.InvalidSyntaxError(node.getPosStart(), node.getPosEnd(), "Expected 'int' or 'float'"));
    }

    public RTResult visitStringNode(StringNode node, Context context) {
        return new RTResult().success(new LString((String) node.getToken().getValue(), context).setPos(node.getPosStart(), node.getPosEnd()));
    }

    public RTResult visitListNode(ListNode node, Context context) {
        RTResult res = new RTResult();
        List<Value> vals = new ArrayList<>();
        for (Node n : node.getNodes()) {
            vals.add(res.register(visit(n, context)));
            if (res.shouldReturn()) return res;
        }
        return res.success(new LList(vals).setPos(node.getPosStart(), node.getPosEnd()));
    }

    public RTResult visitMultilineNode(MultilineNode node, Context context) {
        RTResult res = new RTResult();
        List<Value> vals = new ArrayList<>();
        for (Node n : node.getNodes()) {
            if (setOnlySymbols && context.getParent() == null) {
                if (!(n instanceof FuncDefNode || n instanceof VarAssignNode)) {
                    continue;
                }
            }
            vals.add(res.register(visit(n, context)));
            if (res.shouldReturn()) return res;
        }
        return res.success(new LList(vals).setPos(node.getPosStart(), node.getPosEnd()));
    }

    public RTResult visitMapNode(MapNode node, Context context) {
        RTResult res = new RTResult();
        HashMap<Value, Value> pairs = new HashMap<>();
        for (Tuple<Node, Node> tup : node.getPairs()) {
            pairs.put(res.register(visit(tup.getLeft(), context)), res.register(visit(tup.getRight(), context)));
            if (res.shouldReturn()) return res;
        }
        return res.success(new LMap(pairs).setContext(context).setPos(node.getPosStart(), node.getPosEnd()));
    }

    public RTResult visitBinaryOperationNode(BinaryOperationNode node, Context context) {
        RTResult res = new RTResult();
        Value left = res.register(visit(node.getStartNode(), context));
        if (res.shouldReturn()) return res;
        Value right = res.register(visit(node.getEndNode(), context));
        if (res.shouldReturn()) return res;
        Tuple<BasicType, Error> result = left.apply(node.getOperationToken(), right);
        if (result == null)
            return res;
        if (result.getRight() != null) return res.failure(result.getRight());
        return res.success(result.getLeft().setContext(context).setPos(node.getPosStart(), node.getPosEnd()));
    }

    public RTResult visitUnaryOperationNode(UnaryOperationNode node, Context context) {
        RTResult res = new RTResult();
        Value val = res.register(visit(node.getNode(), context));
        if (res.shouldReturn()) return res;

        Tuple<BasicType, Error> result = null;
        if (node.getOperationToken().getType().equals(TT_MINUS)) {
            result = val.apply(new Token(TT_MUL, null, node.getOperationToken().getPosStart(), node.getOperationToken().getPosEnd(), "*"), new LInt(-1));
        } else if (node.getOperationToken().getType().equals(TT_BANG)) {
            result = Tuple.of(new LBoolean(!(val.isTrue())).setContext(context).setPos(val.getPosStart(), val.getPosEnd()), null);
        }

        if (result != null) {
            if (result.getRight() != null)
                return res.failure(result.getRight());
            val = result.getLeft();
        }
        return res.success(val.setContext(context).setPos(node.getPosStart(), node.getPosEnd()));
    }

    public RTResult visitVarAccessNode(VarAccessNode node, Context context) {
        RTResult res = new RTResult();
        String varName = (String) node.getToken().getValue();
        Value value;
        if (node.getContext() == null)
            value = context.getSymbolTable().get(varName);
        else {
            if (context.getContainedByName(node.getContext().getValue().toString()) == null) {
                return res.failure(new Error.RunTimeError(node.getContext().getPosStart(), node.getContext().getPosEnd(), "'" + node.getContext().getValue().toString() + "' is not defined", context));
            }
            value = context.getContainedByName(node.getContext().getValue().toString()).getSymbolTable().get(varName);
        }
        if (value == null) return res.failure(new Error.RunTimeError(node.getPosStart(), node.getPosEnd(), "'" + varName + "' is not defined", context));
        value = value.copy().setPos(node.getPosStart(), node.getPosEnd()).setContext(context);
        return res.success(value);
    }

    public RTResult visitVarAssignNode(VarAssignNode node, Context context) {
        RTResult res = new RTResult();
        String varName = (String) node.getToken().getValue();
        Value value = res.register(visit(node.getValueNode(), context));
        if (res.shouldReturn()) return res;

        String expectedType = null;
        if (node.getType() != null) {
            expectedType = (String) node.getType().getValue();
        }
        if (expectedType != null) {
            if (Constants.getInstance().TYPES.get(expectedType) == null || Constants.getInstance().TYPES.get(value.getType()).contains(expectedType) || value.getType().equals("nullType")) {
                value.setType(expectedType);
                Error err = context.getSymbolTable().set(expectedType, varName, value, false);
                if (err != null)
                    return res.failure(err);
                return res.success(value);
            } else if (Constants.getInstance().CONVERT_CLASSES.containsKey(expectedType)) {
                try {
                    value = res.register((RTResult) Constants.getInstance().CONVERT_CLASSES.get(expectedType).getMethod("from", Value.class).invoke(null, value));
                    if (res.shouldReturn()) return res;
                    Error err = context.getSymbolTable().set(expectedType, varName, value, false);
                    if (err != null)
                        return res.failure(err);
                    return res.success(value);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            return res.failure(new Error.RunTimeError(value.getPosStart(), value.getPosEnd(), "Wrong type; Expected '" + expectedType + "', got '" + value.getType() + "'", context));
        } else if (context.getSymbolTable().hasVar(varName)) {

            Error err = context.getSymbolTable().set(null, varName, value, false);
            if (err != null) {
                if (Constants.getInstance().CONVERT_CLASSES.containsKey(
                        context.getSymbolTable().getSymbolByName(varName).getType())) {
                    try {
                        value = res.register((RTResult) Constants.getInstance().CONVERT_CLASSES.get(context.getSymbolTable().getSymbolByName(varName).getType()).getMethod("from", Value.class).invoke(null, value));
                        if (res.shouldReturn()) return res;
                        err = context.getSymbolTable().set(null, varName, value, false);
                        if (err != null)
                            return res.failure(err);
                        return res.success(value);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
                return res.failure(err);
            }
            return res.success(value);
        }
        return res.failure(new Error.RunTimeError(node.getPosStart(), node.getPosEnd(), "Type not defined. Use 'var' or 'const' for dynamic typing.", context));
    }

    public RTResult visitConditionalNode(ConditionalNode node, Context context) {
        RTResult res = new RTResult();
        for (Tuple<Tuple<Node, Node>, Boolean> cond : node.getCases()) {
            Tuple<Node, Node> condition = cond.getLeft();
            Value conditionVal = res.register(visit(condition.getLeft(), context));
            if (res.shouldReturn()) return res;
            if (!(conditionVal instanceof BasicType)) {
                return res.failure(new Error.RunTimeError(conditionVal.getPosStart(), conditionVal.getPosEnd(), "Expected boolean expression", context));
            }
            if (conditionVal.isTrue()) {
                Value exprValue = res.register(visit(condition.getRight(), context));
                if (res.shouldReturn()) return res;
                if (!cond.getRight()) return res.success(exprValue);
                else return res.success(NullType.Void);
            }
        }
        if (node.getElseCase() != null) {
            Value elseVal = res.register(visit(node.getElseCase().getLeft(), context));
            if (res.shouldReturn()) return res;
            if (!node.getElseCase().getRight()) return res.success(elseVal);
            else return res.success(NullType.Void);
        }
        return res.success(NullType.Void);
    }

    public RTResult visitForNode(ForNode node, Context context) {
        RTResult res = new RTResult();
        LInt step_value;
        Context loopContext = new Context("<anonymous for loop>", context, node.getPosStart());
        loopContext.setSymbolTable(new SymbolTable(context.getSymbolTable()));

        Value start = res.register(visit(node.getStartValueNode(), context));
        if (res.shouldReturn()) return res;
        if (!(start instanceof LInt))
            return res.failure(new Error.RunTimeError(start.getPosStart(), start.getPosEnd(), "Expected int value", context));
        LInt startValue = (LInt) start;

        Value end = res.register(visit(node.getEndValueNode(), context));
        if (res.shouldReturn()) return res;
        if (!(end instanceof LInt))
            return res.failure(new Error.RunTimeError(end.getPosStart(), end.getPosEnd(), "Expected int value", context));
        LInt endValue = (LInt) end;

        if (node.getStepNode() != null) {
            Value step = res.register(visit(node.getStepNode(), context));
            if (res.shouldReturn()) return res;
            if (!(step instanceof LInt))
                return res.failure(new Error.RunTimeError(step.getPosStart(), step.getPosEnd(), "Expected int value", context));
            step_value = (LInt) step;
        } else
            step_value = new LInt(1);

         int[] i = {startValue.getValue()};

         Predicate<Void> condition;

         if (step_value.getValue() >= 0) {
            condition = v -> i[0] < endValue.getValue();
         } else {
             condition = v -> i[0] > endValue.getValue();
         }
         context.getSymbolTable().set(((String) node.getVarTypeToken().getValue()), ((String) node.getVarNameToken().getValue()), new LInt(i[0]), false);
         while (condition.test(null)) {
             context.getSymbolTable().set(null, ((String) node.getVarNameToken().getValue()), new LInt(i[0]), false);
             i[0] += step_value.getValue();

             res.register(visit(node.getBodyNode(), loopContext));
             if (!res.isLoopBreak() && !res.isLoopCont() && res.shouldReturn()) return res;
             loopContext.getSymbolTable().removeAll();
             if (res.isLoopCont())
                 continue;
             if (res.isLoopBreak())
                 break;

         }
         context.getSymbolTable().remove((String) node.getVarNameToken().getValue());
         return res.success(NullType.Void);
    }

    public RTResult visitWhileNode(WhileNode node, Context context) {
        RTResult res = new RTResult();
        Context loopContext = new Context("<anonymous while loop>", context, node.getPosStart());
        loopContext.setSymbolTable(new SymbolTable(context.getSymbolTable()));

        while (true) {
            Value condition = res.register(visit(node.getConditionNode(), loopContext));
            if (res.shouldReturn()) return res;
            if (!(condition instanceof BasicType))
                return res.failure(new Error.RunTimeError(condition.getPosStart(), condition.getPosEnd(), "Expected boolean expression", loopContext));
            if (!condition.isTrue()) break;
            res.register(visit(node.getBodyNode(), loopContext));
            if (!res.isLoopBreak() && !res.isLoopCont() && res.shouldReturn()) return res;
            loopContext.getSymbolTable().removeAll();
            if (res.isLoopCont())
                continue;
            if (res.isLoopBreak())
                break;
        }

        return res.success(NullType.Void);
    }


    public RTResult visitFuncDefNode(FuncDefNode node, Context context) {
        RTResult res = new RTResult();
        List<ReturnNode> retNodes = node.getBodyNode().getNodes().stream().filter(n -> n instanceof ReturnNode).map(n -> (ReturnNode) n).collect(Collectors.toList());
        List<String> retTypes = node.getReturnTypes();

        String funcName = null;
        if (node.getVarNameToken() != null)
            funcName = (String) node.getVarNameToken().getValue();
        Node bodyNode = node.getBodyNode();
        List<String> returnTypes = node.getReturnTypes();
        List<Tuple<String, String>> argNames = node.getArgTokens().stream().map(t -> Tuple.of((String) t.getLeft().getValue(), (String) t.getRight().getValue())).collect(Collectors.toList());
        Value funcValue = new LFunction(funcName, bodyNode, argNames, returnTypes).setContext(context).setPos(node.getPosStart(), node.getPosEnd());

        if (funcName != null)
            context.getSymbolTable().set("function", funcName, funcValue, false);
        for (ReturnNode retNode : retNodes) {
            if (retNode.getNodesToCall().size() != retTypes.size() && !(retNode.getNodesToCall().size() == 0 && retTypes.size() == 1 && retTypes.get(0).equals("void"))) {
                context.getSymbolTable().remove(funcName);
                return res.failure(new Error.RunTimeError(retNode.getPosStart(), retNode.getPosEnd(), "Wrong number of return types; Expected " + retTypes.size() + ", got " + retNodes.size(), context));
            }
            List<Node> sampleParams = node.getArgTokens().stream().map(tokenTokenTuple -> BasicType.getDefaultValue(tokenTokenTuple.getLeft().getValue().toString(), retNode.getPosStart())).collect(Collectors.toList());
            Value val = res.register(visit(new CallNode(new VarAccessNode(new Token(TT_IDENTIFIER, funcName, funcValue.getPosStart(), funcValue.getPosEnd().copy(), null)), sampleParams), context));
            List<Value> vals = new ArrayList<>();
            if (val instanceof LList)
                vals.addAll(((LList) val).getElements());
            else vals.add(val);
            for (int i = 0, retValSize = retNode.getNodesToCall().size(); i < retValSize; i++) {
                Node n = retNode.getNodesToCall().get(i);
                if (!(Constants.getInstance().TYPES.get(retTypes.get(i)) == null || Constants.getInstance().TYPES.get(vals.get(i).getType()).contains(retTypes.get(i)))) {
                    context.getSymbolTable().remove(funcName);
                    return res.failure(new Error.RunTimeError(n.getPosStart(), n.getPosEnd(), "Wrong type; Expected '" + retTypes.get(i) + "', got '" + vals.get(i).getType() + "'", context));
                }
            }
        }
        if (retNodes.size() == 0 && (retTypes.size() > 1 && retTypes.get(0).equals("void"))) {
            context.getSymbolTable().remove(funcName);
            return res.failure(new Error.RunTimeError(node.getPosStart(), node.getPosEnd(), "Wrong number of return types; Expected " + retTypes.size() + ", got 0", context));
        }

        return res.success(funcValue);
    }

    public RTResult visitCallNode(CallNode node, Context context) {
        RTResult res = new RTResult();
        List<Value> args = new ArrayList<>();
        Value valueToCall = res.register(visit(node.getNodeToCall(), context));
        if (res.shouldReturn()) return res;
        valueToCall = valueToCall.copy().setPos(node.getPosStart(), node.getPosEnd()).setContext(context);

        for (Node argNode : node.getArgNodes()) {
            args.add(res.register(visit(argNode, context)));
            if (res.shouldReturn()) return res;
        }

        Value returnVal = res.register(valueToCall.execute(args));
        if (res.shouldReturn()) return res;
        returnVal = returnVal.copy().setContext(context).setPos(node.getPosStart(), node.getPosEnd());
        return res.success(returnVal);
    }

    public RTResult visitIndexNode(IndexNode node, Context context) {
        RTResult res = new RTResult();
        Value left = res.register(visit(node.getLeft(), context));
        if (res.shouldReturn()) return res;
        Value startIndex = res.register(visit(node.getStartIndex(), context));
        if (res.shouldReturn()) return res;
        Value endIndex = res.register(visit(node.getEndIndex(), context));
        if (res.shouldReturn()) return res;
        Tuple<Value, Error> result = left.elementAt(startIndex, endIndex);
        if (result.getRight() != null) return res.failure(result.getRight());
        return res.success(result.getLeft());
    }

    public RTResult visitSetIndexNode(SetIndexNode node, Context context) {
        RTResult res = new RTResult();
        Value left = res.register(visit(node.getLeft(), context));
        if (res.shouldReturn()) return res;
        Value startIndex = res.register(visit(node.getStartIndex(), context));
        if (res.shouldReturn()) return res;
        Value endIndex = res.register(visit(node.getEndIndex(), context));
        if (res.shouldReturn()) return res;
        Value value = res.register(visit(node.getVal(), context));
        if (res.shouldReturn()) return res;
        Tuple<Value, Error> result = left.setElementAt(startIndex, value);
        if (result.getRight() != null) return res.failure(result.getRight());
        return res.success(result.getLeft());
    }

    public RTResult visitContinueNode(ContinueNode node, Context context) {
        return new RTResult().successCont();
    }

    public RTResult visitReturnNode(ReturnNode node, Context context) {
        RTResult res = new RTResult();
        List<Value> retVals = new ArrayList<>();
        if (node.getNodesToCall().size() != 0) {
            for (Node n : node.getNodesToCall()) {
                retVals.add(res.register(visit(n, context)));
            }
        }
        if (retVals.size() == 1) {
            return res.successRet(retVals.get(0));
        }
        if (retVals.size() == 0) {
            return res.success(NullType.Void);
        }
        return res.successRet(new LList(retVals));
    }

    public RTResult visitBreakNode(BreakNode node, Context context) {
        return new RTResult().successBreak();
    }

    public RTResult visitImportNode(ImportNode node, Context context) {
        RTResult res = new RTResult();
        Path path = Paths.get(node.getFileName().getValue() + ".ls");
        if (!Files.exists(path)) return res.failure(new Error.FileAccessError(node.getFileName().getPosStart(), node.getFileName().getPosEnd(), "File not found: '" + path.toAbsolutePath() + "'", context));
        try {
            Tuple<Context, Error> resCtx = Shell.runInternal(path.getFileName().toString(), Files.readString(path), true);
            if (resCtx.getRight() != null) return res.failure(resCtx.getRight());
            if (node.importAll()) {
                resCtx.getLeft().getSymbolTable().symbols.forEach(symbol -> {
                    if (!Shell.GLOBAL_SYMBOL_TABLE.hasVar(symbol.getName())) {
                        context.getSymbolTable().set(symbol.getType(), symbol.getName(), symbol.getValue(), !symbol.canEdit());
                    }
                });
            } else {
                Context extCtx = resCtx.getLeft();
                List<Token> tokensToImport = node.getTokensToImport();
                for (int i = 0, tokensToImportSize = tokensToImport.size(); i < tokensToImportSize; i++) {
                    Token token = tokensToImport.get(i);
                    Value val = extCtx.getSymbolTable().get(token.getValue().toString());
                    if (val == null)
                        return res.failure(new Error.ImportError(token.getPosStart(), token.getPosEnd(), "No importable variable found from file '" + node.getFileName() + "' with name '" + token.getValue().toString() + "':", context));
                    Error err = context.getSymbolTable().set(val.getType(), node.getNames().get(i), val, true);
                    if (err != null) return res.failure(err);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res.success(NullType.Void);
    }

    public RTResult visitFileImportNode(FileImportNode node, Context context) {
        RTResult res = new RTResult();
        Path path = Paths.get(node.getFileName().getValue() + ".ls");
        if (!Files.exists(path)) return res.failure(new Error.FileAccessError(node.getFileName().getPosStart(), node.getFileName().getPosEnd(), "File not found: '" + path.toAbsolutePath() + "'", context));
        try {
            Tuple<Context, Error> resCtx = Shell.runInternal(path.getFileName().toString(), Files.readString(path), true);
            if (resCtx.getRight() != null) return res.failure(resCtx.getRight());
            context.addContainedContext(node.getName(), resCtx.getLeft());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res.success(NullType.Void);
    }
}
