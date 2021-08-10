package agents;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import data.Car;
import de.dfki.mycbr.core.DefaultCaseBase;
import de.dfki.mycbr.core.ICaseBase;
import de.dfki.mycbr.core.Project;
import de.dfki.mycbr.core.casebase.Attribute;
import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.model.AttributeDesc;
import de.dfki.mycbr.core.model.Concept;
import de.dfki.mycbr.core.model.IntegerDesc;
import de.dfki.mycbr.core.model.StringDesc;
import de.dfki.mycbr.core.retrieval.Retrieval;
import de.dfki.mycbr.core.retrieval.Retrieval.RetrievalMethod;
import de.dfki.mycbr.core.similarity.AmalgamationFct;
import de.dfki.mycbr.core.similarity.IntegerFct;
import de.dfki.mycbr.core.similarity.Similarity;
import de.dfki.mycbr.core.similarity.config.AmalgamationConfig;
import de.dfki.mycbr.core.similarity.config.NumberConfig;
import de.dfki.mycbr.core.similarity.config.StringConfig;
import de.dfki.mycbr.io.CSVImporter;
import de.dfki.mycbr.io.XMLExporter;
import de.dfki.mycbr.util.Pair;

public class CarAgent {

	// Werte für die Initialisierung des Projekts
	private static String dataPath = "D:\\";
	private static String dataPath_Alfred = "C:\\Users\\User\\git\\FindYOURCar\\FindYOURCar\\";
	private static String projectName = "FindYOURCar.prj";
	private static String authors = "Mark E., Alfred H.";
	private static String casebaseName = "Casebase";
	private static String casebaseCSVImport = "CB_csvImport";
	private static String topConceptName = "Car";

	// set the separators that are used in the csv file
	private static String csv = "Cars_Import.csv";
	private static String columnseparator = ";";
	private static String multiplevalueseparator = ",";

	// For myCBR
	private Project project;
	private Concept carConcept;
	private ICaseBase casebase;
	private AmalgamationFct carGlobalSim;
	private Retrieval retrieve;

	// Attributes of our car, preparation for CBR
	private StringDesc markeDesc;
	private StringDesc modellDesc;
	private IntegerDesc preisDesc;
	private IntegerDesc hubraumDesc;
	private IntegerDesc psDesc;
	private StringDesc kraftstoffDesc;

	//Mindest-/Höchstwerte von Attributen
	private int minPreis = 0;
	private int maxPreis = 1000000;
	private int minHubraum = 0;
	private int maxHubraum = 10000;
	private int minPS = 0;
	private int maxPS = 2000;



	public CarAgent() {
		initProject();
		initCasebase(); 
	}

	private void initProject() {
		try {
			project = new Project(dataPath_Alfred + projectName);
			carConcept = project.getConceptByID(topConceptName);

			System.err.println("[SHOW] CarAgent: All Attr Desc: " + carConcept.getAllAttributeDescs());
		} catch (Exception e) {
			System.err.println("[ERROR] CarAgent: Projekt '" + projectName + "' wurde nicht geladen!");
		}

		if(project == null) {
			createProject();
		}
	}

	private void initCasebase() {

		casebase = project.getCB(casebaseName);

		if(casebase == null) {
			try {
				casebase = project.createDefaultCB(casebaseName);
				System.out.println("[DEBUG] Die Fallbasis '" + casebaseName + "' wurde erstellt.");
				addCases();
				XMLExporter.save(project, dataPath_Alfred + projectName);
				System.out.println("Das Projekt '" + projectName + "' wurde gespeichert.");
			} catch (Exception e) {
				System.err.println("[ERROR] CarAgent: Fallbasis '" + casebaseName + "' wurde nicht erstellt!");
				e.printStackTrace();
			}
		} else {
			System.out.println("[DEBUG] Die Fallbasis '" + casebaseName + "' wird geladen...");
			addCases();
			XMLExporter.save(project, dataPath_Alfred + projectName);
			System.out.println("Das Projekt '" + projectName + "' wurde gespeichert. In: " + project.getPath());
		}
	}

