package the.picateclas.planificador;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Main {

    public static void main(String args[]) throws IOException {
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
        Planificador p = new Planificador(lines);
        return p.getLineas(p.calculatePlanList());

    }
}
