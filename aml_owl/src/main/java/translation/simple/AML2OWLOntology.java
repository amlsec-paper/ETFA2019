package translation.simple;
/**
 *
 */


import java.io.File;
import java.io.IOException;
import java.util.List;

import CAEX215.*;
import org.eclipse.emf.common.util.EList;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.model.OWLDocumentFormat;

import constants.Consts;

/**
 * @author hua
 */
public class AML2OWLOntology implements IAML2OWL {


    public OWLOntologyManager manager;
    public OWLDataFactory factory;
    //	public OWLReasonerFactory rfactory;
    public OWLReasoner reasoner;
    public OWLOntology aml_ont, output_ont;
    //	public PrefixManager pm;
    private IFHandler if_handler;
    private RFHandler rf_handler;
    private SUCHandler suc_handler;
    private IEHandler ieHandler;
    private ILHandler ilHandler;


    public AML2OWLOntology(String aml_ont_filename) throws OWLOntologyCreationException {
        this.manager = OWLManager.createOWLOntologyManager();
        this.factory = this.manager.getOWLDataFactory();
//		this.rfactory = new StructuralReasonerFactory();
//		this.pm = new DefaultPrefixManager(Consts.importer_pref);


        if (aml_ont_filename != null) {
            this.aml_ont = this.load(aml_ont_filename);
//        		this.reasoner = rfactory.createReasoner(aml_ont);
        }

//		new AttributeHandler(manager, Consts.importer_pref);
        this.if_handler = new IFHandler(manager, Consts.importer_pref);
        this.rf_handler = new RFHandler(manager, Consts.importer_pref);
        this.suc_handler = new SUCHandler(manager, Consts.importer_pref);
        this.ieHandler = new IEHandler(manager, Consts.importer_pref);
        this.ilHandler = new ILHandler(manager, Consts.importer_pref);

    }

    /**
     * Create an ontology with given IRI
     *
     * @param iri
     * @throws OWLOntologyCreationException
     */
    public void createOnt(IRI iri) throws OWLOntologyCreationException {
        this.output_ont = manager.createOntology(iri);
//		OWLImportsDeclaration decl = factory.getOWLImportsDeclaration(this.manager.getOntologyDocumentIRI(this.aml_ont));			
//		manager.applyChange(new AddImport(new_ont, decl));		
    }

    /**
     * Load an ontology from file
     *
     * @param filename
     * @return
     * @throws OWLOntologyCreationException
     */
    public OWLOntology load(String filename) throws OWLOntologyCreationException {

        System.out.println("\n\n=============== Loading OWL ontology from file system ================\n");
        File file = new File(filename);
        // Now load the local copy
        OWLOntology ont = this.manager.loadOntologyFromOntologyDocument(file);
        System.out.println("Loaded ontology: " + ont);
        // We can always obtain the location where an ontology was loaded from
        IRI documentIRI = this.manager.getOntologyDocumentIRI(ont);
        System.out.println("from: " + documentIRI);
        return ont;
    }

    /**
     * Save the current ontology using the given format
     *
     * @param ont
     * @param format
     * @param filename
     * @throws IOException
     * @throws OWLOntologyStorageException
     */
    public void save(OWLOntology ont, OWLDocumentFormat format, String filename) throws IOException, OWLOntologyStorageException {

        System.out.println("\n\n=============== Saving OWL ontology to file system ================\n");

        File file = new File(filename);

//        OWLDocumentFormat f = this.manager.getOntologyFormat(ont);  
//        System.out.println("current format: " + f.toString());           

//      this.manager.saveOntology(ont, new SystemOutDocumentTarget());        
        this.manager.saveOntology(ont, format, IRI.create(file.toURI()));
        System.out.println("successfully saved ontology to file: " + filename);
    }


