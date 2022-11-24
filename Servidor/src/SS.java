import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe SS - Servidor Secundário
 */
public class SS extends Servidor{
    // Servidor primário associado ao Servidor secundário
    private String servidorPrimario;
    private String segurancaSP;
    private String subDominio;
    // Cópia da Base de Dados do respetivo Servidor Primário
    private Map<String, List<Registo>> BD; // cópia da BD do respetivo SP
    // Versão da base de dados
    private int versaoBD;

    /**
     * Construtor vazio para um Servidor Secundário
     */
    public SS() {
        super();
        this.servidorPrimario = "";
        this.segurancaSP = "";
        this.BD = new HashMap<>();
        this.versaoBD = 0;
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
     * @param versaoBD versão da base de dados do SP
     */
    public SS(String dominio, String timeout, String portaAtendimento, String ficheiroLog, List<String> servidoresTopo, String servidorPrimario, String segurancaSP, int versaoBD) {
        super(dominio, portaAtendimento, ficheiroLog, servidoresTopo, timeout);
        this.servidorPrimario = servidorPrimario;
        this.segurancaSP = segurancaSP;
        this.versaoBD = versaoBD;

        this.BD = new HashMap<>();

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

    /**
     * Devolve o inteiro com a versão da base de dados atual do servidor primário
     * 
     * @return inteiro com a versão da base de dados atual do servidor primário
     */    
    public int getVersaoBD() {
        return versaoBD;
    }

    /**
     * Define o valor da versão da base de dados
     * 
     * @param versaoBD valor da versão da base de dados
     */
    public void setVersaoBD(int versaoBD) {
        this.versaoBD = versaoBD;
    }

    public Map<String, List<Registo>> getBD() {
        Map<String,List<Registo>> res = new HashMap<>();

        for(String st : this.BD.keySet()){
            List<Registo> registos = new ArrayList<>();
            for(Registo r : this.BD.get(st) ){
                registos.add(r.clone());
            }
            res.put(st,registos);

        }
        return res;
    }

    /**
     * Devolve uma cópia da base de dados do SP
     * 
     * @return cópia da base de dados do SP
     */
    public void setBD(Map<String, List<Registo>> BD) {
        this.BD = new HashMap<>();

        for(String st : BD.keySet()){
            List<Registo> registos = new ArrayList<>();
            for(Registo r : BD.get(st) ){
                registos.add(r.clone());
            }
            this.BD.put(st,registos);
        }
    }

    /**
     * Veririca se dois servidores secundários são iguais
     * 
     * @param o servidor primário que se quer comparar 
     * @return boolean que diz se os dois servidores secundários sáo iguais
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SS ss = (SS) o;
        return (super.equals(ss) &&
                this.servidorPrimario.equals(ss.getServidorPrimario()) &&
                this.segurancaSP.equals(ss.getSegurancaSP()) &&
                this.versaoBD == ss.getVersaoBD() &&
                this.BD.equals(ss.getBD())
                );

    }

    /**
     * Recebendo a tag, devolve a lista dos registos que possuem a respetiva tag
     * 
     * @param tag tag dos registos que o métood tem de devolver
     * @return lista dos registos com a tag recebida
     */
    public List<Registo> fetchTag(String tag){
        List<Registo> registos = new ArrayList<>();

        for(Registo r : this.BD.get(tag)){
            registos.add(r.clone());
        }

        return registos;

    }

    /**
     * Converte uma lista de registos numa string compactada
     * 
     * @param registos lista de registos
     * @return string compacta com todos os registos nela
     */
    public String listString(List<Registo> registos){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < registos.size();i++){
            if(i != 0) sb.append(",");
            sb.append(registos.get(i).toString());
        }
        return sb.toString();
    }

    /**
     * Devolve o registo com o mesmo nome e tag
     * 
     * @param nome nome do registo desejado
     * @param tag tag do registo desejado
     * @return registo com o nome e a tag pedidos 
     */    
    public Registo fetch(String nome, String tag){
        List<Registo> registos = this.BD.get(tag);
        Registo objetivo = null;

        for(Registo r : registos){
            if(tag.equals("CNAME")){
                if(r.getvalor().equals(nome)) {objetivo = r.clone();break;}
            }
            else {
                if(r.getNome().equals(nome)) {objetivo = r.clone();break;}
            }

        }

        return objetivo;
    }

    /**
     * Método com o objetivo de devolver os registos para ExtraValues, mas garantindo que não registos repetidos na string final
     * 
     * @param registos registos que queremos avaliar e adicionar na string final dos ExtraValues
     * @return string compactada com os todos registos que se deverão adicionar no campo ExtraValues
     */
    public String[] fetchExtra(List<Registo> registos){
        List<Registo> res = new ArrayList<>();
        String[] str = new String[2];

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < registos.size();i++){
            Registo r = fetch(registos.get(i).getvalor(), "A");
            if(!res.contains(r)) {
                res.add(r.clone());
                if(i != 0) sb.append(",");
                sb.append(r);
            }
        }
        str[0] = sb.toString();
        str[1] = Integer.toString(res.size());

        return str;
    }

