

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
	
	private Document document = null;
	
	private MddInstance instance;
	
	/**
	 * Handler per le chiamate GET da parte dei client. Ritorna JSON contenente due campi: 
	 * request, contenente la richiesta effettuata
	 * value, contenente il valore richiesto dal client
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @SuppressWarnings("PMD.LocalVariableCouldBeFinal")
	protected void doGet(
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {

	 
		String requestUri = request.getRequestURI();
		System.out.println(requestUri);
		String json = "{}";
		int ret; 
		ExecutorService service = Executors.newSingleThreadExecutor();
		
		switch(requestUri) {
		case "/MddFrontend/getFMFeatures/":
	    	int featNum = instance.getFMFeatures();
			json = "{\n";
			json += "\"request\": " + JSONObject.quote("calculateNumberOfFeatures") + ",\n";
			json += "\"value\": " + JSONObject.quote(String.valueOf(featNum)) + "\n";
			json += "}";
			break;
		case "/MddFrontend/getFMConstr/":
	    	int constrNum = instance.getFMConstraints() + instance.getFMCTConstraints();
			json = "{\n";
			json += "\"request\": " + JSONObject.quote("calculateNumberOfConstraints") + ",\n";
			json += "\"value\": " + JSONObject.quote(String.valueOf(constrNum)) + "\n";
			json += "}";
			break;
		case "/MddFrontend/getNodeCount/":
			int nc = instance.getNodeCount();
			json = "{\n";
			json += "\"request\": " + JSONObject.quote("getNodeCount") + ",\n";
			json += "\"value\": " + JSONObject.quote(String.valueOf(nc)) + "\n";
			json += "}";
			break;
		case "/MddFrontend/getVarCount/":
			int vcount = instance.getMddVariableCount();
			json = "{\n";
			json += "\"request\": " + JSONObject.quote("getVarCount") + ",\n";
			json += "\"value\": " + JSONObject.quote(String.valueOf(vcount)) + "\n";
			json += "}";
			break;
			
		case "/MddFrontend/calcVc/":
			int vc = calculateVC();

			json = "{\n";
			json += "\"request\": " + JSONObject.quote("calcValidConfigs") + ",\n";
			json += "\"value\": " + JSONObject.quote(String.valueOf(vc)) + "\n";
			json += "}";
			break;
		case"/MddFrontend/calcMdd/":

			int result = calculateMDD();
			
			json = "{\n";
			json += "\"request\": " + JSONObject.quote("calcMdd") + ",\n";
			json += "\"value\": " + JSONObject.quote(String.valueOf(result)) + "\n";
			json += "}";
			
		    
			break;
		}
		
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
	
    
    private int calculateVC() {
    	ExecutorService service = Executors.newSingleThreadExecutor();

		try {
		    Runnable r = new Runnable() {
		        @Override
		        public void run() {
		        	instance.getValidConfigs();
		        }
		    };

		    Future<?> f = service.submit(r);

		    f.get(30, TimeUnit.SECONDS);     // attempt the task for 30 seconds

	    	return instance.getValidConfigs();
		    
		}
		catch (final InterruptedException e) {
		    // The thread was interrupted during sleep, wait or join
			return -3;
		}
		catch (final TimeoutException e) {
			return -2;
		}
		catch (final ExecutionException e) {
		    // An exception from within the Runnable task
			return -1;
		}
		finally {
		    service.shutdown();
		}
    }
    

    private int calculateMDD() {
    	ExecutorService service = Executors.newSingleThreadExecutor();
    	
		try {
		    Runnable r = new Runnable() {
		        @Override
		        public void run() {
		        	instance.calculateMDD();
		        }
		    };

		    Future<?> f = service.submit(r);

		    f.get(30, TimeUnit.SECONDS);     // attempt the task for 30 seconds
		    
		    return 1;
		}
		catch (final InterruptedException e) {
		    // The thread was interrupted during sleep, wait or join
			return -3;
		}
		catch (final TimeoutException e) {
			return -2;
		}
		catch (final ExecutionException e) {
		    // An exception from within the Runnable task
			return -1;
		}
		finally {
		    service.shutdown();
		}
    }
    
	/**
	 * Handler per le chiamate POST dai client
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("PMD.LocalVariableCouldBeFinal")
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
		    System.out.println("Payload: ");
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
					instance = new MddInstance(document);
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
			    instance.addConstraint(constrNode);
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
