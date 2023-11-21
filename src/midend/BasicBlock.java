package midend;

import midend.constant.IntConstant;
import midend.instruction.Instr;
import midend.instruction.InstrOp;
import midend.instruction.ReturnInstr;
import midend.llvm_type.LLvmType;
import midend.value.Value;

import java.util.ArrayList;

public class BasicBlock extends Value {

    private final ArrayList<Instr> instrList;
    private boolean isTerminate;

    public BasicBlock() {
        super(LLvmType.BB_TYPE, LLvmIdent.BBIdent());
        instrList = new ArrayList<>();
        isTerminate = false;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(lLvmIdent.toString().substring(1)).append(":").append('\n');
        instrList.forEach(e -> sb.append('\t').append(e).append('\n'));
        return sb.toString();
    }

    public String label() {
        return "label " + lLvmIdent();
    }

}
