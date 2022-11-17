import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class UDPHandler {

    private String serverIP;
    private String serverPort;

    public UDPHandler(String serverIP, String serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }


    public String connectionHandler(String query) {


        byte[] buffer = new byte[512];


        PDU resposta = null;

        try {


            //Enviar
            InetAddress serverAdd = InetAddress.getByName(serverIP);
            DatagramPacket request = new DatagramPacket(query.getBytes(), query.getBytes().length, serverAdd, Integer.parseInt(serverPort));
            DatagramSocket s = new DatagramSocket();
            s.send(request);


            //Receber
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            s.receive(response);

            resposta = new PDU(response.getData());


            s.close();


        } catch (SocketTimeoutException ex) {
            System.out.println("Timeout error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Client error: " + ex.getMessage());
            ex.printStackTrace();
        }
        return resposta.toString();
    }


}
