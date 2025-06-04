import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * @author RIAS Julien
 * @version 1.0
 * @date 04/03/25
 * @modification 07/04/25
 * @description IHM de Connexion au Serveur UDP 
 *
 */
public class IHM_ClientUDP 
{
	private JFrame frame;
	private JLabel lblVideoCHAI;
	private boolean isConnection = false, isQuitter = false;
	private double FacteurTaille = 0;
	private ThreadGestionClientServeur monThreadGestionClientServeur = null;
	
	/**
	 * Creation de la fenetre graphique de l'application.
	 */
	public IHM_ClientUDP(double facteur, ThreadGestionClientServeur _ThreadGestionClientServeur) 
	{
		this.monThreadGestionClientServeur = _ThreadGestionClientServeur;
		this.FacteurTaille = facteur;
		initialize();
	}
	
	private void initialize()
	{
		Dimension WindowSize = Toolkit.getDefaultToolkit().getScreenSize();
        int Window_width = WindowSize.width;
        int Window_height = WindowSize.height;
        
        this.frame = new JFrame("Client UDP");
        this.frame.setResizable(false);
        this.frame.setBounds(50,50,this.ConvertDoubleToInt(0.6*Window_height*this.FacteurTaille), this.ConvertDoubleToInt(0.6*Window_height));
        this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.frame.getContentPane().setLayout(new BorderLayout());
		
		// Ajoute un listener pour intercepter la fermeture
        this.frame.addWindowListener(new WindowAdapter() 
        {
            @Override
            public void windowClosing(WindowEvent e) 
            {
                // Ta commande ici
                System.out.println("Fermeture detectee !");
                monThreadGestionClientServeur.StopThread();
            }
        });
		
		this.lblVideoCHAI = new JLabel("");
		this.frame.getContentPane().add(this.lblVideoCHAI, BorderLayout.CENTER);
       
		this.frame.setVisible(true);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void StopIHM()
	{
		this.frame.dispose();
	}
	
	/**
	 * Methode permettant de convertir un double en int
	 * @param value
	 * @return
	 */
	private int ConvertDoubleToInt(double value)
	{
		return (int) Math.round(value);
	}
	
	/**
	 * Methode pour obtenir la largeur du lblVideoCHAI
	 * @return Width
	 */
	public int getWidthLblCHAI()
	{
		return this.lblVideoCHAI.getWidth();
	}
	
	/**
	 * Methode pour obtenir la hauteur du lblVideoCHAI
	 * @return Height
	 */
	public int getHeightLblCHAI()
	{
		return this.lblVideoCHAI.getHeight();
	}
	
	/**
	 * Methode permettant de mettre a jour l image affichee sur le label
	 * @param icon
	 */
	public void setVideoCHAI(ImageIcon icon)
	{
		this.lblVideoCHAI.setIcon(icon);
		this.lblVideoCHAI.repaint();
		this.lblVideoCHAI.revalidate();
	}
	
	/**
	 * @return the isConnection
	 */
	public boolean getisConnection() 
	{
		return this.isConnection;
	}

	/**
	 * @param isConnection the isConnection to set
	 */
	public void setConnection(boolean etat) 
	{
		this.isConnection = etat;
	}

	/**
	 * @return the isQuitter
	 */
	public boolean isQuitter() 
	{
		return this.isQuitter;
	}
}
