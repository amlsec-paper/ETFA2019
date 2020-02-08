/**
 * 
 */
package constants;

import org.semanticweb.owlapi.model.IRI;

/**
 * @author hua
 *
 */
public final class AMLObjectPropertyIRIs {

	public static final IRI HAS_MAPPING_OBJECT = IRI.create(Consts.aml_pref + "mappingObject");
	
	public static final IRI HAS_ATTRIBUTE_NAME_MAPPING = IRI.create(Consts.aml_pref + "attributeNameMapping");
	
	public static final IRI HAS_INTERFACE_NAME_MAPPING = IRI.create(Consts.aml_pref + "interfaceNameMapping");
	
	public static final IRI HAS_CAEX_FILE = IRI.create(Consts.aml_pref + "cAEXFile");
	
	public static final IRI HAS_CONSTRAINT = IRI.create(Consts.aml_pref + "constraint");
	
	public static final IRI HAS_COPYRIGHT = IRI.create(Consts.aml_pref + "copyright");
	
	public static final IRI HAS_DESCRIPTION = IRI.create(Consts.aml_pref + "description");
	
//	public static final IRI HAS_EXTERNAL_INTERFACE = IRI.create(Consts.aml_pref + "externalInterface");
	public static final IRI HAS_EXTERNAL_INTERFACE = IRI.create(Consts.aml_pref + "hasEI");
	
	public static final IRI IS_EXTERNAL_INTERFACE_OF = IRI.create(Consts.aml_pref + "isEIOf");
	
	public static final IRI HAS_EXTERNAL_REFERENCE = IRI.create(Consts.aml_pref + "externalReference");	
	
//	public static final IRI HAS_ADDITIONAL_INFORMATION = IRI.create(Consts.aml_pref + "additionalInformation");	
	
	public static final IRI HAS_ATTRIBUTE = IRI.create(Consts.aml_pref + "attribute");
	
	public static final IRI HAS_INSTANCE_HIERARCHY = IRI.create(Consts.aml_pref + "instanceHierarchy");
	
	public static final IRI HAS_INTERFACE_CLASS_LIB = IRI.create(Consts.aml_pref + "interfaceClassLib");
	
	public static final IRI HAS_INTERFACE_CLASS = IRI.create(Consts.aml_pref + "interfaceClass");
	
//	public static final IRI HAS_INTERNAL_ELEMENT = IRI.create(Consts.aml_pref + "internalElement");
	public static final IRI HAS_INTERNAL_ELEMENT = IRI.create(Consts.aml_pref + "hasIE");
	
	public static final IRI IS_INTERNAL_ELEMENT_OF = IRI.create(Consts.aml_pref + "isIEOf");
	
//	public static final IRI HAS_INTERNAL_LINK = IRI.create(Consts.aml_pref + "internalLink");
	public static final IRI HAS_INTERNAL_LINK = IRI.create(Consts.aml_pref + "hasIL");
	
	public static final IRI IS_INTERNAL_LINK_OF = IRI.create(Consts.aml_pref + "isILOf");
	
	public static final IRI HAS_NOMINAL_SCALED_TYPE = IRI.create(Consts.aml_pref + "nominalScaledType");
	
	public static final IRI HAS_ORDINAL_SCALED_TYPE = IRI.create(Consts.aml_pref + "ordinalScaledType");
		
	public static final IRI HAS_REF_BASE_SYSTEMUNIT_PATH = IRI.create(Consts.aml_pref + "refBaseSystemUnitPath");
	
	public static final IRI HAS_REFPARTNER_SIDE_A = IRI.create(Consts.aml_pref + "hasRefPartnerSideA");
	
	public static final IRI HAS_REFPARTNER_SIDE_B = IRI.create(Consts.aml_pref + "hasRefPartnerSideB");
	
	public static final IRI HAS_REF_SEMANTIC = IRI.create(Consts.aml_pref + "refSemantic");
	
	public static final IRI HAS_REVISION = IRI.create(Consts.aml_pref + "revision");
	
	public static final IRI HAS_ROLE_CLASS_LIB = IRI.create(Consts.aml_pref + "roleClassLib");
	
	public static final IRI HAS_ROLE_CLASS = IRI.create(Consts.aml_pref + "roleClass");
	
	public static final IRI HAS_ROLE_REQUIREMENTS = IRI.create(Consts.aml_pref + "roleRequirements");
	
	public static final IRI HAS_SUPPORTED_ROLE_CLASS = IRI.create(Consts.aml_pref + "supportedRoleClass");
	
