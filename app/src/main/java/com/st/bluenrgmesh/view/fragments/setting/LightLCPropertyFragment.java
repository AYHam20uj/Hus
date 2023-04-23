package com.st.bluenrgmesh.view.fragments.setting;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Output;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.bumptech.glide.util.Util;
import com.msi.moble.ApplicationParameters;
import com.msi.moble.LightControlModeClient;
import com.st.bluenrgmesh.MainActivity;
import com.st.bluenrgmesh.R;
import com.st.bluenrgmesh.Utils;
import com.st.bluenrgmesh.models.LCPropertyModel;
import com.st.bluenrgmesh.models.meshdata.Element;
import com.st.bluenrgmesh.view.fragments.base.BaseFragment;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class LightLCPropertyFragment extends BaseFragment {

    Context context;
    Element element;
    Spinner standbySpinner, prolongSpinner;
    Button readButton, saveButton;
    EditText standbyEdit, fadeOnEdit, runEdit, fadeProlongEdit, prolongEdit1, prolongEdit2, fadeStandbyEdit;
    private LightControlModeClient mLightControlModeClient = null;
    int standbyUnit = 0;
    int prolongUnit = 0;
    private LightingLCModelTestCaseMessage mHandler;
    int getCounter = 0;
    int setCounter = 0;
    LCPropertyModel lcProeprtyModel;
    Dialog mDialog = null;

    ApplicationParameters.LightLCPropertyID standbyLuxID = ApplicationParameters.LightLCPropertyID.AMBIENT_LUX_LEVEL_STANDBY;
    ApplicationParameters.LightLCPropertyID standbyLightnessID = ApplicationParameters.LightLCPropertyID.LIGHTNESS_STANDBY;
    ApplicationParameters.LightLCPropertyID fadeOnTimeID = ApplicationParameters.LightLCPropertyID.TIME_FADE_ON;
    ApplicationParameters.LightLCPropertyID runTimeID = ApplicationParameters.LightLCPropertyID.TIME_RUN_ON;
    ApplicationParameters.LightLCPropertyID fadeToStandbyID = ApplicationParameters.LightLCPropertyID.TIME_FADE_STANDBY_MANUAL;
    ApplicationParameters.LightLCPropertyID fadeToProlongID = ApplicationParameters.LightLCPropertyID.TIME_FADE;
    ApplicationParameters.LightLCPropertyID prolongLuxID = ApplicationParameters.LightLCPropertyID.AMBIENT_LUX_LEVEL_PROLONG;
    ApplicationParameters.LightLCPropertyID prolongLightnessID = ApplicationParameters.LightLCPropertyID.LIGHTNESS_ON_PROLONG;
    ApplicationParameters.LightLCPropertyID prolongTimeID = ApplicationParameters.LightLCPropertyID.TIME_PROLONG;

    private final static int MSG_STANDBY_LUXID_GET  = 1;
    private final static int MSG_STANDBY_LIGHTNESSID_GET = 2;
    private final static int MSG_FADEON_TIMEID_GET = 3;
    private final static int MSG_RUN_TIMEID_GET = 4;
    private final static int MSG_FADE_STANDBYID_GET = 5;
    private static final int MSG_FADE_PROLONGID_GET = 6;
    private final static int MSG_PROLONG_LUXID_GET = 7;
    private static final int MSG_PROLONG_LIGHTNESSID_GET = 8;
    private static final int MSG_PROLONG_TIMEID_GET = 9;


    private final static int MSG_STANDBY_LUXID_SET  = 11;
    private final static int MSG_STANDBY_LIGHTNESSID_SET = 12;
    private final static int MSG_FADEON_TIMEID_SET = 13;
    private final static int MSG_RUN_TIMEID_SET = 14;
    private final static int MSG_FADE_STANDBYID_SET = 15;
    private static final int MSG_FADE_PROLONGID_SET = 16;
    private final static int MSG_PROLONG_LUXID_SET = 17;
    private static final int MSG_PROLONG_LIGHTNESSID_SET = 18;
    private static final int MSG_PROLONG_TIMEID_SET = 19;

    boolean isStanbyLux = true, isStandbyLightness = true, isFadeOnTime = true, isRunTime = true, isFadeStanby = true, isFadeProlong = true,
            isProlongLux = true, isProlongLightness = true, isProlongTime = true;

    double timeMaxValue = 16777214;
    int lightnessMaxValue = 65535;
    double luxLevelMaxValue = 16777214;

    ApplicationParameters.Address address;

    private static final String TAG_TEST = "MoBLEapp";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_light_l_c_property, container, false);
        context = container.getContext();
        element = (Element) getArguments().getSerializable(getString(R.string.key_serializable));
        address = new ApplicationParameters.Address(Integer.parseInt(element.getUnicastAddress()));
        initUI(view);
        lcProeprtyModel = new LCPropertyModel();
        addDefaultValuesinModel();
        mHandler = new LightingLCModelTestCaseMessage(this);

        Utils.updateActionBarForFeatures(getActivity(), new LightLCPropertyFragment().getClass().getName());
        mLightControlModeClient = ((MainActivity) context).app.mConfiguration.getNetwork().getLightnessLCModel();

        return view;
    }

    private void addDefaultValuesinModel() {
        lcProeprtyModel.setStandByEditUnit(0);
        lcProeprtyModel.setStandByEditValue("0");
        lcProeprtyModel.setProlongEditUnit(0);
        lcProeprtyModel.setProlongEditValue("0");

        lcProeprtyModel.setProlongTimeValue("0");
        lcProeprtyModel.setFadeOnTimeValue("0");
        lcProeprtyModel.setRunTimeValue("0");
        lcProeprtyModel.setFadeProlongTimeValue("0");
        lcProeprtyModel.setFadestandbyTimeValue("0");
    }

    private void initUI(View view) {
        standbySpinner = view.findViewById(R.id.standbySpinner);
        prolongSpinner = view.findViewById(R.id.prolongLightnessSpinner);
        readButton = view.findViewById(R.id.readButton);
        saveButton = view.findViewById(R.id.saveButton);

        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  getCount();
                getCounter = 0;
                Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(150);
                showProvisioningPopup(context,1);

                if(standbySpinner.getSelectedItemPosition() == 0){
                    isStanbyLux = true;
                    isStandbyLightness = false;
                }
                else {
                    isStanbyLux = false;
                    isStandbyLightness = true;
                }

                if(prolongSpinner.getSelectedItemPosition() == 0){
                    isProlongLux = true;
                    isProlongLightness = false;
                }
                else {
                    isProlongLux = false;
                    isProlongLightness = true;
                }


                isFadeOnTime = false;
                isRunTime = false;
                isFadeStanby = false;
                isFadeProlong = false;
                isProlongTime = false;;
                getAllPropertyStatus();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCounter = 0;
                showProvisioningPopup(context,1);
                Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(150);
                checkForPropertyValueChange();
                boolean isValid = checkValuesInRange();
                if(isValid){
                    if(isFadeStanby && isProlongLux && isProlongLightness && isStanbyLux && isStandbyLightness && isRunTime && isFadeOnTime && isFadeProlong && isProlongTime){
                        showProvisioningPopup(context,2);
                        Utils.showToast(context,"No Property Changed!");
                    }
                    else{
                        setPropertyStatus();
                    }
                }
                else{
                    showProvisioningPopup(context,2);
                }
            }
        });

        standbyEdit = view.findViewById(R.id.standbyEdit);
        fadeOnEdit = view.findViewById(R.id.fadeOnEdit);
        runEdit = view.findViewById(R.id.runEdit);
        fadeProlongEdit = view.findViewById(R.id.fadeProlongEdit);
        prolongEdit1 = view.findViewById(R.id.prolongEdit1);
        prolongEdit2 = view.findViewById(R.id.prolongEdit2);
        fadeStandbyEdit = view.findViewById(R.id.fadeStandbyEdit);

        List<String> list = new ArrayList<String>();
        list.add("Lightness");
        list.add("Lux Level");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        standbySpinner.setAdapter(dataAdapter);
        prolongSpinner.setAdapter(dataAdapter);
    }

    private byte[] bigIntToByteArray( final int i ) {
        BigInteger bigInt = BigInteger.valueOf(i);
        return bigInt.toByteArray();
    }

    private void getCount() {
        /*ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        double i = 16777.210;
        try {
            dos.writeDouble(i);
            dos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        byte [] arr = bos.toByteArray();*/



        BigInteger bigInt = BigInteger.valueOf(1677214);
        byte [] arr =  bigInt.toByteArray();


        byte b1, b2 , b3; // b1 is most significant, then b2 then b3.

        b3 = (byte) (1677214 & 0xFF);
        b2 = (byte) ((1677214 >> 8) & 0xFF);
        b1 = (byte) ((1677214 >> 16) & 0xFF);

        byte [] asdasd = new byte[3];
        asdasd[0] = b1;
        asdasd[1] = b2;
        asdasd[2] = b3;

        final int xx = ByteBuffer.wrap(new byte[] { 0x00, b1, b2, b3 }).getInt();



        BigInteger bigInaat = BigInteger.valueOf(10);
        byte [] arraaa =  bigInaat.toByteArray();

        byte[] input =  new byte[3];
       /* input[0] = (byte) 3;
        input[1] = (byte) -24;
        input[2] = (byte) 0;*/

        ByteBuffer buffer = ByteBuffer.wrap(input, 0, 3);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put(arr);


        final byte[] array = new byte[] { 0x00, (byte) 3, (byte) -24, 0x00 };//you need 4 bytes to get an integer (padding with a 0 byte)
        final int x = ByteBuffer.wrap(new byte[] { 0x00, (byte) 3, (byte) -24, 0x00 }).getInt();


        int f = (input[2] & 0xFF) << 16 | (input[1] & 0xFF) <<8 | (input[0] & 0xFF);
        int fgfg= f;
    }

    private boolean checkValuesInRange() {
        int fadeOnTimeValue = Integer.parseInt(fadeOnEdit.getText().toString());
        int runOnTimeValue = Integer.parseInt(runEdit.getText().toString());
        int fadeStandbyTimeValue = Integer.parseInt(fadeStandbyEdit.getText().toString());
        int fadeProlongTimeValue = Integer.parseInt(fadeProlongEdit.getText().toString());
        int prolongTimeValue = Integer.parseInt(prolongEdit1.getText().toString());
        if(fadeOnTimeValue < 0 || fadeOnTimeValue > timeMaxValue){
            Utils.showToast(context,"Fade On value invalid.");
            return false;
        }
        else if(runOnTimeValue < 0 || runOnTimeValue > timeMaxValue){
            Utils.showToast(context,"Run time value invalid.");
            return false;
        }
        else if(fadeStandbyTimeValue < 0 || fadeStandbyTimeValue > timeMaxValue){
            Utils.showToast(context,"Fade to standby value invalid.");
            return false;
        }
        else if(fadeProlongTimeValue < 0 || fadeProlongTimeValue > timeMaxValue){
            Utils.showToast(context,"Fade to prolong value invalid.");
            return false;
        }
        else if(prolongTimeValue < 0 || prolongTimeValue > timeMaxValue){
            Utils.showToast(context,"Prolong time value invalid.");
            return false;
        }
        else{
            int standbyPosition = standbySpinner.getSelectedItemPosition();
            if(standbyPosition == 0){
                int standbyLightness = Integer.parseInt(standbyEdit.getText().toString());
                if(standbyLightness < 0 || standbyLightness > lightnessMaxValue){
                    Utils.showToast(context,"Standby Lightness value invalid.");
                    return false;
                }
            }
            else{
                int standbyLux = Integer.parseInt(standbyEdit.getText().toString());
                if(standbyLux < 0 || standbyLux > luxLevelMaxValue){
                    Utils.showToast(context,"Standby Lux value invalid.");
                    return false;
                }
            }

            int prolongPosition = prolongSpinner.getSelectedItemPosition();
            if(prolongPosition == 0){
                int prolongLightness = Integer.parseInt(prolongEdit2.getText().toString());
                if(prolongLightness < 0 || prolongLightness > lightnessMaxValue){
                    Utils.showToast(context,"Prolong Lightness value invalid.");
                    return false;
                }
            }
            else{
                int prolongLux = Integer.parseInt(prolongEdit2.getText().toString());
                if(prolongLux < 0 || prolongLux > luxLevelMaxValue){
                    Utils.showToast(context,"Prolong Lux value invalid.");
                    return false;
                }
            }
        }
        return true;
    }

    private void setPropertyStatus() {


        Message msg = new Message();

        if(setCounter == 0){
            msg.what = MSG_FADEON_TIMEID_SET;
            mHandler.sendMessageDelayed(msg,1000);
        }
        else if(setCounter == 1){
            msg.what = MSG_RUN_TIMEID_SET;
            mHandler.sendMessageDelayed(msg,1000);
        }
        else if(setCounter == 2){
            msg.what = MSG_FADE_PROLONGID_SET;
            mHandler.sendMessageDelayed(msg,1000);
        }
        else if(setCounter == 3){
            msg.what = MSG_FADE_STANDBYID_SET;
            mHandler.sendMessageDelayed(msg,1000);
        }
        else if(setCounter == 4){
            msg.what = MSG_PROLONG_TIMEID_SET;
            mHandler.sendMessageDelayed(msg,1000);
        }
        else if(setCounter == 5){
            standbyUnit = standbySpinner.getSelectedItemPosition();
            if(standbyUnit == 0){
                msg.what = MSG_STANDBY_LIGHTNESSID_SET;
            }
            else if(standbyUnit == 1){
                msg.what = MSG_STANDBY_LUXID_SET;
            }
            mHandler.sendMessageDelayed(msg,1000);
        }
        else if(setCounter == 6){
            prolongUnit = prolongSpinner.getSelectedItemPosition();
            if(prolongUnit == 0){
                msg.what = MSG_PROLONG_LIGHTNESSID_SET;
            }
            else if(prolongUnit == 1){
                msg.what = MSG_PROLONG_LUXID_SET;
            }
            mHandler.sendMessageDelayed(msg,1000);
        }
    }

    private void checkForPropertyValueChange() {
        if(lcProeprtyModel.getProlongEditUnit() != prolongSpinner.getSelectedItemPosition()){
            if(prolongSpinner.getSelectedItemPosition() == 0){
                isProlongLightness = false;
            }
            else{
                isProlongLux = false;
            }
        }

        if(!(lcProeprtyModel.getProlongEditValue().equalsIgnoreCase(prolongEdit2.getText().toString()))){
            if(prolongSpinner.getSelectedItemPosition() == 0){
                isProlongLightness = false;
            }
            else{
                isProlongLux = false;
            }
        }

        if(!(lcProeprtyModel.getStandByEditValue().equalsIgnoreCase(standbyEdit.getText().toString()))){
            if(standbySpinner.getSelectedItemPosition() == 0){
                isStandbyLightness = false;
            }
            else{
                isStanbyLux = false;
            }
        }

        if(lcProeprtyModel.getStandByEditUnit() != standbySpinner.getSelectedItemPosition()){
            if(standbySpinner.getSelectedItemPosition() == 0){
                isStandbyLightness = false;
            }
            else{
                isStanbyLux = false;
            }
        }

        if(!(lcProeprtyModel.getFadeOnTimeValue().equalsIgnoreCase(fadeOnEdit.getText().toString()))){
            isFadeOnTime = false;
        }

        if(!(lcProeprtyModel.getRunTimeValue().equalsIgnoreCase(runEdit.getText().toString()))){
            isRunTime = false;
        }

        if(!(lcProeprtyModel.getFadeProlongTimeValue().equalsIgnoreCase(fadeProlongEdit.getText().toString()))){
            isFadeProlong = false;
        }

        if(!(lcProeprtyModel.getFadestandbyTimeValue().equalsIgnoreCase(fadeStandbyEdit.getText().toString()))){
            isFadeStanby = false;
        }

        if(!(lcProeprtyModel.getProlongTimeValue().equalsIgnoreCase(prolongEdit1.getText().toString()))){
            isProlongTime = false;
        }
    }

    private void getAllPropertyStatus() {
        Message msg = new Message();

        if(getCounter == 0){
            msg.what = MSG_FADEON_TIMEID_GET;
            mHandler.sendMessageDelayed(msg,1000);
        }
        else if(getCounter == 1){
            msg.what = MSG_RUN_TIMEID_GET;
            mHandler.sendMessageDelayed(msg,1000);
        }
        else if(getCounter == 2){
            msg.what = MSG_FADE_PROLONGID_GET;
            mHandler.sendMessageDelayed(msg,1000);
        }
        else if(getCounter == 3){
            msg.what = MSG_FADE_STANDBYID_GET;
            mHandler.sendMessageDelayed(msg,1000);
        }
        else if(getCounter == 4){
            msg.what = MSG_PROLONG_TIMEID_GET;
            mHandler.sendMessageDelayed(msg,1000);
        }
        else if(getCounter == 5){
            standbyUnit = standbySpinner.getSelectedItemPosition();
            if(standbyUnit == 0){
                msg.what = MSG_STANDBY_LIGHTNESSID_GET;
            }
            else if(standbyUnit == 1){
                msg.what = MSG_STANDBY_LUXID_GET;
            }
            mHandler.sendMessageDelayed(msg,1000);
        }
        else if(getCounter == 6){
            prolongUnit = prolongSpinner.getSelectedItemPosition();
            if(prolongUnit == 0){
                msg.what = MSG_PROLONG_LIGHTNESSID_GET;
            }
            else if(prolongUnit == 1){
                msg.what = MSG_PROLONG_LUXID_GET;
            }
            mHandler.sendMessageDelayed(msg,1000);
        }
    }

    LightControlModeClient.LightLCPropertylStatusCallback mLightLCPropertylStatusCallback = new LightControlModeClient.LightLCPropertylStatusCallback() {
        @Override
        public void onLightLCPropertyStatus(boolean timeout, ApplicationParameters.LightLCPropertyID propertyID,
                                            ApplicationParameters.LightLCPropertyValue propertyVal) {
            if(timeout){
                Log.i(TAG_TEST,"<<LightingLCServer  Model  => LightLCPropertylStatus Response status = " + "Timeout**");
                if(propertyID.getValue() == fadeOnTimeID.getValue()){
                    isFadeOnTime = true;
                }
                else if(propertyID.getValue() == runTimeID.getValue()){
                    isRunTime = true;
                }
                else if(propertyID.getValue() == fadeToProlongID.getValue()){
                    isFadeProlong = true;
                }
                else if(propertyID.getValue() == fadeToStandbyID.getValue()){
                    isFadeStanby = true;
                }
                else if(propertyID.getValue() == prolongTimeID.getValue()){
                    isProlongTime = true;
                }
                else if(propertyID.getValue() == standbyLightnessID.getValue()){
                    isStandbyLightness = true;
                }
                else if(propertyID.getValue() == standbyLuxID.getValue()){
                    isStanbyLux = true;
                }
                else if(propertyID.getValue() == prolongLightnessID.getValue()){
                    isProlongLightness = true;
                }
                else if(propertyID.getValue() == prolongLuxID.getValue()){
                    isProlongLux = true;
                }
            }
            else {
                if(propertyVal != null){
                    if(propertyID.getValue() == fadeOnTimeID.getValue()){
                        isFadeOnTime = true;
                        lcProeprtyModel.setFadeOnTimeValue(String.valueOf(propertyVal.getValue()));
                        fadeOnEdit.setText(String.valueOf(propertyVal.getValue()));
                    }
                    else if(propertyID.getValue() == runTimeID.getValue()){
                        isRunTime = true;
                        lcProeprtyModel.setRunTimeValue(String.valueOf(propertyVal.getValue()));
                        runEdit.setText(String.valueOf(propertyVal.getValue()));
                    }
                    else if(propertyID.getValue() == fadeToProlongID.getValue()){
                        isFadeProlong = true;
                        lcProeprtyModel.setFadeProlongTimeValue(String.valueOf(propertyVal.getValue()));
                        fadeProlongEdit.setText(String.valueOf(propertyVal.getValue()));
                    }
                    else if(propertyID.getValue() == fadeToStandbyID.getValue()){
                        isFadeStanby = true;
                        lcProeprtyModel.setFadestandbyTimeValue(String.valueOf(propertyVal.getValue()));
                        fadeStandbyEdit.setText(String.valueOf(propertyVal.getValue()));
                    }
                    else if(propertyID.getValue() == prolongTimeID.getValue()){
                        isProlongTime = true;
                        lcProeprtyModel.setProlongTimeValue(String.valueOf(propertyVal.getValue()));
                        prolongEdit1.setText(String.valueOf(propertyVal.getValue()));
                    }
                    else if(propertyID.getValue() == standbyLightnessID.getValue()){
                        isStandbyLightness = true;
                        lcProeprtyModel.setStandByEditValue(String.valueOf(propertyVal.getValue()));
                        lcProeprtyModel.setStandByEditUnit(0);
                        standbyEdit.setText(String.valueOf(propertyVal.getValue()));
                        standbySpinner.setSelection(0);
                    }
                    else if(propertyID.getValue() == standbyLuxID.getValue()){
                        isStanbyLux = true;
                        lcProeprtyModel.setStandByEditValue(String.valueOf(propertyVal.getValue()));
                        lcProeprtyModel.setStandByEditUnit(1);
                        standbyEdit.setText(String.valueOf(propertyVal.getValue()));
                        standbySpinner.setSelection(1);
                    }
                    else if(propertyID.getValue() == prolongLightnessID.getValue()){
                        isProlongLightness = true;
                        lcProeprtyModel.setProlongEditValue(String.valueOf(propertyVal.getValue()));
                        lcProeprtyModel.setProlongEditUnit(0);
                        prolongEdit2.setText(String.valueOf(propertyVal.getValue()));
                        standbySpinner.setSelection(0);
                    }
                    else if(propertyID.getValue() == prolongLuxID.getValue()){
                        isProlongLux = true;
                        lcProeprtyModel.setProlongEditValue(String.valueOf(propertyVal.getValue()));
                        lcProeprtyModel.setProlongEditUnit(1);
                        prolongEdit2.setText(String.valueOf(propertyVal.getValue()));
                        standbySpinner.setSelection(1);
                    }
                }
            }
            if(isFadeStanby && isProlongLux && isProlongLightness && isStanbyLux && isStandbyLightness && isRunTime && isFadeOnTime && isFadeProlong && isProlongTime){
                showProvisioningPopup(context,2);
            }
        }
    };


    LightControlModeClient.LightLCPropertylStatusCallback mLightLCPropertylSetStatusCallback = new LightControlModeClient.LightLCPropertylStatusCallback() {
        @Override
        public void onLightLCPropertyStatus(boolean timeout, ApplicationParameters.LightLCPropertyID propertyID,
                                            ApplicationParameters.LightLCPropertyValue propertyVal) {
            if(timeout){
                Log.i(TAG_TEST,"<<LightingLCServer  Model  => LightLCPropertylStatus Response status = " + "Timeout**");
                if(propertyID.getValue() == fadeOnTimeID.getValue()){
                    isFadeOnTime = true;
                }
                else if(propertyID.getValue() == runTimeID.getValue()){
                    isRunTime = true;
                }
                else if(propertyID.getValue() == fadeToProlongID.getValue()){
                    isFadeProlong = true;
                }
                else if(propertyID.getValue() == fadeToStandbyID.getValue()){
                    isFadeStanby = true;
                }
                else if(propertyID.getValue() == prolongTimeID.getValue()){
                    isProlongTime = true;
                }
                else if(propertyID.getValue() == standbyLightnessID.getValue()){
                    isStandbyLightness = true;
                }
                else if(propertyID.getValue() == standbyLuxID.getValue()){
                    isStanbyLux = true;
                }
                else if(propertyID.getValue() == prolongLightnessID.getValue()){
                    isProlongLightness = true;
                }
                else if(propertyID.getValue() == prolongLuxID.getValue()){
                    isProlongLux = true;
                }
            }
            else {
                if(propertyVal != null){
                    if(propertyID.getValue() == fadeOnTimeID.getValue()){
                        isFadeOnTime = true;
                        lcProeprtyModel.setFadeOnTimeValue(String.valueOf(propertyVal.getValue()));
                    }
                    else if(propertyID.getValue() == runTimeID.getValue()){
                        isRunTime = true;
                        lcProeprtyModel.setRunTimeValue(String.valueOf(propertyVal.getValue()));
                    }
                    else if(propertyID.getValue() == fadeToProlongID.getValue()){
                        isFadeProlong = true;
                        lcProeprtyModel.setFadeProlongTimeValue(String.valueOf(propertyVal.getValue()));
                    }
                    else if(propertyID.getValue() == fadeToStandbyID.getValue()){
                        isFadeStanby = true;
                        lcProeprtyModel.setFadestandbyTimeValue(String.valueOf(propertyVal.getValue()));
                    }
                    else if(propertyID.getValue() == prolongTimeID.getValue()){
                        isProlongTime = true;
                        lcProeprtyModel.setProlongTimeValue(String.valueOf(propertyVal.getValue()));
                    }
                    else if(propertyID.getValue() == standbyLightnessID.getValue()){
                        isStandbyLightness = true;
                        lcProeprtyModel.setStandByEditValue(String.valueOf(propertyVal.getValue()));
                        lcProeprtyModel.setStandByEditUnit(0);
                    }
                    else if(propertyID.getValue() == standbyLuxID.getValue()){
                        isStanbyLux = true;
                        lcProeprtyModel.setStandByEditValue(String.valueOf(propertyVal.getValue()));
                        lcProeprtyModel.setStandByEditUnit(1);
                    }
                    else if(propertyID.getValue() == prolongLightnessID.getValue()){
                        isProlongLightness = true;
                        lcProeprtyModel.setProlongEditValue(String.valueOf(propertyVal.getValue()));
                        lcProeprtyModel.setProlongEditUnit(0);
                    }
                    else if(propertyID.getValue() == prolongLuxID.getValue()){
                        isProlongLux = true;
                        lcProeprtyModel.setProlongEditValue(String.valueOf(propertyVal.getValue()));
                        lcProeprtyModel.setProlongEditUnit(1);
                    }
                }
            }

            if(isFadeStanby && isProlongLux && isProlongLightness && isStanbyLux && isStandbyLightness && isRunTime && isFadeOnTime && isFadeProlong && isProlongTime){
                showProvisioningPopup(context,2);
            }

        }
    };

    private class LightingLCModelTestCaseMessage extends Handler {

        private final WeakReference<LightLCPropertyFragment> lightinglc;

        LightingLCModelTestCaseMessage(LightLCPropertyFragment activity) {
            super(Looper.getMainLooper());
            lightinglc = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final LightLCPropertyFragment messageSender = lightinglc.get();
            if (messageSender == null) return;

            switch (msg.what) {

                case MSG_FADEON_TIMEID_GET:
                    mLightControlModeClient.getLightControlProperty(address,
                            fadeOnTimeID,
                            mLightLCPropertylStatusCallback);
                    getCounter++;
                    getAllPropertyStatus();
                    break;

                case MSG_FADE_PROLONGID_GET:
                    mLightControlModeClient.getLightControlProperty(address,
                            fadeToProlongID,
                            mLightLCPropertylStatusCallback);
                    getCounter++;
                    getAllPropertyStatus();
                    break;

                case MSG_FADE_STANDBYID_GET:
                    mLightControlModeClient.getLightControlProperty(address,
                            fadeToStandbyID,
                            mLightLCPropertylStatusCallback);
                    getCounter++;
                    getAllPropertyStatus();
                    break;

                case MSG_RUN_TIMEID_GET:
                    mLightControlModeClient.getLightControlProperty(address,
                            runTimeID,
                            mLightLCPropertylStatusCallback);
                    getCounter++;
                    getAllPropertyStatus();
                    break;

                case MSG_PROLONG_TIMEID_GET:
                    mLightControlModeClient.getLightControlProperty(address,
                            prolongTimeID,
                            mLightLCPropertylStatusCallback);
                    getCounter++;
                    getAllPropertyStatus();
                    break;

                case MSG_STANDBY_LIGHTNESSID_GET:
                    mLightControlModeClient.getLightControlLightnessProperty(address,
                            standbyLightnessID,
                            mLightLCPropertylStatusCallback);
                    getCounter++;
                    getAllPropertyStatus();
                    break;

                case MSG_STANDBY_LUXID_GET:
                    mLightControlModeClient.getLightControlProperty(address,
                            standbyLuxID,
                            mLightLCPropertylStatusCallback);
                    getCounter++;
                    getAllPropertyStatus();
                    break;

                case MSG_PROLONG_LIGHTNESSID_GET:
                    mLightControlModeClient.getLightControlLightnessProperty(address,
                            prolongLightnessID,
                            mLightLCPropertylStatusCallback);
                    getCounter++;
                    getAllPropertyStatus();
                    break;

                case MSG_PROLONG_LUXID_GET:
                    mLightControlModeClient.getLightControlProperty(address,
                            prolongLuxID,
                            mLightLCPropertylStatusCallback);
                    getCounter++;
                    getAllPropertyStatus();
                    break;

                case MSG_FADEON_TIMEID_SET:
                    if(!(fadeOnEdit.getText().toString().equalsIgnoreCase(lcProeprtyModel.getFadeOnTimeValue()))){
                        ApplicationParameters.LightLCPropertyValue lightLCPropertyValue = new ApplicationParameters.LightLCPropertyValue(Integer.parseInt(fadeOnEdit.getText().toString()));
                        mLightControlModeClient.setLightLCProperty(true,address,
                                fadeOnTimeID,lightLCPropertyValue,
                                mLightLCPropertylSetStatusCallback);
                    }
                    setCounter++;
                    setPropertyStatus();
                    break;

                case MSG_FADE_PROLONGID_SET:
                    if(!(fadeProlongEdit.getText().toString().equalsIgnoreCase(lcProeprtyModel.getFadeProlongTimeValue()))){
                        ApplicationParameters.LightLCPropertyValue lightLCPropertyValue = new ApplicationParameters.LightLCPropertyValue(Integer.parseInt(fadeProlongEdit.getText().toString()));
                        mLightControlModeClient.setLightLCProperty(true,address,
                                fadeToProlongID,lightLCPropertyValue,
                                mLightLCPropertylSetStatusCallback);
                    }
                    setCounter++;
                    setPropertyStatus();
                    break;

                case MSG_FADE_STANDBYID_SET:
                    if(!(fadeStandbyEdit.getText().toString().equalsIgnoreCase(lcProeprtyModel.getFadestandbyTimeValue()))){
                        ApplicationParameters.LightLCPropertyValue lightLCPropertyValue = new ApplicationParameters.LightLCPropertyValue(Integer.parseInt(fadeStandbyEdit.getText().toString()));
                        mLightControlModeClient.setLightLCProperty(true,address,
                                fadeToStandbyID,lightLCPropertyValue,
                                mLightLCPropertylSetStatusCallback);
                    }
                    setCounter++;
                    setPropertyStatus();
                    break;

                case MSG_RUN_TIMEID_SET:
                    if(!(runEdit.getText().toString().equalsIgnoreCase(lcProeprtyModel.getRunTimeValue()))){
                        ApplicationParameters.LightLCPropertyValue lightLCPropertyValue = new ApplicationParameters.LightLCPropertyValue(Integer.parseInt(runEdit.getText().toString()));
                        mLightControlModeClient.setLightLCProperty(true,address,
                                runTimeID,lightLCPropertyValue,
                                mLightLCPropertylSetStatusCallback);
                    }
                    setCounter++;
                    setPropertyStatus();
                    break;

                case MSG_PROLONG_TIMEID_SET:
                    if(!(prolongEdit1.getText().toString().equalsIgnoreCase(lcProeprtyModel.getProlongTimeValue()))){
                        ApplicationParameters.LightLCPropertyValue lightLCPropertyValue = new ApplicationParameters.LightLCPropertyValue(Integer.parseInt(prolongEdit1.getText().toString()));
                        mLightControlModeClient.setLightLCProperty(true,address,
                                prolongTimeID,lightLCPropertyValue,
                                mLightLCPropertylSetStatusCallback);
                    }
                    setCounter++;
                    setPropertyStatus();
                    break;

                case MSG_STANDBY_LIGHTNESSID_SET:
                    if(!(standbyEdit.getText().toString().equalsIgnoreCase(lcProeprtyModel.getStandByEditValue()))){
                        ApplicationParameters.PropertyValue lightLCPropertyValue = new ApplicationParameters.PropertyValue(Integer.parseInt(standbyEdit.getText().toString()));
                        mLightControlModeClient.setLightLCLightnessProperty(true,address,
                                standbyLightnessID,lightLCPropertyValue,
                                mLightLCPropertylSetStatusCallback);
                    }
                    else if(standbySpinner.getSelectedItemPosition() != lcProeprtyModel.getStandByEditUnit()){
                        ApplicationParameters.LightLCPropertyValue lightLCPropertyValue = new ApplicationParameters.LightLCPropertyValue(Integer.parseInt(standbyEdit.getText().toString()));
                        mLightControlModeClient.setLightLCProperty(true,address,
                                standbyLightnessID,lightLCPropertyValue,
                                mLightLCPropertylSetStatusCallback);
                    }
                    setCounter++;
                    setPropertyStatus();
                    break;

                case MSG_STANDBY_LUXID_SET:
                    if(!(standbyEdit.getText().toString().equalsIgnoreCase(lcProeprtyModel.getStandByEditValue()))){
                        ApplicationParameters.LightLCPropertyValue lightLCPropertyValue = new ApplicationParameters.LightLCPropertyValue(Integer.parseInt(standbyEdit.getText().toString()));
                        mLightControlModeClient.setLightLCProperty(true,address,
                                standbyLuxID,lightLCPropertyValue,
                                mLightLCPropertylSetStatusCallback);
                    }
                    else if(standbySpinner.getSelectedItemPosition() != lcProeprtyModel.getStandByEditUnit()){
                        ApplicationParameters.LightLCPropertyValue lightLCPropertyValue = new ApplicationParameters.LightLCPropertyValue(Integer.parseInt(standbyEdit.getText().toString()));
                        mLightControlModeClient.setLightLCProperty(true,address,
                                standbyLuxID,lightLCPropertyValue,
                                mLightLCPropertylSetStatusCallback);
                    }
                    setCounter++;
                    setPropertyStatus();
                    break;

                case MSG_PROLONG_LIGHTNESSID_SET:
                    if(!(prolongEdit2.getText().toString().equalsIgnoreCase(lcProeprtyModel.getProlongEditValue()))){
                        ApplicationParameters.PropertyValue lightLCPropertyValue = new ApplicationParameters.PropertyValue(Integer.parseInt(prolongEdit2.getText().toString()));
                        mLightControlModeClient.setLightLCLightnessProperty(true,address,
                                prolongLightnessID,lightLCPropertyValue,
                                mLightLCPropertylSetStatusCallback);
                    }
                    else if(prolongSpinner.getSelectedItemPosition() != lcProeprtyModel.getProlongEditUnit()){
                        ApplicationParameters.LightLCPropertyValue lightLCPropertyValue = new ApplicationParameters.LightLCPropertyValue(Integer.parseInt(prolongEdit2.getText().toString()));
                        mLightControlModeClient.setLightLCProperty(true,address,
                                prolongLightnessID,lightLCPropertyValue,
                                mLightLCPropertylSetStatusCallback);
                    }
                    setCounter++;
                    setPropertyStatus();
                    break;

                case MSG_PROLONG_LUXID_SET:
                    if(!(prolongEdit2.getText().toString().equalsIgnoreCase(lcProeprtyModel.getProlongEditValue()))){
                        ApplicationParameters.LightLCPropertyValue lightLCPropertyValue = new ApplicationParameters.LightLCPropertyValue(Integer.parseInt(prolongEdit2.getText().toString()));
                        mLightControlModeClient.setLightLCProperty(true,address,
                                prolongLuxID,lightLCPropertyValue,
                                mLightLCPropertylSetStatusCallback);
                    }
                    else if(prolongSpinner.getSelectedItemPosition() != lcProeprtyModel.getProlongEditUnit()){
                        ApplicationParameters.LightLCPropertyValue lightLCPropertyValue = new ApplicationParameters.LightLCPropertyValue(Integer.parseInt(prolongEdit2.getText().toString()));
                        mLightControlModeClient.setLightLCProperty(true,address,
                                prolongLuxID,lightLCPropertyValue,
                                mLightLCPropertylSetStatusCallback);
                    }
                    setCounter++;
                    setPropertyStatus();
                    break;
            }
        }
    }

    public void showProvisioningPopup(Context context, int isShowing) {
        if (context != null) {
            if(isShowing == 1){
                if (mDialog == null) {
                    mDialog = new Dialog(context);
                    // mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    mDialog.setContentView(R.layout.progressbar_layout);
                    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                }
            }
            else if(isShowing == 2){
                mDialog.dismiss();
                mDialog = null;
            }
        }
    }
}