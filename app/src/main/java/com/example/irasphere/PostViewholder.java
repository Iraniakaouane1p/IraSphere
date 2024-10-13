package com.example.irasphere;

import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.fragment.app.FragmentActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.SimpleExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.MediaSourceFactory;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.TrackSelector;
import androidx.media3.exoplayer.upstream.BandwidthMeter;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;
import androidx.media3.extractor.DefaultExtractorsFactory;
import androidx.media3.extractor.ExtractorsFactory;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

public class PostViewholder extends RecyclerView.ViewHolder {
    ImageView imageViewprofile, iv_post;
    TextView tv_name, tv_desc, tv_likes, tv_comment, tv_time, tv_nameprofile;
    ImageButton likebtn, menuoptions, commentbtn;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference likesref;
    FirebaseDatabase databaseref = FirebaseDatabase.getInstance();

    int likescount;

    public PostViewholder(@NonNull View itemView) {
        super(itemView);
    }

    @OptIn(markerClass = UnstableApi.class)
    public void SetPost(FragmentActivity activity, String name, String url, String postUri, String time, String uid, String type, String desc) {
        imageViewprofile = itemView.findViewById(R.id.ivprofile_item);
        iv_post = itemView.findViewById(R.id.iv_post_item);
        tv_desc = itemView.findViewById(R.id.tv_desc_post);
        //tv_comment = itemView.findViewById(R.id.tv_comment_post);
        commentbtn = itemView.findViewById(R.id.commentbutton_posts);
        likebtn = itemView.findViewById(R.id.likebutton_posts);
        tv_likes = itemView.findViewById(R.id.tv_likes_post);
        menuoptions = itemView.findViewById(R.id.morebutton_posts);
        tv_time = itemView.findViewById(R.id.tv_time_post);
        tv_nameprofile = itemView.findViewById(R.id.tv_name_post);

        PlayerView playerView = itemView.findViewById(R.id.exoplayer_item_post);

        if (type.equals("iv")) {
            // Chargement des images et du texte pour le type "iv"
            Picasso.get().load(url).into(imageViewprofile);
            Picasso.get().load(postUri).into(iv_post);
            tv_desc.setText(desc);
            tv_time.setText(time);
            tv_nameprofile.setText(name);
            playerView.setVisibility(View.INVISIBLE); // Cacher le lecteur ExoPlayer pour le type "iv"

        } else if (type.equals("vv")) {
            // Traitement pour le type "vv" (vidéo)
            iv_post.setVisibility(View.INVISIBLE);
            tv_desc.setText(desc);
            tv_time.setText(time);
            tv_nameprofile.setText(name);
            Picasso.get().load(url).into(imageViewprofile);
            try {
                // Création du lecteur ExoPlayer pour la vidéo
                DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(activity).build();
                AdaptiveTrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();
                TrackSelector trackSelector = new DefaultTrackSelector(activity, trackSelectionFactory);
                SimpleExoPlayer exoPlayer = new SimpleExoPlayer.Builder(activity).setTrackSelector(trackSelector).build();

                // Créer un MediaItem à partir de l'URI de la vidéo
                MediaItem mediaItem = MediaItem.fromUri(postUri);

                // Créer un MediaSourceFactory
                MediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(activity);

                // Créer un MediaSource à partir du MediaItem
                MediaSource mediaSource = mediaSourceFactory.createMediaSource(mediaItem);

                playerView.setPlayer(exoPlayer);
                exoPlayer.setMediaSource(mediaSource);
                exoPlayer.prepare();
                exoPlayer.setPlayWhenReady(false);
            } catch (Exception e) {
                // Gérer les exceptions ici...
                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void likeChecker(final String postkey) {
        likebtn = itemView.findViewById(R.id.likebutton_posts);
        likesref = databaseref.getReference("post likes");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();
        likesref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postkey).hasChild(uid)) {
                    likebtn.setImageResource(R.drawable.ic_like);
                    likescount = (int) snapshot.child(postkey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likescount) + " likes");
                } else {
                    likebtn.setImageResource(R.drawable.ic_dislike);
                    likescount = (int) snapshot.child(postkey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likescount) + " likes");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Gérer les erreurs ici...
            }
        });
    }
}
