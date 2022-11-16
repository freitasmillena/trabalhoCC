public class Registo {
    private String valor;
    private int timetolive;
    private String tag;
    private int prioridade;
    private String nome;

    public Registo() {
        this.valor = "";
        this.timetolive = 0;
        this.tag = "";
        this.prioridade = 0;
        this.nome = "";
    }

    public Registo(String valor, int timetolive, String tag, int prioridade, String nome) {
        this.valor = valor;
        this.timetolive = timetolive;
        this.tag = tag;
        this.prioridade = prioridade;
        this.nome = nome;
    }

    public Registo(Registo registo) {
        this.valor = registo.getvalor();
        this.timetolive = registo.getTimetolive();
        this.tag = registo.getTag();
        this.prioridade = registo.getPrioridade();
        this.nome = registo.getNome();
    }

    public Registo(byte[] data){
        String msg =  new String(data).trim();
        String[] arrOfStr = msg.split(" ", 5);
        this.nome = arrOfStr[0];
        this.tag = arrOfStr[1];
        this.valor = arrOfStr[2];
        this.timetolive = Integer.parseInt(arrOfStr[3]);
        this.prioridade = Integer.parseInt(arrOfStr[4]);
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
                this.tag.equals(registo.getTag()) &&
                this.timetolive == registo.getTimetolive() &&
                this.prioridade == registo.getPrioridade() &&
                this.nome.equals(registo.getNome())
        );
    }

    public Registo clone(){
        return new Registo(this);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.nome)
                .append(" ")
                .append(this.tag)
                .append(" ")
                .append(this.valor)
                .append(" ")
                .append(this.timetolive)
                .append(" ")
                .append(this.prioridade);
       return sb.toString();
    }
}
