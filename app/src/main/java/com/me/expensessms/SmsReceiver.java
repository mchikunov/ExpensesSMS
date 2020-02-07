package com.me.expensessms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.TextView;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
public String str;
    public TextView text;
    @Override
    public void onReceive(Context context, Intent intent) {
        {
//---получить входящее SMS сообщение---
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            str = "";
            if (bundle != null) {
//---извлечь полученное SMS ---
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                for (int i = 0; i < msgs.length; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    str += "SMS from " + msgs[i].getOriginatingAddress();
                    str += " :";
                    str += msgs[i].getMessageBody().toString();
                    str += "\n";
                }
//---Показать новое SMS сообщение---

                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();


            }
        }
    }
}
