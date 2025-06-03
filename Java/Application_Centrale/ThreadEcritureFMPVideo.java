

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Arrays;

import javax.swing.ImageIcon;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import Threads.ThreadGestionTraitementImage;
import Utils.Tempo;

/**
 * @author RIAS Julien
 * @version 1.0
 * @date 03/04/25
 * @modification 03/04/25
 * @description Thread servant a l ecriture de la video des deux drones sur le fichier de file mapping partage avec l environnement CHAI3D
 *
 */

public class ThreadEcritureFMPVideo extends Thread
{
	private boolean flagEndThreadEcritureFMPVideo = false;
	private cFileMappingPictureServeur moncFileMappingPictureServeur = null;
	private ThreadGestionTraitementImage monThreadGestionImage = null;
	private ImageIcon ImgDrone_1 = null, ImgDrone_2 = null;
	private byte[] monByteArrayImage = null;
	
	public ThreadEcritureFMPVideo(ThreadGestionTraitementImage _ThreadGestionTraitementImage)
	{
		this.monThreadGestionImage = _ThreadGestionTraitementImage;
	}
	
	public void run()
	{
		System.out.println("Debut du Thread Ecriture Video FMP");
		this.moncFileMappingPictureServeur = new cFileMappingPictureServeur(true); // desactive le mode debug du filemapping dans la console
		this.moncFileMappingPictureServeur.OpenServer("Video_Drone"); // cree et ouvre le serveur du filemapping avec le nom Video_Drone
		while(!this.flagEndThreadEcritureFMPVideo)
		{
			//Attente de la disponibilite de l image des differents drones avant le traitement de celle ci
			if(this.monThreadGestionImage.isImageDrone1FinalReady())
			{
				this.ImgDrone_1 = this.monThreadGestionImage.getImgIconFinal_1();
				this.monThreadGestionImage.setImageDrone1FinalReady(false);
			}
			if(this.monThreadGestionImage.isImageDrone2FinalReady())
			{
				this.ImgDrone_2 = this.monThreadGestionImage.getImgIconFinal_2();
				this.monThreadGestionImage.setImageDrone2FinalReady(false);
			}
			if(this.ImgDrone_1 != null && this.ImgDrone_2 != null && !this.moncFileMappingPictureServeur.getVirtualPictureMutexBlocAccess())
			{
				//On bloque l acces au fichier
				this.moncFileMappingPictureServeur.setVirtualPictureMutexBlocAccess(true);
				//On compose une image en collant les deux images recues
				this.monByteArrayImage = this.ComposeImageDrones(this.bufferedImageToMat((BufferedImage)this.ImgDrone_1.getImage()), this.bufferedImageToMat((BufferedImage)this.ImgDrone_2.getImage()));
				//On ecrit cette derniere octet par octet au sein du fichier
				for(int i = 0; i < this.monByteArrayImage.length; i++)
				{
					this.moncFileMappingPictureServeur.setMapFileOneByOneUnsignedChar(i,(short)this.monByteArrayImage[i]);
				}
				//On indique au sein du fichier la taille de l image
				this.moncFileMappingPictureServeur.setVirtualPictureDataSize(this.monByteArrayImage.length);
				//On redonne acces au fichier de memoire partagee
				this.moncFileMappingPictureServeur.setVirtualPictureMutexBlocAccess(false);
				this.ImgDrone_1 = null;
				this.ImgDrone_2 = null;
				new Tempo(5);
			}
			new Tempo(1);
		}
		System.out.println("Fin du Thread Ecriture Video FMP");
		this.moncFileMappingPictureServeur.CloseServer();
		System.gc();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Methode permettant de mettre ajour l image 2 a envoye en fmp
	 * @param icon
	 */
	public void setImage2FMP(ImageIcon icon)
	{
		this.ImgDrone_2 = icon;
	}
	
	/**
	 * Methode permettant de mettre ajour l image 1 a envoye en fmp
	 * @param icon
	 */
	public void setImage1FMP(ImageIcon icon)
	{
		this.ImgDrone_1 = icon;
	}
	
	
	/**
	 * Methode permettant de mettre a jour l etat du booleen flagEndThreadEcritureFMPVideo
	 * @param etat
	 */
	public void setflagEndThreadEcritureFMPVideo(boolean etat)
	{
		this.flagEndThreadEcritureFMPVideo = etat;
	}
	
	
	/**
	 * Methode permettant de composer l image des deux drones
	 * @return byte[] ByteArrayImageComposee
	 */
	private byte[] ComposeImageDrones(Mat Image_Drone_1, Mat Image_Drone_2)
	{
		Mat ImageComposee = new Mat();
		MatOfByte matOfByte = new MatOfByte();
		Core.hconcat(Arrays.asList(Image_Drone_1,Image_Drone_2), ImageComposee);
		Imgcodecs.imencode(".jpg", ImageComposee, matOfByte);
		Imgcodecs.imwrite("merged.jpg", ImageComposee);
		byte[] ByteArrayImageComposee = matOfByte.toArray();
		return(ByteArrayImageComposee);
	}
	
	/**
	 * Methode de conversion d un BufferedImage vers un Mat
	 */
	private Mat bufferedImageToMat(BufferedImage bi) 
	{
		Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		  byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		  mat.put(0, 0, data);
		  return mat;
	}
}
