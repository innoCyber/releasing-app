package ptml.releasing.damages.view;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import dagger.android.support.DaggerAppCompatActivity;
import ptml.releasing.R;

public class ReleasingDamagesSelectZoneActivity extends DaggerAppCompatActivity {

    private Button btnReturn;
    private ImageButton btnBack;
    private ImageButton btnFront;
    private ImageButton btnRight;
    private ImageButton btnTop;
    private ImageButton btnLeft;
    private ImageButton btnDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_releasing_damages_select_zone);
        setTitle(R.string.releasing_damages_select_zone_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnFront = (ImageButton) findViewById(R.id.ReleasingDamagesBtnFront);
        btnBack = (ImageButton) findViewById(R.id.ReleasingDamagesBtnBack);
        btnRight = (ImageButton) findViewById(R.id.ReleasingDamagesBtnRight);
        btnTop = (ImageButton) findViewById(R.id.ReleasingDamagesBtnTop);
        btnLeft = (ImageButton) findViewById(R.id.ReleasingDamagesBtnLeft);
        btnDown = (ImageButton) findViewById(R.id.ReleasingDamagesBtnDown);

        btnReturn = (Button) findViewById(R.id.ReleasingDamagesBtnReturn);

        setupListeners();

       /* if(ReleasingActivity.cargo.getContainerType() == 2) {
            DamagesActivity.currentDamageZone = "T";
            gotoSelectPoint();
        }*/
    }

    private void setupListeners() {

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(0);
                finish();
            }
        });

        btnFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamageZone = "F";
                gotoSelectPoint();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamageZone = "B";
                gotoSelectPoint();
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamageZone = "R";
                gotoSelectPoint();
            }
        });

        btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamageZone = "T";
                gotoSelectPoint();
            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamageZone = "L";
                gotoSelectPoint();
            }
        });

        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamageZone = "D";
                gotoSelectPoint();
            }
        });
    }

    private void gotoSelectPoint() {
        if(DamagesActivity.currentDamageZone.equals("F") || DamagesActivity.currentDamageZone.equals("B"))
            startActivityForResult(new Intent(this, ReleasingDamagesSelectFrontActivity.class), 0);
        else
            startActivityForResult(new Intent(this, ReleasingDamagesSelectSideActivity.class), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != -1 /*|| (ReleasingActivity.cargo.getContainerType() == 2) */)
            finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

