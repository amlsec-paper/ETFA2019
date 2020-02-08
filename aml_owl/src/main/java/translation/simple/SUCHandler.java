package translation.simple;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import CAEX215.AttributeType;
import CAEX215.CAEXObject;
import CAEX215.InterfaceClassType;
import CAEX215.InternalElementType;
import CAEX215.InternalLinkType;
import CAEX215.SupportedRoleClassType;
import CAEX215.SystemUnitFamilyType;
import constants.AMLObjectPropertyIRIs;

public class SUCHandler extends AMLEntityHandler {

	private OWLDataFactory factory;
	private AttributeHandler attribute_handler;
	private EIHandler ic_handler;
	private IEHandler ie_handler;
	
	public SUCHandler(OWLOntologyManager manager, String ns) {
		// TODO Auto-generated constructor stub
		this.factory = manager.getOWLDataFactory();
		this.ns = ns;
		this.manager = manager;
		this.attribute_handler = new AttributeHandler(manager, ns);
		this.ic_handler = new EIHandler(manager, ns);
		this.ie_handler = new IEHandler(manager, ns);
	}
	
	@Override
	public OWLEntity cvt2Owl(CAEXObject obj, OWLOntology ont) {
		// TODO Auto-generated method stub		
//		OWLNamedIndividual ind_suc = factory.getOWLNamedIndividual(IRI.create(ns + obj.getName()));
		// extend simple name to the full path to cope with ILP config
//		OWLNamedIndividual ind_suc = factory.getOWLNamedIndividual(IRI.create(ns + "[suc:" + obj.getName() + "]"));
		
		
//		OWLNamedIndividual ind_suc = factory.getOWLNamedIndividual(IRI.create(ns + "suc_" + obj.getName()));
		OWLNamedIndividual ind_suc = factory.getOWLNamedIndividual(createIRI(obj));
		return ind_suc;		
	}

