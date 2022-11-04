import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SS extends Servidor{
    private String servidorPrimario;
    private String segurancaSP;
    private Map<String,Registo> BD; // cópia da BD do respetivo SP
    private int versaoBD;

    public SS() {
        super();
        this.servidorPrimario = "";
        this.segurancaSP = "";
        this.BD = new HashMap<>();
        this.versaoBD = 0;
    }

    public SS(String dominio, String portaAtendimento, String ficheiroLog, String ficheiroST, List<String> servidoresTopo, String servidorPrimario, String segurancaSP, Map<String, Registo> BD, int versaoBD) {
        super(dominio, portaAtendimento, ficheiroLog, ficheiroST, servidoresTopo);
        this.servidorPrimario = servidorPrimario;
        this.segurancaSP = segurancaSP;
        this.versaoBD = versaoBD;

        this.BD = new HashMap<>();

        for(String st : BD.keySet()){
            this.BD.put(st, BD.get(st).clone());
        }
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

}
