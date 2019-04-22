package ptml.releasing.damages.view;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import dagger.android.support.DaggerAppCompatActivity;
import ptml.releasing.R;

public class ReleasingDamagesSelectSideActivity extends DaggerAppCompatActivity {

    private Button btnReturn;
    private ImageButton btnBack;
    private ImageButton btnBackBody;
    private ImageButton btnBackRight;
    private ImageButton btnBackLeft;
    private ImageButton btnCenterBack;
    private ImageButton btnCenterBody;
    private ImageButton btnCenterRight;
    private ImageButton btnCenterLeft;
    private ImageButton btnFrontCenter;
    private ImageButton btnFrontRight;
    private ImageButton btnFrontBody;
    private ImageButton btnFrontLeft;
    private ImageButton btnFront;

    private TextView txtPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_releasing_damages_select_side);
        setTitle(R.string.releasing_damages_select_position_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnBack = (ImageButton) findViewById(R.id.ReleasingDamagesBtnBack);
        btnBackBody = (ImageButton) findViewById(R.id.ReleasingDamagesBtnBackBody);
        btnBackRight = (ImageButton) findViewById(R.id.ReleasingDamagesBtnBackRight);
        btnBackLeft = (ImageButton) findViewById(R.id.ReleasingDamagesBtnBackLeft);
        btnCenterBack = (ImageButton) findViewById(R.id.ReleasingDamagesBtnCenterBack);
        btnCenterLeft = (ImageButton) findViewById(R.id.ReleasingDamagesBtnCenterLeft);
        btnCenterBody = (ImageButton) findViewById(R.id.ReleasingDamagesBtnCenterBody);
        btnCenterRight = (ImageButton) findViewById(R.id.ReleasingDamagesBtnCenterRight);
        btnFrontCenter = (ImageButton) findViewById(R.id.ReleasingDamagesBtnFrontCenter);
        btnFrontLeft = (ImageButton) findViewById(R.id.ReleasingDamagesBtnFrontLeft);
        btnFrontBody = (ImageButton) findViewById(R.id.ReleasingDamagesBtnFrontBody);
        btnFrontRight = (ImageButton) findViewById(R.id.ReleasingDamagesBtnFrontRight);
        btnFront = (ImageButton) findViewById(R.id.ReleasingDamagesBtnFront);

        txtPosition = (TextView)findViewById(R.id.ReleasingDamagesTxtPosition);

        if(DamagesActivity.currentDamageZone.equals("T"))
            txtPosition.setText("Detail: Top");
        else if(DamagesActivity.currentDamageZone.equals("D"))
            txtPosition.setText("Detail: Down");
        else if(DamagesActivity.currentDamageZone.equals("L"))
            txtPosition.setText("Detail: Left");
        else if(DamagesActivity.currentDamageZone.equals("R"))
            txtPosition.setText("Detail: Right");
        else
            txtPosition.setText("Detail: Unknown");


        btnReturn = (Button) findViewById(R.id.ReleasingDamagesBtnReturn);

        setupListeners();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupListeners() {


        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(-1);
                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "BAC";
                gotoSelectDamage();
            }
        });

        btnBackRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "BRI";
                gotoSelectDamage();
            }
        });

        btnBackBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "BBO";
                gotoSelectDamage();
            }
        });

        btnBackLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "BLE";
                gotoSelectDamage();
            }
        });

        btnCenterBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "CBA";
                gotoSelectDamage();
            }
        });

        btnCenterRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "CRI";
                gotoSelectDamage();
            }
        });

        btnCenterBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "CBO";
                gotoSelectDamage();
            }
        });

        btnCenterLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "CLE";
                gotoSelectDamage();
            }
        });

        btnFrontCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "FCE";
                gotoSelectDamage();
            }
        });

        btnFrontRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "FRI";
                gotoSelectDamage();
            }
        });

        btnFrontBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "FBO";
                gotoSelectDamage();
            }
        });

        btnFrontLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "FLE";
                gotoSelectDamage();
            }
        });

        btnFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "FRO";
                gotoSelectDamage();
            }
        });
    }

    private void gotoSelectDamage() {
        startActivityForResult(new Intent(this, ReleasingDamagesSelectDamageActivity.class), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != -1)
            finish();
    }
}

