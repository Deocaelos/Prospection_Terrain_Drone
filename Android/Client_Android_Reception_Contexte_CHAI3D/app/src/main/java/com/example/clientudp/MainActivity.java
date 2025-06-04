package com.example.clientudp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private Button Button_NoVR, Button_VR;
    private EditText IPServeur;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        this.Button_VR = (Button) findViewById(R.id.buttonVR);
        this.Button_VR.setOnClickListener(this);

        this.Button_NoVR = (Button) findViewById(R.id.buttonNoVR);
        this.Button_NoVR.setOnClickListener(this);

        //----EditText----//
        this.IPServeur = (EditText) findViewById(R.id.editTextTextIPServeur);
    }

    //##################################################################//
    // Methode appelee lors d un click sur un des boutons				//
    //##################################################################//
    public void onClick(View v)
    {
        //On verifie si une adresse IP a bien ete rentree
        if(!this.IPServeur.getText().toString().trim().isEmpty())
        {
            if (v == Button_VR)
            {
                this.log("Mode VR");

            }
            if (v == Button_NoVR)
            {
                this.log("Mode Non VR");
                Intent intent = new Intent(MainActivity.this, ActivityNoVR.class);
                intent.putExtra("IP_SERVEUR",this.IPServeur.getText().toString());
                startActivity(intent);
            }
        }
        else
        {
            this.log("Entrez une adresse IP correcte");
        }
    }

    //##################################################################//
    // Methode permettant d afficher une notification       			//
    //##################################################################//
    private void log(String text)
    {
        runOnUiThread(() -> {
            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 25);
            toast.show();
        });
    }




}