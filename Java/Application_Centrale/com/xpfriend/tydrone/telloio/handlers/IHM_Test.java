package com.xpfriend.tydrone.telloio.handlers;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * @author RIAS Julien
 * @version 1.0
 * @date 17/03/25
 * @modification 17/03/25
 * @description IHM de controle des drones
 *
 */

public class IHM_Test 
{
	private JFrame frame;
	private JButton[] ButtonCommande = new JButton[10];
	private JLabel lblNewLabel;
	/**
	 * Creation de la fenetre graphique de l'application.
	 */
	public IHM_Test() 
	{
		initialize();
	}
	
	private void initialize()
	{
		Dimension WindowSize = Toolkit.getDefaultToolkit().getScreenSize();
        int Window_width = this.DoubleToInt(WindowSize.width*0.45);
        int Window_height = this.DoubleToInt(WindowSize.height-0.05*WindowSize.height);
        
        String[] Nom_Commande = {"-90","Avancer","+90","Gauche","Droite","-180","Reculer","+180","Monter","Descendre","Decoller"};
		
		this.frame =  new JFrame("");
		this.frame.setBounds(100, 0, 864, 520);
		this.frame.setResizable(false);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.getContentPane().setLayout(null);
		
		this.lblNewLabel = new JLabel("");
		this.lblNewLabel.setBounds(10, 11, 653, 379);
		this.frame.getContentPane().add(this.lblNewLabel);
		
		
		this.frame.setVisible(true);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Methode permettant de mettre a jour l image affichee sur le label
	 * @param icon
	 */
	public void setVideo(ImageIcon icon)
	{
		this.lblNewLabel.setIcon(icon);
		this.lblNewLabel.repaint();
		this.lblNewLabel.revalidate();
	}
	
	
	/**
	 * Extinction de l ihm accueil
	 */
	public void StopIHMAccueil()
	{
		this.frame.dispose();
	}
	
	/**
	 * Setter de l ui de connexion
	 * @param etat de l ui
	 */
	
	public void setUIConnexion(boolean etat)
	{
		//this.btnConnexion.setEnabled(etat);
		//this.comboBox_Drone1.setEnabled(etat);
		//this.comboBox_Drone2.setEnabled(etat);
	}
	
	/**
	 * Methode permettant de convertir un double en int
	 * @param value
	 * @return
	 */
	private int DoubleToInt(double value)
	{
		return((int) Math.round(value));
	}
}
