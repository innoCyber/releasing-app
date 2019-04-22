package ptml.releasing.damages.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import dagger.android.support.DaggerAppCompatActivity;
import ptml.releasing.R;
import ptml.releasing.damages.model.AssignedDamage;

import java.util.LinkedList;
import java.util.List;

public class DamagesActivity extends DaggerAppCompatActivity {

    private Button btnAdd;
    private ListView lstDamages;
    private Button btnReturn;
    private TextView txtNoItems;

    public static List<AssignedDamage> currentDamages = new LinkedList<AssignedDamage>();

    public static String currentDamageZone = ""; // R,T,L,D,F,B
    public static String currentDamagePoint = ""; // BAC,BRI,BBO,BLE,CBA,CRI,CBO,CLE,FCE,FRI,FBO,FLE,FRO

    class CargoDamagesAdapter extends BaseAdapter {

        Context context;
        List<AssignedDamage> damages;
        private LayoutInflater inflater = null;

        public CargoDamagesAdapter(Context context, List<AssignedDamage> damages) {

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

            if(convertView == null){
                convertView = inflater.inflate(R.layout.cell_cargo_damages, null);
            }



            TextView txtDamageName = (TextView) convertView.findViewById(R.id.CellCargoDamagesTxtDamage);
            ImageButton btnUnassign = (ImageButton) convertView.findViewById(R.id.CellCargoDamagesBtnUnassign);
            final TextView txtDamageNumber = (TextView) convertView.findViewById(R.id.CellCargoDamagesTxtNumberValue);
            final Button btnDecreaseNumber = (Button)convertView.findViewById(R.id.CellCargoDamagesBtnDecreaseNumber);
            final Button btnIncreaseNumber = (Button)convertView.findViewById(R.id.CellCargoDamagesBtnIncreaseNumber);
            final EditText edtRemarks = (EditText) convertView.findViewById(R.id.CellCargoEdtDamageRemarks);

            edtRemarks.setText(damages.get(position).getDamageRemarks());
            txtDamageName.setText(damages.get(position).getName());
            txtDamageNumber.setText(Integer.valueOf(damages.get(position).getDamageCount()).toString());

            if(damages.get(position).getDamageCount() == 1)
                btnDecreaseNumber.setEnabled(false);
            else
                btnDecreaseNumber.setEnabled(true);


            btnDecreaseNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int currentValue = damages.get(position).getDamageCount();
                    if(currentValue>1)
                        currentValue--;
                    damages.get(position).setDamageCount(currentValue);

                    txtDamageNumber.setText(Integer.valueOf(damages.get(position).getDamageCount()).toString());

                    if(currentValue == 1)
                        btnDecreaseNumber.setEnabled(false);
                    else
                        btnDecreaseNumber.setEnabled(true);
                }
            });

            btnIncreaseNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int currentValue = damages.get(position).getDamageCount();
                    currentValue++;
                    damages.get(position).setDamageCount(currentValue);

                    txtDamageNumber.setText(Integer.valueOf(damages.get(position).getDamageCount()).toString());

                    if(currentValue > 1)
                        btnDecreaseNumber.setEnabled(true);
                    else
                        btnDecreaseNumber.setEnabled(false);
                }
            });

            edtRemarks.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Update the adapter
                    damages.get(position).setDamageRemarks(edtRemarks.getText().toString());
                }
            });

            btnUnassign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(DamagesActivity.this);

                    builder.setTitle(R.string.general_msg_confirm);
                    builder.setMessage(R.string.general_msg_confirm_unassign_damage);

                    builder.setPositiveButton(R.string.general_btn_yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            DamagesActivity.currentDamages.remove(position);

                            refreshDamages();
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton(R.string.general_btn_no, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

            txtDamageName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setMessage(damages.get(position).getName())
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
        setContentView(R.layout.activity_releasing_damages);
        setTitle(R.string.releasing_damages_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnAdd = (Button) findViewById(R.id.ReleasingDamagesBtnAssign);
        lstDamages = (ListView) findViewById(R.id.ReleasingDamagesLstDamages);
        btnReturn = (Button) findViewById(R.id.ReleasingDamagesBtnReturn);
        txtNoItems = (TextView)findViewById(R.id.ReleasingDamagesTxtNoItems);

        if(DamagesActivity.currentDamages.size() == 0)
            addDamage();

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
                setResult(0);
                finish();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDamage();
            }
        });
    }

    void addDamage() {
        startActivityForResult(new Intent(DamagesActivity.this, ReleasingDamagesSelectZoneActivity.class), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        refreshDamages();

        if(currentDamages.size() == 0) {
            setResult(0);
            finish();
        }
    }

    private void refreshDamages() {
        CargoDamagesAdapter adapter = new CargoDamagesAdapter(this, currentDamages);
        lstDamages.setAdapter(adapter);

        if(lstDamages.getAdapter().getCount()==0) {
            txtNoItems.setVisibility(View.VISIBLE);
        } else {
            txtNoItems.setVisibility(View.INVISIBLE);
        }
    }
}
