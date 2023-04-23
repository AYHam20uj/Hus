package com.st.bluenrgmesh.view.fragments.setting.group;

import android.content.Context;
import android.os.Bundle;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.st.bluenrgmesh.MainActivity;
import com.st.bluenrgmesh.R;
import com.st.bluenrgmesh.Utils;
import com.st.bluenrgmesh.view.fragments.base.BaseFragment;
import com.st.bluenrgmesh.view.fragments.setting.managekey.KeyManagementFragment;

public class SubscribeSettingsFragment extends BaseFragment {

    Button done;
    EditText modelId;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subscribe_settings, container, false);
        context = container.getContext();
        Utils.updateActionBarForFeatures(getActivity(), new SubscribeSettingsFragment().getClass().getName());
        initUi(view);
        return view;

    }

    private void initUi(View view) {
        done = view.findViewById(R.id.doneBtn);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(modelId.getText().toString().equals(""))){
                    Utils.setSubscribeModelID(context,modelId.getText().toString());
                    Toast.makeText(context,"Subscriber Model ID saved",Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).onBackPressed();
                    Utils.controlNavigationDrawer(getActivity(), new SubscribeSettingsFragment().getClass().getName(), DrawerLayout.LOCK_MODE_UNLOCKED);
                }
                else{
                    Toast.makeText(context,"No Model ID entered",Toast.LENGTH_SHORT).show();
                }
            }
        });

        modelId = view.findViewById(R.id.modelEdit);
    }
}