package midend.instruction;

import backend.MipsBuilder;
import backend.mipsinstr.IInstr;
import backend.mipsinstr.MDInstr;
import backend.mipsinstr.RInstr;
import backend.operand.Immediate;
import backend.operand.Operand;
import backend.operand.Reg;
import midend.BasicBlock;
import midend.constant.Constant;
import midend.llvm_type.LLvmType;
import midend.value.Value;

public class AluInstr extends Instr {
    private final Value operand0;
    private final Value operand1;

    public AluInstr(LLvmType llvmType, InstrOp instrOp, Value operand0, Value operand1, BasicBlock parentBB) {
        super(llvmType, instrOp, parentBB);
        addUse(this.operand0 = operand0);
        addUse(this.operand1 = operand1);
    }

    @Override
    public String toString() {
        return String.format("%s = %s %s %s, %s",
                lLvmIdent, instrOp, lLvmType,
                operand0.lLvmIdent(),
                operand1.lLvmIdent()
        );
    }

    @Override
    public void generateMips() {
        Operand op0 = MipsBuilder.applyOperand(operand0, true);
        Operand op1 = MipsBuilder.applyOperand(operand1, true);
        Operand rt = MipsBuilder.applyOperand(this, false);
        if (op0 instanceof Reg rs && op1 instanceof Reg rd) {
            // r指令
            switch (instrOp) {
                case ADD -> MipsBuilder.addMipsInstr(new RInstr(RInstr.RType.addu, rt, rs, rd));
                case SUB -> MipsBuilder.addMipsInstr(new RInstr(RInstr.RType.subu, rt, rs, rd));
                case MUL -> {
                    MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.multu, rs, rd));
                    MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.mflo, rt));
                }
                case DIV -> {
                    MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.div, rs, rd));
                    MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.mflo, rt));
                }
                case REM -> {
                    MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.div, rs, rd));
                    MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.mfhi, rt));
                }
            }
        } else if (op0 instanceof Reg rs && op1 instanceof Immediate imm) {
            switch (instrOp) {
                case ADD -> MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.addiu, rt, rs, imm));
                case SUB -> MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.addiu, rt, rs, imm.neg()));
                case MUL -> {
                    MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v0, imm));
                    MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.multu, rs, Reg.v0));
                    MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.mflo, rt));
                }
                case DIV -> {
                    MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v0, imm));
                    MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.div, rs, Reg.v0));
                    MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.mflo, rt));
                }
                case REM -> {
                    MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v0, imm));
                    MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.div, rs, Reg.v0));
                    MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.mfhi, rt));
                }
            }
        } else if (op0 instanceof Immediate imm && op1 instanceof Reg rd) {
            switch (instrOp) {
                case ADD -> MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.addiu, rt, rd, imm));
                case SUB -> {
                    MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v0, imm));
                    MipsBuilder.addMipsInstr(new RInstr(RInstr.RType.subu, rt, Reg.v0, rd));
                }
                case MUL -> {
                    MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v0, imm));
                    MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.multu, rd, Reg.v0));
                    MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.mflo, rt));
                }
                case DIV -> {
                    MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v0, imm));
                    MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.div, Reg.v0, rd));
                    MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.mflo, rt));
                }
                case REM -> {
                    MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v0, imm));
                    MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.div, Reg.v0, rd));
                    MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.mfhi, rt));
                }
            }
        } else if (op0 instanceof Immediate && op1 instanceof Immediate) {
            int rs = ((Constant) operand0).getVal();
            int rd = ((Constant) operand1).getVal();
            Immediate imm = switch (instrOp) {
                case ADD -> new Immediate(rs + rd);
                case SUB -> new Immediate(rs - rd);
                case MUL -> new Immediate(rs * rd);
                case DIV -> new Immediate(rs / rd);
                case REM -> new Immediate(rs % rd);
                default -> throw new RuntimeException();
            };
            MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, rt, imm));
        }
    }
}
