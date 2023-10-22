package frontend.semantic;

import frontend.lexical.Ident;

public class Symbol {

    private final Ident ident;
    private final SymType symType;
    private final boolean isConstant;

    public Symbol(Ident ident, SymType symType, boolean isConstant) {
        this.ident = ident;
        this.symType = symType;
        this.isConstant = isConstant;
    }

    public Ident getIdent() {
        return ident;
    }

    public boolean isConstant() {
        return isConstant;
    }

    public SymType getSymType() {
        return symType;
    }

    public int getDimNum() throws Exception {
        return switch (symType) {
            case VAR -> 0;
            case DIM1 -> 1;
            case DIM2 -> 2;
            default -> throw new Exception("没有维数可言");
        };
    }
}
