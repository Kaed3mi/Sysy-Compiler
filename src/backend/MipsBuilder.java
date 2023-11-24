package backend;

import backend.data.Data;
import backend.mipsinstr.JInstr;
import backend.mipsinstr.MemInstr;
import backend.mipsinstr.MipsInstr;
import backend.operand.*;
import midend.BasicBlock;
import midend.GlobalVar;
import midend.LLvmBuilder;
import midend.constant.Constant;
import midend.instruction.AllocaInstr;
import midend.value.Value;

import java.util.ArrayList;
import java.util.HashMap;

public class MipsBuilder {

    private final static Memory memory = new Memory();
    private final static ArrayList<MipsInstr> mipsInstrList = new ArrayList<>();
    private final static RegisterPool registerPool = new RegisterPool();
    private final static HashMap<BasicBlock, Label> labelMap = new HashMap<>();
    private static StackFrame stackFrame = new StackFrame();
    private static Label programEntry;

    public static void addData(Data data) {
        memory.add(data);
    }

    public static Data getData(GlobalVar globalVar) {
        return memory.getData(globalVar);
    }

    public static void addMipsInstr(MipsInstr mipsInstr) {
        mipsInstrList.add(mipsInstr);
    }

    public static Operand applyOperand(Value value, boolean read) {
        if (value instanceof Constant constant) {
            return new Immediate(constant);
        }
        if (registerPool.regsUsingValue(value)) {
            return registerPool.get(value);
        } else {
            Reg reg = registerPool.alloc(value);
            if (read) {
                load(value, reg);
            }
            return reg;
        }
    }

    public static boolean regsUsingValue(Value value) {
        return registerPool.regsUsingValue(value);
    }

    public static void store(Value value, Reg reg) {
        if (value instanceof GlobalVar) {
            MipsBuilder.addMipsInstr(new MemInstr(MemInstr.MemType.sw, reg, new Addr(memory.getOffset((GlobalVar) value), Reg.gp)));
        } else if (value instanceof AllocaInstr) {
            MipsBuilder.addMipsInstr(new MemInstr(MemInstr.MemType.sw, reg, new Addr(stackFrame.getOffset(value), Reg.sp)));
        } else {
            if (!stackFrame.contains(value)) {
                stackFrame.push(value);
            }
            MipsBuilder.addMipsInstr(new MemInstr(MemInstr.MemType.sw, reg, new Addr(stackFrame.getOffset(value), Reg.sp)));
        }
    }

    public static void load(Value value, Reg reg) {
        if (value instanceof GlobalVar) {
            MipsBuilder.addMipsInstr(new MemInstr(MemInstr.MemType.lw, reg, new Addr(memory.getOffset((GlobalVar) value), Reg.gp)));
        } else if (value instanceof AllocaInstr) {
            MipsBuilder.addMipsInstr(new MemInstr(MemInstr.MemType.lw, reg, new Addr(stackFrame.getOffset(value), Reg.sp)));
        } else {
            if (!stackFrame.contains(value)) {
                throw new RuntimeException("not found in stackFrame");
            }
            MipsBuilder.addMipsInstr(new MemInstr(MemInstr.MemType.lw, reg, new Addr(stackFrame.getOffset(value), Reg.sp)));
        }
    }

    public static Immediate getSpOffset(Value value) {
        return stackFrame.getOffset(value);
    }

    public static Immediate getGpOffset(Value value) {
        return memory.getOffset((GlobalVar) value);
    }

    public static void newStackFrame() {
        stackFrame = new StackFrame();
    }

    /**
     * 声明局部变量
     */
    public static void stackFrameAlloc(AllocaInstr allocaInstr) {
        stackFrame.alloc(allocaInstr);
    }

    public static void stackFramePush(Value value) {
        stackFrame.push(value);
    }

    public static Immediate getStackFrameSize() {
        return new Immediate(stackFrame.getStackFrameSize());
    }

    /**
     * 进行函数跳转时保留现场
     */
    public static void saveAllReg() {
        registerPool.saveAllReg();
    }

    public static void clearReg() {
        registerPool.clearReg();
    }

    // 需要提前声明，否则出现null
    public static void declareLabel(BasicBlock basicBlock) {
        Label label = new Label(basicBlock);
        labelMap.put(basicBlock, label);
    }

    public static void setParamRegs(Value... values) {
        registerPool.setParamRegs(values);
    }

    public static void addLabel(BasicBlock basicBlock) {
        if (!labelMap.containsKey(basicBlock)) {
            throw new RuntimeException();
        }
        addMipsInstr(labelMap.get(basicBlock));
    }

    public static Operand getLabel(BasicBlock basicBlock) {
        if (!labelMap.containsKey(basicBlock)) {
            throw new RuntimeException();
        }
        return labelMap.get(basicBlock);
    }

    public static void setProgramEntry(Label programEntry) {
        MipsBuilder.programEntry = programEntry;
    }

    public static String buildMips() {
        LLvmBuilder.generateMips();
        mipsInstrList.add(0, new JInstr(JInstr.JType.j, programEntry));
        StringBuilder sb = new StringBuilder();
        // 内存：
        sb.append(".data  0x10008000\n");
        memory.forEach(e -> sb.append('\t').append(e).append('\n'));
        // 指令：
        sb.append("\n.text\n");
        mipsInstrList.forEach(e -> {
            if (e instanceof Label) {
                sb.append(e).append(':').append('\n');
            } else {
                sb.append('\t').append(e).append('\n');
            }
        });
        sb.append('\n');
        return sb.toString();
    }
}
