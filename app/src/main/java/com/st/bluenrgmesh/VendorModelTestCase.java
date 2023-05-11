package com.st.bluenrgmesh;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.msi.moble.ApplicationParameters;
import com.msi.moble.GenericPowerOnOffModelClient;
import com.msi.moble.mobleAddress;
import com.msi.moble.mobleNetwork;
import com.st.bluenrgmesh.datamap.Nucleo;
import com.st.bluenrgmesh.logger.LoggerConstants;
import com.st.bluenrgmesh.utils.AllConstants;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VendorModelTestCase  {
    mobleNetwork network;
    mobleAddress nodeAddress;
    private static final String TAG_TEST ="adbTest" ;
    private final VendorModelTestCaseMEssage mHandler = new VendorModelTestCaseMEssage(this);

    public static Context contextMainActivity;
    private static int _start = 1;

    int mMessageCount = 0;
    int mMessageSizePosition = 0;
    byte [] mMessageByteArray;
    int mMessageDelay;
    String t1 = "";
    String t2 = "";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");




    public VendorModelTestCase(mobleNetwork network, mobleAddress nodeAddress) {
        this.network = network;
        this.nodeAddress = nodeAddress;
    }

    public void sendGenericPowerOnOffMessages(int messageSizePosition, int messageCount,int messageDelay,Context context) {
        this.mMessageSizePosition = messageSizePosition;
        this.mMessageCount = messageCount;
        this.mMessageDelay = messageDelay;
        this.contextMainActivity = context;

        Log.v(TAG_TEST,"Vendor Message count number : " + messageCount);
        Log.v(TAG_TEST,"Vendor Message size number : " + messageSizePosition);

        Log.v(TAG_TEST,"MoBLELibrary : " + "Messages Count ==> " + messageCount);
        Log.v(TAG_TEST,"MoBLELibrary : " + "Message Delay ==> " + messageDelay);


        if(mMessageSizePosition == 0){
            //filledDatainByteArray(8);
            mMessageByteArray = new byte[]{0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08};
        }
        else if(mMessageSizePosition == 1){
           // filledDatainByteArray(16);
            mMessageByteArray = new byte[]{0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0a,0x0b,0x0c,0x0d,0x0e,0x0f,0x10};
        }
        else if(mMessageSizePosition == 2){
            filledDatainByteArray(256);
          //  mMessageByteArray = new byte[]{0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08};
        }

        Message msg = new Message();

        mHandler.sendMessageDelayed(msg, 100);
    }

    private void filledDatainByteArray(int size) {
        byte[] localMessageArray = new byte[size];
        for(int i=0; i<size ; i++ ){
            localMessageArray[i] = (byte) (i+1);
        }
        mMessageByteArray = localMessageArray;
    }


    private void sendVendorValueByteArrayValue(byte[] bytes) {
        if(_start <= mMessageCount)
        {
           // ((MainActivity) contextMainActivity).mUserDataRepository.getNewDataFromRemote("Vendor Message count ==> " + _start, LoggerConstants.TYPE_SEND);
            int rem = _start%2;

            Log.v("MoBLELibrary","Sending Vendor Model Command application side 1 ==> " + _start);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        _start += 1;
                        sendVendorValueByteArrayValue(mMessageByteArray);
                    }},mMessageDelay);

            if(rem == 0){
                bytes[0]=(byte) 2;
                network.getApplication().setRemoteData(nodeAddress, Nucleo.APPLI_CMD_LED_CONTROL, 1, bytes, false);
            } else{
                bytes[0]=(byte) 1;
                network.getApplication().setRemoteData(nodeAddress, Nucleo.APPLI_CMD_LED_CONTROL, 1, bytes, false);
            }

        }
        else {
            _start =_start -1;
            ((MainActivity) contextMainActivity).mUserDataRepository.getNewDataFromRemote("Vendor Message completed count ==> " + _start, LoggerConstants.TYPE_SEND);
            Log.v(TAG_TEST,"Vendor Message completed count ==> " + String.valueOf(_start));
            _start = 1;
        }
    }

    private class VendorModelTestCaseMEssage extends Handler {
        private final WeakReference<VendorModelTestCase> mGonoff;
        VendorModelTestCaseMEssage(VendorModelTestCase activity) {
            super(Looper.getMainLooper());
            mGonoff = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final VendorModelTestCase messageSender = mGonoff.get();
            if (messageSender == null) return;
            //MainActivity.network.advise(Utils.mGroupReadCallback);
            sendVendorValueByteArrayValue(mMessageByteArray);

        }
    }
}
