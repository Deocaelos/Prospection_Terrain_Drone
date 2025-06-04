import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * @author RIAS Julien
 * @version 1.0
 * @date 04/03/25
 * @modification 04/03/25
 * @description IHM de Connexion au Serveur UDP 
 *
 */
public class IHM_ConnexionUDP 
{
	private JFrame frame;
	private JTextField textField_IPServeur;
	private JLabel lblIPServeur;
	private JButton btnConnexion;
	private boolean isConnection = false;
	
	/**
	 * Creation de la fenetre graphique de l'application.
	 */
	public IHM_ConnexionUDP() 
	{
		initialize();
	}
	
	private void initialize()
	{
		this.frame =  new JFrame();
		this.frame.setBounds(100, 50, 300, 116);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.getContentPane().setLayout(null);
		
		this.lblIPServeur = new JLabel("IP Serveur :");
		this.lblIPServeur.setHorizontalAlignment(SwingConstants.RIGHT);
		this.lblIPServeur.setBounds(-14, 12, 85, 14);
		this.frame.getContentPane().add(this.lblIPServeur);
		
		this.textField_IPServeur = new JTextField();
		this.textField_IPServeur.setHorizontalAlignment(SwingConstants.CENTER);
		this.textField_IPServeur.setBounds(78, 8, 200, 20);
		this.frame.getContentPane().add(textField_IPServeur);
		this.textField_IPServeur.setColumns(10);
		
		this.btnConnexion = new JButton("Se Connecter");
		this.btnConnexion.setBounds(10, 43, 264, 23);
		this.btnConnexion.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				if(!textField_IPServeur.getText().equals(""))
				{
					isConnection = true;
					setUIConnexion(false);
				}
			}
		});
		this.frame.getContentPane().add(this.btnConnexion);
		
		this.frame.setVisible(true);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Methode pour fermer l IHM
	 */
	public void Quitter()
	{
		this.frame.dispose();
	}
	
	/**
	 * Methode permettant de set l etat de l UI de Connexion
	 * @param etat
	 */
	public void setUIConnexion(boolean etat)
	{
		this.btnConnexion.setEnabled(etat);
		this.textField_IPServeur.setEnabled(etat);
	}
	
	/**
	 * @return the IP Adrress enter by the user
	 */
	public String getIPServeur()
	{
		return this.textField_IPServeur.getText();
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
	
	
}
