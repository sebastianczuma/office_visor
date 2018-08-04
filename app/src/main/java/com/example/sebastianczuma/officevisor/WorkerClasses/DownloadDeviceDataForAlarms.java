package com.example.sebastianczuma.officevisor.WorkerClasses;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
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
import com.example.sebastianczuma.officevisor.Activities.MainActivity;
import com.example.sebastianczuma.officevisor.Database.DbHandlerDevices;
import com.example.sebastianczuma.officevisor.Interfaces.UrlAddress;
import com.example.sebastianczuma.officevisor.NotificationUpload.UploadEventReceiver;
import com.example.sebastianczuma.officevisor.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sebastianczuma on 23.08.2016.
 */
public class DownloadDeviceDataForAlarms implements UrlAddress {
    private static final int NOTIFICATION_ID = 1;
    private RequestQueue requestQueue;
    private Context appCtx;
    private String token;
    private String buildingName;
    private String floorNumber;
    private String roomName;
    private String deviceName;
    private String deviceType;
    private String endValue = "";
    private int exceededValue;

    public DownloadDeviceDataForAlarms(Context context, String token, String buildingName, String floorNumber, String roomName, String deviceName, String deviceType) {
        this.appCtx = context;
        this.token = token;
        this.buildingName = buildingName;
        this.floorNumber = floorNumber;
        this.roomName = roomName;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        requestQueue = Volley.newRequestQueue(appCtx);
    }

    public void getData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlCallLogUpload, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("success")) {

                        DbHandlerDevices dbhd = new DbHandlerDevices(appCtx);
                        String alarm = dbhd.returnDeviceAlarm(buildingName, floorNumber, roomName, deviceName);
                        if (alarm != null) {
                            if (alarm.equals("włączony")) {

                                try {
                                    int value = Integer.parseInt(jsonObject.get("data").toString());

                                    int maxValue = dbhd.returnDeviceAlarmMaxValue(buildingName, floorNumber, roomName, deviceName);
                                    int minValue = dbhd.returnDeviceAlarmMinValue(buildingName, floorNumber, roomName, deviceName);

                                    if (value < minValue) {
                                        exceededValue = value - minValue;
                                        endValueResolver();
                                        if (dbhd.returnDeviceAlarm(buildingName, floorNumber, roomName, deviceName).equals("włączony")) {
                                            dbhd.updateIsDeviceAlarmActive(buildingName, floorNumber, roomName, deviceName, "tak");
                                            buildNotification();
                                        }
                                    } else if (value > maxValue) {
                                        exceededValue = value - maxValue;
                                        endValueResolver();
                                        if (dbhd.returnDeviceAlarm(buildingName, floorNumber, roomName, deviceName).equals("włączony")) {
                                            dbhd.updateIsDeviceAlarmActive(buildingName, floorNumber, roomName, deviceName, "tak");
                                            buildNotification();
                                        }
                                    }
                                } catch (Exception e) {

                                }
                            } else {
                                String isAlarmActive = dbhd.returnIsDeviceAlarmActive(buildingName, floorNumber, roomName, deviceName);
                                if (isAlarmActive != null) {
                                    if (isAlarmActive.equals("tak")) {
                                        dbhd.updateIsDeviceAlarmActive(buildingName, floorNumber, roomName, deviceName, "");
                                    }
                                }
                            }
                        }
                        dbhd.close();
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

    private void buildNotification() {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(appCtx);
        builder.setAutoCancel(true)
                .setColor(appCtx.getResources().getColor(R.color.colorAccent))
                .setSmallIcon(R.drawable.ic_domain_black_24dp);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Czujnik " + deviceName + " przekroczył zadaną wartość o " + exceededValue + endValue + "\nLokalizacja czujnika:\n" + buildingName + "\n" + "Poziom " + floorNumber + "\n" + roomName);
        bigText.setBigContentTitle("Przekroczona wartość w budynku " + buildingName);
        bigText.setSummaryText("Alert - wartość przekroczona");
        builder.setStyle(bigText);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        PendingIntent pendingIntent = PendingIntent.getActivity(appCtx,
                NOTIFICATION_ID,
                new Intent(appCtx, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setDeleteIntent(UploadEventReceiver.getDeleteIntent(appCtx));

        final NotificationManager manager = (NotificationManager)
                appCtx.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    private void endValueResolver() {
        switch (deviceType) {
            case "Czujnik obecności":
                break;
            case "Czujnik temperatury":
                endValue = "°C";
                break;
            case "Czujnik dymu":
                break;
            case "Czujnik wilgotności":
                endValue = "%";
                break;
            case "Oświetlenie":
                break;
            case "Zamek":
                break;
            case "Czujnik gazu":
                break;
            case "Czujnik wody":
                break;
        }
    }
}
