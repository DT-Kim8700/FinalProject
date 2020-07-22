package com.eunzzi.p0617;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class FragmentGps extends Fragment implements OnMapReadyCallback{

    private FragmentManager fragmentManager = null;
    private MapFragment gps_map = null;

    private Button gps_btn = null;
    private ImageButton gps_alert = null;

    private GoogleMap map = null;
    private MarkerOptions locationMarker1, locationMarker2 = null;
    private LatLng curPoint, wardCurPoint = null;

    private double latitude,longitude, wardLatitude, wardLongitude;
    private String guardianPoint, wardPoint = null;

    private String brac_num;

    private String url = null;
    private RequestQueue requestQueue = null;      // 서버에 요청을 보내는 역할

    static View view;

    public FragmentGps(String brac_num) {
        this.brac_num = brac_num;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        try{
            view = inflater.inflate(R.layout.fragment_gps, container, false);

        }catch (InflateException e){
        }

        viewSetting();

        gps_map.getMapAsync(this);

        // 팔찌 역할을 하는 핸드폰은 서비스 기능을 통해 값을 서버로 수시로 전달해야한다.
        // 버튼을 누르면 팔찌의 위치와 보호자의 위치를 표시
        // 서버에 등록된 보호자의 위치와 팔찌간의 거리를 계산해준다.
        // 보호자의 위치는 등록 형식으로 저장하고 원할 떄 갱신할 수 있게 해준다.
        gps_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 위치 파악
                guardianPointUpload();

            }
        });

        // 해당 버튼을 누르면 바로 신고 접수가 된다.
        gps_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveCall();
            }
        });

        return view;
    }

    public void onResume() {

        super.onResume();

        if (map != null) {
            map.setMyLocationEnabled(true);
        }
    }

    public void onPause() {

        super.onPause();

        if(map != null){
            map.setMyLocationEnabled(false);
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

    private void viewSetting(){
        fragmentManager = getActivity().getFragmentManager();
        gps_map = (MapFragment)fragmentManager.findFragmentById(R.id.gps_map);
        gps_btn = view.findViewById(R.id.gps_btn);
        gps_alert = view.findViewById(R.id.gps_alert);
    }

    private void guardianPointUpload(){
        guardianPoint = Double.toString(latitude) + "/" + Double.toString(longitude);

        // ShardPreference 로 핸드폰에 보호자 위치 자체 저장
        ActivityLogin.guardianPoint.edit().putString("guardianPoint", guardianPoint).commit();
        Toast.makeText(getContext(), "위치가 갱신되었습니다", Toast.LENGTH_SHORT).show();
    }

    private void moveCall(){
        url = ActivityLogin.url_ip + "Call";

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getActivity());
        }

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        RequestCall requestCall = new RequestCall(brac_num, url, responseListener);
        requestQueue.add(requestCall);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:062-613-6444"));
        startActivity(intent);
    }


    // 구글맵이 준비되면 호출된다.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        startLocationService();
        map.setMyLocationEnabled(true);
    }

    public void startLocationService(){

        // 1. 위치 관리자 객체 참조
        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);      // LocationManager 객체 참조


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);     // 이전에 확인했던 위치 정보 가져오기

        if(location != null){
            latitude = location.getLatitude();          // 위도
            longitude = location.getLongitude();         // 경도
        }

        // 3. 위치 정보 업데이트 요청
        GPSListener gpsListener = new GPSListener();        // 리스너 객체 생성
        long mintime = 5000;   // 5초
        float minDistance = 0;

        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mintime, minDistance, gpsListener);
        // 위치 요청하기 . mintime(10초) 마다 위치정보를 전달 받는다.

    }

    // 위치 리스너 클래스를 내부 클래스로 선언
    class GPSListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {      // 위치가 확인되었을 때 자동으로 호출되는 onLocationChanged() 매서드
            wardPoint = ActivityLogin.wardPoint.getString("wardPoint", "35.169901/126.924019");
            wardLatitude = Double.parseDouble(wardPoint.split("/")[0]);
            wardLongitude = Double.parseDouble(wardPoint.split("/")[1]);

            latitude = location.getLatitude();          // 위도
            longitude = location.getLongitude();         // 경도

            showCurrentLocation(latitude,longitude,wardLatitude,wardLongitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }

    }

    private void showCurrentLocation(double latitude, double longitude, double wardLatitude, double wardLongitude){

        curPoint = new LatLng(latitude,longitude);       // 현재 위치의 좌표로 LatLng 객체 생성
        // LatLng : 경위도 좌표로 구성된 위치를 지도에 표시할 수 있도록 정의된 클래스.
        //      - 지구 상의 특정위치를 표현하거나 구글맵 객체의 animateCamera() 메서드를 이용해서 그 위치를 중심으로 지도를 보여줄 수도 있다.

        wardCurPoint = new LatLng(wardLatitude, wardLongitude);

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(wardCurPoint,15));      // 지정한 위치의 지도 영역 보여주기

        showLocationMarker(curPoint, wardCurPoint);

    }

    private void showLocationMarker(LatLng point1 , LatLng point2){
        if(locationMarker1 == null){
            locationMarker1 = new MarkerOptions();   // 마커 객체 생성
            locationMarker1.position(point1);
            locationMarker1.title("내 위치");
            locationMarker1.snippet("GPS가 확인한 위치");
            locationMarker1.icon(BitmapDescriptorFactory.fromResource(R.drawable.point1));
            map.addMarker(locationMarker1);
        }else {
            locationMarker1.position(point1);
        }

        if(locationMarker2 == null){
            locationMarker2 = new MarkerOptions();   // 마커 객체 생성
            locationMarker2.position(point2);
            locationMarker2.title("내 위치");
            locationMarker2.snippet("GPS가 확인한 위치");
            locationMarker2.icon(BitmapDescriptorFactory.fromResource(R.drawable.point2));
            map.addMarker(locationMarker2);
        }else {
            locationMarker2.position(point2);
        }
    }
}

