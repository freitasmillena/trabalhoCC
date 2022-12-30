/**
 * Classe Registo - corrresponde a um pedaço de informação da base de dados dos servidores
 * 
 * @author Millena Freitas (a97777)
 * @author Guilherme Martins (a92847)
 * @author Vasco Oliveira (a96361)
 */
public class Registo {
    // Valor associado ao registo
    private String valor;
    // Tempo de validade do registo : tempo máximo em segundos que os dados podem existir na cache dum servidor
    private long timetolive;
    // Tag do registo
    private String tag;
    // Valor da prioridade do registo (varia entre 0 e 1 milhão)
    private int prioridade;
    // Nome associado ao registo
    private String nome;
    private boolean valid; //valid or not valid
    private String origem;

    /**
     * Construtor vazio para um Registo
     */
    public Registo() {
        this.valor = "";
        this.timetolive = 0;
        this.tag = "";
        this.prioridade = 0;
        this.nome = "";
    }

    /**
     * Construtor para criar um registo com os dados recebidos
     * 
     * @param valor Valor associado ao registo
     * @param timetolive Tempo de validade do registo : tempo máximo em segundos que os dados podem existir na cache dum servidor
     * @param tag Tag do registo
     * @param prioridade Valor da prioridade do registo (varia entre 0 e 1 milhão)
     * @param nome Nome associado ao registo
     */
    public Registo(String valor, long timetolive, String tag, int prioridade, String nome, String origem) {
        this.valor = valor;
        this.timetolive = timetolive;
        this.tag = tag;
        this.prioridade = prioridade;
        this.nome = nome;
        this.origem = origem;
        this.valid = true;
    }

    /**
     * Métoodo que permite copiar os campos do registo obtido para o atual
     * 
     * @param registo registo que se quer copiar
     */
    public Registo(Registo registo) {
        this.valor = registo.getvalor();
        this.timetolive = registo.getTimetolive();
        this.tag = registo.getTag();
        this.prioridade = registo.getPrioridade();
        this.nome = registo.getNome();
        this.origem = registo.getOrigem();
        this.valid = registo.isValid();
    }

    /**
     * Conversão de um array de bytes no seu registo
     * 
     * @param data array de bytes que representa um registo
     */
    public Registo(byte[] data){
        String msg =  new String(data).trim();
        String[] arrOfStr = msg.split(" ", 7);
        this.nome = arrOfStr[0];
        this.tag = arrOfStr[1];
        this.valor = arrOfStr[2];
        this.timetolive = Integer.parseInt(arrOfStr[3]);
        this.prioridade = Integer.parseInt(arrOfStr[4]);
	this.valid = !arrOfStr[5].equals("false");
	this.origem = arrOfStr[6];
    }

    /**
     * Verifica se o registo é válido para puder ser usado pelos servidores.
     * 
     * @return booleano que diz se o registo é válida ou não
     */
    public boolean isValid() {
        return this.valid;
    }

    /**
     * Define o registo segundo a validade recebida pelo método.
     * 
     * @param valid booleano que indica a validade do registo
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * Mostra de qual servidor provém o respetivo registo
     * 
     * @return origem do registo
     */
    public String getOrigem() {
        return origem;
    }

    /**
     * Devolve nome associado ao registo
     * 
     * @return nome associado ao registo
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Define nome associado ao registo
     * 
     * @param nome associado ao registo
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Devolve string do valor associado ao registo
     * 
     * @return string do valor associado ao registo
     */
    public String getvalor() {
        return valor;
    }

    /**
     * Define string do valor associado ao registo
     * 
     * @param valor string do valor associado ao registo
     */
    public void setvalor(String valor) {
        this.valor = valor;
    }

    /**
     * Devolve o Tempo de validade do registo
     * @return Tempo de validade do registo
     */
    public long getTimetolive() {
        return timetolive;
    }

    /**
     * Define o Tempo de validade do registo
     * @param timetolive Tempo de validade do registo
     */
    public void setTimetolive(int timetolive) {
        this.timetolive = timetolive;
    }

    /**
     * Devolve a Tag do registo 
     * 
     * @return Tag do registo
     */
    public String getTag(){
        return this.tag;
    }

    /**
     * Define a Tag do registo
     * 
     * @param tag Tag do registo 
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Devolve o valor da priridade do registo
     * 
     * @return valor da prioridade do registo
     */
    public int getPrioridade() {
        return this.prioridade;
    }

    /**
     * Define o valor da priridade do registo
     * 
     * @param prioridade valor da prioridade do registo
     */
    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    /**
     * Verifica se o registo recebido é igual ao atual
     * 
     * @return booleano que diz se ambos os registos são iguais
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registo registo = (Registo) o;
        return (this.valor.equals(registo.getvalor()) &&
                this.tag.equals(registo.getTag()) &&
                this.timetolive == registo.getTimetolive() &&
                this.prioridade == registo.getPrioridade() &&
                this.nome.equals(registo.getNome()) &&
                this.valid == registo.isValid() &&
                this.origem.equals(registo.getOrigem())
        );
    }

    /**
     * Cria um clone de um registo
     * 
     * @return uma cópia do registo
     */
    public Registo clone(){
        return new Registo(this);
    }

    /**
     * Converet o registo nuam string comapctadad com todos os seus campos
     * 
     * @return string compacta com todos os campos do registo
     */
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
                .append(this.prioridade)
		.append(" ")
		.append(this.valid)
		.append(" ")
                .append(this.origem);
       return sb.toString();
    }

    public void updateTTL(){
        this.timetolive = this.timetolive + System.currentTimeMillis();
    }
}
