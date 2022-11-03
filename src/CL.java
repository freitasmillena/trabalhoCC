import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CL {
    private List<String> listaSR;
    private String query;
    private String resposta;

    public CL(){
        this.listaSR = new ArrayList<>();
        this.query = "";
        this.resposta = "";
    }
    public CL(List<String> listaSR, String query, String resposta) {
        this.listaSR = new ArrayList<>();
        for(String sr : listaSR){
            this.listaSR.add(sr);
        }
        this.query = query;
        this.resposta = resposta;
    }

    public List<String> getListaSR() {
        List<String> res = new ArrayList<>();

        for(String sr : this.listaSR){
            res.add(sr);
        }
        return res;
    }

    public void setListaSR(List<String> listaSR) {
        this.listaSR = new ArrayList<>();
        for(String sr : listaSR){
            this.listaSR.add(sr);
        }
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CL cl = (CL) o;
        return (this.listaSR.equals(cl.getListaSR()) &&
                this.query.equals(cl.getQuery()) &&
                this.resposta.equals(cl.getResposta())
        );
    }

}
