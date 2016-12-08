package com.basicelixir.pawel.torrentnotifier;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.target.Target;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class FacebookLogginDialog extends DialogFragment {

    String TAG = "pawell";
    private FirebaseAuth fireBaseAuth;
    private View view;
    private CallbackManager callbackManager;
    private LoginButton faceBookBtn;
    private FirebaseDatabase firebaseDatabase;
    private AccessTokenTracker accessTokenTracker;
    private FacebookLogOutListener faceBookLogOutLisetener;
    private SharedPreferences sharedPreferences;
    private Context context;
    private ImageView loginIv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_log_in, container, false);
        faceBookBtn = (LoginButton) view.findViewById(R.id.faceBook_login_button);
        loginIv =(ImageView)view.findViewById(R.id.loginBg);

        Glide.with(this)
                .load(R.drawable.login)
                .asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(loginIv);
        fireBaseAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        firebaseDatabase = FirebaseDatabase.getInstance();
        faceBookLogOutLisetener = (MainActivity) getActivity();

        faceBookBtn.setReadPermissions("email", "public_profile");
        faceBookBtn.setFragment(this);
        faceBookBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handelFacebookAccesToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    fireBaseAuth.signOut();
                    faceBookLogOutLisetener.logOutListener(true);
                }
                dismiss();
                FacebookLogginDialog.this.dismiss();
            }
        };
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void handelFacebookAccesToken(AccessToken accessToken) {
        Log.i(TAG, "handelFacebookAccesToken: " + accessToken.getToken());

        AuthCredential credentials = FacebookAuthProvider.getCredential(accessToken.getToken());
        fireBaseAuth.signInWithCredential(credentials).addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                sharedPreferences = context.getSharedPreferences("prefTorrentNot", Context.MODE_PRIVATE);
                if (!sharedPreferences.getBoolean("ALLREADY_CREATED", false)) {
                    sharedPreferences.edit().putBoolean("ALLREADY_CREATED", true).apply();
                    Log.i(TAG, "onComplete: " + task.isSuccessful());
                    firebaseDatabase.getReference().child("users").child(task.getResult().getUser().getUid()).child("ble").setValue("ble");
                    firebaseDatabase.getReference().child("list").child(task.getResult().getUser().getUid()).child("email").setValue(task.getResult().getUser().getEmail());
                    firebaseDatabase.getReference().child("users").child(task.getResult().getUser().getUid()).child("eligebleForChat").setValue(true);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}

