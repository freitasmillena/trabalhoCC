import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class TCPComm implements Runnable{
    private Data bd;
    private Socket socket;
    private String logFile;
    private List<String> SS = new ArrayList<>();

    public TCPComm(Data bd, Socket socket, String logFile, List<String> SS) {
        this.bd = bd;
        this.socket = socket;
        this.logFile = logFile;
        this.SS = SS;
    }


    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            //while(true) {
                // Recebeu pedido de transf de zona "1;1;dominio"
                String request = in.readLine();
                long start = System.currentTimeMillis();

                String[] args = request.split(";", 3);
                String ipSS = socket.getInetAddress().toString();
                String[] ip = ipSS.split("/", 2);

                System.out.println(request);

                boolean flag = false;

                if (args[2].equals("SOASERIAL")) {
                    sleep(40000);
                    String response = "1;1;" + this.bd.getSOASERIAL();
                    System.out.println(response);
                    out.println(response);
                    out.flush();

                    String res = in.readLine();
                    String[] ok = res.split(";", 3);
                    if(ok[2].equals("ok")) flag = true;
                }


             if(flag){
                 String req = in.readLine();
                 String[] arg = req.split(";", 3);
                if (arg[2].equals(this.bd.getdominio()) && this.SS.contains(ip[1])) {

                    String size = this.bd.BDsize();

                    //Envia n de linhas da base de dados "2;1;nlinhas"
                    String response = "2;1;" + size;
                    System.out.println(response);
                    out.println(response);
                    out.flush();


                    //Recebe n de linhas de volta da base de dados
                    String responseLinhas = in.readLine();
                    System.out.println(responseLinhas);
                    String[] argsLinha = responseLinhas.split(";", 3);


                    if (argsLinha[2].equals(size)) {
                        //Se for igual, pode enviar linha a linha
                        int numero = 1;
                        List<Registo> regs = this.bd.getAllRegistos();

                        for (Registo r : regs) {


                            String registo = r.toString();

                            //enviar registo
                            String linhaBD = "3;" + Integer.toString(numero) + ";" + registo;
                            System.out.println(linhaBD);
                            out.println(linhaBD);
                            out.flush();

                            numero++;

                        }
                        long elapsedTimeMillis = System.currentTimeMillis() - start;
                        String dadosEntrada = "SP " + Long.toString(elapsedTimeMillis) + " ms";
                        // Logs
                        Log zt = new Log("ZT", ip[1], dadosEntrada);
                        //Log Terminal
                        System.out.println(zt);
                        //Log Ficheiro
                        zt.logToFile(this.logFile);

                    }

                } else {
                    String response = "1;1;dominioInvalido";
                    System.out.println(response);
                    out.println(response);
                    out.flush();
                    // Logs
                    Log ez = new Log("EZ", ip[1], "SP");
                    //Log Terminal
                    System.out.println(ez);
                    //Log Ficheiro
                    ez.logToFile(this.logFile);
                }
            }
             else {
                 socket.shutdownOutput();
                 socket.shutdownInput();
                 socket.close();
             }
            //}

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
