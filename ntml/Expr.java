package ntml;

import java.util.List;

abstract class Expr {
    abstract <R> R accept(Visitor<R> visitor);

    interface Visitor<R> {
        R visitTitleExpr(Title expr);
        R visitSubtitleExpr(Subtitle expr);
        R visitCaptionExpr(Caption expr);
        R visitBlockExpr(Block expr);
        R visitParagraphExpr(Paragraph expr);
        R visitListingExpr(Listing expr);
        R visitTextExpr(Text expr);
        R visitGroupingExpr(Grouping expr);
        R visitListItemExpr(ListItem expr);
        R visitHorizontalLineExpr(HorizontalLine expr);
        R visitDefinitionExpr(Definition expr);
        R visitLinkExpr(Link expr);
        R visitImageExpr(Image expr);
        R visitCodeExpr(Code expr);
        R visitMathExpr(Math expr);
        R visitTableExpr(Table expr);
        R visitCellExpr(Cell expr);
    }
    
    interface Listable {
        <R> R acceptListable(Visitor<R> visitor);
    }
    
    static class Text extends Expr implements Listable {
        Text(String content) {
            this.content = content;
        }
        
        @Override
        public <R> R acceptListable(Visitor<R> visitor) {
            return this.accept(visitor);
        }
        
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitTextExpr(this);
        }
        
        final String content;
        
