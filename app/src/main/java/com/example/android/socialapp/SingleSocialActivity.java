package com.example.android.socialapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class SingleSocialActivity extends AppCompatActivity {

    private String post_key = null;
    private DatabaseReference mDatabaseReference;
    private ImageView singlePostImage;
    private TextView singlePostTitle;
    private TextView singlePostDesc;
    private Button deleteButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_social);

        post_key = getIntent().getExtras().getString("PostId");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("SocialApp");

        singlePostDesc = (TextView) findViewById(R.id.singleDesc);
        singlePostTitle = (TextView) findViewById(R.id.singleTitle);
        singlePostImage = (ImageView) findViewById(R.id.singleImageView);

        mAuth = FirebaseAuth.getInstance();
        deleteButton = (Button) findViewById(R.id.singleDeleteButton);
        deleteButton.setVisibility(View.INVISIBLE);

        mDatabaseReference.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                String post_image = (String) dataSnapshot.child("image").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();

                singlePostTitle.setText(post_title);
                singlePostDesc.setText(post_desc);
                Picasso.with(SingleSocialActivity.this).load(post_image).into(singlePostImage);

                if (mAuth.getCurrentUser().getUid().equals(post_uid)) {
                    deleteButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void deleteButtonClicked(View view) {
        mDatabaseReference.child(post_key).removeValue();
        Intent mainIntent = new Intent(SingleSocialActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }
}
