package ntml;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Ntml {
    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    
    public static void main(String[] args) throws IOException {
        String defaultOutputFilePath = "./output.html";
        String deafultStyleFilePath = "./style.css";
        
        if (args.length > 3 || args.length < 1) {
            System.out.println("Usage: ntml [script]");
            System.exit(64);
        } else if (args.length == 3) {
            runFile(args[0], args[1], args[2]);
        } else if (args.length == 2) {
            runFile(args[0], args[1], defaultOutputFilePath);
        } else if (args.length == 1) {
            runFile(args[0], deafultStyleFilePath, defaultOutputFilePath);
        }
    }
  
    private static void runFile(String path, String outputPath, String stylePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()), outputPath, stylePath);
        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }
    
    private static void run(String source, String stylePath, String outputPath) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        
        Parser parser = new Parser(tokens);
        List<Expr> expressions = parser.parse();
        if (hadError) return;
        
        Translator translator = new Translator(expressions);
        List<String> tags = translator.translate();
        
        Formatter formatter = new Formatter();
        formatter.write(tags, outputPath, stylePath);
    }
    
    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
  
    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }
}
