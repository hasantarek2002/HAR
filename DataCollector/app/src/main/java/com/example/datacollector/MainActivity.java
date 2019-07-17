package com.example.datacollector;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    private static RadioGroup radio_g;
    private RadioButton radio_b;
    private static Button btn_start,btn_stop;
    private static TextView txt_status_message;
    private static EditText subject_id;
    public String activity_label= "";
    public String subject = "";
    public String PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public long MIN_TIME = 1000;

    private static final int N_SAMPLES = 50;
    private static List<Float> x;
    private static List<Float> y;
    private static List<Float> z, x_g, y_g, z_g;
    private static List<Long> timstamps;

    private Sensor accelerometer,gyroscope;
    public SensorManager sensorManager = null;
    public SensorEventListener sensorListener = null;
    public SensorEventListener sensorListener_gyro = null;

    public long prevLocTime = -1;
    public long prevTime = 0;
    public long curTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        x = new ArrayList<>();
        y = new ArrayList<>();
        z = new ArrayList<>();
        x_g = new ArrayList<>();
        y_g = new ArrayList<>();
        z_g = new ArrayList<>();
        timstamps = new ArrayList<>();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        Log.d("Android Version", " : "+currentapiVersion);

        if (currentapiVersion > 22){
            // Do something for versions above lollipop
            getPermissions();
        }

        radio_g = findViewById(R.id.rg_activity);
        btn_start= findViewById(R.id.start_button);
        btn_stop = findViewById(R.id.stop_button);
        txt_status_message = findViewById(R.id.txt_status);
        subject_id = findViewById(R.id.subject_id);

        btn_stop.setEnabled(false);
        btn_stop.setText("Disable");

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("On Start()", "Service Started....");
                //Toast.makeText(MainActivity.this, "Service Started...", Toast.LENGTH_SHORT).show();

                if (radio_g.getCheckedRadioButtonId() == -1)
                {
                    Log.d("Radio Button","No Checked Radio Button");
                    Toast.makeText(MainActivity.this,"First select the ACTIVITY LABEL",Toast.LENGTH_LONG).show();
                }else {
                    Log.d("Radio Button","Radio Button Checked ");

                    int select_id = radio_g.getCheckedRadioButtonId();
                    radio_b= (RadioButton) findViewById(select_id);
                    activity_label= radio_b.getText().toString();
                    subject = subject_id.getText().toString();
                    Log.d("ID",Integer.toString(select_id)+" :"+activity_label);

                    //Toast.makeText(MainActivity.this,"Id "+Integer.toString(select_id)+" : "+radio_b.getText().toString()+" Label : "+activity_label,Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this,"Service Started\nActivity Label : "+activity_label+ "subject Id: "+subject,Toast.LENGTH_SHORT).show();
                    btn_start.setEnabled(false);
                    radio_g.setEnabled(false);
                    subject_id.setEnabled(false);

                    btn_stop.setEnabled(true);
                    btn_stop.setText("STOP");

                    btn_start.setText("Disabled");
                    txt_status_message.setText("Data collecting...");

                    init();
                }
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //destroy();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.app_name);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setMessage("Do you want to Stop Data Collection ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //finish();
                                destroy();
                                btn_start.setEnabled(true);
                                radio_g.setEnabled(true);
                                subject_id.setEnabled(true);

                                radio_g.clearCheck();

                                btn_stop.setEnabled(false);
                                btn_stop.setText("Disabled");

                                btn_start.setText("START");
                                txt_status_message.setText("Ready for Data Collection");
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                /*
                btn_start.setEnabled(true);
                radio_g.setEnabled(true);

                radio_g.clearCheck();

                btn_stop.setEnabled(false);
                btn_stop.setText("Disabled");

                btn_start.setText("START");
                txt_status_message.setText("Ready for Data Collection");
                */

            }
        });

    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Log.d("external found",PATH);
            return true;
        }
        Log.d("external not found",PATH);
        return false;
    }

    public void writeToFile(List<Float> x, List<Float> y, List<Float> z, List <Long> timestamps) {
        File file = new File(PATH + "/acc_data.txt");
        FileOutputStream fOut = null;
        try {
            fOut  = new FileOutputStream(file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for(int i=0; i<x.size(); i++){
            String x_acc= Float.toString(x.get(i));
            String y_acc= Float.toString(y.get(i));
            String z_acc= Float.toString(z.get(i));
            String timestamp = Long.toString(timestamps.get(i));
            try {
                fOut.write((x_acc+",").getBytes());
                fOut.write((y_acc+",").getBytes());
                fOut.write((z_acc+",").getBytes());
                fOut.write((timestamp+",").getBytes());
                fOut.write((subject+",").getBytes());
                fOut.write((activity_label+"\n").getBytes());
                Log.d("Data", "Data Written");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //For storing accelerometer and Gyroscope data
    public void writeToFile(List<Float> x, List<Float> y, List<Float> z, List<Float> x_g, List<Float> y_g, List<Float> z_g, int minVal) {
        File file = new File(PATH + "/data.txt");
        //File acc_file = new File(PATH + "/accData.txt");
        //File gyro_file = new File(PATH + "/gyroData.txt");
        FileOutputStream fOut = null;
        //FileOutputStream accfOut = null;
        //FileOutputStream gyrofOut = null;

        try {
            fOut  = new FileOutputStream(file, true);
            //accfOut  = new FileOutputStream(acc_file, true);
            //gyrofOut  = new FileOutputStream(gyro_file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for(int i=0; i<minVal; i++){
            String x_acc= Float.toString(x.get(i));
            String y_acc= Float.toString(y.get(i));
            String z_acc= Float.toString(z.get(i));

            String x_gyro = Float.toString(x_g.get(i));
            String y_gyro = Float.toString(y_g.get(i));
            String z_gyro = Float.toString(z_g.get(i));
            try {
                fOut.write((x_acc+",").getBytes());
                fOut.write((y_acc+",").getBytes());
                fOut.write((z_acc+",").getBytes());
                fOut.write((x_gyro+",").getBytes());
                fOut.write((y_gyro+",").getBytes());
                fOut.write((z_gyro+",").getBytes());
                fOut.write((subject+",").getBytes());
                fOut.write((activity_label+"\n").getBytes());

                Log.d("Data", "Data Written");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fOut.close();
            //accfOut.close();
            //gyrofOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /*
    accfOut.write((x_acc+",").getBytes());
    accfOut.write((y_acc+",").getBytes());
    accfOut.write((z_acc+",").getBytes());
    accfOut.write((subject+",").getBytes());
    accfOut.write((activity_label+"\n").getBytes());

    gyrofOut.write((x_gyro+",").getBytes());
    gyrofOut.write((y_gyro+",").getBytes());
    gyrofOut.write((z_gyro+",").getBytes());
    gyrofOut.write((subject+",").getBytes());
    gyrofOut.write((activity_label+"\n").getBytes());
    */
    public void init() {
        Log.d("in init()", "Initializing");
        prevTime = System.currentTimeMillis();
        sensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    x.add(sensorEvent.values[0]);
                    y.add(sensorEvent.values[1]);
                    z.add(sensorEvent.values[2]);
                    timstamps.add(System.currentTimeMillis());
                }
                /*
                if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    x_g.add(sensorEvent.values[0]);
                    y_g.add(sensorEvent.values[1]);
                    z_g.add(sensorEvent.values[2]);
                }
                */
                curTime = System.currentTimeMillis();
//              Log.d("Time = ", "" + (curTime - prevTime) + " > " + MIN_TIME);
                if (curTime - prevTime > MIN_TIME) {
                    //int minVal = Math.min(x.size(), x_g.size());
                    if(isExternalStorageWritable()){
                        //writeToFile(x,y,z,x_g,y_g,z_g,minVal);
                        writeToFile(x,y,z,timstamps);
                    }
                    x.clear();
                    y.clear();
                    z.clear();
                    timstamps.clear();
                    //x_g.clear();
                    //y_g.clear();
                    //z_g.clear();
                    prevTime = curTime;
                }

                /*
                if (x.size() != N_SAMPLES && y.size() != N_SAMPLES && z.size() != N_SAMPLES ){
                    x.add(sensorEvent.values[0]);
                    y.add(sensorEvent.values[1]);
                    z.add(sensorEvent.values[2]);
                }else {
                    if(isExternalStorageWritable()){
                        writeToFile(x,y,z);
                    }
                    x.clear();
                    y.clear();
                    z.clear();
                }
                */

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(sensorListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
//        sensorManager.registerListener(sensorListener,
//                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);

        //Checks if the Accelerometer or Gyroscope sensor is available or not
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null ){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setMessage("Accelerometer Sensor is not available, please exit")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    public void destroy() {
        Log.d("in Destroy()", "Stopped....");
        if(sensorListener != null) {
            sensorManager.unregisterListener(sensorListener);
        }
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
    }



    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    @TargetApi(Build.VERSION_CODES.M)
    private void getPermissions() {
        Log.e("GETPERMISSIONS", "INSIDE GETPERMISSIONS");
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("FINE_LOCATION");
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("READ_PHONE_STATE");
        if (!addPermission(permissionsList, Manifest.permission.INTERNET))
            permissionsNeeded.add("INTERNET");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("WRITE_EXTERNAL_STORAGE");


        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);

                Toast.makeText(this, message,  Toast.LENGTH_LONG).show();
                Log.d("Message", message);

                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permissions", "All Permissions Granted");
                    // All Permissions Granted
                }
                else {
                    // Permission Denied
                    Log.d("Permissions", "Some Permission is Denied");
                    for (int i = 0; i < permissions.length; i++){
                        if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                            Log.d("No permssion : ", permissions[i]);
                        }

                    }
                    Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onBackPressed() {
        Toast.makeText(this, "ooh crap!", Toast.LENGTH_SHORT).show();
    }
}
