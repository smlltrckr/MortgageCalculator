package com.example.smllt.mortgagecalculator;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.DecimalFormat;
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
                try  {
                    //closes keyboard when calculate is pressed
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                } catch (Exception e) {

                }
                //do calculations and show results
                calculate();
            }

        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try  {
                    //close keyboard when reset is pressed
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                } catch (Exception e) {

                }
                //clear all fields and hide results
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
            //no home value
            homeValue.setError("Home Value is required!");
            missingFieldCounter++;
        }else if( Double.parseDouble(downPayment.getText().toString()) > Double.parseDouble(homeValue.getText().toString())){
            //home value is less than the down payment
            downPayment.setError("Down payment must be smaller than house value");
        }else{
                //set 0's or actual values for down payment
                if(downPayment.getText().length() == 0){
                    dwnPayment = 0.0;
                } else {
                    dwnPayment = Double.parseDouble( downPayment.getText().toString());
                }
            //calculate principle
                principle = ( Double.parseDouble( homeValue.getText().toString() ) - dwnPayment );
            }

       //interest rate error checking
        if (interestRate.getText().length() == 0){
            interestRate.setError("Interest Rate is Required!");
            missingFieldCounter++;
        }else{
            //parse to double for interest rate and divide by 12 to get monthly interest
            monthlyInterest = Double.parseDouble(interestRate.getText().toString()) / 100 / 12;
        }
        //parse to int for number of payments to be made.
        if(termsDropdown.getSelectedItem().toString().length() == 0){
            missingFieldCounter++;
        }else{
            years = Integer.parseInt(termsDropdown.getSelectedItem().toString());
            numPayments = years * 12;
        }
        //if the property tax rate isn;t entered, make it 0. It is NOT required
        if (propertyTaxRate.getText().length() == 0){
           propertyTaxPercent = 0.0;
        }else{//parse to double for property tax percentage
            propertyTaxPercent = Double.parseDouble(propertyTaxRate.getText().toString()) / 100 ;
        }
        if (missingFieldCounter > 0) {
            output.setVisibility(View.INVISIBLE);
            missingFieldCounter = 0;
            return;
        }

        //this is the monthly payment without the property tax
        monthlyPayment = principle * ( (monthlyInterest * Math.pow(monthlyInterest + 1 , numPayments)) / (Math.pow(monthlyInterest + 1, numPayments)-1));
        monthlyPayment = monthlyPayment + ( propertyInterestPaid(years, propertyTaxPercent, Double.parseDouble(homeValue.getText().toString())) / numPayments);


        //START DISPLAYING OUTPUT
        output.setVisibility(View.VISIBLE);

        //format the monthly payment to a String and display it
        String monthlyPaymentToStr = format(monthlyPayment).toString();
        monthlyPaymentAmount.setText("$"+monthlyPaymentToStr);
        //find the total interest paid and set it to a string. Set the TextView as a str
        //NEED THIS NEXT LINE to reset monthly payment to not have property tax
        monthlyPayment = principle * ( (monthlyInterest * Math.pow(monthlyInterest + 1 , numPayments)) / (Math.pow(monthlyInterest + 1, numPayments)-1));
        String totIntPaidStr = format(interestPaid(monthlyPayment, numPayments, principle)).toString();
        totalInterestPaid.setText("$"+totIntPaidStr);
        //find the total property tax paid and set to a string then place in the TextView to show result.
        String totPropTaxPaid = format(propertyInterestPaid(years, propertyTaxPercent, Double.parseDouble(homeValue.getText().toString()))).toString();
        totalPropertyTaxPaid.setText("$"+totPropTaxPaid);
        //sets the payoff date
        payOffDate.setText(getFutureDate(new Date(), numPayments));

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

    /**
     *This will get a future date however many months ahead of the number of payment (12 * years) value
     * @param currentDate the date currently
     * @param months the number of months for the term of the loan (please pull in numPayment)
     * @return return the String of the date that the payments will be finished
     */
    public String getFutureDate(Date currentDate, int months) {
        DateFormat df = new SimpleDateFormat("MMMM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);

        cal.add(Calendar.MONTH, months-1);

        Date futureDate = cal.getTime();
        return df.format(futureDate);
    }

    /**
     * This will format decimals to X.XX and round them too.
     * @param numToFormat a double that needs formatting
     * @return a double that is formatted into X.XX
     */
	public Double format(Double numToFormat){
		Double d = numToFormat;
		DecimalFormat df = new DecimalFormat("#.##");
		d = Double.valueOf(df.format(d));

		return d;
	}
}
