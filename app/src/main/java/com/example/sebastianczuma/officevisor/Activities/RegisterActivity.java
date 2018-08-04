package com.example.sebastianczuma.officevisor.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sebastianczuma.officevisor.R;
import com.example.sebastianczuma.officevisor.WorkerClasses.RegisterNew;

public class RegisterActivity extends AppCompatActivity {
    private EditText email;
    private EditText name;
    private EditText surname;
    private EditText password;
    private EditText re_password;

    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        ctx = this;

        email = (EditText) findViewById(R.id.pole_email);
        name = (EditText) findViewById(R.id.pole_imie);
        surname = (EditText) findViewById(R.id.pole_nazwisko);
        password = (EditText) findViewById(R.id.pole_haslo);
        re_password = (EditText) findViewById(R.id.pole_powtorz_haslo);

        Button register = (Button) findViewById(R.id.przycisk_rejestracja);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog().show();
            }
        });
    }

    public Dialog createDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("zgadzasz się na warunki?")
                .setPositiveButton("Akceptuję", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String passwordValue = password.getText().toString();
                        if (re_password.getText().toString().equals(passwordValue)) {
                            String emailValue = email.getText().toString();
                            String nameValue = name.getText().toString();
                            String surnameValue = surname.getText().toString();

                            RegisterNew registerNew = new
                                    RegisterNew(
                                    ctx,
                                    emailValue,
                                    nameValue,
                                    surnameValue,
                                    passwordValue);
                            registerNew.registerNewUser();
                        } else {
                            Toast.makeText(ctx, "Hasła muszą być zgodne", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //?
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}