import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class MddInstanceTest {
	private MddInstance mddInst1;
	private MddInstance mddInst2;
	private MddInstance mddInst3;

	public MddInstanceTest() throws SAXException, IOException, ParserConfigurationException {
		final DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();  
		Document document; 
		// Modello gplTiny
		document = documentBuilder.parse("src/main/webapp/WEB-INF/featureModels/gplTinyModel.xml"); 
		mddInst1 = new MddInstance(document);   		
		// Modello gplSmallTest
		document = documentBuilder.parse("src/main/webapp/WEB-INF/featureModels/gplSmallTestModel.xml"); 
		mddInst2 = new MddInstance(document);   		
		// Modello Game of Life
		document = documentBuilder.parse("src/main/webapp/WEB-INF/featureModels/golModel.xml"); 
		mddInst3 = new MddInstance(document);   		
	}
	
	@Test
	public void featureCountTest1 () {
		// gplTiny feat# = 4		
		assertEquals(mddInst1.getFMInfo(2), 4);
	}
	@Test
	public void featureCountTest2 () {
		// gplSmallTest feat# = 19	
		assertEquals(mddInst2.getFMInfo(2), 19);
	}
	@Test
	public void featureCountTest3 () {
		// golModel feat# = 50	
		assertEquals(mddInst3.getFMInfo(2), 15);
	}

	@Test
	public void inlineConstraintCountTest1 () {
		// gplTiny constr# = 3	
		assertTrue(mddInst1.getFMInfo(1) == 3);
	}
	@Test
	public void inlineConstraintCountTest2 () {
		// gplSmallTest 
		assertTrue(mddInst2.getFMInfo(1) == 8);
	}
	@Test
	public void inlineConstraintCountTest3 () {
		// golModel
		assertTrue(mddInst3.getFMInfo(1) == 8);
	}
	
	@Test
	public void crossTreeConstraintCountTest1 () {
		// gplTiny 
		assertTrue(mddInst1.getFMInfo(3) == 1);
	}
	@Test
	public void crossTreeConstraintCountTest2 () {
		// gplSmallTest 
		assertTrue(mddInst2.getFMInfo(3) == 3);
	}
	@Test
	public void crossTreeConstraintCountTest3 () {
		// golModel
		assertTrue(mddInst3.getFMInfo(3) == 8);
	}
	
	@Test
	public void MDDTest1 () {
		// gplTiny 
		assertTrue(mddInst1.calculateMDD() > 0);
	}
	@Test
	public void MDDTest2 () {
		// gplSmallTest 
		assertTrue(mddInst2.calculateMDD() > 0);
	}
	@Test
	public void MDDTest3 () {
		// golModel 
		assertTrue(mddInst3.calculateMDD() > 0);
	}
	
	@Test
	public void variableCountTest1 () {
		// Il numero di variabili deve corrispondere al numero di constraint inline
		// gplTiny	
		mddInst1.calculateMDD();
		assertTrue(mddInst1.getMddVariableCount() == mddInst1.getFMInfo(1));
	}
	@Test
	public void variableCountTest2 () {
		// gplSmallTest 
		mddInst2.calculateMDD();
		assertTrue(mddInst2.getMddVariableCount() == mddInst2.getFMInfo(1));
	}
	@Test
	public void variableCountTest3 () {
		// golModel
		mddInst3.calculateMDD();
		assertTrue(mddInst3.getMddVariableCount() == mddInst3.getFMInfo(1));
	}

	@Test
	public void validConfigsTest1 () {
		// gplTiny 
		mddInst1.calculateMDD();
		assertTrue(mddInst1.getValidConfigs() == 6);
	}
	@Test
	public void validConfigsTest2 () {
		// gplSmallTest 
		mddInst2.calculateMDD();
		assertTrue(mddInst2.getValidConfigs() == 24);
	}
	@Test
	public void validConfigsTest3 () {
		// golModel
		mddInst3.calculateMDD();
		assertTrue(mddInst3.getValidConfigs() == 544);
	}
	

	@Test
    @SuppressWarnings("PMD.LocalVariableCouldBeFinal")
	public void addConstraint1 () throws ParserConfigurationException, SAXException, IOException {
		// gplSmallTest 
		mddInst1.calculateMDD();
		int oldVC = mddInst1.getValidConfigs();
		String constraintNode = "<constraints><rule><var>Cycle</var></rule></constraints>";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(constraintNode));
	    Document tempDoc = builder.parse(is);
	    Node constrNode = tempDoc.getElementsByTagName("constraints").item(0);
	    
	    mddInst1.addConstraint(constrNode);

		assertTrue(mddInst1.getValidConfigs() == 2);
	}
	@Test
    @SuppressWarnings("PMD.LocalVariableCouldBeFinal")
	public void addConstraint2 () throws ParserConfigurationException, SAXException, IOException {
		// gplSmallTest 
		mddInst2.calculateMDD();
		int oldVC = mddInst2.getValidConfigs();
		String constraintNode = "<constraints><rule><var>BFS</var></rule></constraints>";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(constraintNode));
	    Document tempDoc = builder.parse(is);
	    Node constrNode = tempDoc.getElementsByTagName("constraints").item(0);
	    
	    mddInst2.addConstraint(constrNode);

		assertTrue(mddInst2.getValidConfigs() < oldVC);
	}
	@Test
    @SuppressWarnings("PMD.LocalVariableCouldBeFinal")
	public void addConstraint3 () throws ParserConfigurationException, SAXException, IOException {
		// golModel 
		mddInst3.calculateMDD();
		int oldVC = mddInst3.getValidConfigs();
		String constraintNode = "<constraints><rule><var>GeneratorSelection</var></rule></constraints>";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(constraintNode));
	    Document tempDoc = builder.parse(is);
	    Node constrNode = tempDoc.getElementsByTagName("constraints").item(0);
	    
	    mddInst3.addConstraint(constrNode);

		assertTrue(mddInst3.getValidConfigs() < oldVC);
	}
	
}
