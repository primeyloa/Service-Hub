import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GenerateCSV {

    public static void generateCSV(int size) throws IOException {

        FileWriter writer = new FileWriter("data_" + size + ".csv");
        Random random = new Random();

        writer.write("Index,Value\n");

        for (int i = 1; i <= size; i++) {
            int value = random.nextInt(1000);
            writer.write(i + "," + value + "\n");
        }

        writer.close();

        System.out.println("data_" + size + ".csv created");
    }

    public static void main(String[] args) throws IOException {

        generateCSV(30);
        generateCSV(50);
        generateCSV(100);
        generateCSV(300);
    }
}
