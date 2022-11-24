import java.util.*;

/**
 * Claase SR - Servidor de Resolução
 */
public class SR extends Servidor{
    private List<String> servidoresDNS;
    private Map<String, Registo> cache;

    public SR() {
        super();
        this.servidoresDNS = new ArrayList<>();
        this.cache = new HashMap<>();
    }

    public SR(String dominio, String timeout, String portaAtendimento, String ficheiroLog, List<String> servidoresTopo, List<String> servidoresDNS, Map<String, Registo> cache) {
        super(dominio, portaAtendimento, ficheiroLog, servidoresTopo, timeout);
        this.servidoresDNS = new ArrayList<>();
        for(String dns : servidoresDNS){
            this.servidoresDNS.add(dns);
        }
        this.cache = new HashMap<>();
        for(String st : cache.keySet()){
            this.cache.put(st, cache.get(st).clone());
        }
    }

    public List<String> getServidoresDNS() {
        List<String> res = new ArrayList<>();
        for(String st : this.servidoresDNS){
            res.add(st);
        }
        return res;
    }

    public void setServidoresDNS(List<String> servidoresDNS) {
        this.servidoresDNS = new ArrayList<>();

        for(String st : servidoresDNS){
            this.servidoresDNS.add(st);
        }
    }

    public Map<String, Registo> getCache() {
        Map<String, Registo> res = new HashMap<>();

        for(String st : this.cache.keySet()){
            res.put(st, this.cache.get(st).clone());
        }
        return res;
    }

    public void setCache(Map<String, Registo> cache) {
        this.cache = new HashMap<>();

        for(String st : cache.keySet()){
            this.cache.put(st, cache.get(st).clone());
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SR sr = (SR) o;
        return (super.equals(sr) &&
                this.servidoresDNS.equals(sr.getServidoresDNS()) &&
                this.cache.equals(sr.getCache())
        );
    }

    @Override
    public void transf_zone() {

    }

    @Override
    public void run() {
        System.out.println("sou um SR e ainda n tenho run");
    }

}
