package com.neusoft.qiangzi.remotecontroldemo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeStateViewModel extends ViewModel {

    MutableLiveData<HomeState> homeStateMutableLiveData;
    HomeState homeState;
    HomeStateViewModel(){
        homeState = new HomeState();
        homeStateMutableLiveData = new MutableLiveData<>(homeState);
    }

    public LiveData<HomeState> getHomeStateMutableLiveData() {
        return homeStateMutableLiveData;
    }

    void receivedRometeCommand(String cmd){
        if(cmd.equals("LIGHT_ON")){
            homeState.lighton = true;
        }else if(cmd.equals("LIGHT_OFF")){
            homeState.lighton = false;
        }
    }

}
