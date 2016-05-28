package com.viifly.taste.fileshare;

import com.viifly.taste.fileshare.FshareConstants;
import org.fourthline.cling.binding.annotations.*;

@UpnpService(
        serviceId = @UpnpServiceId(FshareConstants.UPLOAD_SERVICE_ID),
        serviceType = @UpnpServiceType(value = FshareConstants.UPLOAD_SERVICE_ID, version = 1)
)
public class UploadService {

    @UpnpStateVariable(name="UploadUrl", sendEvents = false)
    private String uploadUrl;

    @UpnpAction(out = @UpnpOutputArgument(name = "UploadURL"))
    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

}
