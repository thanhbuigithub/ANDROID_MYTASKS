package com.example.mytasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAddListAdapter extends RecyclerView.Adapter<RecyclerViewAddListAdapter.ViewHolder>{
    private ArrayList<Integer> mSrcs = new ArrayList<>();
    private Context mContext;

    private int selectedPosition = -1;

    public RecyclerViewAddListAdapter(ArrayList<Integer> mSrcs, Context mContext) {
        this.mSrcs = mSrcs;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.imageView.setImageResource(mSrcs.get(position));

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = holder.getAdapterPosition();
                ListTaskActivity.iconPosition_addList= selectedPosition;
                ListTaskActivity.iconImageview.setImageResource(mSrcs.get(selectedPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSrcs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_icon);
        }
    }
}

