package motorph.util;

import java.io.*;

/**
 * Utility class for fixing CSV files by enclosing fields containing commas in double quotes.
 * <p>
 * This program reads an input CSV file line by line, applies fixes to fields with embedded commas,
 * and writes the corrected lines to an output CSV file.
 * </p>
 */
public class CsvAutoFixer {

    /**
     * Main method to execute the CSV fixing process.
     * Modify inputFile and outputFile paths as needed.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        String inputFile = "input.csv";   // Change to your CSV file path
        String outputFile = "fixed.csv";  // Output fixed CSV file

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = br.readLine()) != null) {
                String fixedLine = fixCsvLine(line);
                bw.write(fixedLine);
                bw.newLine();
            }

            System.out.println("CSV fixing completed. Output file: " + outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fixes a CSV line by enclosing any field containing commas in double quotes,
     * unless already quoted.
     * <p>
     * Uses a heuristic to merge fields that belong together when the number of columns is incorrect,
     * assuming the address field contains commas.
     * </p>
     *
     * @param line the CSV line to fix
     * @return the fixed CSV line
     */
    private static String fixCsvLine(String line) {
        String[] parts = line.split(",");
        int expectedColumns = 8;  // Set this to your expected number of columns

        if (parts.length == expectedColumns) {
            for (int i = 0; i < parts.length; i++) {
                parts[i] = quoteIfNeeded(parts[i]);
            }
            return String.join(",", parts);
        }

        int firstPartCount = 4;  // columns before address
        int lastPartCount = expectedColumns - firstPartCount - 1; // columns after address

        int addressStart = firstPartCount;
        int addressEnd = parts.length - lastPartCount - 1;

        StringBuilder addressBuilder = new StringBuilder();
        for (int i = addressStart; i <= addressEnd; i++) {
            if (addressBuilder.length() > 0) addressBuilder.append(",");
            addressBuilder.append(parts[i].trim());
        }

        String[] fixedParts = new String[expectedColumns];
        for (int i = 0; i < firstPartCount; i++) {
            fixedParts[i] = quoteIfNeeded(parts[i].trim());
        }
        fixedParts[firstPartCount] = "\"" + addressBuilder.toString() + "\"";

        int fixedIndex = firstPartCount + 1;
        for (int i = addressEnd + 1; i < parts.length; i++) {
            fixedParts[fixedIndex++] = quoteIfNeeded(parts[i].trim());
        }

        return String.join(",", fixedParts);
    }

    /**
     * Encloses the field in double quotes if it contains commas and is not already quoted.
     *
     * @param field the CSV field to check
     * @return the quoted field if needed; otherwise, the original field
     */
    private static String quoteIfNeeded(String field) {
        if (field.contains(",") && !(field.startsWith("\"") && field.endsWith("\""))) {
            return "\"" + field + "\"";
        }
        return field;
    }
}