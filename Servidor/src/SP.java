import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

public class SP extends Servidor{
    private String ficheiroBD;
    private String segurancaBD;
    private List<String> servidoresSecundarios;

    private Map<String,List<Registo>> BD; // a String corresponde ao Tipo de Valor do Registo
                                    // à exceção do A em que é igual ao nome do parametro + dominio

    private int versaoBD;


    // Construtor vazio
    public SP() {
        super();
        this.ficheiroBD = "";
        this.segurancaBD = "";
        this.servidoresSecundarios = new ArrayList<>();
        this.BD = new HashMap<>();
        this.versaoBD = 0;
    }


    // Construtor completo
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

    public int getVersaoBD() {
        return versaoBD;
    }

    public void setVersaoBD(int versaoBD) {
        this.versaoBD = versaoBD;
    }

    public String getFicheiroBD() {
        return ficheiroBD;
    }

    public void setFicheiroBD(String ficheiroBD) {
        this.ficheiroBD = ficheiroBD;
    }

    public String getSegurancaBD() {
        return segurancaBD;
    }

    public void setSegurancaBD(String segurancaBD) {
        this.segurancaBD = segurancaBD;
    }

    public List<String> getServidoresSecundarios() {
        List<String> res = new ArrayList<>();
        for(String st : this.servidoresSecundarios) {
            res.add(st);
        }
        return res;
    }

    public void setServidoresSecundarios(List<String> servidoresSecundarios) {
        this.servidoresSecundarios = new ArrayList<>();
        for(String st : servidoresSecundarios) {
            this.servidoresSecundarios.add(st);
        }
    }

    public void addServidorSecundario(String ss) {
        this.servidoresSecundarios.add(ss);
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



    public List<Registo> fetchTag(String tag){
        List<Registo> registos = new ArrayList<>();

        for(Registo r : this.BD.get(tag)){
            registos.add(r.clone());
        }

        return registos;

    }

    public String listString(List<Registo> registos){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < registos.size();i++){
            if(i != 0) sb.append(",");
            sb.append(registos.get(i).toString());
        }
        return sb.toString();
    }

    public Registo fetch(String nome, String tag){
        List<Registo> registos = this.BD.get(tag);
        Registo objetivo = null;

        for(Registo r : registos){
            if(r.getNome().equals(nome)) {objetivo = r.clone();break;}
        }

        return objetivo;
    }


    public String[] fetchExtra(List<Registo> registos){
        List<Registo> res = new ArrayList<>();
        String[] str = new String[2];

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < registos.size();i++){
            if(i != 0) sb.append(",");
            if(!res.contains(registos.get(i))) {
                res.add(registos.get(i).clone());
                sb.append(fetch(registos.get(i).getvalor(), "A"));
            }
        }
        str[0] = sb.toString();
        str[1] = Integer.toString(res.size());

        return str;
    }

    public String handleQuery(PDU query){

            List<Registo> authorities = fetchTag("NS");
            String nAuthorities = Integer.toString(authorities.size());
            String auth = listString(authorities);
            String type = query.getTypeOfValue();
            String nome = query.getName();
            String response = "";
            String extra = "";
            String nValues = "";
            String nExtra = "";

            //A
            if(type.equals("A")){
                response = fetch(nome,"A").toString();
                nValues = "1";
                extra = fetchExtra(authorities)[0];
            }
            else if (type.equals("CNAME")){
                Registo r = fetch(nome, "CNAME");
                response = r.toString();
                nValues = "1";
                authorities.add(r);
                String[] extras = fetchExtra(authorities);
                extra = extras[0];
                nExtra = extras[1];
            }
            else {
                //MX ou NS
                if(!nome.equals(this.getDominio())){
                    Registo r = fetch(nome, nome);
                    response = r.toString();
                    nValues = "1";
                    authorities.add(r);
                    String[] extras = fetchExtra(authorities);
                    extra = extras[0];
                    nExtra = extras[1];
                }
                else {
                    List<Registo> r = fetchTag(type);
                    nValues = Integer.toString(r.size());
                    response = listString(r);
                    for(Registo reg : r) authorities.add(reg);
                    String[] extras = fetchExtra(authorities);
                    extra = extras[0];
                    nExtra = extras[1];
                }

            }

            PDU resposta = new PDU(query.getMessageID(),nome,type, "0", nValues, nAuthorities,nExtra,response,auth,extra);
            return resposta.ToString();
    }


    public void run(){
        System.out.println("Servidor inicializou");
        while (true) {
            byte[] buffer = new byte[256];
            DatagramSocket serverS = null;

            try {
                //Receber
                serverS = new DatagramSocket(5353);
                DatagramPacket receiver = new DatagramPacket(buffer, buffer.length);
                serverS.receive(receiver);
                serverS.close();

                System.out.println("Servidor recebeu");

                int portClient = receiver.getPort();
                InetAddress ipCliente = receiver.getAddress();

                PDU query = new PDU(receiver.getData());

                //Logs query recebida
                Log l1 = new Log("QR", ipCliente.toString(), query.ToString());

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
                Log l2 = new Log("RP", ipCliente.toString(), pdu);
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
