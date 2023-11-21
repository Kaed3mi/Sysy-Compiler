package midend;

import midend.function.FuncTable;

import java.util.ArrayList;

public class LLvmBuilder {
    private static final ArrayList<GlobalVar> globalVarTable = new ArrayList<>();
    private static FuncTable funcTable;

    public static void addGlobalVar(GlobalVar globalVar) {
        globalVarTable.add(globalVar);
    }

    public static void setFuncTable(FuncTable funcTable) {
        LLvmBuilder.funcTable = funcTable;
    }

    public static String LLvmOutput() {
        StringBuilder sb = new StringBuilder();
        globalVarTable.forEach(e -> sb.append(e).append('\n'));
        sb.append(funcTable);
        return sb.toString();
    }


}
