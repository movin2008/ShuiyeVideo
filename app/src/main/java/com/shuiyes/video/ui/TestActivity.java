package com.shuiyes.video.ui;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.shuiyes.video.R;

import java.util.Arrays;

import static java.lang.Math.sqrt;

public class TestActivity extends Activity implements SensorEventListener {

    private final String TAG = this.getClass().getSimpleName();

    private SensorManager sensorManager;
    private Sensor magneticSensor;
    private Sensor accelerometerSensor;
    private Sensor gyroscopeSensor;
    private Sensor oritationSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        orientation3 = (TextView) findViewById(R.id.orientation3);
        orientation = (TextView) this.findViewById(R.id.orientation);
        orientation2 = (TextView) this.findViewById(R.id.orientation2);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        oritationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册陀螺仪传感器，并设定传感器向应用中输出的时间间隔类型是SensorManager.SENSOR_DELAY_GAME(20000微秒)

        //SensorManager.SENSOR_DELAY_FASTEST(0微秒)：最快。最低延迟，一般不是特别敏感的处理不推荐使用，该模式可能在成手机电力大量消耗，由于传递的为原始数据，诉法不处理好会影响游戏逻辑和UI的性能
        //SensorManager.SENSOR_DELAY_GAME(20000微秒)：游戏。游戏延迟，一般绝大多数的实时性较高的游戏都是用该级别
        //SensorManager.SENSOR_DELAY_NORMAL(200000微秒):普通。标准延时，对于一般的益智类或EASY级别的游戏可以使用，但过低的采样率可能对一些赛车类游戏有跳帧现象
        //SensorManager.SENSOR_DELAY_UI(60000微秒):用户界面。一般对于屏幕方向自动旋转使用，相对节省电能和逻辑处理，一般游戏开发中不使用

        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, oritationSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    //记录rotationMatrix矩阵值
    private float[] r = new float[9];
    //记录通过getOrientation()计算出来的方位横滚俯仰值
    private float[] values = new float[3];
    private float[] gravity = {0f, 0f, 9.81f};
    private float[] geomagnetic = null;
    private TextView orientation, orientation2,orientation3;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (gravity != null && geomagnetic != null) {
                if (SensorManager.getRotationMatrix(r, null, gravity, geomagnetic)) {
                    SensorManager.getOrientation(r, values);
                    float degree = (float) ((360f + values[0] * 180f / Math.PI) % 360);
                    degree = Math.round(degree * 100f) / 100f;
                    if (Math.abs(mCarAngle2 - degree) > 1) {
                        mCarAngle2 = degree;
                        orientation2.setText(degree + " ℃");
                        Log.i(TAG, "计算出来的方位角＝" + degree);
                    }
                }
            }
        }
    };

    private float mCarAngle = 0;
    private float mCarAngle2 = 0;

    private static final float NS2S = 1.0f / 1000000000.0f;
    private static final float EPSILON = 1f;
    private final float[] deltaRotationVector = new float[4];
    private float[] mDeltaRotationMatrix = new float[9];

    private float timestamp;
    public double totalAngZ;

    //坐标轴都是手机从左侧到右侧的水平方向为x轴正向，从手机下部到上部为y轴正向，垂直于手机屏幕向上为z轴正向
    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {
//                case Sensor.TYPE_ACCELEROMETER: //加速度传感器
//                    gravity = event.values;
//                    handler.sendEmptyMessage(0);
//                    break;
            case Sensor.TYPE_MAGNETIC_FIELD://磁场传感器
                geomagnetic = event.values;
                handler.sendEmptyMessage(0);
                break;
            case Sensor.TYPE_ORIENTATION://方向传感器
                // 精度 0.01
                float x = (float) (Math.round(event.values[0] * 100)) / 100;
                if (Math.abs(mCarAngle - x) > 1) {
                    mCarAngle = x;
                    orientation.setText(x + "");
                    Log.i(TAG, "mCarAngle=" + mCarAngle);
                }
                break;
            case Sensor.TYPE_GYROSCOPE://磁场传感器
                // This timestep's delta rotation to be multiplied by the current rotation
                // after computing it from the gyro sample data.
                if (timestamp != 0 && event.timestamp - timestamp > 0) {
                    float ts = event.timestamp - timestamp;
                    final float dT = ts * NS2S;
                    // Axis of the rotation sample, not normalized yet.
                    float axisX = event.values[0];
                    float axisY = event.values[1];
                    float axisZ = event.values[2];
                    Log.i(TAG, "totalAngY = " + axisY*dT+" - "+dT);

                    float omegaMagnitude = (float) Math.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);
                    if (omegaMagnitude > EPSILON) {
                        axisX /= omegaMagnitude;
                        axisY /= omegaMagnitude;
                        axisZ /= omegaMagnitude;
                    }

                    float thetaOverTwo = omegaMagnitude * dT / 2.0f;
                    float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
                    float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
                    deltaRotationVector[0] = sinThetaOverTwo * axisX;
                    deltaRotationVector[1] = sinThetaOverTwo * axisY;
                    deltaRotationVector[2] = sinThetaOverTwo * axisZ;
                    deltaRotationVector[3] = cosThetaOverTwo;

                    float[] deltaRotationMatrix = new float[9];
                    SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);

                    totalAngZ += deltaRotationMatrix[0];
                    orientation3.setText(totalAngZ + "");
                    Log.i(TAG, "totalAngZ = " + Arrays.toString(deltaRotationMatrix));
                }
                timestamp = event.timestamp;
                break;
        }
    }

    /**
     mDeltaRotationMatrix = naivMatrixMultiply(mDeltaRotationMatrix, deltaRotationMatrix);
     float[] angleChange = new float[3];
     SensorManager.getAngleChange(angleChange, deltaRotationMatrix, mDeltaRotationMatrix);
     */

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Performs naiv n^3 matrix multiplication and returns C = A * B
     *
     * @param A Matrix in the array form (e.g. 3x3 => 9 values)
     * @param B Matrix in the array form (e.g. 3x3 => 9 values)
     * @return A * B
     */
    public float[] naivMatrixMultiply(float[] B, float[] A) {
        int nA, nB;
        nA = (int) Math.sqrt(A.length);
        nB = (int) Math.sqrt(B.length);

        if (nA != nB){
            throw new RuntimeException("Illegal matrix dimensions.");
        }

        float[] C = new float[nA * nA];
        for (int i = 0; i < nA; i++){
            for (int j = 0; j < nA; j++){
                for (int k = 0; k < nA; k++){
                    C[i + nA * j] += (A[i + nA * k] * B[k + nA * j]);
                }
            }
        }
        return C;
    }
}