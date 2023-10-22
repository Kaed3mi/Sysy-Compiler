package frontend.syntax.ast.function;

import exceptions.CompileError;
import exceptions.ErrorBuilder;
import exceptions.ErrorType;
import frontend.lexical.Ident;
import frontend.syntax.ast.CompUnit;
import frontend.syntax.ast.statement.Block;
import frontend.syntax.ast.statement.BlockItem;
import frontend.syntax.ast.statement.ReturnStmt;
import midend.llvm_type.BasicType;
import midend.llvm_type.LLvmType;

/**
 * FuncDef ::= FuncType Ident '(' [FuncFParams] ')' Block
 */
public class FuncDef implements CompUnit {
    private FuncType funcType;
    private Ident ident;
    private FuncFParams funcFParams;
    private Block block;

    public FuncDef(FuncType funcType, Ident ident, FuncFParams funcFParams, Block block) {
        this.funcType = funcType;
        this.ident = ident;
        this.funcFParams = funcFParams;
        this.block = block;
        checkIllegal();
    }

    public FuncType getFuncType() {
        return funcType;
    }

    public Ident getIdent() {
        return ident;
    }

    public FuncFParams getFuncFParams() {
        return funcFParams;
    }

    public Block getBlock() {
        return block;
    }

    public LLvmType toLLvmType() {
        if (funcType.returnsInt()) {
            return LLvmType.I32_TYPE;
        } else return BasicType.VOID_TYPE;
    }

    public void checkIllegal() {
        if (ident.getLineNum() < 0) {
            // 内置函数getint()和printf()
            return;
        }
        if (funcType.returnsInt()) {
            for (BlockItem blockItem : block.getBlockItems()) {
                if (blockItem instanceof ReturnStmt) {
                    // 不需要考虑需要返回值但是return;的情况
                    return;
                }
            }
            ErrorBuilder.appendError(new CompileError(block.getRbraceLineNum(), ErrorType.MISSING_RETURN, "缺少返回值"));
        } else {
            for (BlockItem blockItem : block.getBlockItems()) {
                if (blockItem instanceof ReturnStmt && ((ReturnStmt) blockItem).getExp() != null) {
                    ErrorBuilder.appendError(new CompileError(((ReturnStmt) blockItem).getLineNum(),
                            ErrorType.EXCESS_RETURN, "返回值多余"));
                }
            }
        }
    }
}
