package github.io.rahmatsyam.androidstepcounter;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Button btnReset;

    TextView tv_steps;

    SensorManager sensorManager;

    Sensor sensor;

    int stepInSensor = 0;
    int stepAtReset;

    boolean running = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_steps = findViewById(R.id.tv_steps);

        btnReset = findViewById(R.id.btn_reset);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepAtReset = stepInSensor;

                SharedPreferences.Editor editor = getSharedPreferences("wew", MODE_PRIVATE).edit();
                editor.putInt("stepreset", stepAtReset);
                editor.apply();

                tv_steps.setText(String.valueOf(0));

                stopSensorListener();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        running = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (running) {
            stepInSensor = (int) event.values[0];
            int stepSinceReset = stepInSensor - stepAtReset;
            // tv_steps.setText(String.valueOf(event.values[0]));
            tv_steps.setText(String.valueOf(stepSinceReset));
        } else {
            event.values[0] = 0;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /*   @Override
       protected void onDestroy(){
           super.onDestroy();
           sensorManager.unregisterListener(this);
           sensorManager = null;
           running = false;

       }*/
    @Override
    protected void onStop() {
        super.onStop();

    }

    private void stopSensorListener() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this, sensor);
            sensorManager = null;
        }
    }

    @Override
    public void onBackPressed() {
        stopSensorListener();
        moveTaskToBack(true);
        finish();

    }
}
