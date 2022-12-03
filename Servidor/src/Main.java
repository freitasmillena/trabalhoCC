/**
 * Classe Main principal que recebe os argumentos do terminal, os Servidores
 */
public class Main {
    public static void main(String[] args) {
        String portaAtendimento = args[0];
        String valorTimeout = args[1];
        String debug = args[2]; // y/n
        String configPath = args[3];
        String typeServer = args[4];


        Parser p = new Parser();

        Servidor serv = null;

        switch (typeServer) {
            case "sp":
                serv = new SP();
                serv.setTimeOut(valorTimeout);
                p.fileParserConfigSP(configPath, (SP) serv);
                p.fileParserDadosSP((SP) serv);
                break;
            case "ss":
                serv = new SS();
                serv.setTimeOut(valorTimeout);
                p.fileParserConfigSS(configPath, (SS) serv);
                break;
            case "sr":
                serv = new SR();
                serv.setTimeOut(valorTimeout);
                p.fileParserConfigSR(configPath, (SR) serv);
                break;
        }

        //assert serv != null;
        Servidor finalServ = serv;
        Runnable worker = () -> {
            finalServ.transf_zone();
        };
        Thread tcp = new Thread(worker);
        tcp.start();

        serv.query();

    }
}

