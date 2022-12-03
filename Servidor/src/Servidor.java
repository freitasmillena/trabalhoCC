import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstrata para os Servidores.
 * Pode ser um Servidor Primário (SP), Secundário (SS) ou de Resolução (SR).
 * 
 * @author Millena Freitas (a97777) - 50%
 * @author Guilherme Martins (a92847) - 20%
 * @author Vasco Oliveira (a96361) - 30%
 */
abstract class Servidor {
    // Nome de domínio do servidor
    private String dominio;
    // Número da Porta de atendimento do servidor
    private String portaAtendimento;
    // Diretoria para aceder ao ficheiro de Logs
    private String ficheiroLog;
    // Tempo máximo de espera pela resposta a uma query
    private String timeOut;
    // Lista dos servidores de topo
    private List<String> servidoresTopo;

    /**
     * Construtor para criação inicial de um Servidor sem dados registados
     */
    public Servidor() {
        this.dominio = "";
        this.portaAtendimento = "";
        this.ficheiroLog = "";
        this.servidoresTopo = new ArrayList<>();
        this.timeOut = "";
    }

    /**
     * Construtor do Servidor com todos os dados para os seus atributos
     * 
     * @param dominio nome de domínio do servidor
     * @param portaAtendimento número da Porta de atendimento do servidor
     * @param ficheiroLog diretoria para aceder ao ficheiro de Logs
     * @param servidoresTopo lista dos servidores de topo
     * @param timeout tempo máximo de espera pela resposta a uma query
     */
    public Servidor(String dominio, String portaAtendimento, String ficheiroLog, List<String> servidoresTopo, String timeout) {
        this.dominio = dominio;
        this.portaAtendimento = portaAtendimento;
        this.ficheiroLog = ficheiroLog;
        this.servidoresTopo = new ArrayList<>();
        this.timeOut = timeout;

        for(String st : servidoresTopo){
            this.servidoresTopo.add(st);
        }
    }

    /**
     * Devolve o timeout do servidor
     * 
     * @return timeout do servidor
     */
    public String getTimeOut() {
        return timeOut;
    }

    /**
     * Define o timeout do servidor
     * 
     * @param timeOut timeout do servidor
     */
    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    /**
     * Devolve o nome de domínio do servidor
     * 
     * @return nome de domínio do servidor
     */
    public String getDominio() {
        return dominio;
    }

    /**
     * Define o nome de domínio do servidor
     * 
     * @param dominio nome de domínio a definir no servidor
     */
    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    /**
     * Devolve (em String) o número da Porta de Atendimento do servidor
     * 
     * @return string com o número da Porta de Atendimento do servidor
     */
    public String getPortaAtendimento() {
        return portaAtendimento;
    }

    /**
     * Define a porta de atendimento do Servidor
     * 
     * @param portaAtendimento número da Porta de Atendimento do servidor
     */
    public void setPortaAtendimento(String portaAtendimento) {
        this.portaAtendimento = portaAtendimento;
    }

    /**
     * Devolve a diretoria para aceder ao ficheiro de Logs
     * 
     * @return diretoria do ficheiro de Logs
     */
    public String getFicheiroLog() {
        return ficheiroLog;
    }

    /**
     * Define a diretoria para aceder ao ficheiro de Logs
     * 
     * @param ficheiroLog diretoria para aceder ao ficheiro de Logs
     */
    public void setFicheiroLog(String ficheiroLog) {
        this.ficheiroLog = ficheiroLog;
    }

    /**
     * Devolve a lista com todos os servidores de topo para o servidor em causa.
     * 
     * @return lista com todos os servidores de topo para o servidor em causa
     */
    public List<String> getServidoresTopo() {
        List<String> res = new ArrayList<>();
        for(String st : this.servidoresTopo) {
            res.add(st);
        }
        return res;
    }

    /**
     * Define os servidores de topo para o servidor em causa
     * 
     * @param servidoresTopo lista dos servidores de topo para o servidor em causa
     */
    public void setServidoresTopo(List<String> servidoresTopo) {
        this.servidoresTopo = new ArrayList<>();

        for(String st : servidoresTopo){
            this.servidoresTopo.add(st);
        }
    }

    /**
     * Adiciona um servidor de topo (ST) à lista de STs associados ao servidor
     * 
     * @param st string do servidor de topo a adicionar
     */
    public void assServidorTopo(String st){
        this.servidoresTopo.add(st);
    }

    /**
     * Verifica se o servidor recebido é igual ao atual
     * 
     * @param o servidor que se deseja comparar
     * @return booleano que indica se o servidor é igual ao servidor recebido
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Servidor servidor = (Servidor) o;
        return (this.dominio.equals(servidor.getDominio()) &&
                this.timeOut.equals(servidor.getTimeOut()) &&
                this.portaAtendimento.equals(servidor.getPortaAtendimento()) &&
                this.ficheiroLog.equals(servidor.getFicheiroLog()) &&
                this.servidoresTopo.equals(servidor.getServidoresTopo()));

    }

/**
 * Método abstrato que define o funcionamento da Transferência de Zona no tipo de servidor que utilizar
 */
public abstract void transf_zone();
/**
 * Método abstrato para colocar o servidor constantemente à espera de pedidos de um cliente
 */
public abstract void query();
}
