package com.rhem.atmosphere;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.gwt20.shared.Constants;
import org.atmosphere.handler.AbstractReflectorAtmosphereHandler;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * This is a simple handler example to show how to use GWT RPC serialization
 *
 * @author p.havelaar
 */
public class GwtRpcAtmosphereHandler extends AbstractReflectorAtmosphereHandler {

    static final Logger logger = Logger.getLogger("AtmosphereHandler");
    @Override
    public void onRequest(AtmosphereResource ar) throws IOException {
        if (ar.getRequest().getMethod().equals("GET") ) {
            doGet(ar);
        } else if (ar.getRequest().getMethod().equals("POST") ) {
            doPost(ar);
        }
    }

    public void doGet(AtmosphereResource ar) {

        // lookup the broadcaster, if not found create it. Name is arbitrary
        ar.setBroadcaster(ar.getAtmosphereConfig().getBroadcasterFactory().lookup("MyBroadcaster", true));

        ar.suspend();
    }

    /**
     * receive push message from client
     **/
    public void doPost(AtmosphereResource ar) {
        Object msg = ar.getRequest().getAttribute(Constants.MESSAGE_OBJECT);
        if (msg != null) {
            logger.info("received RPC post: " + msg.toString());
            // for demonstration purposes we will broadcast the message to all connections
            ar.getAtmosphereConfig().getBroadcasterFactory().lookup("MyBroadcaster").broadcast(msg);
        }
    }


    @Override
    public void destroy() {

    }

}