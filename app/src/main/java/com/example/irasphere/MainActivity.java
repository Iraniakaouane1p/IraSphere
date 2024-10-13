package com.example.irasphere;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        //Fragements


        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNav);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new Fragment1()).commit();



    }
    private BottomNavigationView.OnNavigationItemSelectedListener onNav=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selected= null;

            int itemId = item.getItemId();
            if(itemId==R.id.profile_bottom){
                selected=new Fragment1();
            } else if (itemId== R.id.ask_bottom) {
                selected=new Fragment2();
            }else if (itemId== R.id.queue_bottom) {
                selected=new Fragment3();
            }else if (itemId==R.id.home_bottom) {
                selected=new Fragment4();
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,selected).commit();

            return  true;
        }
    };


    public void logout(View view) {
        auth.signOut();
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if (user==null){
            Intent intent= new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();

        }
    }
}