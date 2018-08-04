package com.example.sebastianczuma.officevisor.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.sebastianczuma.officevisor.R;
import com.example.sebastianczuma.officevisor.WorkerClasses.LoginAuto;
import com.example.sebastianczuma.officevisor.WorkerClasses.LoginManual;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Context ctx = this;

        final EditText login = (EditText) findViewById(R.id.login);
        final EditText password = (EditText) findViewById(R.id.haslo);
        Button logmein = (Button) findViewById(R.id.zaloguj);
        Button register = (Button) findViewById(R.id.zarejestruj);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, RegisterActivity.class));
            }
        });

        logmein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String loginValue = login.getText().toString();
                String passwordValue = password.getText().toString();

                LoginManual loginManual = new LoginManual(ctx, loginValue, passwordValue);
                loginManual.LogMeIn();
            }
        });

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = preferences.getString("token", "");

        if (!token.equals("")) {
            LoginAuto loginAuto = new LoginAuto(ctx, token);
            loginAuto.autoLoginMe();
        }
        //bypassLogin();
    }

    private void bypassLogin() {
        startActivity(new Intent(this, MainActivity.class));
    }
}