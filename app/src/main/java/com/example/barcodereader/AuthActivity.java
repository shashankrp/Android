package com.example.barcodereader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AuthActivity extends AppCompatActivity {

    // Sharedpreferences
    private SharedPreferences authPref,setupPref;
    private SharedPreferences.Editor authEditor,setupEditor;
    private boolean isFirstTIme,loginStatus;

    // Setup Varibles
    public EditText fname, lname,mobile,email,password,cpassword, etVerifyCode,loginEmail,loginPass,licenceKey;
    private CountryCodePicker countryCodePicker;
    private Button registerBtn,loginBtn,verifyOtp,proceedLicenceKey,backBtn;
    private TextView registerHere,forgotPassword, loginHere,forgot_pass;
    private ProgressDialog progressDialog;
    private boolean doubleBackPressed=false,verificaionStatus = false;

    //Layout
    private LinearLayout registerLayout,loginLayout,authlayout,getlicenceLayout;

    //Firebase
    private FirebaseAuth mAuth;
    private String codeSent = "", phoneNumber;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendingToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        authPref = getSharedPreferences("AuthPref", MODE_PRIVATE);
        authEditor = authPref.edit();

        setupPref = getSharedPreferences("SetupPref",MODE_PRIVATE);
        setupEditor = setupPref.edit();


        isFirstTIme = authPref.getBoolean("IsFirstTimeLaunch", true);
        loginStatus = authPref.getBoolean("loginStatus",false);

        if(!isFirstTIme && loginStatus) {
            launchSetupScreen();
        }

        setContentView(R.layout.activity_auth);


        // EditText
        fname = (EditText) findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        mobile = findViewById(R.id.mobile);
        email=findViewById(R.id.email);
        password = findViewById(R.id.password);
        cpassword=findViewById(R.id.c_password);
       // etVerifyCode = findViewById(R.id.verify_otp);
        loginEmail = findViewById(R.id.login_email);
        loginPass = findViewById(R.id.login_password);
        licenceKey=findViewById(R.id.licenceKey);


        //Country code picker
        countryCodePicker = findViewById(R.id.country_code);

        // Button
        registerBtn =(Button) findViewById(R.id.register_btn);
        loginBtn = (Button) findViewById(R.id.login_btn);

        proceedLicenceKey=(Button) findViewById(R.id.proceedLicenceKey);

        //TextView
        registerHere = findViewById(R.id.register_here);
        //forgotPassword = findViewById(R.id.forgot_password);
        loginHere = findViewById(R.id.login_here);

        //Layout
        authlayout = findViewById(R.id.auth_screen);
        registerLayout = findViewById(R.id.register_layout);
        loginLayout = findViewById(R.id.login_layout);
        getlicenceLayout=findViewById(R.id.getlicenceLayout);



        registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLayout.setVisibility(View.GONE);
                registerLayout.setVisibility(View.VISIBLE);

                //Reset
               // etVerifyCode.setText("");
                fname.setText("");
                lname.setText("");
                mobile.setText("");
                email.setText(" ");
                password.setText("");
                cpassword.setText("");
            }
        });

        loginHere.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                loginLayout.setVisibility(View.VISIBLE);
                registerLayout.setVisibility(View.GONE);
                //Reset
                loginEmail.setText("");
                loginPass.setText("");
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fname.getText().toString().isEmpty()) {
                    fname.setError("This field cannot be empty");
                } else if (lname.getText().toString().isEmpty()) {
                    lname.setError("This field cannot be empty");
                } else if (mobile.getText().toString().isEmpty()) {
                    mobile.setError("This field cannot be empty");
                }else if(mobile.length()<10){
                    mobile.setError("Please enter valid Phone number");
                } else if (password.getText().toString().isEmpty()) {
                    password.setError("This field cannot be empty");
                } else if (email.getText().toString().isEmpty()) {
                    email.setError("This field cannot be empty");
                }else if (!password.getText().toString().equals(cpassword.getText().toString())) {
                    password.setError("Password mismatch!");
                }else{
                    //Reset Error
                    fname.setError(null);
                    lname.setError(null);
                    mobile.setError(null);
                    password.setError(null);
                    cpassword.setError(null);

                    //Send OTP
                    phoneNumber = countryCodePicker.getSelectedCountryCodeWithPlus()+mobile.getText().toString();
                   //create service new User
                    new CreateUser().execute();

                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginEmail.getText().toString().isEmpty() || loginPass.getText().toString().isEmpty()){
                    Snackbar.make(authlayout,"Fields shouldn't be empty!",Snackbar.LENGTH_SHORT).show();
                } else {
                    new LoginUser().execute();
                }
            }
        });


        //get licence key  button click
        proceedLicenceKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (licenceKey.getText().toString().isEmpty()) {
                    licenceKey.setError("This field cannot be empty");
                } else if (licenceKey.length()>8) {
                    licenceKey.setError("should be length 8 character");
                }
                else
                {
                    new setLicence().execute();
                }
            }
        });



        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                verificaionStatus = true;
                progressDialog.dismiss();
                registerLayout.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);
                new CreateUser().execute();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                verificaionStatus = false;
                progressDialog.dismiss();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Snackbar.make(authlayout, "Invalid phone number", Snackbar.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(authlayout, "Quota exceeded.", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                codeSent = s;
                resendingToken = forceResendingToken;
                Snackbar.make(authlayout,"OTP has been Sent",Snackbar.LENGTH_SHORT).show();
            }
        };
    }
    //method for launch next Screen
    private void launchSetupScreen() {

        startActivity(new Intent(this,sample.class));
    }
    @Override
    public void onBackPressed() {
        if(doubleBackPressed) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
        } else {
            doubleBackPressed = true;
            Snackbar.make(authlayout,R.string.press_back_again,Snackbar.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackPressed = false;
                }
            },2000);
        }
    }

    //async task for createing new user
    class CreateUser extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AuthActivity.this.progressDialog = ProgressDialog.show(AuthActivity.this,"","Creating new User...");
        }

        @Override
        protected String doInBackground(String... strings) {
            //Auth verify
            String fname,lname,mobile,url,email,password,TAG_SUCCESS = "success";
            JSONParser jsonParser = new JSONParser();

            url = "http://www.intellectualsai.com/Textile/registration.php";

            fname=AuthActivity.this.fname.getText().toString();
            lname=AuthActivity.this.lname.getText().toString();
            mobile=AuthActivity.this.phoneNumber.toString();
            email = AuthActivity.this.email.getText().toString();
            password = AuthActivity.this.password.getText().toString();


            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("firstname",fname.trim()));
            params.add(new BasicNameValuePair("lastname",lname.trim()));
            params.add(new BasicNameValuePair("email",email.trim()));
            params.add(new BasicNameValuePair("mobile",mobile.trim()));
            params.add(new BasicNameValuePair("password", password.trim()));

            Log.d("CHECK:: ",""+fname+" "+lname);
            try{
                JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",params);

                int success = jsonObject.getInt(TAG_SUCCESS);

                if (success == 1)
                {
                    AuthActivity.this.authEditor.putBoolean("IsFirstTimeLaunch",false);
                    AuthActivity.this.authEditor.commit();
                    //verifyOtpLayout.setVisibility(View.GONE);
                    //Store Data
                    AuthActivity.this.setupEditor.putString("fname",fname.trim());
                    AuthActivity.this.setupEditor.putString("lname",lname.trim());
                    AuthActivity.this.setupEditor.putString("mobile",mobile.trim());
                    AuthActivity.this.setupEditor.putString("email",email.trim());
                    AuthActivity.this.setupEditor.putString("password",password.trim());
                    setupEditor.putBoolean("IsCalled", false);
                    AuthActivity.this.setupEditor.commit();



                    Snackbar.make(AuthActivity.this.authlayout, "Registration Successfull" , Snackbar.LENGTH_SHORT).show();
                }
                else {
                    Snackbar.make(AuthActivity.this.authlayout, "Registration Failed" , Snackbar.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            AuthActivity.this.progressDialog.dismiss();
            loginLayout.setVisibility(View.VISIBLE);
            //getlicenceLayout.setVisibility(View.VISIBLE);
            registerLayout.setVisibility(View.GONE);
            Intent instant=new Intent(AuthActivity.super.getApplicationContext(),AuthActivity.class);
            startActivity(instant);
        }
    }
    class setLicence extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AuthActivity.this.progressDialog = ProgressDialog.show(AuthActivity.this,"","Registring Licence...");
        }
        @Override
        protected String doInBackground(String... strings) {
            //Auth verify
            String androidid,licencekey,url,TAG_SUCCESS = "success";
            JSONParser jsonParser = new JSONParser();

            //url = "http://www.intellectualsai.com/Textile/registrationLicence.php";

            //icencekey=AuthActivity.this.licenceKey.getText().toString();
            //androidid= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            List<NameValuePair> params = new ArrayList<>();
            //params.add(new BasicNameValuePair("LicenceKey",licencekey.trim()));
            //params.add(new BasicNameValuePair("AndroidID",androidid.trim()));

            //Log.d("CHECK:: ",""+licencekey+" "+androidid);
            try{
                JSONObject jsonObject = jsonParser.makeHttpRequest("www.google.com/","POST",params);
                int success = jsonObject.getInt(TAG_SUCCESS);
                if (success == 1)      {
                    AuthActivity.this.authEditor.putBoolean("IsFirstTimeLaunch",false);
                    AuthActivity.this.authEditor.commit();
                    //Store Data

                    AuthActivity.this.setupEditor.commit();
                    Snackbar.make(AuthActivity.this.authlayout, "Licence Registration Successfull" , Snackbar.LENGTH_SHORT).show();

                }
                else {
                    Snackbar.make(AuthActivity.this.authlayout, "Licence Registration Failed" , Snackbar.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            AuthActivity.this.progressDialog.dismiss();
            loginLayout.setVisibility(View.VISIBLE);
            getlicenceLayout.setVisibility(View.GONE);
            licenceKey.setText("");
        }
    }

    class LoginUser extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AuthActivity.this.progressDialog = ProgressDialog.show(AuthActivity.this,"","Logging in...");
        }
        @Override
        protected String doInBackground(String... strings) {
            String url,email,password,TAG_SUCCESS = "success";
            JSONParser jsonParser = new JSONParser();

            url = "http://www.intellectualsai.com/Textile/authen_login.php";

            email = AuthActivity.this.loginEmail.getText().toString();
            password = AuthActivity.this.loginPass.getText().toString();

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("EmailL",email.trim()));
            params.add(new BasicNameValuePair("PasswordL", password.trim()));
            try{
                JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",params);

                int success = jsonObject.getInt(TAG_SUCCESS);
                System.out.println(success);
                if (success == 1) {
                    //progressDialog.dismiss();
                    AuthActivity.this.authEditor.putBoolean("IsFirstTimeLaunch",false);
                    AuthActivity.this.authEditor.putBoolean("loginStatus",true);
                    AuthActivity.this.authEditor.commit();
                    AuthActivity.this.launchSetupScreen();
                    Intent instant=new Intent(getApplicationContext(),sample.class);
                    startActivity(instant);
                } else {
                    //progressDialog.dismiss();
                    Snackbar.make(AuthActivity.this.authlayout,"Login Failed!",Snackbar.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            AuthActivity.this.progressDialog.dismiss();
        }
    }
}
