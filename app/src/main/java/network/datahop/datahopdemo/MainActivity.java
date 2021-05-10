package network.datahop.datahopdemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.UUID;

import datahop.Datahop;
import datahop.ConnectionHook;
import datahop.WifiHook;
import network.datahop.datahopdemo.net.Config;
import network.datahop.datahopdemo.net.DiscoveryListener;
import network.datahop.datahopdemo.net.ble.BleDiscoveryListener;
import network.datahop.datahopdemo.net.ble.BLEAdvertising;
import network.datahop.datahopdemo.net.ble.BLEServiceDiscovery;
import network.datahop.datahopdemo.net.ble.GattServerCallback;
import network.datahop.datahopdemo.net.wifi.HotspotListener;
import network.datahop.datahopdemo.net.wifi.WifiDirectHotSpot;

import static java.util.UUID.nameUUIDFromBytes;

public class MainActivity extends AppCompatActivity implements ConnectionHook,  HotspotListener, BleDiscoveryListener {

    private static final String root = ".datahop";
    private static final String TAG = MainActivity.class.getSimpleName();
    ArrayList<String> activePeers = new ArrayList<>();

 //   WifiDirectHotSpot hotspot;

    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private static final int PERMISSION_WIFI_STATE = 3;
    //private static final int PERMISSION_REQUEST_WIFI_CHANGE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("-----Version :", Datahop.version());
        try {
            BLEServiceDiscovery bleDriver = BLEServiceDiscovery.getInstance(getApplicationContext());
  //          hotspot = new WifiDirectHotSpot(getApplicationContext(), this);
            BLEServiceDiscovery bleDiscoveryDriver = BLEServiceDiscovery.getInstance(getApplicationContext());
            BLEAdvertising bleAdvertisingDriver = BLEAdvertising.getInstance(getApplicationContext());

            Datahop.init(getApplicationContext().getCacheDir() + "/" + root, this, bleDiscoveryDriver,bleAdvertisingDriver);
            // set ble driver
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Node Id", Datahop.getID());
        Log.d("Node Status onCreate", String.valueOf(Datahop.isNodeOnline()));
        if (!Datahop.isNodeOnline()) {
            try {
                Datahop.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //startHotspot();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG,"Permissions "+requestCode+" "+permissions+" "+grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG,"Location accepted");
                    //timers.setLocationPermission(true);
                    //if(timers.getStoragePermission())startService();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG,"Location not accepted");

                }
                break;
            }
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG,"Storage accepted");
                    //timers.setStoragePermission(true);
                    //new CreateWallet(getApplicationContext()).execute();
                    //if(timers.getLocationPermission())startService();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //G.Log(TAG,"Storage not accepted");

                }
        }

        // other 'case' lines to check for other
        // permissions this app might request.

    }

    private void requestForPermissions()
    {
        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);
                }
            });
            builder.show();
        }
        if (this.checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.CHANGE_WIFI_STATE}, PERMISSION_WIFI_STATE);
                }
            });
            builder.show();
        }
        if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            });
            builder.show();
        }

    }


    public void onStart() {
        super.onStart();
        Log.d("Node Status onStart", String.valueOf(Datahop.isNodeOnline()));
        try {
            String Id = Datahop.getID();
            final TextView textViewID = this.findViewById(R.id.textview_id);
            textViewID.setText(Id);

            String addrs = Datahop.getAddress();
            final TextView textViewAddrs = this.findViewById(R.id.textview_address);
            textViewAddrs.setText(addrs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void peerConnected(String s) {
        Log.d("*** Peer Connected ***", s);
        activePeers.add(s);
    }

    @Override
    public void peerDisconnected(String s) {
        Log.d("* Peer Disconnected *", s);
        activePeers.remove(s);
    }

    /*@Override
    public void startHotspot(){

        if (!hotspot.isConnected()) {
            Log.d(TAG, "Job adv finished not connected");
            hotspot.stop(new WifiDirectHotSpot.StartStopListener() {
                public void onSuccess() {
                    Log.d(TAG, "Hotspot stop success");
                    hotspot.start(new WifiDirectHotSpot.StartStopListener() {
                        public void onSuccess() {
                            Log.d(TAG, "Hotspot started");
                        }

                        public void onFailure(int reason) {
                            Log.d(TAG, "Hotspot started failed, error code " + reason);
                        }
                    });
                }

                public void onFailure(int reason) {
                    Log.d(TAG, "Hotspot stop failed, error code " + reason);
                }
            });

        }

    }

    @Override
    public void stopHotspot(){}*/


    @Override
    public void setNetwork(String network, String password) {
        Log.d(TAG, "Set network " + network);
        //serverCallback.setNetwork(network, password);
    }

    @Override
    public void connected() {
        //mDataSharingServer.start();
    }

    @Override
    public void disconnected() {
        //mDataSharingServer.close();
    }


    @Override
    public void peerDiscovered(String device) {
        Datahop.peerDiscovered(device);
    }

    @Override
    public void peerDiscoveredDiffStatus(String network) {
        Datahop.peerDifferentStatusDiscovered(network,null,null);

    }

    @Override
    public void peerDiscoveredSameStatus(String device) {
        Datahop.peerSameStatusDiscovered(device,null,null);

    }
}