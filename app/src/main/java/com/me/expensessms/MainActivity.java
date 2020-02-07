package com.me.expensessms;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity {


    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    private String bank;
    private String period = "Daily";
    ArrayList<SMS> items;
    private String [] banks;
    DbHelper _db = new DbHelper(this);
    ListView smsList;
    BoxAdapter boxAdapter;
    int year1;
    int month1;
    int day1;
    int periodsListPosition;
    int bankListPosition;
    int widthOfSmslist=150;
    private TextView textOver, startPeriod, textBank;
    SharedPreferences sharedPreferences;
    OtherUtils otherUtils;
    private float factor;
    boolean isDbEmpty=false;

    private InterstitialAd interstitialAd;

    @Override
    protected void onResume() {
        super.onResume();

       // sharedPreferences = getSharedPreferences("qweqwe", Context.MODE_PRIVATE);
     /*  sharedPreferences =  getDefaultSharedPreferences(getApplicationContext());
       year1 =  sharedPreferences.getInt("year1", -1);
        month1 = sharedPreferences.getInt("month1", -1);
        day1 = sharedPreferences.getInt("day1", -1);


        startPeriod = findViewById(R.id.startPeriod);

        startPeriod.setText("From: \n"+ year1+ " "+ (month1)+" " +day1);*/
        startPeriod = findViewById(R.id.startPeriod);
        Bundle b = getIntent().getExtras();
        if (b!=null) {
            if (b.getInt("yearNew") != 0) {
                year1 = b.getInt("yearNew");
                month1 = b.getInt("monthNew");
                day1 = b.getInt("dayNew");
                startPeriod.setText("From: \n" + year1 + " " + (month1 + 1) + " " + day1);
            }
            //   drawList(year1, month1, day1);
        }
        }
/*        if (sharedPreferences.getInt("year1", 0)==0) {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
            cal.setTimeInMillis(_db.getLastDate(-1));
            year1 = cal.get(Calendar.YEAR);
            month1 = cal.get(Calendar.MONTH) + 1;
            day1 = cal.get(Calendar.DAY_OF_MONTH);
            startPeriod = findViewById(R.id.startPeriod);
            startPeriod.setText("From: \n" + year1 + " " + month1 + " " + day1);
            month1-=1;
        }*/






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences =  getDefaultSharedPreferences(getApplicationContext());
       // sharedPreferences = getSharedPreferences("qweqwe", Context.MODE_PRIVATE);
        year1 = sharedPreferences.getInt("year1", -1);
        month1 = sharedPreferences.getInt("month1", -1);
       day1 =  sharedPreferences.getInt("day1", -1);



        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-9469424738513188/1224862935");
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);

        factor = getApplicationContext().getResources().getDisplayMetrics().density;


     //   Map<String, ?> rrr = sharedPreferences.getAll();
//rrr.get("BANKS");
        setContentView(R.layout.activity_main);

        startPeriod = findViewById(R.id.startPeriod);
        startPeriod.setText(getString(R.string.from)+"\n"+ year1+ "-"+ ((month1)+1) +"-" +day1);

        DbHelper _db = new DbHelper(this);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS},
                MY_PERMISSIONS_REQUEST_SMS_RECEIVE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        if (sharedPreferences.getInt("year1", year1)==0) {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
            cal.setTimeInMillis(_db.getLastDate(-1));
            year1 = cal.get(Calendar.YEAR);
            month1 = cal.get(Calendar.MONTH) + 1;
            day1 = cal.get(Calendar.DAY_OF_MONTH);
            startPeriod = findViewById(R.id.startPeriod);
            startPeriod.setText(getString(R.string.from)+"\n" + year1 + " " + month1 + " " + day1);
            month1 -=1;
        }


      //  _db.deleteSome();
