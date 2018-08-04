package com.example.sebastianczuma.officevisor.WorkerClasses;

import android.util.Log;
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
import com.example.sebastianczuma.officevisor.Activities.RoomActivity;
import com.example.sebastianczuma.officevisor.Interfaces.UrlAddress;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sebastianczuma on 23.08.2016.
 */
public class DownloadDeviceDataForRoomActivity implements UrlAddress {
    private static final int NOTIFICATION_ID = 1;
    private RequestQueue requestQueue;
    private RoomActivity appCtx;
    private String token;
    private String buildingName;
    private String floorNumber;
    private String roomName;
    private String deviceName;

    public DownloadDeviceDataForRoomActivity(RoomActivity context, String token, String buildingName, String floorNumber, String roomName, String deviceName) {
        this.appCtx = context;
        this.token = token;
        this.buildingName = buildingName;
        this.floorNumber = floorNumber;
        this.roomName = roomName;
        this.deviceName = deviceName;
        requestQueue = Volley.newRequestQueue(appCtx);
    }

    public void getData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlCallLogUpload, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("success")) {

                        appCtx.dataArray.put(deviceName, jsonObject.get("data").toString());
                        appCtx.dataSetChanged();


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
