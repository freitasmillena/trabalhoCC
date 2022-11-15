import java.util.Random;

public class Cliente {

    public static void main(String[] args){
        String serverAdd = args[1];
        String name = args[2];
        String type = args[3];
        String serverPort = "5353";

        Random r = new Random();
        int msgID = r.nextInt(65535-1) + 1;


        PDU query = new PDU(Integer.toString(msgID), name, type);
        String enviar = query.toString();
        System.out.println(enviar);

        UDPHandler handler = new UDPHandler(serverAdd, serverPort);
        String resposta = handler.connectionHandler(enviar);
        System.out.println(resposta);


    }
}
