import java.util.Vector;

/**
 * @author RIAS Julien
 * @version 1.0
 * @date 03/03/25
 * @modification 05/04/25
 * @description Classe ThreadClientFMPPicture de type Thread permettant de recuperer l image issue de CHAI3D
 *
 */
public class ThreadClientFMPPicture extends Thread
{
	private boolean flagEndThreadClientFMPPicture = false;
	private cFileMappingPictureClient monClientFMPPicture = null;
	private ThreadServeurConnexionUDP monThreadServeurConnexionUDP = null;
	private byte[] monByteArrayPictureCHAI = null;
	private Vector<HandlerEnvoiFFMPEG> ClientList = new Vector<>();
	
	public ThreadClientFMPPicture(ThreadServeurConnexionUDP _ThreadServeurConnexionUDP) 
	{
		this.monThreadServeurConnexionUDP = _ThreadServeurConnexionUDP;
	}
	
	public void run()
	{
		System.out.println("Debut du Thread ClientFMPPicture");
		this.monClientFMPPicture = new cFileMappingPictureClient(false);
		this.monClientFMPPicture.OpenClient("Video_Drone_CHAI3D");
		new Tempo(5000);
		while (!this.flagEndThreadClientFMPPicture)
		{	
			if(!this.monClientFMPPicture.getVirtualPictureMutexBlocAccess())
			{
				this.ClientList = this.monThreadServeurConnexionUDP.getListClient();
		
				this.monClientFMPPicture.setVirtualPictureMutexBlocAccess(true);
				
				if(this.monByteArrayPictureCHAI != null)
				{
					this.monByteArrayPictureCHAI = null;
				}
				this.monByteArrayPictureCHAI = new byte[this.monClientFMPPicture.getVirtualPictureDataSize()];
				for (int i = 0; i < this.monByteArrayPictureCHAI.length; i++)
				{
					this.monByteArrayPictureCHAI[i] = (byte) this.monClientFMPPicture.getMapFileOneByOneUnsignedChar(i);
				}
				this.monClientFMPPicture.setVirtualPictureMutexBlocAccess(false);
				//On envoie le byte array a tous les Threads Clients
				for(int i = 0; i < this.ClientList.size(); i++)
				{
					this.ClientList.get(i).SendImage(this.monByteArrayPictureCHAI);
				}
				new Tempo(5);
			}
			new Tempo(1);
		}
		this.monClientFMPPicture.CloseClient();
		System.gc();
		System.out.println("Fin du Thread ClientFMPPicture");
	}
	
}
