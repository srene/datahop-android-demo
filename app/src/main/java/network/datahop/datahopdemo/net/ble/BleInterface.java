package network.datahop.datahopdemo.net.ble;

import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.UUID;

import bertybridge.Bertybridge;
import bertybridge.NativeBleDriver;
import bertybridge.ProximityTransport;

// BleInterface implements the Golang NativeDriver interface
// berty/go/internal/proximitytransport/nativedriver.go
public class BleInterface implements NativeBleDriver {
    private static final String TAG = "bty.ble.BleInterface";

    public static final String DefaultAddr = "/ble/Qmeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee";
    public static final int ProtocolCode = 0x0042;
    public static final String ProtocolName = "ble";

    private Context mContext;
    private static ProximityTransport mTransport;
    private BLEServiceDiscovery mBleDriver;

    public BleInterface(Context context) {
        mContext = context;
    }

    public void start(String localPID) {
        Log.d(TAG, "start driver");

        this.mTransport = Bertybridge.getProximityTransport(ProtocolName);
        if (mTransport == null) {
            Log.e(TAG, "proximityTransporter not found");
            return ;
        }

        if ((this.mBleDriver = BLEServiceDiscovery.getInstance(mContext)) == null) {
            Log.e(TAG, "can't get BleDriver instance");
            return ;
        }
        this.mBleDriver.start(new ParcelUuid(UUID.nameUUIDFromBytes(localPID.getBytes())));
    }

    public void stop() {
        if (this.mBleDriver != null) {
            this.mBleDriver.stop();
            this.mBleDriver = null;
        }
        mTransport = null;
    }

    /*public boolean dialPeer(String remotePID) {
        if (PeerManager.get(remotePID) != null) {
            return true;
        }
        return false;
    }

    public boolean sendToPeer(String remotePID, byte[] payload) {
        if (this.mBleDriver != null) {
            return this.mBleDriver.SendToPeer(remotePID, payload);
        }
        return false;
    }*/

    public void closeConnWithPeer(String remotePID) {
        DeviceManager.closeDeviceConnection(remotePID);
    }

    public long protocolCode() {
        return ProtocolCode;
    }

    public String protocolName() {
        return ProtocolName;
    }

    public String defaultAddr() {
        return DefaultAddr;
    }

    public static boolean BLEHandleFoundPeer(String remotePID) {
        if (mTransport != null) {
            return mTransport.handleFoundPeer(remotePID);
        }
        return false;
    }

    public static void BLEHandleLostPeer(String remotePID) {
        if (mTransport != null) {
            mTransport.handleLostPeer(remotePID);
        }
    }

    public static void BLEReceiveFromPeer(String remotePID, byte[] payload) {
        if (mTransport != null) {
            mTransport.receiveFromPeer(remotePID, payload);
        }
    }
}
