package com.bitdubai.fermat_cbp_plugin.layer.sub_app_module.crypto_customer_identity.developer.bitdubai.version_1;

import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractModule;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededAddonReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededPluginReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.exceptions.CantGetModuleManagerException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Addons;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;
import com.bitdubai.fermat_api.layer.core.PluginInfo;
import com.bitdubai.fermat_api.layer.modules.common_classes.ActiveActorIdentityInformation;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.location_system.LocationManager;
import com.bitdubai.fermat_cbp_api.layer.identity.crypto_customer.interfaces.CryptoCustomerIdentityManager;
import com.bitdubai.fermat_cbp_api.layer.sub_app_module.crypto_customer_identity.IdentityCustomerPreferenceSettings;
import com.bitdubai.fermat_cbp_api.layer.sub_app_module.crypto_customer_identity.interfaces.CryptoCustomerIdentityModuleManager;
import com.bitdubai.fermat_cbp_plugin.layer.sub_app_module.crypto_customer_identity.developer.bitdubai.version_1.structure.CryptoCustomerIdentityModuleManagerImpl;


/**
 * Created by natalia on 16.09.15.
 */

@PluginInfo(createdBy = "vlzangel", maintainerMail = "vlzangel91@gmail.com", platform = Platforms.CRYPTO_BROKER_PLATFORM, layer = Layers.SUB_APP_MODULE, plugin = Plugins.CRYPTO_CUSTOMER_IDENTITY)
public class CryptoCustomerIdentitySubAppModulePluginRoot extends AbstractModule<IdentityCustomerPreferenceSettings, ActiveActorIdentityInformation> {

    @NeededPluginReference(platform = Platforms.CRYPTO_BROKER_PLATFORM, layer = Layers.IDENTITY, plugin = Plugins.CRYPTO_CUSTOMER)
    private CryptoCustomerIdentityManager identityManager;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_FILE_SYSTEM)
    private PluginFileSystem pluginFileSystem;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.DEVICE_LOCATION)
    private LocationManager locationManager;

    CryptoCustomerIdentityModuleManager moduleManager;

    public CryptoCustomerIdentitySubAppModulePluginRoot() {
        super(new PluginVersionReference(new Version()));
    }

    @Override
    public CryptoCustomerIdentityModuleManager getModuleManager() throws CantGetModuleManagerException {
        if (moduleManager == null)
            moduleManager = new CryptoCustomerIdentityModuleManagerImpl(
                    identityManager,
                    pluginFileSystem,
                    pluginId,
                    this, locationManager);

        return moduleManager;
    }
}