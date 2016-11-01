package com.basicelixir.pawel.torrentnotifier;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class CreateDialog extends DialogFragment implements View.OnClickListener {
    
    Button btnCreate;
    EditText etUserName, etPassword;
    String TAG ="pawell";
    FirebaseAuth fireBaseAuth;
   Activity context;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.create_dialog,container,false);
        fireBaseAuth = FirebaseAuth.getInstance();
        Log.i(TAG, "onCreateView:ttttttttt "+fireBaseAuth);

        btnCreate = (Button)view.findViewById(R.id.btn_create);
        btnCreate.setOnClickListener(this);

        etUserName =(EditText)view.findViewById(R.id.craeteUser_ET);
        etPassword =(EditText)view.findViewById(R.id.create_password);

        return view;
    }


    @Override
    public void onClick(View v) {
        if(v==view.findViewById(R.id.btn_create)){
            String username = etUserName.getText().toString();
           String  password = etPassword.getText().toString();
            createNewUser(username, password);
        }
        
    }

    private void createNewUser(String username, String password) {

        fireBaseAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Log.i(TAG, "onComplete: User ADDED");
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    db.getReference().child("users").child(task.getResult().getUser().getUid()).child("ble").setValue("ble").addOnCompleteListener(new OnCompleteListener<Void>() {
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

}
