package midend.function;

import backend.GenerateMips;
import exceptions.CompileError;
import exceptions.ErrorBuilder;
import exceptions.ErrorType;
import frontend.lexical.Ident;
import midend.LLvmBuilder;

import java.util.HashSet;
import java.util.LinkedHashMap;

public class FuncTable implements GenerateMips {
    private final LinkedHashMap<String, Function> functionTable;
    private final HashSet<Ident> brokenFunctionTable;

    public FuncTable() {
        this.functionTable = new LinkedHashMap<>();
        this.brokenFunctionTable = new HashSet<>();
        init();
        LLvmBuilder.setFuncTable(this);
    }

    private void init() {
        append(ExternFunc.GET_INT);
        append(ExternFunc.PRINTF);
        append(ExternFunc.PUT_INT);
        append(ExternFunc.PUT_CH);
    }

    public void append(Function function) {
        functionTable.put(function.lLvmIdent().name(), function);
    }

    public boolean isDuplicated(Ident ident) {
        return functionTable.containsKey(ident.toString());
    }

    public Function get(Ident ident) {
        if (!functionTable.containsKey(ident.toString()) || brokenFunctionTable.contains(ident)) {
            ErrorBuilder.appendError(new CompileError(ident.getLineNum(), ErrorType.UNDEFINED_IDENT, "没有定义函数"));
            return null;
        }
        return functionTable.get(ident.toString());
    }

    public void addBrokenFunction(Ident ident) {
        brokenFunctionTable.add(ident);
    }

    public boolean isBroken(Ident ident) {
        return brokenFunctionTable.contains(ident);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        functionTable.values().stream().filter(e -> e instanceof ExternFunc && !e.lLvmIdent().name().equals("@printf")).
                forEach(e -> sb.append(e).append('\n'));
        sb.append('\n');
        functionTable.values().stream().filter(e -> !(e instanceof ExternFunc)).
                forEach(e -> sb.append(e).append('\n'));
        return sb.toString();
    }

    @Override
    public void generateMips() {
        functionTable.values().stream().filter(e -> !(e instanceof ExternFunc)).forEach(Function::generateMips);
    }
}
