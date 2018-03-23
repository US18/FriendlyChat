package com.example.uplabdhisingh.friendlychat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    private static final String TAG = "MainActivity";

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final int RC_PHOTO_PICKER =  2;

    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;

    private static final int RC_SIGN_IN = 1;
    private String mUsername;

    //TODO(1): Create variables with type FirebaseDatabase and DatabaseReference.
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessageDatabaseReference;
    //TODO(29): Create a variable here for Firebase Storage.
    private FirebaseStorage mFirebaseStorage;
    //TODO(31):Variable for Storage Reference.
    private StorageReference mChatPhotosStorageReference;

    //TODO(11): Create Instance Variables for FirebaseAuth and AuthStateListener.
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //TODO(5): Create a variable with type ChildEventListener.
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO(2): Use the variables to getinstance for database and to get reference of database.
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mMessageDatabaseReference = mFirebaseDatabase.getReference().child("messages");


        //TODO(12): Initialize Firebase Auth variable in the same way as you did for database.
        mFirebaseAuth=FirebaseAuth.getInstance();
        //TODO(30):Initialization in onCreate Method.
        mFirebaseStorage=FirebaseStorage.getInstance();

        //TODO(32): Initialize the storage reference here.
        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");
        mUsername = ANONYMOUS;

        // Initialize references to views
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);

        // Initialize message ListView and its adapter
        List<FriendlyMessage> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Fire an intent to show an image picker
            }
        });

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
   // TODO(3): Get the edit Text data that is the message typed in the box and convert it toString and store it in friendly message object.
    //TODO(4) : Push the fetched data into the database and set its value as the friendlyMessage's object.

                FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(),mUsername,null);
                mMessageDatabaseReference.push().setValue(friendlyMessage);
                // Clear input box
                mMessageEditText.setText("");
            }
        });
/*
        //TODO(6): Instantiate the variable with new Object and override the methods.
        mChildEventListener = new ChildEventListener() {
            */
            /*
            * Implementation of the added method only
            * because we currently want to show our sent message on our adapter.
            */
            /*
            //TODO(7): In onChildAdded method, add the message using data snapshot's object and set that data to the adapter.
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                mMessageAdapter.add(friendlyMessage);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        //TODO(8): Add the ChildEventListener to the Database Reference.
        mMessageDatabaseReference.addChildEventListener(mChildEventListener);
*/


        //TODO(13): Initialize Auth State Listener at the bottom of onCreate Method.
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //TODO(16): Get the user and check whether the user is logged in or not.
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if( user != null )
                {
                   // Toast.makeText(MainActivity.this, "Welcome to Friendly Chat ! You are now Signed In.", Toast.LENGTH_LONG).show();

                    //TODO(20): Create a method onSignedIn which will be called when user Signs IN.
                    onSignedInInitialize(user.getDisplayName());
                } else
                    {
//TODO(17): Go to this website : https://github.com/firebase/FirebaseUI-Android/tree/master/auth#sign-in
// and copy the sign in provider method's code from email and facebook login.
                        onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()
                                            ))
                                      .build(),
                            RC_SIGN_IN);
                    /*
                   Since We only want Sign in For Email and Google, so We are commenting for Phone number, Facebook and Twitter.
                    * new AuthUI.IdpConfig.PhoneBuilder().build(),
                    * new AuthUI.IdpConfig.FacebookBuilder().build(),
                    * new AuthUI.IdpConfig.TwitterBuilder().build()
                    * */
                }
            }
        };

        //TODO(27): Add onClick on the PhotoPicker Button to allow user to choose Photos.
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });
    }

    //TODO(14): Override onResume and addAuthStateListener by passing the authStateListener's object.
    @Override
    protected void onResume()
    {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    //TODO(15): Override onPause and remove the auth State Listener.
    @Override
    protected void onPause()
    {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        detachDatabaseReadListener();
        mMessageAdapter.clear();
    }


    private void onSignedInInitialize(String username)
    {
        mUsername=username;
        //TODO(21): Copy the childEventListener code to this method so that user can only see the messages when it's signed in.

        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {

        if (mChildEventListener == null) {
            //TODO(6): Instantiate the variable with new Object and override the methods.
            mChildEventListener = new ChildEventListener() {
                /*
                * Implementation of the added method only
                * because we currently want to show our sent message on our adapter.
                */
                //TODO(7): In onChildAdded method, add the message using data snapshot's object and set that data to the adapter.
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    mMessageAdapter.add(friendlyMessage);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            //TODO(8): Add the ChildEventListener to the Database Reference.
            mMessageDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }
    private void onSignedOutCleanup()
    {

        mUsername = ANONYMOUS;
        mMessageAdapter.clear(); //Therefore the user who has signed out, his messages will be deleted from the chat.
        detachDatabaseReadListener();
    }

    //TODO(22): Like attachDb method, create a detachReadListener method to detach it when signed out.
    private void detachDatabaseReadListener()
    {
        if(mChildEventListener != null)
        {
            mMessageDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener=null;
        }

    }

    //TODO(25): override onActivityResult and implement the code.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        //TODO(33): Add one more else if to handle PhotoPicker.

        if(requestCode == RC_SIGN_IN)
        {
            if(resultCode == RESULT_OK)
            {
                Toast.makeText(this, "Signed In!", Toast.LENGTH_SHORT).show();
            } else if(resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this, "Sign In Cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }

        }  else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK)
        {
            //TODO(34): Get the Selected Image's Uri
            Uri selectedImageUri = data.getData();

            //TODO(35): Get the reference Location of the Photo to be saved.
           // assert selectedImageUri != null;
            StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

            //TODO(36): Now Send the selected photo to the Storage.
            photoRef.putFile(selectedImageUri)    //putFile method returns a upload task.
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                    assert downloadUrl != null;
                                    FriendlyMessage friendlyMessage = new FriendlyMessage(null,mUsername,downloadUrl.toString());

                                    //TODO(37); Store the object in the database and you are done !!!
                                    mMessageDatabaseReference.push().setValue(friendlyMessage);
                                }
                            });
        }
    }

    //TODO(26): Implement Sign Out method in onOptionItemSelected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.sign_out:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sign_out,menu);
        return true;

    }


}
