import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String portaAtendimento = args[0];
        String valorTimeout = args[1];
        String debug = args[2]; // y/n
        String configPath = args[3];
        String typeServer = args[4];

        /* Exemplo
        5555 87400 y C:/Users/Mimi/UMinho/CC/trabalhoCC-main/path.txt sp
         */

        Parser p = new Parser();

        Servidor serv = null;

        switch (typeServer) {
            case "sp":
                serv = new SP();
                p.fileParserConfigSP(configPath, (SP) serv);
                p.fileParserDadosSP((SP) serv);
                break;
            case "ss":
                serv = new SS();
                p.fileParserConfigSS(configPath, (SS) serv);
                break;
            case "sr":
                serv = new SR();
                p.fileParserConfigSR(configPath, (SR) serv);
                break;
        }

        System.out.println(serv instanceof SP);
        System.out.println(serv instanceof SS);
        System.out.println(serv instanceof SR);

        //assert serv != null;
        //serv.run();

       // Ficheiros de LOG "C:/Users/Mimi/UMinho/CC/trabalhoCC-main/path.txt"

    }
}