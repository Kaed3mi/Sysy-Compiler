package frontend.lexical;

public class Ident extends Token {

    public Ident(Token token) {
        super(token.getLexeme(), token.getContent(), token.getLineNum());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ident ident = (Ident) o;

        return getContent().equals(ident.getContent());
    }

    @Override
    public int hashCode() {
        return getContent().hashCode();
    }

    public int getLineNum() {
        return super.getLineNum();
    }

    @Override
    public String toString() {
        return getContent();
    }
}
