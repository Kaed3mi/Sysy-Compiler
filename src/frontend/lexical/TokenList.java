package frontend.lexical;

import frontend.syntax.SyntaxOutputBuilder;

import java.util.ArrayList;

public class TokenList {
    private final ArrayList<Token> tokenList;

    private int index;

    public TokenList() {
        tokenList = new ArrayList<>();
        index = 0;
    }

    /**
     * @return TokenList是否还有多余的Token
     */
    public boolean hasNext() {
        return index < tokenList.size();
    }

    /**
     * @return 当前的Token是否为FuncDef
     */
    public boolean lookingAtFuncDef() {
        return tokenList.get(index + 1).getLexeme().isOf(Lexeme.IDENFR, Lexeme.MAINTK) &&
                tokenList.get(index + 2).getLexeme().equals(Lexeme.LPARENT);
    }

    /**
     * @return 当前的Token是否为MainFuncDef
     */
    public boolean lookingAtMainFuncDef() {
        return tokenList.get(index + 1).getLexeme().isOf(Lexeme.MAINTK) &&
                tokenList.get(index + 2).getLexeme().equals(Lexeme.LPARENT);
    }

    /**
     * @return 当前的Token是否为FuncCall
     */
    public boolean lookingAtFuncCall() {
        return tokenList.get(index).getLexeme().isOf(Lexeme.IDENFR, Lexeme.PRINTFTK, Lexeme.GETINTTK) &&
                tokenList.get(index + 1).getLexeme().equals(Lexeme.LPARENT);
    }

    /**
     * @return 当前的Token，并且更新Token
     */
    public Token nextToken() {
        Token ret = tokenList.get(index);
        skip();
        return ret;
    }

    /**
     * @return 当前的Token
     */
    public Token lookingAt() {
        return tokenList.get(index);
    }

    /**
     * 跳过一个Token
     */
    public void skip() {
        SyntaxOutputBuilder.appendLine(tokenList.get(index).toString());
        index++;
    }

    public boolean lookingAtIsOf(Lexeme... lexemes) {
        return lookingAt().getLexeme().isOf(lexemes);
    }

    public boolean isAssignStmt() {
        for (int i = index; i < tokenList.size(); i++) {
            if (tokenList.get(i).getLexeme().isOf(Lexeme.ASSIGN)) {
                return true;
            } else if (tokenList.get(i).getLexeme().isOf(Lexeme.SEMICN)) {
                return false;
            }
        }
        return false;
    }

    /**
     * @param token 将一个Token加入TokenList当中
     */
    public void append(Token token) {
        // COMMENT直接在这里忽略
        if (!token.isComment()) {
            tokenList.add(token);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = index; i < tokenList.size(); i++) {
            sb.append(tokenList.get(i)).append('\n');
        }
        return sb.toString();
    }


}
