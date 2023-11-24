package backend;

import backend.operand.Immediate;
import midend.instruction.AllocaInstr;
import midend.value.Value;

import java.util.HashMap;
import java.util.HashSet;

public class StackFrame {
    private final HashSet<Value> values;
    private final HashMap<Value, Integer> sizes;
    private final HashMap<Value, Integer> offsets;
    private int totalSize;

    public StackFrame() {
        this.values = new HashSet<>();
        this.sizes = new HashMap<>();
        this.offsets = new HashMap<>();
        this.totalSize = 4;
    }

    public boolean contains(Value value) {
        return values.contains(value);
    }

    public void push(Value value) {
        int size = value.lLvmType().size();
        values.add(value);
        sizes.put(value, size);
        offsets.put(value, totalSize);
        this.totalSize += size;
    }

    public void alloc(AllocaInstr allocaInstr) {
        int size = allocaInstr.pointeeType().size();
        values.add(allocaInstr);
        sizes.put(allocaInstr, size);
        offsets.put(allocaInstr, totalSize + size - 4);
        this.totalSize += size;
    }

    public Immediate getOffset(Value value) {
        return new Immediate(-offsets.get(value));
    }

    public int getStackFrameSize() {
        return totalSize;
    }
}
