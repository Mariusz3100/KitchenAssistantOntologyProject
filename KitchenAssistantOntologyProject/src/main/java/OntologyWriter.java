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
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
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

	
	public static final String categoryNameIriSuffix = "#name";
	public static final String baseIdNameIriSuffix = "#baseId";
	public static final String compoundNameIriSuffix = "#compoundId";
	public static final String relevanceNameIriSuffix = "#relevance";
//	public static final String parentNameIriSuffix = "#parent";
	public static final String subcategoryNameIriSuffix = "#subcategoryOf";
	public static final String parentingPrefixNameIriSuffix = "#parentingPrefix";

	
	
	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		ShopComCategory parseJsonCategory=new ShopComCategory("a", "b", "c", 0);
		ShopComCategory parseJsonCategory2=new ShopComCategory("a1", "b1", "c1", 0);
		parseJsonCategory.setParentCategory(parseJsonCategory2);
		parseJsonCategory2.addChildCategory(parseJsonCategory);
		File f=new File(basePath+"/testResults.owl");

		createNewOntology(parseJsonCategory2,f);
	}



	public static void createNewOntology(ShopComCategory parseJsonCategory,File target)
			throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		String ontologyIRI="http://www.semanticweb.org/mariusz/ontologies/2019/3/dynamicOntology";

		OWLOntologyManager inManager = OWLManager.createOWLOntologyManager();
		File f=new File(basePath+"/classes.owl");

		inManager.loadOntologyFromOntologyDocument(new FileDocumentSource(f));
		

		OWLOntology baseOntology = readBaseOntology(inManager);


		OWLOntology modifiedOntology = modifyOntology(parseJsonCategory,inManager, baseOntology);

		
		File outFile=new File(basePath+"/results.owl");
		OWLDocumentFormat ontologyFormat = inManager.getOntologyFormat(baseOntology);
		OWLOntologyManager outManager = OWLManager.createOWLOntologyManager();

		 outManager.saveOntology(modifiedOntology,ontologyFormat,IRI.create(target));
	}



	private static OWLOntology modifyOntology(ShopComCategory parseJsonCategory, OWLOntologyManager inManager, OWLOntology baseOntology) {
		 OWLDataFactory factory = inManager.getOWLDataFactory();


		 Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

		 createIndividualAxioms(parseJsonCategory,factory, axioms);
		 
		 inManager.addAxioms(baseOntology,axioms);
		 
		 return baseOntology;
	}



	private static OWLNamedIndividual createIndividualAxioms(ShopComCategory parseJsonCategory, OWLDataFactory factory, 
			Set<OWLAxiom> axioms) {
		 OWLNamedIndividual individual=new OWLNamedIndividualImpl(IRI.create(ontologyBaseIri+"#"+parseJsonCategory.getCompoundId()));
		 setCategoryClass(factory, individual,axioms);
		 
		addPropertyAxiom(factory, individual, axioms, ontologyBaseIri + categoryNameIriSuffix,parseJsonCategory.getName());
		
		addPropertyAxiom(factory, individual, axioms, ontologyBaseIri + baseIdNameIriSuffix, parseJsonCategory.getBasicId());

		addPropertyAxiom(factory, individual, axioms, ontologyBaseIri + compoundNameIriSuffix, parseJsonCategory.getCompoundId());
		addPropertyAxiom(factory, individual, axioms, ontologyBaseIri + relevanceNameIriSuffix, Integer.toString(parseJsonCategory.getRelevance()));
		addPropertyAxiom(factory, individual, axioms, ontologyBaseIri + parentingPrefixNameIriSuffix, parseJsonCategory.getParentingPrefix()==null?"": parseJsonCategory.getParentingPrefix());

		
		if(parseJsonCategory.getChildCategories()!=null&&parseJsonCategory.getChildCategories().size()>0) {
			for(ShopComCategory childCategory:parseJsonCategory.getChildCategories()) {
				OWLNamedIndividual createdChild = createIndividualAxioms(childCategory,factory,axioms);
				//addPropertyAxiom(factory, individual, axioms, ontologyBaseIri + "#"+subcategoryNameIriSuffix, parseJsonCategory.getCompoundId());
				addsubcategoryAxiom(factory, createdChild,individual, axioms);
			}
		}
		return individual;
		
	}



	private static void setCategoryClass(OWLDataFactory factory, OWLNamedIndividual individual, Set<OWLAxiom> axioms) {
		OWLClass categoryClass = factory.getOWLClass(categoryIri);
		 OWLClassAssertionAxiom owlClassAssertionAxiom = factory.getOWLClassAssertionAxiom(categoryClass, individual);
		 axioms.add(owlClassAssertionAxiom);
	}



	private static void addPropertyAxiom(OWLDataFactory factory, OWLNamedIndividual individual, Set<OWLAxiom> axioms,
			String propertyName, String propertyValue) {
		OWLDataProperty nameProperty = factory.getOWLDataProperty(IRI.create(propertyName));
		 
		OWLAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(nameProperty, individual, propertyValue);
		 axioms.add(axiom);
	}

	private static void addsubcategoryAxiom(OWLDataFactory factory, OWLNamedIndividual parent,OWLNamedIndividual child, Set<OWLAxiom> axioms) {
		OWLObjectProperty owlObjectProperty = factory.getOWLObjectProperty(IRI.create(ontologyBaseIri+subcategoryNameIriSuffix));

		OWLAxiom axiom = factory.getOWLObjectPropertyAssertionAxiom(owlObjectProperty, parent,child);
		 axioms.add(axiom);
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
