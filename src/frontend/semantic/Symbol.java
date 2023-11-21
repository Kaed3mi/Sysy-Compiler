package frontend.semantic;

import frontend.lexical.Ident;
import frontend.semantic.initialization.Initialization;
import midend.llvm_type.LLvmType;
import midend.value.Value;

public class Symbol {

    private final Ident ident;
    private final LLvmType lLvmType;
    private Initialization constInitialization;
    private final Value pointer;

    public Symbol(Ident ident, LLvmType lLvmType, Initialization constInitialization, Value pointer) {
        this.ident = ident;
        this.lLvmType = lLvmType;
        this.constInitialization = constInitialization;
        this.pointer = pointer;
    }

    public Ident getIdent() {
        return ident;
    }

    public boolean isConstant() {
        return constInitialization != null;
    }

//    public SymType getSymType() {
//        return symType;
//    }

//    public int getDimNum() {
//        return switch (symType) {
//            case VAR -> 0;
//            case DIM1 -> 1;
//            case DIM2 -> 2;
//            default -> throw new RuntimeException("没有维数可言");
//        };
//    }

    public Initialization constInitialization() {
        return constInitialization;
    }

    public Value pointer() {
        return pointer;
    }
}
