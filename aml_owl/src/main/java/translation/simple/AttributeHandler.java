package translation.simple;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import CAEX215.AttributeType;
import CAEX215.CAEXObject;
import constants.AMLObjectPropertyIRIs;

public class AttributeHandler extends AMLEntityHandler{	
	
	private OWLDataFactory factory;
	private OWLDataFactory datatype_factory;
	
	public AttributeHandler(OWLOntologyManager manager, String ns) {
		// TODO Auto-generated constructor stub
		this.factory = manager.getOWLDataFactory();
		this.datatype_factory = manager.getOWLDataFactory();
		this.ns = ns;
		this.manager = manager;
	}

	@Override
	public OWLEntity cvt2Owl(CAEXObject obj, OWLOntology ont) {
		// TODO Auto-generated method stub			
//		AttributeType attr = (AttributeType) obj;
//		IRI iri = IRI.create(ns + "has_" + attr.getName());
		OWLDataProperty hasAttribute = factory.getOWLDataProperty(createIRI(obj));

		return hasAttribute;
	}
	
	public OWLDatatype getOWLDatatype(String aml_datatype) {
		if(aml_datatype != null){
			if(aml_datatype.contains("string")) {			
				return datatype_factory.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI());			
			}else if(aml_datatype.contains("double")) {
				return datatype_factory.getOWLDatatype(OWL2Datatype.XSD_DOUBLE.getIRI());			
			}else if(aml_datatype.contains("unsignedShort")) {
//				return datatype_factory.getOWLDatatype(OWL2Datatype.XSD_UNSIGNED_SHORT.getIRI());
				return datatype_factory.getOWLDatatype(OWL2Datatype.XSD_DOUBLE.getIRI());		
			}else if(aml_datatype.contains("bool")) {
//				return datatype_factory.getOWLDatatype(OWL2Datatype.XSD_BOOLEAN.getIRI());	
				return datatype_factory.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI());
			}else if(aml_datatype.contains("positiveInteger")) {
//				return datatype_factory.getOWLDatatype(OWL2Datatype.XSD_POSITIVE_INTEGER.getIRI());
				return datatype_factory.getOWLDatatype(OWL2Datatype.XSD_DOUBLE.getIRI());		
			}else if(aml_datatype.contains("anyURI")) {
//				return datatype_factory.getOWLDatatype(OWL2Datatype.XSD_ANY_URI.getIRI());
				return datatype_factory.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI());		
			}else if(aml_datatype.contains("unsignedInt")) {
//				return datatype_factory.getOWLDatatype(OWL2Datatype.XSD_UNSIGNED_INT.getIRI());
				return datatype_factory.getOWLDatatype(OWL2Datatype.XSD_DOUBLE.getIRI());		
			}else if(aml_datatype.contains("long")) {
//				return datatype_factory.getOWLDatatype(OWL2Datatype.XSD_LONG.getIRI());	
				return datatype_factory.getOWLDatatype(OWL2Datatype.XSD_DOUBLE.getIRI());		
			}else if(aml_datatype.contains("nonPositiveInteger")) {
//				return datatype_factory.getOWLDatatype(OWL2Datatype.XSD_NON_POSITIVE_INTEGER.getIRI());
				return datatype_factory.getOWLDatatype(OWL2Datatype.XSD_DOUBLE.getIRI());		
			}else if(aml_datatype.contains("nonNegativeInteger")) {
//				return datatype_factory.getOWLDatatype(OWL2Datatype.XSD_NON_NEGATIVE_INTEGER.getIRI());
				return datatype_factory.getOWLDatatype(OWL2Datatype.XSD_DOUBLE.getIRI());		
			}else if(aml_datatype.contains("complexType")) {
				return datatype_factory.getTopDatatype();
			}
			else {
				System.out.println("The datatype " + aml_datatype + " is not found in OWL");
				return datatype_factory.getTopDatatype();
			}
		}else
			return datatype_factory.getTopDatatype();
	}
	
	// TODO: change this with utility function
	public String getAttributeValue(AttributeType attribute) {
		if(attribute.getValue() != null) {
			if(attribute.getValue().getMixed().size() > 0) {
				if(attribute.getValue().getMixed().getValue(0) != null) {
					return (String) attribute.getValue().getMixed().getValue(0);					
				}
			}
		}
		
		return null;
	}
	
	private void handleAttribute(AttributeType attribute, OWLEntity entity, OWLOntology ont, IRI iri) {
	
		OWLDataProperty hasAttribute = factory.getOWLDataProperty(iri);
		String datatype = attribute.getAttributeDataType();		
		
		OWLDatatype owlDatatype = getOWLDatatype(datatype);
		OWLDataPropertyRangeAxiom rangeAxiom = factory.getOWLDataPropertyRangeAxiom(
				hasAttribute, owlDatatype);
		this.manager.applyChange(new AddAxiom(ont, rangeAxiom));
		String value = getAttributeValue(attribute);
		
		// In case of class attribute
		if(entity.getEntityType().equals(EntityType.CLASS)) {			
			OWLClass cl = (OWLClass) entity;	
			OWLSubClassOfAxiom ax_sub;
			if(value != null) {
				// class of all individuals which has the attribute with given value
				OWLClassExpression somethingHasAttribute = factory.getOWLDataHasValue(factory.getOWLDataProperty(iri),
		                factory.getOWLLiteral(value, owlDatatype));				
				// make the aml class subclass of this class
				OWLClassExpression cl_hasAttribute = factory.getOWLObjectSomeValuesFrom(factory.getOWLObjectProperty(AMLObjectPropertyIRIs.HAS_ATTRIBUTE),
						somethingHasAttribute);
				ax_sub = factory.getOWLSubClassOfAxiom(cl, cl_hasAttribute);
			}else{
				OWLClassExpression somethingHasAttribute = factory.getOWLDataSomeValuesFrom(factory.getOWLDataProperty(iri),
						factory.getTopDatatype());
				ax_sub = factory.getOWLSubClassOfAxiom(cl, somethingHasAttribute);
			}
			this.manager.applyChange(new AddAxiom(ont, ax_sub));
		}
		
		// In case of instance attribute
		if(entity.getEntityType().equals(EntityType.NAMED_INDIVIDUAL)) {			
			OWLDataPropertyAssertionAxiom instanceHasAttribute;

//			System.out.println(entity.getIRI().toString());
//			System.out.println(iri.toString());
//			System.out.println(attribute.getName());
			if(value != null)
			{
				OWLLiteral literal = factory.getOWLLiteral(value, owlDatatype);
				instanceHasAttribute = factory.getOWLDataPropertyAssertionAxiom(hasAttribute, (OWLNamedIndividual)entity, literal);
				this.manager.applyChange(new AddAxiom(ont, instanceHasAttribute));				
			}else
			{
				// here we got ontology inconsistent problem if we keep this simple model 
				OWLLiteral literal = factory.getOWLLiteral("", owlDatatype);
				instanceHasAttribute = factory.getOWLDataPropertyAssertionAxiom(hasAttribute, (OWLNamedIndividual)entity, literal);
//				this.manager.applyChange(new AddAxiom(ont, instanceHasAttribute));
			}
			// We ignore this case
//			else	
//				instanceHasAttribute = factory.getOWLDataPropertyAssertionAxiom(hasAttribute, (OWLNamedIndividual)entity, factory.getOWLLiteral(""));
			
//			this.manager.applyChange(new AddAxiom(ont, instanceHasAttribute));
		}
		
	}
	
	private void handleSubAttributes(AttributeType attribute, OWLEntity entity, OWLOntology ont, String name) {
		
		// convert structured attributes to flat cascaded attributes
		if(attribute.getAttribute().size() == 0) {							
			IRI iri = IRI.create(ns + "has" + name.substring(0, 1).toUpperCase() + name.substring(1));
//			System.out.println(iri.toString());
			handleAttribute(attribute, entity, ont, iri);
		}else {
			for(AttributeType sub : attribute.getAttribute()) {
				String new_name = name + "_" + sub.getName(); 
//				System.out.println("new_name: " + new_name);
				handleSubAttributes(sub, entity, ont, new_name);
			}			
		}
		
	}

	@Override
	public void add2Owl(CAEXObject obj, OWLEntity entity, OWLOntology ont) {
		// TODO Auto-generated method stub
		AttributeType attribute = (AttributeType) obj;
		
		// convert structured attributes to flat cascaded attributes
		if(attribute.getAttribute().size() == 0) {
			IRI iri = IRI.create(ns + "has" + attribute.getName().substring(0, 1).toUpperCase() + attribute.getName().substring(1));
			handleAttribute(attribute, entity, ont, iri);
		}else {
			System.out.println("Attribute " + attribute.getName() + " has sub attributes");
			handleSubAttributes(attribute, entity, ont, attribute.getName());
		}		
	}
	
	
	private OWLClassExpression getConstraintExpression(AttributeType attr, OWLOntology ont, String name) {
				
		if(attr.getAttribute().size() != 0)
		{
			return null;
		}else
		{
			IRI iri = IRI.create(ns + "has" + name.substring(0, 1).toUpperCase() + name.substring(1));
			OWLDataProperty hasAttribute = factory.getOWLDataProperty(iri);		
			OWLDatatype datatype = this.getOWLDatatype(attr.getAttributeDataType());
			OWLDataPropertyRangeAxiom rangeAxiom = factory.getOWLDataPropertyRangeAxiom(
					hasAttribute, datatype);
			this.manager.applyChange(new AddAxiom(ont, rangeAxiom));
						
			String value = this.getAttributeValue(attr);		
			
			if(value != null) {
				// class of all individuals which has the attribute with given value								
				OWLLiteral literal = factory.getOWLLiteral(value, datatype);
				return factory.getOWLDataHasValue(hasAttribute, literal);
			}else{
				return null;
			}			
		}		
	}
	
	
	public Set<OWLClassExpression> getConstraintExpressions(AttributeType attr, OWLOntology ont, String name) {
	
		Set<OWLClassExpression> constraints = new HashSet<OWLClassExpression>();
		
//		for(AttributeType sub : attr.getAllAttributes()) {
//			System.out.println(sub.getName());
//		}
		
		if(attr.getAttribute().size() == 0) {
			OWLClassExpression constraint = getConstraintExpression(attr, ont, name);
			if(constraint != null)
				constraints.add(constraint);
		}else
		{
			for(AttributeType subAttribute : attr.getAttribute()) {
				constraints.addAll(getConstraintExpressions(subAttribute, ont, name + "_" + subAttribute.getName()));
			}
		}
	
		return constraints;
	}

	@Override
	public IRI createIRI(CAEXObject obj) {
		AttributeType attr = (AttributeType) obj;
		return IRI.create(ns + "has" + attr.getName().substring(0, 1).toUpperCase() + attr.getName().substring(1));
	}
	
	public static String[] deserializeIRItoArray(IRI iri) {
		
		// name of the attribute without the heading "has_"
		String name = deserializeIRI(iri);
		
		// return the rest of the name separated by "_"
		// here we assume that each nested name does not contain any "_" inside
		// TODO: consider a better name split symbol
		return name.split("_");
	}
	
	public static String deserializeIRI(String iri) {
				
		String[] tokens = iri.split("_");
		
		// name itself can contain underscore
		// so we return everything besides the first element which is "has"
		if(tokens != null && tokens.length>1) {
			// need +1 to avoid "_"
			return iri.substring(iri.indexOf("_")+1);
		}			
		else
			return null;
	}
	
	public static String deserializeIRI(IRI iri) {
		
		String post = iri.getShortForm();
		
		return deserializeIRI(post);
	}

