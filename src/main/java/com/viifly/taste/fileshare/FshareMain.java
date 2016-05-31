package com.viifly.taste.fileshare;

import com.viifly.taste.Utils;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.LocalServiceBindingException;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;

import java.io.IOException;
import java.util.Random;

public class FshareMain implements Runnable {
    private NanoFileUploadServer httpServer;

    private static Config config;

    public static Config getConfig() {
        if (config == null) {
            config = new Config();
        }
        return config;
    }

    public FshareMain() {
        initConfig();
    }

    public static void main(String[] args) {
        FshareMain fshare = new FshareMain();
        System.out.println("Config IP:" + getConfig().localIP + ", port:"+ getConfig().serverPort );

        fshare.startHttpServer();
        new Thread(fshare).start();
    }

    public static class Config {
        public int serverPort = 8001;
        public String localIP = "127.0.0.1";
        public String storageDir = "./";
    }

    public void initConfig() {
        getConfig().serverPort = this.getServerPort();
        getConfig().localIP = Utils.getLocalIP();
    }

    public String buildUploadServerUrl() {
        return String.format("http://%s:%d%s", getConfig().localIP, getConfig().serverPort, FshareConstants.UPLOAD_URL_PATH);
    }

    public void updateUrlToService(LocalService<UploadService> uploadService) {
        UploadService svcImpl = uploadService.getManager().getImplementation();
        svcImpl.setUploadUrl(buildUploadServerUrl());
    }

    private void startHttpServer() {
        httpServer = new NanoFileUploadServer(FshareMain.getConfig().serverPort, FshareMain.getConfig().storageDir);
        try {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    httpServer.stop();
                }
            });

            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("HttpServer at " + this.buildUploadServerUrl());
    }


    @Override
    public void run() {
        try {
            final UpnpService upnpService = new UpnpServiceImpl();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    upnpService.shutdown();
                }
            });

            // Add the bound local device to the registry
            upnpService.getRegistry().addDevice(
                    createDevice()
            );

        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }

    }

    LocalDevice createDevice()
            throws ValidationException, LocalServiceBindingException, IOException {
        DeviceIdentity identity =
                new DeviceIdentity(
                        UDN.uniqueSystemIdentifier("FileShare Man-1")
                );

        DeviceType type = new UDADeviceType("FileShare", 1);

        String deviceName = "FileShare - " + Utils.getLocalHostName();
        DeviceDetails details = new DeviceDetails(deviceName,
                new ManufacturerDetails("Taste"),
                new ModelDetails("FileShare V1"));

        LocalService<UploadService> uploadService = new AnnotationLocalServiceBinder().read(UploadService.class);
        uploadService.setManager(new DefaultServiceManager<UploadService>(uploadService, UploadService.class));
        updateUrlToService(uploadService);
        return new LocalDevice(identity, type, details, uploadService);
    }

    public int getServerPort() {
        Random random = new Random();
        int p = 8001 + random.nextInt(100);
        int t = 1;
        while (!Utils.checkPortAvalable(p)) {
            p = 8001 + random.nextInt(100);
            ++t;
            if (t > 5) {
                break;
            }
        }
        return p;
    }

}
