package frontend;

import frontend.lexical.Lexeme;
import frontend.lexical.Token;
import frontend.semantic.SymTable;
import frontend.semantic.Symbol;
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
    private SymTable symTable;

    public Evaluator(SymTable symTable) {
        this.symTable = symTable;
    }


    public Constant eval(InitVal initVal) {
        if (initVal instanceof BinaryExp) {
            return evalBinaryExp((BinaryExp) initVal);
        } else if (initVal instanceof UnaryExp) {
            return evalUnaryExp((UnaryExp) initVal);
        } else {
            throw new RuntimeException("Eval exception");
        }
    }

    private Constant evalBinaryExp(BinaryExp binaryExp) {
        Exp first = binaryExp.getFirst();
        Iterator<Token> opIter = binaryExp.getOperators().listIterator();
        Iterator<Exp> expIter = binaryExp.getFollows().listIterator();
        Constant result = eval(first);
        while (opIter.hasNext() && expIter.hasNext()) {
            Lexeme op = opIter.next().getLexeme();
            Exp follow = expIter.next();
            result = cal(op, result, eval(follow));
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

    private Constant evalPrimaryExp(PrimaryExp exp) {
        if (exp instanceof Number) {
            return evalNumber((Number) exp);
        } else if (exp instanceof Exp) {
            return evalConstExp((ConstExp) exp);
        } else if (exp instanceof LVal) {
            return evalLVal((LVal) exp);
        } else {
            throw new RuntimeException("Eval exception");
        }
    }

    private Constant evalNumber(Number number) {
        return new IntConstant(number);
    }

    private Constant evalConstExp(ConstExp constExp) {
        if (constExp instanceof BinaryExp) {
            return evalBinaryExp((BinaryExp) constExp);
        } else if (constExp instanceof UnaryExp) {
            return evalUnaryExp((UnaryExp) constExp);
        } else {
            throw new RuntimeException("Eval exception");
        }
    }

    private Constant evalLVal(LVal lVal) {
        // 查找符号表
        Symbol symbol = symTable.find(lVal.getIdent());

        // 如果是数组, 逐层找偏移量
        Initialization initialization;
        if (symbol.isConstant()) {
            initialization = symbol.constInitialization();
        } else if (symbol.pointer() instanceof GlobalVar) {
            initialization = ((GlobalVar) symbol.pointer()).initialization();
        } else {
            throw new RuntimeException("Eval exception");
        }
//        ArrayList<IntConstant> indexEvals = new ArrayList<>(); // eval indexes
//        for (Exp index : lVal.getArrayDim()) {
//            indexEvals.add((IntConstant) eval(index));
//        }
//        for (IntConstant index : indexEvals) {
//            if (initialization instanceof VarInitialization) {
//                return symbol.getType() == LlvmType.I32_TYPE ?
//                        CONST_INT_ZERO : ConstantFloat.CONST_FLOAT_ZERO;
//            } else if (initialization instanceof ArrayInitialization) {
//                throw new RuntimeException("Array eval not implemented");
//                initialization = ((ArrayInitialization) initialization).get((int) index.getVal());
//            } else {
//                throw new RuntimeException("Array should be initialized correctly");
//            }
//        }
        Value ret = ((VarInitialization) initialization).initVal(); // 取出初始值
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
