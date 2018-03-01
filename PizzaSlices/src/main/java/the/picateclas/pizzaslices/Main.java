package the.picateclas.pizzaslices;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Main {

    public static void main(String args[]) throws IOException {
        if (args.length < 2) {
            System.out.println("Use: java -jar PizzaSlices.jar <file.in> <timeout(seconds)>");
        } else {
            System.out.println("Input file: " + args[0]);
            System.out.println("Timeout: " + args[1] + " seconds");
            String outputFile = args[0].replace(".in", ".out");
            writeOutput(outputFile, process(readInput(args[0]), Long.valueOf(args[1])));
            System.out.println("Done!");
        }
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

    public static List<String> process(List<String> lines, long timeout) {
        System.out.println("Processing Data...");

        Pizza p = new Pizza(lines);
        // System.out.println(p);
        System.out.println("Pizza loaded");
        PizzaCutter pizzaCutter = new PizzaCutter();

        System.out.println("Cutting pizza...");
        List<Slice> slices = pizzaCutter.cutPizza(p, timeout * 1000);

        List<String> result = new ArrayList<>();
        result.add(String.valueOf(slices.size()));
        int score = 0;
        for (Slice s : slices) {
            result.add(s.toLine());
            score += s.getRows() * s.getColumns();
        }
        System.out.println("Score=" + score);

        return result;
    }
}
