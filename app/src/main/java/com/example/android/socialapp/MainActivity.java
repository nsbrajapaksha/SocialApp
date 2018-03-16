package com.example.android.socialapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
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
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.lang.ref.WeakReference;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class MainActivity extends AppCompatActivity{

    private static RecyclerView mSocialList;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;

    FirebaseRecyclerAdapter<Social, SocialViewHolder> FBRA;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    SkeletonScreen skeletonScreen;
    private boolean animate;
    private boolean recyclerviewLoaded;
    private static ProgressBar mprogressBar;

    AlphaInAnimationAdapter alphaInAnimationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fresco.initialize(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        animate = true;
        recyclerviewLoaded = true;

        mSocialList = (RecyclerView) findViewById(R.id.social_list);
        mSocialList.setHasFixedSize(true);
        mSocialList.setLayoutManager(new LinearLayoutManager(this));
        mprogressBar = (ProgressBar)findViewById(R.id.main_progressBar);

        SlideInLeftAnimator animator = new SlideInLeftAnimator();
        animator.setInterpolator(new OvershootInterpolator());
        mSocialList.setItemAnimator(animator);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("SocialApp");

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);

                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        Query postQuery = mDatabaseReference.orderByKey();
        FirebaseRecyclerOptions<Social> options = new FirebaseRecyclerOptions.Builder<Social>()
                .setQuery(postQuery, Social.class)
                .build();
        FBRA = new FirebaseRecyclerAdapter<Social, SocialViewHolder>(
                options) {
            @NonNull
            @Override
            public SocialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.social_row, parent, false);

                return new SocialViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull SocialViewHolder holder, int position, @NonNull Social model) {

                final String post_key = getRef(position).getKey().toString();

                if (recyclerviewLoaded && position == 0) {
                    mprogressBar.setVisibility(View.INVISIBLE);
                    mSocialList.setVisibility(View.VISIBLE);
                    recyclerviewLoaded = false;
                }

                holder.setDesc(model.getDesc());
                holder.setTitle(model.getTitle());
                holder.setImage(getApplicationContext(), model.getImage());
                holder.setUsername("Shared by " + model.getUsername());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singleSocialActivity = new Intent(MainActivity.this, SingleSocialActivity.class);
                        singleSocialActivity.putExtra("PostId", post_key);
                        startActivity(singleSocialActivity);

                    }
                });
            }
        };

        alphaInAnimationAdapter = new AlphaInAnimationAdapter(FBRA);
        mSocialList.setAdapter(new ScaleInAnimationAdapter(alphaInAnimationAdapter));

    }


    @Override
    protected void onStart() {
        super.onStart();
        mSocialList.setAdapter(new ScaleInAnimationAdapter(alphaInAnimationAdapter));
        FBRA.startListening();
        if (animate && isNetworkAvailable(MainActivity.this)) {
            skeletonScreen = Skeleton.bind(mSocialList)
                    .duration(1000)
                    .adapter(FBRA)
                    .load(R.layout.layout_skeleton)
                    .show();
            MyHandler myHandler = new MyHandler(MainActivity.this);
            myHandler.sendEmptyMessageDelayed(1, 3000);
            animate = false;
        }
        if (!isNetworkAvailable(this)) {
            Snackbar.make(findViewById(R.id.relativeLayout), "No internet access", Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(findViewById(R.id.relativeLayout), "Connected to internet", Snackbar.LENGTH_LONG).show();
        }

        // Manually checking internet connection
        //checkConnection();
        mAuth.addAuthStateListener(mAuthListener);


    }

    public static class MyHandler extends android.os.Handler {
        private final WeakReference<MainActivity> activityWeakReference;

        MyHandler(MainActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (activityWeakReference.get() != null) {
                activityWeakReference.get().skeletonScreen.hide();
                mprogressBar.setVisibility(View.VISIBLE);
                mSocialList.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FBRA.stopListening();
        if (mAuth != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public static class SocialViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public SocialViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title) {
            TextView post_title = (TextView) mView.findViewById(R.id.text_title);
            post_title.setText(title);
        }

        public void setDesc(String desc) {
            TextView post_desc = (TextView) mView.findViewById(R.id.text_desc);
            post_desc.setText(desc);
        }

        public void setImage(Context context, String image) {
            /*final ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(context).load(image)
                    .resize(270, 150)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error_image)
                    .into(post_image);*/
            final SimpleDraweeView mSimpleDraweeView = (SimpleDraweeView) mView.findViewById(R.id.post_image);
            mSimpleDraweeView.setController(
                    Fresco.newDraweeControllerBuilder()
                    .setTapToRetryEnabled(true)
                    .setUri(Uri.parse(image))
                    .build());
        }

        public void setUsername(String username) {
            TextView postUsername = (TextView) mView.findViewById(R.id.textUsername);
            postUsername.setText(username);

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
        else if (id == R.id.addIcon) {
            Intent intent = new Intent(MainActivity.this, PostActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.logOut) {
            mAuth.signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    /*@Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    *//**
     * Callback will be triggered when there is change in
     * network connection
     *//*
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Snackbar.make(findViewById(R.id.relativeLayout), message, Snackbar.LENGTH_LONG).show();
    }*/
}
