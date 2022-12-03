import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class TCPComm implements Runnable{
    private Data bd;
    private Socket socket;
    private String logFile;

    public TCPComm(Data bd, Socket socket, String logFile) {
        this.bd = bd;
        this.socket = socket;
        this.logFile = logFile;
    }


    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            // Recebeu pedido de transf de zona "1;1;dominio"
            String request = in.readLine();
            long start = System.currentTimeMillis();

            String[] args = request.split(";", 3);
            if (args[2].equals(this.bd.getdominio())) {
                String size = this.bd.BDsize();

                //Envia n de linhas da base de dados "2;1;nlinhas"
                String response = "2;1;" + size;
                out.println(response);
                out.flush();


                //Recebe n de linhas de volta da base de dados
                String responseLinhas = in.readLine();
                String[] argsLinha = responseLinhas.split(";", 3);


                //Não vai receber mais nada do SS
                socket.shutdownInput();


                if (argsLinha[2].equals(size)) {
                    //Se for igual, pode enviar linha a linha
                    int numero = 1;
                    List<Registo> regs = this.bd.getAllRegistos();

                    for (Registo r : regs) {


                        String registo = r.toString();

                        //enviar registo
                        String linhaBD = "3;" + Integer.toString(numero) + ";" + registo;
                        out.println(linhaBD);
                        out.flush();

                        numero++;

                    }
                    long elapsedTimeMillis = System.currentTimeMillis() - start;
                    String dadosEntrada = "SP " + Long.toString(elapsedTimeMillis) + " ms";
                    String ipSS = socket.getInetAddress().toString();
                    String[] ip = ipSS.split("/", 2);
                    // Logs
                    Log zt = new Log("ZT", ip[1], dadosEntrada);
                    //Log Terminal
                    System.out.println(zt);
                    //Log Ficheiro
                    zt.logToFile(this.logFile);

                }

            } else {
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
                ez.logToFile(this.logFile);
            }

            socket.shutdownOutput();
            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
