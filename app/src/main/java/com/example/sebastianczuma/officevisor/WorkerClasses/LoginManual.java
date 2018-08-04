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
public class LoginManual implements UrlAddress {
    AlertDialog.Builder builder;
    AlertDialog.Builder builder2;
    private RequestQueue requestQueue;
    private Context appContext;
    private String login;
    private String password;

    public LoginManual(Context context, String login, String password) {
        this.appContext = context;
        this.login = login;
        this.password = password;
        requestQueue = Volley.newRequestQueue(appContext);
    }

    public void LogMeIn() {
        loginDialog().show();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        final SharedPreferences.Editor editor = preferences.edit();
        StringRequest stringRequest = new StringRequest
                (Request.Method.POST, urlLoginManual, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loginDialog().dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("success")) {
                                String imie = jsonObject.getString("name");
                                editor.putString("imie", imie);
                                editor.putString("token", jsonObject.getString("token"));
                                editor.apply();

                                loginDialog().dismiss();

                                Toast.makeText(appContext, "Witaj " + imie, Toast.LENGTH_SHORT).show();
                                ((Activity) appContext).finish();
                                appContext.startActivity(new Intent(appContext, MainActivity.class));
                            } else {
                                errorDialog(jsonObject.getString("error"));
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
                hashMap.put("email", login);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
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
        builder = new AlertDialog.Builder(appContext);
        builder.setMessage("Logowanie, proszę czekać...");
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public Dialog errorDialog(String error) {
        builder2 = new AlertDialog.Builder(appContext);
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
