

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import Interfaces.IHM_AjoutDrone;
import Interfaces.IHM_ChoixUtilisateur;
import Interfaces.IHM_Connexion;
import Utils.Tempo;

/**
 * @author RIAS Julien
 * @version 1.0
 * @date 31/03/25
 * @modification 31/03/25
 * @description Classe Thread_SurveillanceChoixUtilisateur
 *
 */

public class Thread_SurveillanceChoixUtilisateur extends Thread
{
	private boolean flagEndThreadSurveillanceChoixUtilisateur = false;
	private static final String YAML_FILE = "ressources/drones_config.yaml";
	private IHM_ChoixUtilisateur monIHMChoix = null;
	private IHM_AjoutDrone monIHMAjoutDrone = null;
	
	public Thread_SurveillanceChoixUtilisateur(IHM_ChoixUtilisateur _IHM_ChoixUtilisateur)
	{
		this.monIHMChoix = _IHM_ChoixUtilisateur;
	}
	
	public void run()
	{
		System.out.println("Debut du Thread Surveillance Choix");
		while(!this.flagEndThreadSurveillanceChoixUtilisateur)
		{
			if(this.monIHMChoix.getisConfirmer())
			{
				this.monIHMChoix.setUIListeDrone(false);
				switch(this.monIHMChoix.getChoixUser())
				{
					case "Ajouter un Drone":
						System.out.println("Choix: Ajout de drone");
						//Instanciation de l IHM pour l ajout d un drone
						this.monIHMAjoutDrone = new IHM_AjoutDrone();
						//Attente de la fin de l ajout par l utilisateur
						while(!this.monIHMAjoutDrone.isFinished())
						{
							new Tempo(1);
						}
						//Arret de l IHM
						this.monIHMAjoutDrone.StopIHM();
						this.monIHMChoix.setUIConfirmation(true);
						break;
					case "Voir la liste des Drones":
						System.out.println("Choix: Voir la liste des drones");
						this.afficherListeDrones();
						this.monIHMChoix.setUIListeDrone(true);
						this.monIHMChoix.setUIConfirmation(true);
						break;
					case "Se Connecter aux Drones":
						System.out.println("Choix: Connexion aux drones");
						//Declaration et Instanciation de l IHM pour la connexion aux drones
						IHM_Connexion monIHMConnexion = new IHM_Connexion(this.getListeDrones(), this.getListeMAC());
						//Declaration et Instanciation du thread ThreadGestionConnexionDrone
						ThreadGestionConnexionDrone monThreadConnexion = new ThreadGestionConnexionDrone(monIHMConnexion,this.getListeDrones(), this.getListeMAC());
						monThreadConnexion.start();
						this.flagEndThreadSurveillanceChoixUtilisateur = true;
						//Arret de l IHM de choix
						this.monIHMChoix.StopIHM();
						break;
					case "Quitter":
						System.out.println("Choix: Arret du Programme");
						this.flagEndThreadSurveillanceChoixUtilisateur = true;
						this.monIHMChoix.StopIHM();
						break;
				}
				this.monIHMChoix.setisConfirmer(false);
				
			}
			new Tempo(1);
		}
		System.out.println("Fin du Thread Surveillance Choix");
		System.gc();
	}
	
	
    //*****************************METHODES*********************************
	
	/**
     * Recuperer les informations des drones sous forme de tableau de chaines (nom et IP)
     * @return listeDrones
     */
    private String[] getListeDrones() 
    {
    	List<Map<String, String>> Drones_List = this.ChargerDrones();
        if (Drones_List == null || Drones_List.isEmpty()) 
        {
            return new String[0];
        }

        String[] listeDrones = new String[Drones_List.size()];
        for (int i = 0; i < Drones_List.size(); i++) 
        {
            Map<String, String> drone = Drones_List.get(i);
            listeDrones[i] = drone.get("nom") + " -> MAC : " + drone.get("mac");
        }
        return listeDrones;
    }

    /**
     * Recuperer les IP des drones
     * @return ipAddresses
     */
    private String[] getListeMAC() 
    {
    	List<Map<String, String>> Drones_List = this.ChargerDrones();
        if (Drones_List == null || Drones_List.isEmpty()) 
        {
            return new String[0];
        }

        String[] macAddresses = new String[Drones_List.size()];
        for (int i = 0; i < Drones_List.size(); i++) 
        {
            Map<String, String> drone = Drones_List.get(i);
            macAddresses[i] = drone.get("mac");
        }
        return macAddresses;
    }
	
	
	/***
	 * Charger les drones depuis le fichier YAML
	 * @return Liste Drones
	 */
    private List<Map<String, String>> ChargerDrones() 
    {
        Yaml yaml = new Yaml();
        try (FileInputStream inputStream = new FileInputStream(Thread_SurveillanceChoixUtilisateur.YAML_FILE)) 
        {
            // Charger directement la liste des drones comme une liste de maps
            Map<String, List<Map<String, String>>> data = yaml.load(inputStream);
            return data.get("drones");
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement du fichier YAML : " + e.getMessage());
            return null;
        }
    }
	
	/**
     * Afficher la liste des drones avec IP et MAC
     */
    private void afficherListeDrones() 
    {
    	List<Map<String, String>> Drones_List = this.ChargerDrones();
    	String[] Drones = new String[Drones_List.size()];
    	int Index = 0;
        if (Drones_List == null || Drones_List.isEmpty()) 
        {
            System.out.println("Aucun drone enregistre.");
            return;
        }
        for (Map<String, String> drone : Drones_List) 
        {
            Drones[Index] = "Nom : " + drone.get("nom") + " -> IP : " + drone.get("ip") + " -> MAC : " + drone.get("mac");
            Index ++;
        }
        this.monIHMChoix.updateComboBox(Drones);
    }
}
