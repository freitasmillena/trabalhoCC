import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Log {
    private String etiqueta;
    private String tipoEntrada;
    private String ip;
    private String dadosLog;

    public Log(){
        this.dadosLog = "";
        this.ip = "";
        this.tipoEntrada = "";
        this.etiqueta = "";
    }
    public Log(String tipoEntrada, String ip, String dadosLog) {
        this.tipoEntrada = tipoEntrada;
        this.ip = ip;
        this.dadosLog = dadosLog;
        SimpleDateFormat formatter = new SimpleDateFormat("dd:MM:yyyy.HH:mm:ss:SS");
        Date date = new Date();
        this.etiqueta = formatter.format(date);
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getTipoEntrada() {
        return tipoEntrada;
    }

    public void setTipoEntrada(String tipoEntrada) {
        this.tipoEntrada = tipoEntrada;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDadosLog() {
        return dadosLog;
    }

    public void setDadosLog(String dadosLog) {
        this.dadosLog = dadosLog;
    }

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
    public void logToFile(String pathFile) throws IOException {
        File logfile = new File(pathFile);
        logfile.createNewFile();
        FileOutputStream fos = new FileOutputStream(logfile, true);
        fos.write(this.toString().getBytes());
        fos.flush();
        fos.close();
    }


}

