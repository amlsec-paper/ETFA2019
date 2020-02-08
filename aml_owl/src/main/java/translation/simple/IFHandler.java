package translation.simple;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import CAEX215.CAEXObject;
import CAEX215.InterfaceFamilyType;

/**
 * A transformation handler for AML interface family type
 * @author hua
 *
 */

public class IFHandler extends AMLClassHandler {
	
	private OWLDataFactory factory;
	private AttributeHandler attribute_handler;
	
	public IFHandler(OWLOntologyManager manager, String ns) {
		// TODO Auto-generated constructor stub
		this.factory = manager.getOWLDataFactory();
		this.ns = ns;
		this.manager = manager;
		this.attribute_handler = new AttributeHandler(manager, ns);
	}

	@Override
	public void add2Owl(CAEXObject obj, OWLEntity entity, OWLOntology ont) {
		// TODO Auto-generated method stub
		
		OWLClass owl_icf = (OWLClass) cvt2Owl(obj, ont);	
		if(owl_icf == null)
			return;
		
		InterfaceFamilyType icf = (InterfaceFamilyType) obj;
		
		// RefBaseClassPath will be transformed to sub class in OWL		
		String path = icf.getRefBaseClassPath();		
		if(path != null) {
			String[] tokens = path.split("/");
			String base = tokens[tokens.length-1];
			if(!base.equals(obj.getName())) {
				OWLClass owl_base = factory.getOWLClass(IRI.create(ns + base));			
				OWLSubClassOfAxiom sc_ax = factory.getOWLSubClassOfAxiom(owl_icf, owl_base);
				manager.applyChange(new AddAxiom(ont, sc_ax));
			}
		}
		
		
		// interface class as subclass of things that have these attributes
//		for(AttributeType attr : icf.getAttribute()) {
//			attribute_handler.add2Owl(attr, owl_icf, ont);
//		}			

		OWLDeclarationAxiom owl_ic = factory.getOWLDeclarationAxiom(owl_icf);
		manager.applyChange(new AddAxiom(ont, owl_ic));
		
		
//		OWLDataPropertyExpression hasAMLSemantic = factory.getOWLDataProperty(AMLDataPropertyIRIs.HAS_AML_SEMANTIC);
//		OWLDataPropertyAssertionAxiom ax_hasAMLSemantic = factory.getOWLDataPropertyAssertionAxiom(hasAMLSemantic, owl_icf, "InterfaceClass");
//		manager.applyChange(new AddAxiom(ont, ax_hasAMLSemantic));

		// TODO: change to AMLClassIRI
		OWLAnnotation hasSemantic = factory.getOWLAnnotation(factory.getRDFSComment(), factory.getOWLLiteral("InterfaceClass")); 
		OWLAnnotationAssertionAxiom ax_hasSemantic = factory.getOWLAnnotationAssertionAxiom(owl_icf.getIRI(), hasSemantic);
		manager.applyChange(new AddAxiom(ont, ax_hasSemantic));

		OWLAnnotation hasLabel = factory.getOWLAnnotation(factory.getRDFSLabel(), factory.getOWLLiteral(obj.getName()));
		OWLAnnotationAssertionAxiom axHasLabel = factory.getOWLAnnotationAssertionAxiom(owl_icf.getIRI(), hasLabel);
		manager.applyChange(new AddAxiom(ont, axHasLabel));

		// Sub interfaces: the hierarchy of sub interfaces in AML has no specific semantics, will be ignored.
		// All sub interfaces will be transformed to flat structures, unless they have subclass relationship to the parent		
		for(InterfaceFamilyType sub_icf : icf.getInterfaceClass()) {
			add2Owl(sub_icf, entity, ont);
		}
		
	}

	@Override
	public IRI createIRI(CAEXObject obj) {
		// TODO Auto-generated method stub
		return null;
	}

}
