package com.example.irasphere;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class Viewholder_Question extends RecyclerView.ViewHolder {
    TextView name_result, time_result, question_result;

    public Viewholder_Question(@NonNull View itemView) {
        super(itemView);
        name_result = itemView.findViewById(R.id.name_que_item_tv);
        time_result = itemView.findViewById(R.id.time_que_item_tv);
        question_result = itemView.findViewById(R.id.que_item_tv);
    }

    public void setitem(String name, String time, String question) {
        name_result.setText(name);
        time_result.setText(time);
        question_result.setText(question);
    }
}

