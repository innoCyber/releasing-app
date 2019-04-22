package ptml.releasing.damages.view;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import dagger.android.support.DaggerAppCompatActivity;
import ptml.releasing.R;
import ptml.releasing.app.local.ReleasingLocal;
import ptml.releasing.damages.model.AssignedDamage;
import ptml.releasing.download_damages.model.Damage;
import ptml.releasing.download_damages.model.DamageResponse;


import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class ReleasingDamagesSelectDamageActivity extends DaggerAppCompatActivity {

    @Inject
    ReleasingLocal db;


    private TextView txtSearch;
    private EditText edtSearch;
    private ListView lstDamages;
    private Button btnReturn;

    class CargoDamagesAdapter extends BaseAdapter {

        Context context;
        List<Damage> damages;
        private LayoutInflater inflater = null;

        public CargoDamagesAdapter(Context context, List<Damage> damages) {

            this.context = context;
            this.damages = damages;

            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return damages.size();
        }

        @Override
        public Object getItem(int position) {
            return damages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return damages.get(position).getId();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.cell_cargo_select_damages, null);
            }

            TextView txtDamageName = (TextView) convertView.findViewById(R.id.CellCargoDamagesTxtDamage);
            Button btnHigh = (Button) convertView.findViewById(R.id.CellCargoDamagesBtnHigh);
            Button btnLow = (Button) convertView.findViewById(R.id.CellCargoDamagesBtnLow);

            txtDamageName.setText(damages.get(position).getDescription());

            btnHigh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Damage d = damages.get(position);
                    DamagesActivity.currentDamages.add(
                            new AssignedDamage(
                                    d.getId(),
                                    d.getDescription(),
                                    "",
                                    1,
                                    d.getTypeContainer(),
                                    d.getPosition(),
                                    DamagesActivity.currentDamageZone + DamagesActivity.currentDamagePoint,
                                    1));
                    finish();

                }
            });

            btnLow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Damage d = damages.get(position);
                    DamagesActivity.currentDamages.add(
                            new AssignedDamage(
                                    d.getId(),
                                    d.getDescription(),
                                    "",
                                    1,
                                    d.getTypeContainer(),
                                    d.getPosition(),
                                    DamagesActivity.currentDamageZone + DamagesActivity.currentDamagePoint,
                                    0));
                    finish();
                }
            });

            txtDamageName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setMessage(damages.get(position).getDescription())
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show();
                }
            });


            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_releasing_select_damages);
        setTitle(R.string.releasing_damages_select_damage_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtSearch = (TextView) findViewById(R.id.ReleasingDamagesTxtSearch);
        edtSearch = (AutoCompleteTextView) findViewById(R.id.ReleasingDamagesEdtSearch);
        lstDamages = (ListView) findViewById(R.id.ReleasingDamagesLstDamages);
        btnReturn = (Button) findViewById(R.id.ReleasingDamagesBtnReturn);

        refreshDamages();
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

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                refreshDamages();
            }
        });
    }

    private void refreshDamages() {


        String position;
        if (DamagesActivity.currentDamagePoint.equals("BBO") ||
                DamagesActivity.currentDamagePoint.equals("CBO") ||
                DamagesActivity.currentDamagePoint.equals("FBO") ||
                DamagesActivity.currentDamagePoint.equals("BOD")
        ) {
            position = "C";
        } else {
            position = "S";
        }

        //TODO Filter by position

        DamageResponse damageResponse = db.getDamages();

        CargoDamagesAdapter adapter = new CargoDamagesAdapter(this, damageResponse != null ? damageResponse.getData() : new ArrayList<>());
        lstDamages.setAdapter(adapter);
    }
}
