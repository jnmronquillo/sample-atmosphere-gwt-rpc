package com.rhem.atmosphere;

import org.atmosphere.gwt20.client.GwtRpcClientSerializer;
import org.atmosphere.gwt20.client.GwtRpcSerialTypes;

/**
 *
 * @author jotec
 */
@GwtRpcSerialTypes({BaseEvent.class, EventBar.class})
abstract public class RPCSerializer extends GwtRpcClientSerializer {

}