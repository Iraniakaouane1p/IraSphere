package com.example.irasphere;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Fragment4 extends Fragment implements View.OnClickListener {
    Button button;
    RecyclerView recyclerView;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference reference,likref;
    Boolean likechecker=false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment4, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        button = getActivity().findViewById(R.id.createpost_f4);
        reference=database.getReference("All posts");
        likref=database.getReference("post likes");
        recyclerView=getActivity().findViewById(R.id.rv_posts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.createpost_f4) {
            // Rediriger vers l'activité de mise à jour du profil lors du clic sur le bouton "Edit"
            Intent intent = new Intent(getActivity(), PostActivity.class);
            startActivity(intent);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
       FirebaseRecyclerOptions<Postmember> options = new FirebaseRecyclerOptions.Builder<Postmember>()
                .setQuery(reference, Postmember.class)
                .build();


        FirebaseRecyclerAdapter<Postmember, PostViewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter
                <Postmember,PostViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewholder holder, int position, @NonNull Postmember model) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                final String currentUserid=user.getUid();
                final String postkey=getRef(position).getKey();
                holder.SetPost(getActivity(),model.getName(), model.getUrl(), model.getPostUri(), model.getTime(), model.getUid(), model.getType(), model.getDesc());
                 holder.likeChecker(postkey);
                 holder.likebtn.setOnClickListener((view)->{
                     likechecker=true;
                     likref.addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                             if (likechecker.equals(true)){
                                 if (snapshot.child(postkey).hasChild(currentUserid)){
                                     likref.child(postkey).child(currentUserid).removeValue();
                                     Toast.makeText(getActivity(),"Removed free favorite",Toast.LENGTH_SHORT).show();
                                     likechecker=false;
                                 }else {
                                     likref.child(postkey).child(currentUserid).setValue(true);
                                     likechecker=false;
                                     Toast.makeText(getActivity(),"Added to favourite",Toast.LENGTH_SHORT).show();

                                 }
                             }

                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError error) {

                         }
                     });
                 });

            }


            @NonNull
            @Override
            public PostViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
                return new PostViewholder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    }
