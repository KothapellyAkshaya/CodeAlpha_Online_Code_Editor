import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class OnlineCodeEditor {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Online Code Editor is running on port 8080...");
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                CodeExecutionThread executionThread = new CodeExecutionThread(clientSocket);
                executionThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class CodeExecutionThread extends Thread {
    private Socket clientSocket;

    public CodeExecutionThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            
            writer.println("Welcome to the Online Code Editor!");
            writer.println("Please enter your Java code:");

            StringBuilder codeBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                codeBuilder.append(line).append("\n");
            }
            String code = codeBuilder.toString();

            // Execute the Java code
            String output = executeJavaCode(code);

            writer.println("Output:");
            writer.println(output);

            reader.close();
            writer.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String executeJavaCode(String code) {
        // You can use tools like JavaCompiler or external libraries to compile and execute the Java code dynamically.
        // Here's a simple example that uses the built-in Java Runtime to execute the code.

        StringBuilder outputBuilder = new StringBuilder();

        try {
            // Create a temporary file with the Java code
            String fileName = "TempCode.java";
            PrintWriter fileWriter = new PrintWriter(fileName);
            fileWriter.println(code);
            fileWriter.close();

            // Compile the code
            Process compileProcess = Runtime.getRuntime().exec("javac " + fileName);
            compileProcess.waitFor();

            // Execute the code
            Process executeProcess = Runtime.getRuntime().exec("java TempCode");
            BufferedReader reader = new BufferedReader(new InputStreamReader(executeProcess.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                outputBuilder.append(line).append("\n");
            }

            reader.close();

            // Delete the temporary file
            new File(fileName).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputBuilder.toString();
    }
}