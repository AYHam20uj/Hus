/**
 * *****************************************************************************
 *
 * @file NodeSettingFragment.java
 * @author BLE Mesh Team
 * @version V1.11.000
 * @date    20-October-2019
 * @brief User Application file
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
 * BlueNRG-Mesh is based on Motorola’s Mesh Over Bluetooth Low Energy (MoBLE)
 * technology. STMicroelectronics has done suitable updates in the firmware
 * and Android Mesh layers suitably.
 * <p>
 * *****************************************************************************
 */

package com.st.bluenrgmesh.view.fragments.setting.node;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.msi.moble.ApplicationParameters;
import com.msi.moble.ConfigurationModelClient;
import com.msi.moble.GenericOnOffModelClient;
import com.msi.moble.LightLightnessModelClient;
import com.msi.moble.mobleAddress;
import com.msi.moble.mobleNetwork;
import com.st.bluenrgmesh.MainActivity;
import com.st.bluenrgmesh.R;
import com.st.bluenrgmesh.UserApplication;
import com.st.bluenrgmesh.Utils;
import com.st.bluenrgmesh.datamap.Nucleo;
import com.st.bluenrgmesh.logger.LoggerConstants;
import com.st.bluenrgmesh.models.meshdata.settings.NodeSettings;
import com.st.bluenrgmesh.utils.AppDialogLoader;
import com.st.bluenrgmesh.view.fragments.base.BaseFragment;
import com.st.bluenrgmesh.view.fragments.other.meshmodels.health.HealthConfigFragment;
import com.st.bluenrgmesh.view.fragments.other.meshmodels.heartbeat.HeartBeatConfigFragment;

import java.nio.ByteBuffer;

public class NodeSettingFragment extends BaseFragment {

    private View view;
    private AppDialogLoader loader;
    private RelativeLayout lytNodeFeatures;
    private RelativeLayout lytHeartBeatConfig;
    private RelativeLayout lytHealthConfig;
    private NodeSettings nodeSettings;
    private EditText edtName;
    private TextView txtUUID;
    private Button butRemoveNode;
    private mobleAddress peerAddress;
    private RelativeLayout lytNodeInformation;
    private Context context;

    @Override
    public void onAttach(Context context) {
        this.context=context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_nodesetting, container, false);

        loader = AppDialogLoader.getLoader(getActivity());
        Utils.updateActionBarForFeatures(getActivity(), new NodeSettingFragment().getClass().getName());
        initUi();
        
