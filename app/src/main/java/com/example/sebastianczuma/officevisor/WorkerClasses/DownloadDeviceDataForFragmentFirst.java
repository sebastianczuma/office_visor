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
import com.example.sebastianczuma.officevisor.Fragments.FragmentFirst;
import com.example.sebastianczuma.officevisor.Interfaces.UrlAddress;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sebastianczuma on 23.08.2016.
 */
public class DownloadDeviceDataForFragmentFirst implements UrlAddress {
    private static FragmentFirst fragmentFirst;

    public static void getData(final FragmentFirst context, final String token, final String buildingName, final String floorNumber, final String roomName, final String deviceName) {
        RequestQueue requestQueue = Volley.newRequestQueue(context.getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlCallLogUpload, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("success")) {

                        FragmentFirst fragmentFirst = context;

                        fragmentFirst.dataArray.put(deviceName, jsonObject.get("data").toString());
                        fragmentFirst.dataSetChanged();

                    } else {
                        Toast.makeText(context.getContext(), jsonObject.getString("error") + "asas", Toast.LENGTH_SHORT).show();
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
