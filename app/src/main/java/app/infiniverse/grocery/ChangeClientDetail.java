package app.infiniverse.grocery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
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

public class ChangeClientDetail extends AppCompatActivity {

    String name, mobile,  address, password;

    EditText etName, etMobile, etEmail, etAddress, etPassword;
    Button register;

    public static final String PREFS="PREFS";
    SharedPreferences sp;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_client_detail);


        sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        edit = sp.edit();


        etName = findViewById(R.id.name);
        etEmail = findViewById(R.id.email);
        etMobile = findViewById(R.id.mobile);
        etAddress = findViewById(R.id.address);
        etPassword = findViewById(R.id.password);
        register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            updateDetail();
        }
    });


    getUserDetails();
    }
        public void updateDetail () {
            name = etName.getText().toString();
            mobile = etMobile.getText().toString();
            address = etAddress.getText().toString();
            password = etPassword.getText().toString();

            if ((!name.equals("")) && (!mobile.equals("")) && (!address.equals(""))) {
                class RegisterUser extends AsyncTask<String, Void, String> {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if (s.trim().equals("DUS")) {
//                        Toast.makeText(ChangeClientDetail.this, "Detail Updated Successfully ", Toast.LENGTH_SHORT).show();

                            AlertDialog.Builder builder = new AlertDialog.Builder(ChangeClientDetail.this);
                            builder.setTitle("Successful")
                                    .setMessage("Detail Updated Successfully")
                                    .setIcon(R.drawable.ic_check_black)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            Intent ii = new Intent(ChangeClientDetail.this, HomeActivity.class);
                                            startActivity(ii);
                                            finish();
                                        }
                                    });
                            builder.show();
                            edit.putString("name", name);
                            edit.putString("mobile", mobile);
                            edit.putString("city", address);
                            edit.apply();
                        } else {
                            Toast.makeText(ChangeClientDetail.this, s, Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    protected String doInBackground(String... params) {

                        String urls = getResources().getString(R.string.base_url).concat("changeProfileDetails/");
                        try {
                            URL url = new URL(urls);
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setRequestMethod("POST");
                            httpURLConnection.setDoInput(true);
                            httpURLConnection.setDoOutput(true);
                            OutputStream outputStream = httpURLConnection.getOutputStream();
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                            String post_Data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") + "&" +
                                    URLEncoder.encode("mobile", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8") + "&" +
                                    URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8") + "&" +
                                    URLEncoder.encode("login_id", "UTF-8") + "=" + URLEncoder.encode(params[3], "UTF-8") + "&" +
                                    URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(params[4], "UTF-8");

                            bufferedWriter.write(post_Data);
                            bufferedWriter.flush();
                            bufferedWriter.close();
                            outputStream.close();
                            InputStream inputStream = httpURLConnection.getInputStream();
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                            String result = "", line = "";
                            while ((line = bufferedReader.readLine()) != null) {
                                result += line;
                            }
                            return result;
                        } catch (Exception e) {
                            return e.toString();
                        }
                    }
                }

                //creating asynctask object and executing it
                RegisterUser registerObj = new RegisterUser();
                registerObj.execute(name, mobile, address, sp.getString("loginid", null), password);
            } else {
                Toast.makeText(this, "All Fields Are Mandatory", Toast.LENGTH_SHORT).show();
            }
        }

    public void getUserDetails() {
        class FeedUserDetails extends AsyncTask<String, Void, String> {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    try {


                        JSONArray clientDetailArray = new JSONArray(s);
                        JSONObject json_data = new JSONObject();
                        json_data = clientDetailArray.getJSONObject(0);

                        etName.setText(json_data.getString("name"));
                        etMobile.setText(json_data.getString("mobile"));
                        etAddress.setText(json_data.getString("address"));


                    } catch (JSONException e) {
                        Toast.makeText(ChangeClientDetail.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                protected String doInBackground(String... params) {

                    String urls = getResources().getString(R.string.base_url).concat("getClientDetails/");
                    try {
                        URL url = new URL(urls);
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.setDoOutput(true);
                        OutputStream outputStream = httpURLConnection.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                        String post_Data = URLEncoder.encode("login_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");

                        bufferedWriter.write(post_Data);
                        bufferedWriter.flush();
                        bufferedWriter.close();
                        outputStream.close();
                        InputStream inputStream = httpURLConnection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                        String result = "", line = "";
                        while ((line = bufferedReader.readLine()) != null) {
                            result += line;
                        }
                        return result;
                    } catch (Exception e) {
                        return e.toString();
                    }
                }
            }

            //creating asynctask object and executing it
            FeedUserDetails feedUserDetails = new FeedUserDetails();
            feedUserDetails.execute(sp.getString("loginid",null));

    }
}
