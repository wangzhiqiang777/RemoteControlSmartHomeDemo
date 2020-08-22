package com.neusoft.qiangzi.remotecontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.InputType;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.neusoft.qiangzi.socketservicedemo.IOnSocketReceivedListener;
import com.neusoft.qiangzi.socketservicedemo.ISocketBinder;

import java.util.List;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, ServiceConnection {
    ISocketBinder binder;
    Switch swKeting;
    Switch swWoshi;
    Switch swAirCon;
    EditText etTemp;

    HomeStateViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(HomeStateViewModel.class);

        swKeting = findViewById(R.id.switchKeTingLight);
        swWoshi = findViewById(R.id.switchWoShiLight);
        swAirCon = findViewById(R.id.switchAirCon);
        etTemp = findViewById(R.id.etTemp);
        viewModel.getHomeStateMutableLiveData().observe(this, new Observer<HomeState>() {
            @Override
            public void onChanged(HomeState homeState) {

                swKeting.setChecked(homeState.isKetingLightOn());
                swWoshi.setChecked(homeState.isWoshiLightOn());
                swAirCon.setChecked(homeState.isAirconLightOn());
                etTemp.setText(String.valueOf(homeState.getAirconTemp()));
            }
        });

        swKeting.setOnCheckedChangeListener(this);
        swWoshi.setOnCheckedChangeListener(this);
        swAirCon.setOnCheckedChangeListener(this);
        etTemp.setInputType(InputType.TYPE_NULL);


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: bind service..."+ISocketBinder.SERVICE_NAME);
//        if(isServiceRunning("SocketService")){
            Intent i = new Intent();
            i.setComponent(new ComponentName("com.neusoft.qiangzi.socketservicedemo","com.neusoft.qiangzi.socketservicedemo.SocketService"));
            bindService(i, this, BIND_AUTO_CREATE);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(binder!=null) {
            try {
                binder.unregisterListener(receivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(this);
            binder = null;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.switchAirCon:
                viewModel.setAirconOn(b);
                break;
            case R.id.switchKeTingLight:
                viewModel.setKetingLightOn(b);
                break;
            case R.id.switchWoShiLight:
                viewModel.setWoshiLightOn(b);
                break;
        }
    }

    private static final String TAG = "MainActivity";
    IOnSocketReceivedListener receivedListener = new IOnSocketReceivedListener.Stub() {
        @Override
        public void onReceived(String data) throws RemoteException {
            viewModel.receivedRometeCommand(data);
        }
    };
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        binder = ISocketBinder.Stub.asInterface(iBinder);
        viewModel.setBinder(binder);
        viewModel.getHomeState();
        try {
            Log.d(TAG, "onServiceConnected: is called.");
            binder.registerListener(receivedListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    boolean isServiceRunning(String serviceName){
        // 校验服务是否还存在
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(100);
        Log.d(TAG, "isServiceRunning: service list size="+services.size());
        for (ActivityManager.RunningServiceInfo info : services) {
            // 得到所有正在运行的服务的名称
            String name = info.service.getClassName();
            Log.d(TAG, "isServiceRunning: name = "+name);
            if (serviceName.contains(name)) {

                return true;
            }
        }
        return false;
    }
}
