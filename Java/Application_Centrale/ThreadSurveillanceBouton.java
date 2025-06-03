


import com.xpfriend.tydrone.SimpleMain;

import Interfaces.IHM_CasqueVR;
import Interfaces.IHM_ControleDrone;
import Interfaces.IHM_RobotHaptique;
import Interfaces.IHM_Sequence;
import Threads.ThreadCommandQueue;
import Utils.GestionCommandesDrones;
import Utils.GestionTimer;
import Utils.Tempo;

/**
 * @author RIAS Julien
 * @version 1.0
 * @date 22/03/25
 * @modification 10/04/25
 * @description Thread servant a la gestion des appuies sur les boutons
 *
 */

public class ThreadSurveillanceBouton extends Thread
{
	private boolean flagEndThreadSurveillanceBouton = false;
	private IHM_ControleDrone monIHMDrone = null;
	private IHM_Sequence monIHMSequence = null;
	private ThreadCommandQueue monThreadCommandQueue = null;
	private IHM_RobotHaptique monIHMRobot = null;
	private ThreadFMPRobotHaptique monThreadFMPRobot = null;
	private IHM_CasqueVR monIHMCasqueVR = null;
	private ThreadFMPCasqueVR monThreadFMPCasqueVR = null;
	private SimpleMain[] Drones;
	
	public ThreadSurveillanceBouton(IHM_ControleDrone _IHM_Drone,SimpleMain[] _Drones)
	{
		this.monIHMDrone = _IHM_Drone;
		this.Drones =_Drones;
		GestionCommandesDrones.setDrones(_Drones);
	}
	
