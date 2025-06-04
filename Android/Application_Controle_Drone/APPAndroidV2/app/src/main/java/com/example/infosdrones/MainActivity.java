package com.example.infosdrones;

/**
 *
 * @author Abayev Ahmed
 *
 * @Desciption
 *
 * @date: 04/02/2025
 * @Modification 14/04/2025
 *
 * @version: 2
 *
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private EditText IPServeur;
    private Button BtnConnexion;
    private static boolean flagConnexion = false;
    private CommandTelemetrieActivity mesDrones = null;

    private static ThreadConnexionServeur monThreadConnexionServeur = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.BtnConnexion = (Button) findViewById(R.id.BtnConnexion);
        this.BtnConnexion.setOnClickListener(this);

        this.IPServeur = (EditText) findViewById(R.id.IPDroneServeur);
        this.IPServeur.setOnClickListener(this);

        this.mesDrones = new CommandTelemetrieActivity();

        if(MainActivity.monThreadConnexionServeur != null)
        {
            MainActivity.monThreadConnexionServeur = null;
        }


    }

    //##################################################################//
    // Methode appelee lors d un click sur un des boutons				//
    //##################################################################//
    public void onClick(View v)
    {
        if(v == BtnConnexion && !this.IPServeur.getText().toString().trim().isEmpty() && MainActivity.monThreadConnexionServeur == null)
        {
            //Cree et instancier un objet ThreadConnexionServeur nomme monThreadConnexionServeur
            MainActivity.monThreadConnexionServeur = new ThreadConnexionServeur(this,this.IPServeur.getText().toString());
            MainActivity.monThreadConnexionServeur.start();

        }
    }

    //##################################################################//
    // Methode permettant de passer a l activite de commande des drones //
    //##################################################################//
    public void GoActivityCommand()
    {
        runOnUiThread(() -> {
            DataService.set_ThreadConnexionServeur(MainActivity.monThreadConnexionServeur);
            Intent intent = new Intent(MainActivity.this, CommandTelemetrieActivity.class);
            startActivity(intent);
        });
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