package Threads;

import java.util.concurrent.ConcurrentLinkedQueue;
import com.xpfriend.tydrone.SimpleMain;
import Utils.Tempo;

/**
 * @author MONTOYA Daryl
 * @version 1.0
 * @date 22/03/25
 * @modification 09/04/25
 * @description Thread a executer des commandes dans une file d attente
 *
 */

public class ThreadCommandQueue extends Thread 
{
    private ConcurrentLinkedQueue<String> commandQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Integer> commandQueueTempo = new ConcurrentLinkedQueue<>();
    private boolean isRunning = true, isPaused = true, isFinish = false;
    private SimpleMain[] drones;

    public ThreadCommandQueue(SimpleMain[] drones) 
    {
        this.drones = drones;
    }
    
    // Execution des commandes dans la queue
    public void run() 
    {
    	System.out.println("Debut du Thread de Command Queue");
        while (this.isRunning)
        {
            if (!this.isPaused) 
            {
            	if(this.commandQueue.isEmpty()) //Si la queue est vide
            	{
            		this.isFinish = true;
                	this.isPaused = true;
            	}
            	else
            	{
            		String command = this.commandQueue.poll(); // Recupere la commande en tete de la queue
                    int Tempo = this.commandQueueTempo.poll(); //Recupere le temps de tempo associe a la commande
                    if (command != null)
                    {
                        this.executeCommand(command,Tempo); // Execute la commande
                    }
            	}
                new Tempo(1500);
            }
            new Tempo(1);
        }
        System.out.println("Fin du Thread de Command Queue");
        this.commandQueue.clear();
        this.commandQueueTempo.clear();
        System.gc();
    }
    
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Ajouter une commande a la queue
    public void addToCommandQueue(String command, int _Tempo)
    {
        this.commandQueue.add(command);
        this.commandQueueTempo.add(_Tempo);
    }

    // Demarrer l'execution de la queue de commandes
    public void startQueue() 
    {
        if (this.isRunning) 
        {
        	this.isPaused = false;
        	this.isFinish = false;
        }
    }

    // Mettre la queue en pause
    public void pauseQueue() 
    {
    	this.isPaused = true;
    }

    // Reprendre la queue
    public void resumeQueue() 
    {
    	this.isPaused = false;
    }

    // Arreter la queue
    public void stopQueue() 
    {
    	this.isRunning = false;
    }
    
    // Savoir si les commandes ont toutes ete executees
    public boolean getisFinished()
    {
    	return this.isFinish;
    }

    // Logique d'execution de la commande
    private void executeCommand(String _commande, int _Tempo) 
    {
    	System.out.println("Commande executee: "+_commande+" avec un tempo de: "+_Tempo);
    	
    	//On effectue des tests permettant de filtrer certaines commandes qui ont des particularites
    	if(_commande.split("/")[0].equals("land"))
    	{
    		for(int i = 0; i < 10; i++)
    		{
    			this.sendCommandToDrones(_commande,_Tempo,false);
    		}
    	}
    	else if(_commande.split("/")[0].equals("cw"))
    	{
    		int Deg = Integer.parseInt(_commande.split("/")[1]);
    		this.sendRotationtoDrone(Deg);
    	}
    	else if(_commande.split("/")[0].equals("ccw"))
    	{
    		int Deg = -1*Integer.parseInt(_commande.split("/")[1]);
    		this.sendRotationtoDrone(Deg);
    	}
    	else
    	{
    		this.sendCommandToDrones(_commande,_Tempo,true);
    	}
    	
    }

    //Methode permettant l execution des commandes
    private synchronized void sendCommandToDrones(String command, int _Tempo, boolean Arret)
    {
        for (SimpleMain drone : this.drones) 
        {
            drone.entryCommand(command);
        }
        new Tempo(_Tempo);
        if(Arret)
        {
        	for (SimpleMain drone : this.drones) 
            {
                drone.entryCommand("stick 0 0 0 0 0");
            }
        }
    }
    
    private double ConvertAngle180To360(double angle)
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
	private synchronized void sendRotationtoDrone(int Deg)
	{
		double[] DegDepart = new double[this.drones.length];
		double[] DegFinal = new double[this.drones.length];
		boolean[] RotationFinished = new boolean[this.drones.length]; //Tableau de booleen permettant de savoir quand la rotation de tous les drones est finie
		for (int i =0 ;i < this.drones.length; i++)
		{
			DegDepart[i] = this.ConvertAngle180To360(Double.parseDouble(this.drones[i].getState("yaw")));
			DegFinal[i] = this.ConvertAngle180To360(DegDepart[i] + Deg);
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
			this.sendCommandToDrones("stick 0 0 -0.25 0 0",0,false);
		}
		else
		{
			this.sendCommandToDrones("stick 0 0 0.25 0 0",0,false);
		}
		for(int i = 0; i < this.drones.length; i++)
		{
			final int index = i;
			Runnable myRunnable = () -> {
				System.out.println("Debut de la rotation");
				do
				{
					double angle_actuel = this.ConvertAngle180To360(Double.parseDouble(this.drones[index].getState("yaw")));
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
				this.drones[index].entryCommand("stick 0 0 0 0 0");
				System.out.println("Fin de la rotation");
	        };
	        new Thread(myRunnable).start();
		}
		
		boolean WaitForAllDrones = false; //Boolean qui permet d attendre la rotation de tous les drones
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
		
	}
}
