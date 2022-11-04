/* Classe abstrata para servidores */

import java.util.ArrayList;
import java.util.List;

abstract class Servidor {
    private String dominio;
    private String portaAtendimento;
    private String ficheiroLog;
    protected String ficheiroST;
    private List<String> servidoresTopo;

    public Servidor() {
        this.dominio = "";
        this.portaAtendimento = "";
        this.ficheiroLog = "";
        this.servidoresTopo = new ArrayList<>();
    }

    public Servidor(String dominio, String portaAtendimento, String ficheiroLog, String ficheiroST, List<String> servidoresTopo) {
        this.dominio = dominio;
        this.portaAtendimento = portaAtendimento;
        this.ficheiroLog = ficheiroLog;
        this.ficheiroST = ficheiroST;
        this.servidoresTopo = new ArrayList<>();

        for(String st : servidoresTopo){
            this.servidoresTopo.add(st);
        }
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

    public void setFicheiroST(String st) {
        this.ficheiroST = st;
    }

    public String getFicheiroST() {
        return this.ficheiroST;
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


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Servidor servidor = (Servidor) o;
        return (this.dominio.equals(servidor.getDominio()) &&
                this.portaAtendimento.equals(servidor.getPortaAtendimento()) &&
                this.ficheiroLog.equals(servidor.getFicheiroLog()) &&
                this.servidoresTopo.equals(servidor.getServidoresTopo()));

    }

}
