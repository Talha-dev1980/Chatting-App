package com.chatting.chatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatting.chatsapp.utils.MessageModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChattingActivity extends AppCompatActivity {

    private String name, email, status, img, selectedUsrId;
    private TextView tvUserName, tvStatus;
    private CircleImageView imgProfile;
    private String currentUser;
    private EditText edtTypeMessage;
    private ImageButton btnSend;
    private RecyclerView listMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        this.getSupportActionBar().hide();
        setContentView( R.layout.activity_chatting );

        Intent it = getIntent();
        name = it.getStringExtra( "name" );
        status = it.getStringExtra( "status" );
        img = it.getStringExtra( "img" );
        selectedUsrId = it.getStringExtra( "selectedUserId" );

        tvUserName = (TextView) findViewById( R.id.tvUserInfoChat );
        tvStatus = (TextView) findViewById( R.id.tvUserStatusChat );
        edtTypeMessage = (EditText) findViewById( R.id.edtTypeMessage );
        btnSend = (ImageButton) findViewById( R.id.btnSendMessage );
        imgProfile = (CircleImageView) findViewById( R.id.imgUserImgChat );
        listMessages = (RecyclerView) findViewById( R.id.listMessages );
        listMessages.setLayoutManager( new LinearLayoutManager( this ) );

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        tvUserName.setText( name );
        tvStatus.setText( status );
        Picasso.get().load( img ).placeholder( R.drawable.avator ).into( imgProfile );

        btnSend.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edtTypeMessage.getText().toString().trim();
                if (validateMessage( message )) {
                    sendMessage( message );
                }
            }
        } );

    }

    private void sendMessage(String message) {
        Map msgMap = new HashMap();
        msgMap.put( "message", message );
        msgMap.put( "to", message );
        msgMap.put( "from", message );
        FirebaseDatabase.getInstance().getReference().child( "Msgs" ).child( currentUser ).child( selectedUsrId ).push().setValue( msgMap ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference().child( "Msgs" ).child( selectedUsrId ).child( currentUser ).push().setValue( msgMap ).addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText( ChattingActivity.this, "Sent..", Toast.LENGTH_LONG ).show();
                            }
                        }
                    } );

                }
            }
        } );
    }

    private boolean validateMessage(String message) {
        if (message.length() > 1 && !message.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<MessageModel, UsersViewHolder> messagesAdpter = new FirebaseRecyclerAdapter<MessageModel, UsersViewHolder>(
                MessageModel.class,
                R.layout.item_message,
                UsersViewHolder.class,
                FirebaseDatabase.getInstance().getReference().child( "Msgs" ).child( currentUser ).child( selectedUsrId )
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder holder, MessageModel msg, int i) {
                if (msg.getFrom().equals( currentUser )) {
                    holder.tvSentMessage.setText( msg.getMessage() );
                    holder.tvRecievedMessage.setVisibility( View.GONE );
                } else {
                    holder.tvRecievedMessage.setText( msg.getMessage() );
                    holder.tvSentMessage.setVisibility( View.GONE );
                }
            }
        };
        listMessages.setAdapter( messagesAdpter );
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView tvSentMessage, tvRecievedMessage;

        public UsersViewHolder(@NonNull View itemView) {
            super( itemView );
            mView = itemView;
            tvSentMessage = (TextView) mView.findViewById( R.id.tvRecieveMessage );
            tvRecievedMessage = (TextView) mView.findViewById( R.id.tvSendMessage );
        }

    }
}