//	
//	private OWLClass attribute_to_owl(AttributeType attr, String ns) {		
//		
//		OWLClass cl = factory.getOWLClass(IRI.create(ns + attr.getName()));
//		OWLSubClassOfAxiom ax_attribute_sub = factory.getOWLSubClassOfAxiom(cl, factory.getOWLClass(AMLClassIRIs.ATTRIBUTE));
//		AddAxiom addAx_attribute_sub = new AddAxiom(this.new_ont, ax_attribute_sub);
//		this.manager.applyChange(addAx_attribute_sub);
//		return cl;
//	}
//	
//	private void attribute_to_owl(OWLClass cl, AttributeType attr, String ns) {
//		
//		OWLClass cl_attr = attribute_to_owl(attr, ns);
//				
//		if(attr.getValue() != null) {
//			if(attr.getValue().getMixed().size() > 0) {
//				if(attr.getValue().getMixed().getValue(0) != null) {
//					String value = (String) attr.getValue().getMixed().getValue(0);
//					
//					OWLClassExpression cl_hasValue = factory.getOWLDataHasValue(this.manager.getOWLDataFactory().getOWLDataProperty(AMLDataPropertyIRIs.HAS_VALUE),
//			                factory.getOWLLiteral(value));
//					OWLClassExpression cl_attributeHasValue = factory.getOWLObjectIntersectionOf(cl_attr, cl_hasValue);					
//					OWLClassExpression cl_hasAttribute = factory.getOWLObjectSomeValuesFrom(this.manager.getOWLDataFactory().getOWLObjectProperty(AMLObjectPropertyIRIs.HAS_ATTRIBUTE),
//							cl_attributeHasValue);
//					OWLSubClassOfAxiom ax = factory.getOWLSubClassOfAxiom(cl, cl_hasAttribute);
//					AddAxiom addAx_hasValue = new AddAxiom(this.new_ont, ax);
//					this.manager.applyChange(addAx_hasValue);
//				}
//			}
//		}else
//		{
//			OWLClassExpression cl_hasAttribute = factory.getOWLObjectSomeValuesFrom(this.manager.getOWLDataFactory().getOWLObjectProperty(AMLObjectPropertyIRIs.HAS_ATTRIBUTE),cl_attr);
//			OWLSubClassOfAxiom ax_hasAttribute = factory.getOWLSubClassOfAxiom(cl, cl_hasAttribute);					
//			AddAxiom addAx_hasAttribute = new AddAxiom(this.new_ont, ax_hasAttribute);
//			this.manager.applyChange(addAx_hasAttribute);
//		}		
//	}
	
}
