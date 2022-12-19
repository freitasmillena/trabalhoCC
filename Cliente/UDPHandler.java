import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * Classe responsável por gerir os PDUs e enviá-los para o Servidor/Cliente correto
 * 
 * @author Millena Freitas (a97777) - 60%
 * @author Guilherme Martins (a92847) - 20%
 * @author Vasco Oliveira (a96361) - 20%
 */
public class UDPHandler {

    // Endereço IP do servidor que se deseja conectar
    private String serverIP;
    // Número da porta do servidor que se deseja conectar (sempre 5555)
    private String serverPort;


    /**
     * Construtor de um UDPHandler responsável por guardar nos seus atributos o endereço IP e o número da porta do servidor que se deseja conectar
     *
     * @param serverIP endereço IP do servidor que se deseja conectar
     * @param serverPort número da porta do servidor que se deseja conectar (sempre 5555)
     */
    public UDPHandler(String serverIP, String serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }


    /**
     * Método responsável por receber uma Query de um Cliente  e de enviá-lo para o respetivo Servidor
     * 
     * @param query String de um PDU em formato conciso
     * @return String que contém a resposta do Servidor para o pedido efetuado pelo Cliente
     */
    public PDU connectionHandler(String query) {

        byte[] buffer = new byte[2056];

        PDU resposta = null;

        try {

            //Enviar
            InetAddress serverAdd = InetAddress.getByName(serverIP);
            DatagramPacket request = new DatagramPacket(query.getBytes(), query.getBytes().length, serverAdd, Integer.parseInt(serverPort));
            DatagramSocket s = new DatagramSocket();
            s.send(request);

            //Receber
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            s.setSoTimeout(120000);
            s.receive(response);

            resposta = new PDU(response.getData());

            s.close();

        }
        catch (SocketTimeoutException ex) {
            System.out.println("Timeout error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Client error: " + ex.getMessage());
            ex.printStackTrace();
        }
        return resposta;
    }


}
