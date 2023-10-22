package frontend.semantic;

import exceptions.CompileError;
import exceptions.ErrorBuilder;
import exceptions.ErrorType;
import frontend.lexical.Ident;

import java.util.HashMap;

public class SymTable {

    private final HashMap<Ident, Symbol> symbolTable;

    private final SymTable parentTable;

    public SymTable(SymTable parentTable) {
        this.symbolTable = new HashMap<>();
        this.parentTable = parentTable;
    }

    public void append(Symbol symbol) throws Exception {
        if (symbolTable.containsKey(symbol.getIdent())) {
            return;
        }
        symbolTable.put(symbol.getIdent(), symbol);
    }

    public Symbol find(Ident ident) throws Exception {
        if (!symbolTable.containsKey(ident)) {
            if (parentTable == null) {
                ErrorBuilder.appendError(new CompileError(ident.getLineNum(), ErrorType.UNDEFINED_IDENT, "符号表查不到"));
                return null;
            } else {
                return parentTable.find(ident);
            }
        }
        return symbolTable.get(ident);
    }

    public SymTable getParentTable() {
        return parentTable;
    }

    public boolean isDuplicated(Ident ident) {
        return symbolTable.containsKey(ident);
    }
}
