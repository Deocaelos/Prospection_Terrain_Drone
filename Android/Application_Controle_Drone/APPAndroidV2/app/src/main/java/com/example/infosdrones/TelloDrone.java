package com.example.infosdrones;

import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * @author Ebbe Vang
 * @original version 0.1
 *
 * @Desciption Cette classe se connecte à un drone Tello en utilisant une connexion UDP et envoie des commandes au drone.
 * Elle prend en charge toutes les commandes de la SDK 2.0.
 *
 * @Part of TelloDroneConsoleVideo_v1.0
 * @Modification Wilfrid Grassi
 * @Date 13/09/2023
 *
 * @version 1.1 Eleve
 *
 */
public class TelloDrone
{
    private final int udpPortCommand = 8889;
    private final int udpPortTelemetrie = 8890;
    private final int udpPortVideoStream = 11111;
    private String strIPAddress = "192.168.1.10";
    private static DatagramSocket socketCommand = null;
    private DatagramSocket socketTelemetrie = null;
    private DatagramSocket socketVideo = null;
    private InetAddress IPAddress;
    private static boolean isConnected = false;
    private boolean logToConsole = true;
    private boolean streamOn = false;
    private int imageCounter = 0;
    private int index = 0;

    public enum Flip
    {
        LEFT, RIGHT, FORWARD, BACK, BACKLEFT, BACKRIGHT, FRONTLEFT, FRONTRIGHT
    }

    /**
     * Constructeur
     */
    public TelloDrone(int _index)
    {
        this.index = _index;
        log("Initialisation du Drone");
    }

