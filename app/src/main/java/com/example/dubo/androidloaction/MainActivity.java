package com.example.dubo.androidloaction;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private String locationProvider;
    public static final int SHOW_LOCATION = 0, SHOW_LOCATION1 = 1;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getLocation();  //1
        //getLocation1();  //2
       /* Intent intent = new Intent();// 启动服务         //3
        intent.setClass(this, LocationSvc.class);
        startService(intent);*/
        getLocation3();  //4
    }

    private void getLocation3() {
        //获取LocationManager
        LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        /**
         * 参1:选择定位的方式
         * 参2:定位的间隔时间
         * 参3:当位置改变多少时进行重新定位
         * 参4:位置的回调监听
         */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, new LocationListener() {
            //当位置改变的时候调用
            @Override
            public void onLocationChanged(Location location) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                double altitude = location.getAltitude();
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    case LocationProvider.AVAILABLE:
                        break;
                    case LocationProvider.OUT_OF_SERVICE:
                        break;

                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        break;
                }
            }

            //GPS开启的时候调用
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        });
    }

    private void getLocation1() {
        Criteria c = new Criteria();
        c.setPowerRequirement(Criteria.POWER_LOW);//设置耗电量为低耗电
        c.setBearingAccuracy(Criteria.ACCURACY_COARSE);//设置精度标准为粗糙
        c.setAltitudeRequired(false);//设置海拔不需要
        c.setBearingRequired(false);//设置导向不需要
        c.setAccuracy(Criteria.ACCURACY_LOW);//设置精度为低
        c.setCostAllowed(false);//设置成本为不需要
//... Criteria 还有其他属性
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String bestProvider = manager.getBestProvider(c, true);
//得到定位信息
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = null;
        if (!TextUtils.isEmpty(bestProvider)) {
            location = manager.getLastKnownLocation(bestProvider);
        }
        if (null == location){
            //如果没有最好的定位方案则手动配置
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }else  if (manager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){
                location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        if (null == location){
            Log.i("==============", "获取定位失败!");
            return;
        }
        //通过地理编码的到具体位置信息
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.CHINESE);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size()<=0){
            Log.i("==============", "获取地址失败!");
        }
        Address address = addresses.get(0);
        String country = address.getCountryName();//得到国家
        String locality = address.getLocality();//得到城市
        //要获得哪些信息自己看咯
    }

    // 经纬度获取
    private void getLocation() {
        // 获取地理位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
            return;

        }
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                location = locationManager.getLastKnownLocation(locationProvider);
                if (location != null) {
                    //showLocation(location);
                    // 监视地理位置变化
                    locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
                }else {
                    Message msg = new Message();
                    msg.what = SHOW_LOCATION;
                    handler.sendMessage(msg);
                }
            }
        },2000);
    }

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_LOCATION:
                    // 获取Location
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    location = locationManager.getLastKnownLocation(locationProvider);
                    if (location != null) {
                        // 不为空,显示地理位置经纬度
                        //showLocation(location);
                        // 监视地理位置变化
                        locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
                    }else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Message msg1 = new Message();
                                msg1.what = SHOW_LOCATION1;
                                handler.sendMessage(msg1);
                            }
                        },2000);
                    }
                    break;
                case SHOW_LOCATION1:
                    getLocation();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 显示地理位置经度和纬度信息
     *
     * @param location
     */
    private void showLocation(final Location location) {
        Toast.makeText(this, location.getLongitude() + "", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, location.getLatitude() + "", Toast.LENGTH_SHORT).show();
        Log.i("Lot============", location.getLongitude() + "");
        Log.i("Lat==========", location.getLatitude() + "");
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //组装反向地理编码的接口位置
                    StringBuilder url = new StringBuilder();
                    url.append("http://maps.googleapis.com/maps/api/geocode/json?latlng=");
                    url.append(location.getLatitude()).append(",");
                    url.append(location.getLongitude());
                    url.append("&sensor=false");
                    HttpClient client = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(url.toString());
                    httpGet.addHeader("Accept-Language", "zh-CN");
                    HttpResponse response = client.execute(httpGet);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = response.getEntity();
                        String res = EntityUtils.toString(entity);
                        //解析
                        JSONObject jsonObject = new JSONObject(res);
                        //获取results节点下的位置信息
                        JSONArray resultArray = jsonObject.getJSONArray("results");
                        if (resultArray.length() > 0) {
                            JSONObject obj = resultArray.getJSONObject(0);
                            //取出格式化后的位置数据
                            String address = obj.getString("formatted_address");

                            Message msg = new Message();
                            msg.what = SHOW_LOCATION;
                            msg.obj = address;
                            handler.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * LocationListern监听器 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     */

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {
            System.out.print("============status"+status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            System.out.print("============provider"+provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            System.out.print("============provider"+provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            // 如果位置发生变化,重新显示
            //showLocation(location);
            System.out.print("============location"+location);

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            // 移除监听器
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(locationListener);
        }
    }
}
