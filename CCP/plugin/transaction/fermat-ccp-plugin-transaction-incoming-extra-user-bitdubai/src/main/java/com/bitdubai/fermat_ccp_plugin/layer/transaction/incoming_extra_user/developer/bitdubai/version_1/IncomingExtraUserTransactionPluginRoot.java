package com.bitdubai.fermat_ccp_plugin.layer.transaction.incoming_extra_user.developer.bitdubai.version_1;

/**
 * Created by ciencias on 2/16/15.
 */

import com.bitdubai.fermat_api.CantStartPluginException;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.Plugin;
import com.bitdubai.fermat_api.Service;
import com.bitdubai.fermat_api.layer.all_definition.common.abstract_classes.AbstractPlugin;
import com.bitdubai.fermat_api.layer.all_definition.common.utils.AddonReference;
import com.bitdubai.fermat_api.layer.all_definition.common.utils.PluginReference;
import com.bitdubai.fermat_api.layer.all_definition.developer.DatabaseManagerForDevelopers;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabase;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTable;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTableRecord;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperObjectFactory;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.bitcoin_wallet.interfaces.BitcoinWalletManager;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.bitcoin_wallet.interfaces.DealsWithBitcoinWallet;
import com.bitdubai.fermat_ccp_api.layer.transaction.incoming_extra_user.IncomingExtraUserManager;
import com.bitdubai.fermat_api.layer.all_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DealsWithPluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.DatabaseNotFoundException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantOpenDatabaseException;
import com.bitdubai.fermat_cry_api.layer.crypto_module.crypto_address_book.interfaces.CryptoAddressBookManager;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.error_manager.DealsWithErrors;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.error_manager.ErrorManager;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.error_manager.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.event_manager.interfaces.DealsWithEvents;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.event_manager.interfaces.EventManager;
import com.bitdubai.fermat_cry_api.layer.crypto_module.crypto_address_book.interfaces.DealsWithCryptoAddressBook;
import com.bitdubai.fermat_cry_api.layer.crypto_router.incoming_crypto.DealsWithIncomingCrypto;
import com.bitdubai.fermat_cry_api.layer.crypto_router.incoming_crypto.IncomingCryptoManager;
import com.bitdubai.fermat_ccp_plugin.layer.transaction.incoming_extra_user.developer.bitdubai.version_1.developerUtils.IncomingExtraUserDeveloperDatabaseFactory;
import com.bitdubai.fermat_ccp_plugin.layer.transaction.incoming_extra_user.developer.bitdubai.version_1.exceptions.CantDeliverDatabaseException;
import com.bitdubai.fermat_ccp_plugin.layer.transaction.incoming_extra_user.developer.bitdubai.version_1.exceptions.CantInitializeCryptoRegistryException;
import com.bitdubai.fermat_ccp_plugin.layer.transaction.incoming_extra_user.developer.bitdubai.version_1.exceptions.CantStartAgentException;
import com.bitdubai.fermat_ccp_plugin.layer.transaction.incoming_extra_user.developer.bitdubai.version_1.exceptions.CantStartServiceException;
import com.bitdubai.fermat_ccp_plugin.layer.transaction.incoming_extra_user.developer.bitdubai.version_1.interfaces.TransactionAgent;
import com.bitdubai.fermat_ccp_plugin.layer.transaction.incoming_extra_user.developer.bitdubai.version_1.interfaces.TransactionService;
import com.bitdubai.fermat_ccp_plugin.layer.transaction.incoming_extra_user.developer.bitdubai.version_1.structure.IncomingExtraUserDataBaseConstants;
import com.bitdubai.fermat_ccp_plugin.layer.transaction.incoming_extra_user.developer.bitdubai.version_1.structure.IncomingExtraUserEventRecorderService;
import com.bitdubai.fermat_ccp_plugin.layer.transaction.incoming_extra_user.developer.bitdubai.version_1.structure.IncomingExtraUserMonitorAgent;
import com.bitdubai.fermat_ccp_plugin.layer.transaction.incoming_extra_user.developer.bitdubai.version_1.structure.IncomingExtraUserRegistry;
import com.bitdubai.fermat_ccp_plugin.layer.transaction.incoming_extra_user.developer.bitdubai.version_1.structure.IncomingExtraUserRelayAgent;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The Incoming Extra User Transaction Manager Plugin is in charge of coordinating the transactions coming from outside the
 * system, meaning from people not a user of the platform.
 * 
 * This plugin knows which wallet to store the funds.
 * 
 * Usually a crypto address is generated from a particular wallet, and that payment should go there, but there is nothing
 * preventing a user to uninstall a wallet and discard the underlying structure in which the user interface was relaying.
 * 
 * For that reason it is necessary this middle man, to get sure any incoming payment for any wallet that ever existed is
 * not lost.
 * 
 * It can send the funds to a default wallet if some is defined or stored itself until the user manually release them.
 * 
 * It is also a centralized place where to query all of the incoming transaction from outside the system.
 *
 * 
 * * * * * * * 
 * * * 
 */