    public void containsAuth(List<Registo> list, String nome){

        for(Registo r : list){
            if(r.getvalor().equals(nome)) {
                list.remove(r);
                break;
            }
        }


    }

    /**
     * Método responsável por receber uma query de um cliente e por criar o PDU de resposta para o cliente.
     * 
     * @param query PDU recebida a partir de um cliente
     * @return query de resposta do SP para o cliente
     */
    public String handleQuery(PDU query){

        List<Registo> authorities = fetchTag("NS");
        String nAuthorities = Integer.toString(authorities.size());
        String auth = listString(authorities);
        String type = query.getTypeOfValue();
        String nome = query.getName();
        String response = "";
        String extra = "";
        String nValues = "0";
        String nExtra = "0";
        String tags = "";
        String rcode = "0";

        // type of value inválido
        if(!query.getTypes().contains(type)){
            tags = "A";
            rcode = "3";
            nAuthorities = "0";
            auth = "";
        }
        else {
            if (nome.contains(this.subDominio)) {
                // response code 0, sem tags, sem authorities
                // response é NS do sub e extra o A do sub
                Registo r = fetch(this.subDominio, this.subDominio);
                response = r.toString();
                nValues = "1";
                nAuthorities = "0";
                auth = "";
                extra = fetch(r.getvalor(), "A").toString();
                nExtra = "1";
            } else if (nome.contains(super.getDominio())) {
                // response code 0, tag A -> encontrou resposta
                // response code 1, tag A -> n encontrei máquina, sem extra, sem resposta, com NS

                //A
                if (type.equals("A")) {
                    Registo r = fetch(nome, "A");
                    if (r != null) {
                        response = r.toString();
                        nValues = "1";

                    } else {
                        rcode = "1";

                    }
                    containsAuth(authorities, nome);
                    String[] extras = fetchExtra(authorities);
                    extra = extras[0];
                    nExtra = extras[1];
                    tags = "A";
                } else if (type.equals("CNAME")) {
                    Registo r = fetch(nome, "CNAME");
                    if (r != null) {
                        response = r.toString();
                        nValues = "1";
                        authorities.add(r);
                    } else {
                        rcode = "1";
                    }
                    String[] extras = fetchExtra(authorities);
                    extra = extras[0];
                    nExtra = extras[1];
                    tags = "A";
                } else {
                    //MX ou NS
                    List<Registo> r = fetchTag(type);
                    nValues = Integer.toString(r.size());
                    response = listString(r);
                    for (Registo reg : r) authorities.add(reg);
                    String[] extras = fetchExtra(authorities);
                    extra = extras[0];
                    nExtra = extras[1];
                    tags = "A";

                }
            } else { // response code 2, A, sem extra, sem resposta, sem NS
                tags = "A";
                rcode = "2";
                nAuthorities = "0";
                auth = "";

            }
        }
        PDU resposta = new PDU(query.getMessageID(),nome,type, tags, rcode, nValues, nAuthorities,nExtra,response,auth,extra);
        return resposta.ToString();
    }




    /**
     * Método que permite ao Servidor Secundário estar sempre à espera de pedidos e de responder aos mesmos pedidos. 
     */
    public void transf_zone() {

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
                    transfZonaLinha(r);
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
     * Adicionar o registo recebido à base de dados do S
     * 
     * @param tipoValor valor do registo, para inserir no sítio correto da base de dados
     * @param r registo que se deseja adicionar
     */
    public void addRegistoBD(String tipoValor, Registo r) {
        if(this.BD.containsKey(tipoValor)){
            this.BD.get(tipoValor).add(r.clone());
        }
        else{
            List<Registo> reg = new ArrayList<>();
            reg.add(r.clone());
            this.BD.put(tipoValor,reg);
        }
    }

    /**
     * Método Auxiliar para adicionar um registo da base de dados do SP no Servidor secundário
     * @param r registo que se quer adicionar
     */
    public void transfZonaLinha(Registo r){
        if(  (!super.getDominio().equals(r.getNome()) )  && r.getTag().equals("NS")){
            addRegistoBD(r.getNome(), r.clone());
            this.subDominio = r.getNome();
        }
        else {
            addRegistoBD(r.getTag(), r.clone());
        }

    }

    /**
     * Método usado para permitir o Servidor Secundário receber queries e responder a elas.
     */
    public void run(){


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


                //handle Query
                String pdu = handleQuery(query);

                //Enviar
                DatagramPacket sender = new DatagramPacket(pdu.getBytes(), pdu.getBytes().length, ipCliente, portClient);
                DatagramSocket s = new DatagramSocket();
                s.send(sender);
                s.close();

                //Logs resposta
                Log l2 = new Log("RP", args[1], pdu);
                System.out.println(l2);
                l2.logToFile(super.getFicheiroLog());

            } catch (SocketException ex) {
                System.out.println("Socket error: " + ex.getMessage());
            } catch (IOException ex) {
                System.out.println("I/O error: " + ex.getMessage());
            }

        }



    }

}
