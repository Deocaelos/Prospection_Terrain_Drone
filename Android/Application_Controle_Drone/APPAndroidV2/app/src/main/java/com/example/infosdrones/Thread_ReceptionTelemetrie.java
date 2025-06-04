package com.example.infosdrones;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 *
 * @author Abayev Ahmed
 *
 * @Desciption cree un thread qui permet de recuperer les donnees telemetrique
 *
 * @date: 04/02/2025
 * @Modification 14/04/2025
 *
 * @version: 2
 *
 */

public class Thread_ReceptionTelemetrie extends Thread
{
    private int index = 0;
    private boolean flagThread_command_end = false;
    private CommandTelemetrieActivity mesDrones = null;
    private String[] ValeursTeleEnOrdreFinale = new String[16];
    private String DonneesRecues;

    private DatagramSocket socket_Telemetrie = null;
    private DatagramPacket packet_Telemetrie = null;

    public Thread_ReceptionTelemetrie(int _index, CommandTelemetrieActivity _ActivityCommand)
    {
        this.index = _index;
        this.mesDrones = _ActivityCommand;
    }

    /**
     * Methode executee pour la fermeture du thread
     */
    public void Close()
    {
        this.flagThread_command_end = true;
        new Tempo(100);
        this.interrupt();
    }

    public void run()
    {
        try {
            this.socket_Telemetrie = new DatagramSocket(8890+this.index);
            this.packet_Telemetrie = new DatagramPacket(new byte[256],256);
        } catch (SocketException e) {
        }
        while(!this.flagThread_command_end)
        {
            try {
                this.socket_Telemetrie.receive(this.packet_Telemetrie);
            } catch (IOException e) {
            }
            this.DonneesRecues = new String(this.packet_Telemetrie.getData(),0,this.packet_Telemetrie.getLength());
            this.ValeursTeleEnOrdreFinale = this.TraitementDonnees(this.DonneesRecues);
            if (this.index == 1)
            {
                // modification textview avec l'unite et la valeur
                mesDrones.settextViewHUniteDrone1(this.ValeursTeleEnOrdreFinale[6]);
                mesDrones.settextViewTimeUniteDrone(this.ValeursTeleEnOrdreFinale[8]);
                mesDrones.settextViewBatUniteDrone1(this.ValeursTeleEnOrdreFinale[7]);

                if ((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[0]) >= 0) // progressBar AgX
                {
                    mesDrones.setAgXDrone1Moins(0);
                    mesDrones.setAgXDrone1Plus((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[0]));
                }else
                {
                    mesDrones.setAgXDrone1Plus(0);
                    mesDrones.setAgXDrone1Moins((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[0]));
                }

                if ((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[1]) >= 0) // progressBar AgY
                {
                    mesDrones.setAgYDrone1Moins(0);
                    mesDrones.setAgYDrone1Plus((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[1]));
                }else
                {
                    mesDrones.setAgYDrone1Plus(0);
                    mesDrones.setAgYDrone1Moins((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[1]));
                }

                if ((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[2]) >= 0) // progressBar AgZ
                {
                    mesDrones.setAgZDrone1Moins(0);
                    mesDrones.setAgZDrone1Plus((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[2]));
                }else
                {
                    mesDrones.setAgZDrone1Plus(0);
                    mesDrones.setAgZDrone1Moins((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[2]));
                }

                if ((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[3]) >= 0) // progressBar VgX
                {
                    mesDrones.setVgXDrone1Moins(0);
                    mesDrones.setVgXDrone1Plus((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[3]));
                }else
                {
                    mesDrones.setVgXDrone1Plus(0);
                    mesDrones.setVgXDrone1Moins((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[3]));
                }

                if ((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[4]) >= 0) // progressBar Vgy
                {
                    mesDrones.setVgYDrone1Moins(0);
                    mesDrones.setVgYDrone1Plus((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[4]));
                }else
                {
                    mesDrones.setVgYDrone1Plus(0);
                    mesDrones.setVgYDrone1Moins((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[4]));
                }

                if ((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[5]) >= 0) // progressBar VgZ
                {
                    mesDrones.setVgZDrone1Moins(0);
                    mesDrones.setVgZDrone1Plus((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[5]));
                }else
                {
                    mesDrones.setVgZDrone1Plus(0);
                    int Test = (int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[5]);
                    mesDrones.setVgZDrone1Moins((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[5]));
                }

                // progressbar valeur H
                mesDrones.setHDrone1((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[6]));

                // progressbar valeur Pitch Roll Yaw degres
                if ((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[9]) >= 0)
                {
                    mesDrones.setPitchDrone1Moins(0);
                    mesDrones.setPitchDrone1Plus((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[9]));
                }else
                {
                    mesDrones.setPitchDrone1Plus(0);
                    mesDrones.setPitchDrone1Moins((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[9]));
                }

                if ((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[10]) >= 0)
                {
                    mesDrones.setRollDrone1Moins(0);
                    mesDrones.setRollDrone1Plus((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[10]));
                }else
                {
                    mesDrones.setRollDrone1Plus(0);
                    mesDrones.setRollDrone1Moins((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[10]));
                }

                if ((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[11]) >= 0)
                {
                    mesDrones.setYawDrone1Moins(0);
                    mesDrones.setYawDrone1Plus((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[11]));
                }else
                {
                    mesDrones.setYawDrone1Plus(0);
                    mesDrones.setYawDrone1Moins((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[11]));
                }

                // progressbar valeur Bat
                mesDrones.setBatDrone1((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[7]));
            }
            else if (this.index == 2)
            {
                // modification textview avec l'uniter et la valeur

                mesDrones.settextViewHUniteDrone2(this.ValeursTeleEnOrdreFinale[6]);
                mesDrones.settextViewTimeUniteDrone(this.ValeursTeleEnOrdreFinale[8]);
                mesDrones.settextViewBatUniteDrone2(this.ValeursTeleEnOrdreFinale[7]);

                if ((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[0]) >= 0) // progressBar AgX
                {
                    mesDrones.setAgXDrone2Moins(0);
                    mesDrones.setAgXDrone2Plus((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[0]));
                }else
                {
                    mesDrones.setAgXDrone2Plus(0);
                    mesDrones.setAgXDrone2Moins((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[0]));
                }

                if ((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[1]) >= 0) // progressBar AgY
                {
                    mesDrones.setAgYDrone2Moins(0);
                    mesDrones.setAgYDrone2Plus((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[1]));
                }else
                {
                    mesDrones.setAgYDrone2Plus(0);
                    mesDrones.setAgYDrone2Moins((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[1]));
                }

                if ((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[2]) >= 0) // progressBar AgZ
                {
                    mesDrones.setAgZDrone2Moins(0);
                    mesDrones.setAgZDrone2Plus((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[2]));
                }else
                {
                    mesDrones.setAgZDrone2Plus(0);
                    mesDrones.setAgZDrone2Moins((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[2]));
                }

                if ((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[3]) >= 0) // progressBar VgX
                {
                    mesDrones.setVgXDrone2Moins(0);
                    mesDrones.setVgXDrone2Plus((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[3]));
                }else
                {
                    mesDrones.setVgXDrone2Plus(0);
                    mesDrones.setVgXDrone2Moins((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[3]));
                }

                if ((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[4]) >= 0) // progressBar Vgy
                {
                    mesDrones.setVgYDrone2Moins(0);
                    mesDrones.setVgYDrone2Plus((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[4]));
                }else
                {
                    mesDrones.setVgYDrone2Plus(0);
                    mesDrones.setVgYDrone2Moins((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[4]));
                }

                if ((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[5]) >= 0) // progressBar VgZ
                {
                    mesDrones.setVgZDrone2Moins(0);
                    int Test = (int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[5]);
                    mesDrones.setVgZDrone2Plus((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[5]));
                }else
                {
                    mesDrones.setVgZDrone2Plus(0);
                    mesDrones.setVgZDrone2Moins((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[5]));
                }

                // progressbar valeur H
                mesDrones.setHDrone2((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[6]));

                // progressbar valeur Pitch Roll Yaw degres
                if ((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[9]) >= 0)
                {
                    mesDrones.setPitchDrone2Moins(0);
                    mesDrones.setPitchDrone2Plus((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[9]));
                }else
                {
                    mesDrones.setPitchDrone2Plus(0);
                    mesDrones.setPitchDrone2Moins((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[9]));
                }

                if ((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[10]) >= 0)
                {
                    mesDrones.setRollDrone2Moins(0);
                    mesDrones.setRollDrone2Plus((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[10]));
                }else
                {
                    mesDrones.setRollDrone2Plus(0);
                    mesDrones.setRollDrone2Moins((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[10]));
                }

                if ((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[11]) >= 0)
                {
                    mesDrones.setYawDrone2Moins(0);
                    mesDrones.setYawDrone2Plus((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[11]));
                }else
                {
                    mesDrones.setYawDrone2Plus(0);
                    mesDrones.setYawDrone2Moins((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[11]));
                }

                // progressbar valeur Bat
                mesDrones.setBatDrone2((int)Double.parseDouble(this.ValeursTeleEnOrdreFinale[7]));
            }
            new Tempo(10);
        }
        System.gc();
    }


