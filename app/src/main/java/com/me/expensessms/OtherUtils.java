package com.me.expensessms;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

class OtherUtils  {
    private static String leftStr;
    private static int rrr=0;
    private static int ssss=0;
    private Context mContext;
    private String [] banks;
     public OtherUtils(Context context){
         mContext = context;
     }

     int drawList(){


         banks = loadBanks().toArray(new String[loadBanks().size()]);
         String[] banks1 = new String[banks.length+1];
         System.arraycopy(banks, 0, banks1, 1, banks.length);
         banks1[0] = "TOTAL";
         banks = banks1;


         DbHelper _db = new DbHelper(mContext);
         int nOfNewRecords=0;
         int recordsInside=0;
         SMS sms1 = null;
         List<SMS> listSMS = new ArrayList<>();
         String bankPrev = "";
         String prevLeftString = "";
         Date dateStart = null;
         String filter = null;
         String SMSorCardName;

         SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
         // Add 1 in month1 as its 0 based.
         //    String selectedDate = year + "-" +  (month + 1) + "-" + day;

         // Now create a start and end time for this date in order to setup the filter.
         Long dateLst = _db.getLastDate(1);
         // Calendar cal = Calendar.getInstance();
         //   cal.setTimeInMillis(dateLst);
         //    int ww = cal.get(Calendar.DAY_OF_MONTH);
         //   int www = cal.get(Calendar.MONTH);

         if (dateLst != null) {
             dateStart = new Date(dateLst);
         }
        /*try {

           dateStart = formatter.parse(selectedDate + "T00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
         //String smsBody = "";
         // Now create the filter and query the messages.

         if (dateStart != null) {
             filter = "date>" + dateStart.getTime();
         }
         final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
         Cursor cursor = mContext.getContentResolver().query(SMS_INBOX, null, filter, null, "date ASC");

         if (cursor != null) {
             //_db.sortDb();
             while (cursor.moveToNext()) {

                 // Convert date to a readable format.
                 Calendar calendar = Calendar.getInstance();
                 String date = cursor.getString(cursor.getColumnIndex("date"));
                 Long timestamp = Long.parseLong(date);
                 calendar.setTimeInMillis(timestamp);
                 Date finaldate = calendar.getTime();
                 String smsDate = finaldate.toString();
                 String smsBody = cursor.getString(cursor.getColumnIndex("body"));

                 Double numberReady = getNumber(smsBody);
                 String bankNameFromSMS = "";




                 if (numberReady == null) continue;

                 //create sorted arraylist
                     for (String bank: banks
                             ) {

                         if (bank.equals("TOTAL")) continue;


                        String arr[] = bank.split("!");
                        bank = arr[0];
                        SMSorCardName = arr[1];
                         if (SMSorCardName.equals("Card")) bankNameFromSMS = getCard(smsBody);
                         else if (SMSorCardName.equals("SMS")) bankNameFromSMS = cursor.getString(cursor.getColumnIndex("address"));




                         if (bankNameFromSMS!=null&&bankNameFromSMS.contains(bank)){
                             listSMS.add(new SMS(bankNameFromSMS, timestamp, numberReady, smsDate, leftStr));

                         }

                     }






            /*for (String ss: banks
                 ) {
                if (phoneNumber.contains(ss))
                    _db.insertBudget(new SMS(phoneNumber, timestamp, numberReady));
            }*/

                 //items.add(new SMS(phoneNumber, timestamp, numberReady));

             }

         }

         Collections.sort(listSMS, new Comparator<SMS>() {
             @Override
             public int compare(SMS o1, SMS o2) {
                 int bankResult = o1.getBank().compareTo(o2.getBank());

                 if (bankResult != 0) {
                     return bankResult;
                 }

                 return o1.getDate().compareTo(o2.getDate());
             }
         });

         for (SMS sms: listSMS
              ) {
             String bank = sms.getBank();
             String lStr = sms.getComment(); //left of string from amount around
             String lStrPrev = _db.getLastLeftStr(bank);
                 if (!bankPrev.equals(bank)) {
                     recordsInside = 0;
                 }

                     if (recordsInside > 1 && prevLeftString.equals(lStr)) {

                         _db.insertBudget(new SMS(bank, sms.getDate(), sms.getAmount(), null, lStr), 1);
                         nOfNewRecords++;
                         recordsInside++;
                         prevLeftString = lStr;
                     }

                     if (recordsInside <= 1) {
                         if (prevLeftString.equals(lStr)) {
                             _db.insertBudget(sms1, 1);
                             nOfNewRecords++;
                             _db.insertBudget(new SMS(bank, sms.getDate(), sms.getAmount(), null, lStr), 1);
                             nOfNewRecords++;

                         }

                         recordsInside++;

                         sms1 = new SMS(bank, sms.getDate(), sms.getAmount(), null, lStr);
                         prevLeftString = lStr;

                         //for update take last record from DB

                         if (recordsInside==1 && lStrPrev!=null){
                             _db.insertBudget(new SMS(bank, sms.getDate(), sms.getAmount(), null, lStr), 1);
                             nOfNewRecords++;
                           recordsInside++;
                           prevLeftString = lStrPrev;
                         }

                     }


                 bankPrev = bank;


         }





         cursor.close();

         _db.getDiffOfSMS(nOfNewRecords, banks);