// _db.getAllinside(2);
        ///_db.sortDb();
       // _db.getAllinside(1);
        // Now create a SimpleDateFormat object.
       // _db.getBudgetCount();
      //_db.deleteAll();
        //_db.getMonthly("MKB");
        banks = loadBanks().toArray(new String[loadBanks().size()]);
        String[] banks1 = new String[banks.length+1];
        System.arraycopy(banks, 0, banks1, 1, banks.length);
        banks1[0] = getString(R.string.total);
        banks = banks1;



     /*   Bundle b = getIntent().getExtras();
        if (b!=null){
            //  if (b.getInt("year1")!=0) {
            year1 = b.getInt("year1");
            month1 = b.getInt("month1");
            day1 = b.getInt("day1");

            // }
            //   drawList(year1, month1, day1);

        }*/
        otherUtils = new OtherUtils(this);
       sharedPreferences =  getDefaultSharedPreferences(getApplicationContext());
       // String SearchOption = sharedPreferences.getString("SearchSMS", "Card");

if (_db.getLastDate(-1)!=null) { //if db is empty should fill info
    int month2 = month1+1;
    startPeriod.setText(getString(R.string.from) + "\n" + year1 + " " + month2 + " " + day1);
    startPeriod.setTextColor(getResources().getColor(R.color.yellow_color));
    otherUtils.drawList();
}
else {
    startPeriod.setText(getString(R.string.fill_info)+"\n"+getString(R.string.fill_info1));
    startPeriod.setTextSize(1, 13f);
    startPeriod.setTextColor(getResources().getColor(R.color.pink_color));
    Animation anim = new AlphaAnimation(0.0f, 1.0f);
    anim.setDuration(1000); //You can manage the blinking time with this parameter
    anim.setStartOffset(20);
    anim.setRepeatMode(Animation.REVERSE);
    anim.setRepeatCount(Animation.INFINITE);
    startPeriod.startAnimation(anim);
}








       //banks
        Spinner dropdown = findViewById(R.id.spinner1);
        int posBank = 0;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, banks);
        posBank = sharedPreferences.getInt("bankPos", 0);
        dropdown.setAdapter(adapter);
        dropdown.setSelection(posBank);
        dropdown.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bank = (String) adapterView.getItemAtPosition(i);
                bankListPosition = adapterView.getLastVisiblePosition();
                switchPeriods(period);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final Spinner periods = findViewById(R.id.periods);
        int posPeriod;
        String periodsArr[]= new String []{getString(R.string.operations), getString(R.string.daily),
                getString(R.string.weekly), getString(R.string.monthly), getString(R.string.yearly)};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, periodsArr);
        posPeriod = sharedPreferences.getInt("periodPos", 0);
        periods.setAdapter(adapter1);
        periods.setSelection(posPeriod);
        periods.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                period = (String) adapterView.getItemAtPosition(i);
                periodsListPosition = adapterView.getFirstVisiblePosition();
                switchPeriods(period);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




    }

