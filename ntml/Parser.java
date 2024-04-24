package ntml;

import java.util.ArrayList;
import java.util.List;

import static ntml.TokenType.*;

class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
  
    List<Expr> parse() {
        List<Expr> expressions = new ArrayList<>();
        while (!isAtEnd()) {
            expressions.add(expression());
        }

        return expressions;
    }
    
    private Expr expression() {
        try {
            if (check(STRING)) return new Expr.Text(text());
            if (check(CODE)) return new Expr.Code(code());
            if (check(MATH)) return new Expr.Math(math());
            if (match(HASH)) return new Expr.Title(text());
            if (match(DOUBLE_HASH)) return new Expr.Subtitle(text());
            if (match(TRIPLE_HASH)) return new Expr.Caption(text());
            if (match(ANGLE_RIGHT)) return new Expr.Paragraph(text());
            if (match(BRACE_LEFT)) return new Expr.Block(block());
            if (match(BRACKET_LEFT)) return list();
            if (match(DOUBLE_NEWLINE)) return expression();
            if (match(DOUBLE_HYPHEN)) return new Expr.HorizontalLine();
            if (match(SEMICOLON)) return def();
            if (match(DOUBLE_PAREN_LEFT)) return link();
            if (match(DOUBLE_BRACKET_LEFT)) return img();
            if (match(DOUBLE_PIPE)) return table();
            
            return null;
        } catch (ParseError error) {
            return null;
        }
    }
    
    private Expr.Table table() {
        List<List<Expr.Cell>> rows = new ArrayList<>();
        boolean header = false;
        int rowSpan;
        int colSpan;
                    
        while(!check(DOUBLE_PIPE) && !isAtEnd()) {
            if (match(PIPE_HYPHEN)) {
                List<Expr.Cell> row = new ArrayList<>();
                
                while(!check(DOUBLE_PIPE) && !check(PIPE_HYPHEN) && !isAtEnd()) {
                    rowSpan = 1;
                    colSpan = 1;
                    
                    if (match(BANG)) {
                        header = true;
                    } else if (match(PIPE)) {
                        header = false;
                    } else {
                        error(peek(), "Expect either !, | or |-.");
                    }
                    
                    while(!check(STRING) && !isAtEnd()) {
                        if (match(UNDERSCORE)) {
                            rowSpan++;
                        } else if (match(ANGLE_RIGHT)) {
                            colSpan++;
                        } else {
                            error(peek(), "Expect either _ or >.");
                        }
                    }
                    
                    if (peek().type != EOF) {
                        String content = consume(STRING, "Expect string.").literal;
                        row.add(new Expr.Cell(header, rowSpan, colSpan, content));
                    }
                }
                
                rows.add(row);
            } else {
                error(peek(), "Expect |-.");
            }
        }
            
        if (peek().type != EOF) consume(DOUBLE_PIPE, "Expect ||.");
        return new Expr.Table(rows);
    }
    
    private String code() {
        if (check(CODE)) return advance().literal;
        return "";
    }
    
    private String math() {
        if (check(MATH)) return advance().literal;
        return "";
    }
    
    private Expr.Image img() {
        String src = consume(STRING, "Expect source path after [[.").literal;
        Expr.Image img;
        
        if (match(PIPE)) {
            String width = consume(STRING, "Expect width after |.").literal;
            consume(SEMICOLON, "Expect ; after width");
            String height = consume(STRING, "Expect height after ;.").literal;
            img = new Expr.Image(src, width, height);
        } else {
            img = new Expr.Image(src);
        }
        
        if (peek().type != EOF) consume(DOUBLE_BRACKET_RIGHT, "Expect ]].");
        return img;
    }
    
    private Expr.Link link() {
        String href = consume(STRING, "Expect link after ((.").literal;
        Expr.Link link;
        
        if (match(PIPE)) {
            String text = consume(STRING, "Expect text after |.").literal;
            link = new Expr.Link(href, text);
        } else {
            link = new Expr.Link(href);
        }
        
        if (peek().type != EOF) consume(DOUBLE_PAREN_RIGHT, "Expect )).");
        return link;
    }
    
    private Expr.Listing list() {
        boolean ordered = false;
        
        List<Expr.ListItem> items = new ArrayList<>();
        
        if (check(STAR)) {
            ordered = true;
        }
        
        while (!check(BRACKET_RIGHT) && !isAtEnd()) {
            if (match(ordered ? STAR : PLUS)) {
                items.add(listItem());
            } else {
                error(peek(), "Expect either + or *");
            }
        }
        
        if (peek().type != EOF) consume(BRACKET_RIGHT, "Expect ].");
        return new Expr.Listing(items, ordered);
    }
    
    private Expr.Definition def() {
        List<String> definitions = new ArrayList<>();
        String word = consume(STRING, "Expect string after ;.").literal;
        
        while (check(PLUS) && !isAtEnd()) {
            advance();
            definitions.add(consume(STRING, "Expect string after +.").literal);
        }
        
        return new Expr.Definition(word, definitions);
    }
    
    private Expr.ListItem listItem() {
        Expr.ListItem li = null;
        
        try {
            Expr.Listable listable = (Expr.Listable)expression();
            li = new Expr.ListItem(listable);
        } catch (Exception e) {
            error(previous(), " Expect listable expression.");
        }
        
        return li;
    }
    
    private String text() {
        if(check(STRING)) return advance().literal;
        return "";
    }
    
    private List<Expr> block() {
        List<Expr> expressions = new ArrayList<>();

        while (!check(BRACE_RIGHT) && !isAtEnd()) {
          expressions.add(expression());
        }

        consume(BRACE_RIGHT, "Expect '}' after block.");
        return expressions;
    }
  
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }
  
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }
  
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }
  
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }
  
    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
    
    private ParseError error(Token token, String message) {
        Ntml.error(token, message);
        return new ParseError();
    }
}

