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
import javax.swing.DefaultComboBoxModel;

public class IHM_ChoixUtilisateur 
{
    private JFrame frame;
    private JLabel lblBienvenue, lblSkyWind, lblDemandeChoix, lblWarning;
    private JComboBox<String> comboBox_Choix, comboBox_ListeDrone;
    private boolean isConfirmer = false;
	private JButton btnConfirmation;
	private JLabel lblDronesList;


    public IHM_ChoixUtilisateur() 
    {
        initialize();
    }

    private void initialize()
    {
    	//*****************************FRAME*********************************
    	
        this.frame = new JFrame("Choix Utilisateur");
        this.frame.setSize(555, 270);
        this.frame.setResizable(false);
        this.frame.getContentPane().setLayout(null); // Desactivation du layout manager pour un positionnement libre
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //*****************************LABELS*********************************
        this.lblBienvenue = new JLabel("Bienvenue sur");
        this.lblBienvenue.setFont(new Font("Tahoma", Font.PLAIN, 25));
        this.lblBienvenue.setBounds(97, 11, 168, 51); // Positionnement precis
        
        this.lblSkyWind = new JLabel("SkyWind Explorer");
        this.lblSkyWind.setFont(new Font("Source Sans Pro ExtraLight", Font.ITALIC, 25));
        this.lblSkyWind.setBounds(262, 12, 185, 51); // Positionnement precis
        
        this.lblDemandeChoix = new JLabel("Veuillez faire votre choix");
        this.lblDemandeChoix.setHorizontalAlignment(SwingConstants.CENTER);
        this.lblDemandeChoix.setFont(new Font("Tahoma", Font.PLAIN, 12));
        this.lblDemandeChoix.setBounds(-35, 73, 584, 28);
        
        this.lblDronesList = new JLabel("Liste de Drones:");
        this.lblDronesList.setVisible(false);
        this.lblDronesList.setBounds(10, 206, 94, 14);
        this.frame.getContentPane().add(this.lblDronesList);
		
		this.lblWarning = new JLabel("");
		this.lblWarning.setForeground(new Color(255, 0, 0));
		this.lblWarning.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblWarning.setBounds(97, 60, 350, 14);
		
		//*****************************COMBO BOX*********************************
        this.comboBox_Choix = new JComboBox();
        this.comboBox_Choix.setModel(new DefaultComboBoxModel(new String[] {"Ajouter un Drone", "Voir la liste des Drones", "Se Connecter aux Drones","Quitter"}));
		this.comboBox_Choix.setBounds(97, 110, 350, 22);
		
		this.comboBox_ListeDrone = new JComboBox();
		this.comboBox_ListeDrone.setVisible(false);
		this.comboBox_ListeDrone.setBounds(107, 201, 422, 22);
		this.frame.getContentPane().add(this.comboBox_ListeDrone);
        
		//*****************************BOUTON*********************************
		this.btnConfirmation = new JButton("Confirmer");
		this.btnConfirmation.setBounds(97, 157, 350, 23);
		
		//*****************************FRAME ADD*********************************
        this.frame.getContentPane().add(this.lblBienvenue);
        this.frame.getContentPane().add(this.lblSkyWind);
        this.frame.getContentPane().add(this.lblDemandeChoix);
		this.frame.getContentPane().add(this.comboBox_Choix);
		this.frame.getContentPane().add(this.lblWarning);
		this.frame.getContentPane().add(this.btnConfirmation);

        this.frame.setVisible(true);
		//*****************************LISTENERS*********************************
        // 1- Listener Bouton Connexion
        this.btnConfirmation.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				String choix_util= (String) comboBox_Choix.getSelectedItem();
				
				if(choix_util.equals(" "))
				{
					lblWarning.setText("Choix invalide");
				}
				else
				{
					setUIConfirmation(false);
					isConfirmer = true;
				}
			}
		});

    }
    
    //*****************************METHODES*********************************
	
    /**
	 * Methode pour mettre a jour la liste deroulante des drones
	 */
    public void updateComboBox(String[] drones_list) 
    {
    	this.comboBox_ListeDrone.removeAllItems();  
        this.comboBox_ListeDrone.addItem(" ");
        for (String drone : drones_list) 
        {
        	this.comboBox_ListeDrone.addItem(drone);
        }
    }
    
    /**
	 * Setter de l ui de liste de drone
	 * @param etat de l ui
	 */
	
	public void setUIListeDrone(boolean etat)
	{
		this.comboBox_ListeDrone.setVisible(etat);
		this.lblDronesList.setVisible(etat);
	}
    
    
	/**
	 * Setter de l ui de confirmation
	 * @param etat de l ui
	 */
	
	public void setUIConfirmation(boolean etat)
	{
		this.btnConfirmation.setEnabled(etat);
		this.comboBox_Choix.setEnabled(etat);
	}
	
	
	/**
	 * Setter de letat du booleen isConfirmer
	 * @param etat de isConfirmer
	 */
	
	public void setisConfirmer(boolean etat)
	{
		this.isConfirmer = etat;
	}
	
	/**
	 * Getter de letat du booleen isConfirmer
	 * @return etat de isConfirmer
	 */
	
	public boolean getisConfirmer()
	{
		return(this.isConfirmer);
	}
	
	/**
	 * Mehode permettant d obtenir le choix de l utilisateur
	 * @return
	 */
	public String getChoixUser()
	{
		return (String) this.comboBox_Choix.getSelectedItem();
	}
	
	/**
	 * Methode permettant d arreter l ihm
	 */
	public void StopIHM()
	{
		this.frame.dispose();
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
