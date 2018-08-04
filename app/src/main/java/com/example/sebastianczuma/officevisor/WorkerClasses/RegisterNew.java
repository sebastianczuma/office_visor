package com.example.sebastianczuma.officevisor.WorkerClasses;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sebastianczuma.officevisor.Activities.MainActivity;
import com.example.sebastianczuma.officevisor.Interfaces.UrlAddress;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sebastianczuma on 23.08.2016.
 */
public class RegisterNew implements UrlAddress {
    AlertDialog.Builder builder;
    AlertDialog.Builder builder2;
    private RequestQueue requestQueue;
    private Context appCtx;
    private String email;
    private String name;
    private String surname;
    private String password;

    public RegisterNew(Context context, String email, String name, String surname, String password) {
        this.appCtx = context;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.password = password;

        requestQueue = Volley.newRequestQueue(appCtx);
    }

    public void registerNewUser() {
        loginDialog().show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlRegister, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loginDialog().dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("success")) {

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(appCtx);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("imie", name);
                        editor.putString("token", jsonObject.getString("token"));
                        editor.apply();

                        Toast.makeText(appCtx, jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                        // zamieniono miejscem dwie nastepne linie
                        ((Activity) appCtx).finish();
                        appCtx.startActivity(new Intent(appCtx, MainActivity.class));
                    } else {
                        Toast.makeText(appCtx, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loginDialog().dismiss();
                createDialog(error).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("email", email);
                hashMap.put("name", name);
                hashMap.put("surname", surname);
                hashMap.put("password", password);
                return hashMap;
            }
        };
        int socketTimeout = 5000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    public Dialog createDialog(VolleyError error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(appCtx);
        builder.setMessage("Problem z polaczeniem. Proszę sprawdzić połączenie z Internetem.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public Dialog loginDialog() {
        builder = new AlertDialog.Builder(appCtx);
        builder.setMessage("Tworzenie konta, proszę czekać...");
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public Dialog errorDialog(String error) {
        builder2 = new AlertDialog.Builder(appCtx);
        builder2.setMessage(error)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                });
        // Create the AlertDialog object and return it
        return builder2.create();
    }
}
