import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * Classe SP - de um Servidor Primário (extensão de Servidor)
 * 
 * @author Millena Freitas (a97777) - 50%
 * @author Guilherme Martins (a92847) - 20%
 * @author Vasco Oliveira (a96361) - 30%
 */
public class SP extends Servidor{
    // Diretoria do ficheiro de base de dados do servidor primário
    private String ficheiroBD;
    private String segurancaBD;
    private String subDominio;
    private Data BD;
    // Lista dos Servidores Secundários associados ao Servidor Primário
    private List<String> servidoresSecundarios;


    // Construtor vazio
    /**
     * Construtor vazio para um Servidor Primário
     */
    public SP() {
        super();
        this.ficheiroBD = "";
        this.segurancaBD = "";
        this.servidoresSecundarios = new ArrayList<>();
        this.subDominio = null;
    }


    // Construtor completo
    /**
     * Construtor que cria um Servidor Primário a partir dos dados recebidos
     * 
     * @param dominio nome de domínio do servidor primário
     * @param timeout tempo máximo de espera pela resposta a uma query
     * @param portaAtendimento
     * @param ficheiroLog
     * @param servidoresTopo
     * @param ficheiroBD
     * @param segurancaBD
     * @param servidoresSecundarios lista dos Servidores Secundários associados ao Servidor Primário
     */
    public SP(String dominio, String timeout, String portaAtendimento, String ficheiroLog, List<String> servidoresTopo, String ficheiroBD, String segurancaBD, List<String> servidoresSecundarios) {
        super(dominio, portaAtendimento, ficheiroLog, servidoresTopo, timeout);
        this.ficheiroBD = ficheiroBD;
        this.segurancaBD = segurancaBD;
        this.servidoresSecundarios = new ArrayList<>();

        for(String st : servidoresSecundarios){
            this.servidoresSecundarios.add(st);
        }


    }

    public String getSubDominio() {
        return subDominio;
    }

    public void setSubDominio(String subDominio) {
        this.subDominio = subDominio;
    }


    /**
     * Devolve a diretoria onde está o ficheiro da base de dados do SP
     * 
     * @return string com a diretoria onde está o ficheiro da base de dados do SP
     */
    public String getFicheiroBD() {
        return ficheiroBD;
    }

    /**
     * Define a diretoria onde está o ficheiro da base de dados do SP
     * 
     * @param ficheiroBD string com a diretoria onde está o ficheiro da base de dados do SP
     */
    public void setFicheiroBD(String ficheiroBD) {
        this.ficheiroBD = ficheiroBD;
    }

    public String getSegurancaBD() {
        return segurancaBD;
    }

    public void setSegurancaBD(String segurancaBD) {
        this.segurancaBD = segurancaBD;
    }

    /**
     * Devolve a lista dos servidores secundários que estão associados ao servidor primário
     * 
     * @return lista dos servidores secundários que estão associados ao servidor primário
     */
    public List<String> getServidoresSecundarios() {
        List<String> res = new ArrayList<>();
        for(String st : this.servidoresSecundarios) {
            res.add(st);
        }
        return res;
    }

    /**
     * Define a lista dos servidores secundários do SP
     * 
     * @param servidoresSecundarios lista dos servidores secundários que queremos adicionar
     */
    public void setServidoresSecundarios(List<String> servidoresSecundarios) {
        this.servidoresSecundarios = new ArrayList<>();
        for(String st : servidoresSecundarios) {
            this.servidoresSecundarios.add(st);
        }
    }

    /**
     * Adiciona um servidor secundário à lista de servidores secundários do SP
     * 
     * @param ss servidor secundário que se quer adicionar
     */
    public void addServidorSecundario(String ss) {
        this.servidoresSecundarios.add(ss);
    }

    public void setData(Data bd){
        this.BD = bd;
    }


    /**
     * Método que permite ao Servidor Primário estar sempre à espera de pedidos e de responder aos mesmos pedidos. 
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

                Thread tudp = new Thread(new UDPComm(this.BD,query,ipCliente,portClient,super.getFicheiroLog()));
                tudp.start();

            } catch (SocketException ex) {
                System.out.println("Socket error: " + ex.getMessage());
            } catch (IOException ex) {
                System.out.println("I/O error: " + ex.getMessage());
            }

        }

    }


    /**
     * Método responsável por receber um pedido de um Servidor Secundário (SS) para obter uma cópia da base de dados do Servidor Primário e por enviar a cópia para o SS.
     * Simulação da Transferência de Zona quando o SP envia a cópia da sua base de dados para o SS.
     */
    public void transf_zone() {

            try {
                ServerSocket ss = new ServerSocket(24689);
                while(true) {

                    Socket socket = ss.accept();

                    Thread t = new Thread(new TCPComm(this.BD, socket, super.getFicheiroLog()));
                    t.start();

                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }


    }
}


