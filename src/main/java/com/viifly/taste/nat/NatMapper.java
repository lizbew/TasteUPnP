package com.viifly.taste.nat;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;
import org.fourthline.cling.support.igd.PortMappingListener;
import org.fourthline.cling.support.igd.callback.GetExternalIP;
import org.fourthline.cling.support.igd.callback.GetStatusInfo;
import org.fourthline.cling.support.model.Connection;
import org.fourthline.cling.support.model.PortMapping;

import java.net.*;
import java.util.Enumeration;

public class NatMapper implements Runnable {
    protected int mapPort = 8201;
    protected  String mapIP;

    public static void main(String[] args) {
        NatMapper natMapper = new NatMapper();
        natMapper.mapIP = natMapper.getLocalIP();
        new Thread(natMapper).start();

        try {
            while (true) {
                Thread.sleep(5000);
                System.out.println("wait 5s...");
            }
        }catch (InterruptedException ex) {

        }
    }

    @Override
    public void run() {
        PortMapping desiredMapping =
                new PortMapping(
                        this.mapPort,
                        this.mapIP,
                        PortMapping.Protocol.TCP,
                        "My Port Mapping"
                );


        /*
        final UpnpService upnpService =
                new UpnpServiceImpl(
                        new PortMappingListener(desiredMapping)
                );
        */

        final UpnpService upnpService = new UpnpServiceImpl();
        upnpService.getRegistry().addListener(createRegistryListener(upnpService, desiredMapping));

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
               // System.out.println("shutdown UPnP");
                upnpService.shutdown();
            }
        });
        upnpService.getControlPoint().search();
    }

    RegistryListener createRegistryListener(final UpnpService upnpService, final PortMapping desiredMapping) {
        return new PortMappingListener(desiredMapping) {
            @Override
            synchronized public void deviceAdded(Registry registry, Device device) {
                Service connectionService;
                if ((connectionService = discoverConnectionService(device)) == null) return;

                printServiceActionList(connectionService);
                executeAction(upnpService, connectionService);
            }
        };
    }

    private void printServiceActionList(Service service) {
        System.out.println(service);
        if (service.hasActions()) {
            for (Action action : service.getActions()) {
                System.out.println(action);
            }
        }
    }

    private void executeAction(UpnpService upnpService, Service connectionService) {
        //upnpService.getControlPoint().execute(new GenericQueryAction(connectionService, "GetStatusInfo"));
        upnpService.getControlPoint().execute(new GenericQueryAction(connectionService, "GetGenericPortMappingEntry"));
        upnpService.getControlPoint().execute(new GenericQueryAction(connectionService, "GetExternalIPAddress"));


        // GetStatusInfo
        /*
        upnpService.getControlPoint().execute(new GetStatusInfo(connectionService) {

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                System.out.println("GetStatusInfo failure: " + defaultMsg );
            }

            @Override
            protected void success(Connection.StatusInfo statusInfo) {
                System.out.println(String.format("Status: %s, uptimeSeconds: %d, lastError: %s", statusInfo.getStatus(), statusInfo.getUptimeSeconds(), statusInfo.getLastError()));
            }
        });
        */
        // GetExternalIP
        /*
        upnpService.getControlPoint().execute(new GetExternalIP(connectionService) {

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                System.out.println("GetExternalIP failure: " + defaultMsg );
            }

            @Override
            protected void success(String externalIPAddress) {
                System.out.println("externalIPAddress " +  externalIPAddress);
            }
        });
        */
    }

    private String getLocalIP() {
        Enumeration<NetworkInterface> allInterface = null;
        Inet4Address ipAddr = null;
        try {
            allInterface = NetworkInterface.getNetworkInterfaces();
            while (allInterface.hasMoreElements()) {
                NetworkInterface netIf = allInterface.nextElement();

                //System.out.println(String.format("%s, %s", netIf.getName(), netIf.getDisplayName()) );

                Enumeration<InetAddress> addressEnumeration = netIf.getInetAddresses();
                while (addressEnumeration.hasMoreElements()) {
                    InetAddress addr = addressEnumeration.nextElement();
                    if (addr != null && addr instanceof Inet4Address){
                        //System.out.println("loopback " + netIf.isLoopback() + ", " +netIf.isUp()+ ", " +netIf.isVirtual());
                        //System.out.println(addr.getHostAddress());
                        if (!netIf.isLoopback() && !netIf.getDisplayName().contains("VirtualBox")) {
                            ipAddr = (Inet4Address)addr;
                            break;
                        }
                    }
                }
                if (ipAddr != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipAddr.getHostAddress();
    }

    public String getIP2() {
        String ip = null;
        try {
            InetAddress address = InetAddress.getLocalHost();
            System.out.println("hostName: " +address.getHostName() + ", hostAddress: " + address.getHostAddress());
            ip = address.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        };

        return ip;
    }
}
