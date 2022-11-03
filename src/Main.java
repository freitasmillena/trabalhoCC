import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        Log test = new Log("ST", "127.0.0.1", "53 20000 shy");
        Log test2 = new Log("EV", "127.0.0.1", "socorro deus");

        System.out.println(test);
        System.out.println(test2);


        try {
            test2.logToFile("D:/TrabCC/trabalhoCC/logtest.txt");
            test.logToFile("D:/TrabCC/trabalhoCC/logtest.txt");
        } catch (IOException e) {
            System.out.println("deu merda");
        }
    }
}