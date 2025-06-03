import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import com.xpfriend.tydrone.SimpleMain;

import Interfaces.IHM_ControleDrone;
import Utils.GestionCommandesDrones;
import Utils.Tempo;

/**
 * @author RIAS Julien
 * @version 1.0
 * @date 13/05/25
 * @modification 13/05/25
 * @description Classe ThreadCommAppAndroid de type Thread permettant la communication avec l application Android
 *
 */
public class ThreadCommAppAndroid extends Thread
{
	private boolean flagEndThreadCommAppAndroid = false, isConnected = false;
	private SimpleMain[] Drones;
	private ServerSocket serverSocket_CommAndroid = null;
	private Socket socketTCP_CommAndroid = null;
	private BufferedReader In_CommAndroid = null;
	private PrintWriter Out_CommAndroid = null;
	private int PortCommAndroid = 5117;
	private String TrameClient;
	private ThreadEnvoiTelemetrieAndroid[] monThreadEnvoiAndroid = null;
	private IHM_ControleDrone monIHM_ControleDrone = null;
	
	public ThreadCommAppAndroid(SimpleMain[] _Drones, IHM_ControleDrone _IHM_ControleDrone)
	{
		this.Drones = _Drones;
		this.monThreadEnvoiAndroid = new ThreadEnvoiTelemetrieAndroid[this.Drones.length];
		this.monIHM_ControleDrone = _IHM_ControleDrone;
	}
	
