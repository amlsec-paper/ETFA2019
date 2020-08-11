import CAEX215.CAEX215Package;
import CAEX215.CAEXFileType;
import constants.Consts;
import importer.AMLImporter;
import org.eclipse.emf.ecore.EPackage;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormatFactory;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.xml.sax.SAXException;
import parser.AMLParser;
import translation.simple.AML2OWLOntology;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if (args.length == 2) {
            String amlFilePath = args[0];
            String amlOntFilePath = args[1];
            try {
                AMLParser parser = new AMLParser(amlFilePath);
                EPackage modelPackage = CAEX215Package.eINSTANCE;
                AMLImporter importer = new AMLImporter(modelPackage);
                CAEXFileType aml = (CAEXFileType) importer.doImport(parser.getDoc(), false);
                AML2OWLOntology tr = new AML2OWLOntology(null);
                tr.createOnt(IRI.create(Consts.importer_pref));
                tr.transformAML2OWL(aml);
                RDFXMLDocumentFormatFactory factory = new RDFXMLDocumentFormatFactory();
                tr.save(tr.output_ont, factory.createFormat(), amlOntFilePath);
            } catch (DatatypeConfigurationException | OWLOntologyCreationException | IOException | OWLOntologyStorageException | ParserConfigurationException | SAXException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        } else
            throw new
                    IllegalArgumentException("Wrong arguments. Use <aml_file_path> <aml_ont_file_path>");
    }
}
