package com.promit.cryptowallet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    static Spinner addr_dd;
    static ImageView codeImg;
    static DBHandler db;
    static TextView balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHandler(MainActivity.this);
        //db.addAddress("0xf817f103331e5fb18420fe9d813442b1b19f7195");
        //db.addAddress("HELLO22");
        addr_dd = (Spinner) findViewById(R.id.addr_dropdown);
        balance = (TextView) findViewById(R.id.balance_text);
        codeImg = (ImageView) findViewById(R.id.qrView);
        addr_dd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    public void updateBalance() {
        String code = addr_dd.getSelectedItem().toString();
        requestBalance(code);
    }
    public void refreshBalance(View view) {
        updateBalance();
        //createAccount();
    }

    public void setBalance(String bal) {
        balance.setText("Balance: " + bal + " ETH");
    }



    public void requestBalance(String addr){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.blockcypher.com/v1/eth/main/addrs/" +
                addr +
                "/balance";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject obj = new JSONObject(response);
                            String bal = obj.getString("balance");
                            setBalance(bal);
                        } catch (Exception e) {
                            System.out.println("Exception when converting JSON Object");
                        }


                        System.out.println("Response is: " + response );
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setBalance("null");
            }
        });
        queue.add(stringRequest);
    }

    @Override
    public void onResume(){
        super.onResume();
        this.loadDropdown();
        this.updateQRCode();
        this.updateBalance();


    }
    public void loadDropdown() {
        List<String> addresses = db.getAddresses();
        ArrayAdapter<String> adapter = new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, addresses.toArray() );
        addr_dd.setAdapter(adapter);
    }

    public void updateQRCode() {
        String code = addr_dd.getSelectedItem().toString();
        Picasso.get().load("https://chart.googleapis.com/chart?chs=300x300&cht=qr&chl=" +
                        code +
                        "&choe=UTF-8")
                .into(codeImg);
        //requestBalance(code);
    }

    public void removeCurrentAddr(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Wallet")
                .setMessage("Are you sure you want to delete this wallet?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String code = addr_dd.getSelectedItem().toString();
                        db.removeAddress(code);
                        MainActivity.this.loadDropdown();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    public void openAddWallet(View view) {
        Intent intent = new Intent(this, AddWallet.class);
        startActivity(intent);
    }

    public void goCreateWallet(View view) {
        Intent intent = new Intent(this, CreateWallet.class);
        startActivity(intent);
    }

}
