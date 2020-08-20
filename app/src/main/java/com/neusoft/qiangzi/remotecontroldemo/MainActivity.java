package com.neusoft.qiangzi.remotecontroldemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.neusoft.qiangzi.socketservicedemo.IOnSocketReceivedListener;
import com.neusoft.qiangzi.socketservicedemo.ISocketBinder;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    ISocketBinder binder;
    ImageView imageView;
    ToggleButton toggleButton;
    HomeStateViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(HomeStateViewModel.class);

        imageView = findViewById(R.id.imageView);
        toggleButton = findViewById(R.id.toggleButton);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                try {
                    if (b) {
                        binder.sendText("LIGHT_ON");
                    }else {
                        binder.sendText("LIGHT_OFF");
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

//        viewModel.getHomeStateMutableLiveData().observe(this, new Observer<HomeState>() {
//            @Override
//            public void onChanged(HomeState homeState) {
//                if(homeState.lighton){
//                    imageView.setImageResource(R.drawable.ic_android_green);
//                }else{
//                    imageView.setImageResource(R.drawable.ic_android_black);
//                }
//            }
//        });

        Intent i = new Intent();
        i.setComponent(new ComponentName("com.neusoft.qiangzi.socketservicedemo","com.neusoft.qiangzi.socketservicedemo.SocketService"));
        bindService(i, this, BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        binder = ISocketBinder.Stub.asInterface(iBinder);
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