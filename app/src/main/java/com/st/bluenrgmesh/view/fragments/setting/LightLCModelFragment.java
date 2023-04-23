/**
 * *****************************************************************************
 *
 * @file LightLCModelFragment.java
 * @author BLE Mesh Team
 * @version V1.12.000
 * @date    10-September-2020
 * @brief About Application file
 * *****************************************************************************
 * @attention <h2><center>&copy; COPYRIGHT(c) 2017 STMicroelectronics</center></h2>
 * <p>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 3. Neither the name of STMicroelectronics nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <p>
 * BlueNRG-Mesh is based on Motorolaâ€™s Mesh Over Bluetooth Low Energy (MoBLE)
 * technology. STMicroelectronics has done suitable updates in the firmware
 * and Android Mesh layers suitably.
 * <p>
 * *****************************************************************************
 */

package com.st.bluenrgmesh.view.fragments.setting;

import android.content.Context;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.msi.moble.ApplicationParameters;
import com.msi.moble.LightControlModeClient;
import com.st.bluenrgmesh.MainActivity;
import com.st.bluenrgmesh.R;
import com.st.bluenrgmesh.Utils;
import com.st.bluenrgmesh.adapter.models.LightLCModelAdapter;
import com.st.bluenrgmesh.logger.LoggerConstants;
import com.st.bluenrgmesh.models.meshdata.Element;
import com.st.bluenrgmesh.utils.AppDialogLoader;
import com.st.bluenrgmesh.view.fragments.base.BaseFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LightLCModelFragment extends BaseFragment {

    LightLCModelAdapter lightLCModelAdapter;
    RecyclerView recyclerView;
    Context context;
    private LightControlModeClient mLightLCModeModelClient = null;
    private static final String TAG_TEST = "MoBLEapp";
    List<Boolean> isStateOnOff =new ArrayList<Boolean>(Arrays.asList(new Boolean[3]));
    Element element;
    AppDialogLoader loader;
    Button lcPropertButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        this.context=context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_light_l_c_model, container, false);
        context = container.getContext();

        Utils.updateActionBarForFeatures(getActivity(), new LightLCModelFragment().getClass().getName());
        Collections.fill(isStateOnOff, Boolean.FALSE);
        element = (Element) getArguments().getSerializable(getString(R.string.key_serializable));
        initUI(view);
        loader = AppDialogLoader.getLoader(getActivity());
        mLightLCModeModelClient = ((MainActivity) context).app.mConfiguration.getNetwork().getLightnessLCModel();
        return view;
    }

    private void initUI(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerList);
        lcPropertButton = (Button) view.findViewById(R.id.lcPropertyButton);

        lcPropertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.moveToFragment((MainActivity) context, new LightLCPropertyFragment(), element, 0);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        lightLCModelAdapter = new LightLCModelAdapter(context ,isStateOnOff, new LightLCModelAdapter.IRecyclerViewHolderClicks() {
            @Override
            public void onClickRecyclerSwitch(int position, boolean isChecked) {
                isStateOnOff.set(position,isChecked);
                int stateValue;
                if(isChecked){
                    stateValue = 1;
                }
                else{
                    stateValue = 0;
                }

                if(position == 0){
                    sendLCOFFCommand(stateValue);
                }
                else if(position == 1){
                    sendOccupancy(stateValue);
                }
                else if(position == 2){
                    sendLCModeCommand(stateValue);
                }
                else if(position == 3){
                    sendLcPropertyCommand(isChecked);
                }
            }

            @Override
            public void onClickRecyclerRefresh(int position) {
                if(position == 0){
                    getLCONOFFCommand();
                }
                else if(position == 1){
                    getLCOccupancy();
                }
                else if(position ==2 ){
                    getLCModeCommand();
                }
            }
        });

        recyclerView.setAdapter(lightLCModelAdapter);
    }

    private void getLCOccupancy() {
        ApplicationParameters.Address address = new ApplicationParameters.Address(Integer.parseInt(element.getUnicastAddress()));
        mLightLCModeModelClient.getLightLCOM(address,lightLCOMStatusCallback);
    }

    private void getLCModeCommand() {
        ApplicationParameters.Address address = new ApplicationParameters.Address(Integer.parseInt(element.getUnicastAddress()));
        mLightLCModeModelClient.getLightLCMode(address,lightLCModeStatusCallback);
    }

    private void getLCONOFFCommand() {
        ApplicationParameters.Address address = new ApplicationParameters.Address(Integer.parseInt(element.getUnicastAddress()));
        Log.v(TAG_TEST,"onOff get command");
        mLightLCModeModelClient.getLightLCLightOnOff(address,
                lightLCOnOfflStatusCallback);
    }

    private void sendOccupancy(int i) {
        mLightLCModeModelClient = ((MainActivity) getActivity()).app.mConfiguration.getNetwork().getLightnessLCModel();
        ApplicationParameters.TID tid = new ApplicationParameters.TID(Utils.getTIDValue(context));
        ApplicationParameters.Address address = new ApplicationParameters.Address(Integer.parseInt(element.getUnicastAddress()));
        ApplicationParameters.Mode mode = new ApplicationParameters.Mode(i);

        mLightLCModeModelClient.setLightLCOM(Utils.isReliableEnabled(context), address, mode, Utils.isReliableEnabled(context) ? lightLCOMStatusCallback : null);

    }

    private void sendLCModeCommand(int stateValue) {
        mLightLCModeModelClient = ((MainActivity) getActivity()).app.mConfiguration.getNetwork().getLightnessLCModel();
        ApplicationParameters.Mode mode  = new ApplicationParameters.Mode(stateValue);
        ApplicationParameters.Address address = new ApplicationParameters.Address(Integer.parseInt(element.getUnicastAddress()));
        mLightLCModeModelClient.setLightLCMode(Utils.isReliableEnabled(context), address, mode, Utils.isReliableEnabled(context) ? lightLCModeStatusCallback : null);
    }

    private void sendLCOFFCommand(int stateValue) {
        mLightLCModeModelClient = ((MainActivity) getActivity()).app.mConfiguration.getNetwork().getLightnessLCModel();
        int tidValue = Utils.getTIDValue(getActivity());
        ApplicationParameters.TID tid = new ApplicationParameters.TID(tidValue);

        Log.v(TAG_TEST,String.valueOf(tidValue));

        ApplicationParameters.Address address = new ApplicationParameters.Address(Integer.parseInt(element.getUnicastAddress()));
        String onOffText = "DISABLED";
        if(stateValue == 1){
            onOffText = "ENABLED";
        }
        ApplicationParameters.OnOff state = new ApplicationParameters.OnOff(stateValue,onOffText);

        ApplicationParameters.Time transitionTime = new ApplicationParameters.Time(5, ApplicationParameters.Time.Unit.UNIT_1SEC);
        ApplicationParameters.Delay del = new ApplicationParameters.Delay(5);
        ((MainActivity)Utils.contextMainActivity).mUserDataRepository.getNewDataFromRemote("LC LightOnOff Command Send => " + address, LoggerConstants.TYPE_SEND);

        mLightLCModeModelClient.setLightControlLightOnOff(Utils.isReliableEnabled(context), address, state, tid, transitionTime, del, Utils.isReliableEnabled(context) ? lightLCOnOfflStatusCallback : null);
    }

    public void sendLcPropertyCommand(boolean isChecked){
        loader.show();
        ApplicationParameters.LightLCPropertyID propertyID = ApplicationParameters.LightLCPropertyID.AMBIENT_LUX_LEVEL_PROLONG;
        ApplicationParameters.LightLCPropertyValue  propertyVal = new ApplicationParameters.LightLCPropertyValue(90);
        ApplicationParameters.Address address = new ApplicationParameters.Address(Integer.parseInt(element.getUnicastAddress()));

        if(isChecked){
            mLightLCModeModelClient.setLightLCProperty(true, address,
                    propertyID,
                    propertyVal,
                    mLightLCPropertylStatusCallback);
        }
        else{
            mLightLCModeModelClient.getLightControlProperty(address,
                    propertyID,
                    mLightLCPropertylStatusCallback);
        }
    }



    LightControlModeClient.LightLCPropertylStatusCallback mLightLCPropertylStatusCallback = new LightControlModeClient.LightLCPropertylStatusCallback() {
        @Override
        public void onLightLCPropertyStatus(boolean timeout, ApplicationParameters.LightLCPropertyID propertyID,
                                            ApplicationParameters.LightLCPropertyValue propertyVal) {
            if(timeout){
                Log.i(TAG_TEST,"<<LightingLCServer  Model  => LightLCPropertylStatus Response status = " + "Timeout**");
                Utils.showToast(context,"Timeout detected");
            }
            else {
                Log.i(TAG_TEST,"<<LightingLCServer  Model  => LightLCPropertylStatus Response status = " + "Success**");
                Utils.showToast(context,"Property Value -> " + propertyVal);
            }
            loader.dismiss();
        }
    };



    LightControlModeClient.LightLCOnOfflStatusCallback lightLCOnOfflStatusCallback = new LightControlModeClient.LightLCOnOfflStatusCallback() {
        @Override
        public void onLightLCOnOffStatus(boolean timeout, ApplicationParameters.OnOff presentLightOnOff, ApplicationParameters.OnOff targetLightOnOff, ApplicationParameters.Time time) {
           // ApplicationParameters.Address address = new ApplicationParameters.Address(Integer.parseInt(element.getUnicastAddress()));
         //   ((MainActivity)Utils.contextMainActivity).mUserDataRepository.getNewDataFromRemote("LC LightOnOff Command Send => " + address, LoggerConstants.TYPE_RECEIVE);
            if(timeout){
                Log.i(TAG_TEST,"<<LightingLCServer  Model  => lightLCOnOfflStatus Response status = " + "Timeout**");
            }
            else {
                Log.i(TAG_TEST, "<<LightingLCServer  Model  => lightLCOnOfflStatus => Response statusONOFF  : " + presentLightOnOff+"**");
                Log.i(TAG_TEST, "<<LightingLCServer  Model  => lightLCOnOfflStatus => Response statusONOFF1  : " + targetLightOnOff+"**");
                Log.i(TAG_TEST, "<<LightingLCServer  Model  => lightLCOnOfflStatus => Response time  : " + time+"**");
                if(targetLightOnOff.toString().equals("DISABLED")){
                    isStateOnOff.set(0,false);
                }
                else{
                    isStateOnOff.set(0,true);
                }
                updateRecyclerAdapter();
            }
        }
    };

    LightControlModeClient.LightLCOMStatusCallback lightLCOMStatusCallback = new LightControlModeClient.LightLCOMStatusCallback() {
        @Override
        public void onLCOMStatus(boolean timeout, ApplicationParameters.Mode mode) {
            if(timeout){
                Log.i(TAG_TEST,"<<LightingLCServer  Model  => lightLCOMStatus Response status = " + "Timeout**");
            }
            else {
                Log.i(TAG_TEST,"<<LightingLCServer  Model  => lightLCOMStatus Response status = " + "SUCCESS**");
                Log.i(TAG_TEST, "<<LightingLCServer  Model  => lightLCOMStatus => Response mode  : " + mode+"**");
                if(mode.getValue() == 0){
                    isStateOnOff.set(1,false);
                }
                else{
                    isStateOnOff.set(1,true);
                }
                updateRecyclerAdapter();
            }
        }
    };


    //Light LC Model  CallBacks
    LightControlModeClient.LightLCModeStatusCallback lightLCModeStatusCallback = new LightControlModeClient.LightLCModeStatusCallback() {
        @Override
        public void onLCModeStatus(boolean timeout, ApplicationParameters.Mode mode) {
            if(timeout){
                Log.i(TAG_TEST,"<<LightingLCServer  Model  => LightLCModeStatus Response status = " + "Timeout**");
            }
            else {
                Log.i(TAG_TEST,"<<LightingLCServer  Model  => onLCModeStatus Response status = " + "SUCCESS**");
                Log.i(TAG_TEST, "<<LightingLCServer  Model  => LightLCModeStatus => Response mode  : " + mode+"**");
                if(mode.getValue() == 0){
                    isStateOnOff.set(2,false);
                }
                else{
                    isStateOnOff.set(2,true);
                }
                updateRecyclerAdapter();
            }
        }
    };

    private void updateRecyclerAdapter() {
        ((MainActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lightLCModelAdapter.setValues(isStateOnOff);
            }
        });
    }
}