    /**
     * Se connecte au drone en utilisant la commande 'command' et vous permet d'envoyer d'autres commandes
     *
     * @return Si le drone est connecté ou non
     */
    public synchronized boolean connect()
    {
        try
        {
            log("Connexion au drone en cours...");
            this.IPAddress = InetAddress.getByName(this.strIPAddress);
            if(TelloDrone.socketCommand == null)
            {
                TelloDrone.socketCommand = new DatagramSocket(this.udpPortCommand);
                System.out.println("je suis la");
            }

            this.socketTelemetrie = new DatagramSocket(this.udpPortTelemetrie);
            this.socketVideo = new DatagramSocket(this.udpPortVideoStream);
            sendMessage("command");
            //log(receiveMessage());
            if (ok())
            {
                this.isConnected = true;
                log("Drone connecte.");
                return true;

            }
            log("Impossible de se connecter au drone!!!");
            return false;
        } catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Se connecte au drone en utilisant la commande 'command' et permet d'envoyer d'autres commandes
     *
     * @param _Addr
     * 			_Addr : addresse du drone
     * @return Si le drone est connecté ou non
     */
    public synchronized boolean connect(String _Addr)
    {
        this.strIPAddress = _Addr;
        try
        {
            log("Connexion au drone en cours...");
            this.IPAddress = InetAddress.getByName(this.strIPAddress);
            if(TelloDrone.socketCommand == null)
            {
                TelloDrone.socketCommand = new DatagramSocket(this.udpPortCommand);
            }

            //log(receiveMessage());
            for(int i = 0; i<10; i++)
            {
                System.out.println("Tentative de Connexion: " + i);
                this.sendMessage("command");
                new Tempo(20);
            }

            // Envoyer la commande de changement numero des ports telemetrie et video
            if(this.index > 0)
            {
                this.sendMessage("port "+(int)(this.udpPortTelemetrie + this.index)+" "+ (int)(this.udpPortVideoStream + this.index));
                if (!this.ok())
                    return false;
            }

            if (this.socketTelemetrie == null)
            {
                this.socketTelemetrie = new DatagramSocket(this.udpPortTelemetrie + this.index);
            }
            this.isConnected = true;

            if (this.socketVideo == null) {
                this.socketVideo = new DatagramSocket(this.udpPortVideoStream + this.index);
            }
            return true;

        } catch (Exception e)
        {}
        return false;
    }

    public void Close()
    {
        if(this.streamOn)
        {
            this.streamOff();
        }
        new Tempo(100);

        this.reboot();
    }

    /**
     * The drone reboot
     *
     * @return whether the command is accepted or not
     */
    public synchronized void reboot()
    {
        this.sendCommand("reboot");
        System.out.println("Reboot du Drone");
    }

    private boolean ok()
    {
        //String ret = receiveMessage();
        return receiveMessage().equals("ok\u0000\u0000\u0000");
        //return true;
    }

    public static boolean IsConnected()
    {
        return isConnected;
    }

    /**
     * Envoyez n'importe quelle commande au drone
     * consultez le SDK pour plus de détails sur les commandes que vous pouvez envoyer au drone
     *
     * //@param Commande que vous souhaitez envoyer au drone
     * @return Si la commande est acceptée ou non
     */
    private boolean sendMessage(String command)
    {
        try
        {
            byte[] sendData = command.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, this.IPAddress, this.udpPortCommand);
            this.socketCommand.send(sendPacket);
            return true;
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendCommand(String command)
    {
        sendMessage(command);
        if (ok())
        {
            log("command \"" +  command + "\" accepted");
            return true;
        }
        else
        {
            log("command \"" + command + "\" failed");
            return false;
        }
    }

    public String receiveMessage() // attention j'ai changer private en public mais je sais pas si sa aura un impact plus tard
    {
        byte[] receiveData = new byte[5];
        DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
        try
        {
            this.socketCommand.receive(packet);
        } catch (IOException e)
        {
            return "Erreur de communication avec le drone!!!";
        }
        return new String(packet.getData());
    }
    /**
     * Send status message
     *
     * param command status message
     * ex all status :
     * "pitch:%d;roll:%d;yaw:%d;vgx:%d;vgy%d;vgz:%d;templ:%d;temph:%d;tof:%d;h:%d;bat:%d;baro:%.2f; time:%d;agx:%.2f;agy:%.2f;agz:%.2f;\r\n"
     *
     * return whether the drone is connected or not
     */
    private boolean SendStatusMessage(String data)
    {

        byte[] sendData = data.getBytes();
        //System.out.println("SendStatusMessage (" + this.index + "): " + this.IP + "  udpPortStatus: " + this.udpPortStatus);
        final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, this.IPAddress, this.udpPortTelemetrie);

        try
        {
            this.socketTelemetrie.send(sendPacket);
            return true;
        } catch (IOException e)
        {
            return false;
        }
    }

    /**
     * Reception des donnees telemetriques
     *
     * @return Les donnees telemetriques
     */

    public String receiveTelemetrie() // attention j'ai chndanger private en public mais je sais pas si sa aura un impact plus tard
    {
        boolean ret = SendStatusMessage("pitch:%d;roll:%d;yaw:%d;vgx:%d;vgy%d;vgz:%d;templ:%d;temph:%d;tof:%d;h:%d;bat:%d;baro:%.2f; time:%d;agx:%.2f;agy:%.2f;agz:%.2f;\r\n");

        byte[] receiveData = new byte[1024];
        DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
        try
        {
            this.socketTelemetrie.receive(packet);
        } catch (IOException e)
        {
            return "Erreur de communication avec le drone!!!";
        }
        return new String(packet.getData());
    }

    /**
     * Le drone décolle - Le drone montera d'environ 1 mètre
     *
     * @return Si la commande est acceptée ou non
     */
    public synchronized boolean takeoff()
    {
        sendMessage("takeoff");
        if (ok())
        {
            log("Commande takeoff [Ok]");
            return true;
        }
        log("Commande Take off [Erreur!!!]");
        return false;
    }

    /**
     * Le drone atterrit
     *
     * @return Si la commande est acceptée ou non
     */
    public synchronized boolean land()
    {
        sendMessage("land");
        if (ok())
        {
            log("Commande demande d'atterissage du Drone [Ok]");
            return true;
        }
        log("Commande demande d'atterissage du Drone [Erreur!!!]");
        return false;
    }

    /**
     * Faire descendre le drone
     *
     * @param //Distance en cm
     * @return Si la commande est acceptée ou non
     */
    public synchronized boolean goDown(int cm)
    {
        return move("down", cm);
    }

    /**
     * Faire monter le drone
     *
     * @param //Distance en cm
     * @return Si la commande est acceptée ou non
     */
    public synchronized boolean goUp(int cm)
    {
        return move("up", cm);
    }

    /**
     * Faire déplacer le drone vers la gauche
     *
     * @param //Distance en cm
     * @return Si la commande est acceptée ou non
     */
    public synchronized boolean goLeft(int cm)
    {
        return move("left", cm);
    }

    /**
     * Faire déplacer le drone vers la droite
     *
     * @param //Distance en cm
     * @return Si la commande est acceptée ou non
     */
    public synchronized boolean goRight(int cm)
    {
        return move("right", cm);
    }

    /**
     * Faire avancer le drone
     *
     * @param //Distance en cm
     * @return Si la commande est acceptée ou non
     */
    public synchronized boolean goForward(int cm)
    {
        return move("forward", cm);
    }

    /**
     * Faire reculer le drone
     *
     * @param //Distance en cm
     * @return Si la commande est acceptée ou non
     */
    public synchronized boolean goBackwards(int cm)
    {
        return move("back", cm);
    }

    public boolean move(String direction, int cm)
    {
        if (cm >= 20 && cm <= 500)
        {
            sendMessage(direction + " " + cm);
            if (ok())
            {
                log("Commande deplacement du drone " + direction + ": " + cm + "cm.   [Ok]");
                return true;
            }
            log("Commande deplacement du drone " + direction + ": " + cm + "cm.   [Erreur!!!]");
            return false;
        }
        log("Commande deplacement du drone " + direction + " (les valeurs doivent etres entre 20cm and 500cm)");
        return false;
    }

    /**
     * Lancer la diffusion vidéo depuis la caméra avant
     * @return Si la commande est acceptée ou non
     */
    public boolean streamOn()
    {
        if (this.streamOn)
        {
            log("Impossible de lancer le stream video - Le stream est deja lance!!!");
            return false;
        }

        sendMessage("streamon");
        if (ok())
        {
            log("Stream video actif.");
            this.streamOn = true;
            return true;
        }
        log("Impossible de lancer le stream video!!!");
        return false;
    }

    /**
     * Arrêter la diffusion depuis la caméra avant
     * @return
     */
    public boolean streamOff()
    {
        if (!this.streamOn)
        {
            log("Impossible d'arreter le stream video - Le stream video est deja arrete!!!");
            return false;
        }

        sendMessage("streamoff");
        if (ok())
        {
            log("Stream video arrete.");
            return true;
        }
        log("Impossible d'arreter le stream video!!!");
        return false;
    }

    /**
     * Le drone tournera dans le sens des aiguilles d'une montre
     *
     * @param //Degrés que vous souhaitez que le drone tourne - doit être compris entre 0 et 360
     * @return Si la commande est acceptée ou non
     */
    public synchronized boolean rotateClockwise(int degrees)
    {
        if (degrees >= 1 && degrees <= 360)
        {
            sendMessage("cw " + degrees);
            if (ok())
            {
                log("Commande de rotation du drone de " + degrees + "° dans le sens horaire [Ok]");
                return true;
            }
        }
        log("Commande de rotation du drone dans le sens horaire [Erreur!!!]");
        return false;
    }

    /**
     * Le drone tournera dans le sens inverse des aiguilles d'une montre
     *
     * @param //Degrés que vous souhaitez que le drone tourne - doit être compris entre 0 et 360
     * @return Si la commande est acceptée ou non
     */
    public synchronized boolean rotateCounterClockwise(int degrees)
    {
        if (degrees >= 1 && degrees <= 360)
        {
            sendMessage("ccw " + degrees);
            if (ok())
            {
                log("Commande de rotation du drone de " + degrees + "° dans le sens anti-horaire [Ok]");
                return true;
            }
        }
        log("Commande de rotation du drone dans le sens anti-horaire [Erreur!!!]");
        return false;
    }

    /**
     * status des log.
     *
     * @return Si le drone écrit ou non sur la console
     */
    public boolean isLogToConsole()
    {
        return this.logToConsole;
    }

    /**
     * Arrête les moteurs immédiatement!*
     *
     * @return
     */
    public synchronized boolean emergency()
    {
        sendMessage("streamoff");
        if (ok())
        {
            log("Commande d'arret du stream video [Ok]");
            return true;
        }
        log("Commande d'arret du stream video [Erreur!!!]");
        return emergency();
    }

    /**
     * Activer ou désactiver l'enregistrement sur la console
     *
     * @param logToConsole : Enregistrer sur la console
     */
    public void setLogToConsole(boolean logToConsole)
    {
        this.logToConsole = logToConsole;
    }

    private void log(String message)
    {
        if (this.logToConsole)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                System.out.print("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS")) + "]");
            }
            System.out.println(message);
        }
    }

    /**
     * Capturer une image depuis la caméra avant. L'image sera enregistrée dans le dossier 'grabbedImages'
     *
     * @return Numéro de l'image - utilisé comme nom de fichier
     */
    public int grabImage()
    {
        try
        {
            this.imageCounter++;
            String command = "ffmpeg -i udp://"+ this.strIPAddress + ":" + this.udpPortVideoStream + " -vframes 1 -q:v 2 -nostats -loglevel 0 grabbedImages/" + String.format("%04d", this.imageCounter) + ".jpg";
            log("Capture de l'image en cours...");
            Process proc = Runtime.getRuntime().exec(command);
            proc.waitFor();

            // Utilisez la méthode log pour imprimer le numéro du nom de l'image capturée
            /** <code> ?????????????????????????????? </code>*/

            if (new File("grabbedImages/" + String.format("%04d", this.imageCounter) + ".jpg").exists()) return this.imageCounter;
            else return -1;
        } catch (Exception e)
        {
            log("Erreur lors de la capture de l'images!!! [" + e.getMessage() + "]");
            this.imageCounter--;
            return -1;
        }
    }

    /**
     * Obtenez l'URL de la dernière image prise par la méthode grabbedImage
     *
     * @return URL de l'image sous forme de chaîne
     */
    public String getGrabbedImageURL()
    {
        if (this.imageCounter == 0) return null;
        else return "Capture de l'image: grabbedImages/" + String.format("%04d", this.imageCounter) + ".jpg";
    }

    /**
     * Définir la vitesse du drone
     *
     * //@param vitesse en cm/s (1-100)
     * @return Si la vitesse est définie ou non
     */
    public synchronized boolean setSpeed(int speed)
    {
        if (speed >= 1 && speed <= 100)
        {
            sendMessage("speed " + speed);
            if (ok())
            {
                log("Commande definition de la vitesse: " + speed + "cm/s [Ok]");
                return true;
            }
            log("Commande  definition de la vitesse: " + speed + "cm/s [Erreur!!!]");
            return false;
        }
        log(" Commande  definition de la vitesse [Erreur!!!] (valeur de vitesse entre 1 et 100)");
        return false;
    }

    /**
     *  Obtenir la vitesse du drone en cm/s
     *
     * @return vitesse en cm/s (1-100)
     */
    public double getSpeed()
    {
        sendMessage("speed?");
        String speed = receiveMessage();
        try
        {
            double doubleSpeed = Double.parseDouble(speed);
            return doubleSpeed;
        } catch (NumberFormatException e)
        {
            log("Commande de lecture de la vitesse [Erreur!!!]");
            return -1;
        }
    }

    /**
     * Obtenir le niveau de charge de la batterie en pourcentage
     *
     * @return Charge de la batterie en pourcentage (1-100)
     */
    public int getBatteryPercentage()
    {
        sendMessage("battery?");
        String battery = receiveMessage();
        battery = battery.substring(0, battery.length()-3);
        log("Commande niveau de la batterie " + battery + "%");
        try
        {
            int intBat = Integer.parseInt(battery);
            return intBat;
        } catch (NumberFormatException e)
        {
            log("Commande niveau de la batterie [Erreur!!!]");
            return -1;
        }
    }

    /**
     * Obtenir le temps que le drone a passé dans les airs
     *
     * @return temps en vol
     */
    public String getTime()
    {
        sendMessage("speed?");
        return receiveMessage();
    }
    public class Command
    {
        private String command;
        private String reply;

        public Command(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }

        public String getReply() {
            return reply;
        }

        public void setReply(String reply){
            this.reply = reply;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("command: \t");
            builder.append(command);
            builder.append(System.getProperty("line.separator"));
            builder.append("reply \t\t");
            builder.append(reply);
            builder.append(System.getProperty("line.separator"));
            return builder.toString();
        }
    }

    public static interface DroneCommandEventListener {
        void commandExecuted(Command command);

        void commandFinished(Command command);

        void commandAdded(Command command);

        void commandQueueFinished();
    }
}