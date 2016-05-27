package com.viifly.taste.nat;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;

public class GenericQueryAction extends ActionCallback {
    private String action;

    public GenericQueryAction(Service service, String action) {
        super(new ActionInvocation(service.getAction(action)));
        this.action = action;
    }

    @Override
    public void success(ActionInvocation invocation) {
        for (ActionArgumentValue a : invocation.getOutput()) {
            System.out.println(String.format("%s - %s: %s", this.action, a.getArgument().getName(), a.toString()));
        }
    }

    @Override
    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
        System.out.println(this.action +  ": " + defaultMsg);
    }
}
