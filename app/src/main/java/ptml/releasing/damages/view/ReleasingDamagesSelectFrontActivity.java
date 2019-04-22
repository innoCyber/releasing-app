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

public class ReleasingDamagesSelectFrontActivity extends DaggerAppCompatActivity {

    private Button btnReturn;
    private ImageButton btnTop;
    private ImageButton btnBottom;
    private ImageButton btnRight;
    private ImageButton btnLeft;
    private ImageButton btnBody;

    private TextView txtPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_releasing_damages_select_front);
        setTitle(R.string.releasing_damages_select_position_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnTop = (ImageButton) findViewById(R.id.ReleasingDamagesBtnTop);
        btnBottom = (ImageButton) findViewById(R.id.ReleasingDamagesBtnBottom);
        btnRight = (ImageButton) findViewById(R.id.ReleasingDamagesBtnRight);
        btnLeft = (ImageButton) findViewById(R.id.ReleasingDamagesBtnLeft);
        btnBody = (ImageButton) findViewById(R.id.ReleasingDamagesBtnBody);

        txtPosition = (TextView)findViewById(R.id.ReleasingDamagesTxtPosition);

        btnReturn = (Button) findViewById(R.id.ReleasingDamagesBtnReturn);

        if(DamagesActivity.currentDamageZone.equals("F"))
            txtPosition.setText("Detail: Front");
        else if(DamagesActivity.currentDamageZone.equals("B"))
            txtPosition.setText("Detail: Back");
        else
            txtPosition.setText("Detail: Unknown");

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

        btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "TOP";
                gotoSelectDamage();
            }
        });

        btnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "BOT";
                gotoSelectDamage();
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "RIG";
                gotoSelectDamage();
            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "LEF";
                gotoSelectDamage();
            }
        });

        btnBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "BOD";
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