    public boolean verifiyAML(CAEXFileType aml) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Create an OWL class using a name as its ID
     *
     * @param name
     * @return
     */
    private OWLClass createOWLClassByName(String name, String ns) {
        return factory.getOWLClass(IRI.create(ns + name));
    }


    /**
     * Transforms the AML InterfaceClasses inside a InterfaceClassLib to OWL Classes
     *
     * @param aml: the AML file
     * @return
     */
    private boolean transformICLibs(CAEXFileType aml) {

        EList<InterfaceClassLibType> icls = aml.getInterfaceClassLib();

        for (InterfaceClassLibType icl : icls) {
            for (InterfaceFamilyType icf : icl.getInterfaceClass()) {
                if_handler.add2Owl(icf, this.factory.getOWLThing(), output_ont);
            }
        }
        return true;
    }

    /**
     * Transforms the AML RoleClasses inside a RoleClassLib to OWL Classes
     *
     * @param aml
     * @return
     */
    private boolean transformRCLibs(CAEXFileType aml) {

        EList<RoleClassLibType> rcls = aml.getRoleClassLib();

        for (RoleClassLibType rcl : rcls) {
//			if(rcl.getName().contains("AutomationMLBaseRoleClassLib"))
//				break;
            for (RoleFamilyType rf : rcl.getRoleClass()) {
                rf_handler.add2Owl(rf, factory.getOWLThing(), output_ont);
            }
        }
        return true;
    }

    // traverse the SUC class libs in the aml file
    private boolean transformSUCLibs(CAEXFileType aml) {

        // TODO: we assume all source sucs are in the SourceSystemUnitClassLib
        for (SystemUnitClassLibType sucl : aml.getSystemUnitClassLib()) {
//			if(sucl.getName().equalsIgnoreCase("Source") || sucl.getName().equalsIgnoreCase("Dummy")) {
            for (SystemUnitFamilyType suf : sucl.getSystemUnitClass()) {
                this.suc_handler.add2Owl(suf, factory.getOWLThing(), output_ont);
            }
//			}				
        }
        return true;
    }

    private void transformInternalLinks(List<InternalElementType> ies, InstanceHierarchyType iht) {
        for (InternalElementType ie : ies) {
            for (InternalLinkType il : ie.getInternalLink()) {
                this.ilHandler.add2Owl(il, factory.getOWLThing(), output_ont);
                this.ilHandler.setRefPartnerSides(iht, il, factory.getOWLThing(), output_ont);
            }
            transformInternalLinks(ie.getInternalElement(), iht);
        }
    }

    // TODO: traverse the instance hierarchies in the aml file
    private void transformIH(CAEXFileType aml) {

        for (InstanceHierarchyType iht : aml.getInstanceHierarchy())
            for (InternalElementType ie : iht.getInternalElement())
                this.ieHandler.add2Owl(ie, factory.getOWLThing(), output_ont);

        for (InstanceHierarchyType iht : aml.getInstanceHierarchy())
            transformInternalLinks(iht.getInternalElement(), iht);

    }


    public OWLOntology transformAML2OWL(CAEXFileType aml) {

        // create some abstract parent classes for ordering the aml models:

        // caex role class
        OWLClass base_role = createOWLClassByName("AutomationMLBaseRole", Consts.importer_pref);
        OWLDeclarationAxiom owl_role = factory.getOWLDeclarationAxiom(base_role);
        manager.applyChange(new AddAxiom(this.output_ont, owl_role));

        // caex interface class
        OWLClass base_ic = createOWLClassByName("AutomationMLBaseInterface", Consts.importer_pref);
        OWLDeclarationAxiom owl_ic = factory.getOWLDeclarationAxiom(base_ic);
        manager.applyChange(new AddAxiom(this.output_ont, owl_ic));

        if (!transformICLibs(aml))
            return null;

        if (!transformRCLibs(aml))
            return null;

        if (!transformSUCLibs(aml))
            return null;

        transformIH(aml);

        return this.output_ont;

    }

}
