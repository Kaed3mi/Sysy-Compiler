package midend.instruction;

import backend.MipsBuilder;
import backend.mipsinstr.IInstr;
import backend.mipsinstr.JInstr;
import backend.mipsinstr.RInstr;
import backend.mipsinstr.Syscall;
import backend.operand.Immediate;
import backend.operand.Operand;
import backend.operand.Reg;
import midend.BasicBlock;
import midend.constant.IntConstant;
import midend.llvm_type.LLvmType;
import midend.value.Value;

import java.util.Objects;

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

    @Override
    public void generateMips() {
        MipsBuilder.saveAllReg();
        if (Objects.equals(parentBB.getParentFunc().lLvmIdent().name(), "main")) {
            // 直接返回
            MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v0, Syscall.EXIT));
            MipsBuilder.addMipsInstr(new Syscall());
            return;
        }
        if (returnValue != null) {
            if (returnValue instanceof IntConstant) {
                // 返回立即数
                MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v0, new Immediate((IntConstant) returnValue)));
            } else {
                Operand reg = MipsBuilder.applyOperand(returnValue, false);
                MipsBuilder.addMipsInstr(new RInstr(RInstr.RType.or, Reg.v0, reg, Reg.zero));
            }
        }
        MipsBuilder.clearReg();
        MipsBuilder.addMipsInstr(new JInstr(JInstr.JType.jr, Reg.ra));
    }

}
