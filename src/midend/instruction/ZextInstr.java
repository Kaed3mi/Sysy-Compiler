package midend.instruction;

import midend.BasicBlock;
import midend.llvm_type.LLvmType;
import midend.value.Value;

public class ZextInstr extends Instr {
    private final Value srcValue;

    public ZextInstr(Value srcValue, LLvmType toLLvmType, BasicBlock parentBB) {
        super(toLLvmType, InstrOp.ZEXT, parentBB);
        addUse(this.srcValue = srcValue);
    }

    @Override
    public String toString() {
        return String.format(
                "%s = %s %s %s to %s",
                lLvmIdent, instrOp, srcValue.lLvmType(), srcValue.lLvmIdent(), lLvmType
        );
    }

}
