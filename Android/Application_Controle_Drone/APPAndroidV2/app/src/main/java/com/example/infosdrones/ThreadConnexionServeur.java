package com.example.infosdrones;


import android.content.Intent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Abayev Ahmed
 *
 * @Description cree un thread qui permet de se connecter a un drone volant
 *
 * @date: 04/02/2025
 * @Modification 14/04/2025
 *
 * @version: 2
 *
 */

public class ThreadConnexionServeur extends Thread
{
    private boolean FlagThreadConnexionDroneEnd = false, isConnected = false;
    private MainActivity monActivity = null;
    private CommandTelemetrieActivity ActivityCommand = null;
    private Thread_ReceptionTelemetrie[] mesThreadReceptionTelemetrie = null;
    private Thread_EnvoiCommande monThread_EnvoiCommande = null;
    private Thread_Gyroscope monThreadGyro = null;
    private String AddressServeur,TrameServeur;
    private Socket socketTCP_CommAndroid = null;
    private BufferedReader In_CommAndroid = null;
    private PrintWriter Out_CommAndroid = null;
    private int PortServeur = 5117;

    public ThreadConnexionServeur(MainActivity _MainActivity,String _IPServeur)
    {
        this.monActivity = _MainActivity;
        this.AddressServeur = _IPServeur;
        this.mesThreadReceptionTelemetrie = new Thread_ReceptionTelemetrie[2];
    }

    public void run()
    {
        try {
            this.socketTCP_CommAndroid = new Socket(this.AddressServeur,this.PortServeur);
            this.In_CommAndroid = new BufferedReader(new InputStreamReader(this.socketTCP_CommAndroid.getInputStream()));
            this.Out_CommAndroid = new PrintWriter(this.socketTCP_CommAndroid.getOutputStream(),true);
            while(!this.FlagThreadConnexionDroneEnd)
            {
                if(!this.isConnected && (this.TrameServeur = this.In_CommAndroid.readLine()) != null)
                {
                    if(this.TrameServeur.equals("ack_connexion"))
                    {
                        this.monActivity.log("Connexion au Serveur Reussi");
                        this.monActivity.GoActivityCommand();
                        this.isConnected = true;
                    }
                }
                if(this.isConnected)
                {
                    if(this.ActivityCommand != null)
                    {
                        new Tempo(10000);
                        if(this.mesThreadReceptionTelemetrie[0] == null && this.mesThreadReceptionTelemetrie[1] == null)
                        {

                            for(int i = 0; i< this.mesThreadReceptionTelemetrie.length; i++)
                            {
                                this.mesThreadReceptionTelemetrie[i] = new Thread_ReceptionTelemetrie(i+1,this.ActivityCommand);
                                this.mesThreadReceptionTelemetrie[i].start();
                            }


                        }

                        if(this.monThread_EnvoiCommande == null)
                        {
                            this.monThread_EnvoiCommande = new Thread_EnvoiCommande(this.In_CommAndroid,this.Out_CommAndroid,this.socketTCP_CommAndroid,this.ActivityCommand);
                            this.monThread_EnvoiCommande.start();
                        }

                        if(this.monThreadGyro == null)
                        {
                            this.monThreadGyro = new Thread_Gyroscope(this.ActivityCommand,this.Out_CommAndroid, this.In_CommAndroid);
                            this.monThreadGyro.start();
                        }

                        //Gestion de la deconnexion
                        if(this.monThread_EnvoiCommande != null && !this.monThread_EnvoiCommande.isAlive())
                        {
                            this.ActivityCommand.log("Fin du Thread Envoi COmmande");
                            new Tempo(5000);
                            for(int i = 0; i< this.mesThreadReceptionTelemetrie.length; i++)
                            {
                                this.mesThreadReceptionTelemetrie[i].StopThread();
                                while(this.mesThreadReceptionTelemetrie[i].isAlive())
                                {
                                    new Tempo(10);
                                }
                                this.mesThreadReceptionTelemetrie[i] = null;
                            }
                            if(this.monThreadGyro != null)
                            {
                                this.monThreadGyro.stopThread();
                            }
                            if(this.Out_CommAndroid != null)
                            {
                                this.Out_CommAndroid.close();
                                this.Out_CommAndroid = null;
                            }
                            if(this.In_CommAndroid != null)
                            {
                                this.In_CommAndroid.close();
                                this.In_CommAndroid = null;
                            }
                            if(this.socketTCP_CommAndroid != null && !this.socketTCP_CommAndroid.isClosed())
                            {
                                this.socketTCP_CommAndroid.close();
                                this.socketTCP_CommAndroid = null;
                            }
                            this.ActivityCommand.GoActivityConnexion();
                            this.ActivityCommand = null;

                            this.isConnected = false;

                            this.FlagThreadConnexionDroneEnd = true;

                        }
                    }
                }

                new Tempo(10);
            }
            System.gc();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    //##################################################################//
    // Setter ActivityCommand            		                        //
    //##################################################################//
    public void setActivityCommand(CommandTelemetrieActivity _ActivityCommand)
    {
        this.ActivityCommand = _ActivityCommand;
    }

    //##################################################################//
    // Methode appelee pour la fermeture du thread              		//
    //##################################################################//
    public void Close()
    {
        this.FlagThreadConnexionDroneEnd = true;
        new Tempo(100);
    }

}
