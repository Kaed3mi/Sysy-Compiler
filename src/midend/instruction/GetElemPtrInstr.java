package midend.instruction;

import midend.BasicBlock;
import midend.llvm_type.LLvmType;
import midend.llvm_type.PointerType;
import midend.value.Value;

import java.util.ArrayList;
import java.util.StringJoiner;

public class GetElemPtrInstr extends Instr {
    private final Value ptr;
    private ArrayList<Value> indexes;

    public GetElemPtrInstr(LLvmType pointeeType, Value ptr, ArrayList<Value> indexes, BasicBlock parentBB) {
        super(new PointerType(pointeeType), InstrOp.GETELEMPTR, parentBB);
        addUse(this.ptr = ptr);
        // indexes.add(0, IntConstant.ZERO);
        (this.indexes = indexes).forEach(this::addUse);
    }

    private String getIndexesString() {
        StringJoiner stringJoiner = new StringJoiner(", ");
        indexes.forEach(fParam ->
                stringJoiner.add(String.format(
                        "%s %s", fParam.lLvmType(), fParam.lLvmIdent())));
        return stringJoiner.toString();
    }

    @Override
    public String toString() {
        return String.format(
                "%s = %s %s, %s %s, %s",
                lLvmIdent, instrOp, ((PointerType) ptr.lLvmType()).pointeeType(),
                ptr.lLvmType(),
                ptr.lLvmIdent(),
                getIndexesString());
    }
}
