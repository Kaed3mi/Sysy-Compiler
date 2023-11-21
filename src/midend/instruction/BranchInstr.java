package midend.instruction;

import midend.BasicBlock;
import midend.llvm_type.LLvmType;
import midend.value.Value;

public class BranchInstr extends Instr {
    private final Value condVal;
    private final BasicBlock thenBlock;
    private final BasicBlock elseBlock;

    public BranchInstr(Value condVal, BasicBlock thenBlock, BasicBlock elseBlock, BasicBlock parentBB) {
        super(LLvmType.VOID_TYPE, InstrOp.BR, parentBB);
        addUse(this.condVal = condVal);
        addUse(this.thenBlock = thenBlock);
        addUse(this.elseBlock = elseBlock);
    }

    public String toString() {
        return String.format(
                "%s %s %s, %s, %s",
                instrOp,
                condVal.lLvmType(),
                condVal.lLvmIdent(),
                thenBlock.label(),
                elseBlock.label()
        );
    }

}
