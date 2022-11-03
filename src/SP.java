import java.util.*;

public class SP extends Servidor{
    private String ficheiroBD;
    private String segurancaBD;
    private List<String> servidoresSecundarios;
    private Map<String,Registo> BD;
    private int versaoBD;


    // Construtor vazio
    public SP() {
        super();
        this.ficheiroBD = "";
        this.segurancaBD = "";
        this.servidoresSecundarios = new ArrayList<>();
        this.BD = new HashMap<>();
        this.versaoBD = 0;
    }

    // Construtor completo
    public SP(String dominio, String portaAtendimento, String ficheiroLog, List<String> servidoresTopo, String ficheiroBD, String segurancaBD, List<String> servidoresSecundarios, Map<String, Registo> BD, int versaoBD) {
        super(dominio, portaAtendimento, ficheiroLog, servidoresTopo);
        this.ficheiroBD = ficheiroBD;
        this.segurancaBD = segurancaBD;
        this.servidoresSecundarios = new ArrayList<>();
        this.versaoBD = versaoBD;

        for(String st : servidoresSecundarios){
            this.servidoresSecundarios.add(st);
        }

        this.BD = new HashMap<>();

        for(String st : BD.keySet()){
            this.BD.put(st, BD.get(st).clone());
        }
    }

    public int getVersaoBD() {
        return versaoBD;
    }

    public void setVersaoBD(int versaoBD) {
        this.versaoBD = versaoBD;
    }

    public String getFicheiroBD() {
        return ficheiroBD;
    }

    public void setFicheiroBD(String ficheiroBD) {
        this.ficheiroBD = ficheiroBD;
    }

    public String getSegurancaBD() {
        return segurancaBD;
    }

    public void setSegurancaBD(String segurancaBD) {
        this.segurancaBD = segurancaBD;
    }

    public List<String> getServidoresSecundarios() {
        List<String> res = new ArrayList<>();
        for(String st : this.servidoresSecundarios) {
            res.add(st);
        }
        return res;
    }

    public void setServidoresSecundarios(List<String> servidoresSecundarios) {
        this.servidoresSecundarios = new ArrayList<>();
        for(String st : servidoresSecundarios) {
            this.servidoresSecundarios.add(st);
        }
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
        SP sp = (SP) o;
        return (super.equals(sp) &&
                this.ficheiroBD.equals(sp.getFicheiroBD()) &&
                this.segurancaBD.equals(sp.getSegurancaBD()) &&
                this.servidoresSecundarios.equals(sp.getServidoresSecundarios()) &&
                this.BD.equals(sp.getBD()));
    }

}
