package com.neusoft.qiangzi.remotecontroldemo;

import android.os.RemoteException;

import com.google.gson.Gson;
import com.neusoft.qiangzi.socketservicedemo.ISocketBinder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeStateViewModel extends ViewModel {

    MutableLiveData<HomeState> homeStateMutableLiveData;
    HomeState homeState;
    ISocketBinder binder;

    public HomeStateViewModel(){
        homeState = new HomeState();
        homeStateMutableLiveData = new MutableLiveData<>(homeState);
    }

    public LiveData<HomeState> getHomeStateMutableLiveData() {
        return homeStateMutableLiveData;
    }

    void receivedRometeCommand(String cmd){
        if(cmd.equals("KETING_LIGHT_ON")){
            homeState.setKetingLightOn(true);
        }else if(cmd.equals("KETING_LIGHT_OFF")){
            homeState.setKetingLightOn(false);
        }else if(cmd.equals("WOSHI_LIGHT_ON")){
            homeState.setWoshiLightOn(true);
        }else if(cmd.equals("WOSHI_LIGHT_OFF")){
            homeState.setWoshiLightOn(false);
        }else if(cmd.equals("AIRCON_ON")){
            homeState.setAirconOn(true);
        }else if(cmd.equals("AIRCON_OFF")){
            homeState.setAirconOn(false);
        }else if(cmd.equals("GET_HOME_STATE")){
            sendHomeStateToRemote();
        }else if(cmd.startsWith("DATA_HOME_STATE")){
            String json = cmd.substring(cmd.indexOf("=")+1);
            Gson gson = new Gson();
            HomeState hs = gson.fromJson(json, HomeState.class);
            homeState = hs;
        }
        homeStateMutableLiveData.postValue(homeState);
    }

    private void sendHomeStateToRemote() {
        Gson gson = new Gson();
        String json = gson.toJson(homeState, HomeState.class);
        try {
            binder.sendText("DATA_HOME_STATE="+json);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    void setKetingLightOn(boolean onoff){
        homeState.setKetingLightOn(onoff);
        homeStateMutableLiveData.setValue(homeState);
        sendHomeStateToRemote();
    }
    void setWoshiLightOn(boolean onoff){
        homeState.setWoshiLightOn(onoff);
        homeStateMutableLiveData.setValue(homeState);
        sendHomeStateToRemote();
    }
    void setAirconOn(boolean onoff){
        homeState.setAirconOn(onoff);
        homeStateMutableLiveData.setValue(homeState);
        sendHomeStateToRemote();
    }
    void setAirconTemp(int t){
        homeState.setAirconTemp(t);
        homeStateMutableLiveData.setValue(homeState);
        sendHomeStateToRemote();
    }

    public void setBinder(ISocketBinder binder) {
        this.binder = binder;
    }
}
