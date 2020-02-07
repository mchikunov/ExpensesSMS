package com.me.expensessms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class addBank extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    EditText editText, bankName;
    ArrayList<String> itemList;
    SharedPreferences sharedPreferences;
    final String BANKS = "BANKS";
    Set<String> bankSet = new HashSet<>();
    Set<String> deletedItems = new HashSet<>();
    int posInSelection;
    SparseBooleanArray sp;
    String deletedItem;
    private String newItem;
    ListView listV = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank);
        final ProgressBar progressBar = findViewById(R.id.pb);
        String[] items = loadBanks().toArray(new String[loadBanks().size()]);
        itemList = new ArrayList<String>(Arrays.asList(items));
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtview, itemList);
        listV = (ListView) findViewById(R.id.list);
        listV.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listV.setItemsCanFocus(false);

        editText = (EditText) findViewById(R.id.txtInput);
        bankName = findViewById(R.id.bankName);

        final OtherUtils otherUtils = new OtherUtils(this);
        sharedPreferences =  getDefaultSharedPreferences(getApplicationContext());
        String SearchOption = sharedPreferences.getString("SearchSMS", "Card");

        final RadioButton SMSRadioButton = (RadioButton) findViewById(R.id.radio_SMS);
        SMSRadioButton.setOnClickListener(radioButtonClickListener);

        RadioButton CardRadioButton = (RadioButton) findViewById(R.id.radio_Card);
        CardRadioButton.setOnClickListener(radioButtonClickListener);
        if (SearchOption.equals("Card")) CardRadioButton.setChecked(true);
        else {
            bankName.setVisibility(View.INVISIBLE);
            editText.setHint(getString(R.string.sms_from));
            SMSRadioButton.setChecked(true);
        }

//deleteBanks(" ");
        listV.setAdapter(adapter);
        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                sp = listV.getCheckedItemPositions();

                String str = "";
                for (posInSelection = 0; posInSelection < sp.size(); posInSelection++) {
                    str += sp.keyAt(posInSelection) + ",";
                }
                // Toast.makeText(getBaseContext(), "key "+str, Toast.LENGTH_LONG).show();


            }
        });





        Button btAdd = (Button) findViewById(R.id.btAdd);
        Button btDel = (Button) findViewById(R.id.btDel);
        Button buttonScan = findViewById(R.id.btScan);
        final DbHelper _db = new DbHelper(this);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                _db.deleteAll();

                new AsyncTask<Void, Void, Void>() {
                    int totalRecords;

                    @Override
                    protected void onPreExecute() {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        if (SMSRadioButton.isChecked())
                            totalRecords = otherUtils.drawList();
                        else totalRecords = otherUtils.drawList();
                        return null;
                    }


                    @Override
                    protected void onPostExecute(Void voids) {
                        if (SMSRadioButton.isChecked()) {

                            sharedPreferences = getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("SearchSMS", "SMS");
                            editor.apply();
                        } else {
                            sharedPreferences = getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor1 = sharedPreferences.edit();
                            editor1.putString("SearchSMS", "Card");
                            editor1.apply();
                        }
                        String text = "   Completed! " + totalRecords + " matches created in database   ";
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast toast = Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG);
                        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                        v.setTextColor(Color.BLACK);
                        toast.getView().setBackgroundColor(Color.GRAY);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }.execute();

                   /* new Thread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.VISIBLE);
                                }
                            });

                            otherUtils.drawList("Card");

                            progressBar.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });

                        }
                    }).start();*/


            }
        });

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bank = editText.getText().toString();
                String bankN = bankName.getText().toString();
                if (bankName.getVisibility()==View.INVISIBLE)
                    newItem = bank + " SMS ";

                else newItem = bank + " Card " +  bankN;
                // add new item to arraylist
                if (!(bank.equals("")&&bankN.equals(""))) {
                    saveBanks();
                    itemList.add(newItem);
                    editText.setText("");
                    bankName.setText("");
                    adapter.notifyDataSetChanged();
                }
                // notify listview of data changed


            }

        });

        btDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  String newItem = editText.getText().toString();
                for (posInSelection = 0; posInSelection < sp.size(); posInSelection++) {
                    deletedItem = itemList.get(sp.keyAt(posInSelection));
                    deletedItems.add(deletedItem);
                    deleteBanks(deletedItem);


                }
                for (String str : deletedItems
                        ) {
                    itemList.remove(str);
                }

                // notify listview of data changed
                sp.clear();
                adapter.notifyDataSetChanged();
            }

        });


        hideSoftKeyboard();
    }


    View.OnClickListener radioButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RadioButton rb = (RadioButton) v;
            switch (rb.getId()) {
                case R.id.radio_SMS:
                    bankName.setVisibility(View.INVISIBLE);
                    editText.setHint(getString(R.string.sms_from));
                 //   itemList.clear();
                  //  adapter.notifyDataSetChanged();

                    break;
                case R.id.radio_Card:

                    bankName.setVisibility(View.VISIBLE);
                    editText.setHint(getString(R.string.four_digits));
                 //   itemList.clear();
                 //   adapter.notifyDataSetChanged();
                    break;

                default:
                    break;
            }
        }
    };

    private void saveBanks() {
        if (newItem.equals(" ")) return;
        sharedPreferences = getDefaultSharedPreferences(getApplicationContext());
        bankSet = sharedPreferences.getStringSet(BANKS, bankSet);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        bankSet.add(newItem);
        editor.putInt("size", bankSet.size()); //to address one element save of hashset bug of sharedprefs
        editor.putStringSet(BANKS, bankSet);
        editor.commit();
    }

    private void hideSoftKeyboard() {
        if (getCurrentFocus() != null && getCurrentFocus() instanceof EditText) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }


    Set<String> loadBanks() {
        sharedPreferences = getDefaultSharedPreferences(getApplicationContext());
        return bankSet = sharedPreferences.getStringSet(BANKS, bankSet);
    }

    private void deleteBanks(String item) {
        if (item == null) return;
        sharedPreferences = getDefaultSharedPreferences(getApplicationContext());
        bankSet = sharedPreferences.getStringSet(BANKS, bankSet);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        bankSet.remove(item);
        editor.putInt("size", bankSet.size()); //to address one element save of hashset bug of sharedprefs
        editor.putStringSet(BANKS, bankSet);
        editor.commit();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(addBank.this, MainActivity.class);
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        addBank.this.finish();
    }
}

