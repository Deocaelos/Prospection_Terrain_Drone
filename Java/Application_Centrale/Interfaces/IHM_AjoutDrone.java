package Interfaces;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.yaml.snakeyaml.Yaml;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.swing.JTextField;

public class IHM_AjoutDrone 
{
    private JFrame frmAjoutDeDrone;
    private JLabel lblWarning, lblIP, lblMAC, lblNom;
    private boolean isFinished = false;
	private JButton btnConfirmation;
	private JTextField textField_NomDrone,textField_MAC,textField_IP;
	private static final String YAML_FILE = "ressources/drones_config.yaml";
    private static List<Map<String, String>> Drones_List;  


    public IHM_AjoutDrone() 
    {
    	//On charge la liste de drone
    	IHM_AjoutDrone.Drones_List = this.ChargerDrones();
        initialize();
    }

    private void initialize()
    {
    	//*****************************FRAME*********************************
    	
        this.frmAjoutDeDrone = new JFrame("Choix Utilisateur");
        this.frmAjoutDeDrone.setTitle("Ajout de Drone");
        this.frmAjoutDeDrone.setResizable(false);
        this.frmAjoutDeDrone.setBounds(100, 100, 187, 276);
        this.frmAjoutDeDrone.getContentPane().setLayout(null); // Desactivation du layout manager pour un positionnement libre
        this.frmAjoutDeDrone.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.lblWarning = new JLabel("");
		this.lblWarning.setForeground(new Color(255, 0, 0));
		this.lblWarning.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblWarning.setBounds(0, 203, 171, 23);
        
		//*****************************BOUTON*********************************
		this.btnConfirmation = new JButton("Confirmer");
		this.btnConfirmation.setBounds(10, 158, 148, 23);
		this.frmAjoutDeDrone.getContentPane().add(this.lblWarning);
		this.frmAjoutDeDrone.getContentPane().add(this.btnConfirmation);
		
		this.textField_NomDrone = new JTextField();
		this.textField_NomDrone.setHorizontalAlignment(SwingConstants.CENTER);
		this.textField_NomDrone.setBounds(10, 29, 148, 20);
		this.frmAjoutDeDrone.getContentPane().add(this.textField_NomDrone);
		this.textField_NomDrone.setColumns(10);
		
		this.lblNom = new JLabel("Nom du Drone :");
		this.lblNom.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblNom.setBounds(10, 11, 148, 14);
		this.frmAjoutDeDrone.getContentPane().add(this.lblNom);
		
		this.lblIP = new JLabel("IP du Drone :");
		this.lblIP.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblIP.setBounds(10, 60, 148, 14);
		this.frmAjoutDeDrone.getContentPane().add(this.lblIP);
		
		this.textField_IP = new JTextField();
		this.textField_IP.setHorizontalAlignment(SwingConstants.CENTER);
		this.textField_IP.setColumns(10);
		this.textField_IP.setBounds(10, 78, 148, 20);
		this.frmAjoutDeDrone.getContentPane().add(this.textField_IP);
		
		this.lblMAC = new JLabel("MAC du Drone :");
		this.lblMAC.setHorizontalAlignment(SwingConstants.CENTER);
		this.lblMAC.setBounds(10, 109, 148, 14);
		this.frmAjoutDeDrone.getContentPane().add(this.lblMAC);
		
		this.textField_MAC = new JTextField();
		this.textField_MAC.setHorizontalAlignment(SwingConstants.CENTER);
		this.textField_MAC.setColumns(10);
		this.textField_MAC.setBounds(10, 127, 148, 20);
		this.frmAjoutDeDrone.getContentPane().add(this.textField_MAC);


        this.frmAjoutDeDrone.setVisible(true);
        
        
        
		//*****************************LISTENERS*********************************
        // 1- Listener Bouton Connexion
        this.btnConfirmation.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{	
				if(!textField_IP.getText().equals("") && !textField_NomDrone.getText().equals("") && !textField_MAC.getText().equals(""))
				{
					setUIConfirmation(false);
					AjouterDrone();
				}
				else
				{
					lblWarning.setText("Choix invalide");
				}
			}
		});

    }
    
    //*****************************METHODES*********************************
	
    /***
	 * Charger les drones depuis le fichier YAML
	 * @return Liste Drones
	 */
    private List<Map<String, String>> ChargerDrones() 
    {
        Yaml yaml = new Yaml();
        try (FileInputStream inputStream = new FileInputStream(IHM_AjoutDrone.YAML_FILE)) 
        {
            // Charger directement la liste des drones comme une liste de maps
            Map<String, List<Map<String, String>>> data = yaml.load(inputStream);
            return data.get("drones");
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement du fichier YAML : " + e.getMessage());
            return null;
        }
    }
    
    
    /**
     * Methode permettant d ajouter le drone au fichier yaml
     */
    private void AjouterDrone()
    {
    	Map<String, String> drone = Map.of(
            "nom", this.textField_NomDrone.getText(),
            "ip", this.textField_IP.getText(),
            "mac", this.textField_MAC.getText()
        );

    	IHM_AjoutDrone.Drones_List.add(drone);
        System.out.println("Drone ajoute : " + this.textField_NomDrone.getText());
        Yaml yaml = new Yaml();
        Map<String, List<Map<String, String>>> data = Map.of("drones", IHM_AjoutDrone.Drones_List);

        try (FileWriter writer = new FileWriter(IHM_AjoutDrone.YAML_FILE)) 
        {
            yaml.dump(data, writer);
            System.out.println("Drones sauvegardes avec succes !");
            this.isFinished = true;
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde des drones : " + e.getMessage());
            this.lblWarning.setText("Ajout Impossible");
            setUIConfirmation(true);
        }
    }
    
    
	/**
	 * Setter de l ui de confirmation
	 * @param etat de l ui
	 */
	
	public void setUIConfirmation(boolean etat)
	{
		this.btnConfirmation.setEnabled(etat);
		this.textField_IP.setEnabled(etat);
		this.textField_MAC.setEnabled(etat);
		this.textField_NomDrone.setEnabled(etat);
	}
	
	/**
	 * Methode permettant d arreter l ihm
	 */
	public void StopIHM()
	{
		this.frmAjoutDeDrone.dispose();
	}
	
	/**
	 * @return the isFinished
	 */
	public boolean isFinished() 
	{
		return this.isFinished;
	}

	/**
	 * @param isFinished the isFinished to set
	 */
	public void setFinished(boolean isFinished) 
	{
		this.isFinished = isFinished;
	}
}
