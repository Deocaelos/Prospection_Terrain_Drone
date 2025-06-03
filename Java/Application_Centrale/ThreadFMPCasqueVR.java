import Interfaces.IHM_CasqueVR;
import Utils.GestionCommandesDrones;
import Utils.Tempo;

/**
 * @author RIAS Julien
 * @version 1.0
 * @date 12/05/25
 * @modification 12/05/25
 * @description Classe ThreadFMPCasqueVR de type Thread permettant la recuperation des valeurs de controle du casque VR
 *
 */
public class ThreadFMPCasqueVR extends Thread
{
	private boolean flagEndThreadFMPCasqueVR = false;
	private String PreviousCommand ="";
	private cFileMappingManettesClient monClientFMPManettes = null;
	private double axeX = 0.0,axeY = 0.0,axeZ = 0.0,axeRota = 0.0;
	private IHM_CasqueVR monIHM_CasqueVR = null;
	
	public ThreadFMPCasqueVR(IHM_CasqueVR _IHM_CasqueVR)
	{
		this.monIHM_CasqueVR = _IHM_CasqueVR;
	}
	
	public void run()
	{
		System.out.println("Debut du Thread FMP Casque VR");
		this.monClientFMPManettes = new cFileMappingManettesClient(false);
		this.monClientFMPManettes.OpenClient("Commandes_Manettes");
		while(!this.flagEndThreadFMPCasqueVR)
		{
			if(!this.monClientFMPManettes.getVirtualManettesMutexBlocAccess())
			{
				this.monClientFMPManettes.setVirtualManettesMutexBlocAccess(true);
				//On recupere les differentes informations sur les commandes actionnees par l utilisateur dans le casque VR
				this.axeX = this.monClientFMPManettes.getVirtualManettesAxeX_Gauche();
				this.axeY = this.monClientFMPManettes.getVirtualManettesAxeY_Gauche();
				this.axeZ = this.monClientFMPManettes.getVirtualManettesAxeX_Droite();
				this.axeRota = this.monClientFMPManettes.getVirtualManettesAxeY_Droite();
				this.monClientFMPManettes.setVirtualManettesMutexBlocAccess(false);
				this.monIHM_CasqueVR.setColorDirection(this.axeX, this.axeY, this.axeZ, this.axeRota);
				this.TraitementDonnesManettes(this.axeX, this.axeY, this.axeZ, this.axeRota);
			}
			new Tempo(10);
		}
		System.out.println("Fin du Thread FMP Casque VR");
		this.monClientFMPManettes.CloseClient();
		System.gc();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void setflagEndThreadFMPCasqueVR(boolean etat)
	{
		this.flagEndThreadFMPCasqueVR = etat;
	}
	
	/**
	 * Methode permettant de traiter les donnees recues via le FMP
	 * @param _AxeX
	 * @param _AxeY
	 * @param _AxeZ
	 * @param _AxeRota
	 */
	private void TraitementDonnesManettes(double _AxeX, double _AxeY, double _AxeZ, double _AxeRota)
	{
		//On compose la commande a envoyer aux drones sous le format stick
		String ConfigVol ="stick "+_AxeY+" "+_AxeX+" "+_AxeRota+" "+_AxeZ+" 0";
		if(!this.PreviousCommand.equals(ConfigVol)) //On regarde si la commande est la meme si oui on ne renvoie pas la commande aux drones
		{
			GestionCommandesDrones.sendCommandToDrones(ConfigVol);
			this.PreviousCommand = ConfigVol;
		}
	}
}
