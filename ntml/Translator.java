package ntml;

import java.util.ArrayList;
import java.util.List;
import ntml.Expr.ListItem;
import ntml.Expr.Listable;
import ntml.Expr.Visitor;

public class Translator implements Visitor<List<String>> {
    
    public List<Expr> expressions;
    
    public Translator(List<Expr> expressions) {
        this.expressions = expressions;
    }
    
    public List<String> translate() {
        List<String> result = new ArrayList<>();
        
        for (Expr expr : expressions) {
            result.addAll(expr.accept(this));
        }
        
        return result;
    }

    @Override
    public List<String> visitTitleExpr(Expr.Title expr) {
        List<String> result = new ArrayList<>();
        result.add("<h1>");
        result.add(expr.text);
        result.add("</h1>");
        
        return result;
    }

    @Override
    public List<String> visitBlockExpr(Expr.Block expr) {
        List<String> result = new ArrayList<>();
        result.add("<div>");
        
        for(Expr e : expr.expressions) {
            result.addAll(e.accept(this));
        }
        
        result.add("</div>");
        return result; 
    }

    @Override
    public List<String> visitGroupingExpr(Expr.Grouping expr) {
        List<String> result = new ArrayList<>();
        for (Expr e : expr.expressions) {
            result.addAll(e.accept(this));
        }
        return result;
    }

    @Override
    public List<String> visitParagraphExpr(Expr.Paragraph expr) {
        List<String> result = new ArrayList<>();
        result.add("<p>");
        result.add(expr.text);
        result.add("</p>");
        
        return result;
    }

    @Override
    public List<String> visitListingExpr(Expr.Listing expr) {
        List<String> result = new ArrayList<>();
        result.add(expr.ordered ? "<ol>" : "<ul>");
        for(ListItem li : expr.items) {
            result.addAll(li.accept(this));
        }
        result.add(expr.ordered ? "</ol>" : "</ul>");
        
        return result;
    }

    @Override
    public List<String> visitTextExpr(Expr.Text expr) {
        List<String> result = new ArrayList<>();
        result.add(expr.content);
        return result;
    }

    @Override
    public List<String> visitListItemExpr(ListItem expr) {
        List<String> result = new ArrayList<>();
        result.add("<li>");
        result.addAll(expr.item.acceptListable(this));
        result.add("</li>");
        return result;
    }

    @Override
    public List<String> visitSubtitleExpr(Expr.Subtitle expr) {
        List<String> result = new ArrayList<>();
        result.add("<h2>");
        result.add(expr.text);
        result.add("</h2>");
        
        return result;
    }
    
    @Override
    public List<String> visitCaptionExpr(Expr.Caption expr) {
        List<String> result = new ArrayList<>();
        result.add("<h3>");
        result.add(expr.text);
        result.add("</h3>");
        
        return result;
    }

    @Override
    public List<String> visitHorizontalLineExpr(Expr.HorizontalLine expr) {
        List<String> result = new ArrayList<>();
        result.add("<hr>");
        return result;
    }

    @Override
    public List<String> visitDefinitionExpr(Expr.Definition expr) {
        List<String> result = new ArrayList<>();
        result.add("<dl>");
        result.add("<dt>");
        result.add(expr.word);
        result.add("</dt>");
        
        for (String definition : expr.definitions) {
            result.add("<dd>");
            result.add(definition);
            result.add("</dd>");
        }
        
        result.add("</dl>");
        
        return result;
    }

    @Override
    public List<String> visitLinkExpr(Expr.Link expr) {
        List<String> result = new ArrayList<>();
        result.add("<p><a href=\"" + expr.href + "\" target=\"_blank\">");
        result.add(expr.text);
        result.add("</a></p>");
        
        return result;
    }

    @Override
    public List<String> visitImageExpr(Expr.Image expr) {
        List<String> result = new ArrayList<>();
        result.add("<img src=\"" + expr.src + "\" "
                        + "width=\"" + expr.width + "\" "
                        + "height=\"" + expr.height + "\">");
        result.add("<br>");
        
        return result;
    }

    @Override
    public List<String> visitCodeExpr(Expr.Code expr) {
        List<String> result = new ArrayList<>();
        
        result.add("<div class=\"code\">");
        result.add("<pre>" + expr.code + "</pre>");
        result.add("</div>");
        
        return result;
    }

    @Override
    public List<String> visitMathExpr(Expr.Math expr) {
        List<String> result = new ArrayList<>();

        result.add("<div class=\"math\">");
        result.add("$$" + expr.math + "$$");
        result.add("</div>");
        
        return result;
    }
    
    @Override
    public List<String> visitTableExpr(Expr.Table expr) {
        List<String> result = new ArrayList<>();
        
        result.add("<table>");
        for (List<Expr.Cell> row : expr.cells) {
            result.add("<tr>");
            for (Expr.Cell cell : row) {
                result.addAll(cell.accept(this));
            }
            result.add("</tr>");
        }
        result.add("</table>");
        
        return result;    
    }
    
    @Override
    public List<String> visitCellExpr(Expr.Cell expr) {
        List<String> result = new ArrayList<>();
        
        String openingTag = expr.header ? "<th " : "<td ";
                
        if (expr.rowSpan > 1) {
            openingTag += "rowspan=\"" + expr.rowSpan + "\"";
        }
                
        if (expr.colSpan > 1) {
            openingTag += " colspan=\"" + expr.colSpan + "\"";
        }
                
        result.add(openingTag + ">");
        result.addAll(expr.content.acceptListable(this));
        result.add(expr.header ? "</th>" : "</td>");
        
        return result;  
    }
}
