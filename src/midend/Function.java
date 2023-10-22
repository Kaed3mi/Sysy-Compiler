package midend;

import exceptions.CompileError;
import exceptions.ErrorBuilder;
import exceptions.ErrorType;
import frontend.lexical.Ident;
import frontend.syntax.ast.function.FuncCall;
import frontend.syntax.ast.function.FuncDef;
import frontend.syntax.ast.function.FuncType;
import midend.value.Value;

import java.util.ArrayList;

public class Function {
    private final Ident ident;
    private final FuncDef funcDef;
    private final FuncType funcType;
    private final ArrayList<Value> params;

    public Function(FuncDef funcDef) {
        this.ident = funcDef.getIdent();
        this.funcDef = funcDef;
        this.funcType = funcDef.getFuncType();
        this.params = new ArrayList<>();
    }

    public Ident getIdent() {
        return ident;
    }

    public FuncDef getFuncDef() {
        return funcDef;
    }

    public void checkIllegal(FuncCall funcCall) {
        if (funcCall.getFuncRParams() == null || funcDef.getFuncFParams() == null) {
            if (!(funcCall.getFuncRParams() == null && funcDef.getFuncFParams() == null)) {
                ErrorBuilder.appendError(new CompileError(funcCall.getIdent().getLineNum(),
                        ErrorType.WRONG_ARGUMENTS_AMOUNT, "函数调用参数数量错误"));
            }
            return;
        }
        if (funcCall.getFuncRParams().getExps().size() != funcDef.getFuncFParams().getFuncFParams().size()) {
            ErrorBuilder.appendError(new CompileError(funcCall.getIdent().getLineNum(),
                    ErrorType.WRONG_ARGUMENTS_AMOUNT, "函数调用参数数量错误"));
        }
    }

    public FuncType getFuncType() {
        return funcType;
    }

    public ArrayList<Value> getParams() {
        return params;
    }

    public void addParam(Value param) {
        params.add(param);
    }

    public boolean hasReturnVal() {
        return funcType.returnsInt();
    }
}
