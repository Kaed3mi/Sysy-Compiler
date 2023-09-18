import frontend.lexical.Lexer;
import frontend.lexical.TokenList;

import java.io.*;

public class Compiler {
    private static File inputFile;
    private static File outputFile;

    public static void main(String[] args) {
        try {
            fileProcess();
            TokenList tokenList = lexicalAnalysis(inputFile);
            buildLexical(tokenList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void fileProcess() {
        inputFile = new File("testfile.txt");
        outputFile = new File("output.txt");
    }

    private static TokenList lexicalAnalysis(File inputFile) throws Exception {
        StringBuilder sb = new StringBuilder();
        FileReader fileReader = new FileReader(inputFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append('\n');
        }
        bufferedReader.close();
        fileReader.close();
        return Lexer.lex(sb.toString());
    }

    private static void buildLexical(TokenList tokenList) throws IOException {
        FileWriter fileWriter = new FileWriter(outputFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(tokenList.toString());
        bufferedWriter.close();
        fileWriter.close();
    }

}