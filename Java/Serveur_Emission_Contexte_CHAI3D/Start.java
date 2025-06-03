/**
 * @author RIAS Julien
 * @version 1.0
 * @date 03/03/25
 * @modification 05/04/25
 * @description Classe Start
 *
 */
public class Start 
{
	static
	{
		System.loadLibrary("JNIFileMappingPictureClient");
		System.loadLibrary("JNIVirtualPicture");
	}

	public static void main(String[] args) 
	{
		System.out.println("Debut du Start");
		
		ThreadServeurConnexionUDP monThreadServeurConnexionUDP = new ThreadServeurConnexionUDP();
		monThreadServeurConnexionUDP.start();
		
		ThreadClientFMPPicture monThreadClientFMPPicture = new ThreadClientFMPPicture(monThreadServeurConnexionUDP);
		monThreadClientFMPPicture.start();
		
		System.out.println("Fin du Start");	
		
	}
}
