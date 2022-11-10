public class Registo {
    private String valor;
    private int timetolive;
    private String tag;
    private int prioridade;

    public Registo() {
        this.valor = "";
        this.timetolive = 0;
        this.tag = "";
        this.prioridade = 0;
    }

    public Registo(String valor, int timetolive, String tag, int prioridade) {
        this.valor = valor;
        this.timetolive = timetolive;
        this.tag = tag;
        this.prioridade = prioridade; 
    }

    public Registo(Registo registo) {
        this.valor = registo.getvalor();
        this.timetolive = registo.getTimetolive();
        this.tag = registo.getTag();
    }

    public String getvalor() {
        return valor;
    }

    public void setvalor(String valor) {
        this.valor = valor;
    }

    public int getTimetolive() {
        return timetolive;
    }

    public void setTimetolive(int timetolive) {
        this.timetolive = timetolive;
    }

    public String getTag(){
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getPrioridade() {
        return this.prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registo registo = (Registo) o;
        return (this.valor.equals(registo.getvalor()) &&
                this.timetolive == registo.getTimetolive() &&
                this.prioridade == registo.getPrioridade()
        );
    }

    public Registo clone(){
        return new Registo(this);
    }
}
