package com.example.infosdrones;

import static android.content.Context.SENSOR_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Thread_Gyroscope extends Thread implements SensorEventListener {
    private final int seuil = 3;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isRunning = true, isACKReceived = true;
    private OrientationChangeListener orientationListener;
    private CommandTelemetrieActivity ActivityCommand = null;
    private PrintWriter Out_CommAndroid = null;
    private BufferedReader In_CommAndroid = null;

    // Interface pour communiquer avec l'activite
    public interface OrientationChangeListener
    {
        void onOrientationChanged(String direction);
    }

    public Thread_Gyroscope(CommandTelemetrieActivity _CommandTelemetrieActivity,PrintWriter _PrintWriter, BufferedReader _In_CommAndroid)
    {
        this.In_CommAndroid = _In_CommAndroid;
        this.ActivityCommand = _CommandTelemetrieActivity;
        this.Out_CommAndroid = _PrintWriter;
        this.sensorManager = (SensorManager) this.ActivityCommand.getSystemService(this.ActivityCommand.SENSOR_SERVICE);
        if (this.sensorManager == null)
        {
            this.ActivityCommand.log("Erreur: capteurs non disponibles");
            new Tempo(4000);
            return;
        }
        this.accelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }


    public void stopThread()
    {
        this.isRunning = false;
        this.sensorManager.unregisterListener(this);
    }

    @Override
    public void run()
    {
        this.sensorManager.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    // Variables pour suivre l'etat precedent
    private String lastDirection = "Centre";
    private long lastCommandTime = 0;

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (isRunning && this.ActivityCommand.isGyro() && this.isACKReceived) //On effectue une commande que si le mode gyro est active
        {

            float x = event.values[0]; // Inclinaison gauche/droite
            float y = event.values[1]; // Inclinaison avant/arriere

            String commande = null;
            String direction = "Centre";

            // Determiner la direction en fonction de l'inclinaison
            if (x > seuil)
            {
                commande = "Backward";
                direction = "Gauche";
            } else if (x < -seuil)
            {
                commande = "Forward";
                direction = "Droite";
            } else if (y < -seuil)
            {
                commande = "Left";
                direction = "Avant";
            } else if (y > seuil)
            {
                commande = "Right";
                direction = "Arriere";
            }

            // Notifier l'activite du changement d'orientation (meme si on est toujours dans la meme direction)
            if (orientationListener != null)
            {
                orientationListener.onOrientationChanged(direction);
            }

            // Temps ecoule depuis la derniere commande
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastCommandTime;

            // Envoyer une commande uniquement si:
            // 1. On a une commande a envoyer
            // 2. ET (soit la direction a change OU assez de temps s'est ecoule depuis la derniere commande)
            if (commande != null && (!direction.equals(lastDirection) || elapsedTime > 3000))
            {
                this.isACKReceived = false;
                final String Command_ToSend = commande;
                new Thread(() -> {
                    if(this.Out_CommAndroid != null)
                    {
                        this.Out_CommAndroid.println(Command_ToSend);
                    }
                }).start();
                this.getACK();
                // Mettre a jour le moment ou la commande a ete envoyee
                lastCommandTime = currentTime;
                lastDirection = direction;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Non utilise
    }

    //##################################################################//
    // Getter Accuse Reception             		                        //
    //##################################################################//
    private void getACK()
    {
        new Thread(() -> {
            String reponse ="";
            do
            {
                try {
                    reponse = this.In_CommAndroid.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }while(reponse != null && !reponse.equals("ack_commande"));
            this.isACKReceived = true;
        }).start();
    }
}
