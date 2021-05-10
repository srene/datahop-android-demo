package network.datahop.datahopdemo.net.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;

import datahop.BleNativeDriver;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.HashMap;
import java.util.UUID;

import datahop.Datahop;

import static android.bluetooth.le.AdvertiseSettings.ADVERTISE_MODE_BALANCED;
import static android.bluetooth.le.AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM;
import static android.content.Context.BLUETOOTH_SERVICE;

public class BLEAdvertising  implements BleNativeDriver{

    private static final String TAG = BLEAdvertising.class.getSimpleName();
    private BluetoothLeAdvertiser adv;
    private AdvertiseCallback advertiseCallback;
    private BluetoothManager manager;
    private BluetoothAdapter btAdapter;
    private GattServerCallback serverCallback;
    private BluetoothGattServer mBluetoothGattServer;
    private HashMap<UUID,byte[]> advertisingInfo;

    private static volatile BLEAdvertising mBleAdvertising;
    private Context context;
    public BLEAdvertising(Context context){

        manager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        btAdapter = manager.getAdapter();
        this.context = context;
        advertisingInfo = new HashMap();
    }

    // Singleton method
    public static synchronized BLEAdvertising getInstance(Context appContext) {
        if (mBleAdvertising == null) {
            mBleAdvertising = new BLEAdvertising(appContext);
           // initDriver();
        }
        return mBleAdvertising;
    }


    public void start(String parcelUuid) {
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
        startGATTServer(parcelUuid);
    }

    private void startGATTServer(String parcelUuid){

        //Log.d(TAG, "Start server " + hotspot.getNetworkName());

        //stopServer();
        serverCallback = new GattServerCallback(context, parcelUuid,advertisingInfo);
        mBluetoothGattServer = manager.openGattServer(context, serverCallback);
        serverCallback.setServer(mBluetoothGattServer);

        if (mBluetoothGattServer == null) {
            Log.d(TAG, "Unable to create GATT server");
            return;
        }


        ParcelUuid SERVICE_UUID = new ParcelUuid(UUID.nameUUIDFromBytes(parcelUuid.getBytes()));
        BluetoothGattService service = new BluetoothGattService(SERVICE_UUID.getUuid(), BluetoothGattService.SERVICE_TYPE_PRIMARY);


        // Write characteristic
        /*int charnum= (int)Datahop.getAdvertisingUUIDNum();
        for (int i=0;i<charnum;i++){
            String characteristic = Datahop.getAdvertisingUUID(i);
            UUID CHARACTERISTIC_UUID = UUID.nameUUIDFromBytes(characteristic.getBytes());
            Log.d(TAG, "Advertising characteristic " + CHARACTERISTIC_UUID.toString());
            BluetoothGattCharacteristic writeCharacteristic = new BluetoothGattCharacteristic(
                    CHARACTERISTIC_UUID,
                    BluetoothGattCharacteristic.PROPERTY_WRITE,
                    BluetoothGattCharacteristic.PERMISSION_WRITE);
            service.addCharacteristic(writeCharacteristic);
        }*/
        for(UUID uuid:advertisingInfo.keySet()){
            Log.d(TAG, "Advertising characteristic " + uuid.toString());
            BluetoothGattCharacteristic writeCharacteristic = new BluetoothGattCharacteristic(
                    uuid,
                    BluetoothGattCharacteristic.PROPERTY_WRITE,
                    BluetoothGattCharacteristic.PERMISSION_WRITE);
            service.addCharacteristic(writeCharacteristic);
        }
        mBluetoothGattServer.addService(service);

    }

    public void stop() {
        Log.d(TAG, "Stopping ADV");
        adv.stopAdvertising(advertiseCallback);
    }

    @Override
    public void addAdvertisingInfo(String characteristic, byte[] info){
        advertisingInfo.put(UUID.nameUUIDFromBytes(characteristic.getBytes()),info);
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
