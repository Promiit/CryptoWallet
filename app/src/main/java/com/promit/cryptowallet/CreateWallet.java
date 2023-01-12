package com.promit.cryptowallet;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class CreateWallet extends AppCompatActivity {

    EditText addrBox;
    EditText pubkeyBox;
    EditText privkeyBox;
    TextView addrText;
    TextView pubkeyText;
    TextView privkeyText;
    TextView privKeyWarnText;
    Button doneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);

        addrBox = (EditText) findViewById(R.id.address_box);
        pubkeyBox = (EditText) findViewById(R.id.public_key_box);
        privkeyBox = (EditText) findViewById(R.id.private_key_box);
        addrText = (TextView) findViewById(R.id.textview_addr);
        pubkeyText = (TextView) findViewById(R.id.textview_addr2);
        privKeyWarnText = (TextView) findViewById(R.id.textview_addr3);
        privkeyText = (TextView) findViewById(R.id.textview_addr4);
        doneBtn = (Button) findViewById(R.id.generate_wallet_done_btn);
        setFormInvisible();
    }

    protected void setFormInvisible() {

        addrBox.setVisibility(INVISIBLE);
        pubkeyBox.setVisibility(INVISIBLE);
        privkeyBox.setVisibility(INVISIBLE);
        addrText.setVisibility(INVISIBLE);
        pubkeyText.setVisibility(INVISIBLE);
        privKeyWarnText.setVisibility(INVISIBLE);
        privkeyText.setVisibility(INVISIBLE);
        doneBtn.setVisibility(INVISIBLE);

    }

    protected void setFormVisible() {

        addrBox.setVisibility(VISIBLE);
        pubkeyBox.setVisibility(VISIBLE);
        privkeyBox.setVisibility(VISIBLE);
        addrText.setVisibility(VISIBLE);
        pubkeyText.setVisibility(VISIBLE);
        privKeyWarnText.setVisibility(VISIBLE);
        privkeyText.setVisibility(VISIBLE);
        doneBtn.setVisibility(VISIBLE);

    }

    public void clickGenerateWallet(View view) {
        createAccount();
    }

    public void clickDoneGenerateWallet(View view) {
        setBoxes("null","null","null");
        setFormInvisible();
        finish();
    }

    public void setBoxes(String address, String pubkey, String privkey) {
        addrBox.setText(address);
        pubkeyBox.setText(pubkey);
        privkeyBox.setText(privkey);
    }

    /*
    TODO: Generate wallet using own JSONRPC server instead of blockcypher call
     */
    private void createAccount() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.blockcypher.com/v1/eth/main/" +
                "addrs?token=11cad3be9a3d4b4e946c737fedf622d1";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            DBHandler db = new DBHandler(CreateWallet.this);
                            String address = "0x" + obj.getString("address");
                            String pubkey = obj.getString("public");
                            String privkey = obj.getString("private");
                            db.addAddress(address);
                            db.close();
                            CreateWallet.this.setBoxes(address,pubkey,privkey);
                            setFormVisible();
                        } catch (Exception e) {
                            System.out.println("Exception when converting JSON Object");
                        }

                        Log.i("CryptoWallet:", "Response is: " + response );
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("CryptoWallet:","Error getting response when creating address");
            }
        });
        queue.add(stringRequest);
    }


}