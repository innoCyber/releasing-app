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
import ptml.releasing.databinding.ActivityReleasingDamagesSelectFrontBinding;

public class ReleasingDamagesSelectFrontActivity extends BaseActivity<DummyViewModel, ActivityReleasingDamagesSelectFrontBinding> {


    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_releasing_damages_select_front;
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



        if(DamagesActivity.currentDamageZone.equals("F"))
            txtPosition.setText("Detail: Front");
        else if(DamagesActivity.currentDamageZone.equals("B"))
            txtPosition.setText("Detail: Back");
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

        binding.ReleasingDamagesBtnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "TOP";
                gotoSelectDamage();
            }
        });

        binding.ReleasingDamagesBtnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "BOT";
                gotoSelectDamage();
            }
        });

        binding.ReleasingDamagesBtnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "RIG";
                gotoSelectDamage();
            }
        });

        binding.ReleasingDamagesBtnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DamagesActivity.currentDamagePoint = "LEF";
                gotoSelectDamage();
            }
        });

        binding.ReleasingDamagesBtnBody.setOnClickListener(new View.OnClickListener() {
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

