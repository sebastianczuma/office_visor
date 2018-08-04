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
public class LoginAuto implements UrlAddress {
    private RequestQueue requestQueue;
    private Context appCtx;
    private String token;

    public LoginAuto(Context context, String token) {
        this.appCtx = context;
        this.token = token;
        requestQueue = Volley.newRequestQueue(appCtx);
    }

    public void autoLoginMe() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlLoginAuto,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("success")) {
                                SharedPreferences preferences
                                        = PreferenceManager.getDefaultSharedPreferences(appCtx);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("token", jsonObject.getString("token"));
                                editor.apply();

                                ((Activity) appCtx).finish();
                                appCtx.startActivity(new Intent(appCtx, MainActivity.class));

                            } else {
                                Toast.makeText(
                                        appCtx,
                                        jsonObject.getString("error"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                createDialog(error).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("token", token);
                return hashMap;
            }
        };
        int socketTimeout = 5000;
        RetryPolicy policy = new DefaultRetryPolicy(
                socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    public Dialog createDialog(VolleyError error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(appCtx);
        builder.setMessage("Problem z polaczeniem. Proszę sprawdzić połączenie z Internetem.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Close Dialog
                    }
                });
        return builder.create();
    }
}
