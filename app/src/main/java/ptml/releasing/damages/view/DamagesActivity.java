package ptml.releasing.damages.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

import ptml.releasing.BR;
import ptml.releasing.R;
import ptml.releasing.app.base.BaseActivity;
import ptml.releasing.damages.model.ReleasingAssignedDamage;
import ptml.releasing.damages.view_model.DummyViewModel;
import ptml.releasing.databinding.ActivityReleasingDamagesBinding;

public class DamagesActivity extends BaseActivity<DummyViewModel, ActivityReleasingDamagesBinding> {


    public static List<ReleasingAssignedDamage> currentDamages = new LinkedList<ReleasingAssignedDamage>();
    @Nullable
    public static Integer typeContainer = 0;
    public static String currentDamageZone = ""; // R,T,L,D,F,B
    public static String currentDamagePoint = ""; // BAC,BRI,BBO,BLE,CBA,CRI,CBO,CLE,FCE,FRI,FBO,FLE,FRO

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_releasing_damages;
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

    public static void resetValues() {
        currentDamages.clear();
        typeContainer = null;
        currentDamageZone = "";
        currentDamagePoint = "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refreshDamages();

        if (currentDamages.size() == 0) {
            setResult(0);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.releasing_damages_title);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        if (DamagesActivity.currentDamages.size() == 0)
            addDamage();

        refreshDamages();
        setupListeners();
    }

    private void setupListeners() {


        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(0);
                finish();
            }
        });

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDamage();
            }
        });
    }

    void addDamage() {
        startActivityForResult(new Intent(DamagesActivity.this, ReleasingDamagesSelectZoneActivity.class), 0);
    }

    class CargoDamagesAdapter extends BaseAdapter {

        Context context;
        List<ReleasingAssignedDamage> damages;
        private LayoutInflater inflater = null;

        public CargoDamagesAdapter(Context context, List<ReleasingAssignedDamage> damages) {

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



            TextView txtDamageName = convertView.findViewById(R.id.tv_title);
            ImageButton btnUnassign = convertView.findViewById(R.id.btn_delete);
            final TextView txtDamageNumber = convertView.findViewById(R.id.tv_number);
            final ImageButton btnDecreaseNumber = convertView.findViewById(R.id.btn_remove);
            final ImageButton btnIncreaseNumber = convertView.findViewById(R.id.btn_add);
            final EditText edtRemarks = convertView.findViewById(R.id.edit_remarks);

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

    private void refreshDamages() {
        CargoDamagesAdapter adapter = new CargoDamagesAdapter(this, currentDamages);
        binding.listView.setAdapter(adapter);
        binding.listView.setEmptyView(binding.includeEmpty.getRoot());
    }


}