int wer = _db.getBudgetCount();

//_db.getAllinside(1);

         return wer;
     }



     static Double getNumber(String s) {


         ArrayDeque<String> str = new ArrayDeque<>();

         Pattern pattern = Pattern.compile("\\D{5}\\d+[.,][\\d]{2}");
        // Pattern pattern = Pattern.compile("\\D{3}\\d+[.,][\\d]{2}\\D+$");
        // Pattern pattern = Pattern.compile("\\s\\d+[.,][\\d]{2}[\\s|\\S&&[^.]]");
         Pattern pattern1 = Pattern.compile("\\d+\\.[\\d]{2}");
         Matcher m = pattern.matcher(s);
         Double sss = null;
         String number = "";
         String wholeExpr = "";
         while (m.find()) {
              str.add(m.group());

             rrr++;
         }
         wholeExpr = str.peekLast();
        if (wholeExpr!=null) {
           // wholeExpr = str.peekLast();
            Matcher m1 = pattern1.matcher(wholeExpr);
            while (m1.find()) {
                number = m1.group();
                sss = Double.parseDouble(number);
                ssss++;
            }
            leftStr = wholeExpr.replace(number, "");
            if (leftStr.length()>6) leftStr = leftStr.substring(0,5);
        }
         return sss;
     }

     static String getCard(String s) {
         //     if (s.contains("5180")){
         //       s =s ;}
         ArrayDeque<String> str = new ArrayDeque<>();
         Pattern pattern = Pattern.compile("\\S[\\d]{4}");
         Pattern pattern1 = Pattern.compile("[\\d]{4}");
         Matcher m = pattern.matcher(s);
         String  sss = null;
         while (m.find()) {
             str.add(m.group());
             Matcher m1 = pattern1.matcher(str.peekFirst());
             while (m1.find()) {
                 sss = m1.group();
             }
         }
         return sss;
     }

    final String BANKS = "BANKS";
    Set<String> bankSet = new HashSet<>();

    ArrayList<String> loadBanks() {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(mContext);
        bankSet = sharedPreferences.getStringSet(BANKS, bankSet);
        ArrayList<String> list = new ArrayList<>();
        for (String bank : bankSet
                ) {
            String arr[] = bank.split(" ", 3);
            list.add(arr[0]+"!"+arr[1]);
        }

        return list;
    }

    String loadBanksName(String CardNOrBank) {
        String out="";
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(mContext);
        bankSet = sharedPreferences.getStringSet(BANKS, bankSet);
        ArrayList<String> list = new ArrayList<>();
        for (String bank : bankSet
                ) {
            String arr[] = bank.split(" ", 3);


            if (arr.length<3) {
                if (arr[0].equals(CardNOrBank)) out = CardNOrBank;
            }
            else if (arr[0].equals(CardNOrBank)) out = arr[2];
        }
     return out;
    }





}
