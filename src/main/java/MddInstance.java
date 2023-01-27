import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDManagerFactory;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.MDDVariableFactory;
import org.colomoto.mddlib.PathSearcher;
import org.colomoto.mddlib.operators.MDDBaseOperators;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import leo.TestMDD4.MDDConv;




/**
 * Hello world!
 *
 */
public class MddInstance 
{
	public Node rootNode;
	public Node constraintsNode;
	public MDDConv conv;
	public int baseMDD;

	
	public MddInstance(Document document) {
   		System.out.println("Root element: "+ document.getDocumentElement().getNodeName());  
   		// Contains a single item node list for the struct node
   		NodeList nodeList = document.getElementsByTagName("struct");
  			   		
   		Vector<Node> root = new Vector<Node>();
   		root.add(nodeList.item(0).getChildNodes().item(1));
   		rootNode = root.get(0);

   		conv = new MDDConv();
		// Generates the variables for the MDD
		conv.variablesGenerator(root); 
		// Displays the variables currently generated
		conv.displayVars();
		// Generates the MDD with the inline constraints
		try {
			baseMDD = conv.getNode(root.get(0), 1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

   		constraintsNode = document.getElementsByTagName("constraints").item(0);
		
		baseMDD = conv.applyCTConstraints(baseMDD, rootNode, constraintsNode);
	}
	
	public int getValidConfigs() {
		MDDManager manager = conv.returnManager();
		PathSearcher searcher = new PathSearcher(manager, 1);
		searcher.setNode(baseMDD);
		searcher.getPath();
		return searcher.countPaths();
	}
	
	public int getNodeCount() {
		MDDManager manager = conv.returnManager();
		return manager.getNodeCount();
	}

	public int getMddVariableCount() {
		MDDManager manager = conv.returnManager();
		MDDVariable[] vars = manager.getAllVariables();
		return vars.length;
	}

	public void addConstraint(Node constrNode) {

		baseMDD = conv.applyCTConstraints(baseMDD, rootNode, constrNode);
	}
    
    
}