public class IncomingExtraUserTransactionPluginRoot extends AbstractPlugin implements DatabaseManagerForDevelopers, DealsWithBitcoinWallet, DealsWithErrors, DealsWithEvents, DealsWithIncomingCrypto, DealsWithPluginDatabaseSystem, DealsWithCryptoAddressBook,IncomingExtraUserManager, Plugin, Service {

    @Override
    public List<AddonReference> getNeededAddonReferences() {
        return new ArrayList<>();
    }

    @Override
    public List<PluginReference> getNeededPluginReferences() {
        return new ArrayList<>();
    }

    /*
         * DealsWithBitcoinWallet Interface member variables.
         */
    private BitcoinWalletManager bitcoinWalletManager;

    /**
     * DealsWithErrors Interface member variables.
     */
    private ErrorManager errorManager;

    /**
     * DealsWithEvents Interface member variables.
     */
    private EventManager eventManager;

    /**
     * DealsWithIncomingCrypto Interface member variables.
     */
    private IncomingCryptoManager incomingCryptoManager;


    /**
     * DealsWithPluginDatabaseSystem Interface member variables.
     */
    private PluginDatabaseSystem pluginDatabaseSystem;


    /*
     * DealsWithCryptoAddressBook  Interface member variables.
     */
    private CryptoAddressBookManager cryptoAddressBookManager;

    /**
     * IncomingCryptoManager Interface member variables.
     */
    private IncomingExtraUserRegistry registry;

    /**
     * Plugin Interface member variables.
     */
    private UUID pluginId;

    /**
     * Service Interface member variables.
     */
    private ServiceStatus serviceStatus = ServiceStatus.CREATED;
    private TransactionAgent monitor;
    private TransactionAgent relay;
    private TransactionService eventRecorder;


    /*
     * DatabaseManagerForDevelopers interface implementation
     */
    @Override
    public List<DeveloperDatabase> getDatabaseList(DeveloperObjectFactory developerObjectFactory) {
        IncomingExtraUserDeveloperDatabaseFactory dbFactory = new IncomingExtraUserDeveloperDatabaseFactory(pluginId.toString(), IncomingExtraUserDataBaseConstants.INCOMING_EXTRA_USER_DATABASE);
        return dbFactory.getDatabaseList(developerObjectFactory);
    }

    @Override
    public List<DeveloperDatabaseTable> getDatabaseTableList(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase) {
        return IncomingExtraUserDeveloperDatabaseFactory.getDatabaseTableList(developerObjectFactory);
    }

