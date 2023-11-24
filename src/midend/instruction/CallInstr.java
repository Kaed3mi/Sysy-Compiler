package midend.instruction;

import backend.MipsBuilder;
import backend.mipsinstr.*;
import backend.operand.Addr;
import backend.operand.Immediate;
import backend.operand.Operand;
import backend.operand.Reg;
import midend.BasicBlock;
import midend.constant.IntConstant;
import midend.function.ExternFunc;
import midend.function.Function;
import midend.llvm_type.LLvmType;
import midend.value.Value;

import java.util.ArrayList;
import java.util.StringJoiner;

public class CallInstr extends Instr {
    private final Function function;
    private final ArrayList<Value> functionRParams; //实参

    public CallInstr(Function function, ArrayList<Value> functionRParams, BasicBlock parentBB) {
        super(function.lLvmType(), InstrOp.CALL, parentBB);
        addUse(this.function = function);
        (this.functionRParams = functionRParams).forEach(this::addUse);
    }

    private String getRParamsString() {
        StringJoiner stringJoiner = new StringJoiner(", ");
        functionRParams.forEach(fParam ->
                stringJoiner.add(String.format(
                        "%s %s", fParam.lLvmType(), fParam.lLvmIdent())));
        return stringJoiner.toString();
    }

    @Override
    public String toString() {
        return String.format(
                "%s%s %s %s(%s)",
                lLvmType == LLvmType.VOID_TYPE ? "" : String.format("%s = ", lLvmIdent),
                instrOp, lLvmType,
                function.lLvmIdent(),
                getRParamsString()
        );
    }

    @Override
    public void generateMips() {
        if (function instanceof ExternFunc) {
            if (function == ExternFunc.PUT_CH) {
                Immediate imm = new Immediate((IntConstant) functionRParams.get(0));
                MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.a0, imm));
                MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v0, Syscall.PUT_CH));
                MipsBuilder.addMipsInstr(new Syscall());
            } else if (function == ExternFunc.PUT_INT) {
                Value value = functionRParams.get(0);
                Operand output = MipsBuilder.applyOperand(value, true);
                if (output instanceof Immediate imm) {
                    MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.a0, imm));
                } else if (output instanceof Reg rs) {
                    MipsBuilder.addMipsInstr(new RInstr(RInstr.RType.or, Reg.a0, rs, Reg.zero));
                }
                MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v0, Syscall.PUT_INT));
                MipsBuilder.addMipsInstr(new Syscall());
            } else if (function == ExternFunc.GET_INT) {
                MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v0, Syscall.GET_INT));
                MipsBuilder.addMipsInstr(new Syscall());
                Operand rt = MipsBuilder.applyOperand(this, false);
                MipsBuilder.addMipsInstr(new RInstr(RInstr.RType.or, rt, Reg.v0, Reg.zero));
            } else {
                throw new RuntimeException();
            }
        } else if (function instanceof Function) {
            MipsBuilder.saveAllReg();
            // 1-返回地址存入$ra
            MipsBuilder.addMipsInstr(new MemInstr(MemInstr.MemType.sw, Reg.ra, new Addr(new Immediate(0), Reg.sp)));
            // 2-将栈指针$sp寄存器减去一定的值，以便为函数调用分配栈空间。
            MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.addiu, Reg.v0, Reg.sp, MipsBuilder.getStackFrameSize().neg()));
            for (int i = 0; i < functionRParams.size(); i++) {
                // C语言：pass by value
                Value value = functionRParams.get(i);
                if (i < 3) {
                    // 存入寄存器
                    Reg a_i = Reg.values()[Reg.a1.ordinal() + i];
                    if (value instanceof IntConstant) {
                        Immediate imm = new Immediate((IntConstant) value);
                        MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, a_i, imm));
                    } else {
                        Operand rs = MipsBuilder.applyOperand(value, true);
                        MipsBuilder.addMipsInstr(new RInstr(RInstr.RType.or, a_i, rs, Reg.zero));
                    }
                } else {
                    // 存入栈
                    Immediate offset = new Immediate(-(i + 1) * 4);
                    if (value instanceof IntConstant) {
                        Immediate imm = new Immediate((IntConstant) value);
                        MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v1, imm));
                        MipsBuilder.addMipsInstr(new MemInstr(MemInstr.MemType.sw, Reg.v1, new Addr(offset, Reg.v0)));
                    } else {
                        Operand rs = MipsBuilder.applyOperand(value, true);
                        MipsBuilder.addMipsInstr(new MemInstr(MemInstr.MemType.sw, rs, new Addr(offset, Reg.v0)));
                    }
                }
            }
            // 还原sp
            MipsBuilder.addMipsInstr(new RInstr(RInstr.RType.or, Reg.sp, Reg.v0, Reg.zero));
            // 清空寄存器
            MipsBuilder.clearReg();
            MipsBuilder.addMipsInstr(new JInstr(JInstr.JType.jal, MipsBuilder.getLabel(function.getEntryBlock())));
            MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.addiu, Reg.sp, Reg.sp, MipsBuilder.getStackFrameSize()));
            MipsBuilder.addMipsInstr(new MemInstr(MemInstr.MemType.lw, Reg.ra, new Addr(Immediate.ZERO, Reg.sp)));
            // 返回之后需要读取返回值：
            if (lLvmType != LLvmType.VOID_TYPE) {
                Operand retReg = MipsBuilder.applyOperand(this, false);
                MipsBuilder.addMipsInstr(new RInstr(RInstr.RType.or, retReg, Reg.v0, Reg.zero));
            }
        } else {
            throw new RuntimeException();
        }
    }
}
