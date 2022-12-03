import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.List;


/**
 * Classe SS - Servidor Secundário
 * 
 * @author Millena Freitas (a97777) - 50%
 * @author Guilherme Martins (a92847) - 20%
 * @author Vasco Oliveira (a96361) - 30%
 */
public class SS extends Servidor{
    // Servidor primário associado ao Servidor secundário
    private String servidorPrimario;
    private String segurancaSP;
    private String subDominio;
    private Data BD;

    /**
     * Construtor vazio para um Servidor Secundário
     */
    public SS() {
        super();
        this.servidorPrimario = "";
        this.segurancaSP = "";
        this.subDominio = null;
    }

    /**
     * Constrtutor de um Servidor Secundário
     * 
     * @param dominio nome de dominio do SS
     * @param portaAtendimento número da Porta de atendimento do servidor
     * @param ficheiroLog diretoria para aceder ao ficheiro de Logs
     * @param servidoresTopo lista dos servidores de topo
     * @param timeout tempo máximo de espera pela resposta a uma query
     * @param servidorPrimario Servidor primário associado ao Servidor secundário
     * @param segurancaSP
     */
    public SS(String dominio, String timeout, String portaAtendimento, String ficheiroLog, List<String> servidoresTopo, String servidorPrimario, String segurancaSP) {
        super(dominio, portaAtendimento, ficheiroLog, servidoresTopo, timeout);
        this.servidorPrimario = servidorPrimario;
        this.segurancaSP = segurancaSP;
    }

    public String getSubDominio() {
        return subDominio;
    }

    public void setSubDominio(String subDominio) {
        this.subDominio = subDominio;
    }

    /**
     * Devolve o servidor primário associado ao SS
     * 
     * @return servidor primário associado ao SS
     */
    public String getServidorPrimario() {
        return servidorPrimario;
    }

    /**
     * Define o servidor primário associado ao SS
     * 
     * @param servidorPrimario servidor primário associado ao SS
     */
    public void setServidorPrimario(String servidorPrimario) {
        this.servidorPrimario = servidorPrimario;
    }

    public String getSegurancaSP() {
        return segurancaSP;
    }

    public void setSegurancaSP(String segurancaSP) {
        this.segurancaSP = segurancaSP;
    }

    public void setData(Data bd){
        this.BD = bd;
    }


    /**
     * Método que permite ao Servidor Secundário estar sempre à espera de pedidos e de responder aos mesmos pedidos. 
     */
    public void transf_zone() {
        this.BD = Data.getInstance(super.getDominio());

        try{

            InetAddress ipSP = InetAddress.getByName(this.servidorPrimario);

            Socket socket = new Socket(ipSP, 24689);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            //Primeiro pedido -> envia domínio
            String request = "1;1;" + super.getDominio();
            long start = System.currentTimeMillis();
            out.println(request);
            out.flush();



            // Recebe número de linhas
            String response = in.readLine(); // "2;1;nlinhastotal"


            String[] error = response.split(";",3);
            if(!error[2].equals("dominioInvalido")){

                // Devolve número de linhas recebido
                out.println(response);
                out.flush();


                // Não vai enviar mais nada e vai receber linha a linha
                socket.shutdownOutput();

                String[] args = response.split(";", 3); // 3;nlinha;stringregisto
                int nLinhas = Integer.parseInt(args[2]);

                for(int i = 1; i <= nLinhas; i++){
                    String linha = in.readLine();
                    String[] linhaBD = linha.split(";", 3);
                    String registo = linhaBD[2];
                    String[] regs = registo.split(" ",5);
                    Registo r = new Registo(regs[2], Integer.parseInt(regs[3]), regs[1], Integer.parseInt(regs[4]), regs[0]);
                    this.BD.transfZonaLinha(r);
                }

                long elapsedTimeMillis = System.currentTimeMillis()-start;
                String dadosEntrada = "SS " + Long.toString(elapsedTimeMillis) +" ms";
                // Logs
                Log zt = new Log("ZT", this.servidorPrimario, dadosEntrada);
                //Log Terminal
                System.out.println(zt);
                //Log Ficheiro
                zt.logToFile(super.getFicheiroLog());
            }
            else {
                // Logs
                Log ez = new Log("EZ", this.servidorPrimario, "SS");
                //Log Terminal
                System.out.println(ez);
                //Log Ficheiro
                ez.logToFile(super.getFicheiroLog());
            }

            socket.shutdownInput();
            socket.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Método usado para permitir o Servidor Secundário receber queries e responder a elas.
     */
    public void query(){

        while (true) {
            byte[] buffer = new byte[512];
            DatagramSocket serverS = null;

            try {
                //Receber
                serverS = new DatagramSocket(5556);
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

}
