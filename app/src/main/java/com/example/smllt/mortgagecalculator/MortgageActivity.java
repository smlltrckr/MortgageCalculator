package com.example.smllt.mortgagecalculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MortgageActivity extends AppCompatActivity {
    private Button calculateBtn, resetBtn;
    private EditText homeValue,downPayment,interestRate,propertyTaxRate;
    private Spinner termsDropdown;
    private LinearLayout output;
    private TextView monthlyPaymentAmount,totalInterestPaid,totalPropertyTaxPaid,payOffDate;
    private Double monthlyPayment;
    private ScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mortgage);
        //sets up output view, makes it invisible just in case it was visible before
        output = (LinearLayout) findViewById(R.id.output);
        output.setVisibility(View.INVISIBLE);
        // Loads up the choices for the terms spinner.
        termsDropdown = (Spinner)findViewById(R.id.terms);
        String[] termsArray = new String[]{"15", "20", "25", "30", "40"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, termsArray);
        termsDropdown.setAdapter(adapter);
        //loads buttons
        calculateBtn = (Button) findViewById(R.id.calculate);
        resetBtn = (Button) findViewById(R.id.reset);
        //loads edit texts
        sv = (ScrollView) findViewById(R.id.scrollView);
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

    /**
     * This instantiates the initially hidden output fields
     */
    private void instantiateHiddenTexts() {
        monthlyPaymentAmount = (TextView) findViewById(R.id.monthlyPaymentAmount);
        totalInterestPaid = (TextView) findViewById(R.id.totalInterestPaid);
        totalPropertyTaxPaid = (TextView) findViewById(R.id.totalPropertyTaxPaid);
        payOffDate = (TextView) findViewById(R.id.payOffDate);
    }

    /**
     * This computes the initial monthly payments.
     * It also brings in other functions to fill in the rest of the output fields.
     * It makes the output visible to the user.
     */
    private void calculate() {
        //TODO:
        Double monthlyInterest = 0.0;
        Double principle = 0.0;
        Integer numPayments = 0;
        Double propertyTaxPercent = 0.0;
        Integer years = 0;
        Double dwnPayment;
        //for error checking
        Integer missingFieldCounter = 0;
        //parse to double for home value (aka principle)
        if (homeValue.getText().length() == 0){
            homeValue.setError("Home Value is required!");
            missingFieldCounter++;
        }else{
            if(downPayment.getText().length() == 0){
                dwnPayment = 0.0;
            } else {
                dwnPayment = Double.parseDouble( downPayment.getText().toString());
            }
            principle = ( Double.parseDouble( homeValue.getText().toString() ) - dwnPayment );
        }
        //parse to double for interest rate and divide by 12 to get monthly interest
        if (interestRate.getText().length() == 0){
            interestRate.setError("Interest Rate is Required!");
            missingFieldCounter++;
        }else{
            monthlyInterest = Double.parseDouble(interestRate.getText().toString()) / 100 / 12;
        }
        //parse to int for number of payments to be made.
        if(termsDropdown.getSelectedItem().toString().length() == 0){
            missingFieldCounter++;
        }else{
            years = Integer.parseInt(termsDropdown.getSelectedItem().toString());
            numPayments = years * 12;
        }
        //parse to double for property tax percentage
        if (propertyTaxRate.getText().length() == 0){
            propertyTaxRate.setError("Property Tax Rate is required!");
            missingFieldCounter++;
        }else{
            propertyTaxPercent = Double.parseDouble(propertyTaxRate.getText().toString()) / 100 ;
        }

        monthlyPayment = principle * ( (monthlyInterest * Math.pow(monthlyInterest + 1 , numPayments)) / (Math.pow(monthlyInterest + 1, numPayments)-1));
        //I am not sure how to use this formatter.
        //String.format("%.2f", monthlyPayment);
        if (missingFieldCounter > 0) {
            output.setVisibility(View.INVISIBLE);
            missingFieldCounter = 0;
            return;
        }
        output.setVisibility(View.VISIBLE);

        //format the monthly payment to a str and display it
        String monthlyPaymentToStr = monthlyPayment.toString();
        monthlyPaymentAmount.setText(monthlyPaymentToStr);
        //find the total interest paid and set it to a string. Set the TextView as a str
        String totIntPaidStr = interestPaid(monthlyPayment, numPayments, principle).toString();
        totalInterestPaid.setText(totIntPaidStr);
        //find the total property tax paid and set to a string then place in the TextView to show result.
        String totPropTaxPaid = propertyInterestPaid(years, propertyTaxPercent, principle).toString();
        totalPropertyTaxPaid.setText(totPropTaxPaid);
        payOffDate.setText(getFutureDate(new Date(), years * 365));

    }

    /**
     * @param monthlyPayment the monthly payment for the mortgage
     * @param numPayments how many payments will be made over the course of the mortgage
     * @param principle the principle of the loan (house val - down payment)
     * @return the total interest paid in dollar amount of the mortgage
     */
    Double interestPaid(Double monthlyPayment, Integer numPayments, Double principle){
        Double p = principle;
        Double m = monthlyPayment;
        Integer n = numPayments;
        Double interest =  (m*n) - p;
        return interest;
    }

    /**
     * @param yearsPaid the number of years paid for the load
     * @param propTaxPercent the percent in double form for the property tax (int percent / 100)
     * @param houseValue the house's original value.
     * @return the total dollar amount of property tax paid over the course of the mortgage.
     */
    Double propertyInterestPaid(Integer yearsPaid, Double propTaxPercent, Double houseValue){
        Integer y = yearsPaid;
        Double p = propTaxPercent;
        Double h = houseValue;

        return y * h * p;
    }

    /**
     *  Clears all the fields and sets Output LinearLayout to invisible
     */
    private void reset(){
        homeValue.getText().clear();
        downPayment.getText().clear();
        interestRate.getText().clear();
        propertyTaxRate.getText().clear();
        termsDropdown.setSelection(0);
        sv.fullScroll(ScrollView.FOCUS_UP);
        output.setVisibility(View.INVISIBLE);
    }
    /**
     * This instantiates the input fields.
     */
    private void instantiateEditTexts(){
        homeValue = (EditText) findViewById(R.id.homeValue);
        downPayment = (EditText) findViewById(R.id.downPayment);
        interestRate = (EditText) findViewById(R.id.interestRate);
        propertyTaxRate = (EditText) findViewById(R.id.propertyTaxRate);
    }

    public String getFutureDate(Date currentDate, int days) {
        DateFormat df = new SimpleDateFormat("MMMM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, days);

        Date futureDate = cal.getTime();
        return df.format(futureDate);
    }
}
