package com.example.smllt.mortgagecalculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MortgageActivity extends AppCompatActivity {
    private Button calculateBtn, resetBtn;
    private EditText homeValue,downPayment,interestRate,propertyTaxRate;
    private Spinner termsDropdown;
    private LinearLayout output;
    private TextView monthlyPaymentAmount,totalInterestPaid,totalPropertyTaxPaid,payOffDate;
    private Double monthlyPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mortgage);
        //sets up output view, makes it invisible just in case it was visible before
        output = (LinearLayout) findViewById(R.id.output);
        output.setVisibility(View.INVISIBLE);
        // Loads up the choices for the terms spinner.
        termsDropdown = (Spinner)findViewById(R.id.terms);
        String[] termsArray = new String[]{"", "15", "20", "25", "30", "40"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, termsArray);
        termsDropdown.setAdapter(adapter);
        //loads buttons
        calculateBtn = (Button) findViewById(R.id.calculate);
        resetBtn = (Button) findViewById(R.id.reset);
        //loads edit texts
        instantiateEditTexts();
        instantiateHiddenTexts();

        calculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }
    //instantiate the hidden part of the results
    private void instantiateHiddenTexts() {
        monthlyPaymentAmount = (TextView) findViewById(R.id.monthlyPaymentAmount);
        totalInterestPaid = (TextView) findViewById(R.id.totalInterestPaid);
        totalPropertyTaxPaid = (TextView) findViewById(R.id.totalPropertyTaxPaid);
        payOffDate = (TextView) findViewById(R.id.payOffDate);
    }

    /**
     * Does all the calculations when finished sets Output LinearLayout to Visible
     */
    private void calculate() {
        //TODO:
        Double monthlyInterest;
        Double principle;
        Integer numPayments;
        //parse to double for home value (aka principle)
        if (homeValue.getText().equals("") || homeValue == null){
            principle = 0.0;
        }else{
            principle = ( Double.parseDouble( homeValue.getText().toString() ) - Double.parseDouble( downPayment.getText().toString() ) );
        }
        //parse to double for interest rate and divide by 12 to get monthly interest
        if (interestRate.getText().equals("") || interestRate == null){
            monthlyInterest = 0.0;
        }else{
            monthlyInterest = Double.parseDouble(interestRate.getText().toString()) / 100 / 12;
        }
        //parse to int for number of payments to be made.
        if(termsDropdown.getSelectedItem().toString().equals("") || termsDropdown == null){
            numPayments = 0;
        }else{
            numPayments = Integer.parseInt(termsDropdown.getSelectedItem().toString()) * 12;
        }

        monthlyPayment = principle * ( (monthlyInterest * Math.pow(monthlyInterest + 1 , numPayments)) / (Math.pow(monthlyInterest + 1, numPayments)-1));
        //I am not sure how to use this formatter.
        //String.format("%.2f", monthlyPayment);
        output.setVisibility(View.VISIBLE);

        //format the monthly payment to a str and display it
        String monthlyPaymentToStr = monthlyPayment.toString();
        monthlyPaymentAmount.setText(monthlyPaymentToStr);
        //find the total interest paid and set it to a string. Set the TextView as astr
        String totIntPaidStr = interestPaid(monthlyPayment, numPayments, principle).toString();
        totalInterestPaid.setText(totIntPaidStr);


    }

    //doing a func to find total interest paid to keep calculate() clean
    Double interestPaid(Double monthlyPayment, Integer numPayments, Double principle){
        Double p = principle;
        Double m = monthlyPayment;
        Integer n = numPayments;
        Double interest = p - (m*n);
        return interest;
    }

    /**
     *  Clears all the fields and sets Output LinearLayout to invisible
     */
    private void reset(){
        homeValue.getText().clear();
        downPayment.getText().clear();
        interestRate.getText().clear();
        propertyTaxRate.getText().clear();
        output.setVisibility(View.INVISIBLE);
    }
    //this is to instantiate the edit texts so that the onCreate isnt so busy looking
    private void instantiateEditTexts(){
        homeValue = (EditText) findViewById(R.id.homeValue);
        downPayment = (EditText) findViewById(R.id.downPayment);
        interestRate = (EditText) findViewById(R.id.interestRate);
        propertyTaxRate = (EditText) findViewById(R.id.propertyTaxRate);
    }
}
