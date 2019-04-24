package ptml.releasing.damages.view;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import org.jetbrains.annotations.NotNull;

import dagger.android.support.DaggerAppCompatActivity;
import ptml.releasing.BR;
import ptml.releasing.R;
import ptml.releasing.app.base.BaseActivity;
import ptml.releasing.damages.view_model.DummyViewModel;
import ptml.releasing.databinding.ActivityReleasingDamagesSelectZoneBinding;

public class ReleasingDamagesSelectZoneActivity extends BaseActivity<DummyViewModel, ActivityReleasingDamagesSelectZoneBinding> {


    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_releasing_damages_select_zone;
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

        setTitle(R.string.releasing_damages_select_zone_title);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }


        setupListeners();

       /* if(ReleasingActivity.cargo.getContainerType() == 2) {
            DamagesActivity.currentDamageZone = "T";
            gotoSelectPoint();
        }*/
    }

    private void setupListeners() {

        binding.ReleasingDamagesBtnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(0);
                finish();
            }
        });

        binding.ReleasingDamagesBtnFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamageZone = "F";
                gotoSelectPoint();
            }
        });

        binding.ReleasingDamagesBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamageZone = "B";
                gotoSelectPoint();
            }
        });

        binding.ReleasingDamagesBtnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamageZone = "R";
                gotoSelectPoint();
            }
        });

        binding.ReleasingDamagesBtnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamageZone = "T";
                gotoSelectPoint();
            }
        });

        binding.ReleasingDamagesBtnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamageZone = "L";
                gotoSelectPoint();
            }
        });

        binding.ReleasingDamagesBtnDown.setOnClickListener(new View.OnClickListener() {
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

}