	@Override
	public void add2Owl(CAEXObject obj, OWLEntity entity, OWLOntology ont) {
		// TODO Auto-generated method stub
		
		OWLNamedIndividual ind_suf = (OWLNamedIndividual) cvt2Owl(obj, ont);
		SystemUnitFamilyType suf = (SystemUnitFamilyType) obj;
		OWLDeclarationAxiom ax_suf = factory.getOWLDeclarationAxiom(ind_suf);
		manager.applyChange(new AddAxiom(ont, ax_suf));
		
		for(AttributeType attr : suf.getAttribute()) {
			attribute_handler.add2Owl(attr, ind_suf, ont);
		}
			
		for(InterfaceClassType intc : suf.getExternalInterface()) {
			ic_handler.add2Owl(intc, ind_suf, ont);
		}
				
		for(SupportedRoleClassType rf : suf.getSupportedRoleClass()) {
			String role_path = rf.getRefRoleClassPath();
			String[] tokens = role_path.split("/");
			String role_name = tokens[tokens.length-1];
			OWLClassAssertionAxiom assertion = factory.getOWLClassAssertionAxiom(factory.getOWLClass(IRI.create(ns + role_name)), ind_suf);
			manager.applyChange(new AddAxiom(ont, assertion));
		}				
		
		for(InternalElementType ie : suf.getInternalElement()) {
			this.ie_handler.add2Owl(ie, ind_suf, ont);
		}	
		
		/**
		 * TODO: right now, we are connecting two IEs directly, so no Object for Link
		 * 		- we might want to specify the type of the link 
		 * 		- we add link to a pair of EI based on their name, which is given by the AML data, however not very much meaningful
		 */
		for(InternalLinkType il : suf.getInternalLink()) {
			System.out.println("new link: " + il);			
			OWLIndividual indA = null, indB = null;
			for(InternalElementType ie : suf.getInternalElement()) {
				// find the IE which owns refSidePartA
				if(ie.getID().equals(il.getRefPartnerSideA().split(":")[0])) {
					for(InterfaceClassType ei : ie.getExternalInterface()) {
						// find the refSidePartA 
						if(ei.getName().equals(il.getRefPartnerSideA().split(":")[1])) {							
							for(OWLIndividual ind : ont.getIndividualsInSignature()) {
								// find the owl individual for A
								if(ind.asOWLNamedIndividual().getIRI().toString().contains(ei.getName())){
									indA = ind;
								}
							}
						}						
					}
				}
				// find the IE which owns refSidePartB
				if(ie.getID().equals(il.getRefPartnerSideB().split(":")[0])) {
					for(InterfaceClassType ei : ie.getExternalInterface()) {
						// find the refSidePartB
						if(ei.getName().equals(il.getRefPartnerSideB().split(":")[1])) {							
							for(OWLIndividual ind : ont.getIndividualsInSignature()) {
								// find the owl individual for B
								if(ind.asOWLNamedIndividual().getIRI().toString().contains(ei.getName())){
									indB = ind;
								}
							}
						}						
					}
				}
			}
			
			if(indA != null && indB != null) {
				System.out.println("indA:" + indA);
				System.out.println("indB:" + indB);
				OWLObjectPropertyExpression ile = factory.getOWLObjectProperty(AMLObjectPropertyIRIs.HAS_INTERNAL_LINK);
				OWLObjectPropertyAssertionAxiom ax_AB = factory.getOWLObjectPropertyAssertionAxiom(ile, indA, indB);
				OWLObjectPropertyAssertionAxiom ax_BA = factory.getOWLObjectPropertyAssertionAxiom(ile, indB, indA);
				manager.applyChange(new AddAxiom(ont, ax_AB));
				manager.applyChange(new AddAxiom(ont, ax_BA));
			}else
				System.out.println("can not find interface individuals for the link: " + il);
			
//			for(OWLIndividual ieA : ont.getIndividualsInSignature()) {
//				if(ieA.asOWLNamedIndividual().getIRI().toString().contains(idA)){
//					// get the EI					
//					for(OWLIndividual ieB : ont.getIndividualsInSignature()) {
//						if(ieB.asOWLNamedIndividual().getIRI().toString().contains(idB)){
//							System.out.println("icA:" + ieA);
//							System.out.println("icB:" + ieB);
//							OWLObjectPropertyExpression ile = factory.getOWLObjectProperty(AMLObjectPropertyIRIs.CONNECTED_TO);
//							OWLObjectPropertyAssertionAxiom ax_AB = factory.getOWLObjectPropertyAssertionAxiom(ile, ieA, ieB);
//							OWLObjectPropertyAssertionAxiom ax_BA = factory.getOWLObjectPropertyAssertionAxiom(ile, ieB, ieA);
//							manager.applyChange(new AddAxiom(ont, ax_AB));
//							manager.applyChange(new AddAxiom(ont, ax_BA));
//						}
//					}
//				}
//			}
			
//			// add individual
//			OWLNamedIndividual ind_il = factory.getOWLNamedIndividual(IRI.create(ns + "link_" + idA + "-" + idB));
//			OWLDeclarationAxiom ax_il = factory.getOWLDeclarationAxiom(ind_il);
//			manager.applyChange(new AddAxiom(ont, ax_il));
//			// add annotation
//			OWLAnnotation hasSemantic = factory.getOWLAnnotation(factory.getRDFSComment(), factory.getOWLLiteral("InternalLink"));
//			OWLAnnotationAssertionAxiom ax_hasSemantic = factory.getOWLAnnotationAssertionAxiom(ind_il.getIRI(), hasSemantic);
//			manager.applyChange(new AddAxiom(ont, ax_hasSemantic));
//			// add to suc
//			OWLObjectProperty hasIL = factory.getOWLObjectProperty(IRI.create(ns + "has_link"));
		}
		


		// add annotation to AML structure
//		OWLDataPropertyExpression hasSemantic = factory.getOWLDataProperty(AMLDataPropertyIRIs.HAS_AML_SEMANTIC);
//		OWLDataPropertyAssertionAxiom ax_hasSemantic = factory.getOWLDataPropertyAssertionAxiom(hasSemantic, ind_suf, "SystemUnitClass");
//		manager.applyChange(new AddAxiom(ont, ax_hasSemantic));	
		
		// TODO: change to AMLClassIRI
		OWLAnnotation hasSemantic = factory.getOWLAnnotation(factory.getRDFSComment(), factory.getOWLLiteral("SystemUnitClass")); 
		OWLAnnotationAssertionAxiom ax_hasSemantic = factory.getOWLAnnotationAssertionAxiom(ind_suf.getIRI(), hasSemantic);
		manager.applyChange(new AddAxiom(ont, ax_hasSemantic));

		OWLAnnotation hasLabel = factory.getOWLAnnotation(factory.getRDFSLabel(), factory.getOWLLiteral(obj.getName()));
		OWLAnnotationAssertionAxiom axHasLabel = factory.getOWLAnnotationAssertionAxiom(ind_suf.getIRI(), hasLabel);
		manager.applyChange(new AddAxiom(ont, axHasLabel));


//		OWLClassAssertionAxiom isSUC = factory.getOWLClassAssertionAxiom(factory.getOWLClass(AMLClassIRIs.SYSTEMUNIT_CLASS), ind_suf);
//		manager.applyChange(new AddAxiom(ont, isSUC));
		
		for(SystemUnitFamilyType sub : suf.getSystemUnitClass()) {
			add2Owl(sub, ind_suf, ont);
		}
		
	}

	@Override
	public IRI createIRI(CAEXObject obj) {
		return IRI.create(ns + "suc_" + obj.getName());
	}
	
	public static String deserializeIRI(IRI iri) {
		
		String post = iri.getShortForm();
		
		String[] tokens = post.split("_");
		
		// name itself can contain underscore
		// so we return everything besides the first element which is "suc"
		if(tokens != null && tokens.length>1) {
			// need +1 to avoid "_"
			return post.substring(post.indexOf("_")+1);
		}			
		else
			return null;
	}
	
	

}
