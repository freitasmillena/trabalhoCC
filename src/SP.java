import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SP extends Servidor{
    private String ficheiroBD;
    private String segurancaBD;
    private List<String> servidoresSecundarios;

    private Map<String,Registo> BD; // a String corresponde ao Tipo de Valor do Registo
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
    public SP(String dominio, String portaAtendimento, String ficheiroLog, List<String> servidoresTopo, String ficheiroBD, String segurancaBD, List<String> servidoresSecundarios, Map<String, Registo> BD, int versaoBD, String ficheiroST) {
        super(dominio, portaAtendimento, ficheiroLog, servidoresTopo);
        this.ficheiroBD = ficheiroBD;
        this.segurancaBD = segurancaBD;
        this.servidoresSecundarios = new ArrayList<>();
        this.versaoBD = versaoBD;

        for(String st : servidoresSecundarios){
            this.servidoresSecundarios.add(st);
        }

        this.BD = new HashMap<>();

        for(String st: BD.keySet()){
            this.BD.put(st, BD.get(st).clone());
        }
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

    public Map<String, Registo> getBD() {
        Map<String,Registo> res = new HashMap<>();

        for(String st : this.BD.keySet()){
            res.put(st, this.BD.get(st).clone());
        }
        return res;
    }

    public void setBD(Map<String, Registo> BD) {
        this.BD = new HashMap<>();

        for(String st : BD.keySet()){
            this.BD.put(st, BD.get(st).clone());
        }
    }

    public void addRegistoBD(String tipoValor, Registo r) {
        this.BD.put(tipoValor, r);
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

/*

Se calhar é melhor repensar na estrutura do map que guarda as cenas
Talvez um Map<String, List<Registo>> ?
ou Map<String, Map<String,Registo>>?


    public String fetchMail(){

    }

    public String fetchNS(){

    }

    public String fetchA(){

    }
    public String handleQuery(PDU query){
        //se o dominio pedido na query equivale ao dominio tratado pelo SP
        if(query.getName().equals(super.getDominio())){
            //verifica tipo e busca response values
            //busca autoridades
            //busca A

            //devolve resposta
        }
    }



*/


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
                //Log terminal
                System.out.println(query);
                //Log ficheiro
                Log l1 = new Log("QR", ipCliente.toString(), query.ToString());
                l1.logToFile(super.getFicheiroLog());


                //Procurar resposta



                //Enviar
                //String pdu = new PDU
                DatagramPacket sender = new DatagramPacket(pdu.getBytes(), pdu.getBytes().length, ipCliente, portClient);
                DatagramSocket s = new DatagramSocket();
                s.send(sender);
                s.close();

                //Logs resposta
                //System.out.println(pdu);
                //Log l2 = new Log("RP", ipCliente.toString(), pdu);
                //l2.logToFile(super.getFicheiroLog());

            } catch (SocketException ex) {
                System.out.println("Socket error: " + ex.getMessage());
            } catch (IOException ex) {
                System.out.println("I/O error: " + ex.getMessage());
            }

        }
    }

}
