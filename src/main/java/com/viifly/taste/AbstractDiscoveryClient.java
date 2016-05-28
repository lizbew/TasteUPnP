package com.viifly.taste;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.registry.RegistryListener;

public abstract class AbstractDiscoveryClient implements Runnable{

    @Override
    public void run() {
        try {
            UpnpService upnpService = new UpnpServiceImpl();
            // Add a listener for device registration events
            upnpService.getRegistry().addListener(
                    createRegistryListener(upnpService)
            );

            // Broadcast a search message for all devices
            upnpService.getControlPoint().search(
                    new STAllHeader()
            );

        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            System.exit(1);
        }
    }


    abstract public RegistryListener createRegistryListener(final UpnpService upnpService);
}
