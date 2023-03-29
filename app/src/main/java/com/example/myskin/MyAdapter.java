package com.example.myskin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<PredictionDetails> list;

    public MyAdapter(Context context, ArrayList<PredictionDetails> list) {
        this.context = context;
        this.list = list;
    }

    @androidx.annotation.NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull MyViewHolder holder, int position) {
            PredictionDetails pred=list.get(position);
            holder.name.setText(pred.getName());
            holder.symptoms.setText(pred.getSymptoms());
            holder.causes.setText(pred.getCauses());
            holder.treatment.setText(pred.getTreatment());
            holder.description.setText(pred.getDescription());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name,confidence,symptoms,causes,treatment,link,uid,description;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            name=itemView.findViewById(R.id.nameValue);
            description=itemView.findViewById(R.id.descriptionValue);
            symptoms=itemView.findViewById(R.id.SymptomsValue);
            treatment=itemView.findViewById(R.id.treamentValue);
            causes=itemView.findViewById(R.id.CausesValue);

        }
    }
}
