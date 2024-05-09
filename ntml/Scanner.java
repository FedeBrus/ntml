package ntml;

import java.util.ArrayList;
import java.util.List;

import static ntml.TokenType.*;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }
    
    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }
    
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!': addToken(BANG); break;
            case '>': addToken(ANGLE_RIGHT); break;
            case '<': addToken(ANGLE_LEFT); break;
            case ':': addToken(COLON); break;
            case '_': addToken(UNDERSCORE); break; 
            
            case '-':
                addToken(match('-') ? DOUBLE_HYPHEN : HYPHEN);
                break;
            
            case '\\':
                addToken(match('\\') ? DOUBLE_BACKSLASH : BACKSLASH);
                break;
            
            case '|':
                if (match('|')) {
                    addToken(DOUBLE_PIPE);
                } else if (match('-')) {
                    addToken(PIPE_HYPHEN);
                } else {
                    addToken(PIPE);
                }
                break;

            case '(':
                addToken(match('(') ? DOUBLE_PAREN_LEFT : PAREN_LEFT);
                break;
                
            case ')':
                addToken(match(')') ? DOUBLE_PAREN_RIGHT : PAREN_RIGHT);
                break;
                
            case '[':
                addToken(match('[') ? DOUBLE_BRACKET_LEFT : BRACKET_LEFT);
                break;
                
            case ']':
                addToken(match(']') ? DOUBLE_BRACKET_RIGHT : BRACKET_RIGHT);
                break;
                 
            case '{':
                addToken(BRACE_LEFT);
                break;
                
            case '}':
                addToken(BRACE_RIGHT);
                break;
                
            case '#':
                if (match('#')) {
                    addToken(match('#') ? TRIPLE_HASH : DOUBLE_HASH);
                } else {
                    addToken(HASH);
                }
                break;
            
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
                break;

            case ' ':
            case '\r':
            case '\t':
                break;

            case '\n':
                line++;
                break;

            case '$': math(); break;
            case '"': string(); break;
            case '§': code(); break;
                
            default:
                Ntml.error(line, "Unexpected character.");
                break;
        }
    }
    
    private void math() {
        while (peek() != '$' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Ntml.error(line, "Unterminated math.");
            return;
        }

        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(MATH, value);
    }
    
    private void code() {
        while (peek() != '§' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Ntml.error(line, "Unterminated code.");
            return;
        }

        advance();

        String value = source.substring(start + 1, current - 1);
        value = value.replace("<", "&lt");
        value = value.replace(">", "&gt");
        addToken(CODE, value);
    }
    
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }
            
            if (peek() == '\\') {
                advance();
            }
            
            advance();
        }

        if (isAtEnd()) {
            Ntml.error(line, "Unterminated string.");
            return;
        }

        advance();

        String value = source.substring(start + 1, current - 1);
        value = value.replace("<", "&lt");
        value = value.replace(">", "&gt");
        value = escape(value);
        
        addToken(STRING, value);
    }
    
    static String escape(String value) {
        value = value.replace("\\\"", "\"");
        return value;
    }
    
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }
    
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }
    
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z');
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
    
    private boolean isPunctuation(char c) {
        return c == '.' || c == ',';
    }
    
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    } 
    
    private boolean isAtEnd() {
        return current >= source.length();
    }
  
    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, String literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}

