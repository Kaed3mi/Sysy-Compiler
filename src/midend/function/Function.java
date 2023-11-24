package midend.function;

import backend.GenerateMips;
import backend.MipsBuilder;
import backend.operand.Label;
import midend.BasicBlock;
import midend.LLvmIdent;
import midend.llvm_type.LLvmType;
import midend.value.Value;

import java.util.ArrayList;
import java.util.LinkedList;

public class Function extends Value implements GenerateMips {
    protected final ArrayList<FunctionFParam> functionFParams;
    protected final LinkedList<BasicBlock> BBList;

    public Function(LLvmType retType, String funcName) {
        super(retType, LLvmIdent.FuncIdent(funcName));
        this.BBList = new LinkedList<>();
        this.functionFParams = new ArrayList<>();
    }

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

    public ArrayList<FunctionFParam> getFunctionFParams() {
        return functionFParams;
    }

    public BasicBlock getEntryBlock() {
        return BBList.getFirst();
    }

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
        BBList.forEach(e -> sb.append('b').append(e).append('\n'));
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}\n");
        return sb.toString();
    }

    @Override
    public void generateMips() {
        BBList.forEach(MipsBuilder::declareLabel);
        if (lLvmIdent.name().equals("main")) {
            MipsBuilder.setProgramEntry((Label) MipsBuilder.getLabel(getEntryBlock()));
        }
        MipsBuilder.newStackFrame();
        Value[] values = new Value[Math.min(3, functionFParams.size())];
        for (int i = 0; i < functionFParams.size(); i++) {
            if (i < 3) {
                values[i] = functionFParams.get(i);
            }
            MipsBuilder.stackFramePush(functionFParams.get(i));
        }
        MipsBuilder.setParamRegs(values);
        BBList.forEach(BasicBlock::generateMips);
    }
}
