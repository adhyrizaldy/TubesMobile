package com.example.tubesmobiledwi;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import java.text.ParseException;
import java.util.Date ;

import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Locale;
import java.util.Random;

public class NewTaskAct extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    DatePickerDialog dpd;
    int startYear = 0, startMonth = 0, startDay = 0;
    TextView titlepage, addtitle, adddesc, adddate;
    EditText titledoes;
    EditText descdoes;
    EditText datedoes;
    String dateFinal;
    final Calendar myCalendar = Calendar. getInstance () ;
    Button btnSaveTask, btnCancel;
    DatabaseReference reference;
    Integer doesNum = new Random().nextInt();
    String keydoes = Integer.toString(doesNum);
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        titlepage = findViewById(R.id.titlepage);

        addtitle = findViewById(R.id.addtitle);
        adddesc = findViewById(R.id.adddesc);
        adddate = findViewById(R.id.adddate);

        titledoes = findViewById(R.id.titledoes);
        descdoes = findViewById(R.id.descdoes);
        datedoes = findViewById(R.id.datedoes);

        btnSaveTask = findViewById(R.id.btnSaveTask);
        btnCancel = findViewById(R.id.btnCancel);
        dateFinal = todayDateString();
        Date your_date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(your_date);
        startYear = cal.get(Calendar.YEAR);
        startMonth = cal.get(Calendar.MONTH);
        startDay = cal.get(Calendar.DAY_OF_MONTH);

        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int errorStep = 0;
                if (titledoes.length() < 1) {
                    errorStep++;
                    titledoes.setError("Provide a task name.");
                }

                if (datedoes.length() < 4) {
                    errorStep++;
                    datedoes.setError("Provide a specific date");
                }
                if (errorStep == 0) {

                    reference = FirebaseDatabase.getInstance().getReference().child("DoesApp").
                            child("Does" + doesNum);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dateFinal = datedoes.getText().toString();

                            dataSnapshot.getRef().child("titledoes").setValue(titledoes.getText().toString());
                            dataSnapshot.getRef().child("descdoes").setValue(descdoes.getText().toString());
                            dataSnapshot.getRef().child("datedoes").setValue(dateFinal);
                            dataSnapshot.getRef().child("keydoes").setValue(keydoes);
                            Toast.makeText(getApplicationContext(),"Berhasil Di tambah",Toast.LENGTH_SHORT).show();
                            Calendar today = Calendar.getInstance();



                            scheduleNotification(getNotification(titledoes.getText().toString()), 30000);
                            Intent a = new Intent(NewTaskAct.this, MainActivity.class);
                            startActivity(a);


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });


    }



    public String todayDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd/MM/yyyy", Locale.getDefault());

        return dateFormat.toString();

    }
    @SuppressLint("ResourceAsColor")
    public void showStartDatePicker(View view) {
        dpd = DatePickerDialog.newInstance(NewTaskAct.this, startYear, startMonth, startDay);
        dpd.setOnDateSetListener(this);
        dpd.setAccentColor(Color.parseColor("#F63EA5"));
        dpd.setOkColor(Color.parseColor("#F63EA5"));
        dpd.setCancelColor(Color.parseColor("#F63EA5"));
        dpd.show(getFragmentManager(), "startDatepickerdialog");
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        startYear = year;
        startMonth = monthOfYear;
        startDay = dayOfMonth;
        int monthAddOne = startMonth + 1;
        String date = (startDay < 10 ? "0" + startDay : "" + startDay) + "/" +
                (monthAddOne < 10 ? "0" + monthAddOne : "" + monthAddOne) + "/" +
                startYear;
        EditText task_date = (EditText) findViewById(R.id.datedoes);
        task_date.setText(date);

    }

    public void batal(View view) {
        Intent a = new Intent(NewTaskAct.this,MainActivity.class);
        startActivity(a);
    }
    private void scheduleNotification(Notification notification, int delay) {

        Intent notificationIntent = new Intent(this, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content) {
//        Notification.Builder builder = new Notification.Builder(this);
//        builder.setContentTitle("Dwi Todo App Notification");
//        builder.setContentText(content);
//        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
//        return builder.build();

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String NOTIFICATION_CHANNEL_ID = "tp_01";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications",importance);

                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker("TpMobile")
                    .setContentTitle(content)
                    .setContentText("Data Berhasil Ditambahkan")
                    .setContentInfo("Information");
            notificationManager.notify(1, notificationBuilder.build());
        return notificationBuilder.build();
    }


}

