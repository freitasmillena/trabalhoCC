import java.util.*;

public class SP extends Servidor{
    private String ficheiroBD;
    private String segurancaBD;
    private List<String> servidoresSecundarios;

    private Map<String,Registo> BD; // a String corresponde ao Tipo de Valor do Registo: 
                                    // SOASP, SOAADMIN, SOASERIAL, SOAREFRESH, SOARETRY, SOAEXPIRE
    // o mesmo não é feito para alguns Tipos de Valores: 
    private List<Registo> listaNS;
    private List<Registo> listaMX;

    private int versaoBD;


    // Construtor vazio
    public SP() {
        super();
        this.ficheiroBD = "";
        this.segurancaBD = "";
        this.servidoresSecundarios = new ArrayList<>();
        this.BD = new HashMap<>();
        this.listaNS = new ArrayList<>();
        this.listaMX = new ArrayList<>();
        this.versaoBD = 0;
    }


    // Construtor completo
    public SP(String dominio, String portaAtendimento, String ficheiroLog, List<String> servidoresTopo, String ficheiroBD, String segurancaBD, List<String> servidoresSecundarios, Map<String, Registo> BD, int versaoBD, List<Registo> listaNS, List<Registo> listaMX, String ficheiroST) {
        super(dominio, portaAtendimento, ficheiroLog, servidoresTopo);
        this.ficheiroBD = ficheiroBD;
        this.segurancaBD = segurancaBD;
        this.servidoresSecundarios = new ArrayList<>();
        this.versaoBD = versaoBD;

        for(String st : servidoresSecundarios){
            this.servidoresSecundarios.add(st);
        }

        this.BD = new HashMap<>();

        for(String st: BD.keySet()){
            this.BD.put(st, BD.get(st).clone());
        }

        this.listaNS = new ArrayList<>();
        for(Registo r: listaNS){
            this.listaNS.add(r.clone());
        }
        
        this.listaMX = new ArrayList<>();
        for(Registo r: listaMX){
            this.listaMX.add(r.clone());
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

    public void addServidorSecundario(String ss) {
        this.servidoresSecundarios.add(ss);
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

    public void addRegistoBD(String tipoValor, Registo r) {
        this.BD.put(tipoValor, r);
    }

    public void addRegistoNS(Registo r) {
        this.listaNS.add(r);
    }

    public List<Registo> getListaNS() {
        List<Registo> listaNS = new ArrayList<>();

        for(Registo r : this.listaNS) {
            listaNS.add(r.clone());
        }
        return listaNS;
    }

    public void addRegistoMX(Registo r) {
        this.listaMX.add(r);
    }

    public List<Registo> getListaMX() {
        List<Registo> listaMX = new ArrayList<>();

        for(Registo r : this.listaMX) {
            listaMX.add(r.clone());
        }
        return listaMX;
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
