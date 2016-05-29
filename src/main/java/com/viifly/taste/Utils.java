package com.viifly.taste;


import java.net.*;
import java.util.Enumeration;

public class Utils {

    public static String getLocalHostName() {
        Inet4Address address = getLocalInetAddress();
        if (address != null) {
            return address.getHostName();
        }
        return "unknown-host";
    }

    public static String getLocalIP() {
        Inet4Address address = getLocalInetAddress();
        if (address != null) {
            return address.getHostAddress();
        }
        return null;
    }

    public static Inet4Address getLocalInetAddress() {
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
        return ipAddr;
    }

    public static String getIP2() {
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

    public static boolean checkPortAvalable(int port){
        try {
            InetAddress theAddress=InetAddress.getByName("127.0.0.1");
            Socket theSocket = new Socket(theAddress,port);
            theSocket.close();
            theSocket = null;
            theAddress = null;
            return false;
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return true;
    }
}
