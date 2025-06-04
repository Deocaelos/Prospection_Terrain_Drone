package com.example.clientudp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class ActivityNoVR extends AppCompatActivity implements View.OnClickListener
{

    private ImageButton Button_Quit;
    private ImageView ImageFond;
    private String IP_Serveur;
    private ThreadReceptionFluxVideo monThreadReceptionFluxVideo = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_vr);
        //On cache la barre de navigation
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                // Permet au mode immersif de rester actif meme apres une interaction de l'utilisateur
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Maintient la mise en page stable
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                // Permet à la vue de s etendre derriere la barre de navigation
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                // Permet a la vue de s etendre derriere la barre de statut
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Cache la barre de navigation
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                // Cache la barre de statut
                | View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        //----Button----//
        this.Button_Quit = (ImageButton) findViewById(R.id.buttonQUIT);
        this.Button_Quit.setOnClickListener(this);

        //----ImageView----//
        this.ImageFond = (ImageView) findViewById(R.id.FluxCHAI);

        //Recuperation de l ip du serveur
        this.IP_Serveur = getIntent().getStringExtra("IP_SERVEUR");

        //On demarre le Thread de Reception du Flux
        this.monThreadReceptionFluxVideo = new ThreadReceptionFluxVideo(false,this,this.IP_Serveur);
        this.monThreadReceptionFluxVideo.start();
    }

    //##################################################################//
    // Methode appelee lors d un click sur un des boutons				//
    //##################################################################//
    public void onClick(View v)
    {
        if (v == Button_Quit)
        {
            this.ReturnToMain();
        }
    }

    public void ReturnToMain()
    {
        //On stop le thread de reception
        this.monThreadReceptionFluxVideo.StopThread();
        Intent intent = new Intent(ActivityNoVR.this, MainActivity.class);
        startActivity(intent);
    }

    public void ReturnToMain(String Reason)
    {
        this.log(Reason);
        //On stop le thread de reception
        this.monThreadReceptionFluxVideo.StopThread();

        //On attend que le thread soit stoppe
        while(this.monThreadReceptionFluxVideo.isAlive())
        {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Intent intent = new Intent(ActivityNoVR.this, MainActivity.class);
        startActivity(intent);
    }

    //##################################################################//
    // Methode permettant de modifier l image du ImageView				//
    //##################################################################//
    public void setImageFond(Bitmap Image_Fond)
    {
        runOnUiThread(() -> {
            this.ImageFond.setImageBitmap(Image_Fond);
        });
    }

    //##################################################################//
    // Methode permettant d obtenir la largeur du ImageView 			//
    //##################################################################//
    public int getImageFondWidth()
    {
        return(this.ImageFond.getWidth());
    }

    //##################################################################//
    // Methode permettant d obtenir la hauteur du ImageView 			//
    //##################################################################//
    public int getImageFondHeight()
    {
        return(this.ImageFond.getHeight());
    }

    //##################################################################//
    // Methode permettant d afficher une notification       			//
    //##################################################################//
    public void log(String text)
    {
        runOnUiThread(() -> {
            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 25);
            toast.show();
        });
    }


}