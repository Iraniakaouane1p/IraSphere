package com.example.irasphere;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostActivity extends AppCompatActivity {
    ImageView imageView;
    ProgressBar progressBar;
    private Uri selectedUri;
    private static final int PICK_FILE = 1;
    UploadTask uploadTask;
    EditText etdesc;
    Button btnchoosefile, btnuploadfile;
    VideoView videoView;
    String name;
    StorageReference storageReference;
TextUtils tv_likes;
    // Déclaration des références de base de données
    DatabaseReference db1, db2, db3;
    FirebaseDatabase database;
    MediaController mediaController;
    String type;
    Postmember postmember;
    String url; // Ajout de la variable url


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mediaController = new MediaController(this);
        progressBar = findViewById(R.id.pb_post);
        imageView = findViewById(R.id.iv_post);
        videoView = findViewById(R.id.vv_post);
        btnchoosefile = findViewById(R.id.btn_choosefile_post);
        btnuploadfile = findViewById(R.id.btn_uploadfile_post);
        etdesc = findViewById(R.id.et_desc_post);
        // Initialisation de la référence de base de données
        database = FirebaseDatabase.getInstance();


        storageReference = FirebaseStorage.getInstance().getReference("User posts"); // Initialisation de storageReference
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        db1 = database.getReference("All images").child(currentuid);
        db2 = database.getReference("All videos").child(currentuid);
        db3 = database.getReference("All posts");// Correction ici
        btnuploadfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dopost();
            }
        });
        btnchoosefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/*");
        startActivityForResult(intent, PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE || resultCode == RESULT_OK || data != null || data.getData() != null) {
            selectedUri = data.getData();
            if (selectedUri.toString().contains("image")) {
                Picasso.get().load(selectedUri).into(imageView);
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.INVISIBLE);
                type = "iv";
            } else if (selectedUri.toString().contains("video")) {
                videoView.setMediaController(mediaController);
                videoView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                videoView.setVideoURI(selectedUri);
                videoView.start();
                type = "vv";
            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private String getFileExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        String mimeType = contentResolver.getType(uri);
        if (mimeType != null) {
            return mimeType.substring(mimeType.lastIndexOf("/") + 1);
        }
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocumentRef = db.collection("user").document(currentuid);

        userDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    DocumentSnapshot document = task.getResult();
                    name = document.getString("name");
                    url = document.getString("url"); // Initialisez la variable url
                } else {
                    Toast.makeText(PostActivity.this, "erreur", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void Dopost() {
        String desc = etdesc.getText().toString(); // Correction ici
        if (!TextUtils.isEmpty(desc) && selectedUri != null) { // Correction ici
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child("posts/" + System.currentTimeMillis() + "." + getFileExt(selectedUri)); // Modification ici pour ajouter le dossier "posts"
            uploadTask = reference.putFile(selectedUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        // Faites quelque chose avec l'URI de téléchargement ici
                        // Par exemple, vous pouvez l'utiliser pour sauvegarder dans la base de données
                        if (type.equals("iv")) {
                            // Initialisation de postmember
                            postmember = new Postmember();
                            postmember.setDesc(desc);
                            postmember.setName(name);
                            postmember.setPostUri(downloadUri.toString());
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String currentuid = user.getUid(); // Déplacer la déclaration de currentuid ici pour qu'elle soit accessible
                            postmember.setUid(currentuid);
                            postmember.setUrl(url); // Utilisez la variable url initialisée
                            postmember.setType("iv");
                            String id = db1.push().getKey();
                            db1.child(id).setValue(postmember);
                            String id1 = db3.push().getKey();
                            db3.child(id1).setValue(postmember);
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PostActivity.this, "Post Uplaoded", Toast.LENGTH_SHORT).show();
                        } else if (type.equals("vv")) {
                            postmember = new Postmember();
                            postmember.setDesc(desc);
                            postmember.setName(name);
                            postmember.setPostUri(downloadUri.toString());
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String currentuid = user.getUid(); // Déplacer la déclaration de currentuid ici pour qu'elle soit accessible
                            postmember.setUid(currentuid);
                            postmember.setUrl(url); // Utilisez la variable url initialisée
                            postmember.setType("vv");
                            String id3 = db2.push().getKey();
                            db1.child(id3).setValue(postmember);
                            String id4 = db3.push().getKey();
                            db3.child(id4).setValue(postmember);
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PostActivity.this, "Post Uploaded", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PostActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(PostActivity.this, "Error uploading file", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(PostActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }

}
