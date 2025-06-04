package com.example.infosdrones;

import androidx.appcompat.app.AppCompatActivity;
import com.example.infosdrones.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.HashMap;
import java.util.Map;

public class CommandTelemetrieActivity extends AppCompatActivity{

    //Boutons
    private Button takeoffButton, landButton;
    private Button goUpButton, goDownButton, goLeftButton, goRightButton, goForwardButton, goBackwardsButton;
    private Button rotateClockwiseButton, rotateCounterClockwiseButton;
    private Button btn_Deco;
    private CheckBox checkbox_gyro;
    private TextView movementValueTextView, rotationValueTextView;
    private SeekBar rotationSeekBar;
    private int currentMovementDistance = 50; // Valeur par défaut
    private int currentRotationAngle = 90; // Valeur par défaut
    private ThreadConnexionServeur monThreadConnexionServeur = null;


    private ProgressBar progressBarAgXPlusDrone1,progressBarAgXMoinsDrone1,progressBarAgYPlusDrone1,progressBarAgYMoinsDrone1,progressBarAgZPlusDrone1,progressBarAgZMoinsDrone1;
    private ProgressBar progressBarVgXPlusDrone1,progressBarVgXMoinsDrone1,progressBarVgYPlusDrone1,progressBarVgYMoinsDrone1,progressBarVgZPlusDrone1,progressBarVgZMoinsDrone1;
    private ProgressBar progressBarPitchPlusDrone1,progressBarPitchMoinsDrone1,progressBarRollPlusDrone1,progressBarRollMoinsDrone1,progressBarYawPlusDrone1,progressBarYawMoinsDrone1;
    private ProgressBar progressBarBatDrone1,progressBarHDrone1,progressBarTempLDrone1,progressBarTempHDrone1,progressBarBaroDrone1;
    private TextView textViewBatValueDrone1,textViewHValueDrone1,textViewTempHValueDrone1,textViewTempLValueDrone1,textViewBaroValueDrone1;

    private ProgressBar progressBarAgXPlusDrone2,progressBarAgXMoinsDrone2,progressBarAgYPlusDrone2,progressBarAgYMoinsDrone2,progressBarAgZPlusDrone2,progressBarAgZMoinsDrone2;
    private ProgressBar progressBarVgXPlusDrone2,progressBarVgXMoinsDrone2,progressBarVgYPlusDrone2,progressBarVgYMoinsDrone2,progressBarVgZPlusDrone2,progressBarVgZMoinsDrone2;
    private ProgressBar progressBarPitchPlusDrone2,progressBarPitchMoinsDrone2,progressBarRollPlusDrone2,progressBarRollMoinsDrone2,progressBarYawPlusDrone2,progressBarYawMoinsDrone2;
    private ProgressBar progressBarBatDrone2,progressBarHDrone2,progressBarTempLDrone2,progressBarTempHDrone2,progressBarBaroDrone2;
    private TextView textViewBatValueDrone2,textViewHValueDrone2,textViewTempHValueDrone2,textViewTempLValueDrone2,textViewBaroValueDrone2,textViewTimeValue;

