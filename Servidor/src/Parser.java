import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe Parser - responsável por converter conteúdo de um ficheiro em dados importantes para o programa
 * 
 * @author Millena Freitas (a97777) - 15%
 * @author Guilherme Martins (a92847) - 80%
 * @author Vasco Oliveira (a96361) - 5%
 */
public class Parser {

    // Para ficheiros de configuração de SP - Servidores Primários
    /**
     * Método responsável por ler o conteúdo de um ficheiro de configuração e inserí-lo no Servidor Primário recebido
     * Os comentários e as linhas vazias são ignorados.
     * 
     * @param nomeFich diretoria do ficheiro de configuração que contém os dados do servidor primário
     * @param sp estrutura vazia do servidor primário, onde se irão inserir os dados de configuração contidos no respetivo ficheiro 
     */
    public void fileParserConfigSP(String nomeFich, SP sp){
        
        List<String> linhas = lerFicheiro(nomeFich);

        for (String linha : linhas) {

            if (linha.equals("\n")) continue;
            else if (linha.charAt(0) == '#') continue;

            else {
                String[] linhaPartida;
                linhaPartida = linha.split("[ \\r\\n]+");
                
                switch(linhaPartida[1]) {
                    case "DB":
                        sp.setDominio(linhaPartida[0]);
                        sp.setFicheiroBD(linhaPartida[2]);
                        break;
                    // Não tem valor SP porque não existe SP de um SP
                    case "SS":
                        sp.addServidorSecundario(linhaPartida[2]);
                        break;
                    case "DD":
                        sp.setPortaAtendimento(linhaPartida[2]);
                        break;
                    case "ST":
                        fileParserST(linhaPartida[2], sp);
                        break;
                    case "LG":
                        sp.setFicheiroLog(linhaPartida[2]);
                        break;
                }
            }
        }
    }

    // Prioridades aplicáveis apenas para os todos campos
    // prioridade INFINITA: 1 000 000 (1 milhão) 
    /**
     * Método responsável por ler o conteúdo de um ficheiro de dados e inserí-lo na base de dados do Servidor Primário recebido.
     * É necessário realçar o facto de que, quando no ficheiro de dados a linha a visitar não contém um valor de prioridade, será fornecido um valor equivalente a 1 milhão.  
     * Os comentários e as linhas vazias são ignorados.
     * 
     * @param nomeFich diretoria do ficheiro de dados que contém os dados da base de dados do servidor primário
     * @param sp estrutura do servidor primário, onde se irão inserir os dados de base de dados contidos no respetivo ficheiro 
     */
    public void fileParserDadosSP(SP sp) {
        List<String> linhas = lerFicheiro(sp.getFicheiroBD());
        int prioridade;

        for (String linha : linhas) {

            if (linha.trim().isEmpty()) {
                continue;
            }

            else {

                String[] linhaPartida;
                linhaPartida = linha.split("[ \\r\\n]+");

                if (linhaPartida[0].equals("#")) {

                }
                else {

                    if (linhaPartida.length < 5) { // não tem prioridade
                        prioridade = 1000000;
                    } else prioridade = Integer.parseInt(linhaPartida[4]);


                    switch (linhaPartida[1]) {
                        case "SOASP":
                            Registo soap = new Registo(linhaPartida[2], Integer.parseInt(linhaPartida[3]), "SOASP", prioridade, sp.getDominio());
                            sp.addRegistoBD("SOASP", soap);
                            break;
                        case "SOAADMIN":
                            Registo soadmin = new Registo(linhaPartida[2], Integer.parseInt(linhaPartida[3]), "SOAADMIN", prioridade, sp.getDominio());
                            sp.addRegistoBD("SOAADMIN", soadmin);
                            break;
                        case "SOASERIAL":
                            Registo soaserial = new Registo(linhaPartida[2], Integer.parseInt(linhaPartida[3]), "SOASERIAL", prioridade, sp.getDominio());
                            sp.addRegistoBD("SOASERIAL", soaserial);
                            break;
                        case "SOAREFRESH":
                            Registo soarefresh = new Registo(linhaPartida[2], Integer.parseInt(linhaPartida[3]), "SOAREFRESH", prioridade, sp.getDominio());
                            sp.addRegistoBD("SOAREFRESH", soarefresh);
                            break;
                        case "SOARETRY":
                            Registo soaretry = new Registo(linhaPartida[2], Integer.parseInt(linhaPartida[3]), "SOARETRY", prioridade, sp.getDominio());
                            sp.addRegistoBD("SOARETRY", soaretry);
                            break;
                        case "SOAEXPIRE":
                            Registo soaexpire = new Registo(linhaPartida[2], Integer.parseInt(linhaPartida[3]), "SOAEXPIRE", prioridade, sp.getDominio());
                            sp.addRegistoBD("SOAEXPIRE", soaexpire);
                            break;
                        case "NS":
                            if (linhaPartida[0].equals(sp.getDominio())) {
                                Registo ns = new Registo(linhaPartida[2], Integer.parseInt(linhaPartida[3]), "NS", prioridade, sp.getDominio());
                                sp.addRegistoBD("NS", ns);
                            } else {
                                Registo ns = new Registo(linhaPartida[2], Integer.parseInt(linhaPartida[3]), "NS", prioridade, linhaPartida[0]);
                                sp.addRegistoBD(linhaPartida[0], ns);
                                sp.setSubDominio(linhaPartida[0]);
                            }
                            break;
                        case "A":
                            Registo a = new Registo(linhaPartida[2], Integer.parseInt(linhaPartida[3]), "A", prioridade, linhaPartida[0]);
                            sp.addRegistoBD("A", a); // Formato ns1.example.com.
                            break;
                        case "CNAME":
                            Registo cname = new Registo(linhaPartida[2], Integer.parseInt(linhaPartida[3]), "CNAME", prioridade, linhaPartida[0]);
                            sp.addRegistoBD("CNAME", cname);
                            break;
                        case "MX":
                            Registo mx = new Registo(linhaPartida[2], Integer.parseInt(linhaPartida[3]), "MX", prioridade, sp.getDominio());
                            sp.addRegistoBD("MX", mx);
                            break;
                    }
                }
            }
        }
    }

