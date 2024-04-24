package ntml;

class Token {
    final TokenType type;
    final String lexeme;
    final String literal;
    final int line;

    Token(TokenType type, String lexeme, String literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}