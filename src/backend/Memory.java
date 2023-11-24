package backend;

import backend.data.Data;
import backend.operand.Immediate;
import midend.GlobalVar;

import java.util.LinkedHashMap;
import java.util.function.Consumer;

public class Memory {

    private final LinkedHashMap<GlobalVar, Data> dataMap = new LinkedHashMap<>();


    public void add(Data data) {
        dataMap.put(data.getGlobalVar(), data);
    }

    public Immediate getOffset(GlobalVar globalVar) {
        return new Immediate(dataMap.get(globalVar).getAddress());
    }

    public Data getData(GlobalVar globalVar) {
        return dataMap.get(globalVar);
    }


    public void forEach(Consumer<? super Data> action) {
        dataMap.values().forEach(action);
    }

}
