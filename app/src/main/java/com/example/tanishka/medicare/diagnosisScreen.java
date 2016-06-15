package com.example.tanishka.medicare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class diagnosisScreen extends Activity {

    public TextView admNoShow, nameShow, contactNoShow, emailShow, genderShow, dobShow, bloodGroupShow, heightShow, weightShow, metabolicDisorderShow;
    Bundle admNoImported;
    String admNoImportedString, JSON_STRING = "";
    Button checkMedicalHistoryButton;
    JSONObject jsonObject;
    JSONArray jsonArray;
    MedicalHistoryFromJson medicalHistoryFromJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_screen);

        admNoImported = getIntent().getExtras();
        admNoImportedString = admNoImported.getString("ADM_NO_PATIENT");
        admNoShow = (TextView) findViewById(R.id.admNoShow);
        nameShow = (TextView) findViewById(R.id.nameShow);
        contactNoShow = (TextView) findViewById(R.id.contactNoShow);
        emailShow = (TextView) findViewById(R.id.emailShow);
        genderShow = (TextView) findViewById(R.id.genderShow);
        dobShow = (TextView) findViewById(R.id.dobShow);
        bloodGroupShow = (TextView) findViewById(R.id.bloodGroupShow);
        heightShow = (TextView) findViewById(R.id.heightShow);
        weightShow = (TextView) findViewById(R.id.weightShow);
        metabolicDisorderShow = (TextView) findViewById(R.id.metabolicDisorderShow);
        checkMedicalHistoryButton= (Button) findViewById(R.id.checkMedicalHistory);
       // checkMedicalHistoryButton.setVisibility(View.INVISIBLE);
        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(admNoImportedString);
        medicalHistoryFromJson = new MedicalHistoryFromJson(this, admNoImportedString);
        //checkMedicalHistoryButton.setVisibility(View.VISIBLE);
    }


    public void goToMedicalHistoryPatientScreen(View view) {

        medicalHistoryFromJson.goToMedicalHistoryPatientScreen();

    }

    public void enterDetailsOfDiagnosis(View view) {
        Intent intent = new Intent(this, enterDiagnosisDetails.class);
        intent.putExtra("ADM_NO_PATIENT", admNoImportedString);
        startActivity(intent);
    }

    public class BackgroundTask extends AsyncTask<String, Void, String[]> {
        String jsonGetPatientDetailsURL;
        Context context;

        BackgroundTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            jsonGetPatientDetailsURL = "http://172.24.82.176/mediworld/jsonGetPatientDetails.php";
        }

        @Override
        protected String[] doInBackground(String... params) {
            String PATIENT_ADM_NO = params[0];
            try {
                URL url = new URL(jsonGetPatientDetailsURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String data = URLEncoder.encode("PATIENT_ADM_NO", "UTF-8") + "=" + URLEncoder.encode(PATIENT_ADM_NO, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine()) != null) {

                    stringBuilder.append(JSON_STRING + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                JSON_STRING = stringBuilder.toString().trim();

                if (JSON_STRING == "") {
                    Message.message(context, "JSON_STRING is null");
                } else {
                    jsonObject = new JSONObject(JSON_STRING);
                    jsonArray = jsonObject.getJSONArray("Patient_Details_From_Server");
                    String[] details = {"", "", "", "", "", "", "", "", "", "", ""};
                    JSONObject JO = jsonArray.getJSONObject(0);
                    details[0] = JO.getString("_id");
                    details[1] = JO.getString("Name");
                    details[2] = JO.getString("Password");
                    details[3] = JO.getString("Contact_No");
                    details[4] = JO.getString("Email");
                    details[5] = JO.getString("Gender");
                    details[6] = JO.getString("Dob");
                    details[7] = JO.getString("BloodGroup");
                    details[8] = JO.getString("Height");
                    details[9] = JO.getString("Weight");
                    details[10] = JO.getString("Metabolic_Disorders");

                    return details;

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;

        }

        @Override
        protected void onProgressUpdate(Void... avoid) {
            super.onProgressUpdate(avoid);
        }

        @Override
        protected void onPostExecute(String[] result) {
            String[] details = {"", "", "", "", "", "", "", "", "", "", ""};
            details = result;
            admNoShow.setText("Admission No.: " + details[0]);
            nameShow.setText("Name : " + details[1]);
            contactNoShow.setText("Contact No.: " + details[3]);
            emailShow.setText("Email : " + details[4]);
            genderShow.setText("Gender : " + details[5]);
            dobShow.setText("DOB : " + details[6]);
            bloodGroupShow.setText("Blood Group: " + details[7]);
            heightShow.setText("Height : " + details[8]);
            weightShow.setText("Weight: " + details[9]);
            metabolicDisorderShow.setText("Metabolic Disorders : " + details[10]);
        }
    }
}