	public static final IRI HAS_SYSTEMUNIT_CLASS_LIB = IRI.create(Consts.aml_pref + "systemUnitClassLib");
	
	public static final IRI HAS_SYSTEMUNIT_CLASS = IRI.create(Consts.aml_pref + "systemUnitClass");
	
	public static final IRI HAS_UNKNOWN_TYPE = IRI.create(Consts.aml_pref + "unknownType");
	
	public static final IRI HAS_VERSION = IRI.create(Consts.aml_pref + "version");	
	
//	public static final IRI HAS_ATTRIBUTE = IRI.create(Consts.aml_pref + "hasAttribute");
	
//	public static final IRI HAS_ATTRIBUTE_VALUE = IRI.create(Consts.aml_pref + "hasAttributeValue");
	
	

// for aml_handmade
//	public static final IRI CONTAINS = IRI.create(Consts.aml_pref + "contains");
//	
//	public static final IRI HAS_ATTRIBUTE_IC = IRI.create(Consts.aml_pref + "hasAttributeIC");
//	
//	public static final IRI HAS_ATTRIBUTE_IE = IRI.create(Consts.aml_pref + "hasAttributeIE");
//	
//	public static final IRI HAS_ATTRIBUTE_RC = IRI.create(Consts.aml_pref + "hasAttributeRC");
//	
//	public static final IRI HAS_ATTRIBUTE_SUC = IRI.create(Consts.aml_pref + "hasAttributeSUC");
//	
//	public static final IRI HAS_INTERFACE = IRI.create(Consts.aml_pref + "hasInterface");
//	
//	public static final IRI HAS_INTERFACE_IE = IRI.create(Consts.aml_pref + "hasInterfaceIE");
//	
//	public static final IRI HAS_INTERFACE_RC = IRI.create(Consts.aml_pref + "hasInterfaceRC");
//	
//	public static final IRI HAS_INTERFACE_SUC = IRI.create(Consts.aml_pref + "hasInterfaceSUC");
//	
//	public static final IRI HAS_ROLE_REQUIREMENT = IRI.create(Consts.aml_pref + "hasRoleRequirement");
//	
//	public static final IRI HAS_ROLE_REQUIREMENT_IE = IRI.create(Consts.aml_pref + "hasRoleRequirementIE");
//	
//	public static final IRI HAS_ROLE_REQUIREMENT_SUC = IRI.create(Consts.aml_pref + "hasRoleRequirementSUC");
//	
//	
//	
//	public static final IRI HAS_UNIT = IRI.create(Consts.aml_pref + "hasUnit");
//	
//	public static final IRI HAS_UNIT_ATTRIBUTE = IRI.create(Consts.aml_pref + "hasUnitAttribute");
//	
//	public static final IRI IS_PART_OF = IRI.create("http://www.ontologydesignpatterns.org/cp/owl/partof.owl");
//	
//	public static final IRI IS_PART_OF_IC = IRI.create(Consts.aml_pref + "isPartOfIC");
//	
//	public static final IRI IS_PART_OF_IE = IRI.create(Consts.aml_pref + "isPartOfIE");
//	
//	public static final IRI IS_PART_OF_SUC = IRI.create(Consts.aml_pref + "isPartOfSUC");	
//	
//	public static final IRI HAS_SUPPORTED_ROLE_CLASS_IE = IRI.create(Consts.aml_pref + "hasSupportedRoleClassIE");
//	
//	public static final IRI HAS_SUPPORTED_ROLE_CLASS_SUC = IRI.create(Consts.aml_pref + "hasSupportedRoleClassSUC");
//	
//	public static final IRI HAS_UNIT_VALUE = IRI.create(Consts.aml_pref + "hasUnitValue");
//	
//	public static final IRI ECLASS_CONNECTED_TO = IRI.create(Consts.aml_pref + "isConnectedToEClass");	
//	
//	public static final IRI ECLASS_RC_Reference = IRI.create(Consts.aml_pref + "hasRefSemRC");
//	
//	public static final IRI HAS_REFSEM_ATTRIBUTE = IRI.create(Consts.aml_pref + "hasRefSemAttribute");
//	

	
	
	
	
	/**
	   The caller references the constants using <tt>Consts.EMPTY_STRING</tt>, 
	   and so on. Thus, the caller should be prevented from constructing objects of 
	   this class, by declaring this private constructor. 
	*/
	private AMLObjectPropertyIRIs(){
		//this prevents even the native class from 
		//calling this ctor as well :
		throw new AssertionError();
	}

}
