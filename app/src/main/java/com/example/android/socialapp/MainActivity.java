package com.example.android.socialapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mSocialList;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;

    FirebaseRecyclerAdapter<Social, SocialViewHolder> FBRA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSocialList = (RecyclerView) findViewById(R.id.social_list);
        mSocialList.setHasFixedSize(true);
        mSocialList.setLayoutManager(new LinearLayoutManager(this));

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("SocialApp");

        Query postQuery = mDatabaseReference.orderByKey();
        FirebaseRecyclerOptions<Social> options = new FirebaseRecyclerOptions.Builder<Social>()
                .setQuery(postQuery, Social.class)
                .build();
        FBRA = new FirebaseRecyclerAdapter<Social, SocialViewHolder>(
                options) {
            @Override
            public SocialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.social_row, parent, false);

                return new SocialViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull SocialViewHolder holder, int position, @NonNull Social model) {
                holder.setDesc(model.getDesc());
                holder.setTitle(model.getTitle());
            }
        };
        mSocialList.setAdapter(FBRA);

    }


    @Override
    protected void onStart() {
        super.onStart();
        FBRA.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        FBRA.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public static class SocialViewHolder extends RecyclerView.ViewHolder {

        public SocialViewHolder(View itemView) {
            super(itemView);
            View mView = itemView;
        }

        public void setTitle(String title) {
            TextView post_title = (TextView) itemView.findViewById(R.id.text_title);
            post_title.setText(title);
        }

        public void setDesc(String desc) {
            TextView post_desc = (TextView) itemView.findViewById(R.id.text_desc);
            post_desc.setText(desc);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.addIcon) {
            Intent intent = new Intent(MainActivity.this, PostActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