        return view;
    }

    private void initUi() {

        Utils.setSettingType(getActivity(), getString(R.string.str_nodessettings_label));
        nodeSettings = (NodeSettings) getArguments().getSerializable(getString(R.string.key_serializable));
        //peerAddress = mobleAddress.deviceAddress(Integer.parseInt(nodeSettings.getNodesUnicastAddress(), 16));
        peerAddress = mobleAddress.deviceAddress(Integer.parseInt(nodeSettings.getNodesUnicastAddress()));
        Utils.DEBUG(nodeSettings.getNodesName() + " " + nodeSettings.getNodesUnicastAddress() + " >>>>  "+ nodeSettings.getNodesMacAddress());

        lytNodeInformation = (RelativeLayout) view.findViewById(R.id.lytNodeInformation);
        lytNodeFeatures = (RelativeLayout) view.findViewById(R.id.lytNodeFeatures);
        lytHeartBeatConfig = (RelativeLayout) view.findViewById(R.id.lytHeartBeatConfig);
        lytHealthConfig = (RelativeLayout) view.findViewById(R.id.lytHealthConfig);
        butRemoveNode = (Button) view.findViewById(R.id.butRemoveNode);

        edtName = (EditText) view.findViewById(R.id.edtName);
        txtUUID = (TextView) view.findViewById(R.id.txtUUID);
        edtName.setText(nodeSettings.getNodesName());
        txtUUID.setText(nodeSettings.getUuid());

        lytNodeInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.moveToFragment(getActivity(), new NodeInfoFragment(), nodeSettings, 0);
            }
        });

        lytNodeFeatures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //throw new RuntimeException("Test Crash"); // Force a crash
                //settingATempMethod();
                Utils.moveToFragment(getActivity(), new NodeFeaturesFragment(), nodeSettings, 0);
            }
        });

        lytHeartBeatConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.moveToFragment(getActivity(), new HeartBeatConfigFragment(), nodeSettings, 0);
               // Utils.showToast(getActivity(), "Updated soon in next release.");
            }
        });

        lytHealthConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.moveToFragment(getActivity(), new HealthConfigFragment(), nodeSettings, 0);
               // Utils.showToast(getActivity(), "Updated soon in next release.");
            }
        });

        butRemoveNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //button used for addition of group and removing of node and group
                removeNodePopUp();
            }
        });
       /* lytHeartBeatConfig.setClickable(false);
        lytHealthConfig.setClickable(false);*/
    }


    private void removeNodePopUp() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage("Are you sure you want to remove the node?");
        builder1.setCancelable(true);

        builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (/*!g_model*/false) {
                    ((UserApplication) getActivity().getApplication()).mConfiguration.getNetwork().getApplication().setRemoteData(peerAddress, Nucleo.UNCONFIGURE, 1, new byte[]{}, false);

                } else {
                    UserApplication.trace("NODE unprovisioning from AddGroup _ 1 addr " + peerAddress);
                    loader.show();
                    try {
                        MainActivity.network.getConfigurationModelClient().resetNode(new ApplicationParameters.Address(peerAddress.mValue), mNodeResetCallback);
                    }catch (Exception e){}
                }

                ((UserApplication) getActivity().getApplication()).save();
                dialog.cancel();
            }
        });

        builder1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alert11.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);
                alert11.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            }
        });
        alert11.show();

        TextView messageText = (TextView) alert11.findViewById(android.R.id.message);
        messageText.setTypeface(Typeface.DEFAULT_BOLD);

        final Button positiveButton = alert11.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveButtonLL.weight = 10;
        positiveButton.setLayoutParams(positiveButtonLL);

        final Button negativeButton = alert11.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams negativeButtonLL = (LinearLayout.LayoutParams) negativeButton.getLayoutParams();
        negativeButtonLL.weight = 10;
        negativeButton.setLayoutParams(negativeButtonLL);
    }

    public ConfigurationModelClient.NodeResetStatusCallback mNodeResetCallback = new ConfigurationModelClient.NodeResetStatusCallback() {
        @Override
        public void onNodeResetStatus(boolean Timeoutstatus) {

            if (Timeoutstatus == true) {
                loader.hide();
                showForcefullUnprovisionPopup();
            } else {
                unprovisionNodeFromGUI("Unprovisioned Done Successfully");
            }
        }
    };

    private void unprovisionNodeFromGUI(String msg) {
        Utils.saveModelInfo(context, null);
        Utils.setNodeFeatures(context, null);
        Utils.removeProvisionNodeFromJson(getActivity(), nodeSettings.getNodesUnicastAddress());

        UserApplication.trace(msg);
        Utils.showToast(getActivity(), msg);
        //((MainActivity)getActivity()).adviseCallbacks();
        ((MainActivity)getActivity()).clearUnprovisionList();
        ((MainActivity)getActivity()).updateProvisionedTab(nodeSettings.getUuid(), getResources().getInteger(R.integer.PROVISIONED_NODE_REMOVED));
        ((MainActivity) getActivity()).updateModelTab();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loader.hide();
                ((MainActivity)getActivity()).onBackPressed();
            }
        });
    }

    private void showForcefullUnprovisionPopup() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

        TextView title = new TextView(context);
        title.setText("ST BLE Mesh");
        title.setGravity(Gravity.CENTER);
        title.setPadding(10, 30, 10, 20);
        title.setTextColor(getResources().getColor(R.color.st_black));
        title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        title.setTextSize(17);

        TextView msg = new TextView(context);
        msg.setText("Node not connected, do you want to force delete?");
        msg.setGravity(Gravity.CENTER);
        msg.setPadding(24, 10, 24, 10);
        msg.setTextColor(getResources().getColor(R.color.st_black));
        msg.setTextSize(15);


        builder1.setCustomTitle(title);
        builder1.setView(msg);
        builder1.setCancelable(false);

        builder1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                unprovisionNodeFromGUI("Node Force deleted");
                dialog.cancel();
            }
        });

        builder1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                UserApplication.trace("Node unprovisioned failed");
                Utils.showToast(getActivity(), "Node unpairing stopped.");
                dialog.cancel();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alert11.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getColor(R.color.ST_primary_blue));
                alert11.getButton(AlertDialog.BUTTON_NEGATIVE).setPadding(10,0,10,10);

                alert11.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getColor(R.color.ST_primary_blue));
                alert11.getButton(AlertDialog.BUTTON_POSITIVE).setPadding(10,0,10,10);
            }
        });
        alert11.show();

        final Button positiveButton = alert11.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveButtonLL.weight = 10;
        positiveButton.setLayoutParams(positiveButtonLL);

        final Button negativeButton = alert11.getButton(AlertDialog.BUTTON_NEGATIVE);
        LinearLayout.LayoutParams negativeButtonLL = (LinearLayout.LayoutParams) negativeButton.getLayoutParams();
        negativeButtonLL.weight = 10;
        negativeButton.setLayoutParams(negativeButtonLL);
    }


    public NodeSettings getNodeData(){
        if(edtName.getText().toString().isEmpty()){
            nodeSettings.setNodesName(nodeSettings.getNodesName());
        }
        else{
            nodeSettings.setNodesName(edtName.getText().toString());
        }
        return nodeSettings;
    }
}
