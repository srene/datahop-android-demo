package network.datahop.datahopdemo.net.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.HashMap;
import java.util.UUID;

import static android.bluetooth.le.AdvertiseSettings.ADVERTISE_MODE_BALANCED;
import static android.bluetooth.le.AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM;
import static android.content.Context.BLUETOOTH_SERVICE;

public class BLEAdvertising {

    private static final String TAG = BLEAdvertising.class.getSimpleName();
    private BluetoothLeAdvertiser adv;
    private AdvertiseCallback advertiseCallback;
    private BluetoothManager manager;
    private BluetoothAdapter btAdapter;
    private static volatile BLEAdvertising mBleAdvertising;

    public BLEAdvertising(Context context){

        manager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        btAdapter = manager.getAdapter();

    }

    // Singleton method
    public static synchronized BLEAdvertising getInstance(Context appContext) {
        if (mBleAdvertising == null) {
            mBleAdvertising = new BLEAdvertising(appContext);
           // initDriver();
        }
        return mBleAdvertising;
    }


    public void startAdvertising(String parcelUuid) {
        Log.d(TAG, "Starting ADV, Tx power " + parcelUuid.toString());
        if (btAdapter != null) {
            if (btAdapter.isMultipleAdvertisementSupported()) {
                Log.d(TAG, "Starting ADV2, Tx power " + parcelUuid.toString());
                adv = btAdapter.getBluetoothLeAdvertiser();
                advertiseCallback = createAdvertiseCallback();
                ParcelUuid mServiceUUID = new ParcelUuid(UUID.nameUUIDFromBytes(parcelUuid.getBytes()));

                AdvertiseSettings advertiseSettings = new AdvertiseSettings.Builder()
                        .setAdvertiseMode(ADVERTISE_MODE_BALANCED)
                        .setTxPowerLevel(ADVERTISE_TX_POWER_MEDIUM)
                        .setConnectable(true)
                        .build();

                AdvertiseData advertiseData = new AdvertiseData.Builder()
                        //          .addManufacturerData(0, stats.getUserName().getBytes())
                        .addServiceUuid(mServiceUUID)
                        .setIncludeTxPowerLevel(false)
                        .setIncludeDeviceName(false)
                        .build();
                adv.startAdvertising(advertiseSettings, advertiseData, advertiseCallback);
            }
        }
       // Log.d(TAG, "Name length " + stats.getUserName().getBytes().length + " " + advertiseData);

    }

    public void stopAdvertising() {
        Log.d(TAG, "Stopping ADV");
        adv.stopAdvertising(advertiseCallback);
    }

    private AdvertiseCallback createAdvertiseCallback() {
        return new AdvertiseCallback() {
            @Override
            public void onStartFailure(int errorCode) {

                switch (errorCode) {
                    case ADVERTISE_FAILED_DATA_TOO_LARGE:
                        Log.d(TAG,"ADVERTISE_FAILED_DATA_TOO_LARGE");
                        break;
                    case ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                        Log.d(TAG,"ADVERTISE_FAILED_TOO_MANY_ADVERTISERS");
                        break;
                    case ADVERTISE_FAILED_ALREADY_STARTED:
                        Log.d(TAG, "ADVERTISE_FAILED_ALREADY_STARTED");
                        break;
                    case ADVERTISE_FAILED_INTERNAL_ERROR:
                        Log.d(TAG, "ADVERTISE_FAILED_INTERNAL_ERROR");
                        break;
                    case ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                        Log.d(TAG, "ADVERTISE_FAILED_FEATURE_UNSUPPORTED");
                        break;
                    default:
                        Log.d(TAG, "startAdvertising failed with unknown error " + errorCode);
                        break;
                }
            }
        };
    }

}
