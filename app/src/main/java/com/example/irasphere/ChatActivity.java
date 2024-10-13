package com.example.irasphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ChatActivity extends AppCompatActivity {
    DatabaseReference profileRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    RecyclerView recyclerView;
    EditText searchEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        searchEt = findViewById(R.id.saerch_user);
        recyclerView = findViewById(R.id.rv_ch);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        profileRef = database.getReference("All Users");

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Rien à faire ici
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Rien à faire ici
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Ajouter la logique de recherche ici si nécessaire
                String query=searchEt.getText().toString().toLowerCase();
                Query search=profileRef.orderByChild("name").startAt(query).endAt(query+"\uf0ff");
                FirebaseRecyclerOptions<All_UserMmber> options1 = new FirebaseRecyclerOptions.Builder<All_UserMmber>()
                        .setQuery(profileRef, All_UserMmber.class)
                        .build();

                FirebaseRecyclerAdapter<All_UserMmber, ProfileViewholder> firebaseRecyclerAdapter1 =
                        new FirebaseRecyclerAdapter<All_UserMmber, ProfileViewholder>(options1) {
                            @Override
                            protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull All_UserMmber model) {
                                final String postKey = getRef(position).getKey();

                                holder.setProfileInchat(getApplication(), model.getName(), model.getUid(), model.getProf());

                                final String name = model.getName();
                                final String url = model.getUrl();
                                final String uid = model.getUid();

                                holder.sendmessagebtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(ChatActivity.this,MessageActivity.class);
                                        intent.putExtra("name", name);
                                        intent.putExtra("url", url);
                                        intent.putExtra("uid", uid);
                                        startActivity(intent);
                                    }
                                });
                            }

                            @NonNull
                            @Override
                            public ProfileViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.chat_profile_item, parent, false);
                                return new ProfileViewholder(view);
                            }
                        };

                firebaseRecyclerAdapter1.startListening();
                recyclerView.setAdapter(firebaseRecyclerAdapter1);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<All_UserMmber> options1 = new FirebaseRecyclerOptions.Builder<All_UserMmber>()
                .setQuery(profileRef, All_UserMmber.class)
                .build();

        FirebaseRecyclerAdapter<All_UserMmber, ProfileViewholder> firebaseRecyclerAdapter1 =
                new FirebaseRecyclerAdapter<All_UserMmber, ProfileViewholder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewholder holder, int position, @NonNull All_UserMmber model) {
                        final String postKey = getRef(position).getKey();

                        holder.setProfileInchat(getApplication(), model.getName(), model.getUid(), model.getProf());

                        final String name = model.getName();
                        final String url = model.getUrl();
                        final String uid = model.getUid();

                        holder.sendmessagebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ChatActivity.this,MessageActivity.class);
                                intent.putExtra("name", name);
                                intent.putExtra("url", url);
                                intent.putExtra("uid", uid);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProfileViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.chat_profile_item, parent, false);
                        return new ProfileViewholder(view);
                    }
                };

        firebaseRecyclerAdapter1.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter1);
    }
}
