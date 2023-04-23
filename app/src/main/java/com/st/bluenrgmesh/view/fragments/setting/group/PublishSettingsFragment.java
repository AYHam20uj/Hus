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

import com.msi.moble.ApplicationParameters;
import com.st.bluenrgmesh.MainActivity;
import com.st.bluenrgmesh.R;
import com.st.bluenrgmesh.Utils;
import com.st.bluenrgmesh.models.meshdata.MeshRootClass;
import com.st.bluenrgmesh.models.meshdata.Retransmit;
import com.st.bluenrgmesh.models.newmeshdata.MeshRoot;
import com.st.bluenrgmesh.parser.ParseManager;
import com.st.bluenrgmesh.view.fragments.base.BaseFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class PublishSettingsFragment extends BaseFragment {

    Context context;
    EditText retransmitCount, retransmitStep, publishTTl, publishRes, publishPeriodSteps;
    Button done;
    private MeshRootClass meshRootClass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_publish_settings, container, false);
        context = container.getContext();
        Utils.updateActionBarForFeatures(getActivity(), new PublishSettingsFragment().getClass().getName());
        initUi(view);
        checkForDefaultValues();
        try {
            meshRootClass = (MeshRootClass) ParseManager.getInstance().fromJSON(new JSONObject(Utils.getBLEMeshDataFromLocal(context)), MeshRootClass.class).clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void checkForDefaultValues() {
        MeshRootClass meshRootClass = null;
        try {
            meshRootClass = ParseManager.getInstance().fromJSON(new JSONObject(Utils.getBLEMeshDataFromLocal(context)), MeshRootClass.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MeshRootClass meshRootClassTemp = null;

        try {
            meshRootClassTemp = (MeshRootClass) ParseManager.getInstance().fromJSON(new JSONObject(Utils.getBLEMeshDataFromLocal(Utils.contextMainActivity)), MeshRootClass.class).clone();
            if(meshRootClassTemp!=null) {
                for (int j = 0; j < meshRootClassTemp.getNodes().size(); j++) {
                    for (int i = 0; i < meshRootClassTemp.getNodes().get(j).getElements().size(); i++) {
                        if (Integer.parseInt(meshRootClassTemp.getNodes().get(j).getElements().get(i).getUnicastAddress()) == Integer.parseInt(Utils.getModelParentAddressValue(context))) {
                            if (meshRootClassTemp.getNodes().get(j).getElements().get(i).getModels() != null) {
                                for (int k = 0; k < meshRootClassTemp.getNodes().get(j).getElements().get(i).getModels().size(); k++) {
                                    if (meshRootClassTemp.getNodes().get(j).getElements().get(i).getModels().get(k).getModelName().equalsIgnoreCase(Utils.getModelNameValue(context))) {

                                        if (meshRootClassTemp.getNodes().get(j).getElements().get(i).getModels().get(k).getPublish().getRetransmitCount() > 0) {
                                            retransmitCount.setText(String.valueOf(meshRootClassTemp.getNodes().get(j).getElements().get(i).getModels().get(k).getPublish().getRetransmitCount()));
                                        }

                                        if (meshRootClassTemp.getNodes().get(j).getElements().get(i).getModels().get(k).getPublish().getRetransmitSteps() > 0) {
                                            retransmitStep.setText(String.valueOf(meshRootClassTemp.getNodes().get(j).getElements().get(i).getModels().get(k).getPublish().getRetransmitSteps()));
                                        }

                                        if (meshRootClassTemp.getNodes().get(j).getElements().get(i).getModels().get(k).getPublish().getTtl() > 0) {
                                            publishTTl.setText(String.valueOf(meshRootClassTemp.getNodes().get(j).getElements().get(i).getModels().get(k).getPublish().getTtl()));
                                        }

                                        if (meshRootClassTemp.getNodes().get(j).getElements().get(i).getModels().get(k).getPublish().getPublishPeriodSteps() > 0) {
                                            publishPeriodSteps.setText(String.valueOf(meshRootClassTemp.getNodes().get(j).getElements().get(i).getModels().get(k).getPublish().getPublishPeriodSteps()));
                                        }

                                        if (meshRootClassTemp.getNodes().get(j).getElements().get(i).getModels().get(k).getPublish().getPublishPeriodRes() > 0) {
                                            publishRes.setText(String.valueOf(meshRootClassTemp.getNodes().get(j).getElements().get(i).getModels().get(k).getPublish().getPublishPeriodRes()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setValuesInJson(int value, String modelParam){
        try {
            if(meshRootClass!=null) {
                for (int j = 0; j < meshRootClass.getNodes().size(); j++) {
                        for (int i = 0; i < meshRootClass.getNodes().get(j).getElements().size(); i++) {
                            if (Integer.parseInt(meshRootClass.getNodes().get(j).getElements().get(i).getUnicastAddress()) == Integer.parseInt(Utils.getModelParentAddressValue(context))) {
                                if (meshRootClass.getNodes().get(j).getElements().get(i).getModels() != null) {
                                for (int k = 0; k < meshRootClass.getNodes().get(j).getElements().get(i).getModels().size(); k++) {
                                    if (meshRootClass.getNodes().get(j).getElements().get(i).getModels().get(k).getModelName().equalsIgnoreCase(Utils.getModelNameValue(context))) {
                                        if (modelParam.equalsIgnoreCase("retransmit Count")) {
                                            meshRootClass.getNodes().get(j).getElements().get(i).getModels().get(k).getPublish().setRetransmitCount(value);
                                        } else if (modelParam.equalsIgnoreCase("retransmit Steps")) {
                                            meshRootClass.getNodes().get(j).getElements().get(i).getModels().get(k).getPublish().setRetransmitSteps(value);
                                        } else if (modelParam.equalsIgnoreCase("publish TTL")) {
                                            meshRootClass.getNodes().get(j).getElements().get(i).getModels().get(k).getPublish().setTtl(value);
                                        } else if (modelParam.equalsIgnoreCase("publish Res")) {
                                            meshRootClass.getNodes().get(j).getElements().get(i).getModels().get(k).getPublish().setPublishPeriodRes(value);
                                        } else if (modelParam.equalsIgnoreCase("publish Period")) {
                                            meshRootClass.getNodes().get(j).getElements().get(i).getModels().get(k).getPublish().setPublishPeriodSteps(value);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUi(View view) {
        Utils.setSettingType(getActivity(), getString(R.string.str_publish_setting));
        done = view.findViewById(R.id.doneBtn);
        done.setVisibility(View.GONE);
        retransmitCount = view.findViewById(R.id.retransmitCountEdit);
        retransmitStep = view.findViewById(R.id.retransmitStepEdit);
        publishTTl = view.findViewById(R.id.ttlEdit);
        publishRes = view.findViewById(R.id.periodResEdit);
        publishPeriodSteps = view.findViewById(R.id.periodStepsEdit);

      /*  done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!(retransmitCount.getText().toString().equals(""))){
                    if(Integer.valueOf(retransmitCount.getText().toString()) <  32){
                        setValuesInJson(Integer.parseInt(retransmitCount.getText().toString()), "retransmit Count");
                    }
                }

                if(!(retransmitStep.getText().toString().equals(""))){
                    if(Integer.valueOf(retransmitStep.getText().toString()) <  8){
                        setValuesInJson(Integer.parseInt(retransmitStep.getText().toString()), "retransmit Steps");
                    }
                }

                if(!(publishTTl.getText().toString().equals(""))){
                    if(Integer.valueOf(publishTTl.getText().toString()) <  128){
                        setValuesInJson(Integer.parseInt(publishTTl.getText().toString()), "publish TTL");
                    }
                }

                if(!(publishRes.getText().toString().equals(""))){
                    if(Integer.valueOf(publishRes.getText().toString()) <  4){
                        setValuesInJson(Integer.parseInt(publishRes.getText().toString()), "publish Res");
                    }
                }

                if(!(publishPeriodSteps.getText().toString().equals(""))){
                    if(Integer.valueOf(publishPeriodSteps.getText().toString()) <  64){
                        setValuesInJson(Integer.parseInt(publishPeriodSteps.getText().toString()), "publish Period");
                    }
                }

                if(((retransmitStep.getText().toString().equals(""))) && ((retransmitCount.getText().toString().equals(""))) &&
                        ((publishTTl.getText().toString().equals(""))) && ((publishRes.getText().toString().equals("")))
                && ((publishPeriodSteps.getText().toString().equals("")))){
                    Toast.makeText(context,"No values entered",Toast.LENGTH_SHORT).show();
                }
                else {
                    Utils.setBLEMeshDataToLocal(context, ParseManager.getInstance().toJSON(meshRootClass));
                    ((MainActivity)context).updateJsonData();

                    ((MainActivity) getActivity()).onBackPressed();
                    Utils.controlNavigationDrawer(getActivity(), new PublishSettingsFragment().getClass().getName(), DrawerLayout.LOCK_MODE_UNLOCKED);
                }
            }
        });*/
    }

    public void saveOnClickListenerEvent(){
        if(!(retransmitCount.getText().toString().equals(""))){
            if(Integer.valueOf(retransmitCount.getText().toString()) <  32){
                setValuesInJson(Integer.parseInt(retransmitCount.getText().toString()), "retransmit Count");
            }
        }

        if(!(retransmitStep.getText().toString().equals(""))){
            if(Integer.valueOf(retransmitStep.getText().toString()) <  8){
                setValuesInJson(Integer.parseInt(retransmitStep.getText().toString()), "retransmit Steps");
            }
        }

        if(!(publishTTl.getText().toString().equals(""))){
            if(Integer.valueOf(publishTTl.getText().toString()) <  128){
                setValuesInJson(Integer.parseInt(publishTTl.getText().toString()), "publish TTL");
            }
        }

        if(!(publishRes.getText().toString().equals(""))){
            if(Integer.valueOf(publishRes.getText().toString()) <  4){
                setValuesInJson(Integer.parseInt(publishRes.getText().toString()), "publish Res");
            }
        }

        if(!(publishPeriodSteps.getText().toString().equals(""))){
            if(Integer.valueOf(publishPeriodSteps.getText().toString()) <  64){
                setValuesInJson(Integer.parseInt(publishPeriodSteps.getText().toString()), "publish Period");
            }
        }

        if(((retransmitStep.getText().toString().equals(""))) && ((retransmitCount.getText().toString().equals(""))) &&
                ((publishTTl.getText().toString().equals(""))) && ((publishRes.getText().toString().equals("")))
                && ((publishPeriodSteps.getText().toString().equals("")))){
            Toast.makeText(context,"No values entered",Toast.LENGTH_SHORT).show();
        }
        else {
            Utils.setBLEMeshDataToLocal(context, ParseManager.getInstance().toJSON(meshRootClass));
            ((MainActivity)context).updateJsonData();
            ((MainActivity) getActivity()).onBackPressed();
            Toast.makeText(context,"Publish Values saved",Toast.LENGTH_SHORT).show();
            Utils.controlNavigationDrawer(getActivity(), new PublishSettingsFragment().getClass().getName(), DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }
}