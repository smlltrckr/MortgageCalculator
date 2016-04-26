package com.example.smllt.mortgagecalculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class MortgageActivity extends AppCompatActivity {
    private Button calculateBtn, resetBtn;
    private EditText homeValue,downPayment,interestRate,propertyTaxRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mortgage);

        // Loads up the choices for the terms spinner.
        Spinner termsDropdown = (Spinner)findViewById(R.id.terms);
        String[] termsArray = new String[]{"", "15", "20", "25", "30", "40"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, termsArray);
        termsDropdown.setAdapter(adapter);
        //loada buttons
        calculateBtn = (Button) findViewById(R.id.calculate);
        resetBtn = (Button) findViewById(R.id.reset);
        //loads edit texts
        instantiateEditTexts();


        calculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }

    /**
     * Does all the calculations when finished sets Output LinearLayout to Visible
     */
    private void calculate() {
        //TODO:
    }

    /**
     *  Clears all the fields and sets Output LinearLayout to invisible
     */
    private void reset(){
        homeValue.getText().clear();
        downPayment.getText().clear();
        interestRate.getText().clear();
        propertyTaxRate.getText().clear();
    }
    //this is to instantiate the edit texts so that the onCreate isnt so busy looking
    private void instantiateEditTexts(){
        homeValue = (EditText) findViewById(R.id.homeValue);
        downPayment = (EditText) findViewById(R.id.downPayment);
        interestRate = (EditText) findViewById(R.id.interestRate);
        propertyTaxRate = (EditText) findViewById(R.id.propertyTaxRate);
    }
}
