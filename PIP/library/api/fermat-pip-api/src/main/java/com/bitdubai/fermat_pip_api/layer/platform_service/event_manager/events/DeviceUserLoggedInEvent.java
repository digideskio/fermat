package com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.events;

import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.enums.EventType;

/**
 * Created by ciencias on 24.01.15.
 * modified by  by Leon Acosta (laion.cj91@gmail.com) on 27/06/2015.
 */
public class DeviceUserLoggedInEvent extends AbstractFermatEvent {


    private String publicKey;

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return this.publicKey;
    }


    public DeviceUserLoggedInEvent(EventType eventType) {
        super(eventType);
    }


}
