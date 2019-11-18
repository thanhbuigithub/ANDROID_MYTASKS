package com.example.mytasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewChangeThemeAdapter extends RecyclerView.Adapter<RecyclerViewChangeThemeAdapter.ViewHolder>{
    private ArrayList<String> mColors = new ArrayList<>();
    private ArrayList<Integer> ThemesUrls = new ArrayList<>();
    private Integer mThemePicked;
    private Context mContext;

    private int selectedPosition = -1;

    public RecyclerViewChangeThemeAdapter(ArrayList<String> mColors, ArrayList<Integer> ThemesUrls, Context mContext, Integer mThemePicked) {
        this.mColors = mColors;
        this.ThemesUrls = ThemesUrls;
        this.mContext = mContext;
        this.mThemePicked = mThemePicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.change_theme_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        if(selectedPosition == position){
            holder.btnImage.setChecked(true);
        }
        else{
            holder.btnImage.setChecked(false);
        }

        holder.btnImage.setButtonDrawable(R.drawable.custom_checkbox_isimportant);

        holder.color.setText(mColors.get(position));

        holder.btnImage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(holder.btnImage.isChecked()) {
                    selectedPosition = holder.getAdapterPosition();
                    mThemePicked = ThemesUrls.get(selectedPosition);
                    Toast.makeText(mContext, "Pick theme " + mColors.get(selectedPosition), Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.btnImage.setOnClickListener(onStateChangedListener(holder.btnImage, position));
    }

    @Override
    public int getItemCount() {
        return mColors.size();
    }

    private AdapterView.OnClickListener onStateChangedListener(final CheckBox checkBox, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    selectedPosition = position;
                } else {
                    selectedPosition = -1;
                }
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CheckBox btnImage;
        TextView color;

        public ViewHolder(View itemView) {
            super(itemView);
            btnImage = itemView.findViewById(R.id.btnImage);
            color = itemView.findViewById(R.id.color);
        }
    }
}

