package com.xpfriend.tydrone.telloio;

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2RGB;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.xpfriend.tydrone.core.Info;
import com.xpfriend.tydrone.core.Startable;
import com.xpfriend.tydrone.core.VideoFrame;

import Interfaces.IHM_ControleDrone;
import Threads.ThreadGestionTraitementImage;


public class FFmpegVideoReceiver extends Startable 
{
    private final OpenCVFrameConverter.ToMat matConverter = new OpenCVFrameConverter.ToMat();
    private Java2DFrameConverter ByteDecoConverter = null;
    private ThreadGestionTraitementImage monThreadGestionTraitementImage = null;
    private ImageIcon ImgIcon;    
    private int Indice_Drone = 0;
    
    public FFmpegVideoReceiver(int _Indice, ThreadGestionTraitementImage _ThreadGestionTraitementImage)
    {
    	this.Indice_Drone = _Indice;
    	this.monThreadGestionTraitementImage = _ThreadGestionTraitementImage;
    }

    @Override
    public void start(Info info) throws Exception {
        super.start(info);

        //avutil.setLogCallback(new FFmpegAndroidLogCallback());
        this.ByteDecoConverter = new Java2DFrameConverter();
        try (FFmpegFrameGrabber fg = new FFmpegFrameGrabber("udp://@0.0.0.0:1111"+(1+this.Indice_Drone)+"?overrun_nonfatal=1")) {
        	
            fg.start();
            int ignoreCount = 100;
            while (info.isActive()) {
                try {
                    Frame frame = fg.grabImage();
                    if (frame == null) 
                    {
                        continue;
                    }
                    else
                    {
                    	BufferedImage _BufferedImage = this.ByteDecoConverter.convert(frame);
                    	this.ImgIcon = new ImageIcon(_BufferedImage);
                		if(this.Indice_Drone == 0 && !this.monThreadGestionTraitementImage.getImageReady1())
                    	{
                    		this.monThreadGestionTraitementImage.setImgIcon1(this.ImgIcon);
                    		this.monThreadGestionTraitementImage.setImageReady1(true);
                    	} else if(this.Indice_Drone == 1 && !this.monThreadGestionTraitementImage.getImageReady2())
                    	{
                    		this.monThreadGestionTraitementImage.setImgIcon2(this.ImgIcon);
                    		this.monThreadGestionTraitementImage.setImageReady2(true);
                    	}
                  
                    }
                    if (ignoreCount > 0) {
                        ignoreCount--;
                        continue;
                    }

                    info.setFrame(new VideoFrame<>(frame.clone()));

                    if (!info.hasImage()) {
                        info.setImage(toByteArray(frame));
                    }
                } catch (Exception e) {
                    loge(e);
                }
            }

            fg.stop();
            fg.release();
            
        }
        logi("done");
        
    }

    private byte[] toByteArray(Frame frame) {
        if (frame == null) {
            return null;
        }

        Mat img = matConverter.convertToMat(frame);
        Mat mat = new Mat(img.rows(), img.cols(), img.type());
        cvtColor(img, mat, COLOR_BGR2RGB);
        byte[] array = new byte[mat.channels() * mat.cols() * mat.rows()];
        mat.data().get(array);
        return array;
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

}
