package midend.function;

import midend.BasicBlock;
import midend.LLvmIdent;
import midend.llvm_type.LLvmType;
import midend.value.Value;

import java.util.ArrayList;
import java.util.LinkedList;

public class Function extends Value {
    //    private Ident ident;
//    private FuncDef funcDef;
//    private FuncType funcType;
    protected final ArrayList<Value> params;
    protected final ArrayList<FunctionFParam> functionFParams;
    protected final LinkedList<BasicBlock> BBList;

//    public Function(FuncDef funcDef) {
//        super(funcDef.getFuncType().toLLvmType(), LLvmIdent.FuncIdent(funcDef.getIdent()));
//        this.ident = funcDef.getIdent();
//        this.funcDef = funcDef;
//        this.funcType = funcDef.getFuncType();
//        this.params = new ArrayList<>();
//        this.BBList = new LinkedList<>();
//        this.functionFParams = new ArrayList<>();
//    }

    public Function(LLvmType retType, String funcName, FunctionFParam... funcFParams) {
        super(retType, LLvmIdent.FuncIdent(funcName));
        this.params = new ArrayList<>();
        this.BBList = new LinkedList<>();
        this.functionFParams = new ArrayList<>();
        // this.functionFParams = new ArrayList<>(List.of(funcFParams));
    }

//    public Ident getIdent() {
//        return ident;
//    }

//    public FuncDef getFuncDef() {
//        return funcDef;
//    }

    public void addFunctionFParam(FunctionFParam functionFParam) {
        functionFParams.add(functionFParam);
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        BBList.addLast(basicBlock);
    }

//    public void checkIllegal(FuncCall funcCall) {
//        if (funcCall.getFuncRParams() == null || funcDef.getFuncFParams() == null) {
//            if (!(funcCall.getFuncRParams() == null && funcDef.getFuncFParams() == null)) {
//                ErrorBuilder.appendError(new CompileError(funcCall.getIdent().getLineNum(),
//                        ErrorType.WRONG_ARGUMENTS_AMOUNT, "函数调用参数数量错误"));
//            }
//            return;
//        }
//        if (funcCall.getFuncRParams().getExps().size() != funcDef.getFuncFParams().getFuncFParams().size()) {
//            ErrorBuilder.appendError(new CompileError(funcCall.getIdent().getLineNum(),
//                    ErrorType.WRONG_ARGUMENTS_AMOUNT, "函数调用参数数量错误"));
//        }
//    }

//    public FuncType getFuncType() {
//        return funcType;
//    }

    public ArrayList<Value> getParams() {
        return params;
    }

    public void addParam(Value param) {
        params.add(param);
    }

//    public boolean hasReturnVal() {
//        return funcType.returnsInt();
//    }

    private String functionFParasToString() {
        if (functionFParams.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        functionFParams.forEach(e -> sb.append(e).append(", "));
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "define dso_local %s %s(%s) {\n",
                lLvmType, lLvmIdent, functionFParasToString()));
        BBList.forEach(e -> sb.append(e).append('\n'));
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}\n");
        return sb.toString();
    }
}
