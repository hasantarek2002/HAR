package com.example.har;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener, TextToSpeech.OnInitListener {

    private static final int N_SAMPLES = 50;
    private static final int N_TIME_STEPS = 2;
    private static final int N_FEATURES = 31;
    private static final int N_CLASSES = 5;
    private static List<Float> x;
    private static List<Float> y;
    private static List<Float> z;
    private static List< List<Float> > feautureData;

    private TextView sittingsTextView;
    private TextView standingTextView;
    private TextView walkingTextView;
    private TextView walkingDownstairsTextView;
    private TextView walkingUpstairsTextView;
    private TextToSpeech textToSpeech;

    private float[] results;
    private String[] labels = {"SITTING", "STANDING", "WALKING", "WALKING_DOWNSTAIRS", "WALKING_UPSTAIRS"};
    String modelFile = "converted_model.tflite";
    Interpreter tflite;
    FeatureExtractor fe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x = new ArrayList<>();
        y = new ArrayList<>();
        z = new ArrayList<>();

        fe = new FeatureExtractor();
        feautureData = new ArrayList< List<Float> >();

        sittingsTextView = (TextView) findViewById(R.id.sitting_prob);
        standingTextView = (TextView) findViewById(R.id.standing_prob);
        walkingTextView = (TextView) findViewById(R.id.walking_prob);
        walkingDownstairsTextView = (TextView) findViewById(R.id.walking_downstairs_prob);
        walkingUpstairsTextView = (TextView) findViewById(R.id.walking_upstairs_prob);

        try {
            tflite = new Interpreter(loadModelFile(this, modelFile));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

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
        }, 4000, 5000);
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
    public void onSensorChanged(SensorEvent sensorEvent) {
        activityPrediction();
        x.add(sensorEvent.values[0]);
        y.add(sensorEvent.values[1]);
        z.add(sensorEvent.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    // 'SITTING', 'STANDING', 'WALKING', 'WALKING_DOWNSTAIRS', 'WALKING_UPSTAIRS'
    // shape will be X = [None, N_TIME_STEPS, N_FEATURES] = [1, 10, 31]
    // shape of y = [None, N_CLASSES] = [1,5]
    // Android studio 3.4.1

    private void activityPrediction() {
        if (x.size() == N_SAMPLES && y.size() == N_SAMPLES && z.size() == N_SAMPLES) {
            //float[][][] inp=new float[1][N_TIME_STEPS][N_FEATURES];
            //float[][] out=new float[1][N_CLASSES];
            //float [] features_data = new float[N_FEATURES];

            // here call the FeatureExtractor Class
            // which will return the extracted features on features_data array
            //float magnitude[] = fe.calculateMagnitudeArray(x, y, z);

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

            if (feautureData.size() != N_TIME_STEPS){
                Log.d("one window", "Size "+Integer.toString(oneWindowFeatureData.size()) + oneWindowFeatureData.toString() );
                feautureData.add(oneWindowFeatureData);
                Log.d("one window for feature", "Size "+Integer.toString(feautureData.size())+" ,one window for feature Size "+ feautureData.toString() );
//                oneWindowFeatureData.clear();
            }

            if (feautureData.size() == N_TIME_STEPS){
                float[][][] inp=new float[1][N_TIME_STEPS][N_FEATURES];
                float[][] out=new float[1][N_CLASSES];

                for(int i=0; i<N_TIME_STEPS; i++){
                    for (int j=0; j< N_FEATURES; j++){
                        //Log.d("feature", "size "+Integer.toString(feautureData.size()) + feautureData.toString() );
                        inp[0][i][j]=feautureData.get(i).get(j);
                    }
                    //inp[0][i][0]=x.get(i);
                }

                for (int i=0; i< feautureData.size(); i++){
                    feautureData.get(i).clear();
                }
                feautureData.clear();

                tflite.run(inp,out);

                for (int i=0; i<N_CLASSES; i++){
                    results[i] = out[0][i];
                }

                sittingsTextView.setText(Float.toString(round(out[0][0], 2)));
                standingTextView.setText(Float.toString(round(out[0][1], 2)));
                walkingTextView.setText(Float.toString(round(out[0][2], 2)));
                walkingDownstairsTextView.setText(Float.toString(round(out[0][3], 2)));
                walkingUpstairsTextView.setText(Float.toString(round(out[0][4], 2)));

                Log.d("Result sitting ",Float.toString(round(out[0][0], 2)) );
                Log.d("Result standing ",Float.toString(round(out[0][1], 2)) );
                Log.d("Result walking ",Float.toString(round(out[0][2], 2)) );
                Log.d("Result walk_downstair ",Float.toString(round(out[0][3], 2)) );
                Log.d("Result walk_upstairs ",Float.toString(round(out[0][4], 2)) );
            }
            x.clear();
            y.clear();
            z.clear();
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

    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
