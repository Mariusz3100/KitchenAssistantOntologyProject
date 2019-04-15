import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.change.OWLOntologyChangeData;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeVisitor;
import org.semanticweb.owlapi.model.OWLOntologyChangeVisitorEx;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.parameters.Imports;

import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

public class OntologyWriter {
	public static final String basePath="/home/mariusz/development/mgr/workspaces/ontologyWorkspace";
	public static final String ontologyBaseIri="http://www.semanticweb.org/mariusz/ontologies/2019/3/CategoriesOntology";
	public static final String categoryIri="http://www.semanticweb.org/mariusz/ontologies/2019/3/CategoriesOntology#Category";
	public static final String productIri="http://www.semanticweb.org/mariusz/ontologies/2019/3/CategoriesOntology#Product";

	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		ShopComCategory parseJsonCategory=new ShopComCategory("a", "b", "c", 0);
		createNewOntology(parseJsonCategory);
	}



	private static void createNewOntology(ShopComCategory parseJsonCategory)
			throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		String ontologyIRI="http://www.semanticweb.org/mariusz/ontologies/2019/3/dynamicOntology";
		File f=new File(basePath+"/classes.owl");

		OWLOntologyManager inManager = OWLManager.createOWLOntologyManager();
		inManager.loadOntologyFromOntologyDocument(new FileDocumentSource(f));
		

		OWLOntology baseOntology = readBaseOntology(inManager);


		OWLOntology modifiedOntology = modifyOntology(parseJsonCategory,inManager, baseOntology);

		
		File outFile=new File(basePath+"/results.owl");
		OWLDocumentFormat ontologyFormat = inManager.getOntologyFormat(baseOntology);
		OWLOntologyManager outManager = OWLManager.createOWLOntologyManager();

		 outManager.saveOntology(modifiedOntology,ontologyFormat,IRI.create(outFile));
	}



	private static OWLOntology modifyOntology(ShopComCategory parseJsonCategory, OWLOntologyManager inManager, OWLOntology baseOntology) {
		Map<String, OWLClass> OwlClassesMap = createOwlClassesMap(baseOntology);

		 OWLDataFactory factory = inManager.getOWLDataFactory();
		 OWLClass categoryClass = factory.getOWLClass(categoryIri);
		 OWLClass productClass =factory.getOWLClass(productIri);
		 
		 OWLNamedIndividual individual=new OWLNamedIndividualImpl(IRI.create(ontologyBaseIri+"#namedIndividual"));
		 Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

		 OWLDataProperty nameProperty = factory.getOWLDataProperty(IRI.create(ontologyBaseIri + "#name"));

		 OWLAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(nameProperty, individual, "dynamically created property");
		 axioms.add(axiom);
		 
		 inManager.addAxioms(baseOntology,axioms);
		 
		 return baseOntology;
	}



	private static Map<String, OWLClass> createOwlClassesMap(OWLOntology readBaseOntology) {
		Set<OWLClass> classesInSignature = readBaseOntology.getClassesInSignature(Imports.EXCLUDED);
		Map<String,OWLClass> classMap=new HashMap<String,OWLClass>();
		for(OWLClass owlClass:classesInSignature) {
			if(owlClass.getIRI().getIRIString().endsWith(categoryIri)) {
				classMap.put(categoryIri, owlClass);
			}
			if(owlClass.getIRI().getIRIString().endsWith(productIri)) {
				classMap.put(productIri, owlClass);
			}

		}
		if(classMap.size()<2) {
			System.err.println("Not all classes were found");
		}
		return classMap;
	}



	public static OWLOntology readBaseOntology(OWLOntologyManager manager) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {

//		String ontologyIRI="http://www.semanticweb.org/mariusz/ontologies/2019/3/CategoryClassesOntology";
		OWLOntology ontology = manager.getOntology(IRI.create(ontologyBaseIri));
		 OWLDocumentFormat ontologyFormat = manager.getOntologyFormat(ontology);

		
		return ontology;
	}



}