	/**
	 * Erstellt das Projekt "FindYOURCar".
	 * 
	 * @return Das Projekt wurde erstellt.
	 */
	private void createProject() {

		try {
			// Start in modeling view
			project = new Project();
			project.setName(projectName);
			project.setAuthor(authors);

			// Create a new concept
			carConcept = project.createTopConcept(topConceptName);

			// Define the global similarities
			carGlobalSim = carConcept.addAmalgamationFct(AmalgamationConfig.WEIGHTED_SUM, "carSimFct", true);

			// == Strings
			markeDesc = new StringDesc(carConcept, "Marke");
			markeDesc.addStringFct(StringConfig.LEVENSHTEIN, "markeFct", true);
			carGlobalSim.setWeight("Marke", 7);

			modellDesc = new StringDesc(carConcept, "Modell");
			modellDesc.addStringFct(StringConfig.LEVENSHTEIN, "modellFct", true);
			carGlobalSim.setWeight("Modell", 7);

			kraftstoffDesc = new StringDesc(carConcept, "Kraftstoff");
			kraftstoffDesc.addStringFct(StringConfig.LEVENSHTEIN, "kraftstoffFct", true);
			carGlobalSim.setWeight("Kraftstoff", 3);

			/* == Symbols
					categoryDesc = new SymbolDesc(bookConcept, "Category", null);
					categoryDesc.addSymbol("Drama"); 
					categoryDesc.addSymbol("Comedy"); 
					categoryDesc.addSymbol("Thriller"); 
					SymbolFct categoryFct = categoryDesc.addSymbolFct("categoryFct", true);
					categoryFct.setSimilarity("Comedy", "Drama", 0.40);
					categoryFct.setSimilarity("Comedy", "Thriller", 0.20);
					categoryFct.setSimilarity("Drama", "Comedy", 0.40);
					categoryFct.setSimilarity("Drama", "Thriller", 0.75);
					categoryFct.setSimilarity("Thriller", "Comedy", 0.20);
					categoryFct.setSimilarity("Thriller", "Drama", 0.75);
					bookGlobalSim.setWeight("Category", 4);
			 */

			// == Integer
			preisDesc = new IntegerDesc(carConcept, "Preis", minPreis, maxPreis);
			IntegerFct preisFct = preisDesc.addIntegerFct("preisFct", true);
			preisFct.setFunctionTypeL(NumberConfig.POLYNOMIAL_WITH);
			preisFct.setFunctionTypeR(NumberConfig.POLYNOMIAL_WITH);
			preisFct.setFunctionParameterR(2);
			preisFct.setFunctionParameterL(2);
			carGlobalSim.setWeight("Preis", 4);

			hubraumDesc = new IntegerDesc(carConcept, "Hubraum", minHubraum, maxHubraum);
			IntegerFct hubraumFct = preisDesc.addIntegerFct("hubraumFct", true);
			hubraumFct.setFunctionTypeL(NumberConfig.POLYNOMIAL_WITH);
			hubraumFct.setFunctionTypeR(NumberConfig.POLYNOMIAL_WITH);
			hubraumFct.setFunctionParameterR(2);
			hubraumFct.setFunctionParameterL(2);
			carGlobalSim.setWeight("Hubraum", 4);

			psDesc = new IntegerDesc(carConcept, "PS", minPS, maxPS);
			IntegerFct psFct = preisDesc.addIntegerFct("psFct", true);
			psFct.setFunctionTypeL(NumberConfig.POLYNOMIAL_WITH);
			psFct.setFunctionTypeR(NumberConfig.POLYNOMIAL_WITH);
			psFct.setFunctionParameterR(2);
			psFct.setFunctionParameterL(2);
			carGlobalSim.setWeight("PS", 1);

			/* == Booleans
					awardDesc = new BooleanDesc(bookConcept, "Award");
					awardDesc.addBooleanFct("awardDesc", true);
					bookGlobalSim.setWeight("Award", 1);
			 */
			System.out.println("Das Projekt: '" + projectName + "' wurde neu erstellt!");

		} catch (Exception e) {
			System.err.println("[ERROR] CarAgent: Das Projekt konnte nicht erstellt werden.");
			e.printStackTrace();
		}
	}

