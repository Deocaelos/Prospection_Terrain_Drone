import com.xpfriend.tydrone.SimpleMain;

import Utils.GestionTimer;
import Utils.Tempo;

/**
 * @author RIAS Julien ABAYEV Ahmed
 * @version 1.0
 * @date 13/05/25
 * @modification 13/05/25
 * @description Thread servant a la reception de la telemetrie
 *
 */
public class ThreadReceptionTelemetrie extends Thread
{
	private boolean flagEndThreadReceptionTelemetrie = false, isTelemetrie = false;
	private SimpleMain[] Drones = null;
	private cFileMappingTelloEduTelemetryServeur[] monServeurFMPTelemetrie = null;
	
	public ThreadReceptionTelemetrie(SimpleMain[] _Drones)
	{
		this.Drones = _Drones;
		this.monServeurFMPTelemetrie = new cFileMappingTelloEduTelemetryServeur[this.Drones.length];
	}
	
	public void run()
	{
		System.out.println("Debut du Thread Reception Telemetrie");
		new Tempo(10000); //attente pour eviter des demandes de telemetrie alors que le drone ne l envoi pas encore
		//Activer l extension des donnes telemetriques des drones et on initialise les differents serveurs FMP
		for(int i = 0; i < this.Drones.length; i++)
		{
			this.monServeurFMPTelemetrie[i] = new cFileMappingTelloEduTelemetryServeur(false);
			this.monServeurFMPTelemetrie[i].OpenServer("TelemetrieDrone"+(i+1));
			this.Drones[i].setRichStates(true);
		}
		
		while(!this.flagEndThreadReceptionTelemetrie)
		{
			//On recupere la telemetrie des drones
			for(int i = 0; i < this.Drones.length; i++)
			{
				//Placement des valeurs traitees dans le fichier de memoire partagee pour la telemetrie
				this.setValuesToFMP(this.TraitementDonnees(this.Drones[i].getStates()),this.monServeurFMPTelemetrie[i]);
				if(!this.isTelemetrie)
				{
					this.isTelemetrie = true;
				}
			}
			new Tempo(1);
		}
		System.out.println("Fin du Thread Reception Telemetrie");
		for(int i = 0; i < this.Drones.length; i++)
		{
			this.monServeurFMPTelemetrie[i].CloseServer();
		}
		System.gc();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public boolean getIsTelemetrie()
	{
		return this.isTelemetrie;
	}
	
	
	public void setflagEndThreadReceptionTelemetrie(boolean etat)
	{
		this.flagEndThreadReceptionTelemetrie = etat;
	}
	
	
	/**
	 * Methode permettant le traitement des donnees telemetriques recues
	 * @param _Donnees
	 * @return
	 */
	private String[] TraitementDonnees(String _Donnees)
	{
		String[] ValeursTele = new String[2];
		String[] ValeursTeleEnDeordre = new String[12];
		String[] TabValeur = _Donnees.split(";");				// separe les donnees grace au ; 
		for(int i = 0; i < TabValeur.length-1; i++)				
		{
			
			ValeursTele = TabValeur[i].split(":");				// on refait une separation avec : pour separer la valeur et le nom de la valeur ( ex: pitch:0 -> on garde juste la valeur)
			
			switch(ValeursTele[0])
			{
				case "vx":
					ValeursTeleEnDeordre[3] = ValeursTele[1];
					break;
				case "vy":
					ValeursTeleEnDeordre[4] = ValeursTele[1];
					break;
				case "vz":
					ValeursTeleEnDeordre[5] = ValeursTele[1];
					break;
				case "ax":
					ValeursTeleEnDeordre[0] = ValeursTele[1];
					break;
				case "ay":
					ValeursTeleEnDeordre[1] = ValeursTele[1];
					break;
				case "az":
					ValeursTeleEnDeordre[2] = ValeursTele[1];
					break;
				case "pitch":
					ValeursTeleEnDeordre[9] = ValeursTele[1];
					break;
				case "roll":
					ValeursTeleEnDeordre[10] = ValeursTele[1];
					break;
				case "yaw":
					ValeursTeleEnDeordre[11] = ValeursTele[1];
					break;
				case "h":
					ValeursTeleEnDeordre[6] = ValeursTele[1];
					break;
				case "time":
					ValeursTeleEnDeordre[8] = Integer.toString(GestionTimer.getTempsEcoule()); //Ici on utilise un timer interne car avec le protocol de bas niveau les drones ne semblent pas le faire par eux meme
					break;
				case "bat":
					ValeursTeleEnDeordre[7] = ValeursTele[1];
					break;
			}
			
		}
		return ValeursTeleEnDeordre;
	}
	
	/**
	 * Methode permettant d envoyer les valeurs dnas le fichier de mapping
	 * @param monServeurFMP
	 * @param _values
	 * @return
	 */
	
	private void setValuesToFMP(String[] _values, cFileMappingTelloEduTelemetryServeur monServeurFMP)
	{
		if(_values[0]!=null)
		{
			monServeurFMP.setVirtualTelloEduTelemetryAgx(Double.parseDouble(_values[0]));
		}
		if(_values[1]!= null)
		{
			monServeurFMP.setVirtualTelloEduTelemetryAgy(Double.parseDouble(_values[1]));
		}
		if(_values[2]!= null)
		{
			monServeurFMP.setVirtualTelloEduTelemetryAgz(Double.parseDouble(_values[2]));
		}
		if(_values[3]!= null)
		{
			monServeurFMP.setVirtualTelloEduTelemetryVgx(Double.parseDouble(_values[3]));
		}
		if(_values[4]!= null)
		{
			monServeurFMP.setVirtualTelloEduTelemetryVgy(Double.parseDouble(_values[4]));
		}
		if(_values[5]!= null)
		{
			monServeurFMP.setVirtualTelloEduTelemetryVgz(Double.parseDouble(_values[5]));
		}
		if(_values[6]!= null)
		{
			monServeurFMP.setVirtualTelloEduTelemetryH(Integer.parseInt(_values[6]));
		}
		if(_values[7]!= null)
		{
			monServeurFMP.setVirtualTelloEduTelemetryBatteryValue(Integer.parseInt(_values[7]));
		}
		if(_values[8]!= null)
		{
			monServeurFMP.setVirtualTelloEduTelemetryFlyTime(Integer.parseInt(_values[8]));
		}
		if(_values[9]!= null)
		{
			monServeurFMP.setVirtualTelloEduTelemetryPitch(Integer.parseInt(_values[9]));
		}
		if(_values[10]!= null)
		{
			monServeurFMP.setVirtualTelloEduTelemetryRoll(Integer.parseInt(_values[10]));
		}
		if(_values[11]!= null)
		{
			monServeurFMP.setVirtualTelloEduTelemetryYaw(Integer.parseInt(_values[11]));
		}
	}
	
	
	
}
