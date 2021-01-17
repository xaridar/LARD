package lscript.interpreting;

import lscript.Constants;
import lscript.Tuple;
import lscript.errors.Error;
import lscript.lexing.Token;
import lscript.parsing.nodes.*;
import lscript.interpreting.types.*;
import lscript.interpreting.types.LFloat;

import java.lang.Boolean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static lscript.Constants.*;

/**
 * Singleton class, which recursively visits all nodes contained in a nested node and calls operations, functions, and other capabilities of the language.
 */
@SuppressWarnings("unused")
public class Interpreter {

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
        if (node.getOperationToken().getType().equals(Constants.TT_MINUS)) {
            result = val.apply(new Token(Constants.TT_MUL, null, node.getOperationToken().getPosStart(), node.getOperationToken().getPosEnd(), "*"), new LInt(-1));
        } else if (node.getOperationToken().getType().equals(Constants.TT_BANG)) {
            result = val.apply(node.getOperationToken(), null);
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
        Value value = context.getSymbolTable().get(varName);
        if (value == null) return res.failure(new Error.RunTimeError(node.getPosStart(), node.getPosEnd(), "'" + varName + "' is not defined", context));
        value = value.copy().setPos(node.getPosStart(), node.getPosEnd()).setContext(context);
        return res.success(value);
    }

    public RTResult visitVarAssignNode(VarAssignNode node, Context context) {
        RTResult res = new RTResult();
        String varName = (String) node.getToken().getValue();
        Value value = res.register(visit(node.getValueNode(), context));
        if (res.shouldReturn()) return res;

        String expectedType = node.getType();
        if (expectedType != null) {
            if (Constants.getInstance().TYPES.get(expectedType) == null || Constants.getInstance().TYPES.get(value.getType()).contains(expectedType) || value.getType().equals("nullType")) {
                if (!value.getType().equals("nullType"))
                    value.setType(expectedType);
                String lastType = context.getSymbolTable().set(expectedType, varName, value, false);
                if (lastType != null)
                    return res.failure(new Error.RunTimeError(value.getPosStart(), value.getPosEnd(), "Wrong type; Expected '" + lastType + "', got '" + value.getType() + "'", context));
                return res.success(value);
            } else if (Constants.getInstance().CONVERT_CLASSES.containsKey(expectedType)) {
                try {
                    value = (BasicType) Constants.getInstance().CONVERT_CLASSES.get(expectedType).getMethod("from", Value.class).invoke(null, value);
                    String lastType = context.getSymbolTable().set(expectedType, varName, value, false);
                    if (lastType != null)
                        return res.failure(new Error.RunTimeError(value.getPosStart(), value.getPosEnd(), "Wrong type; Expected '" + lastType + "', got '" + value.getType() + "'", context));
                    return res.success(value);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            return res.failure(new Error.RunTimeError(value.getPosStart(), value.getPosEnd(), "Wrong type; Expected '" + expectedType + "', got '" + value.getType() + "'", context));
        } else if (context.getSymbolTable().hasVar(varName)) {
            String lastType = context.getSymbolTable().set(null, varName, value, false);
            if (lastType != null)
                return res.failure(new Error.RunTimeError(value.getPosStart(), value.getPosEnd(), "Wrong type; Expected '" + lastType + "', got '" + value.getType() + "'", context));
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
         while (condition.test(null)) {
             context.getSymbolTable().set((String) node.getVarTypeToken().getValue(), (String) node.getVarNameToken().getValue(), new LInt(i[0]), false);
             i[0] += step_value.getValue();

             res.register(visit(node.getBodyNode(), context));
             if (!res.isLoopBreak() && !res.isLoopCont() && res.shouldReturn()) return res;
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

        while (true) {
            Value condition = res.register(visit(node.getConditionNode(), context));
            if (res.shouldReturn()) return res;
            if (!(condition instanceof BasicType))
                return res.failure(new Error.RunTimeError(condition.getPosStart(), condition.getPosEnd(), "Expected boolean expression", context));
            if (!condition.isTrue()) break;
            res.register(visit(node.getBodyNode(), context));
            if (!res.isLoopBreak() && !res.isLoopCont() && res.shouldReturn()) return res;
            if (res.isLoopCont())
                continue;
            if (res.isLoopBreak())
                break;

        }

        return res.success(NullType.Void);
    }


    public RTResult visitFuncDefNode(FuncDefNode node, Context context) {
        RTResult res = new RTResult();
        String funcName = null;
        if (node.getVarNameToken() != null)
            funcName = (String) node.getVarNameToken().getValue();
        Node bodyNode = node.getBodyNode();
        List<String> returnTypes = node.getReturnTypes();
        List<Tuple<String, String>> argNames = node.getArgTokens().stream().map(t -> Tuple.of((String) t.getLeft().getValue(), (String) t.getRight().getValue())).collect(Collectors.toList());
        Value funcValue = new LFunction(funcName, bodyNode, argNames, returnTypes).setContext(context).setPos(node.getPosStart(), node.getPosEnd());

        if (node.getVarNameToken() != null)
            context.getSymbolTable().set("function", funcName, funcValue, false);

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
        Value index = res.register(visit(node.getIndex(), context));
        if (res.shouldReturn()) return res;
        Tuple<Value, Error> result = left.elementAt(index);
        if (result.getRight() != null) return res.failure(result.getRight());
        return res.success(result.getLeft());
    }

    public RTResult visitSetIndexNode(SetIndexNode node, Context context) {
        RTResult res = new RTResult();
        Value left = res.register(visit(node.getLeft(), context));
        if (res.shouldReturn()) return res;
        Value index = res.register(visit(node.getIndex(), context));
        if (res.shouldReturn()) return res;
        Value value = res.register(visit(node.getVal(), context));
        if (res.shouldReturn()) return res;
        Tuple<Value, Error> result = left.setElementAt(index, value);
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
}