        @Override
        public String toString() {
            return "TEXT: " + content;
        }
    }
    
    static class HorizontalLine extends Expr {
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitHorizontalLineExpr(this);
        }
        
        @Override
        public String toString() {
            return "---";
        }
    }
    
    static class Math extends Expr implements Listable {
        Math(String math) {
            this.math = math;
        }
        
        @Override
        public <R> R acceptListable(Visitor<R> visitor) {
            return this.accept(visitor);
        }
        
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitMathExpr(this);
        }
        
        final String math;
        
        @Override 
        public String toString() {
            return "$" + math + "$";
        }
    }
    
    static class Code extends Expr {
        Code(String code) {
            this.code = code;
        }
        
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCodeExpr(this);
        }
        
        final String code;
        
        @Override 
        public String toString() {
            return "%" + code + "%";
        }
    }
    
    static class Image extends Expr implements Listable {
        Image(String src, String width, String height) {
            this.src = src;
            this.width = width;
            this.height = height;
        }
        
        Image(String src) {
            this.src = src;
            this.width = "100%";
            this.height = "100%";
        }
        
        @Override
        public <R> R acceptListable(Visitor<R> visitor) {
            return this.accept(visitor);
        }
        
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitImageExpr(this);
        }
        
        final String src;
        final String width;
        final String height;
        
        @Override
        public String toString() {
            return "IMG: " + src + "[" + width + ";" + height + "]";
        }
    }
    
    static class Link extends Expr implements Listable {
        Link(String href, String text) {
            this.href = href;
            this.text = text;
        }
        
        Link(String href) {
            this.href = href;
            this.text = href;
        }

        @Override
        public <R> R acceptListable(Visitor<R> visitor) {
            return this.accept(visitor);
        }
        
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLinkExpr(this);
        }
        
        final String text;
        final String href;
        
        @Override
        public String toString() {
            return "LINK: " + text + " -> " + href;
        }
    }
    
    static class Title extends Expr implements Listable {
        Title(String text) {
            this.text = text;
        }
        
        @Override
        public <R> R acceptListable(Visitor<R> visitor) {
            return this.accept(visitor);
        }
        
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitTitleExpr(this);
        }
        
        final String text;
        
        @Override
        public String toString() {
            return "TITLE: " + text;
        }
    }
    
    static class Subtitle extends Expr implements Listable {
        Subtitle(String text) {
            this.text = text;
        }
        
        @Override
        public <R> R acceptListable(Visitor<R> visitor) {
            return this.accept(visitor);
        }
        
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSubtitleExpr(this);
        }
        
        final String text;
        
        @Override
        public String toString() {
            return "SUBTITLE: " + text;
        }
    }
    
    static class Caption extends Expr implements Listable {
        Caption(String text) {
            this.text = text;
        }
        
        @Override
        public <R> R acceptListable(Visitor<R> visitor) {
            return this.accept(visitor);
        }
        
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCaptionExpr(this);
        }
        
        final String text;
        
        @Override
        public String toString() {
            return "CAPTION: " + text;
        }
    }
    
    static class Paragraph extends Expr implements Listable {
        Paragraph(String text) {
            this.text = text;
        }
        
        @Override
        public <R> R acceptListable(Visitor<R> visitor) {
            return this.accept(visitor);
        }
        
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitParagraphExpr(this);
        }
        
        final String text;
        
        @Override
        public String toString() {
            return "PARAGRAPH: " + text;
        }
    }
    
    static class Block extends Expr {
        Block(List<Expr> expressions) {
            this.expressions = expressions;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockExpr(this);
        }

        final List<Expr> expressions;
        
        @Override
        public String toString() {
            String s = "BLOCK {\n";
            for (Expr expr : expressions) {
                s += expr.toString() + "\n";
            }
            return s + "}";
        }
    }

    static class Grouping extends Expr implements Listable {
        Grouping(List<Expr> expressions) {
            this.expressions = expressions;
        }

        @Override
        public <R> R acceptListable(Visitor<R> visitor) {
            return this.accept(visitor);
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        final List<Expr> expressions;

        @Override
        public String toString() {
            String s = "GROUPING (\n";
            for (Expr expr : expressions) {
                s += expr.toString() + "\n";
            }
            return s + ")";
        }
    }
    
    static class Table extends Expr {
        Table(List<List<Cell>> cells) {
            this.cells = cells;
        }
        
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitTableExpr(this);
        }
        
        final List<List<Cell>> cells;
        
        @Override
        public String toString() {
            String s = "TABLE ||\n";
            for (List<Cell> row : cells) {
                for (Cell cell : row) {
                    s += cell.toString();
                }
                s += "\n";
            }
            return s + "||";
        }
    }
    
    static class Cell extends Expr {
        public Cell(boolean header, int rowSpan, int colSpan, Listable content) {
            this.header = header;
            this.rowSpan = rowSpan;
            this.colSpan = colSpan;
            this.content = content;
        }
        
        public Cell(boolean header) {
            this.header = header;
            this.rowSpan = 0;
            this.colSpan = 0;
            this.content = new Text("");
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCellExpr(this);
        }
        
        final boolean header;
        final int rowSpan;
        final int colSpan;
        final Listable content;
        
        @Override
        public String toString() {
            return "|" + content.toString() + "|";
        }
    }
    
    static class Listing extends Expr implements Listable {
        Listing(List<ListItem> items, boolean ordered) {
            this.items = items;
            this.ordered = ordered;
        }

        @Override
        public <R> R acceptListable(Visitor<R> visitor) {
            return this.accept(visitor);
        }
        
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitListingExpr(this);
        }
        
        final List<ListItem> items;
        final boolean ordered;
        
        @Override
        public String toString() {
            String s = "LIST [\n";
            for (ListItem item : items) {
                s += item.toString() + "\n";
            }
            return s + "]";
        }
    }
    
    static class ListItem extends Expr {
        public ListItem(Listable item) {
            this.item = item;
        }
        
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitListItemExpr(this);
        }
        
        final Listable item;
        
        @Override
        public String toString() {
            return ". " + item.toString();
        }
    }
    
    static class Definition extends Expr implements Listable {
        Definition(String word, List<String> definitions) {
            this.word = word;
            this.definitions = definitions;
        }
        
        @Override
        public <R> R acceptListable(Visitor<R> visitor) {
            return this.accept(visitor);
        }
        
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitDefinitionExpr(this);
        }
        
        final String word;
        final List<String> definitions;
        
        @Override
        public String toString() {
            String result = word + ":\n";
            for (String definition : definitions) {
                result += "- " + definition + "\n";
            }
            
            return result;
        }
    }
}
