import java.io.*;

public class UserCSVGenerator {
    public static void main(String[] args) {
        System.out.println("Current working directory: " + System.getProperty("user.dir"));

        String inputFile = "employee_data_full.csv"; 
        String outputFile = "users.csv";        

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

            String header = br.readLine();
            if (header == null) {
                System.out.println("Input file is empty.");
                return;
            }


            String[] columns = header.split(",", -1);
            int empNumIndex = -1;
            for (int i = 0; i < columns.length; i++) {
                if (columns[i].trim().equalsIgnoreCase("Employee Number")) {
                    empNumIndex = i;
                    break;
                }
            }

            if (empNumIndex == -1) {
                System.out.println("Employee Number column not found.");
                return;
            }


            bw.write("employeeNumber,password");
            bw.newLine();

            String line;
            while ((line = br.readLine()) != null) {
  
                String[] data = line.split(",", -1);
                if (data.length > empNumIndex) {
                    String empNum = data[empNumIndex].trim();
                    if (!empNum.isEmpty()) {

                        bw.write(empNum + "," + empNum);
                        bw.newLine();
                    }
                }
            }

            System.out.println("users.csv generated successfully at: " + new File(outputFile).getAbsolutePath());

        } catch (FileNotFoundException e) {
            System.err.println("Input file not found: " + inputFile);
        } catch (IOException e) {
            System.err.println("Error processing files:");
            e.printStackTrace();
        }
    }
}
