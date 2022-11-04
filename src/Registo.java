public class Registo {
    private String valor;
    private int timetolive;

    public Registo() {
        this.valor = "";
        this.timetolive = 0;
    }

    public Registo(String valor, int timetolive) {
        this.valor = valor;
        this.timetolive = timetolive;
    }

    public Registo(Registo registo) {
        this.valor = registo.getvalor();
        this.timetolive = registo.getTimetolive();
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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registo registo = (Registo) o;
        return (this.valor.equals(registo.getvalor()) &&
                this.timetolive == registo.getTimetolive()
        );
    }

    public Registo clone(){
        return new Registo(this);
    }
}
