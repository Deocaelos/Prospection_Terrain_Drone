package com.example.infosdrones;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Abayev Ahmed/ RIAS Julien/ MONTOYA Daryl
 *
 * @Desciption Thread permettant l envoi des commandes a l application java centrale
 *
 * @date: 26/05/25
 * @Modification 26/05/25
 *
 * @version: 2
 *
 */
public class Thread_EnvoiCommande extends Thread
{
    private boolean flagEndThread_EnvoiCommande = false;
    private CommandTelemetrieActivity ActivityCommand = null;
    private Socket socketTCP_CommAndroid = null;
    private BufferedReader In_CommAndroid = null;
    private PrintWriter Out_CommAndroid = null;

    public Thread_EnvoiCommande(BufferedReader _BufferedReader, PrintWriter _PrintWriter, Socket _Socket, CommandTelemetrieActivity _CommandTelemetrieActivity)
    {
        this.socketTCP_CommAndroid = _Socket;
        this.In_CommAndroid = _BufferedReader;
        this.Out_CommAndroid = _PrintWriter;
        this.ActivityCommand = _CommandTelemetrieActivity;
    }

    public void run()
    {
        while(!this.flagEndThread_EnvoiCommande)
        {
            if(this.ActivityCommand.isDeconnexion())
            {
                this.ActivityCommand.log("Deconnexion du serveur");
                this.flagEndThread_EnvoiCommande = true;
            }
            if(this.ActivityCommand.isTakeOff())
            {
                this.ActivityCommand.log("Decolage des drones");
                this.Out_CommAndroid.println("Takeoff");
                this.getACK();
                this.ActivityCommand.setTakeOff(false);
            }
            if(this.ActivityCommand.isLand())
            {
                this.ActivityCommand.log("Atterrissage des drones");
                this.Out_CommAndroid.println("Land");
                this.getACK();
                this.ActivityCommand.setLand(false);
            }
            if(this.ActivityCommand.isForward())
            {
                this.ActivityCommand.log("Commande: Avancer");
                this.Out_CommAndroid.println("Forward");
                this.getACK();
                this.ActivityCommand.setForward(false);
            }
            if(this.ActivityCommand.isBackward())
            {
                this.ActivityCommand.log("Commande: Reculer");
                this.Out_CommAndroid.println("Backward");
                this.getACK();
                this.ActivityCommand.setBackward(false);
            }
            if(this.ActivityCommand.isRight())
            {
                this.ActivityCommand.log("Commande: Aller a droite");
                this.Out_CommAndroid.println("Right");
                this.getACK();
                this.ActivityCommand.setRight(false);
            }
            if(this.ActivityCommand.isLeft())
            {
                this.ActivityCommand.log("Commande: Aller a Gauche");
                this.Out_CommAndroid.println("Left");
                this.getACK();
                this.ActivityCommand.setLeft(false);
            }
            if(this.ActivityCommand.isUp())
            {
                this.ActivityCommand.log("Commande: Monter");
                this.Out_CommAndroid.println("Up");
                this.getACK();
                this.ActivityCommand.setUp(false);
            }
            if(this.ActivityCommand.isDown())
            {
                this.ActivityCommand.log("Commande: Descendre");
                this.Out_CommAndroid.println("Down");
                this.getACK();
                this.ActivityCommand.setDown(false);
            }
            if(this.ActivityCommand.isRotateClockWise())
            {
                this.ActivityCommand.log("Commande: Tourner dans le sens des aiguilles");
                this.Out_CommAndroid.println("Turn/"+this.ActivityCommand.getCurrentRotationAngle());
                this.getACK();
                this.ActivityCommand.setRotateClockWise(false);
            }
            if(this.ActivityCommand.isRotateCounterClockWise())
            {
                this.ActivityCommand.log("Commande: Tourner dans le sens contraire des aiguilles");
                this.Out_CommAndroid.println("Turn/-"+this.ActivityCommand.getCurrentRotationAngle());
                this.getACK();
                this.ActivityCommand.setRotateCounterClockWise(false);
            }
            new Tempo(50);
        }
        System.gc();
    }

    //##################################################################//
    // Getter Accuse Reception             		                        //
    //##################################################################//
    private void getACK()
    {
        String reponse;
        do
        {
            try {
                reponse = this.In_CommAndroid.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }while(!reponse.equals("ack_commande"));
    }

}
