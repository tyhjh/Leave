package com.yorhp.leave;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tyhj on 2017/8/17.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.Holder>{

    ArrayList<String> arrayList;
    Context context;
    LayoutInflater inflater;
    SetMsg setMsg;

    public HistoryAdapter(ArrayList<String> arrayList, Context context,SetMsg msg) {
        this.arrayList = arrayList;
        this.context = context;
        inflater=LayoutInflater.from(context);
        this.setMsg=msg;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.item,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final String msg=arrayList.get(holder.getPosition());
        holder.txt.setText(msg+"");
        holder.txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMsg.setMsg(msg);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        TextView txt;
        public Holder(View itemView) {
            super(itemView);
            txt= (TextView) itemView.findViewById(R.id.txt);
        }
    }

    public interface SetMsg{
        void setMsg(String msg);
    }
}
