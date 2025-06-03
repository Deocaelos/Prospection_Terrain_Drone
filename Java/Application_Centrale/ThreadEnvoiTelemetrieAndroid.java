import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import Utils.Tempo;

/**
 * @author RIAS Julien
 * @version 1.0
 * @date 14/05/25
 * @modification 14/05/25
 * @description Classe ThreadEnvoiTelemetrieAndroid de type Thread permettant l envoi de la telemetrie a l application Android
 *
 */
public class ThreadEnvoiTelemetrieAndroid extends Thread
{
	private boolean flagEndThreadEnvoiTelemetrieAndroid = false;
	
	private cFileMappingTelloEduTelemetryClient monClientFMPTelemetry = null;
	
	private DatagramSocket socket_CommAndroid = null;
	
	private DatagramPacket packet_CommAndroid = null;
	
	private int Index;
	
	private InetAddress AddressClient;
	
	private String Trame_Telemetrie;
	
	
	public ThreadEnvoiTelemetrieAndroid(int _Index, InetAddress _AddressClient)
	{
		this.Index = _Index;
		this.AddressClient = _AddressClient;
	}
	
	public void run()
	{
		System.out.println("Debut du Thread Envoi Telemetrie Android");
		this.monClientFMPTelemetry = new cFileMappingTelloEduTelemetryClient(false);
		this.monClientFMPTelemetry.OpenClient("TelemetrieDrone"+this.Index);
		try {
			//Declaration du socket UDP de communication avec l appareil android
			this.socket_CommAndroid = new DatagramSocket();
			while(!this.flagEndThreadEnvoiTelemetrieAndroid)
			{
				if(!this.monClientFMPTelemetry.getVirtualTelloEduTelemetryMutexBlocAccess())
				{
					//On interdit l acces au fichier de memoire partagee pour la telemetrie
					this.monClientFMPTelemetry.setVirtualTelloEduTelemetryMutexBlocAccess(true);
					//On compose la trame avec les differentes donnees stockees dans le fichier de memoire partagee
					this.Trame_Telemetrie = 
							"ax:"+this.monClientFMPTelemetry.getVirtualTelloEduTelemetryAgx()
							+";ay:"+this.monClientFMPTelemetry.getVirtualTelloEduTelemetryAgy()
							+";az:"+this.monClientFMPTelemetry.getVirtualTelloEduTelemetryAgz()
							+";vx:"+this.monClientFMPTelemetry.getVirtualTelloEduTelemetryVgx()
							+";vy:"+this.monClientFMPTelemetry.getVirtualTelloEduTelemetryVgy()
							+";vz:"+this.monClientFMPTelemetry.getVirtualTelloEduTelemetryVgz()
							+";h:"+this.monClientFMPTelemetry.getVirtualTelloEduTelemetryH()
							+";time:"+this.monClientFMPTelemetry.getVirtualTelloEduTelemetryFlyTime()
							+";pitch:"+this.monClientFMPTelemetry.getVirtualTelloEduTelemetryPitch()
							+";roll:"+this.monClientFMPTelemetry.getVirtualTelloEduTelemetryRoll()
							+";yaw:"+this.monClientFMPTelemetry.getVirtualTelloEduTelemetryYaw()
							+";bat:"+this.monClientFMPTelemetry.getVirtualTelloEduTelemetryBatteryValue();
					//On autorise l acces au fichier de memoire partagee pour la telemetrie
					this.monClientFMPTelemetry.setVirtualTelloEduTelemetryMutexBlocAccess(false);
					//On compose le paquet puis on l envoit au client
					this.packet_CommAndroid = new DatagramPacket(this.Trame_Telemetrie.getBytes(),this.Trame_Telemetrie.getBytes().length,this.AddressClient,8890+this.Index);
					this.socket_CommAndroid.send(this.packet_CommAndroid);
				}
				
				new Tempo(10);
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Fin du Thread Envoi Telemetrie Android");
		this.socket_CommAndroid.close();
		this.monClientFMPTelemetry.CloseClient();
		System.gc();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void StopThread()
	{
		this.flagEndThreadEnvoiTelemetrieAndroid = true;
	}
	
}
