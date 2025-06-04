/**
 * @author RIAS Julien
 * @version 1.0
 * @date 04/03/25
 * @modification 04/03/25
 * @description Classe Start
 *
 */
public class Start 
{

	public static void main(String[] args) 
	{
		System.out.println("Debut du Start");
		
		IHM_ConnexionUDP monIHMConnexion = new IHM_ConnexionUDP();
		
		ThreadGestionClientServeur monThreadGestionClientServeur = new ThreadGestionClientServeur(monIHMConnexion);
		monThreadGestionClientServeur.start();
	}

}