	public void run()
	{
		System.out.println("Debut du Thread de Surveillance Bouton");
		while(!this.flagEndThreadSurveillanceBouton)
		{
			
			if(this.monIHMDrone.getIsRobotHaptique())
			{
				System.out.println("Passage au Controle via un Robot Haptique");
				this.monIHMRobot = new IHM_RobotHaptique();
				this.monThreadFMPRobot = new ThreadFMPRobotHaptique(this.monIHMRobot);
				this.monThreadFMPRobot.start();
				this.monIHMDrone.setRobotHaptique(false);
			}
			//Si lutilisateur veut revenir vers un controle uniquement manuel des drones alors on arrete le thread et l IHM permettant le controle avec le robot haptique
			if(this.monThreadFMPRobot != null && this.monThreadFMPRobot.isAlive() && this.monIHMRobot.getRetour())
			{
				System.out.println("Passage au Controle Manuel");
				this.monThreadFMPRobot.setflagEndThreadFMPRobotHaptique(true);
				while(this.monThreadFMPRobot.isAlive())
				{
					new Tempo(1);
				}
				this.monThreadFMPRobot = null;
				this.monIHMRobot.StopIHM();
				this.monIHMRobot = null;
			}
			
			if(this.monIHMDrone.isCasqueVR())
			{
				System.out.println("Passage au Controle via le casque VR");
				this.monIHMCasqueVR = new IHM_CasqueVR();
				this.monThreadFMPCasqueVR = new ThreadFMPCasqueVR(this.monIHMCasqueVR);
				this.monThreadFMPCasqueVR.start();
				this.monIHMDrone.setCasqueVR(false);
			}
			//Si lutilisateur veut revenir vers un controle uniquement manuel des drones alors on arrete le thread et l IHM permettant le controle avec le casque VR
			if(this.monThreadFMPCasqueVR != null && this.monThreadFMPCasqueVR.isAlive() && this.monIHMCasqueVR.getRetour())
			{
				System.out.println("Passage au Controle Manuel");
				this.monThreadFMPCasqueVR.setflagEndThreadFMPCasqueVR(true);
				while(this.monThreadFMPCasqueVR.isAlive())
				{
					new Tempo(1);
				}
				this.monThreadFMPCasqueVR = null;
				this.monIHMCasqueVR.StopIHM();
				this.monIHMCasqueVR = null;
			}
			
			if(this.monIHMDrone.getIsSequence())
			{
				this.monIHMDrone.setUIDecollage(false);
				this.monIHMDrone.setUIAtterissage(false);
				this.monIHMDrone.setUIRobotHaptique(false);
				this.monIHMDrone.setUISequence(false);
				this.monIHMDrone.setUISuiviChar(false);
				this.monIHMDrone.setUICasqueVR(false);
				this.monIHMDrone.setUIControle_Commande(false);
				if(this.monThreadCommandQueue == null)
				{
					this.monThreadCommandQueue = new ThreadCommandQueue(this.Drones);
					this.monThreadCommandQueue.start();
				}
				if(this.monIHMSequence == null)
				{
					this.monIHMSequence = new IHM_Sequence(this.Drones,this.monThreadCommandQueue);
					this.monIHMSequence.setSequences(this.monIHMSequence.chargerSequences());
					this.monIHMSequence.updateSequenceDropdown();
				}
				this.monIHMDrone.setIsSequence(false);
			}
			
			if(this.monIHMSequence != null && this.monIHMSequence.isRetour())
			{
				this.monIHMSequence.StopIHM();
				this.monIHMSequence = null;
				if(this.monThreadCommandQueue != null)
				{
					this.monThreadCommandQueue.stopQueue();
					this.monThreadCommandQueue = null;
				}
				this.monIHMDrone.setUIDecollage(true);
				this.monIHMDrone.setUIAtterissage(false);
				this.monIHMDrone.setUIRobotHaptique(false);
				this.monIHMDrone.setUISequence(true);
				this.monIHMDrone.setUISuiviChar(false);
				this.monIHMDrone.setUICasqueVR(false);
				this.monIHMDrone.setUIControle_Commande(false);
			}
			
			if(this.monIHMDrone.getIsTakeOff())
			{
				System.out.println("-------------------------------");
				System.out.println("Decollage");
				GestionCommandesDrones.sendCommandToDrones("takeoff");
				this.monIHMDrone.setIsTakeOff(false);
				new Tempo(5000);
				this.monIHMDrone.setUIDecollage(false);
				this.monIHMDrone.setUIAtterissage(true);
				this.monIHMDrone.setUIRobotHaptique(true);
				this.monIHMDrone.setUISequence(false);
				this.monIHMDrone.setUISuiviChar(true);
				this.monIHMDrone.setUICasqueVR(true);
				this.monIHMDrone.setUIControle_Commande(true);
				//On demarre le chrono permettant de savoir le temps de vol
				GestionTimer.StartTimerVol();
			}
			if(this.monIHMDrone.getIsLanding())
			{
				System.out.println("-------------------------------");
				System.out.println("Atterissage");
				
				for(int i =0; i < 10 ; i++)
				{
					GestionCommandesDrones.sendCommandToDrones("land");
					new Tempo(1000);
				}
				
				this.monIHMDrone.setIsLanding(false);
				this.monIHMDrone.setUIDecollage(true);
				this.monIHMDrone.setUIAtterissage(false);
				this.monIHMDrone.setUIRobotHaptique(false);
				this.monIHMDrone.setUISequence(true);
				this.monIHMDrone.setUISuiviChar(false);
				this.monIHMDrone.setUICasqueVR(false);
				this.monIHMDrone.setUIControle_Commande(false);
				//On arrete le chrono permettant de savoir le temps de vol
				GestionTimer.StopTimerVol();
				//Si l atterissage a lieu et que des interfaces permettant le controle non manuel des drones sont encore presentes alors on les arrete
				if(this.monIHMRobot != null)
				{
					this.monThreadFMPRobot.setflagEndThreadFMPRobotHaptique(true);
					while(this.monThreadFMPRobot.isAlive())
					{
						new Tempo(1);
					}
					this.monThreadFMPRobot = null;
					this.monIHMRobot.StopIHM();
					this.monIHMRobot = null;
				}
				if(this.monIHMCasqueVR != null)
				{
					this.monThreadFMPCasqueVR.setflagEndThreadFMPCasqueVR(true);
					while(this.monThreadFMPCasqueVR.isAlive())
					{
						new Tempo(1);
					}
					this.monThreadFMPCasqueVR = null;
					this.monIHMCasqueVR.StopIHM();
					this.monIHMCasqueVR = null;
				}
			}
			if(this.monIHMDrone.getIsUp())
			{
				GestionCommandesDrones.sendCommandToDrones("stick 0 0 0 0.2 0");
				new Tempo(2000);
				GestionCommandesDrones.sendCommandToDrones("stick 0 0 0 0 0");
				this.monIHMDrone.setIsUp(false);
				this.monIHMDrone.setUIControle_Commande(true);
			}
			if(this.monIHMDrone.getIsDown())
			{
				GestionCommandesDrones.sendCommandToDrones("stick 0 0 0 -0.2 0");
				new Tempo(2000);
				GestionCommandesDrones.sendCommandToDrones("stick 0 0 0 0 0");
				this.monIHMDrone.setIsDown(false);
				this.monIHMDrone.setUIControle_Commande(true);
			}
			if(this.monIHMDrone.getIsLeft())
			{
				GestionCommandesDrones.sendCommandToDrones("stick -0.2 0 0 0 0");
				new Tempo(2000);
				GestionCommandesDrones.sendCommandToDrones("stick 0 0 0 0 0");
				this.monIHMDrone.setIsLeft(false);
				this.monIHMDrone.setUIControle_Commande(true);
			}
			if(this.monIHMDrone.getIsRight())
			{
				GestionCommandesDrones.sendCommandToDrones("stick 0.2 0 0 0 0");
				new Tempo(2000);
				GestionCommandesDrones.sendCommandToDrones("stick 0 0 0 0 0");
				this.monIHMDrone.setIsRight(false);
				this.monIHMDrone.setUIControle_Commande(true);
			}
			if(this.monIHMDrone.getIsForward())
			{
				GestionCommandesDrones.sendCommandToDrones("stick 0 0.2 0 0 0");
				new Tempo(2000);
				GestionCommandesDrones.sendCommandToDrones("stick 0 0 0 0 0");
				this.monIHMDrone.setIsForward(false);
				this.monIHMDrone.setUIControle_Commande(true);
			}
			if(this.monIHMDrone.getIsBackward())
			{
				GestionCommandesDrones.sendCommandToDrones("stick 0 -0.2 0 0 0");
				new Tempo(2000);
				GestionCommandesDrones.sendCommandToDrones("stick 0 0 0 0 0");
				this.monIHMDrone.setIsBackward(false);
				this.monIHMDrone.setUIControle_Commande(true);
			}
			if(this.monIHMDrone.isButton90())
			{
				GestionCommandesDrones.sendRotationtoDrone(90);
				this.monIHMDrone.setButton90(false);
				this.monIHMDrone.setUIControle_Commande(true);
			}
			if(this.monIHMDrone.isButtonMinus90())
			{
				GestionCommandesDrones.sendRotationtoDrone(-90);
				this.monIHMDrone.setButtonMinus90(false);
				this.monIHMDrone.setUIControle_Commande(true);
			}
			if(this.monIHMDrone.isButton180())
			{
				GestionCommandesDrones.sendRotationtoDrone(180);
				GestionCommandesDrones.sendCommandToDrones("stick 0 0 0 0 0");
				this.monIHMDrone.setButton180(false);
				this.monIHMDrone.setUIControle_Commande(true);
			}
			if(this.monIHMDrone.isIsflipDroit())
			{
				
				GestionCommandesDrones.sendCommandToDrones("flip 3");
				new Tempo(1000);
				GestionCommandesDrones.sendCommandToDrones("stick 0 0 0 0 0");
				this.monIHMDrone.setIsflipDroit(false);
				this.monIHMDrone.setUIControle_Commande(true);
			}
			if(this.monIHMDrone.isIsflipGauche())
			{
				GestionCommandesDrones.sendCommandToDrones("flip 1");
				new Tempo(1000);
				GestionCommandesDrones.sendCommandToDrones("stick 0 0 0 0 0");
				this.monIHMDrone.setIsflipGauche(false);
				this.monIHMDrone.setUIControle_Commande(true);
			}
			if(this.monIHMDrone.isIsflipAvant())
			{
				GestionCommandesDrones.sendCommandToDrones("flip 0");
				new Tempo(1000);
				GestionCommandesDrones.sendCommandToDrones("stick 0 0 0 0 0");
				this.monIHMDrone.setIsflipAvant(false);
				this.monIHMDrone.setUIControle_Commande(true);
			}
			if(this.monIHMDrone.isIsflipArriere())
			{
				GestionCommandesDrones.sendCommandToDrones("flip 2");
				new Tempo(1000);
				GestionCommandesDrones.sendCommandToDrones("stick 0 0 0 0 0");
				this.monIHMDrone.setIsflipArriere(false);
				this.monIHMDrone.setUIControle_Commande(true);
			}
			new Tempo(1);
		}
		System.out.println("Fin du Thread de Surveillance Bouton");
		if(this.monIHMSequence != null)
		{
			this.monIHMSequence.StopIHM();
		}
		if(this.monThreadCommandQueue != null)
		{
			this.monThreadCommandQueue.stopQueue();
		}
		
		if(this.monThreadFMPRobot != null && this.monThreadFMPRobot.isAlive())
		{
			this.monThreadFMPRobot.setflagEndThreadFMPRobotHaptique(true);
			while(this.monThreadFMPRobot.isAlive())
			{
				new Tempo(1);
			}
			if(this.monIHMRobot != null)
			{
				this.monIHMRobot.StopIHM();
			}
		}
		
		if(this.monThreadFMPCasqueVR != null && this.monThreadFMPCasqueVR.isAlive())
		{
			this.monThreadFMPCasqueVR.setflagEndThreadFMPCasqueVR(true);
			while(this.monThreadFMPCasqueVR.isAlive())
			{
				new Tempo(1);
			}
			if(this.monIHMCasqueVR != null)
			{
				this.monIHMCasqueVR.StopIHM();
			}
		}
		System.gc();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void setflagEndThreadSurveillanceBouton(boolean etat)
	{
		this.flagEndThreadSurveillanceBouton = etat;
	}
}
