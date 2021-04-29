/*******************************************************
 * Copyright (C) 2020 DataHop Labs Ltd <sergi@datahop.network>
 *
 * This file is part of DataHop Network project.
 *
 * All rights reserved
 *******************************************************/

package network.datahop.datahopdemo.net;


public interface LinkListener
{

	void linkNetworkDiscovered(String network);


	void linkNetworkSameDiscovered(String device);

}
