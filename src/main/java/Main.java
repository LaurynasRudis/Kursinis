import RDF.OntologyModel;
import Search.SolrEngine;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;

import java.io.*;


public class Main {

    static String SOLR_URL = "http://localhost:8983/solr";

    public static void main(String[] args) throws IOException, SolrServerException {
        String inputFileName = "src/main/resources/rawdata_part_4.json.rdf";
        String coreName = "kursinis";
        String coreUrl = SOLR_URL +"/" + coreName;
        HttpSolrClient solr = new HttpSolrClient.Builder(coreUrl).build();
        solr.setParser(new XMLResponseParser());
        SolrEngine solrEngine = new SolrEngine(solr);

        OntologyModel model = new OntologyModel(inputFileName);

        solrEngine.index(model);
    }
}
