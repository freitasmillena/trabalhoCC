import java.util.List;

public class PDU {
    private String messageID;
    private String flags; //Q - query, R- recursivo, A - autoritativo
    private String responseCode;
    private String nValues;
    private String nAuthorities;
    private String nExtraValues;
    private String responseValues;
    private String authoritiesValues;
    private String extraValues;
    private String name;
    private String typeOfValue;

    //cliente cria pdu qdo faz query
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
