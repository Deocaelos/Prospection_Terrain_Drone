

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import com.xpfriend.tydrone.SimpleMain;

import Handler.ConnectTelloHandler;
import Interfaces.IHM_Connexion;
import Interfaces.IHM_ControleDrone;
import Threads.ThreadGestionTraitementImage;
import Utils.Tempo;

/**
 * @author RIAS Julien
 * @version 1.0
 * @date 01/04/25
 * @modification 02/04/25
 * @description Classe ThreadGestionConnexionDrone permettant la connexion aux drones
 *
 */

public class ThreadGestionConnexionDrone extends Thread
{
	private boolean flagEndThreadConnexionDrone = false, DronesConnectes = false, isCHAI = false;
	private IHM_ControleDrone monIHMDrone = null;
	private String[] ReseauxDrones = null;
	private int NBDrone = 2;
	private SimpleMain[] ListDrone = null;
	private IHM_Connexion monIHM_Connexion = null;
	private ThreadSurveillanceBouton monThreadSurveillanceBouton = null;
	private ThreadGestionTraitementImage _ThreadGestionTraitementImage = null;
	private ThreadEcritureFMPVideo monThreadEcritureFMP = null;
	private ThreadReceptionTelemetrie monThreadReceptionTelemetrie = null;
	private ThreadCommAppAndroid monThreadCommAndroid = null;
	private ConnectTelloHandler monHandlerConnect = null;
	
	public ThreadGestionConnexionDrone(IHM_Connexion _IHM_Connexion,String[] _listeDrones, String[] _maListeMAC)
	{
		this.monIHM_Connexion = _IHM_Connexion;
	}
	