private void switchPeriods(String period){


        if (period.equals(getString(R.string.operations)))
            createListSMS();


    if (period.equals(getString(R.string.daily))) {
        createListPeriods("'%j'", false);
        createListPeriods("'%j'", true);
    }


    if (period.equals(getString(R.string.weekly))) {
        createListPeriods("'%W'", false);
        createListPeriods("'%W'", true);
    }


    if (period.equals(getString(R.string.monthly))) {
        createListPeriods("'%m'", false);
        createListPeriods("'%m'", true);
    }


    if (period.equals(getString(R.string.yearly))) {
        createListPeriods("'%Y'", false);
        createListPeriods("'%Y'", true);
    }

}

    private void createListPeriods(String period, Boolean income) {

        String operator="<";
if (income) {


    smsList = findViewById(R.id.listIncome);
    ViewGroup.LayoutParams layoutParams = smsList.getLayoutParams();
    layoutParams.width = widthOfSmslist*(int)factor;
    smsList.setLayoutParams(layoutParams);
    textOver = findViewById(R.id.total_and_average_income_balance);
    operator = ">";

}
else {



    smsList = findViewById(R.id.list);
        textOver = findViewById(R.id.total_and_average_expense_balance);
        textBank = findViewById(R.id.bank_show);

}

        ViewGroup.LayoutParams layoutParams = smsList.getLayoutParams();
        layoutParams.width = widthOfSmslist*(int)factor;
        smsList.setLayoutParams(layoutParams);
        items = _db.getPeriods(bank, period, operator, year1, month1, day1);

        if (income){
            String local = getString(R.string.tot_date)+_db.getSumOfItems()+"\n"+
                    getString(R.string.average)+_db.getAverage()+"\n";

            if (!_db.getBalance(bank, banks).contains("-"))
                textOver.setText(local+ getString(R.string.balance)+_db.getBalance(bank, banks));
            else textOver.setText(local);

            textBank.setText(otherUtils.loadBanksName(bank));

        }
else{
            String local = getString(R.string.tot_date)+_db.getSumOfItems()+"\n"+
                    getString(R.string.average)+_db.getAverage()+"\n";
            if (_db.getBalance(bank, banks).contains("-"))
                textOver.setText(local+ getString(R.string.balance)+_db.getBalance(bank, banks));
            else textOver.setText(local);



        }



           boxAdapter = new BoxAdapter(this, items, 0);
           smsList.setAdapter(boxAdapter);
    }


    void createListSMS() {

    int report = 1; //operations

        //   Set<String> loadBanks = new addBank().loadBanks(); //SharedPrefs

        textOver = findViewById(R.id.total_and_average_expense_balance);
        textOver.setText("");
        textOver = findViewById(R.id.total_and_average_income_balance);
        textOver.setText("");
         smsList = findViewById(R.id.list);



        ViewGroup.LayoutParams layoutParams = smsList.getLayoutParams();
        layoutParams.width = 250*(int)factor;
        smsList.setLayoutParams(layoutParams);
        items = _db.getAll(bank);
      //  items.add(_db.getSum(bank));
       // items.get(items.size()-1);
        boxAdapter = new BoxAdapter(this, items, report);
       // ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, items);
        smsList.setAdapter(boxAdapter);

        smsList = findViewById(R.id.listIncome);
       items = new ArrayList<>();//Collections.singletonList("");
       ListAdapter listAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, items);
        smsList.setAdapter(listAdapter1);


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.onDate) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.budgetDateRangeHelp);

            final View view = getLayoutInflater().inflate(R.layout.budget_date_picker_layout, null, false);

            builder.setView(view);
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatePicker startDatePicker = (DatePicker) view.findViewById(R.id.startDate);


                    int yearNew = startDatePicker.getYear();
                    int monthNew = startDatePicker.getMonth();
                    int dayNew = startDatePicker.getDayOfMonth();



                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);

                    Bundle bundle = new Bundle();
                    bundle.putInt("yearNew", yearNew);
                    bundle.putInt("monthNew", monthNew);
                    bundle.putInt("dayNew", dayNew);
                    intent.putExtras(bundle);
                    startActivity(intent);

                    MainActivity.this.finish();
                }


            });
            builder.show();
        }

        if (id==R.id.editBank) {



            //Intent intent = new Intent(MainActivity.this, Advert.class);
            //startActivity(intent);

             Intent intent = new Intent(MainActivity.this, addBank.class);
           startActivity(intent);
        }


        if (id==R.id.about_text) {

            Intent intent = new Intent(MainActivity.this, about_.class);
            startActivity(intent);
        }
        return true;
    }


    final String BANKS = "BANKS";
    Set<String> bankSet = new HashSet<>();



    ArrayList<String> loadBanks(){
        sharedPreferences = getDefaultSharedPreferences(getApplicationContext());
        bankSet = sharedPreferences.getStringSet(BANKS, bankSet);
   //bankSet.remove(" ");
        ArrayList<String> list = new ArrayList<>();
        for (String bank: bankSet
             ) {
            String arr[] = bank.split(" ");
            list.add(arr[0]);
        }


        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // sharedPreferences = getSharedPreferences("qweqwe", Context.MODE_PRIVATE);
        sharedPreferences = getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("bankPos", bankListPosition);
        editor.putInt("periodPos", periodsListPosition);
        editor.putInt("year1", year1);
        editor.putInt("month1", month1);
        editor.putInt("day1", day1);
        editor.apply();
     //   Map<String, ?> rrr = sharedPreferences.getAll();
      //  sharedPreferences.getInt("day1", day1);



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SMS_RECEIVE) {
            // YES!!
            Log.i("TAG", "MY_PERMISSIONS_REQUEST_SMS_RECEIVE --> YES");
        }
    }

    @Override
    public void onBackPressed(){
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    finish();
                }
            });
        }else{
            super.onBackPressed();
        }

    }


}
