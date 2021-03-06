package org.feup.cmov.userticketapp.Controllers;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.feup.cmov.userticketapp.Helpers.CreditCardNumberChangeListener;
import org.feup.cmov.userticketapp.Models.CreditCard;
import org.feup.cmov.userticketapp.Models.DatabaseHelper;
import org.feup.cmov.userticketapp.Models.SharedDataSingleton;
import org.feup.cmov.userticketapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class PaymentActivity extends AppCompatActivity {

    private EditText mCreditCardNumberView;
    private EditText mCreditCardMonthView;
    private EditText mCreditCardYearView;
    private EditText mCreditCardCodeView;

    private SharedDataSingleton sharedData = SharedDataSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCreditCardNumberView = (EditText) findViewById(R.id.credit_card_number);
        mCreditCardNumberView.addTextChangedListener(new CreditCardNumberChangeListener());

        mCreditCardMonthView = (EditText) findViewById(R.id.credit_card_month);
        mCreditCardYearView = (EditText) findViewById(R.id.credit_card_year);
        mCreditCardCodeView = (EditText) findViewById(R.id.credit_card_code);

        Spinner spinner = (Spinner) findViewById(R.id.credit_card_spinner);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final CreditCard[] creditCards = DatabaseHelper.getAllCreditCards(db);

        if (creditCards.length > 0) {

            ArrayList<String> creditCardNumbers = new ArrayList<>();
            for (CreditCard creditCard : creditCards) {
                creditCardNumbers.add(String.valueOf(creditCard.getNumber()));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, creditCardNumbers);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    CreditCard selectedCard = creditCards[position];
                    mCreditCardNumberView.setText(String.valueOf(selectedCard.getNumber()));
                    mCreditCardMonthView.setText(String.valueOf(selectedCard.getMonth()));
                    mCreditCardYearView.setText(String.valueOf(selectedCard.getYear()));
                    mCreditCardCodeView.setText(String.valueOf(selectedCard.getCode()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        else {
            LinearLayout previousCardsLayout = (LinearLayout) findViewById(R.id.previous_cards_layout);
            previousCardsLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onProceedToCheckoutClickHandler(View view) {
        // Reset errors.
        mCreditCardNumberView.setError(null);

        String cardNumber = mCreditCardNumberView.getText().toString();
        String cardMonth = mCreditCardMonthView.getText().toString();
        String cardYear = mCreditCardYearView.getText().toString();
        String cardCode = mCreditCardCodeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(cardNumber)) {
            mCreditCardNumberView.setError(getString(R.string.error_field_required));
            focusView = mCreditCardNumberView;
            cancel = true;
        } else if (!isCreditCardNumberValid(cardNumber)) {
            mCreditCardNumberView.setError(getString(R.string.error_invalid_credit_card_number));
            focusView = mCreditCardNumberView;
            cancel = true;
        }

        if (TextUtils.isEmpty(cardMonth)) {
            mCreditCardMonthView.setError(getString(R.string.error_field_required));
            focusView = mCreditCardMonthView;
            cancel = true;
        } else if (!isCreditCardMonthValid(cardMonth)) {
            mCreditCardMonthView.setError(getString(R.string.error_invalid_credit_card_month));
            focusView = mCreditCardMonthView;
            cancel = true;
        }

        if (TextUtils.isEmpty(cardYear)) {
            mCreditCardYearView.setError(getString(R.string.error_field_required));
            focusView = mCreditCardYearView;
            cancel = true;
        } else if (!isCreditCardYearValid(cardYear)) {
            mCreditCardYearView.setError(getString(R.string.error_invalid_credit_card_year));
            focusView = mCreditCardYearView;
            cancel = true;
        }

        if (TextUtils.isEmpty(cardCode)) {
            mCreditCardCodeView.setError(getString(R.string.error_field_required));
            focusView = mCreditCardCodeView;
            cancel = true;
        } else if (!isCreditCardCodeValid(cardCode)) {
            mCreditCardCodeView.setError(getString(R.string.error_invalid_credit_card_code));
            focusView = mCreditCardCodeView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            CreditCard card = new CreditCard(
                    Long.parseLong(cardNumber.replaceAll("\\D", "")),
                    Integer.parseInt(cardMonth),
                    Integer.parseInt(cardYear),
                    Integer.parseInt(cardCode)
            );
            sharedData.setCreditCard(card);

            Intent intent = new Intent(getBaseContext(), CheckoutActivity.class);
            startActivity(intent);
        }
    }

    private boolean isCreditCardNumberValid(String creditCardNumber) {
        return true;
    }

    private boolean isCreditCardMonthValid(String cardMonth) {
        Integer month = Integer.parseInt(cardMonth, 10);
        return month >= 1 && month <= 12;
    }

    private boolean isCreditCardYearValid(String cardYear) {
        Integer year = Integer.parseInt(cardYear, 10) + 2000;
        Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return year >= currentYear;
    }

    private boolean isCreditCardCodeValid(String cardCode) {
        Integer code = Integer.parseInt(cardCode, 10);
        return code >= 111 && code <= 999;
    }
}

