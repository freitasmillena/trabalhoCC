import java.io.IOException;
import java.net.*;
import java.util.ArrayDeque;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

public class UDPComm implements Runnable{
    private Data bd;
    private PDU query;
    private InetAddress ipCliente;
    private int portClient;
    private String logFile;
    private String timeout;

    public UDPComm(Data d, PDU query, InetAddress ipCliente, int portClient, String logFile, String timeout){
        this.bd = d;
        this.query = query;
        this.ipCliente = ipCliente;
        this.portClient = portClient;
        this.logFile = logFile;
        this.timeout = timeout;
    }

    public void runSR(){
        System.out.println("Dominio: " + bd.getdominio() + " DD: " + bd.getDD());

        byte[] buffer = new byte[2056];

        PDU resposta = null;
        boolean flag = false;

        resposta = this.bd.handleCache(this.query);
        System.out.println("Resposta" + resposta.ToString());

        if(resposta == null){
            System.out.println("Resposta nula");
            Queue<String> ips = new ArrayDeque<>();

            if(this.query.getName().contains(this.bd.getdominio()) && (this.bd.getDD().size() > 0)) {
                for (String s : this.bd.getDD()) {
                    ips.add(s);
                }
            }

            while(true) {
                String ipDD = null;
                DatagramSocket s = null;
                try {
                    ipDD = ips.remove();
                    InetAddress ip = InetAddress.getByName(ipDD);

                    //Enviar
                    DatagramPacket sender = new DatagramPacket(this.query.ToString().getBytes(), this.query.ToString().getBytes().length, ip, 5555);
                    s = new DatagramSocket();
                    s.setSoTimeout(Integer.parseInt(this.timeout));
                    s.send(sender);

                    //Log
                    Log l = new Log("QE", ipDD, this.query.ToString());
                    System.out.println(l);
                    l.logToFile(logFile);

                    //Receber
                    DatagramPacket response = new DatagramPacket(buffer, buffer.length);

                    s.receive(response);
                    resposta = new PDU(response.getData());
                    s.close();

                    //Log
                    Log l2 = new Log("RR", ipDD, resposta.ToString());
                    System.out.println(l2);
                    l2.logToFile(logFile);

                    break;
                } catch (SocketTimeoutException e) {
                    //timeout, tenta pra outro ip DD se tiver

                    // Logs
                    Log to = new Log("TO", ipDD, "Timeout Query");
                    //Log Terminal
                    System.out.println(to);
                    //Log Ficheiro
                    try {
                        to.logToFile(this.logFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (NoSuchElementException e) {
                    //queue is empty, não consegui com DD ou n uso DD para o nome
                    if (!flag) {
                        for (String str : this.bd.getST()) {
                            ips.add(str);
                        }
                        flag = true;
                    } else {
                        assert s != null;
                        s.close();
                        break;
                    }
                } catch (SocketException ex) {
                    System.out.println("Socket error: " + ex.getMessage());
                } catch (IOException ex) {
                    System.out.println("I/O error: " + ex.getMessage());
                }
            }
        }

        flag = false;
       while(resposta != null) {
           System.out.println("Resposta" + resposta.ToString());
           if (resposta.getResponseCode().equals("1") && resposta.getnValues().equals("0")) {
               while (true) {

                   //Adicionar auth e extra à cache
                   List<Registo> auth = resposta.getAuth();
                   List<Registo> extra = resposta.getExtraValues();

                   for(Registo a : auth){
                       a.updateTTL();
                       this.bd.addRegistoBD("NS",a);
                   }

                   for(Registo e : extra){
                       e.updateTTL();
                       this.bd.addRegistoBD("A",e);
                   }


                   Queue<String> ipExtra = new ArrayDeque<>();
                   List<String> extraValues = resposta.getExtraIps();

                   for (String s : extraValues) {
                       ipExtra.add(s);
                   }

                   String ipe = null;
                   DatagramSocket s = null;

                   try {
                       ipe = ipExtra.remove();
                       InetAddress ip = InetAddress.getByName(ipe);

                       //Enviar
                       DatagramPacket sender = new DatagramPacket(this.query.ToString().getBytes(), this.query.ToString().getBytes().length, ip, 5555);
                       s = new DatagramSocket();
                       s.setSoTimeout(Integer.parseInt(this.timeout));
                       s.send(sender);

                       //Log
                       Log l = new Log("QE", ipe, this.query.ToString());
                       System.out.println(l);
                       l.logToFile(logFile);

                       //Receber
                       DatagramPacket response = new DatagramPacket(buffer, buffer.length);

                       s.receive(response);
                       resposta = new PDU(response.getData());
                       s.close();

                       //Log
                       Log l2 = new Log("RR", ipe, resposta.ToString());
                       System.out.println(l2);
                       l2.logToFile(logFile);

                       break;
                   } catch (SocketTimeoutException e) {
                       //timeout, tenta pra outro ip se tiver

                       // Logs
                       Log to = new Log("TO", ipe, "Timeout Query");
                       //Log Terminal
                       System.out.println(to);
                       //Log Ficheiro
                       try {
                           to.logToFile(this.logFile);
                       } catch (IOException ex) {
                           ex.printStackTrace();
                       }
                   } catch (UnknownHostException e) {
                       e.printStackTrace();
                   } catch (NoSuchElementException e) {
                       if (!flag) {
                           for (String str : this.bd.getST()) {
                               ipExtra.add(str);
                           }
                           flag = true;
                       } else {
                           assert s != null;
                           resposta = null;
                           s.close();
                           break;
                       }
                   } catch (SocketException ex) {
                       System.out.println("Socket error: " + ex.getMessage());
                   } catch (IOException ex) {
                       System.out.println("I/O error: " + ex.getMessage());
                   }

               }
           }
           else {
               //enviar resposta ao cliente
               //Enviar
               try {
                   //Adicionar auth e extra à cache
                   List<Registo> auth = resposta.getAuth();
                   List<Registo> extra = resposta.getExtraValues();
                   List<Registo> response = resposta.getResponseValues();

                   for(Registo a : auth){
                       a.updateTTL();
                       this.bd.addRegistoBD("NS",a);
                   }

                   for(Registo e : extra){
                       e.updateTTL();
                       this.bd.addRegistoBD("A",e);
                   }

                   for(Registo r : response){
                       r.updateTTL();
                       this.bd.addRegistoBD(r.getTag(),r);
                   }

                   DatagramPacket sender = new DatagramPacket(resposta.ToString().getBytes(), resposta.ToString().getBytes().length, this.ipCliente, this.portClient);
                   DatagramSocket s = new DatagramSocket();
                   s.send(sender);
                   s.close();


                   String ip = this.ipCliente.toString();
                   String[] args = ip.split("/", 2);

                   //Logs resposta
                   Log l2 = new Log("RP", args[1], resposta.ToString());
                   System.out.println(l2);
                   l2.logToFile(logFile);
		   resposta = null;
               } catch (SocketException ex) {
                   System.out.println("Socket error: " + ex.getMessage());
               } catch (IOException ex) {
                   System.out.println("I/O error: " + ex.getMessage());
               }
           }
       }

    }

    public void run() {
        if(bd.isSR()){
            runSR();
        }
        else {
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
            } catch (SocketException ex) {
                System.out.println("Socket error: " + ex.getMessage());
            } catch (IOException ex) {
                System.out.println("I/O error: " + ex.getMessage());
            }
        }
    }
}
