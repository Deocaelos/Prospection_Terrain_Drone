package Interfaces;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class IHM_ControleDrone {
    private JFrame frame;
    private String IMAGE_PATH = "ressources/Assets/";
    // Stockage des images pour les etats actives et desactives
    private Map<JButton, String> buttonImageMap = new HashMap<>();
    
    // Declaration des boutons
    private JButton btnQuitter, flipGaucheButton, flipAvantButton, flipDroitButton, flipArriereButton,gaucheButton,monterButton, droiteButton, reculerButton, avancerButton, descendreButton, decollerButton, atterirButton,button90, buttonMinus90, button180, buttonSequences, buttonRobotHaptique, buttonSuiviChar, buttonCasqueVR;
    private boolean EtatBoutonQuitter, isflipGauche, isflipAvant, isflipDroit, isflipArriere, isLeft, isUp, isRight,isBackward, isForward, isDown, isTakeOff, isLanding, isButton90, isButtonMinus90, isButton180,isSequence, isRobotHaptique, isSuiviChar , isCasqueVR;
    private JLabel lblVideoDrone1, lblVideoDrone2, lblContour, lblTailleForme,backgroundLabel,labelEtatCommAndroid;
    private JSlider sliderContourVideo, sliderTailleForme;
    private JRadioButton rdbtnDetectionContour, rdbtnDetectionForme;
    private ImageIcon backgroundImage;
    private JLayeredPane layeredPane;
    private boolean flagDetectionContours = false, flagDetectionFormes = false;
    
    public IHM_ControleDrone()
    {
        initialize();
    }
    
    private void initialize()
    {
        // ***************************** FRAME *********************************
        this.frame = new JFrame("Controle du Drone");
        this.frame.setSize(800, 1000);
        this.frame.setResizable(false);
        this.frame.getContentPane().setLayout(null);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Utiliser un JLayeredPane pour gerer les couches
        this.layeredPane = new JLayeredPane();
        this.layeredPane.setPreferredSize(new Dimension(800, 1000));
        this.frame.setContentPane(layeredPane); // Definir le JLayeredPane comme contenu du JFrame
        
        // Charger l image de fond depuis le chemin relatif
        this.backgroundImage = new ImageIcon("ressources/Assets/background.jpg");
        this.backgroundLabel = new JLabel(backgroundImage);
        this.backgroundLabel.setBounds(0, 0, 800, 1000); // Positionner le JLabel pour couvrir tout le JFrame
        
        // Ajouter le JLabel avec l image de fond au JLayeredPane en arriere plan
        this.layeredPane.add(backgroundLabel, Integer.valueOf(JLayeredPane.DEFAULT_LAYER));
        
        // ************************** SLIDERS ***********************
        this.sliderContourVideo = new JSlider();
        this.sliderContourVideo.setEnabled(true);
        this.sliderContourVideo.setMinimum(5);
        this.sliderContourVideo.setMaximum(70);
        this.sliderContourVideo.setValue((this.sliderContourVideo.getMaximum() + this.sliderContourVideo.getMinimum()) / 2);
        this.sliderContourVideo.setBounds(595, 74, 149, 26);
        this.sliderContourVideo.setOpaque(false);
        this.sliderContourVideo.setForeground(Color.WHITE);
        
        this.sliderTailleForme = new JSlider();
        this.sliderTailleForme.setEnabled(true);
        this.sliderTailleForme.setValue(37);
        this.sliderTailleForme.setMinimum(100);
        this.sliderTailleForme.setMaximum(5000);
        this.sliderTailleForme.setBounds(595, 166, 149, 26);
        this.sliderTailleForme.setOpaque(false);
        this.sliderTailleForme.setForeground(Color.WHITE);
        
        // ***************************** LABELS *********************************
        this.lblVideoDrone1 = new JLabel(" Drone 1:");
        this.lblVideoDrone1.setVerticalAlignment(SwingConstants.TOP);
        this.lblVideoDrone1.setFont(new Font("Tahoma", Font.PLAIN, 12));
        this.lblVideoDrone1.setForeground(Color.RED);
        this.lblVideoDrone1.setBackground(Color.BLACK);
        this.lblVideoDrone1.setOpaque(true);
        this.lblVideoDrone1.setHorizontalAlignment(SwingConstants.LEFT);
        this.lblVideoDrone1.setBounds(this.DoubleToInt(this.frame.getWidth() * 0.010),this.DoubleToInt(this.frame.getHeight() * 0.01),this.DoubleToInt((this.frame.getHeight() * 0.20) * (4.0 / 3.0)),this.DoubleToInt(this.frame.getHeight() * 0.20));
        
        this.lblVideoDrone2 = new JLabel(" Drone 2:");
        this.lblVideoDrone2.setVerticalAlignment(SwingConstants.TOP);
        this.lblVideoDrone2.setFont(new Font("Tahoma", Font.PLAIN, 12));
        this.lblVideoDrone2.setForeground(Color.RED);
        this.lblVideoDrone2.setBackground(Color.BLACK);
        this.lblVideoDrone2.setOpaque(true);
        this.lblVideoDrone2.setHorizontalAlignment(SwingConstants.LEFT);
        this.lblVideoDrone2.setBounds(this.DoubleToInt(this.lblVideoDrone1.getWidth() + this.lblVideoDrone1.getX() + this.frame.getWidth() * 0.01),this.lblVideoDrone1.getY(), this.DoubleToInt((this.frame.getHeight() * 0.20) * (4.0 / 3.0)),this.DoubleToInt(this.frame.getHeight() * 0.20));
        
        this.lblContour = new JLabel("Taux Contours: " + this.sliderContourVideo.getValue());
        this.lblContour.setBounds(595, 49, 149, 14);
        this.lblContour.setForeground(Color.WHITE);
        
        this.lblTailleForme = new JLabel("Taille Forme > " + this.sliderTailleForme.getValue());
        this.lblTailleForme.setBounds(595, 141, 149, 14);
        this.lblTailleForme.setForeground(Color.WHITE);
        
        this.labelEtatCommAndroid = new JLabel("Etat Serveur Android: Eteint");
        this.labelEtatCommAndroid.setForeground(Color.red);
        this.labelEtatCommAndroid.setBounds(10, 975, this.frame.getWidth()-15, 14);

        
        // ***************************** BOUTONS *******************************
        // Style commun pour tous les boutons
        Dimension buttonSize = new Dimension(60, 60);
        
        // Initialisation des boutons de Flip
        this.flipGaucheButton = this.createImageButton("flip_left.png", buttonSize);
        this.flipGaucheButton.setBounds(180, 260, 60, 60);
        this.flipGaucheButton.setToolTipText("Flip Gauche");
        this.buttonImageMap.put(this.flipGaucheButton, "flip_left.png");
        this.updateButtonImage(this.flipGaucheButton, false); // Initialiser avec l'image desactivee
        
        this.flipAvantButton = this.createImageButton("flip_forward.png", buttonSize);
        this.flipAvantButton.setBounds(300, 260, 60, 60);
        this.flipAvantButton.setToolTipText("Flip Avant");
        this.buttonImageMap.put(this.flipAvantButton, "flip_forward.png");
        this.updateButtonImage(this.flipAvantButton, false); // Initialiser avec l'image desactivee

        
        this.flipDroitButton = this.createImageButton("flip_right.png", buttonSize);
        this.flipDroitButton.setBounds(420, 260, 60, 60);
        this.flipDroitButton.setToolTipText("Flip Droit");
        this.buttonImageMap.put(this.flipDroitButton, "flip_right.png");
        this.updateButtonImage(this.flipDroitButton, false); // Initialiser avec l'image desactivee

        
        this.flipArriereButton = this.createImageButton("flip_back.png", buttonSize);
        this.flipArriereButton.setBounds(300, 300, 60, 60);
        this.flipArriereButton.setToolTipText("Flip Arriere");
        this.buttonImageMap.put(this.flipArriereButton, "flip_back.png");
        this.updateButtonImage(this.flipArriereButton, false); // Initialiser avec l'image desactivee

        
        // Initialisation des boutons de Mouvement
        this.gaucheButton = this.createImageButton("left.png", buttonSize);
        this.gaucheButton.setBounds(150, 530, 60, 60);
        this.gaucheButton.setToolTipText("Gauche");
        this.buttonImageMap.put(this.gaucheButton, "left.png");
        this.updateButtonImage(this.gaucheButton, false); // Initialiser avec l'image desactivee

        
        this.monterButton = this.createImageButton("up.png", buttonSize);
        this.monterButton.setBounds(300, 460, 60, 60);
        this.monterButton.setToolTipText("Monter");
        this.buttonImageMap.put(this.monterButton, "up.png");
        this.updateButtonImage(this.monterButton, false); // Initialiser avec l'image desactivee

        
        this.droiteButton = this.createImageButton("right.png", buttonSize);
        this.droiteButton.setBounds(450, 530, 60, 60);
        this.droiteButton.setToolTipText("Droite");
        this.buttonImageMap.put(this.droiteButton, "right.png");
        this.updateButtonImage(this.droiteButton, false); // Initialiser avec l'image desactivee

        
        this.reculerButton = this.createImageButton("backward.png", buttonSize);
        this.reculerButton.setBounds(300, 670, 60, 60);
        this.reculerButton.setToolTipText("Reculer");
        this.buttonImageMap.put(this.reculerButton, "backward.png");
        this.updateButtonImage(this.reculerButton, false); // Initialiser avec l'image desactivee

        
        this.avancerButton = this.createImageButton("forward.png", buttonSize);
        this.avancerButton.setBounds(300, 530, 60, 60);
        this.avancerButton.setToolTipText("Avancer");
        this.buttonImageMap.put(this.avancerButton, "forward.png");
        this.updateButtonImage(this.avancerButton, false); // Initialiser avec l'image desactivee

        
        this.descendreButton = this.createImageButton("down.png", buttonSize);
        this.descendreButton.setBounds(300, 600, 60, 60);
        this.descendreButton.setToolTipText("Descendre");
        this.buttonImageMap.put(this.descendreButton, "down.png");
        this.updateButtonImage(this.descendreButton, false); // Initialiser avec l'image desactivee

        
        //Rotation
        this.button90 = this.createImageButton("rotate_right.png", buttonSize);
        this.button90.setBounds(420, 350, 60, 60);
        this.button90.setToolTipText("+90");
        this.buttonImageMap.put(this.button90, "rotate_right.png");
        this.updateButtonImage(this.button90, false); // Initialiser avec l'image desactivee

        
        this.buttonMinus90 = this.createImageButton("rotate_left.png", buttonSize);
        this.buttonMinus90.setBounds(180, 350, 60, 60);
        this.buttonMinus90.setToolTipText("-90");
        this.buttonImageMap.put(this.buttonMinus90, "rotate_left.png");
        this.updateButtonImage(this.buttonMinus90, false); // Initialiser avec l'image desactivee

        
        this.button180 = this.createImageButton("rotate_180.png", buttonSize);
        this.button180.setBounds(300, 350, 60, 60);
        this.button180.setToolTipText("180");
        this.buttonImageMap.put(this.button180, "rotate_180.png");
        this.updateButtonImage(this.button180, false); // Initialiser avec l'image desactivee

        
        // Initialisation des boutons d Actions
        this.decollerButton = this.createImageButton("takeoff.png", new Dimension(80, 80));
        this.decollerButton.setBounds(150, 770, 80, 80);
        this.decollerButton.setToolTipText("Decoller");
        this.buttonImageMap.put(this.decollerButton, "takeoff.png");
        
        
        this.atterirButton = this.createImageButton("land.png", new Dimension(80, 80));
        this.atterirButton.setBounds(420, 770, 80, 80);
        this.atterirButton.setToolTipText("Atterir");
        this.buttonImageMap.put(this.atterirButton, "land.png");
        this.updateButtonImage(this.atterirButton, false); // Initialiser avec l'image desactivee

        
        this.btnQuitter = this.createImageButton("exit.png", new Dimension(50, 50));
        this.btnQuitter.setBounds(621, 920, 50, 50);
        this.btnQuitter.setToolTipText("Quitter");
        this.buttonImageMap.put(this.btnQuitter, "exit.png");
        
        // Initialisation des boutons de Menus
        this.buttonSequences = this.createImageButton("sequence.png", new Dimension(120, 40));
        this.buttonSequences.setBounds(10, 700, 120, 40);
        this.buttonSequences.setToolTipText("Sequences");
        this.buttonImageMap.put(this.buttonSequences, "sequence.png");
        this.updateButtonImage(this.buttonSequences, false); // Initialiser avec l'image desactivee

        
        this.buttonRobotHaptique = this.createImageButton("robot.png", new Dimension(120, 40));
        this.buttonRobotHaptique.setBounds(10, 750, 120, 40);
        this.buttonRobotHaptique.setToolTipText("Robot Haptique");
        this.buttonImageMap.put(this.buttonRobotHaptique, "robot.png");
        this.updateButtonImage(this.buttonRobotHaptique, false); // Initialiser avec l'image desactivee

        
        this.buttonSuiviChar = this.createImageButton("track.png", new Dimension(120, 40));
        this.buttonSuiviChar.setBounds(10, 800, 120, 40);
        this.buttonSuiviChar.setToolTipText("Suivi du Char");
        this.buttonImageMap.put(this.buttonSuiviChar, "track.png");
        this.updateButtonImage(this.buttonSuiviChar, false); // Initialiser avec l'image desactivee


        
        this.buttonCasqueVR = this.createImageButton("casque.png", new Dimension(120, 40));
        this.buttonCasqueVR.setBounds(10, 650, 120, 40);
        this.buttonCasqueVR.setToolTipText("Casque VR");
        this.buttonImageMap.put(this.buttonCasqueVR, "casque.png");
        this.updateButtonImage(this.buttonCasqueVR, true); // Initialiser avec l'image desactivee

        
        // Initialisation du Radiobouton pour les contours
        this.rdbtnDetectionContour = new JRadioButton("Detection Contours ?");
        this.rdbtnDetectionContour.setEnabled(true);
        this.rdbtnDetectionContour.setBounds(595, 19, 149, 23);
        this.rdbtnDetectionContour.setOpaque(false);
        
        this.rdbtnDetectionForme = new JRadioButton("Detection Formes ?");
        this.rdbtnDetectionForme.setEnabled(true);
        this.rdbtnDetectionForme.setBounds(595, 107, 149, 23);
        this.rdbtnDetectionForme.setOpaque(false);
        
        // ***************************** AJOUT DES BOUTONS ************************
        
        // Section Flip
        this.layeredPane.add(this.flipGaucheButton, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.flipAvantButton, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.flipDroitButton, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.flipArriereButton, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        
        // Section Mouvement
        this.layeredPane.add(this.gaucheButton, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.monterButton, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.droiteButton, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.reculerButton, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.avancerButton, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.descendreButton, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.button90, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.buttonMinus90, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.button180, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        
        // Section Actions
        this.layeredPane.add(this.decollerButton, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.atterirButton, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.btnQuitter, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        
        // Section Menus
        this.layeredPane.add(this.buttonSequences, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.buttonRobotHaptique, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.buttonSuiviChar, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.buttonCasqueVR, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        
        // Section Labels
        this.layeredPane.add(this.lblVideoDrone1, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.lblVideoDrone2, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.lblContour, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.lblTailleForme, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.labelEtatCommAndroid,Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        
        // Section Contours
        this.layeredPane.add(this.rdbtnDetectionContour, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.rdbtnDetectionForme, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        
		// Sliders
        this.layeredPane.add(this.sliderContourVideo, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        this.layeredPane.add(this.sliderTailleForme, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
        
        // **************************** LISTENERS **************************************
        this.rdbtnDetectionContour.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (rdbtnDetectionContour.isSelected()) {
                    setFlagDetectionContours(true);
                    sliderContourVideo.setEnabled(true);
                } else {
                    setFlagDetectionContours(false);
                    sliderContourVideo.setEnabled(false);
                }
            }
        });
        
        this.rdbtnDetectionForme.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (rdbtnDetectionForme.isSelected()) {
                    setFlagDetectionFormes(true);
                    sliderTailleForme.setEnabled(true);
                } else {
                    setFlagDetectionFormes(false);
                    sliderTailleForme.setEnabled(false);
                }
            }
        });
        
        this.sliderContourVideo.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                lblContour.setText("Taux Contours: " + sliderContourVideo.getValue());
            }
        });
        
        this.sliderTailleForme.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                lblTailleForme.setText("Taille Forme > " + sliderTailleForme.getValue());
            }
        });
        
        // Listeners pour les boutons de Flip
        this.flipGaucheButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setIsflipGauche(true);
                System.out.println("Appui sur flipGaucheButton");
            }
        });
        
        this.flipAvantButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setIsflipAvant(true);
                System.out.println("Appui sur flipAvantButton");
            }
        });
        
        this.flipDroitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setIsflipDroit(true);
                System.out.println("Appui sur flipDroitButton");
            }
        });
        
        this.flipArriereButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setIsflipArriere(true);
                System.out.println("Appui sur flipArriereButton");
            }
        });
        
        // Listeners pour les boutons de Mouvement
        this.gaucheButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isLeft = true;
                System.out.println("Appui sur gaucheButton");
            }
        });
        
        this.monterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isUp = true;
                System.out.println("Appui sur monterButton");
            }
        });
        
        this.droiteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isRight = true;
                System.out.println("Appui sur droiteButton");
            }
        });
        
        this.reculerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isBackward = true;
                System.out.println("Appui sur reculerButton");
            }
        });
        
        this.avancerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isForward = true;
                System.out.println("Appui sur avancerButton");
            }
        });
        
        this.descendreButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isDown = true;
                System.out.println("Appui sur descendreButton");
            }
        });
        
        this.button90.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setButton90(true);
                System.out.println("Appui sur button90");
            }
        });
        
        this.buttonMinus90.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setButtonMinus90(true);
                System.out.println("Appui sur buttonMinus90");
            }
        });
        
        this.button180.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setButton180(true);
                System.out.println("Appui sur button180");
            }
        });
        
        // Listeners pour les boutons d Actions
        this.decollerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isTakeOff = true;
                System.out.println("Appui sur decollerButton");
            }
        });
        
        this.atterirButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isLanding = true;
                System.out.println("Appui sur atterirButton");
            }
        });
        
        // Listeners pour les boutons de Menus
        this.buttonSequences.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                isSequence = true;
                System.out.println("Appui sur buttonSequences");
            }
        });
        
        this.buttonRobotHaptique.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setRobotHaptique(true);
                System.out.println("Appui sur buttonRobotHaptique");
            }
        });
        
        this.buttonSuiviChar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isSuiviChar = true;
                System.out.println("Appui sur buttonSuiviChar");
            }
        });
        
        this.buttonCasqueVR.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setCasqueVR(true);
                System.out.println("Appui sur buttonCasqueVR");
            }
        });
        
        this.btnQuitter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EtatBoutonQuitter = true;
                System.out.println("Appui sur btnQuitter");
            }
        });
        
        // ***************************** AFFICHAGE DE LA FENETRE *****************************
        this.frame.pack();
        this.frame.setVisible(true);
    }
    
    // ***************************** METHODES *********************************

    // Methode utilitaire pour creer un bouton avec une image
    private JButton createImageButton(String imageName, Dimension size)
    {
        JButton button = new JButton();
        try {
            ImageIcon icon = new ImageIcon(this.IMAGE_PATH + imageName);
            Image img = icon.getImage();
            // Calcul des dimensions pour conserver le ratio
            int originalWidth = icon.getIconWidth();
            int originalHeight = icon.getIconHeight();
            double ratio = (double) originalWidth / originalHeight;
            int newWidth, newHeight;
            if (ratio > 1) {
                newWidth = size.width;
                newHeight = (int) (size.width / ratio);
            } else {
                newHeight = size.height;
                newWidth = (int) (size.height * ratio);
            }
            img = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement de l image: " + imageName);
            button.setText(imageName.replace(".png", ""));
        }
        button.setPreferredSize(size);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        return button;
    }
    
    /**
     * Methode pour mettre � jour l'apparence d'un bouton en fonction de son etat active/desactive
     * 
     * @param button Le bouton � mettre � jour
     * @param enabled L'etat du bouton (active ou desactive)
     */
    private void updateButtonImage(JButton button, boolean enabled) {
        if (buttonImageMap.containsKey(button)) {
            String imageName = buttonImageMap.get(button);
            String imageToLoad;
            
            if (enabled) {
                // Utiliser l'image normale pour l'etat active
                imageToLoad = imageName;
            } else {
                // Utiliser l'image avec suffixe "_disable" pour l'etat desactive
                String nameWithoutExtension = imageName.substring(0, imageName.lastIndexOf("."));
                String extension = imageName.substring(imageName.lastIndexOf("."));
                imageToLoad = nameWithoutExtension + "_disable" + extension;
            }
            
            try {
                ImageIcon icon = new ImageIcon(this.IMAGE_PATH + imageToLoad);
                Image img = icon.getImage();
                
                // Recuperer la taille du bouton
                int width = button.getPreferredSize().width;
                int height = button.getPreferredSize().height;
                
                // Calcul des dimensions pour conserver le ratio
                int originalWidth = icon.getIconWidth();
                int originalHeight = icon.getIconHeight();
                double ratio = (double) originalWidth / originalHeight;
                int newWidth, newHeight;
                if (ratio > 1) {
                    newWidth = width;
                    newHeight = (int) (width / ratio);
                } else {
                    newHeight = height;
                    newWidth = (int) (height * ratio);
                }
                
                img = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                System.out.println("Erreur lors du chargement de l'image: " + imageToLoad);
                // Si l'image n'existe pas, on garde l'image actuelle
            }
        }
    }


	/**
	 * Methode permettant de definir letat des buttons de controle
	 * @param etat
	 */
    public void setUIControle_Commande(boolean etat) {
        // Boutons de mouvement
        this.avancerButton.setEnabled(etat);
        updateButtonImage(this.avancerButton, etat);

        this.reculerButton.setEnabled(etat);
        updateButtonImage(this.reculerButton, etat);

        this.monterButton.setEnabled(etat);
        updateButtonImage(this.monterButton, etat);

        this.descendreButton.setEnabled(etat);
        updateButtonImage(this.descendreButton, etat);

        this.droiteButton.setEnabled(etat);
        updateButtonImage(this.droiteButton, etat);

        this.gaucheButton.setEnabled(etat);
        updateButtonImage(this.gaucheButton, etat);

        // Boutons de flip
        this.flipArriereButton.setEnabled(etat);
        updateButtonImage(this.flipArriereButton, etat);

        this.flipAvantButton.setEnabled(etat);
        updateButtonImage(this.flipAvantButton, etat);

        this.flipDroitButton.setEnabled(etat);
        updateButtonImage(this.flipDroitButton, etat);

        this.flipGaucheButton.setEnabled(etat);
        updateButtonImage(this.flipGaucheButton, etat);

        this.button90.setEnabled(etat);
        updateButtonImage(this.button90, etat);

        this.buttonMinus90.setEnabled(etat);
        updateButtonImage(this.buttonMinus90, etat);

        this.button180.setEnabled(etat);
        updateButtonImage(this.button180, etat);
    }

	/**
	 * Methode de definir letat du button decollage
	 * @param etat
	 */
	public void setUIDecollage(boolean etat)
	{
		this.decollerButton.setEnabled(etat);
	    updateButtonImage(this.decollerButton, etat);

	}
	
	/**
	 * Methode de definir letat du button atterir
	 * @param etat
	 */
	public void setUIAtterissage(boolean etat)
	{
		this.atterirButton.setEnabled(etat);
	    updateButtonImage(this.atterirButton, etat);

	}
	
	/**
	 * Methode de definir letat du button RobotHaptique
	 * @param etat
	 */
	public void setUIRobotHaptique(boolean etat)
	{
		this.buttonRobotHaptique.setEnabled(etat);
	    updateButtonImage(this.buttonRobotHaptique, etat);

	}
	
	/**
	 * Methode de definir letat du button Sequence
	 * @param etat
	 */
	public void setUISequence(boolean etat)
	{
		this.buttonSequences.setEnabled(etat);
	    updateButtonImage(this.buttonSequences, etat);

	}
	
	/**
	 * Methode de definir letat du button Suivi Du CHar
	 * @param etat
	 */
	public void setUISuiviChar(boolean etat)
	{
		this.buttonSuiviChar.setEnabled(etat);
	    updateButtonImage(this.buttonSuiviChar, etat);

	}
	
	/**
	 * Methode de definir letat du button CasqueVR
	 * @param etat
	 */
	public void setUICasqueVR(boolean etat)
	{
		this.buttonCasqueVR.setEnabled(etat);
	    updateButtonImage(this.buttonCasqueVR, etat);

	}
	
	/**
	 * Getter de la value du Slider Contourtaux
	 * @return value du Slider Contourtaux
	 */
	public int getValueSliderContour()
	{
		return(this.sliderContourVideo.getValue());

	}
	
	/**
	 * Getter de la value du Slider TailleForme
	 * @return value du Slider TailleForme
	 */
	public int getValueSliderTailleForme()
	{
		return(this.sliderTailleForme.getValue());
	}
	
	/**
	 * Methode permettant d afficher une iamge sur les labels
	 * 
	 * @param icon
	 * @param Index_Drone
	 */
	public void setVideoDrone(ImageIcon icon, int Index_Drone) {
		if (Index_Drone == 0) {
			this.lblVideoDrone1.setIcon(icon);
			this.lblVideoDrone1.revalidate();
			this.lblVideoDrone1.repaint();
		} else if (Index_Drone == 1) {
			this.lblVideoDrone2.setIcon(icon);
			this.lblVideoDrone2.revalidate();
			this.lblVideoDrone2.repaint();
		}
	}

	/**
	 * Methode permettant d obtenir la largeur du label video
	 * 
	 * @return
	 */
	public int getLblVideoWidth() {
		return this.lblVideoDrone1.getWidth();
	}

	/**
	 * Methode permettant d obtenir la hauteur du label video
	 * 
	 * @return
	 */
	public int getLblVideoHeight() {
		return this.lblVideoDrone1.getHeight();
	}

	private int DoubleToInt(double value) {
		return ((int) Math.round(value));
	}

	/**
	 * Extinction de l ihm
	 */
	public void StopIHM() {
		this.frame.dispose();
	}

	// ****************** GETTER ET SETTER **************************

	/**
	 * Getter pour l etat du bouton Quitter.
	 * 
	 * @return true si le bouton Quitter est active, false sinon.
	 */
	public boolean getEtatBoutonQuitter() {
		return this.EtatBoutonQuitter;
	}

	/**
	 * Setter pour l etat du bouton Quitter.
	 * 
	 * @param etat etat souhaite du bouton Quitter.
	 */
	public void setEtatBoutonQuitter(boolean etat) {
		this.EtatBoutonQuitter = etat;
	}

	/**
	 * Getter pour l etat du bouton Atterrissage.
	 * 
	 * @return true si le bouton Atterrissage est active, false sinon.
	 */
	public boolean getIsLanding() {
		return this.isLanding;
	}

	/**
	 * Setter pour l etat du bouton Atterrissage.
	 * 
	 * @param etat etat souhaite du bouton Atterrissage.
	 */
	public void setIsLanding(boolean etat) {
		this.isLanding = etat;
	}

	/**
	 * Getter pour l etat du bouton Decollage.
	 * 
	 * @return true si le bouton Decollage est active, false sinon.
	 */
	public boolean getIsTakeOff() {
		return this.isTakeOff;
	}

	/**
	 * Setter pour l etat du bouton Decollage.
	 * 
	 * @param etat etat souhaite du bouton Decollage.
	 */
	public void setIsTakeOff(boolean etat) {
		this.isTakeOff = etat;
	}

	/**
	 * Getter pour l etat du bouton Droite.
	 * 
	 * @return true si le bouton Droite est active, false sinon.
	 */
	public boolean getIsRight() {
		return this.isRight;
	}

	/**
	 * Setter pour l etat du bouton Droite.
	 * 
	 * @param etat etat souhaite du bouton Droite.
	 */
	public void setIsRight(boolean etat) {
		this.isRight = etat;
	}

	/**
	 * Getter pour l etat du bouton Gauche.
	 * 
	 * @return true si le bouton Gauche est active, false sinon.
	 */
	public boolean getIsLeft() {
		return this.isLeft;
	}

	/**
	 * Setter pour l etat du bouton Gauche.
	 * 
	 * @param etat etat souhaite du bouton Gauche.
	 */
	public void setIsLeft(boolean etat) {
		this.isLeft = etat;
	}

	/**
	 * Getter pour l etat du bouton Avant.
	 * 
	 * @return true si le bouton Avant est active, false sinon.
	 */
	public boolean getIsForward() {
		return this.isForward;
	}

	/**
	 * Setter pour l etat du bouton Avant.
	 * 
	 * @param etat etat souhaite du bouton Avant.
	 */
	public void setIsForward(boolean etat) {
		this.isForward = etat;
	}

	/**
	 * Getter pour l etat du bouton Arriere.
	 * 
	 * @return true si le bouton Arriere est active, false sinon.
	 */
	public boolean getIsBackward() {
		return this.isBackward;
	}

	/**
	 * Setter pour l etat du bouton Arriere.
	 * 
	 * @param etat etat souhaite du bouton Arriere.
	 */
	public void setIsBackward(boolean etat) {
		this.isBackward = etat;
	}

	/**
	 * Getter pour l etat du bouton Haut.
	 * 
	 * @return true si le bouton Haut est active, false sinon.
	 */
	public boolean getIsUp() {
		return this.isUp;
	}

	/**
	 * Setter pour l etat du bouton Haut.
	 * 
	 * @param etat etat souhaite du bouton Haut.
	 */
	public void setIsUp(boolean etat) {
		this.isUp = etat;
	}

	/**
	 * Getter pour l etat du bouton Bas.
	 * 
	 * @return true si le bouton Bas est active, false sinon.
	 */
	public boolean getIsDown() {
		return this.isDown;
	}

	/**
	 * Setter pour l etat du bouton Bas.
	 * 
	 * @param etat etat souhaite du bouton Bas.
	 */
	public void setIsDown(boolean etat) {
		this.isDown = etat;
	}

	/**
	 * Getter pour l etat du bouton Sequence.
	 * 
	 * @return true si le bouton Sequence est active, false sinon.
	 */
	public boolean getIsSequence() {
		return this.isSequence;
	}

	/**
	 * Setter pour l etat du bouton Sequence.
	 * 
	 * @param etat etat souhaite du bouton Sequence.
	 */
	public void setIsSequence(boolean etat) {
		this.isSequence = etat;
	}

	/**
	 * @return the flagDetectionContours
	 */
	public boolean isFlagDetectionContours() {
		return flagDetectionContours;
	}

	/**
	 * @param flagDetectionContours the flagDetectionContours to set
	 */
	public void setFlagDetectionContours(boolean flagDetectionContours) {
		this.flagDetectionContours = flagDetectionContours;
	}

	/**
	 * @return the flagDetectionFormes
	 */
	public boolean isFlagDetectionFormes() {
		return flagDetectionFormes;
	}

	/**
	 * @param flagDetectionFormes the flagDetectionFormes to set
	 */
	public void setFlagDetectionFormes(boolean flagDetectionFormes) {
		this.flagDetectionFormes = flagDetectionFormes;
	}

	/**
	 * @return the isButton90
	 */
	public boolean isButton90() {
		return this.isButton90;
	}

	/**
	 * @param isButton90 the isButton90 to set
	 */
	public void setButton90(boolean isButton90) {
		this.isButton90 = isButton90;
	}

	/**
	 * @return the isButtonMinus90
	 */
	public boolean isButtonMinus90() {
		return isButtonMinus90;
	}

	/**
	 * @param isButtonMinus90 the isButtonMinus90 to set
	 */
	public void setButtonMinus90(boolean isButtonMinus90) {
		this.isButtonMinus90 = isButtonMinus90;
	}

	/**
	 * @return the isButton180
	 */
	public boolean isButton180() {
		return this.isButton180;
	}

	/**
	 * @param isButton180 the isButton180 to set
	 */
	public void setButton180(boolean isButton180) {
		this.isButton180 = isButton180;
	}

	/**
	 * @return the isflipArriere
	 */
	public boolean isIsflipArriere() {
		return this.isflipArriere;
	}

	/**
	 * @param isflipArriere the isflipArriere to set
	 */
	public void setIsflipArriere(boolean isflipArriere) {
		this.isflipArriere = isflipArriere;
	}

	/**
	 * @return the isflipDroit
	 */
	public boolean isIsflipDroit() {
		return this.isflipDroit;
	}

	/**
	 * @param isflipDroit the isflipDroit to set
	 */
	public void setIsflipDroit(boolean isflipDroit) {
		this.isflipDroit = isflipDroit;
	}

	/**
	 * @return the isflipAvant
	 */
	public boolean isIsflipAvant() {
		return this.isflipAvant;
	}

	/**
	 * @param isflipAvant the isflipAvant to set
	 */
	public void setIsflipAvant(boolean isflipAvant) {
		this.isflipAvant = isflipAvant;
	}

	/**
	 * @return the isflipGauche
	 */
	public boolean isIsflipGauche() {
		return this.isflipGauche;
	}

	/**
	 * @param isflipGauche the isflipGauche to set
	 */
	public void setIsflipGauche(boolean isflipGauche) {
		this.isflipGauche = isflipGauche;
	}

	/**
	 * @return the isRobotHaptique
	 */
	public boolean getIsRobotHaptique() {
		return this.isRobotHaptique;
	}

	/**
	 * @param isRobotHaptique the isRobotHaptique to set
	 */
	public void setRobotHaptique(boolean isRobotHaptique) {
		this.isRobotHaptique = isRobotHaptique;
	}

	/**
	 * @return the isCasqueVR
	 */
	public boolean isCasqueVR() {
		return this.isCasqueVR;
	}

	/**
	 * @param isCasqueVR the isCasqueVR to set
	 */
	public void setCasqueVR(boolean isCasqueVR) {
		this.isCasqueVR = isCasqueVR;
	}
	
	/**
	 * Methode permettant de definir le texte et la couleur de celui au sein du labelEtatCommAndroid
	 * @param text
	 * @param _color
	 */
	public void SetTextEtatCommAndroid(String text, Color _color)
	{
		this.labelEtatCommAndroid.setText("Etat Serveur Android: "+text);
		this.labelEtatCommAndroid.setForeground(_color);
	}
}