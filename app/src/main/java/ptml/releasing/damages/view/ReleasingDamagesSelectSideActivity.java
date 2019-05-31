package ptml.releasing.damages.view;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import dagger.android.support.DaggerAppCompatActivity;
import ptml.releasing.BR;
import ptml.releasing.R;
import ptml.releasing.app.base.BaseActivity;
import ptml.releasing.damages.view_model.DummyViewModel;
import ptml.releasing.databinding.ActivityReleasingDamagesSelectSideBinding;

public class ReleasingDamagesSelectSideActivity extends BaseActivity<DummyViewModel, ActivityReleasingDamagesSelectSideBinding> {


    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_releasing_damages_select_side;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @NotNull
    @Override
    protected Class<DummyViewModel> getViewModelClass() {
        return DummyViewModel.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.releasing_damages_select_position_title);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }


        TextView txtPosition = (TextView) findViewById(R.id.ReleasingDamagesTxtPosition);

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



        setupListeners();
    }

    private void setupListeners() {


        binding.ReleasingDamagesBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(-1);
                finish();
            }
        });

        binding.ReleasingDamagesBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "BAC";
                gotoSelectDamage();
            }
        });

        binding.ReleasingDamagesBtnBackRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "BRI";
                gotoSelectDamage();
            }
        });

        binding.ReleasingDamagesBtnBackBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "BBO";
                gotoSelectDamage();
            }
        });

        binding.ReleasingDamagesBtnBackLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "BLE";
                gotoSelectDamage();
            }
        });

        binding.ReleasingDamagesBtnCenterBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "CBA";
                gotoSelectDamage();
            }
        });

        binding.ReleasingDamagesBtnCenterRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "CRI";
                gotoSelectDamage();
            }
        });

        binding.ReleasingDamagesBtnCenterBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "CBO";
                gotoSelectDamage();
            }
        });

        binding.ReleasingDamagesBtnCenterLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "CLE";
                gotoSelectDamage();
            }
        });

        binding.ReleasingDamagesBtnFrontCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "FCE";
                gotoSelectDamage();
            }
        });

        binding.ReleasingDamagesBtnFrontRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "FRI";
                gotoSelectDamage();
            }
        });

        binding.ReleasingDamagesBtnFrontBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "FBO";
                gotoSelectDamage();
            }
        });

        binding.ReleasingDamagesBtnFrontLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "FLE";
                gotoSelectDamage();
            }
        });

        binding.ReleasingDamagesBtnFront.setOnClickListener(new View.OnClickListener() {
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

