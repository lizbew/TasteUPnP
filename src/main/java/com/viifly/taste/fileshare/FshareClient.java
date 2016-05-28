package com.viifly.taste.fileshare;

import com.viifly.taste.AbstractDiscoveryClient;
import com.viifly.taste.GenericQueryAction;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;

public class FshareClient extends AbstractDiscoveryClient {
    public static void main(String[] args) {
        FshareClient client = new FshareClient();
        new Thread(client).start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public RegistryListener createRegistryListener(final UpnpService upnpService) {
        return new DefaultRegistryListener(){
            ServiceId serviceId = new UDAServiceId(FshareConstants.UPLOAD_SERVICE_ID);

            @Override
            public void remoteDeviceAdded(Registry registry, RemoteDevice device) {

                Service uploadService;
                if ((uploadService = device.findService(serviceId)) != null) {

                    System.out.println("Service discovered: " + uploadService);
                    executeAction(upnpService, uploadService);
                }
            }
        };
    }

    private void executeAction(UpnpService upnpService, Service uploadService) {
        upnpService.getControlPoint().execute(new GenericQueryAction(uploadService, "GetUploadUrl"));
    }
}
