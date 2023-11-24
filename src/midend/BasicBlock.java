package midend;

import backend.GenerateMips;
import backend.MipsBuilder;
import midend.constant.IntConstant;
import midend.function.Function;
import midend.instruction.Instr;
import midend.instruction.InstrOp;
import midend.instruction.ReturnInstr;
import midend.llvm_type.LLvmType;
import midend.value.Value;

import java.util.ArrayList;

public class BasicBlock extends Value implements GenerateMips {

    private final ArrayList<Instr> instrList;
    private boolean isTerminate;
    private final Function parentFunc;

    public BasicBlock(Function parentFunc) {
        super(LLvmType.BB_TYPE, LLvmIdent.BBIdent());
        this.instrList = new ArrayList<>();
        this.isTerminate = false;
        this.parentFunc = parentFunc;
    }

    public void addInstr(Instr instr) {
        if (!isTerminate) {
            instrList.add(instr);
            isTerminate = instr.getInstrOp() == InstrOp.BR || instr.getInstrOp() == InstrOp.RET;
        }
    }

    public void forceTerminate() {
        if (!isTerminate) {
            ReturnInstr returnInstr = new ReturnInstr(lLvmType == LLvmType.I32_TYPE ? IntConstant.ZERO : null, this);
            addInstr(returnInstr);
            isTerminate = true;
        }
    }

    public Function getParentFunc() {
        return parentFunc;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lLvmIdent.name()).append(":").append('\n');
        instrList.forEach(e -> sb.append('\t').append(e).append('\n'));
        return sb.toString();
    }

    public String label() {
        return "label " + lLvmIdent();
    }

    @Override
    public void generateMips() {
        MipsBuilder.addLabel(this);
        for (Instr instr : instrList) {
            instr.generateMips();
        }
        // instrList.forEach(Instr::generateMips);
    }
}