	public void run()
	{
		System.out.println("Debut du Thread Connexion");
		this.ReseauxDrones = new String[this.NBDrone];
		while(!this.flagEndThreadConnexionDrone)
		{
			if(this.monIHM_Connexion!= null && this.monIHM_Connexion.getisConnection())
			{
				System.out.println("Connexion aux drones");
				//Recuperation des reseaux auquels il faut se connecter
				//On extrait les 3 derniers hexas de la mac afin de former le reseau auquel il faut se connecter TELLO-AABBCC
				String[] MacDrone1 = this.monIHM_Connexion.getSelectedDroneMAC1().split("-");
				this.ReseauxDrones[0] = "TELLO-"+MacDrone1[3].toUpperCase()+MacDrone1[4].toUpperCase()+MacDrone1[5].toUpperCase();
				String[] MacDrone2 = this.monIHM_Connexion.getSelectedDroneMAC2().split("-");
				this.ReseauxDrones[1] = "TELLO-"+MacDrone2[3].toUpperCase()+MacDrone2[4].toUpperCase()+MacDrone2[5].toUpperCase();
				
				this.monIHM_Connexion.setisConnection(false);
				this.monIHM_Connexion.StopIHMAccueil();
				this.monIHM_Connexion = null;
				
				//On lance l IHM de controle des drones
				this.monIHMDrone = new IHM_ControleDrone();
				this.monIHMDrone.setUIControle_Commande(false);
				this.monIHMDrone.setUIDecollage(false);
				this.monIHMDrone.setUIAtterissage(false);
				this.monIHMDrone.setUIRobotHaptique(false);
				this.monIHMDrone.setUISequence(false);
				this.monIHMDrone.setUISuiviChar(false);
				
				//On lance le Thread De Gestion du Traitement d Image
				this._ThreadGestionTraitementImage = new ThreadGestionTraitementImage(this.monIHMDrone);
				this._ThreadGestionTraitementImage.start();
				
				//On informe l utilisateur de la configuration de ses interfaces wifi
				this.AfficherPopUp("Configuration des Interfaces Wifi, veuillez attendre", true);
				this.monHandlerConnect = new ConnectTelloHandler(this.NBDrone,this.ReseauxDrones);
				this.monHandlerConnect.goConnect(this.monIHMDrone,this._ThreadGestionTraitementImage);
				
				//On informe l utilisateur de la configuration de ses interfaces wifi
				this.AfficherPopUp("Le controle des Drones est actif", true);
				this.DronesConnectes = true;
				this.ListDrone = this.monHandlerConnect.getDronesAccess();
				this.monIHMDrone.setUIDecollage(true);
				this.monIHMDrone.setUISequence(true);
				
			}
			if(this.DronesConnectes)
			{
				if(this.monThreadSurveillanceBouton == null)
				{
					this.monThreadSurveillanceBouton = new ThreadSurveillanceBouton(this.monIHMDrone,this.ListDrone);
					this.monThreadSurveillanceBouton.start();
				}
				
				if(this.monThreadEcritureFMP == null)
				{
					this.monThreadEcritureFMP = new ThreadEcritureFMPVideo(this._ThreadGestionTraitementImage);
					this.monThreadEcritureFMP.start();
				}
				
				if(this.monThreadReceptionTelemetrie == null)
				{
					this.monThreadReceptionTelemetrie = new ThreadReceptionTelemetrie(this.ListDrone);
					this.monThreadReceptionTelemetrie.start();
					while(!this.monThreadReceptionTelemetrie.getIsTelemetrie())
					{
						new Tempo(500);
					}
				}
				
				if(this.monThreadCommAndroid == null)
				{
					this.monThreadCommAndroid = new ThreadCommAppAndroid(this.ListDrone, this.monIHMDrone);
					this.monThreadCommAndroid.start();
				}
				
				new Tempo(10000);
				if(!this.isCHAI)
				{
					//Lancement de l executable de chai3d
					String path = ThreadGestionConnexionDrone.class.getProtectionDomain().getCodeSource().getLocation().getPath();
					try {
						path = URLDecoder.decode(path, StandardCharsets.UTF_8.name());
						File base = new File(path);
						
						// Chemin complet vers l’exécutable
		                File exe = new File(base, "12-Surveillance_Drone.exe");
		                
		                // Commande Windows pour forcer l'ouverture d'un terminal
		                String[] cmd = {
		                    "cmd.exe",
		                    "/c",
		                    "start",
		                    "\"\"",
		                    exe.getAbsolutePath()
		                };
		                
		                new ProcessBuilder(cmd).start();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					this.isCHAI = true;
				}
			}
			if(this.monIHMDrone != null && this.monIHMDrone.getEtatBoutonQuitter())
			{
				System.out.println("Arret du Programme");
				//Arret du Thread Ecriture FMP
				
				if(this.monThreadEcritureFMP != null)
				{
					this.monThreadEcritureFMP.setflagEndThreadEcritureFMPVideo(true);
					while(this.monThreadEcritureFMP.isAlive())
					{
						new Tempo(1);
					}
				}
				
				//Arret du Thread Surveillance Bouton
				if(this.monThreadSurveillanceBouton != null)
				{
					this.monThreadSurveillanceBouton.setflagEndThreadSurveillanceBouton(true);
					while(this.monThreadSurveillanceBouton.isAlive())
					{
						new Tempo(1);
					}
				}
				//Arret du Thread Traitement Image
				if(this._ThreadGestionTraitementImage != null)
				{
					this._ThreadGestionTraitementImage.setflagEndThreadGestionTraitementImage(true);
					while(this._ThreadGestionTraitementImage.isAlive())
					{
						new Tempo(1);
					}
				}
				//Arret du Thread Traitement Image
				if(this.monThreadReceptionTelemetrie != null)
				{
					this.monThreadReceptionTelemetrie.setflagEndThreadReceptionTelemetrie(true);
					while(this.monThreadReceptionTelemetrie.isAlive())
					{
						new Tempo(1);
					}
				}
				//Arret de l IHM
				if(this.monIHMDrone != null)
				{
					this.monIHMDrone.StopIHM();
				}
				//Deconnexion des drones
				for(int i = 0 ; i < this.ListDrone.length ; i++)
				{
					this.ListDrone[i].close();
				}
				new Tempo(1000);
				//Deconfiguration des interfaces wifi
				this.AfficherPopUp("Deconfiguration des interfaces", true);
				this.monHandlerConnect.Start_OnlyReset();
				
				this.flagEndThreadConnexionDrone = true;
			}
			new Tempo(1);
		}
		System.out.println("Fin du Thread Connexion");
		System.gc();
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Methode permettant d afficher une pop up systeme
	 * @param text
	 * @param Info
	 */
	private void AfficherPopUp(String text, boolean Info)
	{
		if (!SystemTray.isSupported()) 
		{
            System.out.println("Le SystemTray n est pas supporte");
            return;
        }

        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        TrayIcon trayIcon = new TrayIcon(image, "Notification");
        trayIcon.setImageAutoSize(true);
        try {
			tray.add(trayIcon);
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        if(Info)
        {
        	trayIcon.displayMessage("SkyWind Explorer", text, TrayIcon.MessageType.INFO);
        }
        else
        {
        	trayIcon.displayMessage("SkyWind Explorer", text, TrayIcon.MessageType.WARNING);
        }
	}
	
}
