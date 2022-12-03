import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Data {

    private static Data single_instance = null;
    private Map<String, List<Registo>> BD;
    private int versaoBD;
    private String dominio;
    private String subdominio;
    private ReentrantReadWriteLock l = new ReentrantReadWriteLock();

    private Data(String dominio){
        this.BD = new HashMap<>();
        this.dominio = dominio;
    }

    public void setVersaoBD(int versaoBD) {
        this.versaoBD = versaoBD;
    }

    public void setSubdominio(String subdominio) {
        this.subdominio = subdominio;
    }

    public String getdominio() {
        return dominio;
    }

    public String getSubdominio() {
        return subdominio;
    }

    public static Data getInstance(String dominio){
        if(single_instance == null) single_instance = new Data(dominio);
        return single_instance;
    }

    /**
     * Adicionar o registo recebido à base de dados do SP
     *
     * @param tipoValor valor do registo, para inserir no sítio correto da base de dados
     * @param r registo que se deseja adicionar
     */
    public void addRegistoBD(String tipoValor, Registo r) {
        this.l.writeLock().lock();
        try {
            if (this.BD.containsKey(tipoValor)) {
                this.BD.get(tipoValor).add(r.clone());
            } else {
                List<Registo> reg = new ArrayList<>();
                reg.add(r.clone());
                this.BD.put(tipoValor, reg);
            }
        }
        finally {
            this.l.writeLock().unlock();
        }
    }

    /**
     * Recebendo a tag, devolve a lista dos registos que possuem a respetiva tag
     *
     * @param tag tag dos registos que o método tem de devolver
     * @return lista dos registos com a tag recebida
     */
    public List<Registo> fetchTag(String tag){
        List<Registo> registos = new ArrayList<>();
        this.l.readLock().lock();
        try{
            for(Registo r : this.BD.get(tag)){
                registos.add(r.clone());
            }

            return registos;
        }
        finally {
            this.l.readLock().unlock();
        }

    }

    /**
     * Converte uma lista de registos numa string compactada
     *
     * @param registos lista de registos
     * @return string compacta com todos os registos nela
     */
    public String listString(List<Registo> registos){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < registos.size();i++){
            if(i != 0) sb.append(",");
            sb.append(registos.get(i).toString());
        }
        return sb.toString();
    }

    /**
     * Devolve o registo com o mesmo nome e tag
     *
     * @param nome nome do registo desejado
     * @param tag tag do registo desejado
     * @return registo com o nome e a tag pedidos
     */
    public Registo fetch(String nome, String tag) {
        this.l.readLock().lock();
        List<Registo> registos = new ArrayList<>();
        for(Registo r : this.BD.get(tag)) registos.add(r.clone());
        this.l.readLock().unlock();

        Registo objetivo = null;

        for (Registo r : registos) {
            if (tag.equals("CNAME")) {
                if (r.getvalor().equals(nome)) {
                    objetivo = r;
                    break;
                }
            } else {
                if (r.getNome().equals(nome)) {
                    objetivo = r;
                    break;
                }
            }

        }

        return objetivo;
    }

    /**
     * Método com o objetivo de devolver os registos para ExtraValues, mas garantindo que não registos repetidos na string final
     *
     * @param registos registos que queremos avaliar e adicionar na string final dos ExtraValues
     * @return string compactada com os todos registos que se deverão adicionar no campo ExtraValues
     */
    public String[] fetchExtra(List<Registo> registos){
        List<Registo> res = new ArrayList<>();
        String[] str = new String[2];

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < registos.size();i++){
            Registo r = fetch(registos.get(i).getvalor(), "A");
            if(!res.contains(r)) {
                res.add(r.clone());
                if(i != 0) sb.append(",");
                sb.append(r);
            }
        }
        str[0] = sb.toString();
        str[1] = Integer.toString(res.size());

        return str;
    }

    /**
     * Recebendo uma lista de registos para o campo Authorities, elimina o registo com o mesmo nome ao recebido pelo método
     * @param list lista com os registos para o campo Authorities
     * @param nome nome do registo que se quer eliminar
     */
    public void containsAuth(List<Registo> list, String nome){

        for(Registo r : list){
            if(r.getvalor().equals(nome)) {
                list.remove(r);
                break;
            }
        }

    }

    /**
     * Método responsável por receber uma query de um cliente e por criar o PDU de resposta para o cliente.
     *
     * @param query PDU recebida a partir de um cliente
     * @return query de resposta do SP para o cliente
     */
    public String handleQuery(PDU query){

        List<Registo> authorities = fetchTag("NS");
        String nAuthorities = Integer.toString(authorities.size());
        String auth = listString(authorities);
        String type = query.getTypeOfValue();
        String nome = query.getName();
        String response = "";
        String extra = "";
        String nValues = "0";
        String nExtra = "0";
        String tags = "";
        String rcode = "0";

        // type of value inválido
        if(!query.getTypes().contains(type)){
            tags = "A";
            rcode = "3";
            nAuthorities = "0";
            auth = "";
        }

        else {
            if (this.subdominio != null && nome.contains(this.subdominio)) {
                // response code 0, sem tags, sem authorities
                // response é NS do sub e extra o A do sub
                Registo r = fetch(this.subdominio, this.subdominio);
                response = r.toString();
                nValues = "1";
                nAuthorities = "0";
                auth = "";
                extra = fetch(r.getvalor(), "A").toString();
                nExtra = "1";
            } else if (nome.contains(this.dominio)) {
                // response code 0, tag A -> encontrou resposta
                // response code 1, tag A -> n encontrei máquina, sem extra, sem resposta, com NS

                //A
                if (type.equals("A")) {
                    Registo r = fetch(nome, "A");
                    if (r != null) {
                        response = r.toString();
                        nValues = "1";

                    } else {
                        rcode = "1";

                    }
                    containsAuth(authorities, nome);
                    String[] extras = fetchExtra(authorities);
                    extra = extras[0];
                    nExtra = extras[1];
                    tags = "A";
                } else if (type.equals("CNAME")) {
                    Registo r = fetch(nome, "CNAME");
                    if (r != null) {
                        response = r.toString();
                        nValues = "1";
                        authorities.add(r);
                    } else {
                        rcode = "1";
                    }
                    String[] extras = fetchExtra(authorities);
                    extra = extras[0];
                    nExtra = extras[1];
                    tags = "A";
                } else {
                    //MX ou NS
                    List<Registo> r = fetchTag(type);
                    nValues = Integer.toString(r.size());
                    response = listString(r);
                    for (Registo reg : r) authorities.add(reg);
                    String[] extras = fetchExtra(authorities);
                    extra = extras[0];
                    nExtra = extras[1];
                    tags = "A";

                }
            } else { // response code 2, A, sem extra, sem resposta, sem NS
                tags = "A";
                rcode = "2";
                nAuthorities = "0";
                auth = "";

            }
        }

        PDU resposta = new PDU(query.getMessageID(),nome,type, tags, rcode, nValues, nAuthorities,nExtra,response,auth,extra);
        return resposta.ToString();
    }

    /**
     * Devolve o tamanho da base de dados do servidor (em formato String)
     *
     * @return tamanho da base de dados do servidor (em formato String)
     */
    public String BDsize(){
        int linhas = 0;
        this.l.readLock().lock();
        try {
            for (String s : this.BD.keySet()) {
                linhas += this.BD.get(s).size();
            }
            return Integer.toString(linhas);
        }
        finally {
            this.l.readLock().unlock();
        }
    }

    public List<Registo> getAllRegistos(){
        List<Registo> res = new ArrayList<>();
        this.l.readLock().lock();
        try {
            for (String st : this.BD.keySet()) {
                List<Registo> list = this.BD.get(st);
                for (Registo r : list) {
                    res.add(r.clone());
                }
            }
            return res;
        }
        finally {
            this.l.readLock().unlock();
        }
    }

    /**
     * Método Auxiliar para adicionar um registo da base de dados do SP no Servidor secundário
     * @param r registo que se quer adicionar
     */
    public void transfZonaLinha(Registo r){
        if((!this.dominio.equals(r.getNome()) )  && r.getTag().equals("NS")){
            addRegistoBD(r.getNome(), r.clone());
            this.subdominio = r.getNome();
        }
        else {
            addRegistoBD(r.getTag(), r.clone());
        }

    }
}
