package com.kueiweiss.stu.mypaypal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentDetails extends AppCompatActivity {

    TextView textId, textAmount, textStatus;
    EditText mailEditText;
    Button sendMailBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        textId = findViewById(R.id.textId);
        textAmount = findViewById(R.id.textAmount);
        textStatus = findViewById(R.id.textStatus);
        
        mailEditText = findViewById(R.id.mailEditText);
        sendMailBtn = findViewById(R.id.sendMailBtn);
        sendMailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail();
            }
        });

        Intent intent = getIntent();

        try {
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("PaymentDetails"));
            showDetails(jsonObject.getJSONObject("response"), intent.getStringExtra("PaymentAmount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendMail() {
        Toast.makeText(this,  R.string.the_mail_sent, Toast.LENGTH_SHORT).show();

    }

    private void showDetails(JSONObject response, String paymentAmount) {
        try {
            textId.setText(response.getString("id"));
            textStatus.setText(response.getString("state"));
            textAmount.setText(MainActivity.moneyTypeSinge + paymentAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
