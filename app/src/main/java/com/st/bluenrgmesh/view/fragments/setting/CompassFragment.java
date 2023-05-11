package com.st.bluenrgmesh.view.fragments.setting;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.msi.moble.mobleAddress;
import com.st.bluenrgmesh.MainActivity;
import com.st.bluenrgmesh.R;
import com.st.bluenrgmesh.Shared;
import com.st.bluenrgmesh.UserApplication;
import com.st.bluenrgmesh.Utils;
import com.st.bluenrgmesh.datamap.Nucleo;
import com.st.bluenrgmesh.defaultAppCallback;
import com.st.bluenrgmesh.logger.LoggerConstants;
import com.st.bluenrgmesh.view.fragments.base.BaseFragment;
import com.st.bluenrgmesh.view.fragments.other.About;

import static android.content.Context.SENSOR_SERVICE;
import static com.st.bluenrgmesh.MainActivity.network;

public class CompassFragment extends BaseFragment implements SensorEventListener {

    // define the display assembly compass picture
    private ImageView image;

    // record the compass picture angle turned
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;

    TextView tvHeading;
    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compass, container, false);
        mContext = container.getContext();
        // our compass image
        Utils.updateActionBarForFeatures(getActivity(), new CompassFragment().getClass().getName());

        image = view.findViewById(R.id.imageViewCompass);

        // TextView that will tell the user what degree is he heading
        tvHeading = view.findViewById(R.id.tvHeading);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

        tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

        int degreeValue = Math.round(degree);
        if(degree > 85 && degreeValue < 95){
            sendVendorModelCommand();
        }

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        image.startAnimation(ra);
        currentDegree = -degree;

    }

    private void sendVendorModelCommand() {
        network.advise(mGroupReadCallback);
        network.getApplication().setRemoteData(mobleAddress.deviceAddress(Integer.parseInt(String.valueOf(2))),
                Nucleo.APPLI_CMD_LED_CONTROL, 1, new byte[]{Nucleo.APPLI_CMD_LED_ON} , true);
        ((MainActivity) mContext).mUserDataRepository.getNewDataFromRemote("Vendor OnOff command sent to ==> 0002", LoggerConstants.TYPE_SEND);
    }

    public static final defaultAppCallback mGroupReadCallback = new defaultAppCallback() {
        @Override
        public void onResponse(mobleAddress peer, Object cookies, byte status, byte[] data) {
            Log.i("OnResponseStatus", status + "");
            if (status == STATUS_SUCCESS) {
                Log.i("Response: From", String.valueOf(peer));
                Log.i("Response: cookies", String.valueOf(cookies));
                Log.i("Response: status", String.valueOf(status));
                Log.i("Response: Data", Utils.array2string(data));
            } else if (status == STATUS_FAIL_TIMEOUT) { }
        }
    };

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
}