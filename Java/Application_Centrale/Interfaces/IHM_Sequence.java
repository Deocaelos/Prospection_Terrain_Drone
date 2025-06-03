package Interfaces;

import javax.swing.*;
import org.yaml.snakeyaml.Yaml;
import com.xpfriend.tydrone.SimpleMain;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Threads.ThreadCommandQueue;
import Utils.Tempo;

public class IHM_Sequence 
{
    private JFrame frame;
    private JLabel SelectionSequence, lblSequencesDeCommandes;
    private JComboBox<String> sequenceDropdown;
    private JButton buttonCreateSequence, buttonExecuteSequence, buttonBackSequence, btnEnregistrerLaSequence;
    private List<Map<String, Object>> sequences = new ArrayList<>();
    private static final String YAML_SEQUENCES_FILE = "ressources/sequenceauto.yaml";
    private ThreadCommandQueue commandQueue;
    private boolean isRetour = false;

    public IHM_Sequence(SimpleMain[] _Drones,ThreadCommandQueue _ThreadCommandQueue) 
    {
    	this.commandQueue = _ThreadCommandQueue;
        this.initialize();
        this.frame.setVisible(true);
        

    }

    public void initialize() 
    {
        // ********************* FRAME ***********************
        this.frame = new JFrame("Pave de Commandes du Drone");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.getContentPane().setLayout(null); // Permet de positionner librement les composants
        this.frame.setSize(756, 455); // Taille de la fenetre

        // ********************* LABELS ***********************
        this.SelectionSequence = new JLabel("Selectionnez une sequence :");
        this.SelectionSequence.setBounds(157, 204, 200, 25);
        this.frame.getContentPane().add(SelectionSequence);

        this.lblSequencesDeCommandes = new JLabel("Sequences De Commandes Automatiques");
        this.lblSequencesDeCommandes.setFont(new Font("Source Sans Pro ExtraLight", Font.ITALIC, 25));
        this.lblSequencesDeCommandes.setBounds(162, 88, 479, 51);
        this.frame.getContentPane().add(lblSequencesDeCommandes);

        // ********************* COMBOBOX ***********************
        this.sequenceDropdown = new JComboBox<>();
        this.sequenceDropdown.setBounds(334, 204, 200, 25);
        this.frame.getContentPane().add(sequenceDropdown);

        // ********************* BOUTTONS ***********************
        this.buttonCreateSequence = new JButton("Creer une nouvelle sequence");
        this.buttonCreateSequence.setBounds(49, 311, 214, 30);
        this.frame.getContentPane().add(buttonCreateSequence);

        this.buttonExecuteSequence = new JButton("Executer la sequence");
        this.buttonExecuteSequence.setBounds(303, 311, 180, 30);
        this.frame.getContentPane().add(buttonExecuteSequence);

        this.buttonBackSequence = new JButton("Retour");
        this.buttonBackSequence.setBounds(630, 375, 100, 30);
        this.frame.getContentPane().add(buttonBackSequence);

        this.btnEnregistrerLaSequence = new JButton("Enregistrer la sequence");
        this.btnEnregistrerLaSequence.setBounds(521, 311, 180, 30);
        this.frame.getContentPane().add(btnEnregistrerLaSequence);

        // ********************* LISTENERS ***********************
        this.buttonCreateSequence.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                creerNouvelleSequence();
                updateSequenceDropdown();
            }
        });

        this.buttonExecuteSequence.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                int selectedIndex = sequenceDropdown.getSelectedIndex();
                if (selectedIndex != -1) 
                {
                    executerSequenceDeCommandes(selectedIndex);
                } else
                {
                    JOptionPane.showMessageDialog(null, "Veuillez selectionner une sequence.");
                }
            }
        });

        this.buttonBackSequence.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
            	isRetour = true;
            }
        });
    }

    // ********************* METHODES ***********************

    // Methode pour mettre a jour la liste deroulante des sequences
    public void updateSequenceDropdown() 
    {
        List<Map<String, Object>> sequences = chargerSequences();
        this.sequenceDropdown.removeAllItems();
        if (sequences != null) {
            for (Map<String, Object> sequence : sequences)
            {
                sequenceDropdown.addItem((String) sequence.get("name"));
            }
        }
    }

    // 1 - Charger Sequences
    public List<Map<String, Object>> chargerSequences() 
    {
        Yaml yaml = new Yaml();
        try (FileInputStream inputStream = new FileInputStream(YAML_SEQUENCES_FILE)) {
            Map<String, Object> data = yaml.load(inputStream);
            if (data != null && data.containsKey("sequences")) {
                return (List<Map<String, Object>>) data.get("sequences");
            }
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement du fichier de sequences : " + e.getMessage());
        }
        return new ArrayList<>();
    }

    // 2 - Executer une sequence de commandes automatiquement
    public boolean executerSequenceDeCommandes(int indexSequence) 
    {
        if (indexSequence < 0 || indexSequence >= sequences.size()) 
        {
            JOptionPane.showMessageDialog(null, "Sequence non trouvee.");
            return false;
        }

        Map<String, Object> sequenceChoisie = sequences.get(indexSequence);
        List<Map<String, Object>> commandes = (List<Map<String, Object>>) sequenceChoisie.get("commands");
        for (Map<String, Object> commande : commandes) 
        {
            String nomCommande = (String) commande.get("name");
            System.out.println("Commandes: "+nomCommande);
            Map<String, Object> parameters = (Map<String, Object>) commande.get("parameters");
            this.EnregistrerCommande(nomCommande, parameters);
        }
        
        //Execution de la Queue
        this.commandQueue.startQueue();
        //On attend la fin de la Queue
        while(!this.commandQueue.getisFinished())
        {
            new Tempo(1000);
        }
        System.out.println("Queue finie");
        return true;
    }

    // 3 - Enregistrer les commandes dans la sequence
    private void EnregistrerCommande(String _nomCommande, Map<String, Object> _parameters) 
    {
        if (this.commandQueue != null) 
        {
            switch (_nomCommande) 
            {
                case "takeoff":
                    this.commandQueue.addToCommandQueue("takeoff",3000);
                    break;
                case "land":
                    this.commandQueue.addToCommandQueue("land",3000);
                    break;
                case "goUp":
                    this.commandQueue.addToCommandQueue("stick 0 0 0 0.2 0",(int) _parameters.get("duration"));
                    break;
                    
                case "goDown":
                    this.commandQueue.addToCommandQueue("stick 0 0 0 -0.2 0",(int) _parameters.get("duration"));
                    break;

                case "goLeft":
                    this.commandQueue.addToCommandQueue("stick -0.2 0 0 0 0",(int) _parameters.get("duration"));
                    break;

                case "goRight":
                    this.commandQueue.addToCommandQueue("stick 0.2 0 0 0 0",(int) _parameters.get("duration"));
                    break;

                case "goForward":
                    this.commandQueue.addToCommandQueue("stick 0 0.2 0 0 0",(int) _parameters.get("duration"));
                    break;

                case "goBackwards":
                    this.commandQueue.addToCommandQueue("stick 0 -0.2 0 0 0",(int) _parameters.get("duration"));
                    break;
                    
                case "rotateClockwise":
                    int degreesClockwise = Integer.parseInt((String) _parameters.get("value"));
                    this.commandQueue.addToCommandQueue("cw/" + degreesClockwise,1);
                    break;
                case "rotateCounterClockwise":
                    int degreesCounterClockwise = Integer.parseInt((String) _parameters.get("value"));
                    this.commandQueue.addToCommandQueue("ccw/" + degreesCounterClockwise,1);
                    break;
            }
        }
    }

    public void creerNouvelleSequence() 
    {
    	int durationUp = 0;
        int durationDown = 0;
        int durationLeft = 0;
        int durationRight = 0;
        int durationForward = 0;
        int durationBackward = 0;
        
        
        // Demander le nom de la sequence
        String sequenceName = JOptionPane.showInputDialog(null, "Entrez le nom de la nouvelle sequence :");
        if (sequenceName == null || sequenceName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Le nom de la sequence ne peut pas etre vide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Map<String, Object>> commandsList = new ArrayList<>();
        boolean continueAdding = true;

        while (continueAdding) {
            // Demander la commande
            String[] commands = {
                "takeoff", "land", "goUp", "goDown", "goLeft", "goRight",
                "goForward", "goBackwards", "rotateClockwise", "rotateCounterClockwise",
                "streamOn", "streamOff", "reboot", "emergency", "getBatteryPercentage", 
                "getSpeed", "setSpeed", "Close"
            };
            String command = (String) JOptionPane.showInputDialog(null, "Choisissez une commande :", "Ajouter Commande", JOptionPane.QUESTION_MESSAGE, null, commands, commands[0]);

            if (command == null) {
                // L'utilisateur a annule la selection
                break;
            }

            // Demander le parametre si necessaire
            Map<String, Object> parameters = new HashMap<>();
            switch (command) {
            case "goUp":
                String durationUpInput = JOptionPane.showInputDialog(null, "Entrez la duree pour goUp (en millisecondes) :");
                if (durationUpInput != null && !durationUpInput.isEmpty()) {
                    try {
                        durationUp = Integer.parseInt(durationUpInput.trim());
                        parameters.put("duration", durationUp);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "La duree doit etre un nombre valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                }
                break;

            case "goDown":
                String durationDownInput = JOptionPane.showInputDialog(null, "Entrez la duree pour goDown (en millisecondes) :");
                if (durationDownInput != null && !durationDownInput.isEmpty()) {
                    try {
                    	durationDown = Integer.parseInt(durationDownInput.trim());
                    	parameters.put("duration", durationDown);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "La duree doit etre un nombre valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                }
                break;

            case "goLeft":
                String durationLeftInput = JOptionPane.showInputDialog(null, "Entrez la duree pour goLeft (en millisecondes) :");
                if (durationLeftInput != null && !durationLeftInput.isEmpty()) {
                    try {
                    	durationLeft = Integer.parseInt(durationLeftInput.trim());
                    	parameters.put("duration", durationLeft);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "La duree doit etre un nombre valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                }
                break;

            case "goRight":
                String durationRightInput = JOptionPane.showInputDialog(null, "Entrez la duree pour goRight (en millisecondes) :");
                if (durationRightInput != null && !durationRightInput.isEmpty()) {
                    try {
                    	durationRight = Integer.parseInt(durationRightInput.trim());
                    	parameters.put("duration", durationRight);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "La duree doit etre un nombre valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                }
                break;

            case "goForward":
                String durationForwardInput = JOptionPane.showInputDialog(null, "Entrez la duree pour goForward (en millisecondes) :");
                if (durationForwardInput != null && !durationForwardInput.isEmpty()) {
                    try {
                    	durationForward = Integer.parseInt(durationForwardInput.trim());
                    	parameters.put("duration", durationForward);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "La duree doit etre un nombre valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                }
                break;

            case "goBackwards":
                String durationBackwardInput = JOptionPane.showInputDialog(null, "Entrez la duree pour goBackwards (en millisecondes) :");
                if (durationBackwardInput != null && !durationBackwardInput.isEmpty()) {
                    try {
                    	durationBackward = Integer.parseInt(durationBackwardInput.trim());
                    	parameters.put("duration", durationBackward);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "La duree doit etre un nombre valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                }
                break;
                
                case "rotateClockwise":
                case "rotateCounterClockwise":
                    String angle = JOptionPane.showInputDialog(null, "Entrez l'angle (entre 1 et 360 degres) :");
                    if (angle == null || angle.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "L'angle est obligatoire pour cette commande.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    try {
                        int angleValue = Integer.parseInt(angle.trim());
                        if (angleValue < 1 || angleValue > 360) {
                            JOptionPane.showMessageDialog(null, "L'angle doit etre compris entre 1 et 360 degres.", "Erreur", JOptionPane.ERROR_MESSAGE);
                            continue;
                        }
                        parameters.put("angle", angleValue);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "L'angle doit etre un nombre valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    break;
                case "setSpeed":
                    String speed = JOptionPane.showInputDialog(null, "Entrez la vitesse (entre 20 et 500 cm/s) :");
                    if (speed == null || speed.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "La vitesse est obligatoire pour cette commande.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    try {
                        int speedValue = Integer.parseInt(speed.trim());
                        if (speedValue < 20 || speedValue > 500) {
                            JOptionPane.showMessageDialog(null, "La vitesse doit etre comprise entre 20 et 500 cm/s.", "Erreur", JOptionPane.ERROR_MESSAGE);
                            continue;
                        }
                        parameters.put("speed", speedValue);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "La vitesse doit etre un nombre valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    break;
                case "takeoff":
                case "reboot":
                case "emergency":
                case "Close":
                case "getBatteryPercentage":
                case "getSpeed":
                case "streamOn":
                case "streamOff":
                case "land":
                    // Pas de parametre necessaire
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Commande non reconnue.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    continue;
            }

            // Ajouter la commande a la liste
            Map<String, Object> commandMap = new HashMap<>();
            commandMap.put("name", command);
            if (!parameters.isEmpty()) {
                commandMap.put("parameters", parameters);
            }
            commandsList.add(commandMap);

            // Demander si l'utilisateur veut continuer
            int response = JOptionPane.showConfirmDialog(null, "Voulez-vous ajouter une autre commande ?", "Continuer", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.NO_OPTION) {
                continueAdding = false;
            }
        }

        // Creer la nouvelle sequence
        Map<String, Object> newSequence = new HashMap<>();
        newSequence.put("name", sequenceName);
        newSequence.put("commands", commandsList);

        // Ajouter la sequence a la liste et sauvegarder
        if(this.sequences == null)
        {
        	this.sequences = new ArrayList<>();
        }
        this.sequences.add(newSequence);
        sauvegarderSequences();
    }


    public void sauvegarderSequences() 
    {
        Yaml yaml = new Yaml();
        Map<String, Object> data = new HashMap<>();
        data.put("sequences", sequences); // Notez la structure avec 'sequences'
        try (FileWriter writer = new FileWriter(YAML_SEQUENCES_FILE))
        {
            yaml.dump(data, writer);
            System.out.println("Sequences sauvegardees avec succes !");
        } catch (IOException e) 
        {
            System.out.println("Erreur lors de la sauvegarde des sequences : " + e.getMessage());
        }
    }
    
    
    public void StopIHM()
    {
    	this.frame.dispose();
    }

    public void setSequences(List<Map<String, Object>> sequences) 
    {
        this.sequences = sequences;
    }

    public void setDrones(SimpleMain[] _Drones) 
    {
        this.commandQueue = new ThreadCommandQueue(_Drones);
    }

	/**
	 * @return the isRetour
	 */
	public boolean isRetour() 
	{
		return this.isRetour;
	}
}
