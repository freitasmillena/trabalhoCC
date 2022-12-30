import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

/**
 * Claase SR - Servidor de Resolução (extensão da classe Servidor)
 * 
 * @author Millena Freitas (a97777)
 * @author Guilherme Martins (a92847)
 * @author Vasco Oliveira (a96361)
 */
public class SR extends Servidor{
    // Lista de servidores associados ao SR
    private List<String> DD;
    // Cache / Base de dados do SR
    private Data cache;

    /**
     * Construtor vazio para um Servidor de Resolução
     */
    public SR() {
        super();
        this.DD = new ArrayList<>();
    }

    /**
     * Construtor para um ervidor de Resolução
     * 
     * @param dominio nome de domínio do servidor
     * @param portaAtendimento número da Porta de atendimento do servidor
     * @param ficheiroLog diretoria para aceder ao ficheiro de Logs
     * @param servidoresTopo lista dos servidores de topo
     * @param timeout tempo máximo de espera pela resposta a uma query
     * @param servidoresDNS Lista de servidores associados ao SR
     */
    public SR(String dominio, String timeout, String portaAtendimento, String ficheiroLog, List<String> servidoresTopo, List<String> servidoresDNS) {
        super(dominio, portaAtendimento, ficheiroLog, servidoresTopo, timeout);
        this.DD = new ArrayList<>();
        for(String dns : servidoresDNS){
            this.DD.add(dns);
        }
    }


    /**
     * Define e endereço IP para o DD do Servidor de Resolução.
     * 
     * @param DD endereço IP (DD) a adicionar
     */
    public void setDD(String DD) {
        this.DD.add(DD);
    }

    /**
     * Devolve a Lista de servidores associados ao SR
     * @return Cópia da Lista de servidores associados ao SR
     */
    public List<String> getDD() {
        return this.DD;
    }


    /**
     * Define a cache do Servidor de Resolução
     * 
     * @param cache HasMap com a cache do Servidor de Resolução
     */
    public void setCache(Data cache) {
        this.cache = cache;
    }

    /**
     * Veriifca se dois SRs são igauis
     * 
     * @param o Objeto do servidor de resolução recebido para comparação 
     * @return booleano que diz se os dois SRs são iguais
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SR sr = (SR) o;
        return (super.equals(sr) &&
                this.DD.equals(sr.getDD())
        );
    }

    @Override
    public void transf_zone() {

    }

    @Override
    /**
     * Responsável por receber uma query e enviá-la para processamento na comunicação UDP.
     */
    public void query(){

        while (true) {
            byte[] buffer = new byte[512];
            DatagramSocket serverS = null;

            try {
                //Receber
                serverS = new DatagramSocket(5555);
                DatagramPacket receiver = new DatagramPacket(buffer, buffer.length);
                serverS.receive(receiver);
                serverS.close();


                int portClient = receiver.getPort();
                InetAddress ipCliente = receiver.getAddress();

                PDU query = new PDU(receiver.getData());

                //Logs query recebida
                String ip = ipCliente.toString();
                String[] args = ip.split("/", 2);

                Log l1 = new Log("QR", args[1], query.ToString());

                //Log terminal
                System.out.println(l1);
                //Log ficheiro
                l1.logToFile(super.getFicheiroLog());

                Thread tudp = new Thread(new UDPComm(this.cache,query,ipCliente,portClient,super.getFicheiroLog(), this.getTimeOut()));
                tudp.start();

            } catch (SocketException ex) {
                System.out.println("Socket error: " + ex.getMessage());
            } catch (IOException ex) {
                System.out.println("I/O error: " + ex.getMessage());
            }

        }

    }

}
