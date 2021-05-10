/*******************************************************
 * Copyright (C) 2020 DataHop Labs Ltd <sergi@datahop.network>
 *
 * This file is part of DataHop Network project.
 *
 * All rights reserved
 *******************************************************/

package network.datahop.datahopdemo.net.ble;


public interface BleDiscoveryListener
{

	void peerDiscovered(String device);

	void peerDiscoveredDiffStatus(String network);

	void peerDiscoveredSameStatus(String device);

}
