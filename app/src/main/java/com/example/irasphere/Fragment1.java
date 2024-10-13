package com.example.irasphere;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class Fragment1 extends Fragment implements View.OnClickListener {
    ImageView imageView;
    TextView nameEt, profEt, bioEt, emailEt, webEt;
    ImageButton ib_edit, imageButtonMenu;
    DocumentReference reference;
    FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);
        imageView = view.findViewById(R.id.iv_f1);
        nameEt = view.findViewById(R.id.tv_name_f1);
        bioEt = view.findViewById(R.id.tv_bio_f1);
        emailEt = view.findViewById(R.id.tv_email_f1);
        webEt = view.findViewById(R.id.tv_website_f1);
        profEt = view.findViewById(R.id.tv_prof_f1);

        ib_edit = view.findViewById(R.id.ib_edit_f1);
        ib_edit.setOnClickListener(this); // Ajout d'un écouteur de clic au bouton "Edit"

        imageButtonMenu = view.findViewById(R.id.ib_menu_f1);
        imageButtonMenu.setOnClickListener(this);
        imageView.setOnClickListener(this);
        webEt.setOnClickListener(this);
        // Ajout d'un écouteur de clic au bouton de menu

        return view;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.ib_edit_f1) {
            // Rediriger vers l'activité de mise à jour du profil lors du clic sur le bouton "Edit"
            Intent intent = new Intent(getActivity(), UpadateProfile.class);
            startActivity(intent);
        } else if (viewId == R.id.ib_menu_f1) {
            // Afficher le menu lors du clic sur le bouton de menu
            BottomSheetMenu bottomSheetMenu = new BottomSheetMenu();
            bottomSheetMenu.show(getFragmentManager(), "bottomsheet");
        }else if (viewId == R.id.iv_f1) {
            Intent intent1=new Intent(getActivity(),ImageActivity.class);
            startActivity(intent1);

        }else if (viewId == R.id.tv_website_f1) {
            try {
                String url=webEt.getText().toString();
                Intent intent2=new Intent(Intent.ACTION_VIEW);
                intent2.setData(Uri.parse(url));
                startActivity(intent2);

            }catch (Exception e){
                Toast.makeText(getActivity(),"Invalide Url",Toast.LENGTH_SHORT).show();

            }


        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String currentid = user.getUid();
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            reference = firestore.collection("user").document(currentid);
            reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String nameResult = document.getString("name");
                            String bioResult = document.getString("bio");
                            String emailResult = document.getString("email");
                            String webResult = document.getString("web");
                            String url = document.getString("url");
                            String profResult = document.getString("prof");
                            Picasso.get().load(url).into(imageView);
                            nameEt.setText(nameResult);
                            bioEt.setText(bioResult);
                            emailEt.setText(emailResult);
                            webEt.setText(webResult);
                            profEt.setText(profResult);
                        } else {
                            Intent intent = new Intent(getActivity(), CreateProfile.class);
                            startActivity(intent);
                        }
                    }
                }
            });
        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    }
}
