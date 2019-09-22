package com.example.barcodereader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ForgotPassword extends AppCompatActivity {
EditText LicenceKeyE;
Button buttonProc;
LinearLayout licenceKey,resetpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        LicenceKeyE = (EditText) findViewById(R.id.LicenceKeyE);
        buttonProc = (Button) findViewById(R.id.buttonProc);
        licenceKey=findViewById(R.id.licenceKey);
        resetpass=findViewById(R.id.resetpass);
        buttonProc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LicenceKeyE.getText().toString().equals("123456")) {
                    Toast.makeText(getApplicationContext(),
                            "Redirecting...", Toast.LENGTH_SHORT).show();
                    resetpass.setVisibility(View.VISIBLE);
                    licenceKey.setVisibility(View.GONE);
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "Failed...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //get contents from the sharedperference
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backDash=new Intent(ForgotPassword.this, AuthActivity.class);
        startActivity(backDash);
        finish();
    }
}
