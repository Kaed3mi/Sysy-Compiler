package midend.instruction;

import backend.MipsBuilder;
import backend.mipsinstr.BInstr;
import backend.mipsinstr.JInstr;
import backend.operand.Immediate;
import backend.operand.Operand;
import backend.operand.Reg;
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

    @Override
    public void generateMips() {
        // 需要先save，用完寄存器在clear
        MipsBuilder.saveAllReg();
        Operand rs = MipsBuilder.applyOperand(condVal, true);
        if (rs instanceof Immediate imm) {
            MipsBuilder.clearReg();
            MipsBuilder.addMipsInstr(new JInstr(JInstr.JType.j, MipsBuilder.getLabel(imm.getVal() == 0 ? elseBlock : thenBlock)));
        } else {
            MipsBuilder.addMipsInstr(new BInstr(BInstr.BType.bne, rs, Reg.zero, MipsBuilder.getLabel(thenBlock)));
            MipsBuilder.clearReg();
            MipsBuilder.addMipsInstr(new JInstr(JInstr.JType.j, MipsBuilder.getLabel(elseBlock)));
        }

    }

}
