package com.example.irasphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AskActivity extends AppCompatActivity {
    EditText editText;
    Button button;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    DocumentReference userDocumentRef;
    QuestionMember member;
    String userName, userUrl, userPrivacy, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Gérer le cas où l'utilisateur n'est pas connecté
            return;
        }
        String currentUserId = currentUser.getUid();

        editText = findViewById(R.id.ask_et_question);
        button = findViewById(R.id.btn_submit);
        userDocumentRef = firestore.collection("users").document(currentUserId);
        member = new QuestionMember();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(question)) {
                    Calendar currentTime = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss");
                    String dateTime = dateFormat.format(currentTime.getTime());

                    member.setQuestion(question);
                    member.setName(userName);
                    member.setPrivacy(userPrivacy);
                    member.setUrl(userUrl);
                    member.setUserid(userId);
                    member.setTime(dateTime);

                    userDocumentRef.collection("User Questions").add(member)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AskActivity.this, "Question submitted", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AskActivity.this, "Failed to submit question", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(AskActivity.this, "Please enter a question", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        userDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    DocumentSnapshot document = task.getResult();
                    userName = document.getString("name");
                    userUrl = document.getString("url");
                    userPrivacy = document.getString("privacy");
                    userId = document.getId();
                } else {
                    Toast.makeText(AskActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
