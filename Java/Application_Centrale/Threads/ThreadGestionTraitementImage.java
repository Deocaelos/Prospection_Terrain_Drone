package Threads;


import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import Interfaces.IHM_ControleDrone;
import Utils.Tempo;

public class ThreadGestionTraitementImage extends Thread
{
	private boolean flagEndThreadGestionTraitementImage = false, isImageDrone1Ready = false, isImageDrone2Ready = false,isImageDrone1FinalReady = false, isImageDrone2FinalReady = false ;
	private IHM_ControleDrone monIHMDrone = null;
	private ImageIcon ImgIcon_1 = null, ImgIcon_2 = null, ImgIconFinal_1 = null, ImgIconFinal_2 = null;
	private Mat _ImageModifieeMat_1 = null, _ImageModifieeMat_2 = null;
	public ThreadGestionTraitementImage(IHM_ControleDrone _IHM_ControleDrone)
	{
		this.monIHMDrone = _IHM_ControleDrone;
	}
	
	public void run()
	{
		System.out.println("Debut du Thread Gestion Traitement Image");
		Image newImage = null;
		while(!this.flagEndThreadGestionTraitementImage)
		{
			if(this.isImageDrone1Ready && this.isImageDrone2Ready)
			{
				
				if(this.monIHMDrone.isFlagDetectionContours() || this.monIHMDrone.isFlagDetectionFormes())
				{
					this._ImageModifieeMat_1 = this.getContoursFormes((BufferedImage) this.ImgIcon_1.getImage());
					this._ImageModifieeMat_2 = this.getContoursFormes((BufferedImage) this.ImgIcon_2.getImage());
					this.ImgIcon_1 = new ImageIcon(this.Mat2BufferedImage(this._ImageModifieeMat_1));
					this.ImgIcon_2 = new ImageIcon(this.Mat2BufferedImage(this._ImageModifieeMat_2));
					this._ImageModifieeMat_1.release();
					this._ImageModifieeMat_2.release();
				}
				newImage = this.getScaledImage(this.ImgIcon_1.getImage(), this.monIHMDrone.getLblVideoWidth(), this.monIHMDrone.getLblVideoHeight());
				//On affiche l image traitee ou non dans l IHM
				this.monIHMDrone.setVideoDrone(new ImageIcon(newImage), 0);
				newImage = this.getScaledImage(this.ImgIcon_2.getImage(), this.monIHMDrone.getLblVideoWidth(), this.monIHMDrone.getLblVideoHeight());
				this.monIHMDrone.setVideoDrone(new ImageIcon(newImage), 1);
				this.ImgIconFinal_1 = this.ImgIcon_1;
				this.ImgIconFinal_2 = this.ImgIcon_2;
				this.isImageDrone1FinalReady = true;
				this.isImageDrone2FinalReady = true;
				this.isImageDrone1Ready = false;
				this.isImageDrone2Ready = false;
			}
			new Tempo(1);
		}
		System.out.println("Fin du Thread Gestion Traitement Image");
		System.gc();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Methode permettant de mettre a jour l etat du booleen flagEndThreadGestionTraitementImage
	 * @param etat
	 */
	public void setflagEndThreadGestionTraitementImage(boolean etat)
	{
		this.flagEndThreadGestionTraitementImage = etat;
	}
	
	/**
	 * Methode permettant de mettre a jour letat du booleen isImageDrone2Ready
	 * @param etat
	 */
	public void setImageReady2(boolean etat)
	{
		this.isImageDrone2Ready = etat;
	}
	
	
	/**
	 * Methode permettant de mettre a jour letat du booleen isImageDrone1Ready
	 * @param etat
	 */
	public void setImageReady1(boolean etat)
	{
		this.isImageDrone1Ready = etat;
	}
	
	
	/**
	 * Methode permettant de retourner l etat du booleen isImageDrone2Ready
	 * @return
	 */
	public boolean getImageReady2()
	{
		return this.isImageDrone2Ready;
	}
	
	/**
	 * Methode permettant de retourner l etat du booleen isImageDrone1Ready
	 * @return
	 */
	public boolean getImageReady1()
	{
		return this.isImageDrone1Ready;
	}
	
	
	/**
	 * Methode permettant de retourner l image recue du drone 2
	 * @return
	 */
	public ImageIcon getImageDrone2()
	{
		return this.ImgIcon_2;
	}
	
	
	/**
	 * Methode permettant de retourner l image recue du drone 1
	 * @return
	 */
	public ImageIcon getImageDrone1()
	{
		return this.ImgIcon_1;
	}
	
	
	/**
	 * @return the imgIconFinal_1
	 */
	public ImageIcon getImgIconFinal_1() {
		return this.ImgIconFinal_1;
	}

	/**
	 * @return the imgIconFinal_2
	 */
	public ImageIcon getImgIconFinal_2() {
		return this.ImgIconFinal_2;
	}

	/**
	 * Methode de conversion d un Mat vers un bufferedImage
	 */
	private BufferedImage Mat2BufferedImage(Mat matrix)
	{        
	    MatOfByte mob=new MatOfByte();
	    Imgcodecs.imencode(".jpg", matrix, mob);
	    byte ba[]=mob.toArray();

	    BufferedImage bi = null;
		try {
			bi = ImageIO.read(new ByteArrayInputStream(ba));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return bi;
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
	 * @param imgIcon the imgIcon_1 to set
	 */
	public void setImgIcon1(ImageIcon imgIcon) 
	{
		this.ImgIcon_1 = imgIcon;
	}
	
	/**
	 * @param imgIcon the imgIcon_2 to set
	 */
	public void setImgIcon2(ImageIcon imgIcon) 
	{
		this.ImgIcon_2 = imgIcon;
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
	
	
	/**
	 * Methode de detection de contours et de formes
	 * @param BufferedImage Image sasn traitement
	 * @return Mat contoure
	 */
	private Mat getContoursFormes(BufferedImage frame) 
	{
        // Conversion du BufferedImage en Mat
        Mat matFrame = this.bufferedImageToMat(frame);
        Mat grayImage = new Mat();
        Mat flouImage = new Mat();
        Mat edges = new Mat();
        Mat dilatedEdges = new Mat();
        Mat edgesRed = Mat.zeros(matFrame.size(), matFrame.type()); // Initialiser une image noire
        Mat combinedImage = new Mat();
    
        // Convertir l image en niveaux de gris
        Imgproc.cvtColor(matFrame, grayImage, Imgproc.COLOR_BGR2GRAY);
    
        // Appliquer un filtre bilateral (lissage tout en preservant les contours)
        Imgproc.GaussianBlur(grayImage, flouImage, new Size(5, 5), 1.5);

        //Imgproc.bilateralFilter(grayImage, flouImage, 9, 75, 75);
    
        // Detection des contours avec l algorithme Canny
        double lowerThreshold = 0;
        double upperThreshold = this.monIHMDrone.getValueSliderContour();
        Imgproc.Canny(flouImage, edges, lowerThreshold, upperThreshold);
    
        // Appliquer la dilatation pour augmenter la taille des contours
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)); // Taille 3x3
        Imgproc.dilate(edges, dilatedEdges, kernel);
    
        if(this.monIHMDrone.isFlagDetectionFormes())
        {
        	// Trouver les contours
        	List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(dilatedEdges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            contours.removeIf(contour -> Imgproc.contourArea(contour) < this.monIHMDrone.getValueSliderTailleForme()); //Filtrer les petits contours
            for (MatOfPoint contour : contours) 
            {
            		// Approximation des contours pour simplifier la forme
                    MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
                    double perimeter = Imgproc.arcLength(contour2f, true);
                    MatOfPoint2f approx = new MatOfPoint2f();
                    Imgproc.approxPolyDP(contour2f, approx, 0.05 * perimeter, true);

                    // Identifier la forme selon le nombre de sommets
                    int vertexCount = approx.toArray().length;
                    String shapeType = this.identifyShape(vertexCount);
                    if(!shapeType.equals("Inconnu"))
                    {
                    	// Dessiner le contour et afficher la forme detectee
                        Imgproc.drawContours(matFrame, List.of(contour), -1, new Scalar(0, 255, 0), 1);
                        Point textPoint = approx.toArray()[0];
                        Imgproc.putText(matFrame, shapeType, textPoint, Imgproc.FONT_HERSHEY_SIMPLEX, 0.8, new Scalar(255, 0, 0), 2);
                    }
            }
            hierarchy.release();
        }
        
        if(this.monIHMDrone.isFlagDetectionContours())
        {
        	// Appliquer la couleur rouge (vectoris�)
            edgesRed.setTo(new Scalar(0, 0, 255), dilatedEdges); // Affecter rouge la ou les contours sont presents
        	// Superposer les contours rouges sur l image d origine
            Core.add(matFrame, edgesRed, combinedImage);
        }
        else
        {
        	Core.add(matFrame, edgesRed, combinedImage);
        }

        
        // Liberer les ressources inutilisees (libere explicitement la memoire des objets temporaires)
        grayImage.release();
        flouImage.release();
        edges.release();
        dilatedEdges.release();
        edgesRed.release();

        return combinedImage; // Retourner l'image combinee
    }
	
	
	/** 
	 * Fonction pour identifier la forme en fonction du nombre de sommets
	 * @param vertexCount
	 * @return
	 */
	private String identifyShape(int vertexCount) 
	{
	    switch (vertexCount) 
	    {
	        case 3:
	            return "Triangle";
	        case 4:
	            return "Rectangle ou Carre";
	        case 5:
	            return "Pentagone";
	        case 6:
	            return "Hexagone";
	        case 7:
	            return "Heptagone";
	        case 8:
	            return "Octogone";
	        default:
	            return (vertexCount > 8) ? "Cercle" : "Inconnu";
	    }
	}

	/**
	 * @return the isImageDrone1FinalReady
	 */
	public boolean isImageDrone1FinalReady() {
		return this.isImageDrone1FinalReady;
	}

	/**
	 * @param isImageDrone1FinalReady the isImageDrone1FinalReady to set
	 */
	public void setImageDrone1FinalReady(boolean isImageDrone1FinalReady) {
		this.isImageDrone1FinalReady = isImageDrone1FinalReady;
	}

	/**
	 * @return the isImageDrone2FinalReady
	 */
	public boolean isImageDrone2FinalReady() {
		return this.isImageDrone2FinalReady;
	}

	/**
	 * @param isImageDrone2FinalReady the isImageDrone2FinalReady to set
	 */
	public void setImageDrone2FinalReady(boolean isImageDrone2FinalReady) {
		this.isImageDrone2FinalReady = isImageDrone2FinalReady;
	}
}
