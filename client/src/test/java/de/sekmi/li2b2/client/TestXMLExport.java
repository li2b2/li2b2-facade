package de.sekmi.li2b2.client;

import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import de.sekmi.li2b2.client.ont.XMLExport;
import de.sekmi.li2b2.hive.HiveException;

public class TestXMLExport {
	public static void main(String[] args) throws HiveException, XMLStreamException, FactoryConfigurationError, IOException {
	//\\i2b2_LABS\i2b2\Labtests\LAB\(LLB53) Hematology\(LLB54) Blood Diff - Absolute\ABANDS\	
	//\\\\i2b2_LABS\\i2b2\\Labtests\\LAB\\(LLB53) Hematology\\
		XMLExport.main(new String[] {"http://localhost/i2b2/services/PMService/|http://localhost/webclient/index.php",
				"demo@i2b2demo","demouser", "\\\\i2b2_LABS\\i2b2\\Labtests\\LAB\\(LLB53) Hematology\\"});
	}
}
