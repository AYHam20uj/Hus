package com.st.bluenrgmesh.adapter.models;

import android.content.Context;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.msi.moble.ApplicationParameters;
import com.st.bluenrgmesh.R;
import com.st.bluenrgmesh.models.sensor.SensorData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class LightLCModelAdapter extends RecyclerView.Adapter<LightLCModelAdapter.ViewHolder>{

    private Context context;
    private LightLCModelAdapter.IRecyclerViewHolderClicks listener;
    private List<Boolean> isStateOnOFf;

    public LightLCModelAdapter(Context context, List<Boolean> isStateOnOff, LightLCModelAdapter.IRecyclerViewHolderClicks listener) {

        this.context = context;
        this.isStateOnOFf = isStateOnOff;
        this.listener = listener;
    }

    public interface IRecyclerViewHolderClicks {
        void onClickRecyclerSwitch(int position, boolean isChecked);
        void onClickRecyclerRefresh(int position);
    }

    @Override
    public LightLCModelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.light_lc_row_list, parent, false);
        LightLCModelAdapter.ViewHolder vh = new LightLCModelAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(LightLCModelAdapter.ViewHolder holder, final int position) {
        if(position == 0){
            holder.modelAPINameText.setText("Light LC OnOff");
        }
        else if(position == 1){
            holder.modelAPINameText.setText("Light LC OM");
        }
        else if(position == 2){
            holder.modelAPINameText.setText("Light LC Mode");
        }
        else if(position == 3){
            holder.modelAPINameText.setText("Light LC Property");
            holder.refreshStateImage.setVisibility(View.GONE);
        }

        holder.onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listener.onClickRecyclerSwitch(position,isChecked);
            }
        });

        holder.refreshStateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickRecyclerRefresh(position);
            }
        });

        holder.onOffSwitch.setChecked(isStateOnOFf.get(position));
    }

    @Override
    public int getItemCount() {
        return isStateOnOFf.size();
    }

    public void setValues(List<Boolean> isStateOnOff){
        this.isStateOnOFf = isStateOnOff;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView refreshStateImage;
        private TextView modelAPINameText;
        private Switch onOffSwitch;

        public ViewHolder(View itemView) {
            super(itemView);

            onOffSwitch = (Switch) itemView.findViewById(R.id.onOffSwitch);
            refreshStateImage = (ImageView) itemView.findViewById(R.id.refreshImage);
            modelAPINameText = (TextView) itemView.findViewById(R.id.apiNameText);
        }
    }
}
