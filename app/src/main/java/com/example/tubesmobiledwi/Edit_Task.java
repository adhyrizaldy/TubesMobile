package com.example.tubesmobiledwi;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Edit_Task extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    DatePickerDialog dpd;
    int startYear = 0, startMonth = 0, startDay = 0;
    TextView titlepage, addtitle, adddesc, adddate;
    EditText titledoes;
    EditText descdoes;
    EditText datedoes;
    String dateFinal;

    Button btnDelete, btnEditTask;
    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__task);

        titlepage = findViewById(R.id.titlepage);

        addtitle = findViewById(R.id.addtitle);
        adddesc = findViewById(R.id.adddesc);
        adddate = findViewById(R.id.adddate);

        titledoes = findViewById(R.id.titledoes);
        descdoes = findViewById(R.id.descdoes);
        datedoes = findViewById(R.id.datedoes);

        btnEditTask = findViewById(R.id.btnEditTask);
        btnDelete = findViewById(R.id.btnDelete);
        dateFinal = todayDateString();
        Date your_date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(your_date);
        startYear = cal.get(Calendar.YEAR);
        startMonth = cal.get(Calendar.MONTH);
        startDay = cal.get(Calendar.DAY_OF_MONTH);

        titledoes.setText(getIntent().getStringExtra("titledoes"));
        descdoes.setText(getIntent().getStringExtra("descdoes"));
        datedoes.setText(getIntent().getStringExtra("datedoes"));

        final String keykeydoes = getIntent().getStringExtra("keydoes");

        reference = FirebaseDatabase.getInstance().getReference().child("DoesApp").
                child("Does" + keykeydoes);

        btnEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int errorStep = 0;
                if (titledoes.length() < 1) {
                    errorStep++;
                    titledoes.setError("Harus diisi");
                }

                if (datedoes.length() < 4) {
                    errorStep++;
                    datedoes.setError("harus diisi");
                }
                if (errorStep == 0) {
                    // edit data to database

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dateFinal = datedoes.getText().toString();

                            dataSnapshot.getRef().child("titledoes").setValue(titledoes.getText().toString());
                            dataSnapshot.getRef().child("descdoes").setValue(descdoes.getText().toString());
                            dataSnapshot.getRef().child("datedoes").setValue(dateFinal);
                            dataSnapshot.getRef().child("keydoes").setValue(keykeydoes);
                            Toast.makeText(getApplicationContext(),"Berhasil Di Ubah",Toast.LENGTH_SHORT).show();
                            Intent a = new Intent(Edit_Task.this, MainActivity.class);
                            startActivity(a);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Berhasil Dihapus",Toast.LENGTH_SHORT).show();
                            Intent a = new Intent(Edit_Task.this,MainActivity.class);
                            startActivity(a);
                        }else{
                            Toast.makeText(getApplicationContext(),"gagal",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
        dpd = DatePickerDialog.newInstance(Edit_Task.this, startYear, startMonth, startDay);
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
        EditText task_date = findViewById(R.id.datedoes);
        task_date.setText(date);
    }

}