	public void run()
	{
		System.out.println("Debut du Thread Comm App Android");
		try {
			this.serverSocket_CommAndroid = new ServerSocket(this.PortCommAndroid);
			while(!this.flagEndThreadCommAppAndroid)
			{
				if(!this.isConnected)
				{
					System.out.println("En attente de connexion client Android ......");
					this.monIHM_ControleDrone.SetTextEtatCommAndroid("En attente de connexion client Android", Color.orange);
					//Attente d une connexion client
					this.socketTCP_CommAndroid = this.serverSocket_CommAndroid.accept();
					System.out.println("Client Android Connecte depuis "+this.socketTCP_CommAndroid.getInetAddress());
					this.monIHM_ControleDrone.SetTextEtatCommAndroid("Client Android connecte depuis: "+this.socketTCP_CommAndroid.getInetAddress(), Color.green);
					//Configuration des flux d entree et de sortie
					this.In_CommAndroid = new BufferedReader(new InputStreamReader(this.socketTCP_CommAndroid.getInputStream()));
					this.Out_CommAndroid = new PrintWriter(this.socketTCP_CommAndroid.getOutputStream(),true);
					
					//On instancie les differents threads d envoi de telemetrie
					for(int i = 0 ; i < this.Drones.length; i++)
					{
						this.monThreadEnvoiAndroid[i] = new ThreadEnvoiTelemetrieAndroid(i+1,this.socketTCP_CommAndroid.getInetAddress());
						this.monThreadEnvoiAndroid[i].start();
					}
					
					//On envoie un accuse de connexion
					this.SendMessageTCP("ack_connexion");
					this.isConnected = true;
				}
				else
				{
					//On attend une trame client
					this.TrameClient = this.In_CommAndroid.readLine();
					if(this.TrameClient != null)
					{
						//Si cette trame n est pas null on lance le traitement de celle ci afin de savoir ce que le programme doit faire
						System.out.println("Commande recue du client Android : "+this.TrameClient);
						this.TraitementRequete(this.TrameClient);
					}
					else
					{
						//Si la trame est null alors on considere que le client est eteint donc on ferme les flux d entree et de sortie
						System.out.println("Client Android deconnecte....");
						for(int i = 0 ; i < this.Drones.length; i++)
						{
							this.monThreadEnvoiAndroid[i].StopThread();
							while(this.monThreadEnvoiAndroid[i].isAlive())
							{
								new Tempo(10);
							}
						}
						if(this.Out_CommAndroid != null)
						{
							this.Out_CommAndroid.close();
							this.Out_CommAndroid = null;
						}
						if(this.In_CommAndroid != null)
						{
							this.In_CommAndroid.close();
							this.In_CommAndroid = null;
						}
						if(this.socketTCP_CommAndroid != null && !this.socketTCP_CommAndroid.isClosed())
						{
							this.socketTCP_CommAndroid.close();
							this.socketTCP_CommAndroid = null;
						}
						this.isConnected = false;
				}
			}
				new Tempo(1);
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Fin du Thread Comm App Android");
		if(this.Out_CommAndroid != null)
		{
			this.Out_CommAndroid.close();
			this.Out_CommAndroid = null;
		}
		if(this.In_CommAndroid != null)
		{
			try {
				this.In_CommAndroid.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.In_CommAndroid = null;
		}
		if(this.socketTCP_CommAndroid != null && !this.socketTCP_CommAndroid.isClosed())
		{
			try {
				this.socketTCP_CommAndroid.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.socketTCP_CommAndroid = null;
		}
		if(this.serverSocket_CommAndroid != null && !this.serverSocket_CommAndroid.isClosed())
		{
			try {
				this.serverSocket_CommAndroid.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.serverSocket_CommAndroid = null;
		}
		System.gc();
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Methode permettant d envoyer un message en TCP
	 * @param Message
	 */
	private void SendMessageTCP(String Message)
	{
		this.Out_CommAndroid.println(Message);
	}
	
	/**
	 * Methode permettant de traiter les requetes du client android
	 * @param data
	 */
	private void TraitementRequete(String data)
	{
		switch(data.split("/")[0])
		{
			case "Takeoff":
				this.monIHM_ControleDrone.setIsTakeOff(true);
				while(this.monIHM_ControleDrone.getIsTakeOff())
				{
					new Tempo(10);
				}
				this.Out_CommAndroid.println("ack_commande");
				break;
			case "Land":
				this.monIHM_ControleDrone.setIsLanding(true);
				while(this.monIHM_ControleDrone.getIsLanding())
				{
					new Tempo(10);
				}
				this.Out_CommAndroid.println("ack_commande");
				break;
			case "Forward":
				this.monIHM_ControleDrone.setIsForward(true);
				while(this.monIHM_ControleDrone.getIsForward())
				{
					new Tempo(10);
				}
				this.Out_CommAndroid.println("ack_commande");
				break;
			case "Backward":
				this.monIHM_ControleDrone.setIsBackward(true);
				while(this.monIHM_ControleDrone.getIsBackward())
				{
					new Tempo(10);
				}
				this.Out_CommAndroid.println("ack_commande");
				break;
			case "Right":
				this.monIHM_ControleDrone.setIsRight(true);
				while(this.monIHM_ControleDrone.getIsRight())
				{
					new Tempo(10);
				}
				this.Out_CommAndroid.println("ack_commande");
				break;
			case "Left":
				this.monIHM_ControleDrone.setIsLeft(true);
				while(this.monIHM_ControleDrone.getIsLeft())
				{
					new Tempo(10);
				}
				this.Out_CommAndroid.println("ack_commande");
				break;
			case "Up":
				this.monIHM_ControleDrone.setIsUp(true);
				while(this.monIHM_ControleDrone.getIsUp())
				{
					new Tempo(10);
				}
				this.Out_CommAndroid.println("ack_commande");
				break;
			case "Down":
				this.monIHM_ControleDrone.setIsDown(true);
				while(this.monIHM_ControleDrone.getIsDown())
				{
					new Tempo(10);
				}
				this.Out_CommAndroid.println("ack_commande");
				break;
			case "Turn":
				GestionCommandesDrones.sendRotationtoDrone(Integer.parseInt(data.split("/")[1]));
				this.Out_CommAndroid.println("ack_commande");
				break;
		}
	}
	
	
	
	
}
