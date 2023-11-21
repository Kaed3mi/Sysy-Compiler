package midend.instruction;

import midend.BasicBlock;
import midend.function.Function;
import midend.llvm_type.LLvmType;
import midend.value.Value;

import java.util.ArrayList;
import java.util.StringJoiner;

public class CallInstr extends Instr {
    private final Function function;
    private ArrayList<Value> functionRParams; //实参

    public CallInstr(Function function, ArrayList<Value> functionRParams, BasicBlock parentBB) {
        super(function.lLvmType(), InstrOp.CALL, parentBB);
        addUse(this.function = function);
        (this.functionRParams = functionRParams).forEach(this::addUse);
    }

    private String getRParamsString() {
        StringJoiner stringJoiner = new StringJoiner(", ");
        functionRParams.forEach(fParam ->
                stringJoiner.add(String.format(
                        "%s %s", fParam.lLvmType(), fParam.lLvmIdent())));
        return stringJoiner.toString();
    }

    @Override
    public String toString() {
        return String.format(
                "%s%s %s %s(%s)",
                lLvmType == LLvmType.VOID_TYPE ? "" : String.format("%s = ", lLvmIdent),
                instrOp, lLvmType,
                function.lLvmIdent(),
                getRParamsString()
        );
    }

}
