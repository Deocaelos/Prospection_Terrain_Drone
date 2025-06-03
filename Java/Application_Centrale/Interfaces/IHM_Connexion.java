package Interfaces;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IHM_Connexion 
{
    private JFrame frame;
    private JLabel lblBienvenue, lblSkyWind, lblDemandeConnexion, lblDrone1, lblDrone2, lblWarning;
    private JComboBox<String> comboBox_Drone1,comboBox_Drone2;
    private String[] maListeDrone1 = null,maListeDrone2 = null, maListeMAC = null;
    private boolean isConnection = false;
	private JButton btnConnexion;


    public IHM_Connexion(String[] _listeDrones, String[] _maListeMAC) 
    {
    	this.maListeDrone1 = _listeDrones;
		this.maListeDrone2 = _listeDrones;
		this.maListeMAC = _maListeMAC;
        initialize();
        this.updateComboBox1(this.maListeDrone1);
		this.updateComboBox2(this.maListeDrone2);
    }

    private void initialize()
    {
    	//*****************************FRAME*********************************
    	
        this.frame = new JFrame("Connexion aux Drones");
        this.frame.setSize(600, 303);
        this.frame.setResizable(false);
        this.frame.getContentPane().setLayout(null); // Desactivation du layout manager pour un positionnement libre
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //*****************************LABELS*********************************
        this.lblBienvenue = new JLabel("Bienvenue sur");
        this.lblBienvenue.setFont(new Font("Tahoma", Font.PLAIN, 25));
        this.lblBienvenue.setBounds(122, 7, 168, 51); // Positionnement precis
        
        this.lblSkyWind = new JLabel("SkyWind Explorer");
        this.lblSkyWind.setFont(new Font("Source Sans Pro ExtraLight", Font.ITALIC, 25));
        this.lblSkyWind.setBounds(287, 8, 185, 51); // Positionnement precis
        
        this.lblDemandeConnexion = new JLabel("Veuillez connecter les drones s'il vous plait");
        this.lblDemandeConnexion.setHorizontalAlignment(SwingConstants.CENTER);
        this.lblDemandeConnexion.setFont(new Font("Tahoma", Font.PLAIN, 12));
        this.lblDemandeConnexion.setBounds(10, 69, 564, 28); // Positionnement precis
        
        this.lblDrone1 = new JLabel("Drone 1 (Gauche):");
		this.lblDrone1.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblDrone1.setBounds(10, 136, 254, 14);
		
		this.lblDrone2 = new JLabel("Drone 2 (Droite):");
		this.lblDrone2.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblDrone2.setBounds(320, 136, 254, 14);
		
		this.lblWarning = new JLabel("");
		this.lblWarning.setForeground(new Color(255, 0, 0));
		this.lblWarning.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblWarning.setBounds(132, 207, 316, 14);
		
		//*****************************COMBO BOX*********************************
        this.comboBox_Drone1 = new JComboBox();
		this.comboBox_Drone1.setBounds(10, 161, 254, 22);
		
		this.comboBox_Drone2 = new JComboBox();
		this.comboBox_Drone2.setBounds(320, 161, 254, 22);
        
		//*****************************BOUTON*********************************
		this.btnConnexion = new JButton("Se Connecter");
		this.btnConnexion.setBounds(109, 232, 350, 23);
		
		//*****************************FRAME ADD*********************************
        this.frame.getContentPane().add(this.lblBienvenue);
        this.frame.getContentPane().add(this.lblSkyWind);
        this.frame.getContentPane().add(this.lblDemandeConnexion);
        this.frame.getContentPane().add(this.lblDrone1);
        this.frame.getContentPane().add(this.lblDrone2);
		this.frame.getContentPane().add(this.comboBox_Drone1);
		this.frame.getContentPane().add(this.comboBox_Drone2);
		this.frame.getContentPane().add(this.lblWarning);
		this.frame.getContentPane().add(this.btnConnexion);


        this.frame.setVisible(true);
        
        
        
		//*****************************LISTENERS*********************************
        // 1- Listener Bouton Connexion
        this.btnConnexion.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				String choix_drone_1= (String) comboBox_Drone1.getSelectedItem();
				String choix_drone_2= (String) comboBox_Drone2.getSelectedItem();
				
				if(choix_drone_1.equals(" ") || choix_drone_2.equals(" "))
				{
					lblWarning.setText("Choix invalide");
				}
				else
				{
					if(!choix_drone_1.equals(choix_drone_2)) 
					{
						lblWarning.setText("");
						isConnection = true;
						setUIConnexion(false);
					}
					else //Utilisateur a choisi le meme drone
					{
						lblWarning.setText("Attention vous avez choisi le meme drone !");
					}
				}
			}
		});

    }
    
    //*****************************METHODES*********************************
    
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
		this.btnConnexion.setEnabled(etat);
		this.comboBox_Drone1.setEnabled(etat);
		this.comboBox_Drone2.setEnabled(etat);
	}
	
	
	/**
	 * Setter de letat du booleen isConnection
	 * @param etat de isConnection
	 */
	
	public void setisConnection(boolean etat)
	{
		this.isConnection = etat;
	}
	
	/**
	 * Getter de letat du booleen isConnection
	 * @return etat de isConnection
	 */
	
	public boolean getisConnection()
	{
		return(this.isConnection);
	}
	
	/**
	 * Methode pour mettre a jour la liste deroulante des drones 1
	 */
    private void updateComboBox1(String[] drones_list) 
    {
    	this.comboBox_Drone1.removeAllItems();  
        this.comboBox_Drone1.addItem(" ");
        for (String drone : drones_list) 
        {
        	this.comboBox_Drone1.addItem(drone);
        }
    }
    
    /**
	 * Methode pour mettre a jour la liste deroulante des drones 2
	 */
    private void updateComboBox2(String[] drones_list) 
    {
        this.comboBox_Drone2.removeAllItems(); 
        this.comboBox_Drone2.addItem(" ");
        for (String drone : drones_list) 
        {
        	this.comboBox_Drone2.addItem(drone);
        }
    }
    
    /**
     * Methode pour obtenir l ip du drone 1 selectionne
     * @return
     */
    public String getSelectedDroneMAC1() 
    {
	    int selectedIndex = this.comboBox_Drone1.getSelectedIndex()-1;
	    if (selectedIndex != -1) 
	    {
	        return this.maListeMAC[selectedIndex]; 
	    }
	    return null;
	}
    
    /**
     * Methode pour obtenir l ip du drone 2 selectionne
     * @return
     */
    public String getSelectedDroneMAC2() 
    {
	    int selectedIndex = this.comboBox_Drone2.getSelectedIndex()-1;
	    if (selectedIndex != -1) 
	    {
	        return this.maListeMAC[selectedIndex]; 
	    }
	    return null;
	}
    
    /**
     * Methode pour mettre a jour le texte du label warning
     * @param txt
     */
    public void setTextLblWarning(String txt)
    {
    	this.lblWarning.setText(txt);
    }
}
