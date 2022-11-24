import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;

/**
 * Classe SP - de um Servidor Primário (extensão de Servidor)
 */
public class SP extends Servidor{
    // Diretoria do ficheiro de base de dados do servidor primário
    private String ficheiroBD;
    private String segurancaBD;
    private String subDominio;
    // Lista dos Servidores Secundários associados ao Servidor Primário
    private List<String> servidoresSecundarios;

    // Mapa dos registos da base de dados 
    private Map<String,List<Registo>> BD; // a String corresponde ao Tipo de Valor do Registo
                                    // à exceção do A em que é igual ao nome do parametro + dominio
    // Valor da versão da base de dados
    private int versaoBD;


    // Construtor vazio
    /**
     * Construtor vazio para um Servidor Primário
     */
    public SP() {
        super();
        this.ficheiroBD = "";
        this.segurancaBD = "";
        this.servidoresSecundarios = new ArrayList<>();
        this.BD = new HashMap<>();
        this.versaoBD = 0;
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
     * @param versaoBD valor da versão da base de dados
     */
    public SP(String dominio, String timeout, String portaAtendimento, String ficheiroLog, List<String> servidoresTopo, String ficheiroBD, String segurancaBD, List<String> servidoresSecundarios, int versaoBD) {
        super(dominio, portaAtendimento, ficheiroLog, servidoresTopo, timeout);
        this.ficheiroBD = ficheiroBD;
        this.segurancaBD = segurancaBD;
        this.servidoresSecundarios = new ArrayList<>();
        this.versaoBD = versaoBD;

        for(String st : servidoresSecundarios){
            this.servidoresSecundarios.add(st);
        }

        this.BD = new HashMap<>();

    }

    public String getSubDominio() {
        return subDominio;
    }

    public void setSubDominio(String subDominio) {
        this.subDominio = subDominio;
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

    /**
     * Devolve uma cópia da base de dados do SP
     * 
     * @return cópia da base de dados do SP
     */
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
     * Copia a base de dados recebida para o Servidor Primário
     * 
     * @param BD base de dados que queremos copiar
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
     * Adicionar o registo recebido à base de dados do SP
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
     * Veririca se dois servidores primários são iguais
     * 
     * @param o servidor primário que se quer comparar 
     * @return boolean que diz se os dois servidores primários sáo iguais
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SP sp = (SP) o;
        return (super.equals(sp) &&
                this.ficheiroBD.equals(sp.getFicheiroBD()) &&
                this.segurancaBD.equals(sp.getSegurancaBD()) &&
                this.servidoresSecundarios.equals(sp.getServidoresSecundarios()) &&
                this.BD.equals(sp.getBD()));
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

        if(nome.contains(this.subDominio)){
           // response code 0, sem tags, sem authorities
            // response é NS do sub e extra o A do sub
            Registo r = fetch(this.subDominio,this.subDominio);
            response = r.toString();
            nValues = "1";
            nAuthorities = "0";
            auth = "";
            extra = fetch(r.getvalor(), "A").toString();
            nExtra = "1";
        }
        else if(nome.contains(super.getDominio())){
            // response code 0, tag A -> encontrou resposta
            // response code 1, tag A -> n encontrei máquina, sem extra, sem resposta, com NS

            //A
            if(type.equals("A")){
                Registo r = fetch(nome,"A");
                if(r != null){
                    response = r.toString();
                    nValues = "1";

                }
                else{
                    rcode = "1";

                }
                containsAuth(authorities,nome);
                String[] extras = fetchExtra(authorities);
                extra = extras[0];
                nExtra = extras[1];
                tags = "A";
            }
            else if (type.equals("CNAME")){
                Registo r = fetch(nome, "CNAME");
                if(r != null){
                    response = r.toString();
                    nValues = "1";
                    authorities.add(r);
                }
                else {
                    rcode = "1";
                }
                String[] extras = fetchExtra(authorities);
                extra = extras[0];
                nExtra = extras[1];
                tags = "A";
            }
            else {
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
        }
        else { // response code 2, A, sem extra, sem resposta, sem NS
            tags = "A";
            rcode = "2";
            nAuthorities = "0";
            auth = "";

        }

        PDU resposta = new PDU(query.getMessageID(),nome,type, tags, rcode, nValues, nAuthorities,nExtra,response,auth,extra);
        return resposta.ToString();
    }


    /**
     * Método auxiliar para testar como o Servidor Primário responde a possíveis pedidos de Clientes
     */
    public void testing(){

        System.out.println(super.getDominio());
        System.out.println(super.getFicheiroLog());
        System.out.println(super.getTimeOut());
        System.out.println(super.getServidoresTopo());
        System.out.println(this.ficheiroBD);

        for(String s : this.BD.keySet()){
            System.out.println(s + ": ");
            for(Registo r : this.BD.get(s)){
                System.out.println(r);
            }
        }





        /*

        PDU query = new PDU("1234", ".greens.", "MX", "0", "0","0", "0", "", "", "");
        PDU query2 = new PDU("1234", ".greens.", "NS", "0", "0","0", "0", "", "", "");
        PDU query3 = new PDU("1234", "Alicent.greens.", "NS", "0", "0","0", "0", "", "", "");
        PDU query4 = new PDU("1234", "mx1.greens.", "A", "0", "0","0", "0", "", "", "");
        PDU query5 = new PDU("1234", "sp.alicent.greens.", "A", "0", "0","0", "0", "", "", "");
        PDU query6 = new PDU("1234", "ns1.greens.", "A", "0", "0","0", "0", "", "", "");
        PDU query7 = new PDU("1234", "sp.greens.", "CNAME", "0", "0","0", "0", "", "", "");
        PDU query8 = new PDU("1234", "mail1.greens.", "CNAME", "0", "0","0", "0", "", "", "");


        String resposta = handleQuery(query);
        System.out.println(resposta);

        String resposta2 = handleQuery(query2);
        System.out.println(resposta2);

        String resposta3 = handleQuery(query3);
        System.out.println(resposta3);

        String resposta4 = handleQuery(query4);
        System.out.println(resposta4);



        String resposta5 = handleQuery(query5);
        System.out.println(resposta5);

        String resposta6= handleQuery(query6);
        System.out.println(resposta6);

        String resposta7 = handleQuery(query7);
        System.out.println(resposta7);

        String resposta8 = handleQuery(query8);
        System.out.println(resposta8);

        */

    }
    
    /**
     * Método que permite ao Servidor Primário estar sempre à espera de pedidos e de responder aos mesmos pedidos. 
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

    /**
     * Devolve o tamanho da base de dados do servidor (em formato String)
     * 
     * @return tamanho da base de dados do servidor (em formato String)
     */
    public String BDsize(){
        int linhas = 0;
        for(String s : this.BD.keySet()){
            linhas += this.BD.get(s).size();
        }
        return Integer.toString(linhas);
    }

    /**
     * Método responsável por receber um pedido de um Servidor Secundário (SS) para obter uma cópia da base de dados do Servidor Primário e por enviar a cópia para o SS.
     * Simulação da Transferência de Zona quando o SP envia a cópia da sua base de dados para o SS.
     */
    public void transf_zone() {
        try {

            ServerSocket ss = new ServerSocket(24689);

            Socket socket = ss.accept();

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            // Recebeu pedido de transf de zona "1;1;dominio"
            String request = in.readLine();
            long start = System.currentTimeMillis();

            String[] args = request.split(";", 3);
            if(args[2].equals(super.getDominio())){
                String size = BDsize();

                //Envia n de linhas da base de dados "2;1;nlinhas"
                String response = "2;1;" + size;
                out.println(response);
                out.flush();


                //Recebe n de linhas de volta da base de dados
                String responseLinhas = in.readLine();
                String[] argsLinha = responseLinhas.split(";", 3);


                //Não vai receber mais nada do SS
                socket.shutdownInput();


                if(argsLinha[2].equals(size)){
                    //Se for igual, pode enviar linha a linha
                    int numero = 1;
                    for(String st : this.BD.keySet()){
                        List<Registo> list = this.BD.get(st);
                        for(Registo r : list){
                            String registo = r.toString();

                            //enviar registo
                            String linhaBD = "3;" + Integer.toString(numero) + ";" + registo;
                            out.println(linhaBD);
                            out.flush();

                            numero++;
                        }
                    }
                    long elapsedTimeMillis = System.currentTimeMillis()-start;
                    String dadosEntrada = "SP " + Long.toString(elapsedTimeMillis) +" ms";
                    String ipSS = socket.getInetAddress().toString();
                    String[] ip = ipSS.split("/", 2);
                    // Logs
                    Log zt = new Log("ZT", ip[1], dadosEntrada);
                    //Log Terminal
                    System.out.println(zt);
                    //Log Ficheiro
                    zt.logToFile(super.getFicheiroLog());

                }

            }
            else {
                String response = "1;1;dominioInvalido";
                out.println(response);
                out.flush();
                String ipSS = socket.getInetAddress().toString();
                String[] ip = ipSS.split("/", 2);
                // Logs
                Log ez = new Log("EZ", ip[1], "SP");
                //Log Terminal
                System.out.println(ez);
                //Log Ficheiro
                ez.logToFile(super.getFicheiroLog());
            }


            socket.shutdownOutput();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


