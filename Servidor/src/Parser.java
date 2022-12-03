import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
        HashMap<String, String> defaults = new HashMap<>();

        String ar = null; // Para o DEFAULT @

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

                    // Chaves recebidas do DEFAULT
                    Set<String> nomes_defaults = defaults.keySet();

                    // Valor de Prioridade
                    if (linhaPartida.length < 5) { // não tem prioridade
                        prioridade = 1000000;
                    } 
                    else if (nomes_defaults.contains(linhaPartida[4])) {
                        prioridade = Integer.parseInt(defaults.get(linhaPartida[4]));
                    }
                    else prioridade = Integer.parseInt(linhaPartida[4]);


                    String new_linhaPartida2 = null;
                    String new_linhaPartida3 = null;

                    if (nomes_defaults.contains(linhaPartida[2])){
                        new_linhaPartida2 = defaults.get(linhaPartida[2]);
                    }
                    else new_linhaPartida2 = linhaPartida[2];
                    
                    if (linhaPartida.length >= 4) {
                        if (nomes_defaults.contains(linhaPartida[3])){
                            new_linhaPartida3 = defaults.get(linhaPartida[3]);
                        }
                        else new_linhaPartida3 = linhaPartida[3];
                    }


                    switch (linhaPartida[1]) {
                        case "DEFAULT":
                            if (linhaPartida[0].equals("@")) ar = linhaPartida[2];
                            else defaults.put(linhaPartida[0], linhaPartida[2]);
                            break;
                        case "SOASP":
                            Registo soap = new Registo(new_linhaPartida2, Integer.parseInt(new_linhaPartida3), "SOASP", prioridade, sp.getDominio());
                            sp.addRegistoBD("SOASP", soap);
                            //System.out.println(soap.toString());
                            break;
                        case "SOAADMIN":
                            Registo soadmin = new Registo(new_linhaPartida2, Integer.parseInt(new_linhaPartida3), "SOAADMIN", prioridade, sp.getDominio());
                            sp.addRegistoBD("SOAADMIN", soadmin);
                            //System.out.println(soadmin.toString());
                            break;
                        case "SOASERIAL":
                            Registo soaserial = new Registo(new_linhaPartida2, Integer.parseInt(new_linhaPartida3), "SOASERIAL", prioridade, sp.getDominio());
                            sp.addRegistoBD("SOASERIAL", soaserial);
                            //System.out.println(soaserial.toString());
                            break;
                        case "SOAREFRESH":
                            Registo soarefresh = new Registo(new_linhaPartida2, Integer.parseInt(new_linhaPartida3), "SOAREFRESH", prioridade, sp.getDominio());
                            sp.addRegistoBD("SOAREFRESH", soarefresh);
                            //System.out.println(soarefresh.toString());
                            break;
                        case "SOARETRY":
                            Registo soaretry = new Registo(new_linhaPartida2, Integer.parseInt(new_linhaPartida3), "SOARETRY", prioridade, sp.getDominio());
                            sp.addRegistoBD("SOARETRY", soaretry);
                            //System.out.println(soaretry.toString());
                            break;
                        case "SOAEXPIRE":
                            Registo soaexpire = new Registo(new_linhaPartida2, Integer.parseInt(new_linhaPartida3), "SOAEXPIRE", prioridade, sp.getDominio());
                            sp.addRegistoBD("SOAEXPIRE", soaexpire);
                            //System.out.println(soaexpire.toString());
                            break;
                        case "NS":
                            Registo ns = null;
                            if (ar == null) {
                                if (linhaPartida[0].equals(sp.getDominio())) {
                                    ns = new Registo(new_linhaPartida2, Integer.parseInt(new_linhaPartida3), "NS", prioridade, sp.getDominio());
                                    sp.addRegistoBD("NS", ns);
                                }
                                else {
                                    ns = new Registo(new_linhaPartida2, Integer.parseInt(new_linhaPartida3), "NS", prioridade, linhaPartida[0]);
                                    sp.addRegistoBD(linhaPartida[0], ns);
                                    sp.setSubDominio(linhaPartida[0]);
                                }
                            }
                            else { // ar != null
                                if (linhaPartida[0].equals("@")) {
                                    ns = new Registo(new_linhaPartida2, Integer.parseInt(new_linhaPartida3), "NS", prioridade, sp.getDominio());
                                    sp.addRegistoBD("NS", ns);
                                }
                                else {
                                    String[] splitlinha = linhaPartida[0].split("@", 2);
                                    String new_linhaPartida0 = splitlinha[0] + ar;
                                    ns = new Registo(new_linhaPartida2, Integer.parseInt(new_linhaPartida3), "NS", prioridade, new_linhaPartida0);
                                    sp.addRegistoBD(new_linhaPartida0, ns);
                                    sp.setSubDominio(new_linhaPartida0);
                                }
                            }

                            //System.out.println(ns.toString());

                            break;
                        case "A":
                            Registo a = null;
                            if (ar == null) {
                                a = new Registo(new_linhaPartida2, Integer.parseInt(new_linhaPartida3), "A", prioridade, linhaPartida[0]);
                            }
                            else {
                                a = new Registo(new_linhaPartida2, Integer.parseInt(new_linhaPartida3), "A", prioridade, linhaPartida[0] + "." + ar);
                            }
                            sp.addRegistoBD("A", a); // Formato ns1.example.com.
                            //System.out.println(a.toString());
                            break;
                        case "CNAME":
                            Registo cname = null;
                            if (ar == null) {
                                cname = new Registo(new_linhaPartida2, Integer.parseInt(new_linhaPartida3), "CNAME", prioridade, linhaPartida[0]);
                            }
                            else {
                                cname = new Registo(new_linhaPartida2 + "." + ar, Integer.parseInt(new_linhaPartida3), "CNAME", prioridade, linhaPartida[0] + "." + ar);
                            }
                            sp.addRegistoBD("CNAME", cname);
                            //System.out.println(cname.toString());
                            break;
                        case "MX":
                            Registo mx = new Registo(new_linhaPartida2, Integer.parseInt(new_linhaPartida3), "MX", prioridade, sp.getDominio());
                            sp.addRegistoBD("MX", mx);
                            //System.out.println(mx.toString());
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
