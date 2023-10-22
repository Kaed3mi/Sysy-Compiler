package frontend.syntax.ast.function;

import java.util.ArrayList;

/**
 * 函数形参表
 * FuncFParams ::= FuncFParam { ',' FuncFParam }
 */
public class FuncFParams {
    private final ArrayList<FuncFParam> funcFParams;

    public FuncFParams(ArrayList<FuncFParam> funcFParams) {
        this.funcFParams = funcFParams;
    }

    public ArrayList<FuncFParam> getFuncFParams() {
        return funcFParams;
    }
}
