package Utils;

import com.xpfriend.tydrone.SimpleMain;
/**
 * @author RIAS Julien
 * @version 1.0
 * @date 26/05/25
 * @modification 26/05/25
 * @description Classe permettant d envoyer les commandes aux drones
 *
 */
public class GestionCommandesDrones 
{
	private static SimpleMain[] List_Drones = null;
	
	/**
	 * Methode permettant de definir la liste des drones
	 * @param _Drones
	 */
	public static void setDrones(SimpleMain[] _Drones)
	{
		GestionCommandesDrones.List_Drones = _Drones;
	}
	
	/**
	 * Methode permettant d envoyer une commande aux drones
	 * @param Command
	 */
	public static synchronized void sendCommandToDrones(String Command)
	{
		for(int i =0;i<GestionCommandesDrones.List_Drones.length;i++)
		{
			final int index = i;
			Runnable myRunnable = () -> {
				GestionCommandesDrones.List_Drones[index].entryCommand(Command);
	        };
	        new Thread(myRunnable).start();
		}
	}
	
	private static double ConvertAngle180To360(double angle)
	{
		if(angle < 0)
		{
			angle = angle+360;
		}
		return angle;
	}
	
	/**
	 * Methode permettant de definir un angle de rotation pour les drones
	 * @param Deg
	 */
	public static synchronized void sendRotationtoDrone(int Deg)
	{
		double[] DegDepart = new double[GestionCommandesDrones.List_Drones.length];
		double[] DegFinal = new double[GestionCommandesDrones.List_Drones.length];
		boolean[] RotationFinished = new boolean[GestionCommandesDrones.List_Drones.length]; //Tableau de booleen permettant de savoir quand la rotation de tous les drones est finie
		for (int i =0 ;i < GestionCommandesDrones.List_Drones.length; i++)
		{
			DegDepart[i] = GestionCommandesDrones.ConvertAngle180To360(Double.parseDouble(GestionCommandesDrones.List_Drones[i].getState("yaw")));
			DegFinal[i] = GestionCommandesDrones.ConvertAngle180To360(DegDepart[i] + Deg);
			if(DegFinal[i] == 180)
			{
				DegFinal[i] = 179;
			}
			else if(DegFinal[i] == 360)
			{
				DegFinal[i] = 0;
			}
		}
		if(Deg < 0)
		{
			GestionCommandesDrones.sendCommandToDrones("stick 0 0 -0.25 0 0");
		}
		else
		{
			GestionCommandesDrones.sendCommandToDrones("stick 0 0 0.25 0 0");
		}
		for(int i = 0; i < GestionCommandesDrones.List_Drones.length; i++)
		{
			final int index = i;
			Runnable myRunnable = () -> {
				System.out.println("Debut de la rotation");
				System.out.println("Angle actuel: "+GestionCommandesDrones.ConvertAngle180To360(Double.parseDouble(GestionCommandesDrones.List_Drones[index].getState("yaw"))));
				System.out.println("Angle voulu: "+DegFinal[index]);
				do
				{
					double angle_actuel = GestionCommandesDrones.ConvertAngle180To360(Double.parseDouble(GestionCommandesDrones.List_Drones[index].getState("yaw")));
					if(Deg > 0)
					{
						if(angle_actuel > DegFinal[index])
						{
							System.out.println("Angle detecte drone: "+index);
							RotationFinished[index] = true;
							
						}
					}
					else
					{
						if(angle_actuel < DegFinal[index])
						{
							System.out.println("Angle detecte drone: "+index);
							RotationFinished[index] = true;
						}
					}
					
				}while(!RotationFinished[index]);
				GestionCommandesDrones.List_Drones[index].entryCommand("stick 0 0 0 0 0");
				System.out.println("Fin de la rotation");
	        };
	        new Thread(myRunnable).start();
		}
		
		boolean WaitForAllDrones = false; //Booelan qui permet d attendre la rotation de tous les drones
		while(!WaitForAllDrones)
		{
			for(int i = 0 ; i< RotationFinished.length ; i++)
			{
				if(RotationFinished[i])
				{
					WaitForAllDrones = true;
				}
				else
				{
					WaitForAllDrones = false;
					break;
				}
			}
			new Tempo(1);
		}
		System.out.println("Angle Final: "+Double.parseDouble(GestionCommandesDrones.List_Drones[0].getState("yaw")));
		
	}
}
