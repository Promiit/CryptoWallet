package com.promit.cryptowallet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

public class AddWallet extends AppCompatActivity {

    TextInputLayout addrBar;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);

        saveBtn = (Button) findViewById(R.id.add_addr_save_btn);
        addrBar = (TextInputLayout) findViewById(R.id.addr_input);
    }

    public void saveClicked(View view) {
        // Take text in address bar and save to db
        DBHandler db = new DBHandler(this);
        String addr = addrBar.getEditText().getText().toString();
        db.addAddress(addr);
        db.close();
        finish();
    }
}