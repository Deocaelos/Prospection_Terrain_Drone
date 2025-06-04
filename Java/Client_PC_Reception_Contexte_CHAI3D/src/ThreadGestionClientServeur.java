import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.ImageIcon;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameGrabber.Exception;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

/**
 * @author RIAS Julien
 * @version 1.0
 * @date 04/03/25
 * @modification 09/05/25
 * @description Classe ThreadGestionClientServeur de type Thread permettant la gestion des discussions entre le client et le serveur
 *
 */
public class ThreadGestionClientServeur extends Thread
{
	private IHM_ConnexionUDP monIHMConnexion = null;
	private int PortServeurConnexion = 37864, PortServeurVideo= 12513, PortAccuse;
	private DatagramSocket socketUDPClient = null;
	private DatagramPacket packetClient = null;
	private byte[] bufUDPCoConfirm = new byte[256], bufUDPCo;
	private boolean flagEndThreadGestionClientServeur = false, isConnected = false;
	private InetAddress AddressServeur = null;
	private IHM_ClientUDP monIHM_ClientUDP;
	private FFmpegFrameGrabber grabberStream = null;
	private Frame StreamFrame;
	private Java2DFrameConverter converter = null;
	
	public ThreadGestionClientServeur(IHM_ConnexionUDP _IHM_ConnexionUDP)
	{
		this.monIHMConnexion = _IHM_ConnexionUDP;
	}
	
	public void run()
	{
		System.out.println("Debut Thread Gestion Client Serveur");
		try {
			this.socketUDPClient = new DatagramSocket();
			
			while(!this.flagEndThreadGestionClientServeur)
			{
				if(this.monIHMConnexion.getisConnection())
				{
					System.out.println("Connexion au Serveur en cours .....");
					this.bufUDPCo = "Connexion_UDP_Video".getBytes();
					this.AddressServeur = InetAddress.getByName(this.monIHMConnexion.getIPServeur());
					this.packetClient = new DatagramPacket(this.bufUDPCo,this.bufUDPCo.length, this.AddressServeur,this.PortServeurConnexion);
					this.socketUDPClient.send(this.packetClient);
					this.packetClient = new DatagramPacket(this.bufUDPCoConfirm,this.bufUDPCoConfirm.length);
					this.socketUDPClient.receive(this.packetClient);
					String[] receive = new String(this.packetClient.getData(),0,this.packetClient.getLength()).split("/");
					InetAddress ServeurIPAddress =this.packetClient.getAddress();
					if(ServeurIPAddress.getHostAddress().equals(this.monIHMConnexion.getIPServeur()) && receive[0].equals("Connexion_Confirmee"))
					{
						System.out.println("Connexion Reussie avec le serveur");
						this.monIHMConnexion.Quitter();
						this.grabberStream = new FFmpegFrameGrabber("udp://0.0.0.0:"+this.PortServeurVideo);
						this.grabberStream.setFormat("mpegts");
						this.grabberStream.start();
						this.converter = new Java2DFrameConverter();  
						this.PortAccuse = Integer.parseInt(receive[1]);
						this.isConnected = true;
					}
					else
					{
						System.out.println("Connexion Echouee avec le serveur");
						this.monIHMConnexion.setUIConnexion(true);
						this.isConnected = false;
					}
					this.monIHMConnexion.setConnection(false);
				}
				if(this.isConnected)
				{
					if((this.StreamFrame = this.grabberStream.grab()) != null) //On recupere une frame et on regarde si elle n est pas nulle
					{
						if(this.monIHM_ClientUDP == null)
						{
							this.monIHM_ClientUDP = new IHM_ClientUDP((double)this.StreamFrame.imageWidth/this.StreamFrame.imageHeight, this);
						}
						this.monIHM_ClientUDP.setVideoCHAI(new ImageIcon(this.getScaledImage(this.converter.convert(this.StreamFrame), this.monIHM_ClientUDP.getWidthLblCHAI(), this.monIHM_ClientUDP.getHeightLblCHAI())));
						this.sendAccuseReception();
					}
				}

				new Tempo(1);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!this.socketUDPClient.isClosed())
		{
			this.socketUDPClient.close();
		}
		this.monIHM_ClientUDP.StopIHM();
		if(!this.grabberStream.isCloseInputStream())
		{
			try {
				this.grabberStream.stop();
				this.grabberStream.release();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Fin Thread Gestion Client Serveur");
		System.gc();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	
	public void StopThread()
	{
		System.out.println("Test");
		this.socketUDPClient.close();
		this.flagEndThreadGestionClientServeur = true;
		this.interrupt();
		try {
			this.grabberStream.stop();
			this.grabberStream.release();
		} catch (Exception e) {
		}
		
	}
	
	
	/**
	 * Methode permettant le rescaled une Image
	 * @param srcImg
	 * @param _w
	 * @param _h
	 * @return resizedImg
	 */
	private Image getScaledImage(Image srcImg, int _w, int _h)
	{
	    BufferedImage resizedImg = new BufferedImage(_w, _h, BufferedImage.TYPE_3BYTE_BGR);
	    Graphics2D g2 = resizedImg.createGraphics();
	
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, _w, _h, null);
	    g2.dispose();
	
	    return resizedImg;
	}
	
	/**
	 * Methode envoyant les accuses de reception
	 */
	private void sendAccuseReception()
	{
		byte[] byte_accuse = "accuse_ok".getBytes();
		DatagramPacket packetAccuse = new DatagramPacket(byte_accuse,byte_accuse.length, this.AddressServeur, this.PortAccuse);
		try {
			this.socketUDPClient.send(packetAccuse);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
