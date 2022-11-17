import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SS extends Servidor{
    private String servidorPrimario;
    private String segurancaSP;
    private Map<String, List<Registo>> BD; // cópia da BD do respetivo SP
    private int versaoBD;

    public SS() {
        super();
        this.servidorPrimario = "";
        this.segurancaSP = "";
        this.BD = new HashMap<>();
        this.versaoBD = 0;
    }

    public SS(String dominio, String timeout, String portaAtendimento, String ficheiroLog, List<String> servidoresTopo, String servidorPrimario, String segurancaSP, int versaoBD) {
        super(dominio, portaAtendimento, ficheiroLog, servidoresTopo, timeout);
        this.servidorPrimario = servidorPrimario;
        this.segurancaSP = segurancaSP;
        this.versaoBD = versaoBD;

        this.BD = new HashMap<>();

    }

    public String getServidorPrimario() {
        return servidorPrimario;
    }

    public void setServidorPrimario(String servidorPrimario) {
        this.servidorPrimario = servidorPrimario;
    }

    public String getSegurancaSP() {
        return segurancaSP;
    }

    public void setSegurancaSP(String segurancaSP) {
        this.segurancaSP = segurancaSP;
    }

    public int getVersaoBD() {
        return versaoBD;
    }

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
            if(tag.equals("CNAME")){
                if(r.getvalor().equals(nome)) {objetivo = r.clone();break;}
            }
            else {
                if(r.getNome().equals(nome)) {objetivo = r.clone();break;}
            }

        }

        return objetivo;
    }


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

    @Override
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

    public void transfZonaLinha(Registo r){
        if(  (!super.getDominio().equals(r.getNome()) )  && r.getTag().equals("NS")){
            addRegistoBD(r.getNome(), r.clone());
        }
        else {
            addRegistoBD(r.getTag(), r.clone());
        }

    }

    public void run(){


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
