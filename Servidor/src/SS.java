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

    @Override
    public void run() {
        System.out.println("sou um SS e ainda n tenho run");
    }

}
