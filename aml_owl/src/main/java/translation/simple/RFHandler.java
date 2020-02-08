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
import CAEX215.RoleFamilyType;

public class RFHandler extends AMLClassHandler{
	
	private OWLDataFactory factory;
	private AttributeHandler attribute_handler;
	private EIHandler ic_handler;
	
	public RFHandler(OWLOntologyManager manager, String ns) {
		// TODO Auto-generated constructor stub
		this.factory = manager.getOWLDataFactory();
		this.ns = ns;
		this.manager = manager;
		this.attribute_handler = new AttributeHandler(manager, ns);
		this.ic_handler = new EIHandler(manager, ns);
	}

	@Override
	public void add2Owl(CAEXObject obj, OWLEntity entity, OWLOntology ont) {
		// TODO Auto-generated method stub
		
		OWLClass cl = (OWLClass) cvt2Owl(obj, ont);

		RoleFamilyType rf = (RoleFamilyType)obj;
		

//		System.out.println(rf.getName());
		if(rf.getRefBaseClassPath() != null) {			
				
//			System.out.println(rf.getName());
//			if(rf.getName().contains("ArticulatedRobot"))
//				System.out.println(rf.getRefBaseClassPath());			
			String[] tokens = rf.getRefBaseClassPath().split("/");
			int num_tokens = tokens.length;
			String base = tokens[num_tokens-1];
			if(!base.equals(obj.getName())) {
				OWLClass owl_base = factory.getOWLClass(IRI.create(ns + base));
				OWLSubClassOfAxiom sc_ax = factory.getOWLSubClassOfAxiom(cl, owl_base);
				manager.applyChange(new AddAxiom(ont, sc_ax));	
			}
		}

		
		// role class as subclass of things that have these attributes
//		for(AttributeType attr : rf.getAttribute()) {
//			attribute_handler.add2Owl(attr, cl, ont);
//		}
//		
		// role class as subclass of things that have these interfaces
//		for(ExternalInterfaceType ei : rf.getExternalInterface()) {
//			ic_handler.add2Owl(ei, cl, ont);
//		}		
		
		OWLDeclarationAxiom owl_role = factory.getOWLDeclarationAxiom(cl);
		manager.applyChange(new AddAxiom(ont, owl_role));
		
		// TODO: change to AMLClassIRI
		OWLAnnotation hasSemantic = factory.getOWLAnnotation(factory.getRDFSComment(), factory.getOWLLiteral("RoleClass")); 
		OWLAnnotationAssertionAxiom ax_hasSemantic = factory.getOWLAnnotationAssertionAxiom(cl.getIRI(), hasSemantic);
		manager.applyChange(new AddAxiom(ont, ax_hasSemantic));

		OWLAnnotation hasLabel = factory.getOWLAnnotation(factory.getRDFSLabel(), factory.getOWLLiteral(obj.getName()));
		OWLAnnotationAssertionAxiom axHasLabel = factory.getOWLAnnotationAssertionAxiom(cl.getIRI(), hasLabel);
		manager.applyChange(new AddAxiom(ont, axHasLabel));
				
		for(RoleFamilyType sub_rf : rf.getRoleClass()) {
			add2Owl(sub_rf, entity, ont);
		}								
		
	}

	@Override
	public IRI createIRI(CAEXObject obj) {
		// TODO Auto-generated method stub
		return null;
	}

}