    public void StopThread()
    {
        this.flagThread_command_end = true;
        if(!this.socket_Telemetrie.isClosed())
        {
            this.socket_Telemetrie.close();
        }
    }

    /**
     * Methode permettant de traiter les donnees recues
     * @param _Donnees
     * @return
     */
    private String[] TraitementDonnees(String _Donnees)
    {
        String[] ValeursTele = new String[2];
        String[] ValeursTeleEnOrdre = new String[12];
        String[] TabValeur = _Donnees.split(";");				// separe les donnees grace au ;
        for(int i = 0; i < TabValeur.length; i++)
        {
            ValeursTele = TabValeur[i].split(":");				// on refait une separation avec : pour separer la valeur et le nom de la valeur ( ex: pitch:0 -> on garde juste la valeur)
            switch(ValeursTele[0])
            {
                case "vx":
                    ValeursTeleEnOrdre[3] = ValeursTele[1];
                    break;
                case "vy":
                    ValeursTeleEnOrdre[4] = ValeursTele[1];
                    break;
                case "vz":
                    ValeursTeleEnOrdre[5] = ValeursTele[1];
                    break;
                case "ax":
                    ValeursTeleEnOrdre[0] = ValeursTele[1];
                    break;
                case "ay":
                    ValeursTeleEnOrdre[1] = ValeursTele[1];
                    break;
                case "az":
                    ValeursTeleEnOrdre[2] = ValeursTele[1];
                    break;
                case "pitch":
                    ValeursTeleEnOrdre[9] = ValeursTele[1];
                    break;
                case "roll":
                    ValeursTeleEnOrdre[10] = ValeursTele[1];
                    break;
                case "yaw":
                    ValeursTeleEnOrdre[11] = ValeursTele[1];
                    break;
                case "h":
                    ValeursTeleEnOrdre[6] = ValeursTele[1];
                    break;
                case "time":
                    ValeursTeleEnOrdre[8] = ValeursTele[1];
                    break;
                case "bat":
                    ValeursTeleEnOrdre[7] = ValeursTele[1];
                    break;
            }
        }
        return ValeursTeleEnOrdre;
    }
}
