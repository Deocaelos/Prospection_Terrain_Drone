
import Interfaces.IHM_ChoixUtilisateur;

/**
 * @author RIAS Julien
 * @version 1.0
 * @date 31/03/25
 * @modification 02/04/25
 * @description Classe Start
 *
 */
public class Start 
{
	//Chargement des differentes DLL
	static
	{
		System.loadLibrary("JNIFileMappingPictureServeur");
		System.loadLibrary("JNIVirtualRobotHaptiqueDrone");
		System.loadLibrary("JNIFileMappingRobotHaptiqueDroneClient");
		System.loadLibrary("JNIFileMappingManettesClient");
		System.loadLibrary("JNIFileMappingTelloEduTelemetryClient");
		System.loadLibrary("JNIFileMappingTelloEduTelemetryServeur");
		System.loadLibrary("/lib/opencv_java4100");
	}
	
	public static void main(String[] args) 
	{
		System.out.println("Debut du Start");
		IHM_ChoixUtilisateur monIHMChoix = new IHM_ChoixUtilisateur();
		
		//Declaration et instanciation du thread Thread_SurveillanceChoixUtilisateur
		Thread_SurveillanceChoixUtilisateur monThreadCHoix = new Thread_SurveillanceChoixUtilisateur(monIHMChoix);
		monThreadCHoix.start();
		
		System.out.println("Fin du Start");
	}

}
