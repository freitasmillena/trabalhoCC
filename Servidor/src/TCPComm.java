import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Classe que define a maneira como é efetuada a comunicação entre um Servidor Primário e um Servidor Secundário para a Transferência de Zona, utilizando o protocolo de comunicação TCP.
 * Implementada tendo em conta para uso em Threads (Runnable).
 * 
 * @author Millena Freitas (a97777)
 * @author Guilherme Martins (a92847)
 * @author Vasco Oliveira (a96361)
 */
public class TCPComm implements Runnable{
    private Data bd;
    private Socket socket;
    private String logFile;
    private List<String> SS = new ArrayList<>();
    private int timeout;

    /**
     * Constrtutor de uma estrutura para a comunicação TCP.
     */
    public TCPComm(Data bd, Socket socket, String logFile, List<String> SS, int timeout) {
        this.bd = bd;
        this.socket = socket;
        this.logFile = logFile;
        this.SS = SS;
        this.timeout = timeout;
    }



    /**
     * Executa a classe TCPcom, quando inserida numa Thread.
     */
    public void run(){
        String ipss=null;
        try{
            BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out=new PrintWriter(socket.getOutputStream());


            // Recebeu pedido de transf de zona "1;1;dominio"
            String request=in.readLine();
            long start=System.currentTimeMillis();

            String[]args=request.split(";",3);
            String ipSS=socket.getInetAddress().toString();
            String[]ip=ipSS.split("/",2);
            ipss=ip[1];
            //System.out.println(request);

            //boolean flag=false;

            if(args[2].equals("SOASERIAL")) {
                //sleep(15000);
                String response = "1;1;" + this.bd.getSOASERIAL();
                //System.out.println(response);
                out.println(response);
                out.flush();
            }
            socket.setSoTimeout(this.timeout);
            String res = in.readLine();

            String[]arg= res.split(";",3);
            if(arg[2].equals(this.bd.getdominio())&&this.SS.contains(ip[1])){

                String size=this.bd.BDsize();

                //Envia n de linhas da base de dados "2;1;nlinhas"

                String response="2;1;"+size;
                //System.out.println(response);
                out.println(response);
                out.flush();


                //Recebe n de linhas de volta da base de dados
                String responseLinhas=in.readLine();
                //System.out.println(responseLinhas);
                String[]argsLinha=responseLinhas.split(";",3);


                if(argsLinha[2].equals(size)){
                    //Se for igual, pode enviar linha a linha
                    int numero=1;
                    List<Registo> regs=this.bd.getAllRegistos();

                    int length=0;
                    for(Registo r:regs) {

                        String registo = r.toString();
                        length += registo.getBytes(StandardCharsets.UTF_8).length;

                        //enviar registo
                        String linhaBD = "3;" + Integer.toString(numero) + ";" + registo;

                        out.println(linhaBD);
                        out.flush();
                        //System.out.println(linhaBD);
                        numero++;

                        String check = in.readLine();
                        String[] ok = check.split(";", 3);

                        if(ok[2].equals("timeout")){
                            throw new SocketTimeoutException();
                        }
                    }

                    socket.shutdownInput();
                    socket.shutdownOutput();
                    socket.close();

                    long elapsedTimeMillis=System.currentTimeMillis()-start;
                    String dadosEntrada="SP "+Long.toString(elapsedTimeMillis)+" ms "+Integer.toString(length)+" bytes";
                    // Logs
                    Log zt=new Log("ZT",ip[1],dadosEntrada);
                    //Log Terminal
                    System.out.println(zt);
                    //Log Ficheiro
                    zt.logToFile(this.logFile);
                }
            }
            else{
                String response="1;1;dominioInvalido";
                //System.out.println(response);
                out.println(response);
                out.flush();

                socket.shutdownOutput();
                socket.shutdownInput();
                socket.close();

                // Logs
                Log ez=new Log("EZ",ip[1],"SP");
                //Log Terminal
                System.out.println(ez);
                //Log Ficheiro
                ez.logToFile(this.logFile);
            }
        }
        catch(NullPointerException e) {
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        catch( SocketTimeoutException | SocketException e){
            try{
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();

                // Logs
                Log to=new Log("TO",ipss,"Zone Transfer");
                //Log Terminal
                System.out.println(to);
                //Log Ficheiro
                to.logToFile(this.logFile);
            }catch(NullPointerException | IOException ex){
                ex.printStackTrace();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
