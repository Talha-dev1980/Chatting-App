package com.chatting.chatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

public class Dashboard extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private RecyclerView usersList;
    private String currentUser;
    private String name,img,email,status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_dashboard );
        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference().child( "UsersData" );

        usersList = (RecyclerView) findViewById( R.id.listUsers );
        usersList.setLayoutManager( new LinearLayoutManager( this ) );

        if (mAuth.getCurrentUser()!=null ){
        currentUser=mAuth.getCurrentUser().getUid().toString();
        FirebaseMessaging.getInstance().subscribeToTopic( mAuth.getCurrentUser().getUid().toString() );
        fetchUserProfile();}
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            FirebaseRecyclerAdapter<UserInfo, UsersViewHolder> adapter = new FirebaseRecyclerAdapter<UserInfo, UsersViewHolder>(
                    UserInfo.class,
                    R.layout.item_user,
                    UsersViewHolder.class,
                    mReference
            ) {
                @Override
                protected void populateViewHolder(UsersViewHolder holder, final UserInfo user, final int i) {
                    try {
                        holder.setPic( user.getImg().toString()+"" );

                        holder.tvUsername.setText( user.getName() );
                        holder.tvStatus.setText( user.getStatus() );
                        final String user_id = getRef( i ).getKey();
                        holder.mView.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent it=new Intent(Dashboard.this,Profile.class);
                                it.putExtra( "name",user.getName() );
                                it.putExtra( "email",user.getEmail() );
                                it.putExtra( "userId",user_id );
                                it.putExtra( "profile",user.getImg() );
                                it.putExtra( "status",user.getStatus() );
                                startActivity( it );

                            }
                        } );

                        //dialog.dismiss();
                    } catch (DatabaseException e) {
                        e.printStackTrace();
                    }
                }
            };
            usersList.setAdapter( adapter );


        } else {
            startActivity( new Intent( Dashboard.this, MainActivity.class ) );
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_Profile:
                Intent it=new Intent(Dashboard.this,Profile.class);
                it.putExtra( "name",name);
                it.putExtra( "email",email);
                it.putExtra( "userId",currentUser );
                it.putExtra( "profile",img);
                it.putExtra( "status",status );
                startActivity( it );
                return true;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                onStart();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchUserProfile(){
        FirebaseDatabase.getInstance().getReference().child( "UsersData" ).child( currentUser ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("img"  )){
                    img=snapshot.child( "img" ).getValue().toString();

                }
                name=snapshot.child( "name" ).getValue().toString();
                email=snapshot.child( "email" ).getValue().toString();
                status=snapshot.child( "status" ).getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText( Dashboard.this,"Error while loading data",Toast.LENGTH_LONG ).show();
            }
        } );
    }
    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        // public     CircleImageView userImage;

        View mView;
        TextView tvUsername, tvStatus;

        public UsersViewHolder(@NonNull View itemView) {
            super( itemView );
            mView = itemView;

            tvStatus = (TextView) mView.findViewById( R.id.userStatuslist );
            tvUsername = (TextView) mView.findViewById( R.id.tvUserNameList );


        }

        public void setPic(String link) {
            ImageView img = (ImageView) itemView.findViewById( R.id.userImageList );
            Picasso.get().load( link + "" ).placeholder( R.drawable.avator ).into( img );

        }

    }
}