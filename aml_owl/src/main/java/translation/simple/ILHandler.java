package translation.simple;

import CAEX215.*;
import constants.AMLObjectPropertyIRIs;
import org.semanticweb.owlapi.model.*;

import java.util.List;

/**
 * A transformation handler for the AML internal link family type.
 * Conversion to OWL entity.
 */
public class ILHandler extends AMLEntityHandler {

    private OWLDataFactory factory;

    private final String INTERNAL_LINK_ROLE = "Link";

    public ILHandler(OWLOntologyManager manager, String ns) {
        this.factory = manager.getOWLDataFactory();
        this.ns = ns;
        this.manager = manager;
    }

    @Override
    public OWLEntity cvt2Owl(CAEXObject obj, OWLOntology ont) {
        return factory.getOWLNamedIndividual(createIRI(obj));
    }

    @Override
    public void add2Owl(CAEXObject obj, OWLEntity entity, OWLOntology ont) {
        if (!(obj instanceof InternalLinkType))
            throw new IllegalArgumentException("CAEX object is not an internal link.");
        InternalLinkType il = (InternalLinkType) obj;
        OWLNamedIndividual ind_il;

        // Create the OWL entity for the IL
        if (entity.getEntityType().equals(EntityType.NAMED_INDIVIDUAL))
            ind_il = (OWLNamedIndividual) cvt2Owl(il, ont);
        else
            ind_il = factory.getOWLNamedIndividual(IRI.create(ns + il.getName()));

        OWLDeclarationAxiom ax_il = factory.getOWLDeclarationAxiom(ind_il);
        manager.applyChange(new AddAxiom(ont, ax_il));

        // Add type 'Link' to individual 'InternalLink'
        OWLClass internalLinkRole = factory.getOWLClass(IRI.create(ns + INTERNAL_LINK_ROLE));
        OWLClassAssertionAxiom axAssertion = null;
        if (internalLinkRole != null)
            axAssertion = factory.getOWLClassAssertionAxiom(internalLinkRole, ind_il);
        if (axAssertion != null)
            manager.applyChange(new AddAxiom(ont, axAssertion));

        //TODO: change to AMLClassIRI
        OWLAnnotation hasSemantic = factory.getOWLAnnotation(factory.getRDFSComment(), factory.getOWLLiteral("InternalLink"));
        OWLAnnotationAssertionAxiom ax_hasSemantic = factory.getOWLAnnotationAssertionAxiom(ind_il.getIRI(), hasSemantic);
        manager.applyChange(new AddAxiom(ont, ax_hasSemantic));

        OWLAnnotation hasLabel = factory.getOWLAnnotation(factory.getRDFSLabel(), factory.getOWLLiteral(obj.getName()));
        OWLAnnotationAssertionAxiom axHasLabel = factory.getOWLAnnotationAssertionAxiom(ind_il.getIRI(), hasLabel);
        manager.applyChange(new AddAxiom(ont, axHasLabel));

    }

    @Override
    public IRI createIRI(CAEXObject obj) {
        InternalLinkType il = (InternalLinkType) obj;
        if (il.getID() != null)
            return IRI.create(ns + String.join("_", "il", obj.getName(), obj.getID()));
        else
            return IRI.create(ns + String.join("_", "il", obj.getName()));
    }

    /**
     * Returns an OWL individual from the instance hierarchy that represents either a `RefPartnerSideA` or `RefPartnerSideB` of an internal link.
     *
     * @param ies   a list of internal elements
     * @param il    the internal link from which the partner side individual is retrieved
     * @param ont   the ontology
     * @param sideA true, if individual from `RefPartnerSideA` is retrieved, otherwise `RefPartnerSideB`
     * @return an individual representing either `RefPartnerSideA` or `RefPartnerSideB` of an internal link
     */
    private OWLIndividual getRefPartnerSideIndividualFromInstanceHierarchy(List<InternalElementType> ies, InternalLinkType il, OWLOntology ont, boolean sideA) {

        String refSide = sideA ? il.getRefPartnerSideA() : il.getRefPartnerSideB();

        for (InternalElementType ie : ies) {
            // find the IE which owns refSideA/B
            if (ie.getID().equals(refSide.split(":")[0])) {
                for (InterfaceClassType ei : ie.getExternalInterface()) {
                    // find the refSide
                    if (ei.getName().equals(refSide.split(":")[1])) {
                        for (OWLIndividual ind : ont.getIndividualsInSignature()) {
                            // find the owl individual for side A/B
                            if (ind.asOWLNamedIndividual().getIRI().equals(IRI.create(ns + "ei_" + ei.getName() + "_" + ei.getID()))) {
                                return ind;
                            }
                        }
                    }
                }
            }
            // RefPartnerSide A/B may be directly referenced by ID (instead of <parentIeId:EiName>)
            else {
                for (InterfaceClassType ei : ie.getExternalInterface()) {
                    if (ei.getID().equals(refSide)) {
                        for (OWLIndividual ind : ont.getIndividualsInSignature()) {
                            // find the owl individual for side A/B
                            if (ind.asOWLNamedIndividual().getIRI().equals(IRI.create(ns + "ei_" + ei.getName() + "_" + ei.getID()))) {
                                return ind;
                            }
                        }
                    }
                }
            }
            // Recursive call to process children
            OWLIndividual indFound = getRefPartnerSideIndividualFromInstanceHierarchy(ie.getInternalElement(), il, ont, sideA);
            if (indFound != null) return indFound;
        }
        return null;
    }

    /**
     * Sets the `refPartnerSideA` and `refPartnerSideB` object property of a given internal link individual.
     *
     * @param iht    the instance hierarchy
     * @param il     the internal link (CAEX object) for which the corresponding individual ref partner sides should be set
     * @param entity the OWL entity
     * @param ont    the ontology
     */
    public void setRefPartnerSides(InstanceHierarchyType iht, InternalLinkType il, OWLEntity entity, OWLOntology ont) {
        OWLNamedIndividual indIL;

        // Create the OWL entity for the IL
        if (entity.getEntityType().equals(EntityType.NAMED_INDIVIDUAL))
            indIL = (OWLNamedIndividual) cvt2Owl(il, ont);
        else
            indIL = factory.getOWLNamedIndividual(IRI.create(ns + il.getName()));

        OWLIndividual indA = getRefPartnerSideIndividualFromInstanceHierarchy(iht.getInternalElement(), il, ont, true);
        OWLIndividual indB = getRefPartnerSideIndividualFromInstanceHierarchy(iht.getInternalElement(), il, ont, false);

        if (indA != null && indB != null) {
            System.out.println("new link: " + il);
            OWLObjectPropertyExpression hasRefSideA = factory.getOWLObjectProperty(AMLObjectPropertyIRIs.HAS_REFPARTNER_SIDE_A);
            OWLObjectPropertyExpression hasRefSideB = factory.getOWLObjectProperty(AMLObjectPropertyIRIs.HAS_REFPARTNER_SIDE_B);
            OWLObjectPropertyAssertionAxiom axRefA = factory.getOWLObjectPropertyAssertionAxiom(hasRefSideA, indIL, indA);
            OWLObjectPropertyAssertionAxiom axRefB = factory.getOWLObjectPropertyAssertionAxiom(hasRefSideB, indIL, indB);
            manager.applyChange(new AddAxiom(ont, axRefA));
            manager.applyChange(new AddAxiom(ont, axRefB));
        } else
            System.out.println("Cannot find interface individuals for the link: " + il + ". Side A is null: " + (indA == null) + ", Side B is null: " + (indB == null));

    }

}
