package com.mad.snapoverflow.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.support.annotation.NonNull;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.snapoverflow.R;
import com.mad.snapoverflow.model.UsersSignupModel;
import com.mad.snapoverflow.view.Activities.LoginActivity;

public class SignUpViewModel extends BaseObservable {

    private Context mContext;
    public String mEmail, mPassword, mUniversity, mAoi, mDate, mUsername;
    private UsersSignupModel users;
    private EditText mEmailET, mPassET;
    private ProgressBar mProgressSign;
    private FirebaseAuth mAuth;
    private static final String USERS = "Users";

    public SignUpViewModel(Context context, String email, String password, String aoi, String date, String uni,
                           String username, ProgressBar progress, EditText emailET, EditText passET) {
        mContext = context;
        mEmail = email;
        mPassword = password;
        mUniversity = uni;
        mAoi = aoi;
        mDate = date;
        mUsername = username;
        mProgressSign = progress;
        mEmailET = emailET;
        mPassET = passET;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
        notifyPropertyChanged(R.id.textEmailReg);
    }

    public String getPassword() {
        return mPassword;

    }

    public void setPassword(String password) {
        mPassword = password;
        notifyPropertyChanged(R.id.textPasswordReg);
    }

    public String getUniversity() {
        return mUniversity;
    }

    public void setUniversity(String university) {
        mUniversity = university;
        notifyPropertyChanged(R.id.textUniversity);
    }

    public String getAoi() {
        return mAoi;
    }

    public void setAoi(String aoi) {
        mAoi = aoi;
        notifyPropertyChanged(R.id.textAoI);
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
        notifyPropertyChanged(R.id.textDate);
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
        notifyPropertyChanged(R.id.textUsername);
    }



    private void registerUser() {
        final String email = getEmail();
        final String password = getPassword();
        final String university = getUniversity();
        final String aoi = getAoi();
        final String date = getDate();
        final String username = getUsername();

        if (email.isEmpty()) {
            mEmailET.setError(mContext.getResources().getString(R.string.email_error));
            mEmailET.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailET.setError(mContext.getResources().getString(R.string.email_valid));
            mEmailET.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            mPassET.setError(mContext.getResources().getString(R.string.password_error));
            mPassET.requestFocus();
            return;
        }

        if (password.length() < 6) {
            mPassET.setError(mContext.getResources().getString(R.string.min_string));
            mPassET.requestFocus();
            return;
        }


        mProgressSign.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mProgressSign.setVisibility(View.GONE);
                if (task.isSuccessful()) {

                    UsersSignupModel users = new UsersSignupModel (
                            username,
                            email,
                            password,
                            university,
                            aoi,
                            date
                    );

                    FirebaseDatabase.getInstance().getReference(USERS)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                launchRegisteredActivity();
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.reg_toast_one), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.reg_toast_two), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }


    public View.OnClickListener onClickRegister(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        };
    }

    public View.OnClickListener onClickLogin(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLoginActivity();
            }
        };
    }

    public void launchRegisteredActivity(){
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }


    public void launchLoginActivity() {
        mContext.startActivity(new Intent(mContext, LoginActivity.class));
    }

}