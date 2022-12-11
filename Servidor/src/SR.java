import java.util.*;

/**
 * Claase SR - Servidor de Resolução (extensão da classe Servidor)
 * 
 * @author Millena Freitas (a97777) - 50%
 * @author Guilherme Martins (a92847) - 20%
 * @author Vasco Oliveira (a96361) - 30%
 */
public class SR extends Servidor{
    // Lista de servidores associados ao SR
    private List<String> servidoresDNS;
    // Cache / Base de dados do SR
    private Data cache;
    private String DD;

    /**
     * Construtor vazio para um Servidor de Resolução
     */
    public SR() {
        super();
        this.servidoresDNS = new ArrayList<>();
    }

    /**
     * Construtor para um ervidor de Resolução
     * 
     * @param dominio nome de domínio do servidor
     * @param portaAtendimento número da Porta de atendimento do servidor
     * @param ficheiroLog diretoria para aceder ao ficheiro de Logs
     * @param servidoresTopo lista dos servidores de topo
     * @param timeout tempo máximo de espera pela resposta a uma query
     * @param servidoresDNS Lista de servidores associados ao SR
     */
    public SR(String dominio, String timeout, String portaAtendimento, String ficheiroLog, List<String> servidoresTopo, List<String> servidoresDNS) {
        super(dominio, portaAtendimento, ficheiroLog, servidoresTopo, timeout);
        this.servidoresDNS = new ArrayList<>();
        for(String dns : servidoresDNS){
            this.servidoresDNS.add(dns);
        }
    }

    public String getDD() {
        return DD;
    }

    public void setDD(String DD) {
        this.DD = DD;
    }

    /**
     * Devolve a Lista de servidores associados ao SR
     * @return Cópia da Lista de servidores associados ao SR
     */
    public List<String> getServidoresDNS() {
        List<String> res = new ArrayList<>();
        for(String st : this.servidoresDNS){
            res.add(st);
        }
        return res;
    }

    /**
     * Define os servidores associados ao SR
     * @param servidoresDNS Lista de servidores associados ao SR
     */
    public void setServidoresDNS(List<String> servidoresDNS) {
        this.servidoresDNS = new ArrayList<>();

        for(String st : servidoresDNS){
            this.servidoresDNS.add(st);
        }
    }


    /**
     * Define a cache do Servidor de Resolução
     * 
     * @param cache HasMap com a cache do Servidor de Resolução
     */
    public void setCache(Data cache) {
        this.cache = cache;
    }

    /**
     * Veriifca se dois SRs são igauis
     * 
     * @param o Objeto do servidor de resolução recebido para comparação 
     * @return booleano que diz se os dois SRs são iguais
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SR sr = (SR) o;
        return (super.equals(sr) &&
                this.servidoresDNS.equals(sr.getServidoresDNS())
        );
    }

    @Override
    public void transf_zone() {

    }

    @Override
    public void query() {
        System.out.println("sou um SR e ainda n tenho run");
    }

}
