package com.me.expensessms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SMSExpense.db";

    private SQLiteDatabase db;
    private static final int ORIGINAL_DATABASE_VERSION = 3;
    private double average;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, ORIGINAL_DATABASE_VERSION);
    }
Double sumOfItems =0D;





    static class dbIds implements BaseColumns {
        public static final String TABLE = "SMS";
        public static final String BANK = "bank";
        public static final String DATE = "date";
        public static final String DATE_SQL = "dateSql";
        public static final String AMOUNT = "amount";
        public static final String CATEGORY = "category";
        public static final String COMMENT = "comment";

        public static final String TABLE_1 = "res";



    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;


        db.execSQL(
                "create table " + dbIds.TABLE + "(" +
                        dbIds._ID + " INTEGER primary key autoincrement, " +
                        dbIds.BANK + " text, " + dbIds.DATE + " REAL, " + dbIds.DATE_SQL + " NUMERIC, " + dbIds.AMOUNT + " REAL, " +
                        dbIds.CATEGORY + " text, " + dbIds.COMMENT + " text)");

        db.execSQL(
                "create table " + dbIds.TABLE_1 + "(" +
                        dbIds._ID + " INTEGER primary key autoincrement, " +
                        dbIds.BANK + " text, " + dbIds.DATE + " REAL, " + dbIds.DATE_SQL + " NUMERIC, " + dbIds.AMOUNT + " REAL, " +
                        dbIds.CATEGORY + " text, " + dbIds.COMMENT + " text)");




    }

    public void insertBudget(SMS sms, int table)
    {
      //  db.execSQL("DROP TABLE IF EXISTS " + dbIds.TABLE);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
       // SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:MM:SS");
        SQLiteDatabase writableDb = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbIds.BANK, sms.getBank());
        contentValues.put(dbIds.DATE, sms.getDate());
        contentValues.put(dbIds.DATE_SQL, sms.getDate()/1000L);
        //contentValues.put(dbIds.DATE_SQL, dateFormat.format((sms.getDate())));
        contentValues.put(dbIds.AMOUNT, sms.getAmount());
        contentValues.put(dbIds.CATEGORY, sms.getCategory());
        contentValues.put(dbIds.COMMENT, sms.getComment());


        if (table == 1)
        {final long newId = writableDb.insert(dbIds.TABLE, null, contentValues);}

        else if (table == 2)
        {final long newId = writableDb.insert(dbIds.TABLE_1, null, contentValues);}


    }