    private boolean isTakeOff = false, isLand = false, isUp = false, isDown = false, isLeft = false, isRight = false, isForward = false, isBackward = false, isRotateClockWise = false, isRotateCounterClockWise = false, isDeconnexion = false, isGyro = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command_telemetrie);
        textViewTimeValue = (TextView) findViewById(R.id.textViewTimeValue);

        //Recuperation du Thread et on donne acces au Thread a cette activite
        this.monThreadConnexionServeur = DataService.get_ThreadConnexionServeur();
        this.monThreadConnexionServeur.setActivityCommand(this);

        // Initialisation des vues
        this.takeoffButton = (Button) findViewById(R.id.btn_takeoff);
        this.landButton = (Button) findViewById(R.id.btn_land);

        // Initialisation des boutons de contrôle directionnel
        this.goUpButton = (Button) findViewById(R.id.btn_up);
        this.goDownButton = (Button) findViewById(R.id.btn_down);
        this.goLeftButton = (Button) findViewById(R.id.btn_left);
        this.goRightButton = (Button) findViewById(R.id.btn_right);
        this.goForwardButton = (Button) findViewById(R.id.btn_forward);
        this.goBackwardsButton = (Button) findViewById(R.id.btn_backward);

        // Initialisation des boutons de rotation
        this.rotateClockwiseButton = (Button) findViewById(R.id.btn_turn_right);
        this.rotateCounterClockwiseButton = (Button) findViewById(R.id.btn_turn_left);

        this.btn_Deco = (Button) findViewById(R.id.button_deconnexion);

        this.rotationSeekBar = findViewById(R.id.seekbar_rotation);
        this.rotationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentRotationAngle = progress;
                rotationValueTextView.setText(progress + "°");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });



        // Initialisation des SeekBars et TextView associes
        this.rotationSeekBar = findViewById(R.id.seekbar_rotation);
        this.rotationValueTextView = findViewById(R.id.rotationValueTextView);

        this.progressBarAgXPlusDrone1 = (ProgressBar) findViewById(R.id.progressBarAgXPlusDrone1);
        this.progressBarAgXMoinsDrone1 = (ProgressBar) findViewById(R.id.progressBarAgXMoinsDrone1);
        this.progressBarAgYPlusDrone1 = (ProgressBar) findViewById(R.id.progressBarAgYPlusDrone1);
        this.progressBarAgYMoinsDrone1 = (ProgressBar) findViewById(R.id.progressBarAgYMoinsDrone1);
        this.progressBarAgZPlusDrone1 = (ProgressBar) findViewById(R.id.progressBarAgZPlusDrone1);
        this.progressBarAgZMoinsDrone1 = (ProgressBar) findViewById(R.id.progressBarAgZMoinsDrone1);
        this.progressBarVgXPlusDrone1 = (ProgressBar) findViewById(R.id.progressBarVgXPlusDrone1);
        this.progressBarVgXMoinsDrone1 = (ProgressBar) findViewById(R.id.progressBarVgXMoinsDrone1);
        this.progressBarVgYPlusDrone1 = (ProgressBar) findViewById(R.id.progressBarVgYPlusDrone1);
        this.progressBarVgYMoinsDrone1 = (ProgressBar) findViewById(R.id.progressBarVgYMoinsDrone1);
        this.progressBarVgZPlusDrone1 = (ProgressBar) findViewById(R.id.progressBarVgZPlusDrone1);
        this.progressBarVgZMoinsDrone1 = (ProgressBar) findViewById(R.id.progressBarVgZMoinsDrone1);

        this.progressBarPitchPlusDrone1 = (ProgressBar) findViewById(R.id.progressBarPitchPlusDrone1);
        this.progressBarPitchMoinsDrone1 = (ProgressBar) findViewById(R.id.progressBarPitchMoinsDrone1);
        this.progressBarRollPlusDrone1 = (ProgressBar) findViewById(R.id.progressBarRollPlusDrone1);
        this.progressBarRollMoinsDrone1 = (ProgressBar) findViewById(R.id.progressBarRollMoinsDrone1);
        this.progressBarYawPlusDrone1 = (ProgressBar) findViewById(R.id.progressBarYawPlusDrone1);
        this.progressBarYawMoinsDrone1 = (ProgressBar) findViewById(R.id.progressBarYawMoinsDrone1);
        this.progressBarBatDrone1 = (ProgressBar) findViewById(R.id.progressBarBatDrone1);
        this.progressBarHDrone1 = (ProgressBar) findViewById(R.id.progressBarHDrone1);

        this.textViewBatValueDrone1 = (TextView) findViewById(R.id.textViewBatValueDrone1);
        this.textViewHValueDrone1 = (TextView) findViewById(R.id.textViewHValueDrone1);

        this.progressBarAgXPlusDrone2 = (ProgressBar) findViewById(R.id.progressBarAgXPlusDrone2);
        this.progressBarAgXMoinsDrone2 = (ProgressBar) findViewById(R.id.progressBarAgXMoinsDrone2);
        this.progressBarAgYPlusDrone2 = (ProgressBar) findViewById(R.id.progressBarAgYPlusDrone2);
        this.progressBarAgYMoinsDrone2 = (ProgressBar) findViewById(R.id.progressBarAgYMoinsDrone2);
        this.progressBarAgZPlusDrone2 = (ProgressBar) findViewById(R.id.progressBarAgZPlusDrone2);
        this.progressBarAgZMoinsDrone2 = (ProgressBar) findViewById(R.id.progressBarAgZMoinsDrone2);
        this.progressBarVgXPlusDrone2 = (ProgressBar) findViewById(R.id.progressBarVgXPlusDrone2);
        this.progressBarVgXMoinsDrone2 = (ProgressBar) findViewById(R.id.progressBarVgXMoinsDrone2);
        this.progressBarVgYPlusDrone2 = (ProgressBar) findViewById(R.id.progressBarVgYPlusDrone2);
        this.progressBarVgYMoinsDrone2 = (ProgressBar) findViewById(R.id.progressBarVgYMoinsDrone2);
        this.progressBarVgZPlusDrone2 = (ProgressBar) findViewById(R.id.progressBarVgZPlusDrone2);
        this.progressBarVgZMoinsDrone2 = (ProgressBar) findViewById(R.id.progressBarVgZMoinsDrone2);

        this.progressBarPitchPlusDrone2 = (ProgressBar) findViewById(R.id.progressBarPitchPlusDrone2);
        this.progressBarPitchMoinsDrone2 = (ProgressBar) findViewById(R.id.progressBarPitchMoinsDrone2);
        this.progressBarRollPlusDrone2 = (ProgressBar) findViewById(R.id.progressBarRollPlusDrone2);
        this.progressBarRollMoinsDrone2 = (ProgressBar) findViewById(R.id.progressBarRollMoinsDrone2);
        this.progressBarYawPlusDrone2 = (ProgressBar) findViewById(R.id.progressBarYawPlusDrone2);
        this.progressBarYawMoinsDrone2 = (ProgressBar) findViewById(R.id.progressBarYawMoinsDrone2);
        this.progressBarBatDrone2 = (ProgressBar) findViewById(R.id.progressBarBatDrone2);
        this.progressBarHDrone2 = (ProgressBar) findViewById(R.id.progressBarHDrone2);

        this.textViewBatValueDrone2 = (TextView) findViewById(R.id.textViewBatValueDrone2);
        this.textViewHValueDrone2 = (TextView) findViewById(R.id.textViewHValueDrone2);


        //Initialisation du checkbox
        this.checkbox_gyro = (CheckBox) findViewById(R.id.checkBoxGyro);

        ///////////////////////////////Listener///////////////////////////////
        this.takeoffButton.setOnClickListener(v ->
        {
            isTakeOff = true;
        });

        this.landButton.setOnClickListener(v ->
        {
            isLand = true;
        });

        this.goUpButton.setOnClickListener(v ->
        {
            isUp = true;
        });

        this.goDownButton.setOnClickListener(v ->
        {
            isDown = true;
        });

        this.goRightButton.setOnClickListener(v ->
        {
            isRight = true;
        });

        this.goLeftButton.setOnClickListener(v ->
        {
            isLeft = true;
        });

        this.goForwardButton.setOnClickListener(v ->
        {
            isForward = true;
        });

        this.goBackwardsButton.setOnClickListener(v ->
        {
            isBackward = true;
        });

        this.rotateClockwiseButton.setOnClickListener(v ->
        {
            isRotateClockWise = true;
        });

        this.rotateCounterClockwiseButton.setOnClickListener(v ->
        {
            isRotateCounterClockWise = true;
        });

        this.btn_Deco.setOnClickListener(v ->
        {
            isDeconnexion = true;
        });

        this.checkbox_gyro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
            {
                this.log("Activation du mode Gyroscope");
            } else
            {
                this.log("Desactivation du mode Gyroscope");
            }
            isGyro = isChecked;
        });
    }



    //##################################################################//
    // Methode permettant de passer a l activite de connexion au serveur//
    //##################################################################//
    public void GoActivityConnexion()
    {
        runOnUiThread(() -> {
            Intent intent = new Intent(CommandTelemetrieActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
    public void setAgXDrone1Plus(int agXDrone1)
    {
        runOnUiThread(() -> progressBarAgXPlusDrone1.setProgress(agXDrone1));
    }
    public void setAgXDrone1Moins(int agXDrone1)
    {
        runOnUiThread(() -> progressBarAgXMoinsDrone1.setProgress(Math.abs(agXDrone1)));
    }
    public void setAgYDrone1Plus(int agYDrone1)
    {
        runOnUiThread(() -> progressBarAgYPlusDrone1.setProgress(agYDrone1));
    }
    public void setAgYDrone1Moins(int agYDrone1)
    {
        runOnUiThread(() -> progressBarAgYMoinsDrone1.setProgress(Math.abs(agYDrone1)));
    }
    public void setAgZDrone1Plus(int agZDrone1)
    {
        runOnUiThread(() -> progressBarAgZPlusDrone1.setProgress(agZDrone1));
    }
    public void setAgZDrone1Moins(int agZDrone1)
    {
        runOnUiThread(() -> progressBarAgZMoinsDrone1.setProgress(Math.abs(agZDrone1)));
    }
    public void setVgXDrone1Plus(int vgXDrone1)
    {
        runOnUiThread(() -> progressBarVgXPlusDrone1.setProgress(vgXDrone1));
    }
    public void setVgXDrone1Moins(int vgXDrone1)
    {
        runOnUiThread(() -> progressBarVgXMoinsDrone1.setProgress(Math.abs(vgXDrone1)));
    }
    public void setVgYDrone1Plus(int vgYDrone1)
    {
        runOnUiThread(() -> progressBarVgYPlusDrone1.setProgress(vgYDrone1));
    }
    public void setVgYDrone1Moins(int vgYDrone1)
    {
        runOnUiThread(() -> progressBarVgYMoinsDrone1.setProgress(Math.abs(vgYDrone1)));
    }
    public void setVgZDrone1Plus(int vgZDrone1)
    {
        runOnUiThread(() -> progressBarVgZPlusDrone1.setProgress(vgZDrone1));
    }
    public void setVgZDrone1Moins(int vgZDrone1)
    {
        runOnUiThread(() -> progressBarVgZMoinsDrone1.setProgress(Math.abs(vgZDrone1)));
    }
    public void setHDrone1(int hDrone1)
    {
        runOnUiThread(() -> progressBarHDrone1.setProgress(hDrone1));
    }
    public void setBaroDrone1(int baroDrone1)
    {
        runOnUiThread(() -> progressBarBaroDrone1.setProgress(baroDrone1));
    }
    public void setPitchDrone1Plus(int pitchDrone1)
    {
        runOnUiThread(() -> progressBarPitchPlusDrone1.setProgress(pitchDrone1));
    }
    public void setPitchDrone1Moins(int pitchDrone1)
    {
        runOnUiThread(() -> progressBarPitchMoinsDrone1.setProgress(Math.abs(pitchDrone1)));
    }
    public void setRollDrone1Plus(int rollDrone1)
    {
        runOnUiThread(() -> progressBarRollPlusDrone1.setProgress(rollDrone1));
    }
    public void setRollDrone1Moins(int rollDrone1)
    {
        runOnUiThread(() -> progressBarRollMoinsDrone1.setProgress(Math.abs(rollDrone1)));
    }
    public void setYawDrone1Plus(int yawDrone1)
    {
        runOnUiThread(() -> progressBarYawPlusDrone1.setProgress(yawDrone1));
    }
    public void setYawDrone1Moins(int yawDrone1)
    {
        runOnUiThread(() -> progressBarYawMoinsDrone1.setProgress(Math.abs(yawDrone1)));
    }
    public void setTempLDrone1(int tempLDrone1)
    {
        runOnUiThread(() -> progressBarTempLDrone1.setProgress(tempLDrone1));
    }
    public void setTempHDrone1(int tempHDrone1)
    {
        runOnUiThread(() -> progressBarTempHDrone1.setProgress(tempHDrone1));
    }
    public void setBatDrone1(int batDrone1)
    {
        runOnUiThread(() -> progressBarBatDrone1.setProgress(batDrone1));
    }

    public void settextViewBatUniteDrone1(String BatUniteDrone1)
    {
        runOnUiThread(() -> textViewBatValueDrone1.setText(BatUniteDrone1+"%"));
    }
    public void settextViewHUniteDrone1(String HUniteDrone1)
    {
        runOnUiThread(() -> textViewHValueDrone1.setText(HUniteDrone1+"cm"));
    }

    // ------------------------------------------ Drone 2

    public void setAgXDrone2Plus(int agXDrone2)
    {
        runOnUiThread(() -> progressBarAgXPlusDrone2.setProgress(agXDrone2));
    }
    public void setAgXDrone2Moins(int agXDrone2)
    {
        runOnUiThread(() -> progressBarAgXMoinsDrone2.setProgress(Math.abs(agXDrone2)));
    }
    public void setAgYDrone2Plus(int agYDrone2)
    {
        runOnUiThread(() -> progressBarAgYPlusDrone2.setProgress(agYDrone2));
    }
    public void setAgYDrone2Moins(int agYDrone2)
    {
        runOnUiThread(() -> progressBarAgYMoinsDrone2.setProgress(Math.abs(agYDrone2)));
    }
    public void setAgZDrone2Plus(int agZDrone2)
    {
        runOnUiThread(() -> progressBarAgZPlusDrone2.setProgress(agZDrone2));
    }
    public void setAgZDrone2Moins(int agZDrone2)
    {
        runOnUiThread(() -> progressBarAgZMoinsDrone2.setProgress(Math.abs(agZDrone2)));
    }
    public void setVgXDrone2Plus(int vgXDrone2)
    {
        runOnUiThread(() -> progressBarVgXPlusDrone2.setProgress(vgXDrone2));
    }
    public void setVgXDrone2Moins(int vgXDrone2)
    {
        runOnUiThread(() -> progressBarVgXMoinsDrone2.setProgress(Math.abs(vgXDrone2)));
    }
    public void setVgYDrone2Plus(int vgYDrone2)
    {
        runOnUiThread(() -> progressBarVgYPlusDrone2.setProgress(vgYDrone2));
    }
    public void setVgYDrone2Moins(int vgYDrone2)
    {
        runOnUiThread(() -> progressBarVgYMoinsDrone2.setProgress(Math.abs(vgYDrone2)));
    }
    public void setVgZDrone2Plus(int vgZDrone2)
    {
        runOnUiThread(() -> progressBarVgZPlusDrone2.setProgress(vgZDrone2));
    }
    public void setVgZDrone2Moins(int vgZDrone2)
    {
        runOnUiThread(() -> progressBarVgZMoinsDrone2.setProgress(Math.abs(vgZDrone2)));
    }
    public void setHDrone2(int hDrone2)
    {
        runOnUiThread(() -> progressBarHDrone2.setProgress(hDrone2));
    }
    public void setBaroDrone2(int baroDrone2)
    {
        runOnUiThread(() -> progressBarBaroDrone2.setProgress(baroDrone2));
    }
    public void setPitchDrone2Plus(int pitchDrone2)
    {
        runOnUiThread(() -> progressBarPitchPlusDrone2.setProgress(pitchDrone2));
    }
    public void setPitchDrone2Moins(int pitchDrone2)
    {
        runOnUiThread(() -> progressBarPitchMoinsDrone2.setProgress(Math.abs(pitchDrone2)));
    }
    public void setRollDrone2Plus(int rollDrone2)
    {
        runOnUiThread(() -> progressBarRollPlusDrone2.setProgress(rollDrone2));
    }
    public void setRollDrone2Moins(int rollDrone2)
    {
        runOnUiThread(() -> progressBarRollMoinsDrone2.setProgress(Math.abs(rollDrone2)));
    }
    public void setYawDrone2Plus(int yawDrone2)
    {
        runOnUiThread(() -> progressBarYawPlusDrone2.setProgress(yawDrone2));
    }
    public void setYawDrone2Moins(int yawDrone2)
    {
        runOnUiThread(() -> progressBarYawMoinsDrone2.setProgress(Math.abs(yawDrone2)));
    }
    public void setTempLDrone2(int tempLDrone2)
    {
        runOnUiThread(() -> progressBarTempLDrone2.setProgress(tempLDrone2));
    }
    public void setTempHDrone2(int tempHDrone2)
    {
        runOnUiThread(() -> progressBarTempHDrone2.setProgress(tempHDrone2));
    }
    public void setBatDrone2(int batDrone2)
    {
        runOnUiThread(() -> progressBarBatDrone2.setProgress(batDrone2));
    }

    public void settextViewBatUniteDrone2(String BatUniteDrone2)
    {
        runOnUiThread(() -> textViewBatValueDrone2.setText(BatUniteDrone2+"%"));
    }
    public void settextViewHUniteDrone2(String HUniteDrone2)
    {
        runOnUiThread(() -> textViewHValueDrone2.setText(HUniteDrone2+"cm"));
    }

    public void settextViewTimeUniteDrone(String TimeUniteDrone)
    {
        runOnUiThread(() -> textViewTimeValue.setText(TimeUniteDrone+"s"));
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

    public boolean isTakeOff() {
        return this.isTakeOff;
    }

    public void setTakeOff(boolean takeOff) {
        this.isTakeOff = takeOff;
    }

    public boolean isLand() {
        return this.isLand;
    }

    public void setLand(boolean land) {
        this.isLand = land;
    }

    public boolean isUp() {
        return this.isUp;
    }

    public void setUp(boolean up) {
        this.isUp = up;
    }

    public boolean isDown() {
        return this.isDown;
    }

    public void setDown(boolean down) {
        this.isDown = down;
    }

    public boolean isLeft() {
        return this.isLeft;
    }

    public void setLeft(boolean left) {
        this.isLeft = left;
    }

    public boolean isRight() {
        return this.isRight;
    }

    public void setRight(boolean right) {
        this.isRight = right;
    }

    public boolean isForward() {
        return this.isForward;
    }

    public void setForward(boolean forward) {
        this.isForward = forward;
    }

    public boolean isBackward() {
        return this.isBackward;
    }

    public void setBackward(boolean backward) {
        this.isBackward = backward;
    }

    public boolean isRotateClockWise() {
        return this.isRotateClockWise;
    }

    public void setRotateClockWise(boolean rotateClockWise) {
        this.isRotateClockWise = rotateClockWise;
    }

    public boolean isRotateCounterClockWise() {
        return this.isRotateCounterClockWise;
    }

    public void setRotateCounterClockWise(boolean rotateCounterClockWise) {
        this.isRotateCounterClockWise = rotateCounterClockWise;
    }

    public int getCurrentRotationAngle() {
        return this.currentRotationAngle;
    }

    public boolean isDeconnexion() {
        return this.isDeconnexion;
    }

    public boolean isGyro()
    {
        return this.isGyro;
    }

}