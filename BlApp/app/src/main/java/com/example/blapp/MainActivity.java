package com.example.blapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.Button;

import android.app.TimePickerDialog;
import android.widget.TimePicker;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    static final UUID muuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public OutputStream outputStream;
    public BluetoothSocket btSocket;

    private TextView statustext;
    private TextView lastoptext;
    public TextView schedule;
    public TextView montofr;
    public TextView weekend;
    public TextView atmtf;
    public TextView atw;

    public TextView dimmertext;
    public TextView colortext;
    public TextView poweredby;

    public SeekBar dimmerbar;
    public int dimmervalue = 0;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public Switch onoff;

    private Button automode;
    private Button manualymode;
    public Button bluelight;
    public Button redlight;
    public Button greenlight;
    public Button orangelight;
    public Button skylight;
    public Button dimmerbutton;
    public Button schedulebutton;
    public Button infobutton;
    public Button colorbutton;

    private ImageView imageprofile;

    public EditText alarmmtf;
    public EditText alarmw;

    public boolean prevdim = false;
    public boolean prevsch = false;
    public boolean prevcol = false;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        statustext = findViewById(R.id.statustext);
        lastoptext = findViewById(R.id.lastoptext);
        montofr = findViewById(R.id.montofr);
        weekend = findViewById(R.id.weekend);
        schedule = findViewById(R.id.schedule);
        atmtf = findViewById(R.id.atmtf);
        atw = findViewById(R.id.atw);
        dimmertext = findViewById(R.id.dimmertext);
        colortext = findViewById(R.id.colortext);
        poweredby = findViewById(R.id.poweredby);

        dimmerbar = findViewById(R.id.dimmerbar);

        onoff = findViewById(R.id.onoff);

        imageprofile = findViewById(R.id.imageprofile);

        automode = findViewById(R.id.automode);
        manualymode = findViewById(R.id.manualymode);
        bluelight = findViewById(R.id.blueled);
        redlight = findViewById(R.id.redled);
        greenlight = findViewById(R.id.greenled);
        orangelight = findViewById(R.id.orangeled);
        skylight = findViewById(R.id.skyled);
        dimmerbutton = findViewById(R.id.dimmerbutton);
        schedulebutton = findViewById(R.id.schedulebutton);
        infobutton = findViewById(R.id.infobutton);
        colorbutton = findViewById(R.id.colorbutton);

        alarmw = findViewById(R.id.alarmw);
        alarmmtf = findViewById(R.id.alarmmtf);

        final int[] autonow = {1};
        final int[] mannow = {1};
        final int[] info = {1};

        onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // 1. find devices
                    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                    System.out.println(btAdapter.getBondedDevices());

                    // 2. create the object
                    BluetoothDevice hc05 = btAdapter.getRemoteDevice("98:D3:31:F5:9D:CF");
                    System.out.println(hc05.getName());

                    // 3. create the socket
                    btSocket = null;
                    int counter = 0;
                    do {
                        try {
                            btSocket = hc05.createRfcommSocketToServiceRecord(muuid);
                            System.out.println(btSocket);
                            btSocket.connect();
                            System.out.println(btSocket.isConnected());
                        } catch (IOException e) {
                            System.out.println("problem with (opening) socket");
                        }
                        counter ++;
                    }
                    while(!btSocket.isConnected() && counter < 3);

                    // 4. open the connection, the outputStream
                    try {
                        outputStream = btSocket.getOutputStream();
                        statustext.setText("status: ok");
                        lastoptext.setText("last operation: apertura dell'outputStream");
                    } catch (IOException e) {
                        System.out.println("problem with outputStream");
                        statustext.setText("status: problem with (opening) outputStream");
                    }

                    // try the connection
                    try {
                        String data = "B";
                        outputStream.write(data.getBytes());
                        System.out.println("stringa mandata");
                        lastoptext.setText("last operation: outputStream funzionante");
                        statustext.setText("status: ok");
                    } catch (IOException e) {
                        System.out.println("problem with outputstrem");
                        statustext.setText("status: problem on sending");
                    }

                    automode.setEnabled(true);
                    manualymode.setEnabled(true);

                    automode.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            if(autonow[0] == 1){
                                manualymode.setEnabled(false);
                                try {
                                    char data = '(';
                                    outputStream.write(data);
                                    lastoptext.setText("last operation: open auto mode ");
                                    statustext.setText("status: ok");
                                }
                                catch (IOException e) {
                                    statustext.setText("status: problem on mode");
                                }

                                part(true);
                                if(statustext.getVisibility() == View.VISIBLE){
                                    statustext.setVisibility(View.INVISIBLE);
                                    lastoptext.setVisibility(View.INVISIBLE);
                                    poweredby.setVisibility(View.INVISIBLE);
                                    imageprofile.setVisibility(View.INVISIBLE);
                                }
                                colorbutton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        colorbutton.setEnabled(false);
                                        dimmerbutton.setEnabled(false);
                                        schedulebutton.setEnabled(true);

                                        colorbutton.setVisibility(View.INVISIBLE);
                                        colortext.setVisibility(View.VISIBLE);
                                        redlight.setVisibility(View.VISIBLE);
                                        bluelight.setVisibility(View.VISIBLE);
                                        greenlight.setVisibility(View.VISIBLE);
                                        orangelight.setVisibility(View.VISIBLE);
                                        skylight.setVisibility(View.VISIBLE);

                                        //dimmerbutton.setVisibility(View.INVISIBLE);
                                        schedulebutton.setVisibility(View.VISIBLE);
                                        schedule.setVisibility(View.INVISIBLE);
                                        montofr.setVisibility(View.INVISIBLE);
                                        atmtf.setVisibility(View.INVISIBLE);
                                        alarmmtf.setVisibility(View.INVISIBLE);
                                        weekend.setVisibility(View.INVISIBLE);
                                        atw.setVisibility(View.INVISIBLE);
                                        alarmw.setVisibility(View.INVISIBLE);
                                    }
                                });
                                schedulebutton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        schedulebutton.setEnabled(false);
                                        dimmerbutton.setEnabled(false);
                                        colorbutton.setEnabled(true);

                                        colorbutton.setVisibility(View.VISIBLE);
                                        colortext.setVisibility(View.INVISIBLE);
                                        redlight.setVisibility(View.INVISIBLE);
                                        bluelight.setVisibility(View.INVISIBLE);
                                        greenlight.setVisibility(View.INVISIBLE);
                                        orangelight.setVisibility(View.INVISIBLE);
                                        skylight.setVisibility(View.INVISIBLE);

                                        schedulebutton.setVisibility(View.INVISIBLE);
                                        schedule.setVisibility(View.VISIBLE);
                                        montofr.setVisibility(View.VISIBLE);
                                        atmtf.setVisibility(View.VISIBLE);
                                        alarmmtf.setVisibility(View.VISIBLE);
                                        weekend.setVisibility(View.VISIBLE);
                                        atw.setVisibility(View.VISIBLE);
                                        alarmw.setVisibility(View.VISIBLE);
                                    }
                                });
                                autonow[0] = 0;
                            }
                            else{
                                manualymode.setEnabled(true);
                                try {
                                    char data = '(';
                                    outputStream.write(data);
                                    lastoptext.setText("last operation: close auto mode ");
                                    statustext.setText("status: ok");
                                }
                                catch (IOException e) {
                                    statustext.setText("status: problem on mode");
                                }
                                stato_sistema(true);
                                autonow[0] = 1;
                            }
                        }
                    });
                    manualymode.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            if(mannow[0] == 1){
                                automode.setEnabled(false);
                                try {
                                    char data = ')';
                                    outputStream.write(data);
                                    lastoptext.setText("last operation: open manualy mode ");
                                    statustext.setText("status: ok");
                                }
                                catch (IOException e) {
                                    statustext.setText("status: problem on mode");
                                }

                                part(false);
                                if(statustext.getVisibility() == View.VISIBLE){
                                    statustext.setVisibility(View.INVISIBLE);
                                    lastoptext.setVisibility(View.INVISIBLE);
                                    poweredby.setVisibility(View.INVISIBLE);
                                    imageprofile.setVisibility(View.INVISIBLE);
                                }

                                mannow[0] = 0;
                                colorbutton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        colorbutton.setEnabled(false);
                                        schedulebutton.setEnabled(false);
                                        dimmerbutton.setEnabled(true);

                                        colorbutton.setVisibility(View.INVISIBLE);
                                        colortext.setVisibility(View.VISIBLE);
                                        redlight.setVisibility(View.VISIBLE);
                                        bluelight.setVisibility(View.VISIBLE);
                                        greenlight.setVisibility(View.VISIBLE);
                                        orangelight.setVisibility(View.VISIBLE);
                                        skylight.setVisibility(View.VISIBLE);


                                        dimmerbutton.setVisibility(View.VISIBLE);
                                        dimmertext.setVisibility(View.INVISIBLE);
                                        dimmerbar.setVisibility(View.INVISIBLE);
                                    }
                                });
                                dimmerbutton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        schedulebutton.setEnabled(false);
                                        dimmerbutton.setEnabled(false);
                                        colorbutton.setEnabled(true);

                                        colorbutton.setVisibility(View.VISIBLE);
                                        colortext.setVisibility(View.INVISIBLE);
                                        redlight.setVisibility(View.INVISIBLE);
                                        bluelight.setVisibility(View.INVISIBLE);
                                        greenlight.setVisibility(View.INVISIBLE);
                                        orangelight.setVisibility(View.INVISIBLE);
                                        skylight.setVisibility(View.INVISIBLE);


                                        dimmerbutton.setVisibility(View.INVISIBLE);
                                        dimmerbar.setVisibility(View.VISIBLE);
                                        dimmertext.setVisibility(View.VISIBLE);
                                    }
                                });

                                if(dimmertext.getVisibility() == View.VISIBLE){
                                    dimmerbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                        @Override
                                        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                                            dimmervalue = progress;
                                        }
                                        @Override
                                        public void onStartTrackingTouch(SeekBar seekBar) {

                                        }
                                        @Override
                                        public void onStopTrackingTouch(SeekBar seekBar) {
                                            try {
                                                String data= "D";
                                                outputStream.write(data.getBytes());
                                                lastoptext.setText("last operation: send string "+data);
                                                System.out.println("status: ok");
                                            } catch (IOException e) {
                                                System.out.println("status: problem on sending");
                                            }
                                            try {
                                                String data;
                                                if(dimmervalue < 10){data = String.valueOf(dimmervalue);}
                                                else{data = ":";}
                                                outputStream.write(data.getBytes());
                                                lastoptext.setText("last operation: send string "+data);
                                                System.out.println("status: ok");
                                            } catch (IOException e) {
                                                System.out.println("status: problem on sending");
                                            }
                                        }
                                    });
                                }
                            }
                            else{
                                automode.setEnabled(true);
                                lastoptext.setText("last operation: close manualy mode ");
                                statustext.setText("status: ok");
                                stato_sistema(true);
                                mannow[0] = 1;
                            }
                        }
                    });

                    bluelight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                String data = "b";
                                outputStream.write(data.getBytes());
                                lastoptext.setText("last operation: set led blue");
                                statustext.setText("status: ok");
                            } catch (IOException e) {
                                statustext.setText("status: problem on intent");
                            }
                        }
                    });
                    redlight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                String data = "r";
                                outputStream.write(data.getBytes());
                                lastoptext.setText("last operation: set led red");
                                statustext.setText("status: ok");
                            } catch (IOException e) {
                                statustext.setText("status: problem on intent");
                            }
                        }
                    });
                    greenlight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                String data = "g";
                                outputStream.write(data.getBytes());
                                lastoptext.setText("last operation: set led green");
                                statustext.setText("status: ok");
                            } catch (IOException e) {
                                statustext.setText("status: problem on intent");
                            }
                        }
                    });
                    orangelight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                String data = "o";
                                outputStream.write(data.getBytes());
                                lastoptext.setText("last operation: set led orange");
                                statustext.setText("status: ok");
                            } catch (IOException e) {
                                statustext.setText("status: problem on intent");
                            }
                        }
                    });
                    skylight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                String data = "s";
                                outputStream.write(data.getBytes());
                                lastoptext.setText("last operation: set led sky");
                                statustext.setText("status: ok");
                            } catch (IOException e) {
                                statustext.setText("status: problem on intent");
                            }
                        }
                    });
                }
                else {
                    statustext.setText("status: none");
                    lastoptext.setText("last operation: none");

                    // close socket
                    try{
                        String data = "F";
                        outputStream.write(data.getBytes());
                        outputStream.close();
                        btSocket.close();
                        System.out.println(btSocket.isConnected());
                    } catch (IOException e) {
                        System.out.println("problem with (closure) socket");
                    }


                    automode.setEnabled(false);
                    manualymode.setEnabled(false);
                }
                stato_sistema(isChecked);
            }
        });
        if(!onoff.isChecked()){
            stato_sistema(false);
            automode.setEnabled(false);
            manualymode.setEnabled(false);
        }

        imageprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, (Uri.parse("https://github.com/gitandrehub")));
                startActivity(intent);
            }
        });
        infobutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(info[0] == 1){
                    statustext.setVisibility(View.VISIBLE);
                    lastoptext.setVisibility(View.VISIBLE);
                    poweredby.setVisibility(View.VISIBLE);
                    imageprofile.setVisibility(View.VISIBLE);

                    System.out.println("info mode");

                    if(dimmerbutton.getVisibility() == View.VISIBLE){
                        dimmerbutton.setVisibility(View.INVISIBLE);
                        prevdim = true;
                    } else if(schedulebutton.getVisibility() == View.VISIBLE){
                        schedulebutton.setVisibility(View.INVISIBLE);
                        prevsch = true;
                    } else if(colorbutton.getVisibility() == View.VISIBLE){
                        colorbutton.setVisibility(View.INVISIBLE);
                        prevcol = true;
                    }
                    info[0] = 0;
                }
                else{
                    statustext.setVisibility(View.INVISIBLE);
                    lastoptext.setVisibility(View.INVISIBLE);
                    poweredby.setVisibility(View.INVISIBLE);
                    imageprofile.setVisibility(View.INVISIBLE);

                    if(prevdim){
                        dimmerbutton.setVisibility(View.VISIBLE);
                        prevdim = false;
                    } else if(prevsch){
                        schedulebutton.setVisibility(View.VISIBLE);
                        prevsch = false;
                    } else if(prevcol){
                        colorbutton.setVisibility(View.VISIBLE);
                        prevcol = false;
                    }
                    info[0] = 1;
                }
            }
        });
    }

    private void stato_sistema(boolean check){
        if(check){
            bluelight.setVisibility(View.VISIBLE);
            redlight.setVisibility(View.VISIBLE);
            greenlight.setVisibility(View.VISIBLE);
            orangelight.setVisibility(View.VISIBLE);
            skylight.setVisibility(View.VISIBLE);
            colortext.setVisibility(View.VISIBLE);
        }
        else{
            bluelight.setVisibility(View.INVISIBLE);
            redlight.setVisibility(View.INVISIBLE);
            greenlight.setVisibility(View.INVISIBLE);
            orangelight.setVisibility(View.INVISIBLE);
            skylight.setVisibility(View.INVISIBLE);
            colortext.setVisibility(View.INVISIBLE);
        }


        schedule.setVisibility(View.INVISIBLE);
        montofr.setVisibility(View.INVISIBLE);
        atmtf.setVisibility(View.INVISIBLE);
        alarmmtf.setVisibility(View.INVISIBLE);
        weekend.setVisibility(View.INVISIBLE);
        atw.setVisibility(View.INVISIBLE);
        alarmw.setVisibility(View.INVISIBLE);
        dimmertext.setVisibility(View.INVISIBLE);
        dimmerbar.setVisibility(View.INVISIBLE);

        schedulebutton.setVisibility(View.INVISIBLE);
        dimmerbutton.setVisibility(View.INVISIBLE);
        colorbutton.setVisibility(View.INVISIBLE);

        prevdim = false;
        prevcol = false;
        prevsch = false;
    }
    private void part(boolean vis){
        try{
            if(vis){
                schedule.setVisibility(View.VISIBLE);
                montofr.setVisibility(View.VISIBLE);
                atmtf.setVisibility(View.VISIBLE);
                alarmmtf.setVisibility(View.VISIBLE);
                weekend.setVisibility(View.VISIBLE);
                atw.setVisibility(View.VISIBLE);
                alarmw.setVisibility(View.VISIBLE);

                dimmertext.setVisibility(View.INVISIBLE);
                dimmerbar.setVisibility(View.INVISIBLE);
                dimmerbar.setEnabled(false);
                dimmerbutton.setEnabled(false);
            }
            else{
                schedule.setVisibility(View.INVISIBLE);
                montofr.setVisibility(View.INVISIBLE);
                atmtf.setVisibility(View.INVISIBLE);
                alarmmtf.setVisibility(View.INVISIBLE);
                weekend.setVisibility(View.INVISIBLE);
                atw.setVisibility(View.INVISIBLE);
                alarmw.setVisibility(View.INVISIBLE);

                dimmertext.setVisibility(View.VISIBLE);
                dimmerbar.setVisibility(View.VISIBLE);
                dimmerbar.setEnabled(true);
                dimmerbutton.setVisibility(View.INVISIBLE);
            }
            colorbutton.setVisibility(View.VISIBLE);
            colorbutton.setEnabled(true);
            colortext.setVisibility(View.INVISIBLE);
            redlight.setVisibility(View.INVISIBLE);
            bluelight.setVisibility(View.INVISIBLE);
            greenlight.setVisibility(View.INVISIBLE);
            orangelight.setVisibility(View.INVISIBLE);
            skylight.setVisibility(View.INVISIBLE);
        }
        catch(Exception e){e.printStackTrace();}
    }
    public void mostraTimePicker(View view){
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int edittext = view.getId();
        System.out.println(edittext);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHourOfDay, int selectedMinute) {
                String selectedTime = selectedHourOfDay + ":" + selectedMinute;

                System.out.println(edittext);
                if(edittext == R.id.alarmmtf){
                    try {
                        String data = "M";
                        outputStream.write(data.getBytes());
                    } catch (IOException e) {
                        System.out.println("problem with set allarm");
                    }
                    alarmmtf.setText(selectedTime);
                }
                else{
                    try{
                        String data = "W";
                        outputStream.write(data.getBytes());
                    } catch (IOException e) {
                        System.out.println("problem with set allarm");
                    }
                    alarmw.setText(selectedTime);
                }
                try{
                    outputStream.write(selectedTime.getBytes());
                    lastoptext.setText("last operation: set allarm");
                } catch (IOException e) {
                    System.out.println("problem with set allarm");
                }
            }
        }, hourOfDay, minute, true);
        timePickerDialog.show();
    }
}