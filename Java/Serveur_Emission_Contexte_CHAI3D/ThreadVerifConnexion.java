import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * @author RIAS Julien
 * @version 1.0
 * @date 09/05/25
 * @modification 10/05/25
 * @description Classe ThreadVerifConnexion de type Thread permettant la verification de la connexion entre le client et le serveur
 *
 */
public class ThreadVerifConnexion extends Thread
{
	private boolean flagEndThreadVerifConnexion = false;
	private ThreadServeurConnexionUDP monThreadServeurConnexionUDP = null;
	private InetAddress AddresseClient = null;
	private DatagramSocket socketVideoUDP = null;
	private int NbNonReponse = 0, PortVerif, PortClient,IndexClient;
	
	public ThreadVerifConnexion(InetAddress _InetAddress, ThreadServeurConnexionUDP _ThreadServeurConnexionUDP, int _PortVerif, int _PortClient, int _index)
	{
		this.AddresseClient = _InetAddress;
		this.monThreadServeurConnexionUDP = _ThreadServeurConnexionUDP;
		this.IndexClient = _index;
		this.PortVerif = _PortVerif+this.IndexClient;
		this.PortClient= _PortClient;
	}
	
	public void run()
	{
		System.out.println("Debut du Thread Verif Connexion pour le client: "+this.AddresseClient.getHostAddress());
		try {
			this.socketVideoUDP = new DatagramSocket(this.PortVerif);
			this.socketVideoUDP.setSoTimeout(1500); //Mis en place d un timeout
			while(!this.flagEndThreadVerifConnexion)
			{
				this.isAccuseReception(this.AddresseClient);
				if(this.NbNonReponse > 15)
				{
					this.monThreadServeurConnexionUDP.DeconnecterClient(this.AddresseClient, this.PortClient);
				}
				new Tempo(1000);
			}
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Fin du Thread Verif Connexion pour le client: "+this.AddresseClient.getHostAddress());
		this.socketVideoUDP.close();
		System.gc();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Methode permettant d arreter le thread
	 */
	public void StopThread()
	{
		this.flagEndThreadVerifConnexion = true;
	}
	
	
	/**
	 * Methode permettant d attendre un accuse de reception
	 * @param ClientAddress
	 * @return
	 */
	private void isAccuseReception(InetAddress ClientAddress)
	{
		try {
			byte[] ByteArrayAccuseReception = new byte[32];
			DatagramPacket packetAccuseReception = new DatagramPacket(ByteArrayAccuseReception,ByteArrayAccuseReception.length);
			this.socketVideoUDP.receive(packetAccuseReception);
			String accuse = new String(packetAccuseReception.getData(),0,packetAccuseReception.getLength());
			if(accuse.equals("accuse_ok") && packetAccuseReception.getAddress().getHostAddress().equals(ClientAddress.getHostAddress()))
			{
				this.NbNonReponse = 0;
			}
			
			//On vide le buffer car celui peut se remplir d accuses et donc fausser la verif
			this.socketVideoUDP.setSoTimeout(5);
			DatagramPacket tmp = new DatagramPacket(new byte[32],32);
			try {
			  while(true) this.socketVideoUDP.receive(tmp);
			} catch(SocketTimeoutException ignored) {}
			this.socketVideoUDP.setSoTimeout(1500);
			
		} catch (SocketTimeoutException e) {
			this.NbNonReponse++;
        }
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
