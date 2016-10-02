package com.basicelixir.pawel.torrentnotifier;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Pawel on 01/10/2016.
 */

public class CreateDialog extends AlertDialog implements View.OnClickListener {
    
    Button btnCreate;
    EditText etUserName, etPassword;
    String TAG ="pawell";
    FirebaseAuth fireBaseAuth;
   Activity context;
    protected CreateDialog(Context context) {
        super(context);
        this.context =(Activity) context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_dialog);
        fireBaseAuth = FirebaseAuth.getInstance();

        btnCreate = (Button)findViewById(R.id.btn_create);
        btnCreate.setOnClickListener(this);

        etUserName =(EditText)findViewById(R.id.username_ET);
        etPassword =(EditText)findViewById(R.id.password_ET);
    }

    @Override
    public void onClick(View v) {
        if(v==findViewById(R.id.btn_create)){
            String username = etUserName.getText().toString();
           String  password = etPassword.getText().toString();
            createNewUser(username, password);
        }
        
    }

    private void createNewUser(String username, String password) {

        fireBaseAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Log.i(TAG, "onComplete: User ADDED");
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    db.getReference().child("Users").child(task.getResult().getUser().getUid()).setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "onComplete: user saved to db "+task.isSuccessful());
                        }
                    });


                }else{
                    Log.i(TAG, "onComplete: CREATE ERROR "+ task.getException());
                }
            }
        });

    
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(TAG, "onDetachedFromWindow: ");
    }
}
