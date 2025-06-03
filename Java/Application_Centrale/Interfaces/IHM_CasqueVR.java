package Interfaces;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

/**
 * @author RIAS Julien
 * @version 1.0
 * @date 12/05/25
 * @modification 12/05/25
 * @description IHM de controle des drones via le casque de realite virutelle
 *
 */
public class IHM_CasqueVR 
{
	private JFrame frame;
	private JButton btnRetour;
	private JSlider slider_Y,slider_X,slider_Z,slider_Rota;
	private JLabel lblAvant,lblReculer,lblDroite,lblGauche,lblMonter,lblDescendre,lblRotaDroite, lblRotaGauche;
	private boolean isRetour = false;
	
	public IHM_CasqueVR()
	{
		this.initialize();
		this.ResetColorDirection(); 
	}
	
	private void initialize()
	{
		this.frame =  new JFrame("Casque VR");
		this.frame.setBounds(100, 0, 687, 298);
		this.frame.setResizable(false);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.getContentPane().setLayout(null);
		
		this.slider_Y = new JSlider();
		this.slider_Y.setEnabled(false);
		this.slider_Y.setOpaque(false);
		this.slider_Y.setBounds(62, 108, 222, 26);
		this.frame.getContentPane().add(this.slider_Y);
		
		this.slider_X = new JSlider();
		this.slider_X.setOrientation(SwingConstants.VERTICAL);
		this.slider_X.setOpaque(false);
		this.slider_X.setEnabled(false);
		this.slider_X.setBounds(161, 11, 25, 222);
		this.frame.getContentPane().add(this.slider_X);
		
		this.slider_Z = new JSlider();
		this.slider_Z.setOrientation(SwingConstants.VERTICAL);
		this.slider_Z.setOpaque(false);
		this.slider_Z.setEnabled(false);
		this.slider_Z.setBounds(341, 11, 25, 222);
		this.frame.getContentPane().add(this.slider_Z);
		
		this.slider_Rota = new JSlider();
		this.slider_Rota.setOpaque(false);
		this.slider_Rota.setEnabled(false);
		this.slider_Rota.setBounds(401, 108,  222, 26);
		this.frame.getContentPane().add(this.slider_Rota);
		
		
		this.btnRetour = new JButton("Retour");
		this.btnRetour.setBounds(573, 230, 89, 23);
		this.btnRetour.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				isRetour = true;
			}
			
		});
		this.frame.getContentPane().add(this.btnRetour);
		
		this.lblAvant = new JLabel("Avancer");
		this.lblAvant.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblAvant.setBounds(144, 4, 56, 14);
		this.frame.getContentPane().add(this.lblAvant);
		
		this.lblReculer = new JLabel("Reculer");
		this.lblReculer.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblReculer.setBounds(149, 231, 46, 14);
		this.frame.getContentPane().add(this.lblReculer);
		
		this.lblDroite = new JLabel("Droite");
		this.lblDroite.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblDroite.setBounds(285, 114, 46, 14);
		this.frame.getContentPane().add(this.lblDroite);
		
		this.lblGauche = new JLabel("Gauche");
		this.lblGauche.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblGauche.setBounds(13, 114, 46, 14);
		this.frame.getContentPane().add(this.lblGauche);
		
		this.lblMonter = new JLabel("Monter");
		this.lblMonter.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblMonter.setBounds(329, 4, 46, 14);
		this.frame.getContentPane().add(this.lblMonter);
		
		this.lblDescendre = new JLabel("Descendre");
		this.lblDescendre.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblDescendre.setBounds(309, 231, 89, 14);
		this.frame.getContentPane().add(this.lblDescendre);
		
		this.lblRotaDroite = new JLabel("Rotation Droite");
		this.lblRotaDroite.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblRotaDroite.setBounds(358, 97, 89, 14);
		this.frame.getContentPane().add(this.lblRotaDroite);
		
		this.lblRotaGauche = new JLabel("Rotation Gauche");
		this.lblRotaGauche.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblRotaGauche.setBounds(580, 97, 89, 14);
		this.frame.getContentPane().add(this.lblRotaGauche);
		
		this.frame.setVisible(true);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Methode permettant de changer la couleur des labels selon la direction
	 * @param AxeX
	 * @param AxeY
	 * @param AxeZ
	 * @param AxeRota
	 */
	public void setColorDirection(double AxeX, double AxeY, double AxeZ, double AxeRota)
	{
		this.ResetColorDirection();
		if(AxeX == 0.25)
		{
			this.lblAvant.setForeground(Color.GREEN);
			this.slider_X.setValue(100);
		}
		else if(AxeX == -0.25)
		{
			this.lblReculer.setForeground(Color.GREEN);
			this.slider_X.setValue(-100);
		}
		if(AxeY == 0.25)
		{
			this.lblGauche.setForeground(Color.GREEN);
			this.slider_Y.setValue(-100);
		}
		else if(AxeY == -0.25)
		{
			this.lblDroite.setForeground(Color.GREEN);
			this.slider_Y.setValue(100);
		}
		if(AxeZ == 0.25)
		{
			this.lblMonter.setForeground(Color.GREEN);
			this.slider_Z.setValue(-100);
		}
		else if(AxeZ == -0.25)
		{
			this.lblDescendre.setForeground(Color.GREEN);
			this.slider_Z.setValue(100);
		}
		if(AxeRota == 0.25)
		{
			this.lblRotaDroite.setForeground(Color.GREEN);
			this.slider_Rota.setValue(100);
		}
		else if(AxeRota == -0.25)
		{
			this.lblRotaDroite.setForeground(Color.GREEN);
			this.slider_Rota.setValue(-100);
		}
	}
	
	
	/**
	 * Methode permettant de remettre la couleur originelle aux labels et de rmettre les sliders a leur origine
	 */
	private void ResetColorDirection()
	{
		this.lblAvant.setForeground(Color.red);
		this.lblReculer.setForeground(Color.red);
		this.lblMonter.setForeground(Color.red);
		this.lblDescendre.setForeground(Color.red);
		this.lblGauche.setForeground(Color.red);
		this.lblDroite.setForeground(Color.red);
		this.lblRotaDroite.setForeground(Color.red);
		this.lblRotaGauche.setForeground(Color.red);
		this.slider_Rota.setValue(0);
		this.slider_X.setValue(0);
		this.slider_Y.setValue(0);
		this.slider_Z.setValue(0);
	}
	
	/**
	 * @return the isRetour
	 */
	public boolean getRetour() 
	{
		return this.isRetour;
	}

	/**
	 * Extinction de l ihm
	 */
	public void StopIHM()
	{
		this.frame.dispose();
	}
}
