package ntml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Formatter {
    
    static void write(List<String> tags, String outputPath, String stylePath) {
        try {
            File f = new File(outputPath);
            FileWriter fw = new FileWriter(f);
            String title = "Output";

            List<String> output = new ArrayList<>();
            output.add("<html>");
            output.addAll(createHead(title, stylePath));
            output.add("<body>");
            output.add("<div class=\"main\">");
            output.add("<script src=\"https://polyfill.io/v3/polyfill.min.js?features=es6\"></script>");
            output.add("<script id=\"MathJax-script\" async src=\"https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js\"></script>");
            output.addAll(tags);
            output.add("</div>");
            output.add("</body>");
            output.add("</html>");
            
            int tabCount = 0;
            
            for(int i = 0; i < output.size() - 1; i++) {
                String str = output.get(i);
                String next = output.get(i + 1);
                String openTagRegex = "<[^\\/][^>]*>";
                String closedTagRegex = "<\\/[^>]*>";
                String genericTagRegex = "<[^>]*>";
                
                for(int j = 0; j < tabCount; j++) {
                    fw.write("\t");
                }
                fw.write(str);
                fw.write("\n");
                
                if (!noIndent(str)) {
                    if (str.matches(openTagRegex) && !next.matches(closedTagRegex)) {
                        tabCount++;
                    } else if (next.matches(closedTagRegex) && !str.matches(openTagRegex)) { // Se un tago indenta al contrario vuol dire che è chiuso OPPURE è contenuto seguito
            // da un chiuso
                        tabCount--;
                    }
                }
            }
            
            fw.write(output.get(output.size() - 1));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static boolean noIndent(String str) {
        if (str.matches("<link.*>") 
            || str.matches("<img.*>") 
            || str.matches("<script.*")
            || str.matches("<br>")
            || str.matches("<hr>")) {
            return true;
        }
        
        return false;
    }
    
    static List<String> createHead(String title, String stylePath) throws IOException {
        List<String> head = new ArrayList<>();
        
        head.add("<head>");
        List<String> lines = Files.readAllLines(Paths.get(stylePath));
        head.add("<style>");
        for (String line : lines) {
            head.add(line);
        }
        head.add("</style>");
        head.add("<title>");
        head.add(title);
        head.add("</title>");
        head.add("</head>");
        
        return head;
    }
}
