import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;


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
    private long counter;

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
    /*
    public void transf_zone()  {

        while(true) {

            try {
                InetAddress ipSP = InetAddress.getByName(this.servidorPrimario);

                Socket socket = new Socket(ipSP, 24689);

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                //Pergunta versão
                String requestBD = "1;1;SOASERIAL";
                System.out.println(requestBD);
                out.println(requestBD);
                out.flush();

                String responseBD = null;

                //Long timeoutVersao = Long.parseLong(super.getTimeOut()) + System.currentTimeMillis();

                //Recebe versão
                socket.setSoTimeout(Integer.parseInt(super.getTimeOut()));
                responseBD = in.readLine();
                System.out.println(responseBD);

                if(System.currentTimeMillis() <= timeoutVersao) {
                    String[] res = responseBD.split(";", 3);

                    if (res[2].equals(this.BD.getSOASERIAL())) {
                        this.BD.changeValid(true);
                        System.out.println("soaserial igual");

                        String response = "1;1;done";
                        System.out.println(response);
                        out.println(response);
                        out.flush();

                        socket.shutdownInput();
                        socket.shutdownOutput();
                        socket.close();
                        sleep(Long.parseLong(this.BD.getSOAREFRESH(super.getTimeOut())));

                    }
                    // Versão diferente, inicia transferência de zona
                    else {

                        String resp = "1;1;ok";
                        System.out.println(resp);
                        out.println(resp);
                        out.flush();

                        System.out.println("transf zona inicia");
                        //Primeiro pedido -> envia domínio
                        String request = "1;1;" + super.getDominio();

                        out.println(request);
                        out.flush();
                        System.out.println(request);

                        long start = System.currentTimeMillis();
                        Long timeout = Long.parseLong(super.getTimeOut()) + start;

                        // Recebe número de linhas
                        String response = in.readLine(); // "2;1;nlinhastotal"
                        System.out.println(response);
                        String[] error = response.split(";", 3);
                        List<Registo> rlist = new ArrayList<>();

                        if (!error[2].equals("dominioInvalido")) {
                            this.BD.clear();

                            // Devolve número de linhas recebido
                            out.println(response);
                            out.flush();

                            String[] args = response.split(";", 3); // 3;nlinha;stringregisto
                            int nLinhas = Integer.parseInt(args[2]);
                            boolean ok = true;
                            int length = 0;
                            for (int i = 1; i <= nLinhas; i++) {
                                String linha = in.readLine();

                                if(System.currentTimeMillis() <= timeout) {
                                    System.out.println(linha);
                                    String[] linhaBD = linha.split(";", 3);
                                    String registo = linhaBD[2];
                                    length += registo.getBytes(StandardCharsets.UTF_8).length;
                                    String[] regs = registo.split(" ", 5);
                                    Registo r = new Registo(regs[2], Integer.parseInt(regs[3]), regs[1], Integer.parseInt(regs[4]), regs[0], "other");
                                    //this.BD.transfZonaLinha(r);
                                    rlist.add(r);

                                    String respTransf = "3;"+ Integer.toString(i) + ";ok";
                                    System.out.println(respTransf);
                                    out.println(respTransf);
                                    out.flush();
                                }
                                else{
                                    ok = false;

                                    String respErro = "3;"+ Integer.toString(i) + ";timeout";
                                    System.out.println(respErro);
                                    out.println(respErro);
                                    out.flush();

                                    break;
                                }
                            }

                            long elapsedTimeMillis = System.currentTimeMillis() - start;
                            socket.shutdownInput();
                            socket.shutdownOutput();
                            socket.close();

                            //transf. zona concluída antes do timeout
                            if(ok) {
                                for(Registo r : rlist){
                                    this.BD.transfZonaLinha(r);
                                }
                                // Logs
                                String dadosEntrada = "SS " + Long.toString(elapsedTimeMillis) + " ms " + Integer.toString(length) + " bytes";
                                Log zt = new Log("ZT", this.servidorPrimario, dadosEntrada);
                                //Log Terminal
                                System.out.println(zt);
                                //Log Ficheiro
                                zt.logToFile(super.getFicheiroLog());
                                System.out.println("transf zona termina");
                                this.counter = System.currentTimeMillis();
                                sleep(Long.parseLong(this.BD.getSOAREFRESH(super.getTimeOut())));
                            }
                            else {
                                // timeout
                                System.out.println(this.BD.BDsize());

                                //Log timeout
                                Log to = new Log("TO", this.servidorPrimario, "Zone Transfer");
                                //Log Terminal
                                System.out.println(to);
                                //Log Ficheiro
                                to.logToFile(super.getFicheiroLog());

                                sleep(Long.parseLong(this.BD.getSOARETRY(super.getTimeOut())));
                                System.out.println("ovo tentar de novo");

                            }

                        } else {
                            socket.shutdownInput();
                            socket.shutdownOutput();
                            socket.close();

                            // Logs
                            Log ez = new Log("EZ", this.servidorPrimario, "SS");
                            //Log Terminal
                            System.out.println(ez);
                            //Log Ficheiro
                            ez.logToFile(super.getFicheiroLog());

                            sleep(Long.parseLong(this.BD.getSOAREFRESH(super.getTimeOut())));
                        }
                    }
                }
                else {
                    String respErro = "1;1;timeout";
                    System.out.println(respErro);
                    out.println(respErro);
                    out.flush();
                    socket.shutdownInput();
                    socket.shutdownOutput();
                    socket.close();

                    //Log timeout
                    Log to = new Log("TO", this.servidorPrimario, "Zone Transfer");
                    //Log Terminal
                    System.out.println(to);
                    //Log Ficheiro
                    to.logToFile(super.getFicheiroLog());

                    sleep(Long.parseLong(this.BD.getSOARETRY(super.getTimeOut())));
                    System.out.println("ovo tentar de novo");
                }
            }
            catch (SocketException e) {
                //Log timeout
                Log to = new Log("TO", this.servidorPrimario, "Zone Transfer");
                //Log Terminal
                System.out.println(to);
                //Log Ficheiro
                try {
                    to.logToFile(super.getFicheiroLog());
                    sleep(Long.parseLong(this.BD.getSOARETRY(super.getTimeOut())));
                } catch (InterruptedException | IOException ex) {
                    ex.printStackTrace();
                }
            }catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }
    }*/

    public void transf_zone() {
        while(true) {

            Socket socket = null;
            try {
                InetAddress ipSP = InetAddress.getByName(this.servidorPrimario);

                socket = new Socket(ipSP, 24689);

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                //Pergunta versão
                String requestBD = "1;1;SOASERIAL";
                //System.out.println(requestBD);
                out.println(requestBD);
                out.flush();

                String responseBD = null;

                //Recebe versão
                socket.setSoTimeout(Integer.parseInt(super.getTimeOut()));
                responseBD = in.readLine();
                //System.out.println(responseBD);

                String[] res = responseBD.split(";", 3);

                if (res[2].equals(this.BD.getSOASERIAL())) {
                    this.BD.changeValid(true);
                    this.counter = System.currentTimeMillis();
                    //System.out.println("soaserial igual");

                    /*String response = "1;1;done";
                    System.out.println(response);
                    out.println(response);
                    out.flush();*/

                    socket.shutdownOutput();
                    socket.shutdownInput();
                    socket.close();
                    sleep(Long.parseLong(this.BD.getSOAREFRESH(super.getTimeOut())));

                }
                // Versão diferente, inicia transferência de zona
                else {
                        /*
                        String resp = "1;1;ok";
                        System.out.println(resp);
                        out.println(resp);
                        out.flush();*/

                    //System.out.println("transf zona inicia");
                    //Primeiro pedido -> envia domínio
                    String request = "1;1;" + super.getDominio();

                    out.println(request);
                    out.flush();
                    //System.out.println(request);

                    long start = System.currentTimeMillis();


                    // Recebe número de linhas
                    socket.setSoTimeout(Integer.parseInt(super.getTimeOut()));
                    String response = in.readLine(); // "2;1;nlinhastotal"
                    //System.out.println(response);
                    String[] error = response.split(";", 3);
                    List<Registo> rlist = new ArrayList<>();

                    if (!error[2].equals("dominioInvalido")) {
                        this.BD.clear();

                        // Devolve número de linhas recebido
                        out.println(response);
                        out.flush();

                        String[] args = response.split(";", 3); // 3;nlinha;stringregisto
                        int nLinhas = Integer.parseInt(args[2]);
                        int length = 0;
                        Long timeout = Long.parseLong(super.getTimeOut()) + System.currentTimeMillis();

                        for (int i = 1; i <= nLinhas; i++) {
                            String linha = in.readLine();
                            if(System.currentTimeMillis() <= timeout) {
                                //System.out.println(linha);
                                String[] linhaBD = linha.split(";", 3);
                                String registo = linhaBD[2];
                                length += registo.getBytes(StandardCharsets.UTF_8).length;
                                String[] regs = registo.split(" ", 7);
                                Registo r = new Registo(regs[2], Integer.parseInt(regs[3]), regs[1], Integer.parseInt(regs[4]), regs[0], "other");
                                //this.BD.transfZonaLinha(r);
                                rlist.add(r);

                                //envia ao sp que está ok
                                String respTransf = "3;"+ Integer.toString(i) + ";ok";
                                //System.out.println(respTransf);
                                out.println(respTransf);
                                out.flush();
                            }
                            else {
                                //timeout
                                String respErro = "3;"+ Integer.toString(i) + ";timeout";
                                //System.out.println(respErro);
                                out.println(respErro);
                                out.flush();
                                throw new SocketTimeoutException();
                            }
                        }

                        long elapsedTimeMillis = System.currentTimeMillis() - start;

                        socket.shutdownInput();
                        socket.shutdownOutput();
                        socket.close();

                        //transf. zona concluída antes do timeout
                        for (Registo r : rlist) {
                            this.BD.transfZonaLinha(r);
                        }

                        // Logs
                        String dadosEntrada = "SS " + Long.toString(elapsedTimeMillis) + " ms " + Integer.toString(length) + " bytes";
                        Log zt = new Log("ZT", this.servidorPrimario, dadosEntrada);
                        //Log Terminal
                        System.out.println(zt);
                        //Log Ficheiro
                        zt.logToFile(super.getFicheiroLog());
                        //System.out.println("transf zona termina");
                        this.counter = System.currentTimeMillis();
                        sleep(Long.parseLong(this.BD.getSOAREFRESH(super.getTimeOut())));
                    } else {
                        //dominio invalido
                        socket.shutdownInput();
                        socket.shutdownOutput();
                        socket.close();

                        // Logs
                        Log ez = new Log("EZ", this.servidorPrimario, "SS");
                        //Log Terminal
                        System.out.println(ez);
                        //Log Ficheiro
                        ez.logToFile(super.getFicheiroLog());

                        sleep(Long.parseLong(this.BD.getSOAREFRESH(super.getTimeOut())));
                    }
                }
            }
            catch (SocketTimeoutException | SocketException e) {
                try {
                    //System.out.println(this.BD.BDsize());
                    socket.shutdownInput();
                    socket.shutdownOutput();
                    socket.close();

                    //Log timeout
                    Log to = new Log("TO", this.servidorPrimario, "Zone Transfer");
                    //Log Terminal
                    System.out.println(to);
                    //Log Ficheiro
                    to.logToFile(super.getFicheiroLog());
                    sleep(Long.parseLong(this.BD.getSOARETRY(super.getTimeOut())));
                } catch (NullPointerException | InterruptedException | IOException ex) {
                    try {
                        sleep(Long.parseLong(this.BD.getSOARETRY(super.getTimeOut())));
                    } catch (InterruptedException exc) {
                        exc.printStackTrace();
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

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

                long time = System.currentTimeMillis();
                long soaexpire = Long.parseLong(this.BD.getSOAEXPIRE(super.getTimeOut()));
                if(time - this.counter >= soaexpire){
                    this.BD.changeValid(false);
                }
                else {
                    Thread tudp = new Thread(new UDPComm(this.BD,query,ipCliente,portClient,super.getFicheiroLog(),this.getTimeOut()));
                    tudp.start();
                }
            } catch (SocketException ex) {
                System.out.println("Socket error: " + ex.getMessage());
            } catch (IOException ex) {
                System.out.println("I/O error: " + ex.getMessage());
            }

        }

    }

}
