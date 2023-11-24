package backend.data;

import midend.GlobalVar;

import java.util.ArrayList;
import java.util.Objects;

public class WordData extends Data {

    private final String ident;
    private final ArrayList<Integer> words;

    public WordData(String ident, GlobalVar globalVar, ArrayList<Integer> words) {
        super(ident, globalVar, 4 * words.size());
        this.ident = ident;
        this.words = words;
    }

    public WordData(String ident, GlobalVar globalVar, Integer word) {
        super(ident, globalVar, 4);
        this.ident = ident;
        this.words = new ArrayList<>();
        words.add(word);
    }

    @Override
    public String toString() {
        return String.format(
                "%s: .word %s", ident, words.stream().map(Objects::toString)
                        .reduce((s1, s2) -> s1 + ", " + s2).orElse("")
        );
    }
}
