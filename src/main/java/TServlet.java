

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariableFactory;
import org.colomoto.mddlib.PathSearcher;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import leo.TestMDD4.MDDConv;

/**
 * Servlet implementation class TServlet
 */
@WebServlet("/TServlet")
public class TServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String ALLOWED_DOMAINS_REGEXP = ".*";
	Document document = null;
	

	private MddInstance inst;
	
    /**
     * Default constructor. 
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     * @throws InterruptedException 
     */
    public TServlet() {
        // TODO Auto-generated constructor stub
    	// super();
    	
    	
   		// FMtoCTWtoMediciMDDGen(modelName);
   		
		/*
		System.out.println("Before setNode (Base MDD: " + baseMDD + ")");  
		searcher.setNode(baseMDD);
		searcher.getPath();
		nPaths = searcher.countPaths();
   		System.out.print("After Constraints Paths #:\n " + nPaths + "\n\n");*/

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	
	protected void doGet(
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {

	
		String requestUri = request.getRequestURI();
		System.out.println(requestUri);
		String json = "{}";
		if (requestUri.equals("/MddFrontend/calcVc/")) {

			System.out.println("just tell me i'm at least in here");
			
	    	int vc = inst.getValidConfigs();
			json = "{\n";
			json += "\"request\": " + JSONObject.quote("calculateValidConfigs") + ",\n";
			json += "\"validConfigs\": " + JSONObject.quote(String.valueOf(vc)) + "\n";
			json += "}";
	    	
		}
		if (requestUri.equals("/MddFrontend/addFeat/*")) {

			String name = requestUri.substring("/MddFrontend/addFeat/".length());
			if(name != null){
				json = "{\n";
				json += "\"request\": " + JSONObject.quote("addFeature") + ",\n";
				json += "\"name\": " + JSONObject.quote(name) + "\n";
				json += "}";
			}
		}
		/*PrintWriter out = response.getWriter();
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		out.print(json);
		out.flush();*/
		
		
		String origin = request.getHeader("Origin");
		if (origin != null && origin.matches(ALLOWED_DOMAINS_REGEXP)) {
			response.addHeader("Access-Control-Allow-Origin", origin);
			response.setHeader("Allow", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS");
			if (origin != null) {
			    String headers = request.getHeader("Access-Control-Request-Headers");
			    String method = request.getHeader("Access-Control-Request-Method");
			    response.addHeader("Access-Control-Allow-Methods", method);
			    response.addHeader("Access-Control-Allow-Headers", headers);
			    response.setContentType("application/json");
			}
		}
		
		
		
	    
	    response.setCharacterEncoding("UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
	    response.setStatus(200);
	    response.getWriter().write(json);
	}
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String pathInfo = request.getPathInfo();
		String requestUri = request.getRequestURI();
		if(pathInfo == null || pathInfo.equals("/")){
			StringBuilder buffer = new StringBuilder();
		    BufferedReader reader = request.getReader();
		    String line;
		    while ((line = reader.readLine()) != null) {
		        buffer.append(line);
		    }
		    
		    String payload = buffer.toString();
		    System.out.println("");
		    System.out.println(payload);
		    
			if (requestUri.equals("/MddFrontend/sendFM/")) {
				
			    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			    DocumentBuilder builder = null;
				try {
					builder = factory.newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    InputSource is = new InputSource(new StringReader(payload));
			    try {
					document = builder.parse(is);
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (requestUri.equals("/MddFrontend/calcMdd/")) {
		    	inst = new MddInstance(document);
			}

			if (requestUri.equals("/MddFrontend/newConstr/")) {
				String constraintNode = "<constraints>"+payload+"</constraints>";
				
			    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			    DocumentBuilder builder = null;
			    Document tempDoc = null;
				try {
					builder = factory.newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    InputSource is = new InputSource(new StringReader(constraintNode));
			    try {
			    	tempDoc = builder.parse(is);
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    Node constrNode = tempDoc.getElementsByTagName("constraints").item(0);
		    	inst.addConstraint(constrNode);
			}
			
		}
		else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		
		
		// doGet(request, response);
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}

}