    @Override
    public List<DeveloperDatabaseTableRecord> getDatabaseTableContent(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase, DeveloperDatabaseTable developerDatabaseTable) {
        Database database;
        try {
            database = this.pluginDatabaseSystem.openDatabase(pluginId, IncomingExtraUserDataBaseConstants.INCOMING_EXTRA_USER_DATABASE);
            return IncomingExtraUserDeveloperDatabaseFactory.getDatabaseTableContent(developerObjectFactory, database, developerDatabaseTable);
        }catch (CantOpenDatabaseException cantOpenDatabaseException){
            /**
             * The database exists but cannot be open. I can not handle this situation.
             */
            FermatException e = new CantDeliverDatabaseException("I can't open database",cantOpenDatabaseException,"WalletId: " + developerDatabase.getName(),"");
            this.errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_INCOMING_EXTRA_USER_TRANSACTION,UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN,e);
        }
        catch (DatabaseNotFoundException databaseNotFoundException) {
            FermatException e = new CantDeliverDatabaseException("Database does not exists",databaseNotFoundException,"WalletId: " + developerDatabase.getName(),"");
            this.errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_INCOMING_EXTRA_USER_TRANSACTION,UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN,e);
        }
        catch (Exception e) {
            FermatException e1 = new CantDeliverDatabaseException("Unexpected Exception",e,"","");
            this.errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_INCOMING_EXTRA_USER_TRANSACTION,UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN,e1);
        }
        // If we are here the database could not be opened, so we return an empry list
        return new ArrayList<>();
    }

    /*
     * DealsWithBitcoinWallet Interface implementation
     */
    @Override
    public void setBitcoinWalletManager(BitcoinWalletManager bitcoinWalletManager){
        this.bitcoinWalletManager = bitcoinWalletManager;
    }

    /**
     *DealsWithErrors Interface implementation.
     */
    @Override
    public void setErrorManager(ErrorManager errorManager) {
        this.errorManager = errorManager;
    }

    /**
     * DealWithEvents Interface implementation.
     */
    @Override
    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }


    /**
     *DealsWithIncomingCrypto Interface implementation.
     */
    @Override
    public void setIncomingCryptoManager(IncomingCryptoManager incomingCryptoManager) {
        this.incomingCryptoManager = incomingCryptoManager;
    }

    /**
     * DealsWithPluginDatabaseSystem interface implementation.
     */
    @Override
    public void setPluginDatabaseSystem(PluginDatabaseSystem pluginDatabaseSystem) {
        this.pluginDatabaseSystem = pluginDatabaseSystem;
    }

    /*
     * DealsWithCryptoAddressBook  Interface implementation
     */
    @Override
    public void setCryptoAddressBookManager(CryptoAddressBookManager cryptoAddressBookManager){
        this.cryptoAddressBookManager = cryptoAddressBookManager;
    }

    /**
     * Plugin interface implementation.
     */
    @Override
    public void setId(UUID pluginId) {
        this.pluginId = pluginId;
    }


    /**
     * Service Interface implementation.
     */
    @Override
    public void start()  throws CantStartPluginException{


        /**
         * I will initialize the Registry, which in turn will create the database if necessary.
         */
        this.registry = new IncomingExtraUserRegistry();

        try {
            this.registry.setErrorManager(this.errorManager);
            this.registry.setPluginDatabaseSystem(this.pluginDatabaseSystem);
            this.registry.initialize(this.pluginId);
        }
        catch (CantInitializeCryptoRegistryException cantInitializeCryptoRegistryException) {

            /**
             * If I can not initialize the Registry then I can not start the service.
             */
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_INCOMING_CRYPTO_TRANSACTION, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, cantInitializeCryptoRegistryException);
            throw new CantStartPluginException(cantInitializeCryptoRegistryException, Plugins.BITDUBAI_INCOMING_CRYPTO_TRANSACTION);
        }
        catch (Exception e) {
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_INCOMING_CRYPTO_TRANSACTION, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, FermatException.wrapException(e));
            throw new CantStartPluginException(e, Plugins.BITDUBAI_INCOMING_CRYPTO_TRANSACTION);
        }


        /**
         * I will start the Event Recorder.
         */
        this.eventRecorder = new IncomingExtraUserEventRecorderService(eventManager, registry);

        try {
            this.eventRecorder.start();
        }
        catch (CantStartServiceException cantStartServiceException) {

            /**
             * I cant continue if this happens.
             */
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_INCOMING_CRYPTO_TRANSACTION, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, cantStartServiceException);
            throw new CantStartPluginException(cantStartServiceException, Plugins.BITDUBAI_INCOMING_CRYPTO_TRANSACTION);
        }
        catch (Exception e){
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_INCOMING_CRYPTO_TRANSACTION, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            throw new CantStartPluginException(e, Plugins.BITDUBAI_INCOMING_CRYPTO_TRANSACTION);
        }
        /**
         * I will start the Relay Agent.
         */
        this.relay = new IncomingExtraUserRelayAgent(this.bitcoinWalletManager, this.errorManager, eventManager,this.registry, this.cryptoAddressBookManager);
        try {
            this.relay.start();
        }
        catch (CantStartAgentException cantStartAgentException) {

            /**
             * Note that I stop previously started services and agents.
             */
            this.eventRecorder.stop();

            /**
             * I cant continue if this happens.
             */
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_INCOMING_CRYPTO_TRANSACTION, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, cantStartAgentException);

            throw new CantStartPluginException(Plugins.BITDUBAI_INCOMING_CRYPTO_TRANSACTION);
        }
        catch (Exception e){
            this.eventRecorder.stop();
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_INCOMING_CRYPTO_TRANSACTION, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            throw new CantStartPluginException(e, Plugins.BITDUBAI_INCOMING_CRYPTO_TRANSACTION);
        }

        /**
         * I will start the Monitor Agent.
         */
        this.monitor = new IncomingExtraUserMonitorAgent(this.errorManager, this.incomingCryptoManager, this.registry);
        try {
            this.monitor.start();
        }
        catch (CantStartAgentException cantStartAgentException) {

            /**
             * Note that I stop previously started services and agents.
             */
            this.eventRecorder.stop();
            this.relay.stop();

            /**
             * I cant continue if this happens.
             */
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_INCOMING_CRYPTO_TRANSACTION, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, cantStartAgentException);

            throw new CantStartPluginException(Plugins.BITDUBAI_INCOMING_CRYPTO_TRANSACTION);
        }
        catch (Exception e){
            this.eventRecorder.stop();
            this.relay.stop();
            errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_INCOMING_CRYPTO_TRANSACTION, UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            throw new CantStartPluginException(e, Plugins.BITDUBAI_INCOMING_CRYPTO_TRANSACTION);
        }

        this.serviceStatus = ServiceStatus.STARTED;
    }

    @Override
    public void pause() {

        this.serviceStatus = ServiceStatus.PAUSED;

    }

    @Override
    public void resume() {

        this.serviceStatus = ServiceStatus.STARTED;

    }

    @Override
    public void stop() {

        this.eventRecorder.stop();
        this.relay.stop();
        this.monitor.stop();

        this.serviceStatus = ServiceStatus.STOPPED;
    }

    @Override
    public ServiceStatus getStatus() {
        return this.serviceStatus;
    }

}
