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
            Operand rt = MipsBuilder.applyOperand(returnValue, true);
            if (rt instanceof Immediate imme) {
                // 返回立即数
                MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v0, imme));
            } else if (rt instanceof Reg) {
                MipsBuilder.addMipsInstr(new RInstr(RInstr.RType.or, Reg.v0, rt, Reg.zero));
            }
        }
        MipsBuilder.clearReg();
        MipsBuilder.addMipsInstr(new JInstr(JInstr.JType.jr, Reg.ra));
    }

}
