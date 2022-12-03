import java.util.Random;

/**
 * Classe do Cliente
 */
public class Cliente {

    /**
     * Função ativada quando um Cliente escreve um pedido através do terminal.
     * O pedido recebido é transformado num PDU (em formato String) para que este possa chegar ao Servidor destino.
     * Depois de enviado, o Cliente fica à espera pela resposta do Servidor.
     * 
     * @param args - argumentos recebidos pelo terminal 
     */
    public static void main(String[] args){
        String serverAdd = args[1];
        String name = args[2];
        String type = args[3];
        String serverPort = "5556";

        Random r = new Random();
        int msgID = r.nextInt(65535-1) + 1;


        PDU query = new PDU(Integer.toString(msgID), name, type);
        String enviar = query.toString();
        System.out.println("Query enviada:");
        System.out.println(query.imprime());

        UDPHandler handler = new UDPHandler(serverAdd, serverPort);
        PDU resposta = handler.connectionHandler(enviar);
        System.out.println("Resposta recebida:");
        System.out.println(resposta.imprime());
    }
}
