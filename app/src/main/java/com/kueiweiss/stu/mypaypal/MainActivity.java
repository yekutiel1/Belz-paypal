package com.kueiweiss.stu.mypaypal;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.kueiweiss.stu.mypaypal.Config.Config;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    public static final int PAYPAL_REQUEST_CODE = 7171;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    Button btnPayNow;
    EditText editAmount;

    String amount = "";

    // add Spinner
    String moneyType;
    static String moneyTypeSinge;
    String[] typeMoneyArray;
    String[] typeMoneyWordArray;
    Spinner typeMoneySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // add to Spinner
        typeMoneyArray = new String[]{"$", "₪"};
        typeMoneyWordArray = new String[]{"USD", "ILS"};
        typeMoneySpinner = findViewById(R.id.typeSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, typeMoneyArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeMoneySpinner.setAdapter(adapter);

        typeMoneySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                moneyType = typeMoneyWordArray[i];
                moneyTypeSinge = typeMoneyArray[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        btnPayNow = findViewById(R.id.btnPayNow);
        editAmount = findViewById(R.id.editAmount);

        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editAmount.getText().toString().isEmpty()){
                    editAmount.setError(getString(R.string.enter_sum));

                }else {

                processPayment();
                }
            }
        });
    }

    private void processPayment() {
        amount = editAmount.getText().toString();
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), moneyType,
                " תרומה למרכז ", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);

    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE){

            if (resultCode == RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null){
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);

                        startActivity(new Intent(this, PaymentDetails.class)
                        .putExtra("PaymentDetails", paymentDetails)
                        .putExtra("PaymentAmount", amount));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }else if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, R.string.cancel, Toast.LENGTH_SHORT).show();
            }
        }else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID){
            Toast.makeText(this, R.string.invalid, Toast.LENGTH_SHORT).show();
        }
    }
}
