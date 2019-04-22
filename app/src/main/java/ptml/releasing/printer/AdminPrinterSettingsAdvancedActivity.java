package ptml.releasing.printer;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.discovery.BluetoothDiscoverer;
import com.zebra.sdk.printer.discovery.DeviceFilter;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;
import dagger.android.support.DaggerAppCompatActivity;
import ptml.releasing.R;
import ptml.releasing.app.utils.Constants;


public class AdminPrinterSettingsAdvancedActivity extends DaggerAppCompatActivity {

    private Button btnClose;
    private Button btnChangePrinter;
    private Button btnResetCode;
    private EditText edtPrinter;
    private EditText edtLabelCpclData;

    private String currentPrinter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_settings_advanced_printer);
        setTitle(R.string.admin_settings_advanced_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnClose = (Button)findViewById(R.id.AdminPrinterSettingsBtnClose);
        btnResetCode = (Button)findViewById(R.id.AdminPrinterSettingsBtnReset);
        btnChangePrinter = (Button)findViewById(R.id.AdminPrinterSettingsBtnChangePrinter);

        edtLabelCpclData = (EditText)findViewById(R.id.AdminPrinterSettingsEdtLabelCpclData);
        edtPrinter = (EditText)findViewById(R.id.AdminPrinterSettingsEdtPrinterValue);

        edtLabelCpclData.setText(Constants.DEFAULT_PRINTER_CODE);
    /*    ReleasingDBAdapter db = new ReleasingDBAdapter(AdminPrinterSettingsAdvancedActivity.this);
        db.open();

        currentPrinter = db.getSettings().getCurrentPrinter();
        edtPrinter.setText(db.getSettings().getCurrentPrinterName());

        String label = db.getSettings().getLabelCpclData();
        label = label.replaceAll("\r", "");

        edtLabelCpclData.setText(label);

        db.close();*/

        btnResetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtLabelCpclData.setText(Constants.DEFAULT_PRINTER_CODE);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  ReleasingDBAdapter db = new ReleasingDBAdapter(AdminPrinterSettingsAdvancedActivity.this);
                db.open();

                Settings s = db.getSettings();

                s.setCurrentPrinter(currentPrinter);
                s.setCurrentPrinterName(edtPrinter.getText().toString());

                String label = edtLabelCpclData.getText().toString();
                label = label.replaceAll("\r", "");
                label = label.replaceAll("\n", "\r\n");

                s.setLabelCpclData(label);
                db.setSettings(s);
                db.close();*/

                setResult(0);
                finish();
            }
        });

        btnChangePrinter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builderSingle = new AlertDialog.Builder(AdminPrinterSettingsAdvancedActivity.this);
                builderSingle.setTitle("Discovering printers...");

                final ArrayAdapter<DiscoveredPrinter> arrayAdapter = new ArrayAdapter<DiscoveredPrinter>(AdminPrinterSettingsAdvancedActivity.this,
                        android.R.layout.select_dialog_singlechoice);

                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String address = arrayAdapter.getItem(which).address;
                        String name = arrayAdapter.getItem(which).getDiscoveryDataMap().get("FRIENDLY_NAME");

                        for (String settingsKey : arrayAdapter.getItem(which).getDiscoveryDataMap().keySet()) {
                            System.out.println("Key: " + settingsKey + " Value: " + arrayAdapter.getItem(which).getDiscoveryDataMap().get(settingsKey));
                        }

                        currentPrinter = address;
                        edtPrinter.setText(name);
                        dialog.dismiss();
                    }
                });

                final AlertDialog printerDialog = builderSingle.show();

                new Thread(new Runnable() {
                    public void run() {
                        Looper.prepare();

                        try {
                            BluetoothDiscoverer.findPrinters(AdminPrinterSettingsAdvancedActivity.this,
                                    new DiscoveryHandler() {
                                        @Override
                                        public void foundPrinter(final DiscoveredPrinter discoveredPrinter) {

                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    arrayAdapter.add(discoveredPrinter);
                                                    arrayAdapter.notifyDataSetChanged();
                                                }
                                            });
                                        }

                                        @Override
                                        public void discoveryFinished() {

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (arrayAdapter.getCount() == 0)
                                                        builderSingle.setTitle("No printers found.");
                                                    else
                                                        builderSingle.setTitle("Select a printer:");
                                                }
                                            });
                                        }

                                        @Override
                                        public void discoveryError(final String errorMessage) {

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    printerDialog.dismiss();
                                                    new AlertDialog.Builder(AdminPrinterSettingsAdvancedActivity.this)
                                                            .setMessage(errorMessage)
                                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                }
                                                            })
                                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                                            .show();
                                                }
                                            });
                                        }
                                    }, new DeviceFilter() {
                                        @Override
                                        public boolean shouldAddPrinter(BluetoothDevice bluetoothDevice) {
                                            return true;
                                        }
                                    });
                        } catch (ConnectionException ce) {
                            ce.printStackTrace();
                        } finally {
                            Looper.myLooper().quit();
                        }
                    }
                }).start();

            }
        });
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
