package com.neusoft.qiangzi.remotecontroldemo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.neusoft.qiangzi.socketservicedemo.IOnSocketReceivedListener;
import com.neusoft.qiangzi.socketservicedemo.ISocketBinder;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    ISocketBinder binder;
    ImageView imageView;
    ImageView imageViewWoshi;
    ImageView imageViewAir;
    ToggleButton toggleButton;
    ToggleButton toggleButton2;
    ToggleButton toggleButton3;
    SeekBar seekBar;
    TextView tvTemp;
    TextView tvTemp3;

    HomeStateViewModel viewModel;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(HomeStateViewModel.class);

        imageView = findViewById(R.id.imageView);
        imageViewWoshi = findViewById(R.id.imageViewWoshi);
        imageViewAir = findViewById(R.id.imageViewAirCon);
        toggleButton = findViewById(R.id.toggleButton);
        toggleButton2 = findViewById(R.id.toggleButton2);
        toggleButton3 = findViewById(R.id.toggleButton3);
        tvTemp = findViewById(R.id.textViewTemp);
        tvTemp3 = findViewById(R.id.textView3);
        seekBar = findViewById(R.id.seekBar);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                viewModel.setKetingLightOn(b);
            }
        });
        toggleButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                viewModel.setWoshiLightOn(b);
            }
        });
        toggleButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                viewModel.setAirconOn(b);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tvTemp.setText(String.valueOf(i+15));
                tvTemp3.setText(String.valueOf(i+15));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                viewModel.setAirconTemp(seekBar.getProgress()+15);
            }
        });

        viewModel.getHomeStateMutableLiveData().observe(this, new Observer<HomeState>() {
            @Override
            public void onChanged(HomeState homeState) {
                if(homeState.isKetingLightOn()){
                    imageView.setImageResource(R.drawable.ic_android_green);
                }else{
                    imageView.setImageResource(R.drawable.ic_android_black);
                }
                if(homeState.isWoshiLightOn()){
                    imageViewWoshi.setImageResource(R.drawable.ic_baseline_wb_sunny_red);
                }else {
                    imageViewWoshi.setImageResource(R.drawable.ic_baseline_wb_sunny_gray);
                }
                if(homeState.isAirconLightOn()){
                    imageViewAir.setImageResource(R.drawable.aircontrol_on);
                }else {
                    imageViewAir.setImageResource(R.drawable.aircontrol_off);
                }

                tvTemp.setText(String.valueOf(homeState.getAirconTemp()));
                tvTemp3.setText(String.valueOf(homeState.getAirconTemp()));
                seekBar.setProgress(homeState.getAirconTemp()-15);
            }
        });

        Intent i = new Intent();
        i.setComponent(new ComponentName("com.neusoft.qiangzi.socketservicedemo","com.neusoft.qiangzi.socketservicedemo.SocketService"));
        bindService(i, this, BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        binder = ISocketBinder.Stub.asInterface(iBinder);
        viewModel.setBinder(binder);
        try {
            binder.registerListener(new IOnSocketReceivedListener.Stub() {
                @Override
                public void onReceived(String data) throws RemoteException {
                    viewModel.receivedRometeCommand(data);
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }
}