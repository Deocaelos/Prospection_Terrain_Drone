import Interfaces.IHM_RobotHaptique;
import Utils.GestionCommandesDrones;
import Utils.Tempo;

/**
 * @author RIAS Julien
 * @version 1.0
 * @date 27/03/25
 * @modification 10/04/25
 * @description Classe ThreadFMPRobotHaptique de type Thread permettant la recuperation des valeurs du robot haptique
 *
 */
public class ThreadFMPRobotHaptique extends Thread
{
	private boolean flagEndThreadFMPRobotHaptique = false, UserPresence = false;
	private boolean[] Last_Axe_Value = new boolean[6]; //X+ X- Y+ Y- Z+ Z-
	private cFileMappingRobotHaptiqueDroneClient monClientFMPRobot = null;
	private int axeX = 0,axeY = 0,axeZ = 0, Tempo = 1;
	private IHM_RobotHaptique monIHM_RobotHaptique = null;
	
	public ThreadFMPRobotHaptique(IHM_RobotHaptique _IHM_RobotHaptique)
	{
		this.monIHM_RobotHaptique = _IHM_RobotHaptique;
	}
	
	public void run()
	{
		System.out.println("Debut du Thread FMP Robot Haptique");
		this.monClientFMPRobot = new cFileMappingRobotHaptiqueDroneClient(false);
		this.monClientFMPRobot.OpenClient("RobotHaptique_Position");
		while(!this.flagEndThreadFMPRobotHaptique)
		{
			if(!this.monClientFMPRobot.getVirtualRobotHaptiqueDroneMutexBlocAccess())
			{
				this.monClientFMPRobot.setVirtualRobotHaptiqueDroneMutexBlocAccess(true);
				//On recupere le booleen pour savoir si l utilisateur qui utilise le robot haptique conforme sa presence a laide du bouton central
				this.UserPresence = this.monClientFMPRobot.getVirtualRobotHaptiqueDroneUserPresence();
				this.monIHM_RobotHaptique.setUserPresence(this.UserPresence);
				//Si l utilisateur est present alors on peut recuperer les differentes valeurs et donc commander les drones, sinon on arrete tout deplacement
				if(this.UserPresence)
				{
					this.axeX = this.monClientFMPRobot.getVirtualRobotHaptiqueDroneAxeX();
					this.axeY = this.monClientFMPRobot.getVirtualRobotHaptiqueDroneAxeY();
					this.axeZ = this.monClientFMPRobot.getVirtualRobotHaptiqueDroneAxeZ();
					this.monClientFMPRobot.setVirtualRobotHaptiqueDroneMutexBlocAccess(false);
					this.monIHM_RobotHaptique.setColorDirection(this.axeX, this.axeY, this.axeZ);
					this.TraitementDonnesRobot(this.axeX, this.axeY, this.axeZ);
					this.Tempo = 500;
				}
				else
				{
					GestionCommandesDrones.sendCommandToDrones("stick 0 0 0 0 0");
					this.monClientFMPRobot.setVirtualRobotHaptiqueDroneMutexBlocAccess(false);
					this.Tempo = 1;
				}
			}
			else
			{
				this.Tempo = 1;
			}
			new Tempo(this.Tempo);
		}
		System.out.println("Fin du Thread FMP Robot Haptique");
		this.monClientFMPRobot.CloseClient();
		System.gc();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void setflagEndThreadFMPRobotHaptique(boolean etat)
	{
		this.flagEndThreadFMPRobotHaptique = etat;
	}
	
	
	/**
	 * Methode permettant de traiter les donnees recues via le FMP pour le robot haptique
	 * @param _AxeX
	 * @param _AxeY
	 * @param _AxeZ
	 */
	private void TraitementDonnesRobot(int _AxeX, int _AxeY, int _AxeZ)
	{
		//Booleen toContinue permettant de commander les drones dans une seule direction a la fois
		
		//Le tableau de booleen Last_Axe_Value permet quant a lui de nexecuter qu une seule fois la meme commande
		boolean toContinue = true;
		//Etat null
		if(_AxeX == 0 && _AxeY == 0 && _AxeZ == 0)
		{
			this.ResetLastValueAxe();
			GestionCommandesDrones.sendCommandToDrones("stick 0 0 0 0 0");
		}
		//Etats non null
		if(_AxeX == 1 && !this.Last_Axe_Value[0])
		{
			GestionCommandesDrones.sendCommandToDrones("stick 0 0.2 0 0 0");
			this.ResetLastValueAxe();
			this.Last_Axe_Value[0] = true;
			toContinue = false;
		}
		else if(_AxeX == -1 && !this.Last_Axe_Value[1])
		{
			GestionCommandesDrones.sendCommandToDrones("stick 0 -0.2 0 0 0");
			this.ResetLastValueAxe();
			this.Last_Axe_Value[1] = true;
			toContinue = false;
		}
		
		if(toContinue)
		{
			if(_AxeY == 1 && !this.Last_Axe_Value[2])
			{
				GestionCommandesDrones.sendCommandToDrones("stick -0.2 0 0 0 0");
				this.ResetLastValueAxe();
				this.Last_Axe_Value[2] = true;
				toContinue = false;
			}
			else if(_AxeY == -1 && !this.Last_Axe_Value[3])
			{
				GestionCommandesDrones.sendCommandToDrones("stick 0.2 0 0 0 0");
				this.ResetLastValueAxe();
				this.Last_Axe_Value[3] = true;
				toContinue = false;
			}
		}
		
		if(toContinue)
		{
			if(_AxeZ == 1 && !this.Last_Axe_Value[4])
			{
				GestionCommandesDrones.sendCommandToDrones("stick 0 0 0 -0.2 0");
				this.ResetLastValueAxe();
				this.Last_Axe_Value[4] = true;
			}
			else if(_AxeZ == -1 && !this.Last_Axe_Value[5])
			{
				GestionCommandesDrones.sendCommandToDrones("stick 0 0 0 0.2 0");
				this.ResetLastValueAxe();
				this.Last_Axe_Value[5] = true;
			}
		}
	}
	
	
	/**
	 * Methode permettant de remmetre les valeur des anciens axes
	 */
	private void ResetLastValueAxe()
	{
		for (int i = 0 ; i< this.Last_Axe_Value.length; i++)
		{
			this.Last_Axe_Value[i] = false;
		}
	}
}
