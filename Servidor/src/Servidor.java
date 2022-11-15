/* Classe abstrata para servidores */

import java.util.ArrayList;
import java.util.List;

abstract class Servidor {
    private String dominio;
    private String portaAtendimento;
    private String ficheiroLog;
    private String timeOut;
    private List<String> servidoresTopo;

    public Servidor() {
        this.dominio = "";
        this.portaAtendimento = "";
        this.ficheiroLog = "";
        this.servidoresTopo = new ArrayList<>();
        this.timeOut = "";
    }

    public Servidor(String dominio, String portaAtendimento, String ficheiroLog, List<String> servidoresTopo, String timeout) {
        this.dominio = dominio;
        this.portaAtendimento = portaAtendimento;
        this.ficheiroLog = ficheiroLog;
        this.servidoresTopo = new ArrayList<>();
        this.timeOut = timeout;

        for(String st : servidoresTopo){
            this.servidoresTopo.add(st);
        }
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public String getPortaAtendimento() {
        return portaAtendimento;
    }

    public void setPortaAtendimento(String portaAtendimento) {
        this.portaAtendimento = portaAtendimento;
    }

    public String getFicheiroLog() {
        return ficheiroLog;
    }

    public void setFicheiroLog(String ficheiroLog) {
        this.ficheiroLog = ficheiroLog;
    }

    public List<String> getServidoresTopo() {
        List<String> res = new ArrayList<>();
        for(String st : this.servidoresTopo) {
            res.add(st);
        }
        return res;
    }

    public void setServidoresTopo(List<String> servidoresTopo) {
        this.servidoresTopo = new ArrayList<>();

        for(String st : servidoresTopo){
            this.servidoresTopo.add(st);
        }
    }

    public void assServidorTopo(String st){
        this.servidoresTopo.add(st);
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Servidor servidor = (Servidor) o;
        return (this.dominio.equals(servidor.getDominio()) &&
                this.timeOut.equals(servidor.getTimeOut()) &&
                this.portaAtendimento.equals(servidor.getPortaAtendimento()) &&
                this.ficheiroLog.equals(servidor.getFicheiroLog()) &&
                this.servidoresTopo.equals(servidor.getServidoresTopo()));

    }


public abstract void run();
}
