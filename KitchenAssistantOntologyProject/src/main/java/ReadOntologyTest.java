import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.parameters.Imports;

import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

public class ReadOntologyTest {
	public static final String basePath="/home/mariusz/development/mgr/workspaces/ontologyWorkspace";
	
	
	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		 OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		 File f=new File(basePath+"/classes.owl");
		 manager.loadOntologyFromOntologyDocument(new FileDocumentSource(f));
		 String ontologyIRI="http://www.semanticweb.org/mariusz/ontologies/2019/3/CategoriesOntology";
		 OWLOntology ontology = manager.getOntology(IRI.create(ontologyIRI));
		 
		 
		 Set<OWLClass> classesInSignature = ontology.getClassesInSignature(Imports.EXCLUDED);
		 
		 Set<OWLDataProperty> dataPropertiesInSignature = ontology.getDataPropertiesInSignature();
		 ontology.getDatatypesInSignature();
		 Set<OWLNamedIndividual> individualsInSignature = ontology.getIndividualsInSignature();
		 
		 OWLNamedIndividual individual=new OWLNamedIndividualImpl(IRI.create("dynamicallyCreated"));
		 
		 OWLDataFactory factory = manager.getOWLDataFactory();
//		 OWLIndividual john = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#John"));
//		 individualsInSignature.add(individual);
		 
//		 OWLDataProperty hasAge = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#hasAge"));
		 OWLDataProperty nameProperty = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#name"));
		 
	//	 OWLAxiom axiom4 = factory.getOWLDataPropertyAssertionAxiom(hasAge, john, 33);
		 OWLAxiom axiom5 = factory.getOWLDataPropertyAssertionAxiom(nameProperty, individual, "dynamically created property");
		 
		 Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
//		 axioms.add(axiom4);
		 axioms.add(axiom5);
		 manager.addAxioms(ontology, axioms);
		 
		 
		 File f2=new File(basePath+"/classes results1.owl");
		 OWLOntologyManager savingManager = OWLManager.createOWLOntologyManager();
		 OWLDocumentFormat ontologyFormat = manager.getOntologyFormat(ontology);

		 
		 savingManager.saveOntology(ontology,ontologyFormat ,new FileOutputStream(f2));;

		 System.out.println(ontology);
	}
}
