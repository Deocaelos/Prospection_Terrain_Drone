import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Vector;

/**
 * @author RIAS Julien
 * @version 1.0
 * @date 03/03/25
 * @modification 09/05/25
 * @description Classe ThreadClientFMPPicture de type Thread permettant d accepter les connexions clientes en UDP
 *
 */
public class ThreadServeurConnexionUDP extends Thread
{
	private boolean flagEndThreadServeurConnexionUDP = false;
	private DatagramSocket socketUDPCo;
	private int PortUDPCo = 37864;
	private byte[] bufUDPCo = new byte[256], bufUDPCoConfirm;
	private Vector<InetAddress> AddressClient = new Vector<>();
	private Vector<Integer> PortClient = new Vector<>();
	private Vector<HandlerEnvoiFFMPEG> ListClient = new Vector<>();
	private Vector<ThreadVerifConnexion> ListThreadVerif = new Vector<>();
	
	public ThreadServeurConnexionUDP() {}
	
	public void run()
	{
		System.out.println("Debut du Thread ServeurConnexionUDP");
		try {
			this.socketUDPCo = new DatagramSocket(this.PortUDPCo);
			int IndexClient = 1;
			while(!this.flagEndThreadServeurConnexionUDP)
			{
				DatagramPacket packetServeur = new DatagramPacket(this.bufUDPCo, this.bufUDPCo.length);
				this.socketUDPCo.receive(packetServeur);
				String receive = new String(packetServeur.getData(),0,packetServeur.getLength());
				InetAddress ClientIPAddress = packetServeur.getAddress();
				int PortClient = packetServeur.getPort();
				if(receive.equals("Connexion_UDP_Video") && !this.AddressClient.contains(ClientIPAddress))
				{
					System.out.println("Nouveau Client avec l adresse: "+ClientIPAddress+" et le port: "+PortClient);
					this.bufUDPCoConfirm = ("Connexion_Confirmee/"+Integer.toString(this.PortUDPCo+IndexClient)).getBytes(); //On transmet en plus de la confirmation le port utilise pour la verification
					this.AddressClient.add(ClientIPAddress);
					this.PortClient.add(PortClient);
					packetServeur = new DatagramPacket(this.bufUDPCoConfirm, this.bufUDPCoConfirm.length, ClientIPAddress, PortClient);
					this.socketUDPCo.send(packetServeur);
					ThreadVerifConnexion monThreadVerifConnexion = new ThreadVerifConnexion(ClientIPAddress,this,this.PortUDPCo, PortClient,IndexClient);
					monThreadVerifConnexion.start();
					this.ListThreadVerif.add(monThreadVerifConnexion);
					new Tempo(1500);
					HandlerEnvoiFFMPEG monHandlerEnvoiFFMPEG = new HandlerEnvoiFFMPEG(ClientIPAddress.getHostAddress());
					this.ListClient.add(monHandlerEnvoiFFMPEG);
					IndexClient++;
				}
				else if(receive.equals("Deconnexion_UDP_Video") && this.AddressClient.contains(ClientIPAddress))
				{
					this.DeconnecterClient(ClientIPAddress, PortClient);
				}
				this.bufUDPCo = new byte[256];
				new Tempo(1);
			}
			this.socketUDPCo.close();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Fin du Thread ServeurConnexionUDP");
		System.gc();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/***
	 * Methode permettant la deconnexion/desinscription d un client
	 * @param _ClientAdd
	 * @param _PortClient
	 * @throws IOException
	 */
	public void DeconnecterClient(InetAddress _ClientAdd, int _PortClient) throws IOException
	{
		System.out.println("Client avec l adresse: "+_ClientAdd+" et le port: "+_PortClient+" retire");
		this.bufUDPCoConfirm = "Deconnexion_Confirmee".getBytes();
		this.PortClient.remove(this.AddressClient.indexOf(_ClientAdd));
		this.ListClient.get(this.AddressClient.indexOf(_ClientAdd)).StopStream();
		this.ListClient.remove(this.AddressClient.indexOf(_ClientAdd));
		this.ListThreadVerif.get(this.AddressClient.indexOf(_ClientAdd)).StopThread();
		this.ListThreadVerif.remove(this.AddressClient.indexOf(_ClientAdd));
		this.AddressClient.remove(this.AddressClient.indexOf(_ClientAdd));
		DatagramPacket packetDeco = new DatagramPacket(this.bufUDPCoConfirm, this.bufUDPCoConfirm.length, _ClientAdd, _PortClient);
		this.socketUDPCo.send(packetDeco);
	}
	
	/**
	 * Methode permettant d obtenir la liste des clients connectes
	 * @return
	 */
	public Vector<HandlerEnvoiFFMPEG> getListClient()
	{
		return this.ListClient;
	}
	
	
	
}
