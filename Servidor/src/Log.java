import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe Log
 * 
 * @author Millena Freitas (a97777) - 10%
 * @author Guilherme Martins (a92847) - 10%
 * @author Vasco Oliveira (a96361) - 80%
 */
public class Log {
    // Data e hora completa do sistema operativo na altura em que aconteceu a atividade registada
    private String etiqueta;
    // Tipo de entrada (QR, QE, RP, RR, ZT, EZ)
    private String tipoEntrada;
    // Endereço IP do servidor associado
    private String ip;
    // Dados de entrada do Log
    private String dadosLog;

    /**
     * 
     */
    public Log(){
        this.dadosLog = "";
        this.ip = "";
        this.tipoEntrada = "";
        this.etiqueta = "";
    }

    /**
     * Construtor de um Log a partir dos dados recebidos
     * 
     * @param tipoEntrada tipo de entrada (QR, QE, RP, RR, ZT, EZ)
     * @param ip endereço IP
     * @param dadosLog Dados de entrada do Log
     */
    public Log(String tipoEntrada, String ip, String dadosLog) {
        this.tipoEntrada = tipoEntrada;
        this.ip = ip;
        this.dadosLog = dadosLog;
        SimpleDateFormat formatter = new SimpleDateFormat("dd:MM:yyyy.HH:mm:ss:SS");
        Date date = new Date();
        this.etiqueta = formatter.format(date);
    }

    /**
     * Devolve etiqueta do log
     * 
     * @return etiqueta do log
     */
    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Define a etiqueta do log
     * 
     * @param etiqueta etiqueta recebida
     */
    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /**
     * Devolve o tipo de entrada do log
     * 
     * @return tipo de entrada do log
     */
    public String getTipoEntrada() {
        return tipoEntrada;
    }

    /**
     * Define o tipo de entrada do log
     * 
     * @param tipoEntrada tipo de entrada recebida
     */
    public void setTipoEntrada(String tipoEntrada) {
        this.tipoEntrada = tipoEntrada;
    }

    /**
     * Devolve o Endereço IP do servidor associado ao log
     * 
     * @return Endereço IP do servidor associado
     */
    public String getIp() {
        return ip;
    }

    /**
     * Define o Endereço IP do servidor associado do log
     * 
     * @param ip Endereço IP do servidor recebido
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Devolve os Dados de entrada do Log
     * 
     * @return Dados de entrada do Log
     */
    public String getDadosLog() {
        return dadosLog;
    }

    /**
     * Define os Dados de entrada do Log
     * 
     * @param dadosLog Dados de entrada recebidas
     */
    public void setDadosLog(String dadosLog) {
        this.dadosLog = dadosLog;
    }

    /**
     * Verifica se dois Logs são iguais
     * 
     * @param o log que se quer comparar
     * @return boolean a dizer se os dois logs são iguais
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Log log = (Log) o;
        return (this.etiqueta.equals(log.getEtiqueta()) &&
                this.dadosLog.equals(log.getDadosLog()) &&
                this.ip.equals(log.getIp()) &&
                this.tipoEntrada.equals(log.getTipoEntrada())
        );
    }

    /**
     * Devolve uma string compactada com todos os campos do Log
     * 
     * @return string compactada do Log
     */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.etiqueta)
                .append(" ")
                .append(this.tipoEntrada)
                .append(" ")
                .append(this.ip)
                .append(" ")
                .append(this.dadosLog)
                .append("\n");
        return sb.toString();
    }

    /**
     * Método que adiciona um registo Log no ficheiro que contém todos os Logs já criados
     * 
     * @param pathFile diretoria para criar o ficheiro de Logs
     * @throws IOException lança erro se não conseguir criar o ficheiro desejado
     */
    public void logToFile(String pathFile) throws IOException {
        File logfile = new File(pathFile);
        logfile.createNewFile();
        FileOutputStream fos = new FileOutputStream(logfile, true);
        fos.write(this.toString().getBytes());
        fos.flush();
        fos.close();
    }


}

