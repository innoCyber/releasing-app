package ptml.releasing.ui.configuration.adapter;

/**
 * Created by Cyberman on 3/12/2019.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import ptml.releasing.R;
import ptml.releasing.db.models.config.BaseConfig;

public class ConfigurationSpinnerAdapter extends ArrayAdapter<BaseConfig> {
    private List<? extends BaseConfig> list;

    public ConfigurationSpinnerAdapter(Context context, int id, List<? extends BaseConfig> list){
        super(context,id, (List<BaseConfig>) list);
        this.list=list;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent ){
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.spinner_configuration_layout, parent,false);
        }

        float scale = getContext().getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (2*scale + 0.5f);
        convertView.setPadding(dpAsPixels, dpAsPixels,dpAsPixels ,dpAsPixels);
        convertView.findViewById(R.id.img_drop).setVisibility(View.VISIBLE);
        TextView textView= convertView.findViewById(R.id.tv_category);

        textView.setText(list.get(position).getValue());
        return convertView;
    }


    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent){
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.spinner_configuration_layout, parent,false);
        }


        convertView.findViewById(R.id.img_drop).setVisibility(View.GONE);
        TextView textView= convertView.findViewById(R.id.tv_category);

        textView.setText(list.get(position).getValue());
        return convertView;

    }
}
