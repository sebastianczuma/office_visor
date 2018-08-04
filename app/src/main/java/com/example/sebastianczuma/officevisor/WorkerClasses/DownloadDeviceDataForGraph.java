package com.example.sebastianczuma.officevisor.WorkerClasses;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sebastianczuma.officevisor.Activities.DeviceInfo;
import com.example.sebastianczuma.officevisor.Interfaces.UrlAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sebastianczuma on 23.08.2016.
 */
public class DownloadDeviceDataForGraph implements UrlAddress {
    private static final int NOTIFICATION_ID = 1;
    private RequestQueue requestQueue;
    private DeviceInfo appCtx;
    private String token;
    private String buildingName;
    private String floorNumber;
    private String roomName;
    private String deviceName;

    public DownloadDeviceDataForGraph(DeviceInfo context, String token, String buildingName, String floorNumber, String roomName, String deviceName) {
        this.appCtx = context;
        this.token = token;
        this.buildingName = buildingName;
        this.floorNumber = floorNumber;
        this.roomName = roomName;
        this.deviceName = deviceName;
        requestQueue = Volley.newRequestQueue(appCtx);
    }

    public void getData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlGraphData, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("result");
                    Log.d("DANE", result + " / " + result.length());

                    for (int i = result.length() - 1; i >= 0; i--) {
                        Log.d("DANE1", result.getString(i) + "");
                        result.getJSONObject(i);
                        Log.d("DANE2", result.getJSONObject(i).getString("dane") + "");
                        appCtx.data.add(result.getJSONObject(i).getString("dane"));
                        appCtx.date.add(result.getJSONObject(i).getString("data"));
                    }

                    if (result.length() > 0) {
                        appCtx.graphSize = result.length();
                        appCtx.device_data.setText(result.getJSONObject(0).getString("dane") + "°C");
                        appCtx.drawGraph();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DANE", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("token", token);
                hashMap.put("nazwa_budynku", buildingName);
                hashMap.put("numer_poziomu", floorNumber);
                hashMap.put("nazwa_pomieszczenia", roomName);
                hashMap.put("nazwa_urzadzenia", deviceName);
                Log.d("TAG", buildingName + floorNumber + roomName + deviceName);
                return hashMap;
            }
        };
        int socketTimeout = 5000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
    }
}
