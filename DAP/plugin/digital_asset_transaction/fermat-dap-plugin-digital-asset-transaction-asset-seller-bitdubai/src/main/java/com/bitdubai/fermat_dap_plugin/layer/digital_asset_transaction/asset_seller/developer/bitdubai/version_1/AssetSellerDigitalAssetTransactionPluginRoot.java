package com.bitdubai.fermat_dap_plugin.layer.digital_asset_transaction.asset_seller.developer.bitdubai.version_1;

import com.bitdubai.fermat_api.CantStartAgentException;
import com.bitdubai.fermat_api.CantStartPluginException;
import com.bitdubai.fermat_api.CantStopAgentException;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractPlugin;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededAddonReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededPluginReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.developer.DatabaseManagerForDevelopers;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabase;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTable;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTableRecord;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperObjectFactory;
import com.bitdubai.fermat_api.layer.all_definition.enums.Addons;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantCreateDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantOpenDatabaseException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.DatabaseNotFoundException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.interfaces.BitcoinNetworkManager;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.asset_vault.interfaces.AssetVaultManager;
import com.bitdubai.fermat_dap_api.layer.all_definition.digital_asset.AssetNegotiation;
import com.bitdubai.fermat_dap_api.layer.all_definition.exceptions.CantSetObjectException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.exceptions.CantGetAssetUserActorsException;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUser;
import com.bitdubai.fermat_dap_api.layer.dap_actor.asset_user.interfaces.ActorAssetUserManager;
import com.bitdubai.fermat_dap_api.layer.dap_network_services.asset_transmission.exceptions.CantSendDigitalAssetMetadataException;
import com.bitdubai.fermat_dap_api.layer.dap_network_services.asset_transmission.interfaces.AssetTransmissionNetworkServiceManager;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.asset_seller.exceptions.CantStartAssetSellTransactionException;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.asset_seller.interfaces.AssetSellerManager;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.exceptions.CantDeliverDatabaseException;
import com.bitdubai.fermat_dap_api.layer.dap_transaction.common.exceptions.CantStartServiceException;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.asset_user_wallet.interfaces.AssetUserWalletManager;
import com.bitdubai.fermat_dap_api.layer.dap_wallet.common.exceptions.CantLoadWalletException;
import com.bitdubai.fermat_dap_plugin.layer.digital_asset_transaction.asset_seller.developer.bitdubai.version_1.developer_utils.AssetSellerDeveloperDatabaseFactory;
import com.bitdubai.fermat_dap_plugin.layer.digital_asset_transaction.asset_seller.developer.bitdubai.version_1.structure.database.AssetSellerDAO;
import com.bitdubai.fermat_dap_plugin.layer.digital_asset_transaction.asset_seller.developer.bitdubai.version_1.structure.database.AssetSellerDatabaseConstants;
import com.bitdubai.fermat_dap_plugin.layer.digital_asset_transaction.asset_seller.developer.bitdubai.version_1.structure.database.AssetSellerDatabaseFactory;
import com.bitdubai.fermat_dap_plugin.layer.digital_asset_transaction.asset_seller.developer.bitdubai.version_1.structure.events.AssetSellerMonitorAgent;
import com.bitdubai.fermat_dap_plugin.layer.digital_asset_transaction.asset_seller.developer.bitdubai.version_1.structure.events.AssetSellerRecorderService;
import com.bitdubai.fermat_dap_plugin.layer.digital_asset_transaction.asset_seller.developer.bitdubai.version_1.structure.functional.AssetSellerTransactionManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.interfaces.EventManager;

import java.util.Collections;
import java.util.List;

/**
 * Created by Víctor A. Mars M. (marsvicam@gmail.com) on 9/02/16.
 */
