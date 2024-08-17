package com.care.careme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class PhyziHealth extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phyzi_health);
        LinearLayout bmi = (LinearLayout) findViewById(R.id.bmiLayout);
        LinearLayout step = (LinearLayout) findViewById(R.id.stepLayout);
        LinearLayout bp = (LinearLayout) findViewById(R.id.bpLayout);
        bmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PhyziHealth.this, PhyziHealth_bmiActivity.class));
            }
        });

        step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PhyziHealth.this, PhyziHealth_StepCounter.class));
            }
        });


        bp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PhyziHealth.this, PhyziHealth_BloodPressure_View.class));
            }
        });

    }
}