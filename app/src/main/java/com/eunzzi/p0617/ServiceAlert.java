package com.eunzzi.p0617;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ServiceAlert extends Service {

    private NotificationCompat.Builder Notifi = null;
    private ThreadAlert thread = null;
    private Intent serviceIntent = null;

    private String url = null;
    private RequestQueue requestQueue = null;      // 서버에 요청을 보내는 역할

    private String brac_num, id, name = null;

    private String cctv_lat_long , rec_data = null;

    private Calendar cal = null;

    private int date_distance;
    private double distance;

    // 피보호자를 마지막으로 감지한 cctv의 위.경도와 보호자의 위경도
    private double cctv_latitude, cctv_longitude,guardian_latitude, guardian_longitude;
    private String[] cctv_point, guardian_point = new String[2];


    @Override
    public void onCreate() {
        super.onCreate();
        if (serviceIntent!=null) {
            stopService(serviceIntent);
            serviceIntent = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        brac_num = intent.getStringExtra("brac_num");
        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");

        // 아래에서 설정한 inner 클래스 handler를 객체생성하여 쓰레드에 전달
        ServiceHandler handler = new ServiceHandler();
        thread = new ThreadAlert(handler);
        thread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 위도 경도를 바탕으로 두 지점의 거리를 구하는 매서드
    public double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {

        double theta, dis;

        theta = longitude1 - longitude2;
        dis = Math.sin(deg2rad(latitude1)) * Math.sin(deg2rad(latitude2)) + Math.cos(deg2rad(latitude1))
                * Math.cos(deg2rad(latitude2)) * Math.cos(deg2rad(theta));
        dis = Math.acos(dis);
        dis = rad2deg(dis);

        dis = dis * 60 * 1.1515;
        dis = dis * 1.609344;    // 단위 mile 에서 km 변환.

        return dis;
    }

    // 주어진 도(degree) 값을 라디언으로 변환
    private double deg2rad(double deg) {
        return (double) (deg * Math.PI / (double) 180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad) {
        return (double) (rad * (double) 180d / Math.PI);
    }

    // inner 클래스
    class ServiceHandler extends Handler {

        private PendingIntent pendingIntent;
        private NotificationManager manager;
        private NotificationChannel channel = null;
        private final String CHANNEL_ID = "10001";
        private CharSequence channelName = null;
        private String description = null;
        private int importance;       // 알림 중요도. 값에 따라 진동이나 소리, 단순 메세지로 알릴 것인지 결정된다.

        @Override
        public void handleMessage(android.os.Message msg) {     // 메세지 수신
            intentSetting();
            wardCheck();
        }

        private void intentSetting(){
            serviceIntent = new Intent(ServiceAlert.this, ActivityMenu.class);
            serviceIntent.putExtra("brac_num", brac_num);
            serviceIntent.putExtra("id", id);
            serviceIntent.putExtra("name", name);
            serviceIntent.putExtra("bracelet", true);

            pendingIntent = PendingIntent.getActivity(ServiceAlert.this, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        private void wardCheck(){
            url = ActivityLogin.url_ip + "Alert";

            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // 피보호자의 위치값(피보호자가 마지막으로 체크된 cctv 위치)을 얻어온다
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(ServiceAlert.this);
            }

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        rec_data = jsonObject.getString("rec_data");
                        cctv_lat_long = jsonObject.getString("cctv_lat_long");
                        cal = Calendar.getInstance();

                        // 팔찌의 최근기록을 마크한 CCTV위치를 ShardPreference 변수에 저장. GPS 페이지에서 이 값을 보여준다.
                        ActivityLogin.wardPoint.edit().putString("wardPoint", cctv_lat_long).commit();


                        // 현재시간과 마지막 기록의 시간과의 차이. 분 단위
                        date_distance = (cal.get(Calendar.HOUR_OF_DAY) -  Integer.parseInt(rec_data.substring(8,10))) * 60
                                + (cal.get(Calendar.MINUTE) -  Integer.parseInt(rec_data.substring(10,12)));

                        // 두 지점간의 거리계산. 테스트용 CCTV 위치 0/0
                        cctv_point = cctv_lat_long.split("/");
                        guardian_point = ActivityLogin.guardianPoint.getString("guardianPoint", "30.0/30.0").split("/");

                        cctv_latitude = Double.parseDouble(cctv_point[0]);
                        cctv_longitude = Double.parseDouble(cctv_point[1]);

                        guardian_latitude = Double.parseDouble(guardian_point[0]);
                        guardian_longitude = Double.parseDouble(guardian_point[1]);

                        distance = calculateDistance(cctv_latitude, cctv_longitude, guardian_latitude, guardian_longitude);

                        Log.v("TAG", distance+"");
                        Log.v("TAG", date_distance+"");

                        // 알림 메세지. distance(피보호자와 보호자의 거리)가 설정 거리보다 멀어지거나 마지막으로 기록된 시간이 현재시간보다 30분 이전일 때 알림을 주게된다.
                        // 두 사람의 거리 => 피보호자의 마지막 기록의 카메라 위치값과 보호자가 설정한 위치값의 거리
                        // 피보호자의 마지막 기록의 카메라 위치값 => 비콘으로 값을 구함
                        // 보호자가 설정한 위치값 -> 보호자의 핸드폰 GPS로 값을 구함
                        if (distance >= 10 || date_distance>=30) {
                            showNotifi();
                        }   // 두암동 영암마트와 동강대까지의 거리가 대략 1km (걸어서 5~10분)

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            RequestAlert requestAlert = new RequestAlert(brac_num, url, responseListener);
            requestQueue.add(requestAlert);
        }

        // 상단 알림메세지 설정
        private void showNotifi(){
            Notifi = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setContentTitle("알림!")
                    .setContentText("피보호자의 위치를 확인해주세요")
                    .setSmallIcon(R.drawable.point2)
                    .setTicker("알림!!!")
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                channelName = "HelpServer";
                description = "오레오 이상을 위한 것임";
                importance = NotificationManager.IMPORTANCE_HIGH;       // 알림 중요도. 값에 따라 진동이나 소리, 단순 메세지로 알릴 것인지 결정된다.

                channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
                channel.setDescription(description);

                // 노티피케이션 채널을 시스템에 등록
                assert manager != null;
                manager.createNotificationChannel(channel);

            }
            assert manager != null;

            manager.notify(1, Notifi.build());
        }

    }



}

