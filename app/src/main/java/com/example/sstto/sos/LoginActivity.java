/**
 * Request permissions to access device features
 * Event handlers for SignIn
 * Initiate GoogleAPI SignIn
 * Handle result from GoogleAPI
 * Created by Istiak
 */
package com.example.sstto.sos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs";
    private static final int RC_SIGN_IN = 1;
    public SignInButton msignin;
    SharedPreferences sharedpreferences;
    GoogleSignInClient mGoogleSignInClient;

    /**
     * First time login
     * Request permission
     * Listen for SignIn
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        /*Fetch log in status from memory*/
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String restoredText = sharedpreferences.getString("loggedin", null);
        msignin = (SignInButton) findViewById(R.id.signinbtn);
        if (restoredText != null)
        {

            Intent homeIntent = new Intent(this, MainActivity.class);
            startActivity(homeIntent);
            finish();
        }
        else
        {
            /*First-time login Request permission*/
            int PERMISSION_ALL = 1;
            String[] PERMISSIONS = {android.Manifest.permission.SEND_SMS,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.BLUETOOTH_PRIVILEGED,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                    android.Manifest.permission.CALL_PHONE,
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.READ_SMS,
                    android.Manifest.permission.CAMERA
            };
            if(!hasPermissions(this, PERMISSIONS)){
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            /*SignIn listener*/
            msignin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });
        }
    }
    /**
     * Initiate Google SignIn
     */
    private void signIn()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Handle Google SignIn output
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                /*Raise Exception on failed login*/
                GoogleSignInAccount account = task.getResult(ApiException.class);
                /*Successful login, set parameter & load main screen*/
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("loggedin", "true");
                editor.commit();
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            } catch (ApiException e) {
                /*Display failure*/
                Log.d("GOOGLE API", e.getMessage ());
                e.printStackTrace();
                Toast.makeText(this, "Authentication failed.",Toast.LENGTH_LONG).show();
            }
        }
    }
    /**
     * Request permission
     */
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
