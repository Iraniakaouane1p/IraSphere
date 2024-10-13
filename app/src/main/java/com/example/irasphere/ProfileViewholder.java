package com.example.irasphere;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class ProfileViewholder extends RecyclerView.ViewHolder {
    TextView textViewName, textViewProfession, viewUserprofile,sendmessagebtn;
    ImageView imageView;
    CardView cardView;

    public ProfileViewholder(@NonNull View itemView) {
        super(itemView);
    }

    public void setProfile(Application fragmentActivity, String name, String uid, String prof, String url) {
        cardView = itemView.findViewById(R.id.card_view_profile); // Assurez-vous que cet ID est correct
        textViewName = itemView.findViewById(R.id.tv_name_f1);
        textViewProfession = itemView.findViewById(R.id.tv_prof_f1);
       // viewUserprofile=itemView.findViewById(R.id.);
        imageView = itemView.findViewById(R.id.iv_f1);
        Picasso.get().load(url).into(imageView);
        textViewProfession.setText(prof);
        textViewName.setText(name);
    }


   public void setProfileInchat(Application application, String name, String uid, String prof) {
       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       String userid = user.getUid();
       ImageView imageView = itemView.findViewById(R.id.iv_ch_item);
       TextView nametv = itemView.findViewById(R.id.name_ch_item_tv);
       TextView proftv = itemView.findViewById(R.id.ch_itemprof_tv);
       sendmessagebtn = itemView.findViewById(R.id.send_messagech_item_btn);
        /*if (userid.equals(uid)) {
            Picasso.get().load(url).into(imageView);
            nametv.setText(name);
            proftv.setText(prof);
        }else {
                Picasso.get().load(url).into(imageView);
                nametv.setText(name);
                proftv.setText(prof);
        }*/

   }
}
