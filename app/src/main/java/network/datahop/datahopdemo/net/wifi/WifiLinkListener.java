/*******************************************************
 * Copyright (C) 2020 DataHop Labs Ltd <sergi@datahop.network>
 *
 * This file is part of DataHop Network project.
 *
 * All rights reserved
 *******************************************************/

package network.datahop.datahopdemo.net.wifi;


public interface WifiLinkListener
{

	void wifiLinkConnected(String address);

	void wifiLinkDisconnected();

	void timeout();

}
