package com.promit.cryptowallet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    static Spinner addrDropDownSpinner;
    static ImageView qrCodeImage;
    static DBHandler db;
    static TextView balanceText;
    static String LOG_TAG = "CryptoWallet:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHandler(MainActivity.this);
        addrDropDownSpinner = (Spinner) findViewById(R.id.addr_dropdown);
        balanceText = (TextView) findViewById(R.id.balance_text);
        qrCodeImage = (ImageView) findViewById(R.id.qrView);
        // Creating listener for dropdown
        addrDropDownSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity.this.updateQRCode();
                MainActivity.this.updateBalance();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        loadDropdown();
        updateQRCode();
        updateBalance();
    }

    /*
    Check which address selected in spinner and update balance
     */
    public void updateBalance() {
        if (addrDropDownSpinner.getSelectedItem() == null) return;
        String code = addrDropDownSpinner.getSelectedItem().toString();
        requestBalance(code);
    }
    /*
    Button event listener method for refresh button
     */
    public void refreshBalance(View view) {
        updateBalance();
    }

    /*
    For testing purposes only
     */
    private void setBalance(String bal) {
        balanceText.setText("Balance: " + bal + " ETH");
    }

    /*
    Call to API to request balance on @param addr
     */
    public void requestBalance(String addr){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://localhost:3000/balance/" +
                addr;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            double bal = obj.getDouble("balance");
                            // to convert from wei to eth
                            double power = 1000000000000000000l;
                            bal = bal / power;
                            String balance = "" + bal;
                            Log.i(LOG_TAG, "Balance is " + balance);
                            setBalance(balance);
                        } catch (Exception e) {
                            System.out.println("Exception when converting JSON Object");
                            e.printStackTrace();
                            setBalance("null");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setBalance("null");
            }
        });
        queue.add(stringRequest);
    }

    /*
    Reload page on resume
     */
    @Override
    public void onResume(){
        super.onResume();
        this.loadDropdown();
        this.updateQRCode();
        this.updateBalance();
    }

    /*
    Populate spinner from db entries
     */
    public void loadDropdown() {
        List<String> addresses = db.getAddresses();
        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, addresses.toArray() );
        addrDropDownSpinner.setAdapter(adapter);
    }

    /*
    Load image from Google API to Picasso
     */
    public void updateQRCode() {
        if (addrDropDownSpinner.getSelectedItem() == null) return;
        String code = addrDropDownSpinner.getSelectedItem().toString();
        Picasso.get().load("https://chart.googleapis.com/chart?chs=300x300&cht=qr&chl=" +
                        code +
                        "&choe=UTF-8")
                .into(qrCodeImage);
    }

    /*
    Handles pop-up confirmation and removal of address from db
     */
    public void removeCurrentAddr(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Wallet")
                .setMessage("Are you sure you want to delete this wallet?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String code = addrDropDownSpinner.getSelectedItem().toString();
                        db.removeAddress(code);
                        MainActivity.this.loadDropdown();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /*
    Button listener to 'Add Wallet' page
     */
    public void openAddWallet(View view) {
        Intent intent = new Intent(this, AddWallet.class);
        startActivity(intent);
    }

    /*
    Button listener to 'Create Wallet' page
     */
    public void goCreateWallet(View view) {
        Intent intent = new Intent(this, CreateWallet.class);
        startActivity(intent);
    }

}