    // Para ficheiros de configuração de SS - Servidores Secundários
    /**
     * Método responsável por ler o conteúdo de um ficheiro de configuração e inserí-lo no Servidor Secundário recebido.
     * Os comentários e as linhas vazias são ignorados.
     * 
     * @param nomeFich diretoria do ficheiro de configuração que contém os dados do servidor secundário
     * @param ss estrutura vazia do servidor secundário, onde se irão inserir os dados de configuração contidos no respetivo ficheiro 
     */
    public void fileParserConfigSS(String nomeFich, SS ss){

        List<String> linhas = lerFicheiro(nomeFich);

        for (String linha : linhas) {
            if (linha == "\n") continue;
            else if (linha.charAt(0) == '#') continue;

            else {
                String[] linhaPartida;
                linhaPartida = linha.split("[ \\r\\n]+");
                
                switch(linhaPartida[1]) {
                    // Não tem valor SS, nem DB
                    case "SP":
                        ss.setDominio(linhaPartida[0]);
                        ss.setServidorPrimario(linhaPartida[2]);
                        break;
                    case "DD":
                        ss.setPortaAtendimento(linhaPartida[2]);
                        break;
                    case "ST":
                        fileParserST(linhaPartida[2], ss);
                        break;
                    case "LG":
                        ss.setFicheiroLog(linhaPartida[2]);
                        break;
                }
            }
        }
    }

    // Para ficheiros de configuração de SR - Servidores de Resolução
    /**
     * Método responsável por ler o conteúdo de um ficheiro de configuração e inserí-lo no Servidor de Resolução recebido.
     * Os comentários e as linhas vazias são ignorados.
     * 
     * @param nomeFich diretoria do ficheiro de configuração que contém os dados do servidor de resolução
     * @param ss estrutura vazia do servidor de resolução, onde se irão inserir os dados de configuração contidos no respetivo ficheiro 
     */
    public void fileParserConfigSR(String nomeFich, SR sr){


        List<String> linhas = lerFicheiro(nomeFich);

        for (String linha : linhas) {
            if (linha.equals("\n")) continue;
            else if (linha.charAt(0) == '#') continue;

            else {
                String[] linhaPartida;
                linhaPartida = linha.split("[ \\r\\n]+");
                
                switch(linhaPartida[1]) {
                    // Não tem valor SS, nem DB
                    case "DD":
                        sr.setDominio(linhaPartida[0]);
                        sr.setPortaAtendimento(linhaPartida[2]);
                        break;
                    case "ST":

                        fileParserST(linhaPartida[2], sr);
                        break;
                    case "LG":
                        sr.setFicheiroLog(linhaPartida[2]);
                        break;
                }
            }
        }
    }

    /**
     * Método responsável por ler o conteúdo de um ficheiro de configuração e inserí-lo no Servidor de Topo recebido (ST).
     * Os comentários e as linhas vazias são ignorados.
     * 
     * @param nomeFich diretoria do ficheiro de configuração que contém os dados do servidor de topo
     * @param s estrutura vazia do servidor de topo, onde se irão inserir os dados de configuração contidos no respetivo ficheiro 
     */
    public void fileParserST(String nomeFich, Servidor s) {
        List<String> linhas = lerFicheiro(nomeFich);

        for (String linha : linhas) {
            if (linha.equals("\n")) continue;
            else if (linha.charAt(0) == '#') continue;

            else {
                String[] linhaPartida;
                linhaPartida = linha.split("\\n");
                s.assServidorTopo(linhaPartida[0]);
            }
        }
    }

    /**
     * Método auxiliar para ler um ficheiro e devolver as linhas do mesmo
     * 
     * @param nomeFich diretoria do ficheiro que se deve fazer parser
     * @return lista de strings com as várias linhas do ficheiro recebido
     */
    public List<String> lerFicheiro(String nomeFich) {
        List<String> lines;
        try { lines = Files.readAllLines(Paths.get(nomeFich), StandardCharsets.UTF_8);}
        catch(IOException exc) {
            System.out.println("n achou " + nomeFich);
            lines = new ArrayList<>();
        }
        return lines;
    }

    
}
