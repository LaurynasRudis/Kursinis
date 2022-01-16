package Search;

import RDF.OntologyModel;
import org.apache.jena.ontology.Individual;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;


public class SolrEngine {
    private HttpSolrClient solr;

    public SolrEngine(HttpSolrClient solr) {
        this.solr = solr;
    }

    public void index(OntologyModel model) throws IOException, SolrServerException {
        ExtendedIterator<Individual> lexicalEntryListIterator = model.getLexicalEntryIndividualList();
        while(lexicalEntryListIterator.hasNext()){
            Individual individual = lexicalEntryListIterator.next();
            String id = individual.getURI();
            String label = individual.getLabel(null);
            String senseExample = model.findSenseExample(individual);
            String lemma = model.findLemma(individual);
            String definition = model.findDefinition(individual);
            SolrInputDocument document = newSolrInputDocument(id, label, senseExample, lemma, definition);
            solr.add(document);
        }
        solr.commit();
    }

    public SolrInputDocument newSolrInputDocument(
            String id,
            String label,
            String senseExample,
            String lemma,
            String definition) {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", id);
        document.addField("label", label);
        document.addField("lemma", lemma);
        document.addField("sense", senseExample);
        document.addField("definition", definition);
        return document;
    }
}
