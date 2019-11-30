package com.example.activityrecognition;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import weka.classifiers.trees.RandomForest;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class MainActivity extends AppCompatActivity implements SensorEventListener, TextToSpeech.OnInitListener{

    private static final int N_SAMPLES = 50;
    private static final int N_CLASSES = 3;
    private static List<Float> x;
    private static List<Float> y;
    private static List<Float> z;

    private TableRow sittingTableRow;
    private TableRow standingTableRow;
    private TableRow walkingTableRow;

    private TextView sittingTextView;
    private TextView standingTextView;
    private TextView walkingTextView;

    private TextToSpeech textToSpeech;
    private float[] results;
    private static Instances dataset;
    private String[] labels = {"SITTING", "STANDING", "WALKING"};
    FeatureExtractor fe;
    DataIntanceCreation dataIntanceCreation;
    RandomForest rf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        x = new ArrayList<>();
        y = new ArrayList<>();
        z = new ArrayList<>();

        fe = new FeatureExtractor();
        dataIntanceCreation = new DataIntanceCreation();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        Log.d("Android Version", " : "+currentapiVersion);

        PackageManager PM= this.getPackageManager();
        boolean accelerometer = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);

        if (currentapiVersion < 21){
            showExitDialogue("Your phone's API level should be greater than 20 to use this App");
        }

        if(! accelerometer){
            showExitDialogue("Acceleromter sensor is a must to use this application.");
        }

        sittingTextView = (TextView) findViewById(R.id.sitting_prob);
        standingTextView = (TextView) findViewById(R.id.standing_prob);
        walkingTextView = (TextView) findViewById(R.id.walking_prob);

        sittingTableRow = (TableRow) findViewById(R.id.sitting_row);
        standingTableRow = (TableRow) findViewById(R.id.standing_row);
        walkingTableRow = (TableRow) findViewById(R.id.walking_row);

        try {
            rf = (RandomForest) weka.core.SerializationHelper.read(getAssets().open("rf.model"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        dataset = dataIntanceCreation.getDataInstance();
        results = new float[N_CLASSES];
        textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setLanguage(Locale.US);
    }

    @Override
    public void onInit(int i) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (results == null || results.length == 0) {
                    return;
                }
                float max = -1;
                int idx = -1;
                for (int i = 0; i < results.length; i++) {
                    if (results[i] > max) {
                        idx = i;
                        max = results[i];
                    }
                }

                textToSpeech.speak(labels[idx], TextToSpeech.QUEUE_ADD, null, Integer.toString(new Random().nextInt()));
            }
        }, 3000, 3000);
    }

    private void showExitDialogue(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.activity_icon);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    protected void onPause() {
        getSensorManager().unregisterListener(this);
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "ooh crap!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        activityPrediction();
        x.add(sensorEvent.values[0]);
        y.add(sensorEvent.values[1]);
        z.add(sensorEvent.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void activityPrediction() {
        if (x.size() == N_SAMPLES && y.size() == N_SAMPLES && z.size() == N_SAMPLES) {

            List<Float> magnitude = fe.calculateMagnitudeArray(x, y, z);
            List<Float> means = fe.mean(x, y, z, magnitude);
            List<Float> stds = fe.calculateSTD(x, y, z, magnitude);
            List<Float> rms = fe.calculateRmsValue(x, y, z, magnitude);
            List<Float> mins = fe.calculateMinValue(x, y, z, magnitude);
            List<Float> maxs = fe.calculateMaxValue(x, y, z, magnitude);
            List<Float> medians = fe.median(x, y, z, magnitude);
            List<Float> mads = fe.mad(x, y, z, magnitude);
            float corrXY = fe.Correlation(x, y);
            float corrYZ = fe.Correlation(y, z);
            float corrXZ = fe.Correlation(x, z);
            List<Float> corr =  new ArrayList<>(Arrays.asList( corrXY,corrYZ,corrXZ));

            List<Float> oneWindowFeatureData = new ArrayList<>();
            oneWindowFeatureData.addAll(means);
            oneWindowFeatureData.addAll(stds);
            oneWindowFeatureData.addAll(rms);
            oneWindowFeatureData.addAll(mins);
            oneWindowFeatureData.addAll(maxs);
            oneWindowFeatureData.addAll(medians);
            oneWindowFeatureData.addAll(mads);
            oneWindowFeatureData.addAll(corr);

            double[] values = new double[dataset.numAttributes()];
            for (int i=0; i<dataset.numAttributes()-1; i++) {
                values[i] = oneWindowFeatureData.get(i);
            }
            Instance inst = new DenseInstance(1.0, values);
            dataset.add(inst);
            dataset.setClassIndex(dataset.numAttributes()-1);
            int classIndex = -1;
            try {
                classIndex = (int)rf.classifyInstance(dataset.instance(0));
                Log.d("class : ",Integer.toString(classIndex));
                dataset.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }

            int idx = -1;
            for (int i=0; i<N_CLASSES; i++){
                if(classIndex == i ) {
                    idx = i;
                    results[i] = (float) 1.0;
                    Log.d("LOOP IF: ","class index "+ Integer.toString(i)+" res "+Float.toString(results[i]));
                }else{
                    results[i] = (float) 0.0;
                    Log.d("LOOP ELSE: ","class index "+ Integer.toString(i)+" res "+Float.toString(results[i]));
                }

            }

            sittingTextView.setText(Float.toString(round(results[0], 2)));
            standingTextView.setText(Float.toString(round(results[1], 2)));
            walkingTextView.setText(Float.toString(round(results[2], 2)));


            Log.d("Result sitting ",Float.toString(round(results[0], 2)) );
            Log.d("Result standing ",Float.toString(round(results[1], 2)) );
            Log.d("Result walking ",Float.toString(round(results[2], 2)) );

            setRowsColor(idx);

            x.clear();
            y.clear();
            z.clear();
        }
    }

    private void setRowsColor(int idx) {
        sittingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        standingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        walkingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
        if(idx == 0) {
            sittingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorBlue, null));
        }
        else if (idx == 1) {
            standingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorBlue, null));
        }
        else if (idx == 2){
            walkingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorBlue, null));
        }
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    private SensorManager getSensorManager() {
        return (SensorManager) getSystemService(SENSOR_SERVICE);
    }
}
