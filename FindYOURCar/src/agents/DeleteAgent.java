package agents;

import java.util.Collection;

import de.dfki.mycbr.core.ICaseBase;
import de.dfki.mycbr.core.Project;
import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.model.Concept;
import de.dfki.mycbr.io.XMLExporter;

public class DeleteAgent {
	
	private static String dataPath_Alfred = "C:\\Users\\User\\git\\FindYOURCar\\FindYOURCar\\";
	private static String projectName = "FindYOURCar.prj";
	
	private static String casebaseName = "Casebase";
	private static String casebaseCSVImport = "CB_csvImport";
	
	private static String topConceptName = "Car";
	
	// For myCBR
	private Project project;
	private Concept carConcept;
	private ICaseBase casebase;

	public DeleteAgent() {
		initProject();
	}

	private void initProject() {
		try {
			project = new Project(dataPath_Alfred + projectName);
		} catch (Exception e) {
			System.err.println("[ERROR]: DeleteAgent: Projekt konnte nicht erstellt werden.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Leert eine bestimmte Fallbasis.
	 * 
	 * @author Alfred
	 */
	public void deleteCasebase(String casebaseName) {

//		casebase = project.getCB(casebaseName);
//		System.out.println("[SAVE]: " + casebaseName + "-F�lle vorm L�schen: " + casebase.getCases().size());
//		casebase = project.deleteCaseBase(casebaseName);
//		Collection<Instance> x = casebase.getCases();
		casebase = project.deleteCaseBase(casebaseName);
		System.out.println("[SHOW]: " + casebaseName + "-F�lle nachm L�schen: " + casebase.getCases().size());
		
		XMLExporter.save(project, dataPath_Alfred + projectName);
		System.out.println("[SAVE]: Projekt gespeichert.");
	}
	
	
	
	/**
	 * L�scht alle Fallbasen des Projekts:
	 * <li>Casebase
	 * <li>CB_csvImport
	 * 
	 * @author Alfred
	 */
	public void deleteAllCasebases() {
		casebase = project.getCB(casebaseName);
		System.out.println(casebaseName + "-F�lle vorm L�schen: " + casebase.getCases().size());
		project.deleteCaseBase(casebaseName);
		System.out.println(casebaseName + "-F�lle nachm L�schen: " + casebase.getCases().size());
		
		casebase = project.getCB(casebaseCSVImport);
		System.out.println(casebaseCSVImport + "-F�lle vorm L�schen: " + casebase.getCases().size());
		project.deleteCaseBase(casebaseCSVImport);
		System.out.println(casebaseCSVImport + "-F�lle nachm L�schen: " + casebase.getCases().size());
		
		XMLExporter.save(project, dataPath_Alfred + projectName);
		System.out.println("[SAVE]: Projekt gespeichert.");
	}
	
	/**
	 * L�scht alle Instanzen aus dem carConzept.
	 */
	public void deleteInstances() {
		carConcept = project.getConceptByID(topConceptName);
		System.out.println("All Instances vorm L�schen: " + carConcept.getAllInstances().size());
		
		for(Instance i : carConcept.getAllInstances()) {
			carConcept.removeInstance(i.getName());
			System.out.println("[DELETE]: " + i.getName());
		}
		System.out.println("All Instances nachm L�schen: " + carConcept.getAllInstances().size());
		
		XMLExporter.save(project, dataPath_Alfred + projectName);
		System.out.println("[SAVE]: Projekt gespeichert.");
	}
}
