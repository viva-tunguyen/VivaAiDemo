package com.example.vivaaidemo.demo.presentation.demo.face.detail.detect;

import static android.content.Context.WIFI_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.vivaaidemo.R;
import com.example.vivaaidemo.demo.common.BaseViewModel;

import org.apache.commons.validator.routines.InetAddressValidator;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vcc.viva.ai.bin.VccAiResult;
import vcc.viva.ai.bin.callback.VccAiRequestCallback;
import vcc.viva.ai.bin.entity.face.DetectFace;

public class FaceDetectViewModel extends BaseViewModel {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private static final int REQUEST_CODE_ENABLE_LOCATION = 201;
    private final MutableLiveData<String> message;
    private final MutableLiveData<Network> network;
    private final MutableLiveData<Location> location;
    private final MutableLiveData<DetectFace> data;
    private final MutableLiveData<Boolean> progress;
    private final MutableLiveData<List<String>> permissions;

    private final List<String> requiredPermissions = new ArrayList<String>() {
        {
            add(Manifest.permission.READ_EXTERNAL_STORAGE);
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            add(Manifest.permission.ACCESS_COARSE_LOCATION);
            add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    };

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public FaceDetectViewModel() {
        this.data = new MutableLiveData<>();
        this.network = new MutableLiveData<>();
        this.location = new MutableLiveData<>();
        this.message = new MutableLiveData<>();
        this.progress = new MutableLiveData<>(false);
        this.permissions = new MutableLiveData<>();
    }

    /* **********************************************************************
     * Function
     ********************************************************************** */
    protected void clear() {
        data.postValue(null);
        network.postValue(null);
        location.postValue(null);
        message.postValue(null);
        progress.postValue(null);
        permissions.postValue(null);
    }

    public MutableLiveData<DetectFace> getData() {
        return data;
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public MutableLiveData<Network> getNetwork() {
        return network;
    }

    public MutableLiveData<Location> getLocation() {
        return location;
    }

    public MutableLiveData<Boolean> getProgress() {
        return progress;
    }

    public MutableLiveData<List<String>> getPermissions() {
        return permissions;
    }

    public void faceDetect(Context context, String link, String wifi, String location) {
        if (TextUtils.isEmpty(link)) {
            message.setValue("Please fill some text");
            return;
        }

        progress.postValue(true);
        VccAiRequestCallback callback = new VccAiRequestCallback() {
            @Override
            public void success(VccAiResult result) {
                progress.postValue(false);
                try {
                    if(result.getData() != null) {
                        DetectFace data = (DetectFace) result.getData();
                        FaceDetectViewModel.this.data.postValue(data);
                    }
                    message.postValue(result.getMessage());
                } catch (Exception e) {
                    message.setValue(e.getMessage());
                }
            }

            @Override
            public void fail(int code, String msg) {
                progress.postValue(false);
                message.postValue(String.format("Error[%s] : %s", code, msg));
            }
        };
        if (checkPermission(context)) {
            manager.runBackground(() -> manager.face().faceDetect(callback, link, wifi, location));
        }
    }

    public void setLocation(Context context, Activity activity) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d("ntt", "onLocationChanged: " + location);
                    FaceDetectViewModel.this.location.postValue(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(@NonNull String provider) {
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                }
            };
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
            }
        }
        else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ENABLE_LOCATION);

            Location empty = new Location(LocationManager.NETWORK_PROVIDER);
            empty.setLatitude(0);
            empty.setLongitude(0);
            FaceDetectViewModel.this.location.postValue(empty);
        }
    }

    public void setNetwork(Context context) {
        String wifiname;
        Resources res = context.getResources();
        try {
            final ConnectivityManager connMgr = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            final NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifi != null && wifi.isConnectedOrConnecting()) {
                WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = manager.getConnectionInfo();
                if (wifiInfo != null) {
                    NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                    if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                        wifiname = wifiInfo.getSSID();
                        if (wifiname.startsWith("\"") && wifiname.endsWith("\""))
                            wifiname = wifiname.substring(1, wifiname.length() - 1);
                        String wifiIp = Formatter.formatIpAddress(wifiInfo.getIpAddress());
                        String wifiMac = wifiInfo.getBSSID();
                        Network networkInfo = new Network(wifiname, wifiIp, wifiMac);
                        network.postValue(networkInfo);
                    }
                }
            } else if (mobile != null && mobile.isConnectedOrConnecting()) {
                TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String carrierName = manager.getNetworkOperatorName();
                Pair<String, String> mobileNetwork;
                switch (mobile.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                    case TelephonyManager.NETWORK_TYPE_GSM:
                        mobileNetwork = new Pair<>(carrierName, FaceDetectFragment.CellularType.M2G.getTag());
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        mobileNetwork = new Pair<>(carrierName, FaceDetectFragment.CellularType.M4G.getTag());
                        break;
                    case TelephonyManager.NETWORK_TYPE_NR:
                        mobileNetwork = new Pair<>(carrierName, FaceDetectFragment.CellularType.M5G.getTag());
                        break;
                    default:
                        mobileNetwork = new Pair<>(carrierName, FaceDetectFragment.CellularType.M3G.getTag());
                        break;
                }
                String mobileName = String.format("%s %s", mobileNetwork.first, mobileNetwork.second);
                String mobileIp = getMobileIp(true);
                Network networkInfo = new Network(mobileName, mobileIp, "");
                network.postValue(networkInfo);
            } else {
                Network networkInfo = new Network(res.getString(R.string.unvailable_network), res.getString(R.string.unvailable_network), res.getString(R.string.unvailable_network));
                network.postValue(networkInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getMobileIp(boolean useIPv4) {
        try {
            List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : networkInterfaces) {
                List<InetAddress> inetAddresses = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress inetAddress : inetAddresses) {
                    if (!inetAddress.isLoopbackAddress()) {
                        String sAddr = inetAddress.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressValidator.getInstance().isValidInet4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4) {
                                return sAddr;
                            }
                        } else {
                            if (!isIPv4) {
                                // drop ip6 port suffix
                                int delim = sAddr.indexOf('%');
                                String address = delim < 0 ? sAddr : sAddr.substring(0, delim);
                                return address;
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    private boolean checkPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            boolean isGrantFile = Environment.isExternalStorageManager();
            if (!isGrantFile) {
                permissions.postValue(new ArrayList<String>() {{
                    add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
                }});
                progress.postValue(false);
            }
            return isGrantFile;
        } else {
            boolean result = true;
            List<String> missingPermission = new ArrayList<>();
            for (String permission : requiredPermissions) {
                boolean isGrant = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
                result &= isGrant;
                if (!isGrant) {
                    missingPermission.add(permission);
                }
            }
            if (missingPermission.size() > 0) {
                permissions.postValue(missingPermission);
                progress.postValue(false);
            }
            return result;
        }
    }

    /* **********************************************************************
     * Class
     ********************************************************************** */
    public static class Network {
        private String name;
        private String ipAddress;
        private String macAddress;

        public Network(String name, String ipAddress, String macAddress) {
            this.name = name;
            this.ipAddress = ipAddress;
            this.macAddress = macAddress;
        }

        public String getName() {
            return name;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public String getMacAddress() {
            return macAddress;
        }
    }
}
