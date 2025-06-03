package Interfaces;



import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * @author RIAS Julien
 * @version 1.0
 * @date 17/03/25
 * @modification 10/04/25
 * @description IHM de controle des drones grace au robot haptique
 *
 */

public class IHM_RobotHaptique 
{
	private JFrame frame;
	private JSlider slider_Y,slider_X,slider_Z;
	private JLabel lblPresenceUser, lblAvant,lblReculer,lblDroite,lblGauche,lblMonter,lblDescendre;
	private JButton btnRetour;
	private boolean isRetour = false;
	/**
	 * Creation de la fenetre graphique de l'application.
	 */
	public IHM_RobotHaptique() 
	{
		this.initialize();
		this.ResetColorDirection();
	}
	
	private void initialize()
	{	
		this.frame =  new JFrame("Robot Haptique");
		this.frame.setBounds(100, 0, 640, 278);
		this.frame.setResizable(false);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.getContentPane().setLayout(null);
		
		this.slider_Y = new JSlider();
		this.slider_Y.setMinimum(-100);
		this.slider_Y.setEnabled(false);
		this.slider_Y.setOpaque(false);
		this.slider_Y.setBounds(62, 108, 222, 26);
		this.frame.getContentPane().add(this.slider_Y);
		
		this.slider_X = new JSlider();
		this.slider_X.setMinimum(-100);
		this.slider_X.setOrientation(SwingConstants.VERTICAL);
		this.slider_X.setOpaque(false);
		this.slider_X.setEnabled(false);
		this.slider_X.setBounds(161, 11, 25, 222);
		this.frame.getContentPane().add(this.slider_X);
		
		this.slider_Z = new JSlider();
		this.slider_Z.setMinimum(-100);
		this.slider_Z.setOrientation(SwingConstants.VERTICAL);
		this.slider_Z.setOpaque(false);
		this.slider_Z.setEnabled(false);
		this.slider_Z.setBounds(341, 11, 25, 222);
		this.frame.getContentPane().add(this.slider_Z);
		
		this.lblPresenceUser = new JLabel("Presence Utilisateur");
		this.lblPresenceUser.setForeground(new Color(0, 0, 0));
		this.lblPresenceUser.setOpaque(true);
		this.lblPresenceUser.setBackground(new Color(255, 0, 0));
		this.lblPresenceUser.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblPresenceUser.setBounds(431, 26, 150, 26);
		this.frame.getContentPane().add(this.lblPresenceUser);
		
		this.btnRetour = new JButton("Retour");
		this.btnRetour.setBounds(526, 205, 89, 23);
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
		this.lblReculer.setBounds(149, 221, 46, 14);
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
		this.lblDescendre.setBounds(309, 221, 89, 14);
		this.frame.getContentPane().add(this.lblDescendre);
		
		this.frame.setVisible(true);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Methode permettant de changer la couleur du lable UserPresence selon un booleen
	 * @param etat
	 */
	public void setUserPresence(boolean etat)
	{
		if(etat)
		{
			this.lblPresenceUser.setBackground(Color.GREEN);
		}
		else
		{
			this.lblPresenceUser.setBackground(Color.red);
		}
	}
	
	/**
	 * Methode permettant de changer la couleur des labels selon la direction
	 * @param AxeX
	 * @param AxeY
	 * @param AxeZ
	 */
	public void setColorDirection(int AxeX, int AxeY, int AxeZ)
	{
		this.ResetColorDirection();
		if(AxeX == 1)
		{
			this.lblAvant.setForeground(Color.GREEN);
			this.slider_X.setValue(100);
		}
		else if(AxeX == -1)
		{
			this.lblReculer.setForeground(Color.GREEN);
			this.slider_X.setValue(-100);
		}
		else if(AxeY == 1)
		{
			this.lblGauche.setForeground(Color.GREEN);
			this.slider_Y.setValue(-100);
		}
		else if(AxeY == -1)
		{
			this.lblDroite.setForeground(Color.GREEN);
			this.slider_Y.setValue(100);
		}
		else if(AxeZ == 1)
		{
			this.lblMonter.setForeground(Color.GREEN);
			this.slider_Z.setValue(-100);
		}
		else if(AxeZ == -1)
		{
			this.lblDescendre.setForeground(Color.GREEN);
			this.slider_Z.setValue(100);
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
