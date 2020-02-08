package translation.simple;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import CAEX215.AttributeType;
import CAEX215.CAEXObject;
import CAEX215.InterfaceClassType;
import constants.AMLObjectPropertyIRIs;

/**
 * Transformation handler for AML ExternalInterface
 * @author hua
 *
 */

public class EIHandler extends AMLEntityHandler{

	private OWLDataFactory factory;
	private AttributeHandler attr_handler;
	
	public EIHandler(OWLOntologyManager manager, String ns) {
		// TODO Auto-generated constructor stub
		this.factory = manager.getOWLDataFactory();
		this.ns = ns;
		this.manager = manager;
		this.attr_handler = new AttributeHandler(manager, ns);
	}
	
	@Override
	public OWLEntity cvt2Owl(CAEXObject obj, OWLOntology ont) {
		// TODO Auto-generated method stub

//		InterfaceClassType ei = (InterfaceClassType)obj;
//		IRI iri;
//		if(ei.getID() != null) {
////			iri = IRI.create(ns + ei.getName() + "_" + ei.getID());
//			// extend simple name to the full path to cope with ILP config	
////			iri = IRI.create(ns + "[ei:" + obj.getName() + ":" + obj.getID() + "]");
//			iri = IRI.create(ns + "ei_" + obj.getName() + "_" + obj.getID());
//		}
//		else {
////			iri = IRI.create(ns + ei.getName());
////			iri = IRI.create(ns + "[ei:" + obj.getName() +"]");
//			iri = IRI.create(ns + "ei_" + obj.getName());
//		}
//			
//		OWLNamedIndividual ind_ei = factory.getOWLNamedIndividual(iri);			
		OWLNamedIndividual ind_ei = factory.getOWLNamedIndividual(createIRI(obj));
		return ind_ei;
	}

	@Override
	public void add2Owl(CAEXObject obj, OWLEntity entity, OWLOntology ont) {
		// TODO Auto-generated method stub
		
		InterfaceClassType ei = (InterfaceClassType)obj;
		OWLNamedIndividual ind_ei;
		
		// create the OWL entity for the EI
		if(entity.getEntityType().equals(EntityType.NAMED_INDIVIDUAL)) {
			ind_ei = (OWLNamedIndividual) cvt2Owl(ei, ont);		
		}else {
			ind_ei = factory.getOWLNamedIndividual(IRI.create(ns + ei.getName()));
		}
						
		for(AttributeType attr : ei.getAttribute()) {				
				attr_handler.add2Owl(attr, ind_ei, ont);
		}								
		OWLDeclarationAxiom ax_ei = factory.getOWLDeclarationAxiom(ind_ei);
		manager.applyChange(new AddAxiom(ont, ax_ei));
		
		// add semantic annotation to AML structure
//		OWLDataPropertyExpression hasAMLSemantic = factory.getOWLDataProperty(AMLDataPropertyIRIs.HAS_AML_SEMANTIC);
//		OWLDataPropertyAssertionAxiom ax_hasAMLSemantic = factory.getOWLDataPropertyAssertionAxiom(hasAMLSemantic, ind_ei, "ExternalInterface");
//		manager.applyChange(new AddAxiom(ont, ax_hasAMLSemantic));
		
		//TODO: change to AMLClassIRI
		OWLAnnotation hasSemantic = factory.getOWLAnnotation(factory.getRDFSComment(), factory.getOWLLiteral("ExternalInterface")); 
		OWLAnnotationAssertionAxiom ax_hasSemantic = factory.getOWLAnnotationAssertionAxiom(ind_ei.getIRI(), hasSemantic);
		manager.applyChange(new AddAxiom(ont, ax_hasSemantic));

		OWLAnnotation hasLabel = factory.getOWLAnnotation(factory.getRDFSLabel(), factory.getOWLLiteral(obj.getName()));
		OWLAnnotationAssertionAxiom axHasLabel = factory.getOWLAnnotationAssertionAxiom(ind_ei.getIRI(), hasLabel);
		manager.applyChange(new AddAxiom(ont, axHasLabel));
		
		// handle the EI in case it belongs to an AML class
		if(entity.getEntityType().equals(EntityType.CLASS)) {			
			OWLClass cl = (OWLClass) entity;	
			OWLSubClassOfAxiom ax_sub;
			// class of all individuals which has the given interface
			OWLClassExpression somethingHasInterface = factory.getOWLObjectHasValue(factory.getOWLObjectProperty(AMLObjectPropertyIRIs.HAS_EXTERNAL_INTERFACE),
	                ind_ei);				
			// make the aml class subclass of this class
			ax_sub = factory.getOWLSubClassOfAxiom(cl, somethingHasInterface);
			this.manager.applyChange(new AddAxiom(ont, ax_sub));
		}
		
		// handle the EI in case it belongs to an AML instance
		if(entity.getEntityType().equals(EntityType.NAMED_INDIVIDUAL)) {			
			OWLObjectPropertyExpression hasInterface = factory.getOWLObjectProperty(AMLObjectPropertyIRIs.HAS_EXTERNAL_INTERFACE);
			OWLObjectPropertyAssertionAxiom ax_hasInterface = factory.getOWLObjectPropertyAssertionAxiom(hasInterface, (OWLNamedIndividual) entity, ind_ei);
			manager.applyChange(new AddAxiom(ont, ax_hasInterface));
		}		
		
		// Add class membership of this EI to its Interface Class
		if(ei.getRefBaseClassPath() != null) {
			String[] tokens = ei.getRefBaseClassPath().split("/");
			int num_tokens = tokens.length;
			String base = tokens[num_tokens-1];
			OWLClassAssertionAxiom assertion = factory.getOWLClassAssertionAxiom(factory.getOWLClass(IRI.create(ns + base)), ind_ei);
			manager.applyChange(new AddAxiom(ont, assertion));
		}
		

	}

	@Override
	public IRI createIRI(CAEXObject obj) {
		InterfaceClassType ei = (InterfaceClassType)obj;
		if(ei.getID() != null) {
			return IRI.create(ns + "ei_" + obj.getName() + "_" + obj.getID());
		}
		else {
			return IRI.create(ns + "ei_" + obj.getName());
		}
	}
	
	public static String deserializeIRI(IRI iri) {
		
		String post = iri.getShortForm();
		
		String[] tokens = post.split("_");
		
		// the id of the IE shall be always the last element in tokens
		// if the id is standard: parts of it shall be concatenated by "-"
		if(tokens != null && tokens.length>0)
			return tokens[tokens.length-1];
		else
			return null;
	}
	
}
