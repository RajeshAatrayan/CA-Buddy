package com.ibrickedlabs.drops;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ibrickedlabs.drops.data.Exam;
import com.ibrickedlabs.drops.widgets.BucketPickerView;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by RajeshAatrayan on 26-08-2018.
 */

public class DialogAdd extends DialogFragment {
    private ImageButton buttonClose;
    private EditText inputWhat;
    private BucketPickerView inputWhen;
    private Button addButton;
    private TextView notifyTextview;
    private TimePicker timePicker;
    private  final  String LOG_TAG=DialogAdd.class.getSimpleName();

    public DialogAdd() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.DialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonClose = (ImageButton) view.findViewById(R.id.close_Button);
        inputWhat = (EditText) view.findViewById(R.id.et_Drop);
        inputWhen = (BucketPickerView) view.findViewById(R.id.datePicker);
        addButton = (Button) view.findViewById(R.id.addButton);
        timePicker=(TimePicker)view.findViewById(R.id.timepicker) ;
        notifyTextview=(TextView)view.findViewById(R.id.remainderText);

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ral.ttf");
        notifyTextview.setTypeface(typeface);
        inputWhat.setTypeface(typeface);

        //to close the dialogue
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //to save data
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if( saveData()){
                   dismiss();
               }


            }
        });
    }

    //TODO process the date
    private boolean saveData() {
        String what = inputWhat.getText().toString();
        Log.i(LOG_TAG,"enterd text"+what);
        if(TextUtils.isEmpty(what)){
            inputWhat.setError("CA name can't be empty");
            return false;
        }

        long now = System.currentTimeMillis();
        setAlarmNotification(inputWhen.getTime(),what,now);

        /**
         * we need to set the default  configuration for our realm
         */
        //build up the default configuration
        // initialize Realm
        Realm.init(getContext());

        // create your Realm configuration
        RealmConfiguration realmConfiguration = new RealmConfiguration.
                Builder().
                deleteRealmIfMigrationNeeded().
                build();
        //set the default configurtaion
        Realm.setDefaultConfiguration(realmConfiguration);

        /**
         * which will give the default configuration we set above
         * returs realm obj
         */
        Realm realm = Realm.getDefaultInstance();
        Exam drop = new Exam(what, now,inputWhen.getTime(), false);
        realm.beginTransaction();
        realm.copyToRealm(drop);
        realm.commitTransaction();
        realm.close();
        return  true;


    }

    private void setAlarmNotification(long targetMillisec,String what,long prid) {
        int primaryId=(int)prid;
        long currentTimeInMillis = System.currentTimeMillis();
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTimeInMillis(targetMillisec);
        int alarmDate = todayCalendar.get(Calendar.DATE);
        todayCalendar.set(Calendar.DATE, alarmDate - 1);

        if (Build.VERSION.SDK_INT >= 23)
        {
            todayCalendar.set(todayCalendar.get(Calendar.YEAR),
                    todayCalendar.get(Calendar.MONTH),
                    todayCalendar.get(Calendar.DATE),
                    timePicker.getHour(),
                    timePicker.getMinute(),0);
            Log.i(LOG_TAG,"API>23");
            Log.i(LOG_TAG,todayCalendar.get(Calendar.DAY_OF_MONTH)+"");
        }
        else{
            todayCalendar.set(todayCalendar.get(Calendar.YEAR),
                    todayCalendar.get(Calendar.MONTH),
                    todayCalendar.get(Calendar.DAY_OF_MONTH),
                    timePicker.getCurrentHour(),
                    timePicker.getCurrentMinute(),0);
            Log.i(LOG_TAG,"API<23");

        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
Log.i(LOG_TAG+"alarm time-",simpleDateFormat.format(todayCalendar.getTime()));
Log.i(LOG_TAG+"current time",simpleDateFormat.format(currentTimeInMillis));


long targetAlarmTimeInMillisec=todayCalendar.getTimeInMillis();


        AlarmManager alarmManager=(AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(getContext(),AlarmReciever.class);
        intent.putExtra("subject",what);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(getContext(),primaryId,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,targetAlarmTimeInMillisec,AlarmManager.INTERVAL_DAY,pendingIntent);

    }
}
