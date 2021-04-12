package app.infiniverse.grocery;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity  {


    String name, email, mobile, address, password,cpassword;

    EditText etName, etMobile, etEmail, etAddress, etPassword , etcPassword;
    Button register;

    TextView loginnext;

    int otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);





        loginnext = findViewById(R.id.loginnext);
        etName = findViewById(R.id.name);
        etEmail = findViewById(R.id.email);
        etMobile = findViewById(R.id.mobile);
        etAddress = findViewById(R.id.address);
        etPassword = findViewById(R.id.password);
        etcPassword = findViewById(R.id.cpassword);
        register = findViewById(R.id.register);


        loginnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

        class LoadCitySpinner extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                List<String> city_list = new ArrayList<String>();
                try {
                    JSONArray jArray = new JSONArray(s);
                    JSONObject json_data = new JSONObject();
                    for (int i = 0; i < jArray.length(); i++) {
                        json_data = jArray.getJSONObject(i);

                    }

                } catch (Exception e) {
                    Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String urls = getResources().getString(R.string.base_url).concat("cities");
                    URL url = new URL(urls);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }





        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                name = etName.getText().toString();
                email = etEmail.getText().toString();
                mobile = etMobile.getText().toString();
                address = etAddress.getText().toString();
                password = etPassword.getText().toString();
                cpassword = etcPassword.getText().toString();

                if ((!name.equals("")) && (!email.equals("")) && (!mobile.equals("")) && (!address.equals("")) && (!password.equals("")) && (!cpassword.equals(""))) {
                    TelephonyManager telemanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    if (ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setTitle("Permission Required")
                                .setMessage("SMS And Phone Permission Required to get registered. Do You want to allow");
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                        builder.show();
                    } else {
                        String getSerialNumber = telemanager.getSimSerialNumber();
                        String getSimNumber = telemanager.getLine1Number();
                        if(!cpassword.equals(password)) {
                            Toast.makeText(RegisterActivity.this, "Password Didn't Matched", Toast.LENGTH_SHORT).show();

                        }else if(!mobile.matches("[0-9]{10}"))
                        {
                            Toast.makeText(RegisterActivity.this, "Enter Valid Number", Toast.LENGTH_SHORT).show();
                        }else if(!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))
                        {
                            Toast.makeText(RegisterActivity.this, "Enter Valid Email Address", Toast.LENGTH_SHORT).show();
                        }
                        else registerUser();
                    }
                }else{
                    Toast.makeText(RegisterActivity.this, "All Fields Are Mandatory", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }






    public void registerUser() {

        try {
            otp= generateOTP();
        } catch (Exception e) {
            otp=127856;
        }

        if ((!name.equals("")) && (!email.equals("")) && (!mobile.equals("")) && (!address.equals("")) &&  (!password.equals(""))) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(etMobile.getText().toString(), null, "OTP to get register with ecommerce Store Is "+otp, null, null);
                Intent intent=new Intent(RegisterActivity.this,ValidateOTP.class);
                intent.putExtra("loginid",email);
                intent.putExtra("name",name);
                intent.putExtra("email",email);
                intent.putExtra("mobile",mobile);

                intent.putExtra("address",address);
                intent.putExtra("otp",String.valueOf(otp));
                intent.putExtra("password",password);
                Toast.makeText(this, "OTP Sent to your mobile number", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Toast.makeText(RegisterActivity.this, "MSG Can not sent Check Your Balance", Toast.LENGTH_SHORT).show();
            }


        } else {
            Toast.makeText(this, "All Fields Are Mandatory", Toast.LENGTH_SHORT).show();
        }
    }

    public int generateOTP() throws Exception {
        Random generator = new Random();
        generator.setSeed(System.currentTimeMillis());

        int num = generator.nextInt(99999) + 99999;
        if (num < 100000 || num > 999999) {
            num = generator.nextInt(99999) + 99999;
            if (num < 100000 || num > 999999) {
                throw new Exception("RAJU123");
            }
        }
        return num;
    }

}