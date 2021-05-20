package network.datahop.datahopdemo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

import datahop.Datahop;
import datahop.ConnectionHook;
import network.datahop.datahopdemo.net.ble.BLEAdvertising;
import network.datahop.datahopdemo.net.ble.BLEServiceDiscovery;
import network.datahop.wifidirect.WifiDirectHotSpot;
import network.datahop.wifidirect.WifiLink;
//import wifidriver.WifiConnection;
//import wifidriver.WifiConnectionNotifier;

public class MainActivity extends AppCompatActivity implements ConnectionHook {

    private static final String root = ".datahop";
    private static final String TAG = MainActivity.class.getSimpleName();
    ArrayList<String> activePeers = new ArrayList<>();

    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private static final int PERMISSION_WIFI_STATE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("-----Version :", Datahop.version());
        try {
            BLEServiceDiscovery bleDiscoveryDriver = BLEServiceDiscovery.getInstance(getApplicationContext());
            //bleDiscoveryDriver.setNotifier(Datahop.getBleDiscNotifier());

            BLEAdvertising bleAdvertisingDriver = BLEAdvertising.getInstance(getApplicationContext());
            //bleAdvertisingDriver.setNotifier(Datahop.getBleAdvNotifier());

            WifiDirectHotSpot hotspot = WifiDirectHotSpot.getInstance(getApplicationContext());

            WifiLink connection = WifiLink.getInstance(getApplicationContext());

            Datahop.init(getApplicationContext().getCacheDir() + "/" + root, this, bleDiscoveryDriver, bleAdvertisingDriver, connection, hotspot);

            //hotspot.setNotifier(Datahop.getWifiHotspotNotifier());
            //connection.setNotifier(Datahop.getWifiConnectionNotifier());
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

        requestForPermissions();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permissions " + requestCode + " " + permissions + " " + grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "Location accepted");
                    //timers.setLocationPermission(true);
                    //if(timers.getStoragePermission())startService();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "Location not accepted");

                }
                break;
            }
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "Storage accepted");
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

    private void requestForPermissions() {
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

            final Button button = findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    byte[] array = new byte[7]; // length is bounded by 7
                    new Random().nextBytes(array);
                    String generatedString = new String(array, Charset.forName("UTF-8"));
                    final TextView text = findViewById(R.id.textView);
                    text.setText(generatedString);
                    Log.d(TAG, "Refresh status " + generatedString);
                    // Code here executes on main thread after user presses button
                    Datahop.updateTopicStatus("topic1", generatedString.getBytes());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        Datahop.stop();

        super.onStop();
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



}