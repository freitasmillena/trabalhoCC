import java.util.Objects;

public class Registo {
    private String ip;
    private int timetolive;

    public Registo() {
        this.ip = "";
        this.timetolive = 0;
    }

    public Registo(String ip, int timetolive) {
        this.ip = ip;
        this.timetolive = timetolive;
    }

    public Registo(Registo registo) {
        this.ip = registo.getIp();
        this.timetolive = registo.getTimetolive();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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
        return (this.ip.equals(registo.getIp()) &&
                this.timetolive == registo.getTimetolive()
        );
    }

    public Registo clone(){
        return new Registo(this);
    }
}
