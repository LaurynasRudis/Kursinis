package RDF;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class OntologyModel {
    private String ontologyUri = "http://www.lexinfo.net/lmf#";
    private OntModel model;
    private String lexicalEntryUri = ontologyUri + "LexicalEntry";
    private OntClass lexicalEntryClass;
    private Property hasLemma;
    private Property hasSense;
    private Property hasDefinition;
    private Property hasTextRepresentation;
    private Property hasSenseExample ;
    private Property writtenForm;
    private Property text;

    public OntologyModel(String inputFileName) throws FileNotFoundException {
        InputStream input = new FileInputStream(inputFileName);
        OntModel model = ModelFactory.createOntologyModel();
        model.read(input, "");
        this.lexicalEntryClass = model.getOntClass(lexicalEntryUri);
        this.model = model;
        this.hasLemma = model.createProperty(ontologyUri, "hasLemma");
        this.hasSense =  model.createProperty(ontologyUri, "hasSense");
        this.hasDefinition = model.createProperty(ontologyUri, "hasDefinition");
        this.hasTextRepresentation  = model.createProperty(ontologyUri, "hasTextRepresentation");
        this.hasSenseExample = model.createProperty(ontologyUri, "hasSenseExample");
        this.writtenForm = model.createProperty(ontologyUri, "writtenForm");
        this.text = model.createProperty(ontologyUri, "text");
    }

    public OntModel getModel() {
        return model;
    }

    public OntClass getLexicalEntryClass() {
        return lexicalEntryClass;
    }

    public ExtendedIterator<Individual> getLexicalEntryIndividualList(){
        return model.listIndividuals(lexicalEntryClass);
    }

    private StmtIterator findSenses(Individual individual){
        return individual.listProperties(hasSense);
    }

    public String findSenseExample(Individual individual){
        StringBuilder senseStringBuilder = new StringBuilder();
        int i = 1;
        StmtIterator senses = findSenses(individual);
        while(senses.hasNext()) {
            senseStringBuilder.append(i).append(". ");
            Resource sense = senses.next().getResource();
            try {
                senseStringBuilder.append(sense
                        .getPropertyResourceValue(hasSenseExample)
                        .getProperty(text)
                        .getObject().toString());
            } catch (NullPointerException np) {
                senseStringBuilder.append("null");
            }
            i++;
            senseStringBuilder.append(" ");
        }
        return senseStringBuilder.toString();
    }

    public String findDefinition(Individual individual) {
        StringBuilder definitionStringBuilder = new StringBuilder();
        int i = 1;
        StmtIterator senses = findSenses(individual);
        while (senses.hasNext()) {
            definitionStringBuilder.append(i).append(". ");
            Resource sense = senses.next().getResource();
            try {
                definitionStringBuilder.append(sense
                        .getPropertyResourceValue(hasDefinition)
                        .getPropertyResourceValue(hasTextRepresentation)
                        .getProperty(writtenForm)
                        .getObject().toString());
            } catch (NullPointerException np) {
                definitionStringBuilder.append("null");
            }
            i++;
            definitionStringBuilder.append(" ");
        }
        return definitionStringBuilder.toString();
    }

    public String findLemma(Individual individual){
        String lemma;
        try{
            lemma = individual
                    .getPropertyResourceValue(hasLemma)
                    .getProperty(writtenForm)
                    .getObject().toString();
        } catch(NullPointerException np){
            lemma = "";
        }
        return lemma;
    }
}
