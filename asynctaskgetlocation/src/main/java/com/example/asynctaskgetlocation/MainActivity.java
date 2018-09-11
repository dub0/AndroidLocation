package com.example.asynctaskgetlocation;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String provider;//位置提供器
    private LocationManager locationManager;//位置服务
    private Location location;
    private TextView get_location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);//获得位置服务
        get_location = (TextView) findViewById(R.id.get_location);
        get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(MainActivity.this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
                    return;

                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
            }
        });

        /*provider = judgeProvider(locationManager);
        if (provider != null) {//有位置提供器的情况
            //为了压制getLastKnownLocation方法的警告
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                getLocation(location);//得到当前经纬度并开启线程去反向地理编码
            } else {
                get_location.setText("暂时无法获得当前位置");
            }
        } else {//不存在位置提供器的情况

        }*/
    }

    /**
     * 得到当前经纬度并开启线程去反向地理编码
     */
    public void getLocation(Location location) {
        String latitude = location.getLatitude() + "";
        String longitude = location.getLongitude() + "";
        String url = "http://api.map.baidu.com/geocoder/v2/?ak=pPGNKs75nVZPloDFuppTLFO3WXebPgXg&callback=renderReverse&location=" + latitude + "," + longitude + "&output=json&pois=0";
        new MyAsyncTask(url).execute();
    }

    class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        String url = null;//要请求的网址
        String str = null;//服务器返回的数据
        String address = null;

        public MyAsyncTask(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            str = getData(url);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                str = str.replace("renderReverse&&renderReverse", "");
                str = str.replace("(", "");
                str = str.replace(")", "");
                JSONObject jsonObject = new JSONObject(str);
                JSONObject address = jsonObject.getJSONObject("result");
                String city = address.getString("formatted_address");
                String district = address.getString("sematic_description");
                get_location.setText("当前位置：" + city + district);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(aVoid);
        }
    }

    /**
     * 判断是否有可用的内容提供器
     *
     * @return 不存在返回null
     */
    private String judgeProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if (prodiverlist.contains(LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;
        } else if (prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        } else {
            Toast.makeText(MainActivity.this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    ////////////////////
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            // 如果位置发生变化,重新显示
            getLocation(location);

        }
    };

    String str = null;//网路请求往回的数据

    public static String getData(String url) {//url网路请求的网址
        URL u = null;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection hc = null;
        InputStream inputStream = null;
        StringBuffer sb = null;
        BufferedReader br = null;
        try {
            hc = (HttpURLConnection) u.openConnection();
            hc.setRequestMethod("GET");
            inputStream = hc.getInputStream();
            sb = new StringBuffer();
            br = new BufferedReader(new InputStreamReader(inputStream));
            String len = null;
            while ((len = br.readLine()) != null) {
                sb.append(len);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            // 移除监听器
            locationManager.removeUpdates(locationListener);
        }
    }
}
