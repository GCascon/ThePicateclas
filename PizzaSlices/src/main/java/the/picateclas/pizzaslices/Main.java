package the.picateclas.pizzaslices;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class Main {

    public static void main(String args[]) throws IOException{
        System.out.println("Input file: " + args[0]);
        String outputFile = args[0].replace(".in", ".out");
        writeOutput(outputFile, process(readInput(args[0])));
        System.out.println("Done!");
    }

    public static List<String> readInput(String filePath) throws IOException {        
        System.out.println("Reading file:" + filePath);
        File input = new File(filePath);
        List<String> lines = FileUtils.readLines(input, "UTF-8");        
        return lines;
    }

    public static void writeOutput(String fileName, List<String> lines) throws IOException {
        System.out.println("Writing output to file:" + fileName);
        File output = new File(fileName);
        FileUtils.writeLines(output, lines);
    }

    public static List<String> process(List<String> lines) {
        System.out.println("Processing Data...");
        
        Pizza p=new Pizza(lines);
        System.out.println(p);
        PizzaCutter pizzaCutter=new PizzaCutter();
        List<Slice> slices=pizzaCutter.cutPizza(p);
        
        List<String> result=new ArrayList<>();
        result.add(String.valueOf(slices.size()));
        int score=0;
        for(Slice s: slices){            
            result.add(s.toLine());
            score+=s.getRows()*s.getColumns();
        }
        System.out.println("Score="+score);
        
        return result;
    }
}
