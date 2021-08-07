package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import agents.CarAgent;
import data.Car;
import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.similarity.Similarity;
import de.dfki.mycbr.util.Pair;


@WebServlet("/CarServlet")
public class CarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	List<Pair<Instance, Similarity>> result;
	ArrayList<Car> resultingCars; 
	
	private int anzahlAngezeigterAutos = 3;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Parameter aus der index.jsp
		// Attribute, die der Benutzer sich wünscht
		String inputMarke = request.getParameter("marke");
		String inputModell = request.getParameter("modell");
		String inputPreis = request.getParameter("preis");
		String inputHubraum = request.getParameter("hubraum");
		String inputPs = request.getParameter("ps");
		String inputKraftstoff = request.getParameter("kraftstoff");
		
		try {
			int inputPreisParsed = Integer.parseInt(inputPreis); 
			int inputHubraumParsed = Integer.parseInt(inputHubraum); 
			int inputPsParsed = Integer.parseInt(inputPs); 
			Car queryCar = new Car(inputMarke, inputModell, inputPreisParsed, inputHubraumParsed, inputPsParsed, inputKraftstoff); 
			
			//Fallbasis aktualisieren
			
			
			// Erstellung der Empfehlungen
			CarAgent carAgent = new CarAgent(); 
			result = carAgent.startQuery(queryCar); 
			resultingCars = carAgent.print(result, anzahlAngezeigterAutos);
			
			request.setAttribute("resultingCars", resultingCars);
			
			// Forward parameters back to the form for usability
			request.setAttribute("inputMarke", inputMarke);
			request.setAttribute("inputModell", inputModell);
			request.setAttribute("inputPreis", inputPreisParsed);
			request.setAttribute("inputHubraum", inputHubraumParsed);
			request.setAttribute("inputPs", inputPsParsed);
			request.setAttribute("inputKraftstoff", inputKraftstoff);
			
			request.getRequestDispatcher("index.jsp").forward(request, response);
			
		} catch (Exception ex)  {
			request.setAttribute("error", "[DEBUG] CarServlet.java: Type Conversion Error! Please insert a number for the year. And don't mess around with the Award Boolean!"); 
			System.out.println("Error: " + ex.getMessage());
			request.getRequestDispatcher("error.jsp").forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
