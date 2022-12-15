import java.util.ArrayList;
import java.util.List;

/**
 * Classe PDU - Protocol Data Unit - para construção de um PDU de resposta do Servidor
 * 
 * @author Millena Freitas (a97777) - 60%
 * @author Guilherme Martins (a92847) - 20%
 * @author Vasco Oliveira (a96361) - 20%
 */
public class PDU {
    // Identificador único da mesnagem
    private String messageID;
    // Flag da mensagem (Q - query, R- recursivo, A - autoritativo)
    private String flags; //Q - query, R- recursivo, A - autoritativo
    // Código de erro na resposta a uma query (0, 1, 2 ou 3)
    private String responseCode;
    // Número de entradas relevantes que respondem diretamente à query e que fazem parte da lista de entradas incluídas no campo RESPONSE VALUES
    private String nValues;
    // Número de entradas que identificam os servidores autoritativos para o domínio incluído no RESULT VALUES
    private String nAuthorities;
    // Número de entradas com informação adicional relacionada com os resultados da query ou com os servidores da lista de autoridades
    private String nExtraValues;
    // String compacta das entradas que fazem match no NAME e TYPE OF VALUE incluídos na cache ou na base de dados do servidor autoritativo
    private String responseValues;
    // String compacta das entradas que fazem match com o NAME e com o tipo de valor igual a NS incluídos na cache ou na base de dados do servidor autoritativo
    private String authoritiesValues;
    // String compacta das entradas do tipo A  e que fazem match no parâmetro com todos os valores no campo RESPONSE VALUES e no campo AUTHORITIES VALUES
    private String extraValues;
    // Nome associado ao 'typeOfValue'
    private String name;
    // Tipo de valor de resposta da query (MX, NS, A, CNAME)
    private String typeOfValue;

    private List<String> types;

    //servidor cria pdu qdo responde query
    /**
     * Construtor de um PDU a partir dos dados recebidos
     * 
     * @param messageID identificador único da mesnagem
     * @param name nome associado ao 'typeOfValue'
     * @param typeOfValue tipo de valor de resposta da query
     * @param responseCode código de erro na resposta a uma query (0, 1, 2 ou 3)
     * @param nValues número de entradas relevantes que respondem diretamente à query e que fazem parte da lista de entradas incluídas no campo RESPONSE VALUES
     * @param nAuthorities número de entradas que identificam os servidores autoritativos para o domínio incluído no RESULT VALUES
     * @param nExtraValues número de entradas com informação adicional relacionada com os resultados da query ou com os servidores da lista de autoridades
     * @param responseValues string compacta das entradas que fazem match no NAME e TYPE OF VALUE incluídos na cache ou na base de dados do servidor autoritativo
     * @param authoritiesValues string compacta das entradas que fazem match com o NAME e com o tipo de valor igual a NS incluídos na cache ou na base de dados do servidor autoritativo
     * @param extraValues string compacta das entradas do tipo A  e que fazem match no parâmetro com todos os valores no campo RESPONSE VALUES e no campo AUTHORITIES VALUES
     */
    public PDU(String messageID, String name, String typeOfValue, String flags,String responseCode, String nValues, String nAuthorities, String nExtraValues, String responseValues, String authoritiesValues, String extraValues) {
        this.messageID = messageID;
        this.name = name;
        this.typeOfValue = typeOfValue;
        this.flags = flags;
        this.responseCode = responseCode;
        this.nValues = nValues;
        this.nAuthorities = nAuthorities;
        this.nExtraValues = nExtraValues;
        this.responseValues = responseValues;
        this.authoritiesValues = authoritiesValues;
        this.extraValues = extraValues;
    }

    public List<String> getTypes() {
        List<String> res = new ArrayList<>();
        for(String s : this.types){
            res.add(s);
        }
        return types;
    }


    /**
     * Devolve a string do identificador único da mensagem
     * 
     * @return string do identificador único da mensagem
     */
    public String getMessageID() {
        return messageID;
    }

    /**
     * Devolve o nome associado ao 'typeOfValue' do PDU
     * 
     * @return string do nome associado ao 'typeOfValue'
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @return string do tipo de valor da resposta da query
     */
    public String getTypeOfValue() {
        return typeOfValue;
    }

    //recebeu query
    /**
     * Método que recebe uma query (em forma de array de bytes) para a reconstrução do PDU
     * 
     * @param data array de bytes da query necessário para a reconstrução de um PDU (Servidor)
     */
    public PDU(byte[] data){
        String msg =  new String(data).trim();
        String[] arrOfStr = msg.split(";", 3);
        String fst[] = arrOfStr[0].split(",", 6);
        String snd[] = arrOfStr[1].split(",", 2);
        this.messageID = fst[0];
        this.flags = fst[1];
        this.responseCode = fst[2];
        this.nValues = fst[3];
        this.nAuthorities = fst[4];
        this.nExtraValues = fst[5];
        this.name = snd[0];
        this.typeOfValue = snd[1];
        this.responseValues = "";
        this.authoritiesValues = "";
        this.extraValues = "";

        this.types = new ArrayList<>();
        this.types.add("NS");
        this.types.add("MX");
        this.types.add("CNAME");
        this.types.add("A");
        this.types.add("PTR");
    }

    //formato conciso
    /**
     * Método que converte uma estrtura PDU numa única String, no seu formato mais conciso
     * 
     * @return String com todos os campos do PDU numa única lista de caracteres concisa
     */
    public String ToString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.messageID).append(",")
                .append(this.flags).append(",")
                .append(this.responseCode).append(",")
                .append(this.nValues).append(",")
                .append(this.nAuthorities).append(",")
                .append(this.nExtraValues).append(";")
                .append(this.name).append(",")
                .append(this.typeOfValue).append(";")
                .append(this.responseValues).append(";")
                .append(this.authoritiesValues).append(";")
                .append(this.extraValues).append(";");

        return sb.toString();
    }

}
