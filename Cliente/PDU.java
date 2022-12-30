import java.util.ArrayList;
import java.util.List;

/**
 * Classe PDU - Protocol Data Unit - para o Cliente
 * 
 * @author Millena Freitas (a97777)
 * @author Guilherme Martins (a92847)
 * @author Vasco Oliveira (a96361)
 */
public class PDU {
    // Identificador único da mensagem
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
    private List<Registo> responseValues;
    // String compacta das entradas que fazem match com o NAME e com o tipo de valor igual a NS incluídos na cache ou na base de dados do servidor autoritativo
    private List<Registo> authoritiesValues;
    // String compacta das entradas do tipo A  e que fazem match no parâmetro com todos os valores no campo RESPONSE VALUES e no campo AUTHORITIES VALUES
    private List<Registo> extraValues;
    // Nome associado ao 'typeOfValue'
    private String name;
    // Tipo de resposta (MX, NS, A, CNAME)
    private String typeOfValue;

    //cliente cria pdu qdo faz query
    /**
     * Construtor que cria um PDU a partir dos dados do pedido efetuado pelo Cliente.
     * Ainda não é preenchido com todos os dados da resposta do Servidor.
     * Muitos dos campos têm valores nulos ou iguais a zero, porque é apenas um PDU de pedido.
     * 
     * @param messageID identificador único da mensagem recebida (aleatório)
     * @param name nome de domínio do servidor
     * @param typeOfValue tipo de resposta que o cliente deseja receber (MX, NS, A, CNAME)
     */
    public PDU(String messageID, String name, String typeOfValue) {
        this.messageID = messageID;
        this.name = name;
        this.typeOfValue = typeOfValue;
        this.flags = "Q";
        this.responseCode = "0";
        this.nValues = "0";
        this.nAuthorities = "0";
        this.nExtraValues = "0";
        this.responseValues = new ArrayList<>();
        this.authoritiesValues = new ArrayList<>();
        this.extraValues = new ArrayList<>();
    }

    //recebeu resposta
    /**
     * Recebendo a resposta do Servidor, em array de bytes, transforma a resposta na sua estrutura de PDU
     * 
     * @param data array de bytes da resposta do Servidor
     */
    public PDU(byte[] data){
        String msg =  new String(data).trim();
        String[] arrOfStr = msg.split(";", 6);
        for(String s : arrOfStr){
            System.out.println(s);
        }
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

        this.responseValues = new ArrayList<>();

        if(!this.nValues.equals("0")){
            String[] responses = arrOfStr[2].split(",", Integer.parseInt(this.nValues));
            for(String s : responses){
                this.responseValues.add(new Registo(s.getBytes()));
            }
        }

        this.authoritiesValues = new ArrayList<>();
        if(!this.nAuthorities.equals("0")){
            String[] responses = arrOfStr[3].split(",", Integer.parseInt(this.nAuthorities));
            for(String s : responses){
                this.authoritiesValues.add(new Registo(s.getBytes()));
            }
        }

        this.extraValues = new ArrayList<>();
        if(!this.nExtraValues.equals("0")){
            String[] responses = arrOfStr[4].split(",", Integer.parseInt(this.nExtraValues));
            for(String s : responses){
                this.extraValues.add(new Registo(s.getBytes()));
            }
        }


    }

    //formato conciso
    /**
     * Método que converte uma estrtura PDU numa única String, no seu formato mais conciso
     * 
     * @return String com todos os campos do PDU numa única lista de caracteres concisa
     */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.messageID).append(",")
                .append(this.flags).append(",")
                .append(this.responseCode).append(",")
                .append(this.nValues).append(",")
                .append(this.nAuthorities).append(",")
                .append(this.nExtraValues).append(";")
                .append(this.name).append(",")
                .append(this.typeOfValue).append(";");
        if(!nValues.equals("0")) {
            for (int i = 0; i < Integer.parseInt(this.nValues); i++) {
                if(i != 0) sb.append(",");
                sb.append(this.responseValues.get(i));
            }
        }
        sb.append(";");
        if(!nAuthorities.equals("0")) {
            for (int i = 0; i < Integer.parseInt(this.nValues); i++) {
                if(i != 0) sb.append(",");
                sb.append(this.authoritiesValues.get(i));
            }
        }
        sb.append(";");
        if(!nExtraValues.equals("0")) {
            for (int i = 0; i < Integer.parseInt(this.nValues); i++) {
                if(i != 0) sb.append(",");
                sb.append(this.extraValues.get(i));
            }
        }
        sb.append(";");

        return sb.toString();
    }


    /**
     * Imprime o conteúdo da query de resposta obtida pelo Cliente, com uma apresentação agradável no terminal do respetivo Cliente.
     *
     * @return String que deverá ser impressa no terminal do Cliente
     */
    public String imprime(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("Message id: " + this.messageID)
                .append("\n")
                .append("Flags: " + this.flags).append("\n")
                .append("Response code: " + this.responseCode).append("\n")
                .append("Number of Values: " + this.nValues).append("\n")
                .append("Number of Authorities: " + this.nAuthorities).append("\n")
                .append("Number of Extra Values: " + this.nExtraValues).append("\n").append("\n")
                .append("Query Info:").append("\n")
                .append("Name: " + this.name).append(" ")
                .append("Type of Value: " + this.typeOfValue).append(";").append("\n");

        if(!nValues.equals("0")) {
            sb.append("\nResponse Values: ").append("\n");
            for (int i = 0; i < Integer.parseInt(this.nValues); i++) {
                if(i != 0) sb.append("\n");
                sb.append(this.responseValues.get(i));
            }
            sb.append("`\n");
        }
        if(!nAuthorities.equals("0")) {
            sb.append("\nAuthorities Values: ").append("\n");
            for (int i = 0; i < Integer.parseInt(this.nAuthorities); i++) {
                if(i != 0) sb.append("\n");
                sb.append(this.authoritiesValues.get(i));
            }
            sb.append("`\n");
        }
        if(!nExtraValues.equals("0")) {
            sb.append("\nExtra Values: ").append("\n");
            for (int i = 0; i < Integer.parseInt(this.nExtraValues); i++) {
                if(i != 0) sb.append("\n");
                sb.append(this.extraValues.get(i));
            }
            sb.append("`\n");
        }

        return sb.toString();
    }
}
