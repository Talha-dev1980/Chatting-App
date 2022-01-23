package com.chatting.chatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class EditProfile extends AppCompatActivity {

    private String name, email, status, imgLink, selectedUserId;
    private Button btnEditImage, btnSave;
    private EditText edtName, edtStatus;
    private CircleImageView imgUser;
    private ProgressDialog dialog;
    private StorageReference mStorageRef;
    private String newImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edit_profile );
        Intent it = getIntent();
        name = it.getStringExtra( "name" );
        status = it.getStringExtra( "status" );
        email = it.getStringExtra( "email" );
        imgLink = it.getStringExtra( "profile" );
        selectedUserId = it.getStringExtra( "userId" );

        btnEditImage = (Button) findViewById( R.id.btnEditPic );
        btnSave = (Button) findViewById( R.id.btnSaveChangesEP );
        edtStatus = (EditText) findViewById( R.id.edt_UserStatusEP );
        edtName = (EditText) findViewById( R.id.edt_userNameEP );
        imgUser = (CircleImageView) findViewById( R.id.imgUserProfileEditProfile );
        mStorageRef = FirebaseStorage.getInstance().getReference().child( "UserProfiles" );

        edtName.setText( name );
        edtStatus.setText( status );

        Picasso.get().load( imgLink ).placeholder( R.drawable.avator ).into( imgUser );

        btnEditImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines( CropImageView.Guidelines.ON )
                        .setAspectRatio( 1, 1 )
                        .start( EditProfile.this );
            }
        } );
        btnSave.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!status.equals( edtStatus ) && !name.equals( edtName ) && !imgLink.equals( newImageURL )) {
                    saveChanges( edtStatus.getText().toString(), edtName.getText().toString(), newImageURL );
                }
            }
        } );


    }

    private void saveChanges(String status, String name, String url) {
        Map map = new HashMap();
        map.put( "name", name );
        map.put( "email", email );
        map.put( "status", status );
        map.put( "img", url+"" );
        FirebaseDatabase.getInstance().getReference().child( "UsersData" ).child( selectedUserId ).updateChildren( map ).addOnCompleteListener( new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText( EditProfile.this, "Profile Changed Successfully...", Toast.LENGTH_LONG ).show();
                    finish();
                } else {
                    Toast.makeText( EditProfile.this, "Profile changes failed...", Toast.LENGTH_LONG ).show();

                }
            }
        } );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult( data );
            if (resultCode == RESULT_OK) {
                dialog = new ProgressDialog( EditProfile.this );
                dialog.setTitle( "Updating..." );
                dialog.setMessage( "Saving image" );
                dialog.setCanceledOnTouchOutside( false );
                dialog.show();
                Uri resultUri = result.getUri();
                saveimage( resultUri );

                //  Toast.makeText(CompanyProfile.this,""+resultUri,Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                dialog.dismiss();
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText( EditProfile.this, "Error While Processing ", Toast.LENGTH_SHORT ).show();
        } else {
            Toast.makeText( EditProfile.this, "Error While Processing Image", Toast.LENGTH_SHORT ).show();

        }
    }

    private void saveimage(Uri resultUri) {
        final File thumb_filepath = new File( resultUri.getPath() );

        final Bitmap thumb_bmp = new Compressor( EditProfile.this ).setMaxHeight( 200 ).setMaxWidth( 200 ).setQuality( 25 )
                .compressToBitmap( thumb_filepath );
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        thumb_bmp.compress( Bitmap.CompressFormat.JPEG, 50, outputStream );
        final byte[] thumb_byte = outputStream.toByteArray();
        //  final String imgId = FirebaseDatabase.getInstance().getReference().push().getKey().toString();
        final StorageReference thumb_path = mStorageRef.child( selectedUserId + ".jpg" );


        UploadTask uploadTask = thumb_path.putBytes( thumb_byte );
        uploadTask.addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot thumbTask) {
                Task uri = thumbTask.getStorage().getDownloadUrl();
                while (!uri.isComplete()) ;
                newImageURL = uri.getResult().toString();
                Picasso.get().load( newImageURL ).placeholder( R.drawable.avator ).into( imgUser );
                dialog.dismiss();
            }
        } );
    }

}