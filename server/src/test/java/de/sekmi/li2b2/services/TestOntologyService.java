package de.sekmi.li2b2.services;

import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;

import de.sekmi.li2b2.client.Li2b2Client;
import de.sekmi.li2b2.client.ont.XMLExport;
public class TestOntologyService extends TestWithServer {

	@Test
	public void exportedXmlMatchesImportedXml() throws Exception {
		this.outputMessageLog = true;
		Li2b2Client client = newAuthenticatedClient("Demo");
		Path temp = Files.createTempFile("ont", ".xml");
		try( Writer w = Files.newBufferedWriter(temp, StandardCharsets.UTF_8) ){
			XMLExport export = new XMLExport(client.ONT(), w);
			export.exportAll();
		}
		System.out.println("Writing exported XML to "+temp);
		Diff diff;
		try( InputStream in1 = TestOntologyService.class.getResourceAsStream("/ontology.xml");
				InputStream in2 = Files.newInputStream(temp) ){
			diff = DiffBuilder.compare(in1).withTest(in2).ignoreComments().ignoreWhitespace().build();
		}
		if( diff.hasDifferences() ) {
			for( Difference d : diff.getDifferences() ) {
				System.out.println("Difference: "+d.toString());
			}
		}
		Assert.assertFalse(diff.hasDifferences());
		Files.delete(temp);
	}
}
