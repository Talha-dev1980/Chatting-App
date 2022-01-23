package com.chatting.chatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.chatting.chatsapp.utils.TimeAgo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private String name, status, email, imgLink, selectedUserId;
    private CircleImageView imgUser;
    private TextView tvUsername, tvstatus;
    private Button btnEdit, btnSendReq, btnAcceptfrnd, btnSendMessage, btnCancelReq, btnUnfriend;
    private String loggedinUser;

    private String URL = "https://fcm.googleapis.com/fcm/send";
    RequestQueue requestQueue;
    private DatabaseReference frndzRef;
    private String currentUser;
    private RecyclerView listFriends, frndsList;
    private DatabaseReference frndzReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_profile );

        Intent it = getIntent();
        name = it.getStringExtra( "name" );
        status = it.getStringExtra( "status" );
        email = it.getStringExtra( "email" );
        imgLink = it.getStringExtra( "profile" );
        selectedUserId = it.getStringExtra( "userId" );

        loggedinUser = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        tvstatus = (TextView) findViewById( R.id.tvUserStatus );
        tvUsername = (TextView) findViewById( R.id.tvUserNAme );
        btnEdit = (Button) findViewById( R.id.btnEditProfileE );
        btnSendMessage = (Button) findViewById( R.id.btnSendMessageProfile );
        btnSendReq = (Button) findViewById( R.id.btnSendReqProfile );
        btnUnfriend = (Button) findViewById( R.id.btnUnFriendProfile );
        btnCancelReq = (Button) findViewById( R.id.btnCancelReqProfile );
        btnAcceptfrnd = (Button) findViewById( R.id.btnAcceptRequestProfile );
        imgUser = (CircleImageView) findViewById( R.id.imgUserProfile );
        listFriends = (RecyclerView) findViewById( R.id.listFriends );
        listFriends.setLayoutManager( new LinearLayoutManager( this ) );
        frndsList = (RecyclerView) findViewById( R.id.listConfirmFriends );
        frndsList.setLayoutManager( new LinearLayoutManager( this ) );

        tvstatus.setText( status );
        tvUsername.setText( name );
        Picasso.get().load( imgLink ).placeholder( R.drawable.avator ).into( imgUser );
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        frndzReference = FirebaseDatabase.getInstance().getReference().child( "Connections" ).child( currentUser );
        if (currentUser.equals( selectedUserId )) {
            btnEdit.setVisibility( View.VISIBLE );
            findViewById( R.id.lytUserAction ).setVisibility( View.GONE );
            findViewById( R.id.tvFrequest ).setVisibility( View.VISIBLE );
            listFriends.setVisibility( View.VISIBLE );
            frndsList.setVisibility( View.VISIBLE );
        } else {
            checkFrndStatus( selectedUserId, currentUser );

        }
        btnAcceptfrnd.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriend( selectedUserId );
                btnSendReq.setVisibility( View.GONE );
                btnAcceptfrnd.setVisibility( View.GONE );
                //onRestart();

            }
        } );
        btnEdit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent( Profile.this, EditProfile.class );
                it.putExtra( "name", name );
                it.putExtra( "email", email );
                it.putExtra( "userId", currentUser );
                it.putExtra( "profile", imgLink );
                it.putExtra( "status", status );
                startActivity( it );
                finish();
            }
        } );
        btnUnfriend.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFriend( selectedUserId, currentUser );
            }
        } );

        btnSendReq.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendRequest( selectedUserId, currentUser );

            }
        } );
        btnCancelReq.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRequest( selectedUserId, currentUser );
            }
        } );
        requestQueue = Volley.newRequestQueue( this );
        frndzRef = FirebaseDatabase.getInstance().getReference().child( "Connections" );
        btnSendMessage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(Profile.this,ChattingActivity.class);
                it.putExtra( "name",name );
                it.putExtra( "status",status );
                it.putExtra( "img",imgLink );
                it.putExtra( "selectedUserId",selectedUserId );
                startActivity( it );

            }
        } );

    }

    private void checkFrndStatus(String selectedUserId, String currentUser) {
        FirebaseDatabase.getInstance().getReference().child( "Friends" ).child( currentUser ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild( selectedUserId )) {
                    btnCancelReq.setVisibility( View.GONE );
                    btnSendReq.setVisibility( View.GONE );
                    btnAcceptfrnd.setVisibility( View.GONE );
                    btnUnfriend.setVisibility( View.VISIBLE );

                } else {

                    btnUnfriend.setVisibility( View.GONE );
                    FirebaseDatabase.getInstance().getReference().child( "Connections" ).child( currentUser ).child( selectedUserId ).addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild( "recieve" )) {
                                if (snapshot.child( "recieve" ).getValue().toString().equals( currentUser )) {
                                    btnAcceptfrnd.setVisibility( View.VISIBLE );
                                    btnSendReq.setVisibility( View.GONE );
                                    btnCancelReq.setVisibility( View.GONE );
                                } else {
                                    btnCancelReq.setVisibility( View.VISIBLE );
                                    btnAcceptfrnd.setVisibility( View.GONE );
                                    btnSendReq.setVisibility( View.GONE );
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    } );


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void cancelRequest(String selectedUserIdCancel, String currentUser) {

        FirebaseDatabase.getInstance().getReference().child( "Connections" ).child( currentUser ).child( selectedUserIdCancel ).setValue( null ).addOnCompleteListener( new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference().child( "Connections" ).child( selectedUserIdCancel ).child( currentUser ).setValue( null ).addOnCompleteListener( new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                //   Toast.makeText( Profile.this, "Friend request cancelled....", Toast.LENGTH_LONG ).show();
                                btnCancelReq.setVisibility( View.GONE );
                                btnSendReq.setVisibility( View.VISIBLE );


                            }
                        }
                    } );

                } else {
                    Toast.makeText( Profile.this, "Failed to cancel Friend request", Toast.LENGTH_LONG ).show();
                }
            }
        } );
    }

    private void sendRequest(String selectedUserId, String currentUser) {
        String currentTime = Calendar.getInstance().getTimeInMillis() + "";
        Map frndReqMap = new HashMap();
        frndReqMap.put( "sent", currentUser );
        frndReqMap.put( "recieve", selectedUserId );
        frndReqMap.put( "timeSent", currentTime );

        FirebaseDatabase.getInstance().getReference().child( "Connections" ).child( currentUser ).child( selectedUserId ).setValue( frndReqMap ).addOnCompleteListener( new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference().child( "Connections" ).child( selectedUserId ).child( currentUser ).setValue( frndReqMap ).addOnCompleteListener( new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText( Profile.this, "Friend request sent....", Toast.LENGTH_LONG ).show();
                                btnSendReq.setVisibility( View.GONE );
                                btnCancelReq.setVisibility( View.VISIBLE );
                            }
                        }
                    } );

                } else {
                    Toast.makeText( Profile.this, "Failed to send Friend request", Toast.LENGTH_LONG ).show();
                }
            }
        } );
    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseRecyclerAdapter<RequestInfo, UsersViewHolder> adapter = new FirebaseRecyclerAdapter<RequestInfo, UsersViewHolder>(
                RequestInfo.class,
                R.layout.item_request,
                UsersViewHolder.class,
                frndzReference

        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder holder, RequestInfo req, int i) {
                //holder.tvUsername.setText( getRef( i ).getKey().toString() );
                FirebaseDatabase.getInstance().getReference().child( "UsersData" ).child( getRef( i ).getKey().toString() ).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.tvUsername.setText( snapshot.child( "name" ).getValue().toString() );
                        holder.setPic( snapshot.child( "img" ).getValue().toString() );
                        holder.tvStatus.setText( setTime( req.getTimeSent() ) + "" );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                } );
                if (req.getRecieve().equals( currentUser )) {
                    holder.btnDeleteReq.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteFriend( getRef( i ).getKey().toString(), currentUser );
                        }
                    } );
                    holder.btnAcceptReq.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addFriend( getRef( i ).getKey().toString() );
                            holder.btnAcceptReq.setVisibility( View.VISIBLE );
                        }
                    } );
                    holder.tvUsername.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            gotoUserProfile( getRef( i ).getKey().toString() );
                        }
                    } );


                } else {
                    holder.btnAcceptReq.setVisibility( View.INVISIBLE );
                    holder.btnDeleteReq.setText( "Cancel Request" );
                    holder.btnDeleteReq.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteFriend( getRef( i ).getKey().toString(), currentUser );
                        }
                    } );
                }
            }
        };
        FirebaseRecyclerAdapter<UserInfo, UsersViewHolder> frndzAdapter = new FirebaseRecyclerAdapter<UserInfo, UsersViewHolder>(
                UserInfo.class,
                R.layout.item_request,
                UsersViewHolder.class,
                FirebaseDatabase.getInstance().getReference().child( "Friends" ).child( currentUser )
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, UserInfo userInfo, int i) {
                usersViewHolder.btnAcceptReq.setVisibility( View.INVISIBLE );
                FirebaseDatabase.getInstance().getReference().child( "UsersData" ).child( getRef( i ).getKey().toString() ).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        usersViewHolder.tvUsername.setText( snapshot.child( "name" ).getValue().toString() );
                        usersViewHolder.tvStatus.setText( snapshot.child( "status" ).getValue().toString() );
                        usersViewHolder.setPic( snapshot.child( "img" ).getValue().toString() );
                        usersViewHolder.btnDeleteReq.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteFriend( getRef( i ).getKey().toString(), currentUser );
                            }
                        } );
                        usersViewHolder.tvUsername.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                gotoUserProfile( getRef( i ).getKey().toString() );
                            }
                        } );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );

            }
        };

        listFriends.setAdapter( adapter );
        frndsList.setAdapter( frndzAdapter );
    }

    private void gotoUserProfile(String frndId) {
        FirebaseDatabase.getInstance().getReference().child( "UsersData" ).child( frndId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Intent it = new Intent( Profile.this, Profile.class );
                it.putExtra( "name", snapshot.child( "name" ).getValue().toString() );
                it.putExtra( "email", snapshot.child( "email" ).getValue().toString() );
                it.putExtra( "userId", frndId );
                it.putExtra( "profile", snapshot.child( "img" ).getValue().toString() );
                it.putExtra( "status", snapshot.child( "status" ).getValue().toString() );
                startActivity( it );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void deleteFriend(String friendId, String currentUser) {
        Map delFriend = new HashMap();
        FirebaseDatabase.getInstance().getReference().child( "Friends" ).child( currentUser ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild( friendId )) {
                    FirebaseDatabase.getInstance().getReference().child( "Friends" ).child( currentUser ).child( friendId ).setValue( null ).addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            FirebaseDatabase.getInstance().getReference().child( "Friends" ).child( friendId ).child( currentUser ).setValue( null ).addOnCompleteListener( new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText( Profile.this, "Friend Deleted", Toast.LENGTH_LONG ).show();
                                    }
                                }
                            } );
                        }
                    } );

                } else {
                    cancelRequest( friendId, currentUser );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void addFriend(String frndId) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        String currentTime = Calendar.getInstance().getTimeInMillis() + "";
        Map friendMap = new HashMap();
        friendMap.put( "time", currentTime );
        //FirebaseDatabase.getInstance().getReference().child( "Connections" ).child( currentUser ).child( frndId ).setValue( null );
        //FirebaseDatabase.getInstance().getReference().child( "Connections" ).child( frndId ).child( currentTime ).setValue( null );
        FirebaseDatabase.getInstance().getReference().child( "Friends" ).child( currentUser )
                .child( frndId ).setValue( friendMap ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference().child( "Friends" ).child( frndId )
                            .child( currentUser ).setValue( friendMap ).addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText( Profile.this, "Friend Added...", Toast.LENGTH_LONG ).show();
                                cancelRequest( frndId, currentUser );

                            } else {
                                Toast.makeText( Profile.this, "Friend Add failed...", Toast.LENGTH_LONG ).show();
                            }

                        }
                    } );
                }
            }
        } );
    }

    private String setTime(String timeSent) {
        TimeAgo timeAgo = new TimeAgo();
        return timeAgo.convertToTimeAgo( Long.parseLong( timeSent ) );
    }


    private void addTofriend(String requestUser) {
        String currentTime = Calendar.getInstance().getTimeInMillis() + "";
        Map frndReqMap = new HashMap();
        frndReqMap.put( "timeFrnd", currentTime );

        FirebaseDatabase.getInstance().getReference().child( "Connections" ).child( currentUser ).child( requestUser ).setValue( frndReqMap ).addOnCompleteListener( new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference().child( "Connections" ).child( requestUser ).child( currentUser ).setValue( frndReqMap ).addOnCompleteListener( new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText( Profile.this, "Added to friends....", Toast.LENGTH_LONG ).show();
                            }
                        }
                    } );

                } else {
                    Toast.makeText( Profile.this, "Failed to Add in friends", Toast.LENGTH_LONG ).show();
                }
            }
        } );
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {


        View mView;
        TextView tvUsername, tvStatus;
        Button btnAcceptReq, btnDeleteReq;

        public UsersViewHolder(@NonNull View itemView) {
            super( itemView );
            mView = itemView;

            tvStatus = (TextView) mView.findViewById( R.id.timeSent );
            tvUsername = (TextView) mView.findViewById( R.id.tvUserSentReq );
            btnAcceptReq = (Button) mView.findViewById( R.id.btnAcceptReq );
            btnDeleteReq = (Button) mView.findViewById( R.id.btnDeleteReq );


        }

        public void setPic(String link) {
            ImageView img = (ImageView) itemView.findViewById( R.id.userReqImageList );
            Picasso.get().load( link + "" ).placeholder( R.drawable.avator ).into( img );

        }

    }
/* private void sendNotification(String selectedUserId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put( "to", "/topics/" + selectedUserId );
            JSONObject notificationObject = new JSONObject();
            notificationObject.put( "title","From "+loggedinUser );
            notificationObject.put( "body",loggedinUser+ " sent you a friend request" );
            jsonObject.put( "notification",notificationObject );
            JsonObjectRequest notifRequest=new JsonObjectRequest( Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            } ){}


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
}