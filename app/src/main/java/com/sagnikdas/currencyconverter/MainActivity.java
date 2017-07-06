package com.sagnikdas.currencyconverter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.webkit.HttpAuthHandler;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner sp1, sp2;
    EditText fromCurrency;
    TextView toCurrency, to, from;
    public static final String apiURL = "http://api.fixer.io/latest";
    String fromCur, toCur, baseCurrencyURL, rev_baseCurrencyURL;
    private String TAG = MainActivity.class.getSimpleName();
    Double toAmount, fromAmount, exchange_rate_amount;
    Button convert;
    ImageButton rev;
    String enteredValue;
    NumberFormat formatter;
    boolean doubleBackToExitPressedOnce = false;

    boolean clicked1 = false, clicked2 = false;

    private AdView mAdView;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        formatter = new DecimalFormat("#0.00000");// for formatting the output

        //************* Banner ad ********************
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        //************* Banner ad ********************

        //************* Intertial ad ********************

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        AdRequest ad = new AdRequest.Builder()
                .build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(ad);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });

        //************* Intertial ad ********************

        // code for from currency spinner
        sp1 = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> mySP1 = ArrayAdapter.createFromResource(this, R.array.fromCurrencies, R.layout.spinner_item);
        mySP1.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp1.setAdapter(mySP1);
        sp1.setOnItemSelectedListener(this);

        // code for to currency spinner
        sp2 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> mySP2 = ArrayAdapter.createFromResource(this, R.array.toCurrencies, R.layout.spinner_item);
        mySP2.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp2.setAdapter(mySP2);
        sp2.setOnItemSelectedListener(MainActivity.this);
        //************** Spinner ************************

        rev = (ImageButton) findViewById(R.id.reverse);
        fromCurrency = (EditText) findViewById(R.id.fromCurrencyAmount);
        toCurrency = (TextView) findViewById(R.id.toCurrencyAmount);
        fromAmount = toAmount = 1.0;
        to = (TextView) findViewById(R.id.to);
        from = (TextView) findViewById(R.id.from);
        to.setText("To");
        from.setText("From");



        rev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveSpinners(sp1, sp2);
            }
        });


        if(fromCurrency.getText().toString().contentEquals("") || fromCurrency.getText().toString().contentEquals(null)){
            fromCurrency.setText("1.0");
        }

        //Getting the currency codes from the selected values from the spinners
        /*fromCur = sp1.getSelectedItem().toString().substring(0,3);
        toCur = sp2.getSelectedItem().toString().substring(0,3);
*/
    }

    void moveSpinners(View v1, View v2){

        Spinner a1 = (Spinner) v1;
        Spinner a2 = (Spinner) v2;

        RelativeLayout root = (RelativeLayout) findViewById(R.id.root_layout);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);

        //int statusBarOffset = dm.heightPixels - root.getMeasuredHeight();

        int originalPosSp1[] = new int[2];
        v1.getLocationOnScreen( originalPosSp1 ); //fetches the coordinates of the spinner2 (x,y)

        int originalPosSp2[] = new int[2];
        v2.getLocationOnScreen( originalPosSp2 );

        int translateDistance = originalPosSp2[1] - originalPosSp1[1];


        if(!clicked1) {
            TranslateAnimation anim1 = new TranslateAnimation(0, 0, 0, translateDistance);
            anim1.setDuration(500);
            anim1.setFillAfter(true);
            v1.startAnimation(anim1);

            fromCur = a2.getSelectedItem().toString().substring(0,3);
            toCur = a1.getSelectedItem().toString().substring(0,3);
            clicked1 = true;
        }
        else{
            TranslateAnimation anim1 = new TranslateAnimation(0, 0, translateDistance, 0);
            anim1.setDuration(500);
            anim1.setFillAfter(true);
            v1.startAnimation(anim1);

            fromCur = a1.getSelectedItem().toString().substring(0,3);
            toCur = a2.getSelectedItem().toString().substring(0,3);
            clicked1 = false;
        }


        if(!clicked2) {
            TranslateAnimation anim2 = new TranslateAnimation(0, 0, 0, -translateDistance);
            anim2.setDuration(500);
            anim2.setFillAfter(true);
            v2.startAnimation(anim2);

            fromCur = a2.getSelectedItem().toString().substring(0,3);
            toCur = a1.getSelectedItem().toString().substring(0,3);
            clicked2 = true;
        }
        else{
            TranslateAnimation anim2 = new TranslateAnimation(0, 0, -translateDistance, 0);
            anim2.setDuration(500);
            anim2.setFillAfter(true);
            v2.startAnimation(anim2);

            fromCur = a1.getSelectedItem().toString().substring(0,3);
            toCur = a2.getSelectedItem().toString().substring(0,3);
            clicked2 = false;
        }

        //***********************************************************************
        enteredValue = fromCurrency.getText().toString();

        if(!(enteredValue.equals("")) && fromCur.equals(toCur)){
            fromAmount = Double.parseDouble(enteredValue); //Amount entered by user in EditText view
            toCurrency.setText(toCur+" "+String.valueOf(formatter.format(fromAmount)));

        }
        else if(!(enteredValue.equals("")) && !fromCur.equals(toCur)){
            fromAmount = Double.parseDouble(enteredValue); //Amount entered by user in EditText view
            Toast.makeText(this, fromCur+" --> "+toCur, Toast.LENGTH_SHORT).show();
            to.setText("To");
            from.setText("From");
            new GetExchangeRates().execute();
        }
        else if(enteredValue.equals("")){
            fromAmount = 1.0; //setting default value
            toCurrency.setText(toCur+" "+String.valueOf(formatter.format(0.0)));
            Toast.makeText(this, "Please Enter a value", Toast.LENGTH_SHORT).show();
        }
        //***********************************************************************




        Toast.makeText(this, fromCur+"---->"+toCur, Toast.LENGTH_SHORT).show();

    }

    //used for showing the large ads.
    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

   @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_refresh:
                Intent intent=new Intent(MainActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void conversionNow(View view){
        enteredValue = fromCurrency.getText().toString();

        if(!(enteredValue.equals("")) && fromCur.equals(toCur)){
            fromAmount = Double.parseDouble(enteredValue); //Amount entered by user in EditText view
            //check from and to are same currency or not. --> if yes then --> set textview| else run thread.
            toCurrency.setText(toCur+" "+String.valueOf(formatter.format(fromAmount)));

        }
        else if(!(enteredValue.equals("")) && !fromCur.equals(toCur)){
            fromAmount = Double.parseDouble(enteredValue); //Amount entered by user in EditText view
            Toast.makeText(this, fromCur+" --> "+toCur, Toast.LENGTH_SHORT).show();
            to.setText("To");
            from.setText("From");
            new GetExchangeRates().execute();
        }
        else if(enteredValue.equals("")){
            fromAmount = 1.0; //setting default value
            toCurrency.setText(toCur+" "+String.valueOf(formatter.format(0.0)));
            Toast.makeText(this, "Please Enter a value", Toast.LENGTH_SHORT).show();
        }

    }

    /*{

        enteredValue = fromCurrency.getText().toString();

        if(!(enteredValue.equals("")) && fromCur.equals(toCur)){
            fromAmount = Double.parseDouble(enteredValue); //Amount entered by user in EditText view
            //check from and to are same currency or not. --> if yes then --> set textview| else run thread.
            toCurrency.setText(fromCur+" "+String.valueOf(formatter.format(fromAmount)));

        }
        else if(!(enteredValue.equals("")) && !fromCur.equals(toCur)){
            fromAmount = Double.parseDouble(enteredValue); //Amount entered by user in EditText view
            //new GetExchangeRates().execute();
            toAmount = fromAmount * (1/exchange_rate_amount);
            toCurrency.setText(fromCur+" "+String.valueOf(formatter.format(toAmount)));
            to.setText("From");
            from.setText("To");

        }
        else if(enteredValue.equals("")){
            fromAmount = 1.0; //setting default value
            toCurrency.setText(fromCur+" "+String.valueOf(formatter.format(0.0)));
            Toast.makeText(this, "Please Enter a value", Toast.LENGTH_SHORT).show();
        }


    }*/

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()){
            case R.id.spinner1:
                fromCur = sp1.getSelectedItem().toString().substring(0,3);
                break;
            case R.id.spinner2:
                toCur = sp2.getSelectedItem().toString().substring(0,3);
                break;
        }

        if(fromCur.equals(toCur)){
            exchange_rate_amount = 1.0; //added now
            toAmount = exchange_rate_amount * fromAmount;
            toCurrency.setText(toCur+" "+String.valueOf(toAmount));
        }



    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private class GetExchangeRates extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            baseCurrencyURL = apiURL+"?base="+fromCur;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response

            String jsonStr = sh.makeServiceCall(baseCurrencyURL);

            Log.e(TAG, "Response from url: " + jsonStr);

            if(jsonStr!=null){

                try{
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject rate = jsonObj.getJSONObject("rates");
                    String exchangeRate = rate.getString(toCur);
                    if(!(exchangeRate.equals(""))){
                        exchange_rate_amount = Double.parseDouble(exchangeRate);
                    }

                    if(!(exchange_rate_amount.equals(""))){
                        toAmount = exchange_rate_amount * fromAmount;

                    }


                }
                catch (final JSONException e){
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                   runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            toCurrency.setText(toCur+" "+String.valueOf(formatter.format(toAmount)));
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
