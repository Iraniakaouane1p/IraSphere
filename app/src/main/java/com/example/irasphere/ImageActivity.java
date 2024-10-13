package com.example.irasphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {
    ImageView imageView;
    TextView textView;
    Button btnEdit,btnDel;
    DocumentReference reference;
    String url;
    FirebaseFirestore db=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        btnDel=findViewById(R.id.btn_del_iv);
        btnEdit=findViewById(R.id.btn_edit_iv);
        textView=findViewById(R.id.tv_name_image);
        imageView = findViewById(R.id.iv_expand); // Initialize imageView
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String currentid =user.getUid();
        reference=db.collection("user").document(currentid);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle edit button click
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle delete button click
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name = document.getString("name");
                        url = document.getString("url");
                        if (imageView != null && url != null) {
                            Picasso.get().load(url).into(imageView);
                            textView.setText(name);
                        } else {
                            Toast.makeText(ImageActivity.this, "ImageView or URL is null", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ImageActivity.this, "No Profile", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ImageActivity.this, "Failed to retrieve profile: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