void deleteSome(double num){
db = getWritableDatabase();

    int deleted = db.delete(dbIds.TABLE_1, dbIds.AMOUNT + "<? AND "+ dbIds.AMOUNT + ">?" , new String[]{String.valueOf(num+0.01), String.valueOf(num-0.01)});

      //  int deleted = db.delete(dbIds.TABLE_1, dbIds.AMOUNT + "<? AND "+ dbIds.AMOUNT + ">?" , new String[]{String.valueOf(-185.00), String.valueOf(-186.00)});
        int tt =0;
}


    void deleteAll(){
        db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + dbIds.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + dbIds.TABLE_1);

        db.execSQL(
                "create table " + dbIds.TABLE + "(" +
                        dbIds._ID + " INTEGER primary key autoincrement, " +
                        dbIds.BANK + " text, " + dbIds.DATE + " REAL, " + dbIds.DATE_SQL + " NUMERIC, " + dbIds.AMOUNT + " REAL, " +
                        dbIds.CATEGORY + " text, " + dbIds.COMMENT + " text)");
      //  db.execSQL("DELETE FROM " + dbIds.TABLE);
        db.execSQL(
                "create table " + dbIds.TABLE_1 + "(" +
                        dbIds._ID + " INTEGER primary key autoincrement, " +
                        dbIds.BANK + " text, " + dbIds.DATE + " REAL, " + dbIds.DATE_SQL + " NUMERIC, " + dbIds.AMOUNT + " REAL, " +
                        dbIds.CATEGORY + " text, " + dbIds.COMMENT + " text)");


    }


    public List<String> getAllSMS (String bank){
        List<String> smsAllList = new ArrayList<>();



        final Calendar date = Calendar.getInstance();

        db = getReadableDatabase();
       // Cursor c = db.rawQuery("SELECT * FROM " + dbIds.TABLE, null);
        Cursor c = db.query(dbIds.TABLE, null, dbIds.BANK + " like ?" , new String[] {"%"+bank+"%"}, null, null, null);

        if (c.moveToFirst()){
            SMS sms =new SMS();
            do {
                String string = "";
                StringBuilder str = new StringBuilder();

                sms.setBank(c.getString(c.getColumnIndex(dbIds.BANK)));
                sms.setDate(c.getLong(c.getColumnIndex(dbIds.DATE)));
                sms.setAmount(c.getDouble(c.getColumnIndex(dbIds.AMOUNT)));
                sms.setCategory(c.getString(c.getColumnIndex(dbIds.CATEGORY)));
                sms.setComment(c.getString(c.getColumnIndex(dbIds.COMMENT)));

                date.setTimeInMillis(sms.getDate());
                String  finaldate = date.getTime().toString();

                str.append(sms.getAmount()).toString();
                string = str.append(finaldate).toString();
                smsAllList.add(string);

            } while (c.moveToNext());
        }
        c.close();
        return smsAllList;

    }


    public SMS getSum (String bank){

        SMS sms =new SMS();
        db = getReadableDatabase();
        Cursor c = db.query(dbIds.TABLE, new String []{"sum(" + dbIds.AMOUNT + ")"}, dbIds.BANK + " like ?" , new String[] {"%"+bank+"%"}, null, null, null);
        if (c.moveToFirst()){
            double sss = c.getDouble(0);
            sms.setAmount(sss);
            c.close();
        }
        return sms;

    }


    public ArrayList<SMS> getPeriods(String bank, String period, String operator, int year, int month, int day){
        sumOfItems = 0D;
        Long oneDayInMillis = 24*60*60*1000L;

        Long dateInc;
        if (year!=0&&month!=0&&day!=0) {
            Calendar calendar = new GregorianCalendar(year, month, day);
            dateInc = calendar.getTimeInMillis();
        }
        else dateInc = getLastDate(-1)-oneDayInMillis*2;

        ArrayList<SMS> smsList = new ArrayList<>();
        db = getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM EEEE");
        SimpleDateFormat dateFormatYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM yyyy");
        NumberFormat f = NumberFormat.getInstance();
        Cursor c;
        String date2="";

        if (bank.equals("TOTAL")){

           c = db.rawQuery("SELECT strftime("+period+", dateSql, 'unixepoch'), SUM(amount), MAX(date) FROM "
                    + dbIds.TABLE_1 + " WHERE amount"+operator+"0 AND date>="+dateInc+" GROUP BY strftime("+period+", dateSql, 'unixepoch')" +
                    " ORDER BY strftime("+period+", dateSql, 'unixepoch') DESC", null);
        }
        else {
            c = db.rawQuery("SELECT strftime(" + period + ", dateSql, 'unixepoch'), SUM(amount), MAX(date) FROM "
                    + dbIds.TABLE_1 + " WHERE bank LIKE ? AND amount" + operator + "0 AND date>="+dateInc+" GROUP BY strftime(" + period + ", dateSql, 'unixepoch')" +
                    " ORDER BY strftime(" + period + ", dateSql, 'unixepoch') DESC", new String[]{"%" + bank + "%"});
        }


       /* Cursor c = db.rawQuery("SELECT strftime('%m', dateSql, 'unixepoch'), SUM(amount) FROM "
                + dbIds.TABLE_1 + " WHERE bank LIKE ? AND amount>0 GROUP BY strftime('%m', dateSql, 'unixepoch')" +
                " ORDER BY strftime('%m', dateSql, 'unixepoch') DESC", new String[]{"%"+bank+"%"});
*/
        if (c.moveToFirst()){

            do {
                SMS sms = new SMS();
                String str1  = c.getString(0);
                Double str2 =  c.getDouble(1);
                Long date1 =  c.getLong(2);
                String str3 = f.format(str2);

                sumOfItems += str2;
               Date date = new Date(date1);

                   date2 = dateFormat.format(date);

               switch (period){

                   case "'%Y'": date2 = dateFormatYear.format(date);
                   break;
                   case "'%m'": date2 = dateFormatMonth.format(date);
                   break;
               }

                sms.setDateString(date2);
                sms.setAmountString(str3);
                sms.setBank(bank);


                smsList.add(sms);
            } while (c.moveToNext());
        }
        average = sumOfItems/(smsList.size());
        c.close();
        return smsList;

    }

    public String  getSumOfItems() {
      //  NumberFormat f = NumberFormat.getInstance();
        DecimalFormat f1 = new DecimalFormat("#,##0");
        return f1.format(sumOfItems);
    }

    public String getAverage() {
        DecimalFormat f1 = new DecimalFormat("#,##0");
        return f1.format(average);
    }

    public ArrayList<SMS> getAll (String bank){
        sumOfItems = 0D;
        ArrayList<SMS> smsList = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMMM H:mm:ss EE");
        //DecimalFormat f = new DecimalFormat("##.00");
        NumberFormat f = NumberFormat.getInstance();
        db = getReadableDatabase();
        Cursor c = null;

        //  Cursor c = db.query(dbIds.TABLE_1, null, dbIds.BANK + " like ? AND amount>?" , new String[] {"%"+bank+"%", "0"}, null, null, null);

        if (bank.equals("TOTAL")) {
            c = db.query(dbIds.TABLE_1, null, null,
                    null, null, null, dbIds.DATE+" DESC");

        }
        else {
            c = db.query(dbIds.TABLE_1, null, dbIds.BANK + " like ? ",
                    new String[]{"%" + bank + "%"}, null, null, dbIds._ID + " DESC");
        }

        if (c.moveToFirst()){

            do {
                SMS sms = new SMS();
                Double amount1  = c.getDouble(c.getColumnIndex(dbIds.AMOUNT));
                sumOfItems+=amount1;
                String bank1 = c.getString(c.getColumnIndex(dbIds.BANK));
                String amount = f.format(amount1);
                Long date1 = c.getLong(c.getColumnIndex(dbIds.DATE));
                Date date0 = new Date(date1);
                String date = formatter.format(date0);
                sms.setDateString(date);
                sms.setAmountString(amount);
                sms.setBank(bank1);
                smsList.add(sms);
            } while (c.moveToNext());
        }

        average = sumOfItems/(smsList.size());
        c.close();
        return smsList;

    }


    public void sortDb(){

        db = getWritableDatabase();

       Cursor c = db.query(dbIds.TABLE, null, null, null,
                null, null, dbIds._ID + " DESC");

        c.close();
    }

    public List<String > getAllinside (int table){
        String table1=null;

        if (table==1) table1=dbIds.TABLE;
        else if (table==2) table1=dbIds.TABLE_1;

        List<String> smsList = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        DecimalFormat f = new DecimalFormat("##.00");
        db = getReadableDatabase();

      //  Cursor c = db.query(dbIds.TABLE_1, null, dbIds.BANK + " like ? AND amount>?" , new String[] {"%"+bank+"%", "0"}, null, null, null);
        Cursor c = db.query(table1, null, null, null, null, null, null);

        if (c.moveToFirst()){

            do {
               Double amount1  = c.getDouble(c.getColumnIndex(dbIds.AMOUNT));
               int id = c.getInt(c.getColumnIndex(dbIds._ID));

               String comment = c.getString(c.getColumnIndex(dbIds.COMMENT));
                String  bank  = c.getString(c.getColumnIndex(dbIds.BANK));
               String amount = f.format(amount1);
               Long date1 = c.getLong(c.getColumnIndex(dbIds.DATE));
               Date date0 = new Date(date1);
               String date = formatter.format(date0);
                smsList.add(id+" "+bank+ " " + date + " " +amount+ " "+ comment);
            } while (c.moveToNext());
        }

        c.close();
        return smsList;

    }


    public List<String > getDates (String bank){
        List<String> smsList = new ArrayList<>();
        SMS sms =new SMS();
        db = getReadableDatabase();
       /* Cursor cc = db.rawQuery("SELECT strftime('%d','now')", null);
        cc.moveToFirst();
       int asd =  cc.getInt(0);*/

        Cursor c = db.rawQuery("SELECT strftime('%Y-%m', dateSql, 'unixepoch'), amount FROM "
                + dbIds.TABLE + " WHERE bank LIKE ? ", new String[]{"%"+bank+"%"});

        if (c.moveToFirst()){

            do {
                String str1  = c.getString(0);
             String str2 =  c.getString(1);
                smsList.add(str1 + " " + str2);
            } while (c.moveToNext());
        }

        c.close();
        return smsList;

    }




    /** get last date from db  = 1, first date = -1;*/

    public Long getLastDate(int r){
        Cursor c = null;
        Long dateLast = null;
        db = getReadableDatabase();
        if (r == 1){
        c = db.query(dbIds.TABLE, new String []{dbIds.DATE}, null, null, null, null,
                dbIds.DATE + " DESC", "1");}
                else if (r == -1){c = db.query(dbIds.TABLE, new String []{dbIds.DATE}, null, null, null, null,
                dbIds.DATE + " ASC", "1");}


        if (c.moveToFirst()){
        dateLast = c.getLong(c.getColumnIndex(dbIds.DATE));}
        c.close();
        return dateLast;

    }

    public String getLastLeftStr(String bank){
        Cursor c = null;
        String leftStrLast = null;
        db = getReadableDatabase();

            c = db.query(dbIds.TABLE, new String []{dbIds.COMMENT, dbIds.AMOUNT}, dbIds.BANK + " like ?", new String[]{"%" + bank + "%"}, null, null,
                    dbIds.DATE + " DESC", "1");



        if (c.moveToFirst()){
            leftStrLast = c.getString(0);

        }
        c.close();
        return leftStrLast;

    }


    public void getDiffOfSMS(int numberOfNewRecords, String[] banks){
        Map<String, Integer> map = new HashMap<>();
        DecimalFormat f = new DecimalFormat("#,##0.00");

        String  bankFromDbLike;

        db = getReadableDatabase();

        if (getBudgetCount()<=1){
            getDiffOfSMSInitial(banks);
            return;
        }

        if (numberOfNewRecords==0) return;



        Cursor c = db.query(dbIds.TABLE, new String[]{"bank"}, null, null,
                null, null, dbIds._ID + " DESC", String.valueOf(numberOfNewRecords));

        for (String bank: banks
             ) {


            if (bank.equals("TOTAL")) continue;


            String arr[] = bank.split("!");
            bank = arr[0];
            // Cursor c = db.query(dbIds.TABLE, new String[] {"bank", "count(bank)"}, null, null,
            //         "bank", null, dbIds._ID + " DESC", String.valueOf(numberOfNewRecords));

            if (c.moveToFirst()) {
                int sdfsf = c.getCount();


                do {
                    bankFromDbLike = c.getString(0);
                    if (bankFromDbLike.contains(bank)) {
                        int count = map.containsKey(bank) ? map.get(bank) : 0;
                        map.put(bank, count + 1);
                    }


                } while (c.moveToNext());


            }
        }
        c.close();

     /*   for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue()==null) entry.setValue(0);
        }*/

        for (String bank: banks
             ) {
            final Calendar date = Calendar.getInstance();
            Double amount1 = 0D;
            Double amount2 = 0D;
            int countTwice = 1;
            int count=0;

            if (bank.equals("TOTAL")) continue;


            String arr[] = bank.split("!");
            bank = arr[0];

//get differences and put to table2
            if (map.get(bank)!=null) {

                c = db.query(dbIds.TABLE, null, dbIds.BANK + " like ?", new String[]{"%" + bank + "%"}, null,
                        null, dbIds._ID + " DESC", String.valueOf(map.get(bank) + 1));
                if (c.moveToFirst()) {
                    SMS sms = new SMS();
                    do {
                        int id  = c.getInt(c.getColumnIndex(dbIds._ID));
                        amount1 = c.getDouble(c.getColumnIndex(dbIds.AMOUNT));

                        if (countTwice == 1) {
                            countTwice++;


                            sms.setBank(c.getString(c.getColumnIndex(dbIds.BANK)));
                            sms.setDate(c.getLong(c.getColumnIndex(dbIds.DATE)));
                            sms.setCategory(c.getString(c.getColumnIndex(dbIds.CATEGORY)));
                            sms.setComment(c.getString(c.getColumnIndex(dbIds.COMMENT)));
                        } else {

                            if (amount1 != 0 && amount2 != 0) {
                                sms.setAmount(amount2 - amount1);

                                insertBudget(sms, 2); //insert to the second db table differences

                                sms.setBank(c.getString(c.getColumnIndex(dbIds.BANK)));
                                sms.setDate(c.getLong(c.getColumnIndex(dbIds.DATE)));
                                sms.setCategory(c.getString(c.getColumnIndex(dbIds.CATEGORY)));
                                sms.setComment(c.getString(c.getColumnIndex(dbIds.COMMENT)));

                            }
                        }
                        amount2 = c.getDouble(c.getColumnIndex(dbIds.AMOUNT));
                        date.setTimeInMillis(sms.getDate());
                        date.get(Calendar.DAY_OF_MONTH);


                    } while (c.moveToNext());
                }
                c.close();
            }

        }
    }

    public String getBalance(String bank, String[] banks){
        Double amount = null;
        Double res = 0D;

        if (bank.equals("TOTAL")){

            for (String ss: banks
                 ) {
                Cursor cc = db.query(dbIds.TABLE, new String []{"amount"}, dbIds.BANK + " like ?", new String[]{"%" + ss + "%"}, null,
                        null, dbIds._ID + " DESC", "1");
                if (cc.moveToFirst()) {
                    amount = cc.getDouble(0);
                    res += amount;
                }
                cc.close();

                amount = res;
            }

        }
        else {
            Cursor c = db.query(dbIds.TABLE, new String []{"amount"}, dbIds.BANK + " like ?", new String[]{"%" + bank + "%"}, null,
                    null, dbIds._ID + " DESC", "1");
            if (c.moveToFirst()) {

                amount = c.getDouble(0);
            }
            c.close();

        }
        DecimalFormat f1 = new DecimalFormat("#,##0.00");
        if (amount!=null) return f1.format(amount);
        else return f1.format(0);

    }



    public void getDiffOfSMSInitial(String[] banks){

        DecimalFormat f = new DecimalFormat("##.00");



        db = getReadableDatabase();

        for (String bank: banks
                ) {
            final Calendar date = Calendar.getInstance();
            Double amount1 = 0D;
            Double amount2 = 0D;
            int countTwice = 1;
            int count=0;

            if (bank.equals("TOTAL")) continue;


            String arr[] = bank.split("!");
            bank = arr[0];

//get differences and put to table2
            Cursor c = db.query(dbIds.TABLE, null, dbIds.BANK + " like ?", new String[]{"%" + bank + "%"}, null, null, null);
            if (c.moveToFirst()) {
                SMS sms = new SMS();
                do {

                    amount1 = c.getDouble(c.getColumnIndex(dbIds.AMOUNT));

                    if (countTwice == 1) {
                        countTwice++;


                        sms.setBank(c.getString(c.getColumnIndex(dbIds.BANK)));
                        sms.setDate(c.getLong(c.getColumnIndex(dbIds.DATE)));
                        sms.setCategory(c.getString(c.getColumnIndex(dbIds.CATEGORY)));
                        sms.setComment(c.getString(c.getColumnIndex(dbIds.COMMENT)));
                    } else {

                        if (amount1 != 0 && amount2 != 0) {
                            count++;
                            sms.setAmount(amount1 - amount2);
                            if (sms.getDate()==null){
                                int ccc = count;
                            }
                            sms.setBank(c.getString(c.getColumnIndex(dbIds.BANK)));
                            sms.setDate(c.getLong(c.getColumnIndex(dbIds.DATE)));
                            sms.setCategory(c.getString(c.getColumnIndex(dbIds.CATEGORY)));
                            sms.setComment(c.getString(c.getColumnIndex(dbIds.COMMENT)));

                            insertBudget(sms, 2); //insert to the second db table differences



                        }
                    }
                    amount2 = c.getDouble(c.getColumnIndex(dbIds.AMOUNT));
                    date.setTimeInMillis(sms.getDate());
                    date.get(Calendar.DAY_OF_MONTH);


                } while (c.moveToNext());
            }
            c.close();

        }

    }

    public int getBudgetCount()
    {
        SQLiteDatabase db = getWritableDatabase();
        Cursor data =  db.rawQuery("SELECT Count(*) FROM " + dbIds.TABLE_1, null);

        int numItems = 0;

        if(data.getCount() == 1)
        {
            data.moveToFirst();
            numItems = data.getInt(0);
        }

        data.close();


        return numItems;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 2:

                db.execSQL(
                        "create table " + dbIds.TABLE_1 + "(" +
                                dbIds._ID + " INTEGER primary key autoincrement, " +
                                dbIds.BANK + " text, " + dbIds.DATE + " REAL, " + dbIds.DATE_SQL + " NUMERIC, " + dbIds.AMOUNT + " REAL, " +
                                dbIds.CATEGORY + " text, " + dbIds.COMMENT + " text)");

        }



    }


}
