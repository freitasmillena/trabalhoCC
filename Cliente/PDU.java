/**
 * Classe PDU - Protocol Data Unit - para o Cliente
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
    private String responseValues;
    // String compacta das entradas que fazem match com o NAME e com o tipo de valor igual a NS incluídos na cache ou na base de dados do servidor autoritativo
    private String authoritiesValues;
    // String compacta das entradas do tipo A  e que fazem match no parâmetro com todos os valores no campo RESPONSE VALUES e no campo AUTHORITIES VALUES
    private String extraValues;
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
        this.responseValues = "";
        this.authoritiesValues = "";
        this.extraValues = "";
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
        String fst[] = arrOfStr[0].split(",", 6);
        String snd[] = arrOfStr[1].split(",", 2);
        this.responseValues = arrOfStr[2];
        this.authoritiesValues = arrOfStr[3];
        this.extraValues = arrOfStr[4];
        this.messageID = fst[0];
        this.flags = fst[1];
        this.responseCode = fst[2];
        this.nValues = fst[3];
        this.nAuthorities = fst[4];
        this.nExtraValues = fst[5];
        this.name = snd[0];
        this.typeOfValue = snd[1];
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
        if(this.flags.equals("Q")){
            sb.append(this.responseValues).append(";");
            sb.append(this.authoritiesValues).append(";");
            sb.append(this.extraValues).append(";");
        }
        else{
            sb.append("\n");
            if(!this.responseValues.equals("")) sb.append(this.responseValues).append(";").append("\n");
            if(!this.authoritiesValues.equals("")) sb.append(this.authoritiesValues).append(";").append("\n");
            if(!this.extraValues.equals("")) sb.append(this.extraValues).append(";").append("\n");

        }




        return sb.toString();
    }

}
