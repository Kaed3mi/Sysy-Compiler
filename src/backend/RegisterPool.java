package backend;

import backend.operand.Reg;
import midend.value.Value;

import java.util.*;

import static backend.operand.Reg.*;

public class RegisterPool {
    private static final ArrayList<Reg> registerPool = new ArrayList<>(List.of(
            t0, t1, t2, t3, t4, t5, t6, t7,
            s0, s1, s2, s3, s4, s5, s6, s7,
            t8, t9,
            k0, k1
    ));

    private final HashSet<Reg> availableRegs;
    private final HashMap<Value, Reg> valueToReg;
    private final HashMap<Reg, Value> regToValue;
    private final LinkedList<Reg> usingReg;

    // 用来存储函数参数的寄存器
    private final HashMap<Reg, Value> regToValue_param;
    private final HashMap<Value, Reg> valueToReg_param;


    public RegisterPool() {
        availableRegs = new HashSet<>(registerPool);
        valueToReg = new HashMap<>();
        regToValue = new HashMap<>();
        usingReg = new LinkedList<>();
        regToValue_param = new HashMap<>();
        valueToReg_param = new HashMap<>();
    }

    public boolean regsUsingValue(Value value) {
        return valueToReg.containsKey(value) || valueToReg_param.containsKey(value);
    }

    public Reg get(Value value) {
        if (valueToReg_param.containsKey(value)) {
            return valueToReg_param.get(value);
        } else {
            return valueToReg.get(value);
        }
    }

    public void setParamRegs(Value... values) {
        if (values.length > 3) {
            throw new RuntimeException();
        }
        regToValue_param.clear();
        valueToReg_param.clear();
        for (int i = 0; i < values.length; i++) {
            Reg reg = Reg.values()[a1.ordinal() + i];
            Value value = values[i];
            regToValue_param.put(reg, value);
            valueToReg_param.put(value, reg);
        }
    }

    public void saveAllReg() {
        valueToReg.forEach(MipsBuilder::store);
    }

    public void clearReg() {
        availableRegs.clear();
        valueToReg.clear();
        regToValue.clear();
        usingReg.clear();

        availableRegs.addAll(registerPool);
    }

    public Reg alloc(Value value) {
        if (availableRegs.isEmpty()) {
            Reg recycleReg = usingReg.removeFirst();
            Value yieldValue = regToValue.get(recycleReg);
            // 回收
            valueToReg.remove(yieldValue);
            regToValue.remove(recycleReg);
            availableRegs.add(recycleReg);
            // 将yieldValue存入内存
            MipsBuilder.store(yieldValue, recycleReg);
        }
        Reg reg = pickReg();
        // 回收
        availableRegs.remove(reg);
        valueToReg.put(value, reg);
        regToValue.put(reg, value);
        usingReg.add(reg);

        return reg;
    }

    private Reg pickReg() {
        for (Reg reg : registerPool) {
            if (availableRegs.contains(reg)) {
                return reg;
            }
        }
        throw new RuntimeException();
    }
}
