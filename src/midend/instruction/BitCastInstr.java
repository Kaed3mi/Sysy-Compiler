package midend.instruction;

import backend.MipsBuilder;
import backend.mipsinstr.IInstr;
import backend.mipsinstr.RInstr;
import backend.operand.Operand;
import backend.operand.Reg;
import midend.BasicBlock;
import midend.GlobalVar;
import midend.llvm_type.LLvmType;
import midend.llvm_type.PointerType;
import midend.value.Value;

/**
 * 类型转换
 */
public class BitCastInstr extends Instr {
    private final Value srcValue;

    public BitCastInstr(Value srcValue, LLvmType dstType, BasicBlock parentBB) {
        super(dstType, InstrOp.BITCAST, parentBB);
        addUse(this.srcValue = srcValue);
    }

    @Override
    public String toString() {
        return String.format(
                "%s = %s %s %s to %s",
                lLvmIdent, instrOp, srcValue.lLvmType(),
                srcValue.lLvmIdent(),
                lLvmType);
    }

    @Override
    public void generateMips() {
        Operand rt = MipsBuilder.applyOperand(this, false);
        if (srcValue instanceof GlobalVar) {
            // 全局变量
            MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.addiu, rt, Reg.gp, MipsBuilder.getGpOffset(srcValue)));
        } else if (srcValue instanceof AllocaInstr) {
            // 局部变量
            MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.addiu, rt, Reg.sp, MipsBuilder.getSpOffset(srcValue)));
        } else if (srcValue.lLvmType() instanceof PointerType) {
            // 指针
            Operand rs = MipsBuilder.applyOperand(srcValue, true);
            MipsBuilder.addMipsInstr(new RInstr(RInstr.RType.or, rt, rs, Reg.zero));
        } else {
            throw new RuntimeException();
        }
    }
}
