import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPComm implements Runnable{
    private Data bd;
    private PDU query;
    private InetAddress ipCliente;
    private int portClient;
    private String logFile;

    public UDPComm(Data d, PDU query, InetAddress ipCliente, int portClient, String logFile){
        this.bd = d;
        this.query = query;
        this.ipCliente = ipCliente;
        this.portClient = portClient;
        this.logFile = logFile;
    }


    public void run() {
        //handle Query
        String pdu = this.bd.handleQuery(query);

        try {
            //Enviar
            DatagramPacket sender = new DatagramPacket(pdu.getBytes(), pdu.getBytes().length, this.ipCliente, this.portClient);
            DatagramSocket s = new DatagramSocket();
            s.send(sender);
            s.close();

            String ip = this.ipCliente.toString();
            String[] args = ip.split("/", 2);

            //Logs resposta
            Log l2 = new Log("RP", args[1], pdu);
            System.out.println(l2);
            l2.logToFile(logFile);
        }
        catch (SocketException ex) {
            System.out.println("Socket error: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
