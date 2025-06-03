import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FFmpegFrameRecorder.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;


/**
 * @author RIAS Julien
 * @version 1.0
 * @date 09/05/25
 * @modification 09/05/25
 * @description Classe ThreadEnvoiFFMPEG de type Thread permettant la diffusion du flux video
 *
 */
public class HandlerEnvoiFFMPEG
{
	private final OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
	private FFmpegFrameRecorder Streamer;
	private String IP_Client;
	private int WidthStream = 864, HeightStream = 540;
	
	public HandlerEnvoiFFMPEG(String _IP)
	{
		this.IP_Client = _IP;
		System.out.println("Creation Stream FFMPEG pour client: "+this.IP_Client);
		this.setupFFMPEG();
	}
	
	public void setupFFMPEG()
	{
		this.Streamer = new FFmpegFrameRecorder("udp://"+this.IP_Client+":12513",this.WidthStream,this.HeightStream,0); //Ici on definit un flux vers un URL, d une certaine taille, et sans audio
		this.Streamer.setFormat("mpegts");            // format MPEG-TS pour UDP
		this.Streamer.setVideoCodec(avcodec.AV_CODEC_ID_H264);  // codec video H.264
		this.Streamer.setFrameRate(30);               // Frequence d images
        this.Streamer.setVideoBitrate(2000 * 1000);   // Debit video
        this.Streamer.setPixelFormat(org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P); // Format de pixel YUV420P
        // GOP et keyframes
        this.Streamer.setGopSize(30);                   // un key-frame (IDR) toutes les 30 images

        // Preset x264 pour latence zero
        this.Streamer.setVideoOption("preset", "ultrafast");
        this.Streamer.setVideoOption("tune", "zerolatency");

        // Parametres x264 pour desactiver scenecut
        // et donc ne pas inserer d I-frames hors cadence fixe
        this.Streamer.setVideoOption("x264-params", "scenecut=0");

        // Flags TS pour re-emettre PAT/PMT et SPS/PPS
        this.Streamer.setOption("mpegts_flags", "+resend_headers");

        // Forcer le flush immediat des paquets UDP
        this.Streamer.setOption("flush_packets", "1");
        try {
			this.Streamer.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Methode permettant de stream une frame a partir d un byte[]
	 * @param data
	 */
	public void SendImage(byte[] data)
	{
		// Conversion d'un tableau de bytes en image OpenCV (mat)
		if(data != null && data.length > 0)
		{
			Mat mat = opencv_imgcodecs.imdecode(new Mat(new BytePointer(data)), opencv_imgcodecs.IMREAD_ANYCOLOR);
	        if(this.WidthStream != mat.cols() || this.HeightStream != mat.rows()) //On verifie pour voir si la taille a change
	        {
	        	this.WidthStream = mat.cols();
	        	this.HeightStream = mat.rows();
	        	try {
					this.Streamer.stop();
					this.Streamer.release();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	new Tempo(1000);
	        	this.setupFFMPEG();
	        	new Tempo(1000);
	        }
			try {
				if(!this.Streamer.isCloseOutputStream())
				{
					this.Streamer.record(this.converter.convert(mat));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mat.release();
		}
	}
	
	/**
	 * Methode permettant d arreter le streaming
	 */
	public void StopStream()
	{
		try {
			this.Streamer.stop();
			this.Streamer.release();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.gc();
	}
}
