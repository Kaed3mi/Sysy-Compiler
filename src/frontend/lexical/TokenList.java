package frontend.lexical;

import java.util.ArrayList;

public class TokenList {
    private final ArrayList<Token> tokenList;

    public TokenList() {
        tokenList = new ArrayList<>();
    }

    public void append(Token token) {
        tokenList.add(token);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        tokenList.stream().filter(token -> !token.isComment()).forEach(token -> sb.append(token).append('\n'));
        return sb.toString();
    }
}
