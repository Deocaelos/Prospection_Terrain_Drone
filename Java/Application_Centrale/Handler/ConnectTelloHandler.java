package Handler;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.xpfriend.tydrone.SimpleMain;

import Interfaces.IHM_ControleDrone;
import Threads.ThreadGestionTraitementImage;
import Utils.Tempo;

public class ConnectTelloHandler 
{

	private static int NBDrone = 2;
	private static SimpleMain[] ListDrones;
	private static String[] ReseauxDrones, InterfacesWlan;
	private static String RaisonFailed = "";
	private static boolean ConnectionFinished = false, ConnectionFailed = false;
	
	public ConnectTelloHandler(int _NBDrone, String[] _ReseauxDrones)
	{
		ConnectTelloHandler.NBDrone = _NBDrone;
		ConnectTelloHandler.ReseauxDrones = _ReseauxDrones;
		ConnectTelloHandler.ListDrones= new SimpleMain[ConnectTelloHandler.NBDrone];
		ConnectTelloHandler.Start();
	}
	
	public void Start_OnlyReset()
	{
		System.out.println("Debut du Handler Reset Interface");
		ConnectTelloHandler.InterfacesWlan = ConnectTelloHandler.getInterfaceWLAN();
		ConnectTelloHandler.ResetFullWinsock();
		System.out.println("Debut du Handler Reset Interface");
	}
	
	private static void Start()
	{
		System.out.println("Debut du Handler Connexion Tello");
		
		ConnectTelloHandler.InterfacesWlan = ConnectTelloHandler.getInterfaceWLAN();
		
		//On compte le nombre d interface
		int count = 0;
        for (String str : ConnectTelloHandler.InterfacesWlan) 
        {
            if (str != null) 
            {
                count++;
            }
        }
        if(count != ConnectTelloHandler.NBDrone)
        {
        	System.out.println("Nombre d interface WLAN correcte insuffisant");
        	ConnectTelloHandler.RaisonFailed = "Nombre d interface WLAN correcte insuffisant";
        	ConnectTelloHandler.ConnectionFailed = true;
        }
        else
        {
        	for(int i = 0; i<ConnectTelloHandler.NBDrone;i++)
        	{
        		//Configuration des interfaces avec leur MASK, IP Fixe, et Gateway
        		//Reset des Interfaces
        		do
        		{
        			ConnectTelloHandler.ConnectionFailed = false;
        			ConnectTelloHandler.ResetInterface(ConnectTelloHandler.InterfacesWlan[i]);
        			new Tempo(2000);
        			ConnectTelloHandler.ConfigCarte(ConnectTelloHandler.InterfacesWlan[i], "192.168.10."+Integer.toString(2+i), "255.255.255.0", "192.168.10.1");
            		new Tempo(1000);
        		}
        		while(ConnectTelloHandler.ConnectionFailed);
        	}
        	if(ConnectTelloHandler.ConnectionFailed)
        	{
        		System.err.println("Configuration des interfaces impossibles");
        		ConnectTelloHandler.RaisonFailed = "Configuration des interfaces impossibles";
        	}
        	else
        	{
        		System.out.println("Interfaces configurees avec succes");
        	}
        }
        System.out.println("Fin du Handler Connexion Tello");
    }
	
