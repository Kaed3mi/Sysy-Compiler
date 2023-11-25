package frontend;

import frontend.lexical.Lexeme;
import frontend.lexical.Token;
import frontend.semantic.Symbol;
import frontend.semantic.initialization.ArrayInitialization;
import frontend.semantic.initialization.Initialization;
import frontend.semantic.initialization.VarInitialization;
import frontend.syntax.ast.declaration.InitVal;
import frontend.syntax.ast.expression.Number;
import frontend.syntax.ast.expression.*;
import midend.GlobalVar;
import midend.constant.Constant;
import midend.constant.IntConstant;
import midend.value.Value;

import java.util.ArrayList;
import java.util.Iterator;

public class Evaluator {

    private final Visitor visitor;

    public Evaluator(Visitor visitor) {
        this.visitor = visitor;
    }


//    private Constant evalInitVal(InitVal initVal) {
//        if (initVal instanceof BinaryExp) {
//            return evalBinaryExp((BinaryExp) initVal);
//        } else if (initVal instanceof UnaryExp) {
//            return evalUnaryExp((UnaryExp) initVal);
//        } else {
//            throw new RuntimeException("Eval exception");
//        }
//    }

    public Constant evalConstExp(ConstExp constExp) {
        if (constExp instanceof BinaryExp) {
            return evalBinaryExp((BinaryExp) constExp);
        } else if (constExp instanceof UnaryExp) {
            return evalUnaryExp((UnaryExp) constExp);
        } else {
            throw new RuntimeException("Eval exception");
        }
    }

    private Constant evalBinaryExp(BinaryExp binaryExp) {
        Exp first = binaryExp.getFirst();
        Iterator<Token> opIter = binaryExp.getOperators().listIterator();
        Iterator<Exp> expIter = binaryExp.getFollows().listIterator();
        Constant result;
        if (first instanceof BinaryExp) {
            result = evalBinaryExp((BinaryExp) first);
        } else {
            result = evalUnaryExp((UnaryExp) first);
        }
        while (opIter.hasNext() && expIter.hasNext()) {
            Lexeme op = opIter.next().getLexeme();
            Exp follow = expIter.next();
            Constant next;
            if (follow instanceof BinaryExp) {
                next = evalBinaryExp((BinaryExp) follow);
            } else {
                next = evalUnaryExp((UnaryExp) follow);
            }
            result = cal(op, result, next);
        }
        return result;
    }

    private Constant evalUnaryExp(UnaryExp unaryExp) {
        Constant first = evalPrimaryExp(unaryExp.getPrimaryExp());
        ArrayList<Token> opIter = unaryExp.getOperators();
        for (int i = opIter.size() - 1; i >= 0; i--) {
            Lexeme op = opIter.get(i).getLexeme();
            first = cal(op, IntConstant.ZERO, first);
        }
        return first;
    }

    private Constant evalPrimaryExp(PrimaryExp primaryExp) {
        if (primaryExp instanceof Number) {
            return evalNumber((Number) primaryExp);
        } else if (primaryExp instanceof Exp) {
            return evalConstExp((ConstExp) primaryExp);
        } else if (primaryExp instanceof LVal) {
            //throw new RuntimeException("Eval exception");
            return evalLVal((LVal) primaryExp);
        } else {
            throw new RuntimeException("Eval exception");
        }
    }

    private Constant evalNumber(Number number) {
        return new IntConstant(number);
    }


    private Constant evalLVal(LVal lVal) {
        Symbol symbol = visitor.curSymTable.find(lVal.getIdent());

        Initialization initialization;
        if (symbol.isConstant()) {
            initialization = symbol.constInitialization();
        } else if (symbol.pointer() instanceof GlobalVar) {
            initialization = ((GlobalVar) symbol.pointer()).initialization();
        } else {
            //initialization = new VarInitialization(IntConstant.ZERO);
            throw new RuntimeException();
        }
        ArrayList<IntConstant> indexEvals = new ArrayList<>();
        for (Exp index : lVal.getArrayDim()) {
            indexEvals.add((IntConstant) evalConstExp((ConstExp) index));
        }
        for (IntConstant index : indexEvals) {
            if (initialization instanceof VarInitialization) {
                return IntConstant.ZERO;
            } else if (initialization instanceof ArrayInitialization) {
                initialization = ((ArrayInitialization) initialization).get(index.getVal());
            } else {
                throw new RuntimeException("");
            }
        }
        Value ret = ((VarInitialization) initialization).initVal();
        return ((Constant) ret);
    }

    private static Constant cal(Lexeme op, Constant left, Constant right) {
        int l = Integer.parseInt(left.toString());
        int r = Integer.parseInt(right.toString());
        int v = switch (op) {
            case PLUS -> l + r;
            case MINU -> l - r;
            case MULT -> l * r;
            case DIV -> l / r;
            case MOD -> l % r;
            default -> throw new RuntimeException("你不准eval");
        };
        return IntConstant.FromInt(v);
    }

}