	/**
	 * Fügt Fälle aus einer CSV-Datei hinzu.
	 * 
	 * @author Alfred
	 */
	private void addCases() {
		try {
			//			Instance instance = carConcept.addInstance("Car 1");
			//			//Prüfen, ob der Fall exisitiert
			//			if (casebase.containsCase("Car 1") == null) {
			//				instance.addAttribute(markeDesc, "Mercedes Benz");
			//				instance.addAttribute(modellDesc, "CLA");
			//				instance.addAttribute(preisDesc, 20000);
			//				instance.addAttribute(hubraumDesc, 2500);
			//				instance.addAttribute(psDesc, 150);
			//				instance.addAttribute(kraftstoffDesc, "Benzin");
			//				casebase.addCase(instance);
			//			}
			//			
			//			instance = carConcept.addInstance("Car 2");
			//			if (casebase.containsCase("Car 2") == null) {
			//				instance.addAttribute(markeDesc, "BMW");
			//				instance.addAttribute(modellDesc, "X6");
			//				instance.addAttribute(preisDesc, 60000);
			//				instance.addAttribute(hubraumDesc, 5500);
			//				instance.addAttribute(psDesc, 250);
			//				instance.addAttribute(kraftstoffDesc, "Diesel");
			//				casebase.addCase(instance);
			//			}
			//			
			//			instance = carConcept.addInstance("Car 3");
			//			if (casebase.containsCase("Car 3") == null) {
			//				instance.addAttribute(markeDesc, "Audi");
			//				instance.addAttribute(modellDesc, "A8");
			//				instance.addAttribute(preisDesc, 50000);
			//				instance.addAttribute(hubraumDesc, 3500);
			//				instance.addAttribute(psDesc, 220);
			//				instance.addAttribute(kraftstoffDesc, "Diesel");
			//				casebase.addCase(instance);
			//			}

			CSVImporter csvImporter = new CSVImporter(dataPath_Alfred + csv, carConcept);
			//			csvImporter.setSeparator(columnseparator); // column separator
			//			csvImporter.setSeparatorMultiple(multiplevalueseparator); // multiple value separator

			csvImporter.readData(); // read csv data
			csvImporter.checkData(); // check formal validity of the data
			csvImporter.addMissingValues(); // add missing values with default values
			csvImporter.addMissingDescriptions(); // add default descriptions

			// Finally to do the import of the instances of the Concept defined use:
			csvImporter.doImport(); // Import the data into the project
			System.out.println("[SHOW] CarAgent: CSV Import erfolgreich.");
			
			//Hier wird das Projekt gespeichert, damit die CB_csvImport erstellt wird
			XMLExporter.save(project, dataPath_Alfred + projectName);
			System.out.println("[SHOW] CarAgent: Projekt gespeichert.");
			addCasesFromCBcsvImport(project.getCB(casebaseCSVImport));

		} catch (Exception e) {
			System.err.println("[DEBUG] CarCBR.java: Fehler beim Hinzufügen der Fälle aus der CSV-Datei: " + csv);
			e.printStackTrace();
		}
	}

