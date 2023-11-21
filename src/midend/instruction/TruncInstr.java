package midend.instruction;

import midend.BasicBlock;
import midend.llvm_type.LLvmType;
import midend.value.Value;

public class TruncInstr extends Instr {

    private final Value srcValue;

    public TruncInstr(Value srcValue, LLvmType toLLvmType, BasicBlock parentBB) {
        super(toLLvmType, InstrOp.TRUNC, parentBB);
        addUse(this.srcValue = srcValue);
    }

    @Override
    public String toString() {
        return String.format(
                "%s %s %s to %s",
                instrOp, srcValue.lLvmType(), srcValue.lLvmIdent(), lLvmType
        );
    }
}
