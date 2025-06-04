package com.example.clientudp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class ThreadReceptionFluxVideo extends Thread
{
    private boolean flagEndThreadReceptionFluxVideo = false, VR_Mode = false;
    private ActivityNoVR monActivityNoVR = null;
    private int PortServeurConnexion = 37864, PortServeurVideo= 0, TailleTrameMax = 0;
    private DatagramSocket socketUDPClient = null;
    private DatagramPacket packetClient = null;
    private byte[] bufUDPCoConfirm = new byte[256], bufUDPCo, bufUDPLengthByte = null, bufUDPByteArrayCHAI;
    private ByteArrayOutputStream ByteArrayVideoCHAI = null; //Permet d ajouter les differents morceaux recues par UDP puis de le convertir en byte[]
    private InetAddress AddressServeur = null;
    private String IP_Serveur;

    public ThreadReceptionFluxVideo(boolean _VR, ActivityNoVR _ActivityNoVR, String _IP)
    {
        this.IP_Serveur = _IP;
        this.VR_Mode = _VR;
        if(this.VR_Mode)
        {

        }
        else
        {
            this.monActivityNoVR = _ActivityNoVR;
        }
    }

    public void run()
    {
        try {
            //Connexion au server
            this.socketUDPClient = new DatagramSocket();
            this.socketUDPClient.setSoTimeout(8000);
            this.bufUDPCo = "Connexion_UDP_Video".getBytes();
            this.AddressServeur = InetAddress.getByName(this.IP_Serveur);
            this.packetClient = new DatagramPacket(this.bufUDPCo,this.bufUDPCo.length, this.AddressServeur,this.PortServeurConnexion);
            this.socketUDPClient.send(this.packetClient);
            this.packetClient = new DatagramPacket(this.bufUDPCoConfirm,this.bufUDPCoConfirm.length);
            this.socketUDPClient.receive(this.packetClient);
            String receive = new String(this.packetClient.getData(),0,this.packetClient.getLength());
            InetAddress ServeurIPAddress = this.packetClient.getAddress();
            if(ServeurIPAddress.getHostAddress().equals(this.IP_Serveur) && receive.equals("Connexion_Confirmee"))
            {
                System.out.println("Connexion Reussie avec le serveur");
                this.ByteArrayVideoCHAI = new ByteArrayOutputStream();

                //Reception de la taille du buffer bufUDPLengthByte
                String[] Taille = null;
                do {
                    byte[] bufTaille = new byte[256];
                    this.packetClient = new DatagramPacket(bufTaille, bufTaille.length);
                    this.socketUDPClient.receive(this.packetClient);
                    Taille = new String(this.packetClient.getData(), 0, this.packetClient.getLength()).split(";");
                    ServeurIPAddress = this.packetClient.getAddress();
                    this.PortServeurVideo = this.packetClient.getPort();
                }
                while (!ServeurIPAddress.getHostAddress().equals(this.AddressServeur.getHostAddress()) || !Taille[0].equals("tailletrame"));
                this.sendAccuseReception();
                this.TailleTrameMax = Integer.parseInt(Taille[1]);
                //Reception du flux
                this.bufUDPLengthByte = new byte[this.TailleTrameMax];
                String reponse;
                this.socketUDPClient.setSoTimeout(0);
                while(!this.flagEndThreadReceptionFluxVideo)
                {
                    this.packetClient = new DatagramPacket(this.bufUDPLengthByte,this.bufUDPLengthByte.length);
                    this.socketUDPClient.receive(this.packetClient);
                    reponse = new String(this.packetClient.getData(),0,this.packetClient.getLength());
                    this.Repondre(this.packetClient.getAddress().getHostAddress(), reponse,this.packetClient.getData(), this.packetClient.getLength());
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            else
            {
                this.monActivityNoVR.ReturnToMain("Erreur de connexion au serveur");
            }
        } catch (SocketException e) {
        } catch (UnknownHostException e) {
            this.monActivityNoVR.ReturnToMain("Erreur de connexion au serveur");
        } catch (IOException e) {
            this.monActivityNoVR.ReturnToMain("Erreur de connexion au serveur");
        }
        if(!this.socketUDPClient.isClosed())
        {
            this.socketUDPClient.close();
        }
        System.gc();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////Methodes/////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Methode permettant d arreter le thread en cours
     */
    public void StopThread()
    {
        this.flagEndThreadReceptionFluxVideo = true;
        this.interrupt();
        if (this.socketUDPClient != null && !this.socketUDPClient.isClosed())
        {
            this.socketUDPClient.close();
        }
    }

    /**
     * Methode permettant de convertir un ByteArray en Bitmap (Image en Android)
     * @param ImageData
     * @return Bitmap
     */
    private Bitmap convertByteArrayToImage(byte[] ImageData)
    {
        Bitmap bitmap = BitmapFactory.decodeByteArray(ImageData, 0, ImageData.length);
        if (bitmap != null)
        {
            return bitmap;
        } else
        {
            System.out.println("Image mal construite");
            return null;
        }
    }


    /**
     * Methode envoyant les accuses de reception
     */
    private void sendAccuseReception()
    {
        byte[] byte_accuse = "accuse_ok".getBytes();
        DatagramPacket packetAccuse = new DatagramPacket(byte_accuse,byte_accuse.length, this.AddressServeur, this.PortServeurVideo);
        try {
            this.socketUDPClient.send(packetAccuse);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Methode permettant de redimensionner une image en Android
     * @param srcImg
     * @param _w
     * @param _h
     * @return Bitmap
     */
    private Bitmap getScaledImage(Bitmap srcImg, int _w, int _h)
    {
        return Bitmap.createScaledBitmap(srcImg, _w, _h, true);
    }

    /**
     * Methode permettant de repondre au serveur
     * @param reponse, AddressRecue, Data, DataLength
     */
    private void Repondre(String AddressRecue, String reponse, byte[] Data, int DataLength)
    {
        if(AddressRecue.equals(this.AddressServeur.getHostAddress()))
        {
            String[] EnTete = reponse.split(";");

            //On calcule le nombre de byte pour l en tete
            int length_entete = EnTete[0].length() + 1;
            switch (EnTete[0])
            {
                case "bytechai":
                    try {
                        this.ByteArrayVideoCHAI.write(Arrays.copyOfRange(Data, length_entete, DataLength));

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;

                case "transmission_finie":
                    Bitmap bitmapRecue = this.convertByteArrayToImage(this.ByteArrayVideoCHAI.toByteArray());
                    this.ByteArrayVideoCHAI = new ByteArrayOutputStream();

                    if(bitmapRecue != null) //Gestion d une mauvaise reception
                    {
                        System.out.println("Image bien construite");
                        if(this.VR_Mode)
                        {

                        }
                        else
                        {
                            bitmapRecue = this.getScaledImage(bitmapRecue,this.monActivityNoVR.getImageFondWidth(),this.monActivityNoVR.getImageFondHeight());
                            this.monActivityNoVR.setImageFond(bitmapRecue);
                        }
                    }

                    this.sendAccuseReception();
                    break;
            }
        }
    }

}
