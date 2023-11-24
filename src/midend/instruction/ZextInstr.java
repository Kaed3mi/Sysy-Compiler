package midend.instruction;

import backend.MipsBuilder;
import backend.mipsinstr.IInstr;
import backend.mipsinstr.SInstr;
import backend.operand.Immediate;
import backend.operand.Operand;
import backend.operand.Reg;
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

    @Override
    public void generateMips() {
        Operand rt = MipsBuilder.applyOperand(this, false);
        Operand rs = MipsBuilder.applyOperand(srcValue, true);
        if (rs instanceof Immediate imm) {
            MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, rt, new Immediate(imm.getVal() == 0 ? 0 : 1)));
        } else if (rs instanceof Reg) {
            MipsBuilder.addMipsInstr(new SInstr(SInstr.SType.sne, rt, rs, Reg.zero));
        }
    }
    
}
