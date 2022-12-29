import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Data {

    private static Data single_instance = null;
    private Map<String, List<Registo>> BD;
    private String dominio;
    private List<String> subdominios;
    private List<String> DD;
    private ReentrantReadWriteLock l = new ReentrantReadWriteLock();
    private List<String> ST = new ArrayList<>();
    private boolean SR;

    private Data(String dominio){
        this.BD = new HashMap<>();
        this.dominio = dominio;
	this.subdominios = new ArrayList<>();
    }

    public void setST(List<String> ST){
        this.ST = ST;
    }

    public List<String> getST() {
        return ST;
    }

    public void setSR(boolean SR) {
        this.SR = SR;
    }

    public boolean isSR() {
        return SR;
    }

    public List<String> getDD() {
        return DD;
    }

    public void setDD(List<String> DD) {
        this.DD = DD;
    }

    public void setSubdominio(String subdominio) {
        this.subdominios.add(subdominio);
    }

    public String getdominio() {
        return dominio;
    }

    public static Data getInstance(String dominio){
        if(single_instance == null) single_instance = new Data(dominio);
        return single_instance;
    }

    public String getSOASERIAL(){
        String res = "-1";
        this.l.readLock().lock();
        try {
            if (this.BD.containsKey("SOASERIAL")) {
                for (Registo r : this.BD.get("SOASERIAL")) {
                    res = r.getvalor();
                }
            }
            return res;
        }
        finally {
            this.l.readLock().unlock();
        }
    }

    public String getSOAREFRESH(String timeout){
        String res = timeout;
        this.l.readLock().lock();
        try {
            if (this.BD.containsKey("SOAREFRESH")) {
                for (Registo r : this.BD.get("SOAREFRESH")) {
                    res = r.getvalor();
                }
            }
            return res;
        }
        finally {
            this.l.readLock().unlock();
        }
    }

    public String getSOARETRY(String timeout){
        String res = timeout;
        this.l.readLock().lock();
        try {
            if (this.BD.containsKey("SOARETRY")) {
                for (Registo r : this.BD.get("SOARETRY")) {
                    res = r.getvalor();
                }
            }
            return res;
        }
        finally {
            this.l.readLock().unlock();
        }
    }

    public String getSOAEXPIRE(String timeout){
        String res = timeout;
        this.l.readLock().lock();
        try {
            if (this.BD.containsKey("SOAEXPIRE")) {
                for (Registo r : this.BD.get("SOAEXPIRE")) {
                    res = r.getvalor();
                }
            }
            return res;
        }
        finally {
            this.l.readLock().unlock();
        }
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
                //System.out.println("if");
                List<Registo> list = this.BD.get(tipoValor);
                for(Registo reg : list){
                    if(reg.getvalor().equals(r.getvalor())){
                        list.remove(reg);
                        break;
                    }
                }
                list.add(r.clone());
            } else {
	        //System.out.println("else");
                List<Registo> reg = new ArrayList<>();
                reg.add(r.clone());
                this.BD.put(tipoValor, reg);
            }
        }
        finally {
            this.l.writeLock().unlock();
        }
    }


    public void clear(){
        this.l.writeLock().lock();
        if(!this.BD.isEmpty()){
            this.BD.clear();
        }
        this.l.writeLock().unlock();
    }

    public void changeValid(boolean valid){
        this.l.writeLock().lock();
        try {
            for (String s : this.BD.keySet()) {
                for (Registo r : this.BD.get(s)) {
                    r.setValid(valid);
                }
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
    public List<Registo> fetchTag(String tag, String name, int SR){
        List<Registo> registos = new ArrayList<>();
        this.l.readLock().lock();
        try{
            if(this.BD.containsKey(tag)) {
                for (Registo r : this.BD.get(tag)) {
                    if(SR == 1){
                        long now = System.currentTimeMillis();
			//System.out.println("TTL: " + r.getTimetolive());
			//System.out.println("Now: " + now);
                        if(r.getTimetolive() < now){
			    //System.out.println("HERE:" + now);
                            r.setValid(false);
                        }
                    }
		    //System.out.println(r.getNome());
                    //System.out.println(name);
		    //System.out.println(r.getNome().equals(name));
		    //System.out.println(r.isValid());
                    if (r.getNome().equals(name) && r.isValid()) {
			//System.out.println("fetchTag adicionou");
                        registos.add(r.clone());
                    }
                }
            }
            return registos;
        }
        finally {
            this.l.readLock().unlock();
        }

    }


    /**
     * Devolve o registo com o mesmo nome e tag
     *
     * @param nome nome do registo desejado
     * @param tag tag do registo desejado
     * @return registo com o nome e a tag pedidos
     */
    public Registo fetch(String nome, String tag, int SR) {

        this.l.readLock().lock();
        try {
            Registo objetivo = null;
            if (this.BD.containsKey(tag)) {
                for (Registo r : this.BD.get(tag)) {
                    if(SR == 1){
                        long now = System.currentTimeMillis();
                        if(r.getTimetolive() < now){
                            r.setValid(false);
                        }
                    }
                    if (tag.equals("CNAME")) {
                        if (r.getvalor().equals(nome) && r.isValid()) {
                            objetivo = r.clone();
                            break;
                        }
                    } else {
                        if (r.getNome().equals(nome) && r.isValid()) {
			   // System.out.println("fetch");
                            objetivo = r.clone();
                            break;
                        }
                    }
                }
            }
            return objetivo;
        }
        finally {
            this.l.readLock().unlock();
        }



    }

    /**
     * Método com o objetivo de devolver os registos para ExtraValues, mas garantindo que não registos repetidos na string final
     *
     * @param registos registos que queremos avaliar e adicionar na string final dos ExtraValues
     * @return string compactada com os todos registos que se deverão adicionar no campo ExtraValues
     */
    public List<Registo> fetchExtra(List<Registo> registos, List<Registo> reg, int SR){
        List<Registo> res = new ArrayList<>();

        for(int i = 0; i < registos.size();i++){
            Registo r = fetch(registos.get(i).getvalor(), "A", SR);

            if(r != null && !res.contains(r)) {
                res.add(r.clone());
            }
        }
        if(reg != null) {
            for(Registo r : reg){
                Registo extra = fetch(r.getvalor(), "A", SR);
                if(extra != null && !res.contains(extra)) {
                    res.add(extra.clone());
                }
            }
        }

        return res;
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

    public String containsSub(String nome){
        String res = null;
        for(String s : this.subdominios){
            if(nome.contains(s)) {
                res = s;
                break;
            }
        }
        return res;
    }
    /**
     * Método responsável por receber uma query de um cliente e por criar o PDU de resposta para o cliente.
     *
     * @param query PDU recebida a partir de um cliente
     * @return query de resposta do SP para o cliente
     */
    public String handleQuery(PDU query){

        List<Registo> auth = fetchTag("NS", this.dominio,0);
        String nAuthorities = Integer.toString(auth.size());
        String type = query.getTypeOfValue();
        String nome = query.getName();
        List<Registo> response = new ArrayList<>();
        List<Registo> extra = new ArrayList<>();
        String nValues = "0";
        String nExtra = "0";
        String tags = "";
        String rcode = "0";

        // type of value inválido
        if(!query.getTypes().contains(type)){
            tags = "A";
            rcode = "3";
            nAuthorities = "0";
            auth = null;
        }

        else {
            String sub = containsSub(nome);
            if (this.subdominios.size() !=0 && sub != null) {
                // response code 1, tag A, sem response, auth é NS do sub, extra só ip do sub
                List<Registo> r = fetchTag("NS", sub,0);
                rcode = "1";
                tags = "A";
                nAuthorities = "1";
                auth = r;
                extra = fetchExtra(r, null,0);
                nExtra = "1";
            } else if (nome.contains(this.dominio) && (nome.length() <= this.dominio.length() + 8)) {
                // response code 0, tag A -> encontrou resposta
                // response code 1, tag A -> n encontrei máquina, sem extra, sem resposta, com NS

                //A
                if (type.equals("A")) {
                    Registo r = fetch(nome, "A",0);
                    if (r != null) {
                        response.add(r);
                        nValues = "1";

                    } else {
                        rcode = "2";

                    }
                    containsAuth(auth, nome);
                    extra = fetchExtra(auth, null,0);
                    nExtra = Integer.toString(extra.size());
                    tags = "A";
                } else if (type.equals("CNAME")) {
                    Registo r = fetch(nome, "CNAME",0);
                    if (r != null) {
                        response.add(r);
                        nValues = "1";
                    } else {
                        rcode = "2";
                    }
                    extra = fetchExtra(auth, response,0);
                    nExtra = Integer.toString(extra.size());
                    tags = "A";
                }
                else if(type.equals("PTR")){
                    String[] nomes = nome.split("-",2);
                    String ip = nomes[0];

                    //inverter pra ter ip
                    String[] sep = ip.split("\\.",4);
                    Collections.reverse(Arrays.asList(sep));
                    String ipFinal = sep[0] + "." + sep[1] + "." + sep[2] + "." + sep[3];
                    //procura
                    Registo r = fetch(ipFinal, "PTR",0);
                    if (r != null) {
                        response.add(r);
                        nValues = "1";

                    } else {
                        rcode = "2";

                    }
                    extra = fetchExtra(auth, null,0);
                    nExtra = Integer.toString(extra.size());
                    tags = "A";
                }
                else {
                    //MX ou NS
                    List<Registo> r = fetchTag(type, nome,0);
                    nValues = Integer.toString(r.size());
                    if(r.size() == 0){
                        rcode = "2";
                    }
                    response = r;
                    extra = fetchExtra(auth,r,0);
                    nExtra = Integer.toString(extra.size());
                    tags = "A";

                }
            } else { // response code 2, A
                tags = "A";
                rcode = "2";
                extra = fetchExtra(auth, null,0);
                nExtra = Integer.toString(extra.size());

            }
        }

        PDU resposta = new PDU(query.getMessageID(),nome,type, tags, rcode, nValues, nAuthorities,nExtra,response,auth,extra);
        return resposta.ToString();
    }


    public List<Registo> getAllTag(String tag){
        List<Registo> registos = new ArrayList<>();
        this.l.readLock().lock();
        try{
            if(this.BD.containsKey(tag)) {
                for (Registo r : this.BD.get(tag)) {
                    long now = System.currentTimeMillis();
                    if(r.getTimetolive() < now){
                            r.setValid(false);
                    }
                    if(r.isValid()) registos.add(r.clone());
                }
            }
            return registos;
        }
        finally {
            this.l.readLock().unlock();
        }
    }


    public String getLPM(String nome, List<Registo> registos){
        String lpm = null;
        int length = -1;

        for(Registo r : registos){
            String name = r.getNome();
            if(nome.contains(name) && name.length() > length){
                lpm = name;
                length = name.length();
            }
        }
        return lpm;
    }

    public PDU handleCache(PDU query){
        PDU resposta = null;

        String tag = query.getTypeOfValue();
        String name = query.getName();

        // Para criar PDU
        List<Registo> auth = null;
        String nAuthorities = "0";
        List<Registo> extra = null;
        String nValues = "0";
        String nExtra = "0";
        String tags = "";
        String rcode = "0";


        List<Registo> responseValue = new ArrayList<>();
        boolean flag = true;
        
        //Procura por resposta direta à query na cache
        //System.out.println(this.BDsize());
        if(tag.equals("NS") || tag.equals("MX")){
            List<Registo> res = fetchTag(tag,name,1);
            //System.out.println("Size ns ou mx" + res.size());
            if(res.size() > 0){
                for(Registo r : res) responseValue.add(r);
            }
        }
        else{
           Registo r = null;
           if(tag.equals("PTR")){
               String[] nomes = name.split("-",2);
               String ip = nomes[0];

               //inverter pra ter ip
               String[] sep = ip.split("\\.",4);
               Collections.reverse(Arrays.asList(sep));
               String ipFinal = sep[0] + "." + sep[1] + "." + sep[2] + "." + sep[3];
               r = fetch(ipFinal,tag,1);
           }
           else{
               r = fetch(name,tag,1);
           }

           if(r != null) {
               //System.out.println("Registo" + r);
               responseValue.add(r);
           }
        }

        //Checar se obteve resposta à query
        if(responseValue.size() > 0){
           // System.out.println("Teve resposta à query na cache");
            //tamanho maior que 0 => teve resposta. Cria PDU com resposta.
            responseValue.removeIf(r -> r.getvalor().contains("sp"));
            nValues = Integer.toString(responseValue.size());

            //Buscar autoridades
            List<Registo> ns = getAllTag("NS");
            String lpm = getLPM(name,ns);
            auth = fetchTag("NS", lpm,1);
	        auth.removeIf(r -> r.getvalor().contains("sp"));
            nAuthorities = Integer.toString(auth.size());

            //Buscar extra
            if(auth.size() > 0) {
                if (tag.equals("A") || tag.equals("PTR")) {
                    extra = fetchExtra(auth, null, 1);
                } else {
                    extra = fetchExtra(auth, responseValue, 1);
                }
                nExtra = Integer.toString(extra.size());

                flag = false;
                resposta = new PDU(query.getMessageID(), name, tag, tags, rcode, nValues, nAuthorities, nExtra, responseValue, auth, extra);
                //System.out.println("Resposta na cache " + resposta.ToString());
            }
        }
        if(flag){
            //Não teve resposta => procura por referência longest prefix match NS ao name

            //Pegar NS
            List<Registo> ns = getAllTag("NS");
            if(ns.size() > 0){
                String lpm = getLPM(name,ns);
               // System.out.println(lpm == null);
                if(lpm != null){
		            //System.out.println(lpm);
                    List<Registo> lpmfinal = new ArrayList<>();
                    for(Registo r: ns){
                        if(r.getNome().equals(lpm)) {
                            //System.out.println(r);
                            lpmfinal.add(r);
                        } //remove os que forem diferentes do lpm
                    }

                    auth = lpmfinal;
                    nAuthorities = Integer.toString(auth.size());
                    //Pegar Extra
                    extra = fetchExtra(auth, null,1);
                    //Formar PDU se extra n for vazio
                    if(extra.size() > 0){
                       // System.out.println("Extra maior que 0");
                        nExtra = Integer.toString(extra.size());
                        resposta = new PDU(query.getMessageID(),name,tag, "A", "1", "0",nAuthorities,nExtra,responseValue,auth,extra);
                        //System.out.println("Referência na cache: " + resposta.ToString());
                    }
                }
            }
        }

        return resposta;
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
            this.subdominios.add(r.getNome());
        }
        addRegistoBD(r.getTag(), r.clone());

    }
}