public class AssetSellerDigitalAssetTransactionPluginRoot extends AbstractPlugin implements
        DatabaseManagerForDevelopers,
        AssetSellerManager {


    //VARIABLE DECLARATION

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_FILE_SYSTEM)
    protected PluginFileSystem pluginFileSystem;

    @NeededAddonReference(platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM, addon = Addons.PLUGIN_DATABASE_SYSTEM)
    private PluginDatabaseSystem pluginDatabaseSystem;

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM, layer = Layers.PLATFORM_SERVICE, addon = Addons.ERROR_MANAGER)
    private ErrorManager errorManager;

    @NeededAddonReference(platform = Platforms.PLUG_INS_PLATFORM, layer = Layers.PLATFORM_SERVICE, addon = Addons.EVENT_MANAGER)
    private EventManager eventManager;

    @NeededPluginReference(platform = Platforms.BLOCKCHAINS, layer = Layers.CRYPTO_VAULT, plugin = Plugins.BITCOIN_ASSET_VAULT)
    private AssetVaultManager assetVaultManager;

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.WALLET, plugin = Plugins.ASSET_USER)
    private AssetUserWalletManager assetUserWalletManager;

    @NeededPluginReference(platform = Platforms.BLOCKCHAINS, layer = Layers.CRYPTO_NETWORK, plugin = Plugins.BITCOIN_NETWORK)
    private BitcoinNetworkManager bitcoinNetworkManager;

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.ACTOR, plugin = Plugins.ASSET_USER)
    private ActorAssetUserManager actorAssetUserManager;

    @NeededPluginReference(platform = Platforms.DIGITAL_ASSET_PLATFORM, layer = Layers.NETWORK_SERVICE, plugin = Plugins.ASSET_TRANSMISSION)
    private AssetTransmissionNetworkServiceManager assetTransmission;

    private AssetSellerMonitorAgent agent;
    private AssetSellerRecorderService recorderService;
    private AssetSellerTransactionManager transactionManager;
    private AssetSellerDAO dao;

    //CONSTRUCTORS
    public AssetSellerDigitalAssetTransactionPluginRoot() {
        super(new PluginVersionReference(new Version()));
    }

    //PUBLIC METHODS
    @Override
    public void start() throws CantStartPluginException {
        try {
            createDatabase();

            dao = new AssetSellerDAO(pluginId, pluginDatabaseSystem, actorAssetUserManager, assetUserWalletManager);
            transactionManager = new AssetSellerTransactionManager(assetUserWalletManager, assetTransmission, actorAssetUserManager, dao);
            initializeMonitorAgent();
            initializeRecorderService();
            super.start();
        } catch (Exception e) {
            throw new CantStartPluginException(FermatException.wrapException(e));
        }
    }

    @Override
    public void stop() {
        try {
            agent.stop();
            recorderService.stop();
        } catch (CantStopAgentException e) {
            e.printStackTrace();
        } finally {
            super.stop();
        }
    }

    @Override
    public void requestAssetSell(ActorAssetUser userToDeliver, AssetNegotiation negotiation) throws CantStartAssetSellTransactionException {
        String context = "ActorAssetUser: " + userToDeliver + " - AssetNegotiation: " + negotiation;
        try {
            transactionManager.requestAssetSell(userToDeliver, negotiation);
        } catch (CantLoadWalletException | CantGetAssetUserActorsException | CantSetObjectException | CantSendDigitalAssetMetadataException e) {
            throw new CantStartAssetSellTransactionException(context, e);
        }
    }

    //PRIVATE METHODS
    private void createDatabase() throws CantCreateDatabaseException {
        AssetSellerDatabaseFactory databaseFactory = new AssetSellerDatabaseFactory(pluginDatabaseSystem, pluginId);
        if (!databaseFactory.databaseExists()) {
            databaseFactory.createDatabase();
        }
    }

    private void initializeMonitorAgent() throws CantStartAgentException {
        agent = new AssetSellerMonitorAgent(assetUserWalletManager, actorAssetUserManager, dao, errorManager, assetTransmission, assetVaultManager, bitcoinNetworkManager);
        agent.start();
    }

    private void initializeRecorderService() throws CantStartServiceException {
        recorderService = new AssetSellerRecorderService(eventManager, pluginDatabaseSystem, pluginId, dao);
        recorderService.start();
    }

    //GETTER AND SETTERS
    @Override
    public List<DeveloperDatabase> getDatabaseList(DeveloperObjectFactory developerObjectFactory) {
        return AssetSellerDeveloperDatabaseFactory.getDatabaseList(developerObjectFactory, pluginId);
    }

    @Override
    public List<DeveloperDatabaseTable> getDatabaseTableList(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase) {
        return AssetSellerDeveloperDatabaseFactory.getDatabaseTableList(developerObjectFactory);
    }

    @Override
    public List<DeveloperDatabaseTableRecord> getDatabaseTableContent(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase, DeveloperDatabaseTable developerDatabaseTable) {
        Database database;
        try {
            database = this.pluginDatabaseSystem.openDatabase(pluginId, AssetSellerDatabaseConstants.ASSET_SELLER_DATABASE);
            return AssetSellerDeveloperDatabaseFactory.getDatabaseTableContent(developerObjectFactory, database, developerDatabaseTable);
        } catch (CantOpenDatabaseException cantOpenDatabaseException) {
            /**
             * The database exists but cannot be open. I can not handle this situation.
             */
            FermatException e = new CantDeliverDatabaseException("Cannot open the database", cantOpenDatabaseException, "DeveloperDatabase: " + developerDatabase.getName(), "");
            this.errorManager.reportUnexpectedPluginException(Plugins.ASSET_SELLER, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (DatabaseNotFoundException databaseNotFoundException) {
            FermatException e = new CantDeliverDatabaseException("Database does not exists", databaseNotFoundException, "DeveloperDatabase: " + developerDatabase.getName(), "");
            this.errorManager.reportUnexpectedPluginException(Plugins.ASSET_SELLER, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        } catch (Exception exception) {
            FermatException e = new CantDeliverDatabaseException("Unexpected Exception", exception, "DeveloperDatabase: " + developerDatabase.getName(), "");
            this.errorManager.reportUnexpectedPluginException(Plugins.ASSET_SELLER, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
        }
        // If we are here the database could not be opened, so we return an empty list
        return Collections.EMPTY_LIST;
    }
    //INNER CLASSES
}
