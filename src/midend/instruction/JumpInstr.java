package midend.instruction;

import midend.BasicBlock;
import midend.llvm_type.LLvmType;

public class JumpInstr extends Instr {

    private final BasicBlock targetBlock;


    public JumpInstr(BasicBlock targetBlock, BasicBlock parentBB) {
        super(LLvmType.VOID_TYPE, InstrOp.BR, parentBB);
        addUse(this.targetBlock = targetBlock);
    }

    @Override
    public String toString() {
        return String.format(
                "%s %s",
                instrOp,
                targetBlock.label()
        );
    }
}
