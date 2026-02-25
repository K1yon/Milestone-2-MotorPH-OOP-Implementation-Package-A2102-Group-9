package motorph.util;

import java.io.*;
public class CsvFixer {

    public static void main(String[] args) {
        String inputFile = "input.csv"; 
        String outputFile = "fixed.csv"; 
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = br.readLine()) != null) {
                String fixedLine = fixLine(line);
                bw.write(fixedLine);
                bw.newLine();
            }

            System.out.println("CSV fixing completed. Output file: " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String fixLine(String line) {
 
        String[] parts = line.split(",", 8);

        if (parts.length < 8) {

            return line;
        }

        String address = parts[4].trim();
        if (!address.startsWith("\"") || !address.endsWith("\"")) {

            parts[4] = "\"" + address + "\"";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            sb.append(parts[i].trim());
            if (i < parts.length - 1) {
                sb.append(",");
            }
        }

        return sb.toString();
    }
}