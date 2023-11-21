package midend.instruction;

import midend.BasicBlock;
import midend.llvm_type.LLvmType;
import midend.value.Value;

public class ReturnInstr extends Instr {

    private final Value returnValue;

    public ReturnInstr(Value returnValue, BasicBlock parentBB) {
        super(returnValue == null ? LLvmType.VOID_TYPE : returnValue.lLvmType()
                , InstrOp.RET, parentBB);
        if (returnValue != null) {
            addUse(this.returnValue = returnValue);
        } else {
            this.returnValue = null;
        }
    }

    @Override
    public String toString() {
        if (this.returnValue == null) {
            return String.format("%s %s", instrOp, LLvmType.VOID_TYPE);
        } else {
            return String.format("%s %s %s", instrOp, lLvmType, returnValue.lLvmIdent());
        }
    }
}