	/**
	 * Prüft, ob die Instanz in der Fallbasis existiert.
	 * 
	 * @param myCB
	 * @return Instanz exisitiert
	 */
	//	private boolean existsInstanceInCB(Project myProject, String cbName, String[] csvInstance) {
	//		
	//		boolean existsInstance = false;
	//		ICaseBase cb = myProject.getCB(cbName);
	//		Collection<Instance> cases = cb.getCases();
	//		Concept myConcept = myProject.getConceptByID(topConceptName);
	//		for(Instance i : cases) {
	//			
	//			// Attribute aus dem Projekt holen
	//			Attribute modellAsAttribute = i.getAttForDesc(myConcept.getAttributeDesc("Modell"));
	//			String modellAsString = modellAsAttribute.getValueAsString();
	//			
	//			for(int d = 0; d < csvInstance.length; d++) {
	//				//System.out.println("[SHOW] CarAgent: CSV-getData(): " + csvInstance[dd]);
	//				if(csvInstance[d].contains(modellAsString)) {
	//					existsInstance = true;
	//					return existsInstance;
	//				}
	//			}
	//		}
	//		return existsInstance;
	//	}
	/**
	 * Die Fallbasis CB_csvImport wird mit der Casebase verglichen, anhand der Auto Modelle (Da einzigartig) und
	 * es werden nur Fälle in die Casebase übernommen, die noch nicht vorhanden sind.
	 * 
	 * @param myCB
	 * @param csvCB
	 * 
	 * @author Alfred
	 */
	private void addCasesFromCBcsvImport(ICaseBase csvCB) {

		Collection<Instance> cbCases = casebase.getCases();
		List<String> tmpCBModells = new ArrayList<String>();

		// Fügt einer tmpListe alle Modelle aus der Casebase als String hinzu.
		for(Instance i : cbCases) {

			AttributeDesc x = carConcept.getAttributeDesc("Modell");
			Attribute xx = i.getAttForDesc(x);
			String modell = xx.getValueAsString();
			
			tmpCBModells.add(modell);
		}

		Collection<Instance> csvCases = csvCB.getCases();

		if(cbCases.isEmpty()) {

			for(Instance i : csvCases) {

				casebase.addCase(i);
				System.out.println("[SHOW] CarAgent: Importiert in die Casebase: " + i.getName());
			} 
		} else {
			
			for(Instance i : csvCases) {
				AttributeDesc x = carConcept.getAttributeDesc("Modell");
				Attribute xx = i.getAttForDesc(x);
				String csvModell = xx.getValueAsString();
				
				if(!tmpCBModells.contains(csvModell)) {
					casebase.addCase(i);
				}
			}
		}
	}

public List<Pair<Instance, Similarity>> startQuery(Car car) {
	// Get the values of the request
	markeDesc = (StringDesc) this.carConcept.getAllAttributeDescs().get("Marke");
	modellDesc = (StringDesc) this.carConcept.getAllAttributeDescs().get("Modell");
	preisDesc = (IntegerDesc) this.carConcept.getAllAttributeDescs().get("Preis");
	hubraumDesc = (IntegerDesc) this.carConcept.getAllAttributeDescs().get("Hubraum");
	psDesc = (IntegerDesc) this.carConcept.getAllAttributeDescs().get("PS");
	kraftstoffDesc = (StringDesc) this.carConcept.getAllAttributeDescs().get("Kraftstoff");

	// Insert values into query
	try {
		retrieve = new Retrieval(carConcept, casebase);
		retrieve.setRetrievalMethod(RetrievalMethod.RETRIEVE_SORTED);
		Instance query = retrieve.getQueryInstance();
		query.addAttribute(markeDesc, markeDesc.getAttribute(car.getMarke()));
		query.addAttribute(modellDesc, modellDesc.getAttribute(car.getModell()));
		query.addAttribute(preisDesc, preisDesc.getAttribute(car.getPreis()));
		query.addAttribute(hubraumDesc, hubraumDesc.getAttribute(car.getHubraum()));
		query.addAttribute(psDesc, psDesc.getAttribute(car.getPs()));
		query.addAttribute(kraftstoffDesc, kraftstoffDesc.getAttribute(car.getKraftstoff()));
	} catch (ParseException e) {
		System.err.println("[ERROR] CarAgent: Error while creating the query! " + e.getMessage());
	}
	// Send query
	retrieve.start();
	System.out.println("[DEBUG] CarAgent: Query successful!");
	return retrieve.getResult();
}

public ArrayList<Car> print(List<Pair<Instance, Similarity>> result, int numberOfBestCases) {

	ArrayList<Car> resultingCars = new ArrayList<Car>();
	for (int i = 0; i < numberOfBestCases; i++) {
		// result is already ordered. So we can just add n Wireshark objects to the
		// list.
		Instance obj = carConcept.getInstance(result.get(i).getFirst().getName());
		Car car = new Car(
				obj.getAttForDesc(markeDesc).getValueAsString(),
				obj.getAttForDesc(modellDesc).getValueAsString(),
				Integer.parseInt(obj.getAttForDesc(preisDesc).getValueAsString()),
				Integer.parseInt(obj.getAttForDesc(hubraumDesc).getValueAsString()),
				Integer.parseInt(obj.getAttForDesc(psDesc).getValueAsString()),
				obj.getAttForDesc(kraftstoffDesc).getValueAsString()
				);

		resultingCars.add(car);
		resultingCars.get(i).setSimilarity(result.get(i).getSecond().getValue());
		System.out.println(result.get(i).getFirst().getName() + " - Similarity: "
				+ Math.floor(result.get(i).getSecond().getValue() * 100) / 100);
	}
	return resultingCars;
}

}