	public void goConnect(IHM_ControleDrone _IHM_Drone, ThreadGestionTraitementImage _ThreadGestionTraitementImage)
	{
		//Ajout des profils reseaux wlan
    	ConnectTelloHandler.setWLANProfile();
    	if(!ConnectTelloHandler.ConnectionFailed)
    	{
    		new Tempo(1500);
        	
        	//Connexion des Interfaces aux reseaux des drones
        	for(int i = 0;i<ConnectTelloHandler.NBDrone;i++)
        	{
        		ConnectTelloHandler.ConnectToWifi(ConnectTelloHandler.InterfacesWlan[i], ConnectTelloHandler.ReseauxDrones[i]);
        		
        		try {
        			
        			//On attend d avoir un ping positif avant de continuer
					while(!InetAddress.getByName("192.168.10.1").isReachable(NetworkInterface.getByInetAddress(InetAddress.getByName("192.168.10."+(2+i))),10,10))
					{
						new Tempo(500);
					}
					
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
        		if(ConnectTelloHandler.ConnectionFailed)
        		{
        			System.err.println("Connection au reseau "+ConnectTelloHandler.ReseauxDrones[i]+" non reussie");
        			ConnectTelloHandler.RaisonFailed = "Connection au reseau "+ConnectTelloHandler.ReseauxDrones[i]+" non reussie";
        			break;
        		}
        	}
        	if(!ConnectTelloHandler.ConnectionFailed)
        	{
        		System.out.println("Succes de la connexion aux Reseaux des Drones");
            	
            	new Tempo(5000);
            	
            	System.out.println("Connexion au drones");
            	for(int i =0; i<ConnectTelloHandler.NBDrone;i++)
            	{
            		try {
            			ConnectTelloHandler.ListDrones[i] = new SimpleMain("192.168.10."+(2+i),i,_IHM_Drone,_ThreadGestionTraitementImage);

        				
        				
            			new Tempo(1500);
    					ConnectTelloHandler.ListDrones[i].run();
    					new Tempo(500);
    				} catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
            	}
            	ConnectTelloHandler.ConnectionFinished = true;
        	}
    	}
	}
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////Methodes///////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////
		
	/**
	 * Methode permettant de reset les configurations d une interface
	 * @param interface_name
	 */
	private static void ResetInterface(String interface_name)
	{
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "netsh interface set interface \"" + interface_name + "\" admin=disable");
	        processBuilder.inheritIO(); // Pour afficher la sortie de la commande dans la console
	        Process process = processBuilder.start();
			process.waitFor(); // Attendre la fin de l execution
			processBuilder = new ProcessBuilder("cmd.exe", "/c", "netsh interface set interface \"" + interface_name + "\" admin=enable");
	        processBuilder.inheritIO(); // Pour afficher la sortie de la commande dans la console
			process = processBuilder.start();
			int exitCode = process.waitFor(); // Attendre la fin de l execution
			if(exitCode == 0)
            {
            	  System.out.println("Reset de l interface: "+interface_name+" reussie avec le code: " + exitCode);
            	  ConnectTelloHandler.ConnectionFailed = false;
            }
            else
            {
            	System.err.println("Reset de l interface: "+interface_name+" non reussie avec le code: " + exitCode);
            	ConnectTelloHandler.ConnectionFailed = true;
            }
			new Tempo(5000);
			//Passage en mode DHCP des interfaces
	        processBuilder = new ProcessBuilder("cmd.exe", "/c","netsh interface ip set address name=\"" + interface_name + "\" source=dhcp");
	        processBuilder.inheritIO(); // Pour afficher la sortie de la commande dans la console
			process = processBuilder.start();
			processBuilder = new ProcessBuilder("cmd.exe", "/c","netsh interface ip set dnsservers name=\"" + interface_name + "\" source=dhcp");
	        processBuilder.inheritIO(); // Pour afficher la sortie de la commande dans la console
			process = processBuilder.start();
			exitCode = process.waitFor();
			if(exitCode == 0)
            {
				System.out.println("Reset du DHCP de l interface: "+interface_name+" reussie avec le code: " + exitCode);
				ConnectTelloHandler.ConnectionFailed = false;
            }
            else
            {
            	System.err.println("Reset du DHCP l interface: "+interface_name+" non reussie avec le code: " + exitCode);
            	ConnectTelloHandler.ConnectionFailed = true;
            }
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * Methode permettant de connecter une interface wlan a un reseau
	 * @param Interface
	 * @param SSID
	 */
	private static void ConnectToWifi(String Interface, String SSID)
	{
		try {
			String command = "netsh wlan connect name="+SSID+" interface=\""+Interface+"\"";
			ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c",command);
			processBuilder.inheritIO(); // Pour afficher la sortie de la commande dans la console
			Process process = processBuilder.start();
			int exitCode = process.waitFor();
			if(exitCode != 0)
			{
				ConnectTelloHandler.ConnectionFailed = true;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		} // Attendre la fin de l'execution de la commande
		 catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	/**
	 * Methode permettant d ajouter les profils reseaux des drones
	 */
	private static void setWLANProfile()
	{
		try {
			String[] profilPath = {"ressources/ProfilDrone_9BFF2B.xml","ressources/ProfilDrone_9C003E.xml","ressources/ProfilDrone_60AA12.xml","ressources/ProfilDrone_9C003F.xml","ressources/ProfilDrone_9BFFC5.xml"};
			
			ProcessBuilder processBuilder;
			for(int i =0; i<profilPath.length;i++)
			{
				processBuilder = new ProcessBuilder("cmd.exe", "/c", "netsh wlan add profile filename="+profilPath[i]);
				processBuilder.inheritIO(); // Pour afficher la sortie de la commande dans la console
				Process process;
				process = processBuilder.start();
				int exitCode = process.waitFor(); // Attendre la fin de l execution de la commande
				if(exitCode != 0)
				{
					System.err.println("Erreur dans la mise a jour des profils reseaux");
					ConnectTelloHandler.ConnectionFailed = true;
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Methode permettant le trie dans les interfaces wlan pour ne recuperer que celle avec un adaptateur usb
	 * @return
	 */
	private static String[] getInterfaceWLAN()
	{
		String[] InterfacesUSBWLAN = new String[ConnectTelloHandler.NBDrone];
		ProcessBuilder processBuilder = new ProcessBuilder("netsh", "wlan", "show", "interfaces");
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            String InterfaceName = null;
            int Index = 0;
            while ((line = reader.readLine()) != null && Index < ConnectTelloHandler.NBDrone) 
            {
            	//On lit la ligne Nom
            	if(line.trim().startsWith("Nom") || line.trim().startsWith("Name"))
            	{
            		InterfaceName = line.trim().split(":")[1].trim(); //Trim() permet d enlever les espaces residuels
            	}
            	//On lit la ligne description
                if (line.trim().startsWith("Description") && (line.trim().contains("USB") || line.trim().contains("Card"))) 
                {
                	System.out.println("USB detecte pour l interface: "+InterfaceName);
                	InterfacesUSBWLAN[Index] = InterfaceName;
                	Index++;
                }
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return InterfacesUSBWLAN;
	}
	
	// Configuration des interfaces
	private static void ConfigCarte(String interfaceName, String ip, String mask, String gateway)
	{
		try {
			new Tempo(1500);
			ProcessBuilder pb = new ProcessBuilder(
	                "netsh", "interface", "ip", "set", "address"
	                ,interfaceName, "static", ip, mask, gateway
	        );
			pb.redirectErrorStream(false);
			Process process = pb.start();
            int exitCode = process.waitFor();
            if(exitCode == 0)
            {
            	  System.out.println("Configuration de l interface: "+interfaceName+" reussie avec le code: " + exitCode);
            	  ConnectTelloHandler.ConnectionFailed = false;
            }
            else
            {
            	 System.err.println("Configuration de l interface: "+interfaceName+" non reussie avec le code: " + exitCode);
            	ConnectTelloHandler.ConnectionFailed = true;
            }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * Methode permettant le reset final des interfaces
	 */
	private static void ResetFullWinsock()
    {
        ProcessBuilder processBuilder  = null;
        Process process = null;
       
        System.out.println("Reset winsock en cours...");
        processBuilder = new ProcessBuilder("cmd.exe", "/c", "netsh winsock reset");
        processBuilder.inheritIO(); // Pour afficher la sortie de la commande dans la console
        // Rediriger la sortie d'erreur vers la sortie standard
        processBuilder.redirectErrorStream(true);
        try {
            process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {}
 
        try {
            System.out.println("Release interfaces en cours...");
            processBuilder = new ProcessBuilder("cmd.exe", "/c", "ipconfig /release");
            processBuilder.inheritIO(); // Pour afficher la sortie de la commande dans la console
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {}
 
        try {
            System.out.println("Reset Ip en cours...");
            processBuilder = new ProcessBuilder("cmd.exe", "/c", "netsh int ip reset");
            processBuilder.inheritIO(); // Pour afficher la sortie de la commande dans la console
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {}
   
        try {
            System.out.println("Flush DNS en cours...");
            processBuilder = new ProcessBuilder("cmd.exe", "/c", "ipconfig /flushdns");
            processBuilder.inheritIO(); // Pour afficher la sortie de la commande dans la console
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {}
       
        ConnectTelloHandler.InterfacesWlan = ConnectTelloHandler.getInterfaceWLAN();
        for(int i=0; i  < ConnectTelloHandler.InterfacesWlan.length; i++)
        {
           ConnectTelloHandler.ResetInterface(ConnectTelloHandler.InterfacesWlan[i]);
           
            String command = "netsh interface ip set address name=" + "\"" + ConnectTelloHandler.InterfacesWlan[i] + "\"" + " dhcp";
            // Utilisation de "runas" pour demander l elevation des privileges
            processBuilder = new ProcessBuilder(
                "cmd.exe", "/c",
                "powershell.exe", "-Command",
                "Start-Process cmd.exe -ArgumentList '/c " + command + "' -Verb RunAs"
            );
            processBuilder.inheritIO(); // Pour afficher la sortie de la commande dans la console
            processBuilder.redirectErrorStream(true);
            // Démarrer le processus
            try {
                process = processBuilder.start();
                process.waitFor();
            } catch (IOException | InterruptedException e) {}
 
        }
    }
	
	/**
	 * Methode permettant d obtenir l acces aux drones
	 * @return
	 */
	public SimpleMain[] getDronesAccess()
	{
		return ConnectTelloHandler.ListDrones;
	}
	

	/**
	 * @return the connectionFinished
	 */
	public boolean isConnectionFinished() 
	{
		return ConnectTelloHandler.ConnectionFinished;
	}
	
	/**
	 * 
	 * @param etat
	 */
	public void setConnectionFailed(boolean etat)
	{
		ConnectTelloHandler.ConnectionFailed = etat;
	}
	
	/**
	 * @return the connectionFailed
	 */
	public boolean isConnectionFailed() 
	{
		return ConnectTelloHandler.ConnectionFailed;
	}

	/**
	 * @return the raisonFailed
	 */
	public String getRaisonFailed() 
	{
		return ConnectTelloHandler.RaisonFailed;
	}

}
