package test.com.bitdubai.fermat_dmp_plugin.layer.middleware.wallet_contacts.developer.bitdubai.version_1.structure.WalletContactsMiddlewareRegistry;

import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_api.layer.all_definition.money.CryptoAddress;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTable;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableRecord;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTransaction;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_dmp_plugin.layer.middleware.wallet_contacts.developer.bitdubai.version_1.structure.WalletContactsMiddlewareDao;
import com.bitdubai.fermat_dmp_plugin.layer.middleware.wallet_contacts.developer.bitdubai.version_1.structure.WalletContactsMiddlewareDatabaseConstants;
import com.bitdubai.fermat_dmp_plugin.layer.middleware.wallet_contacts.developer.bitdubai.version_1.structure.WalletContactsMiddlewareRegistry;
import com.bitdubai.fermat_pip_api.layer.pip_platform_service.error_manager.ErrorManager;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by Nerio on 26/07/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class initializeTest {

    @Mock
    private PluginDatabaseSystem mockPluginDatabaseSystem;
    @Mock
    private ErrorManager mockErrorManager;
    @Mock
    private Database mockDatabase;

    private UUID testPluginId;

    WalletContactsMiddlewareDao walletContactsMiddlewareDao;
    WalletContactsMiddlewareRegistry walletContactsMiddlewareRegistry;

    @Before
    public void setUp() throws Exception {
        testPluginId = UUID.randomUUID();
        walletContactsMiddlewareDao = new WalletContactsMiddlewareDao(mockPluginDatabaseSystem);
        walletContactsMiddlewareRegistry = new WalletContactsMiddlewareRegistry();
        walletContactsMiddlewareRegistry.setPluginDatabaseSystem(mockPluginDatabaseSystem);
        walletContactsMiddlewareRegistry.setPluginId(testPluginId);
        walletContactsMiddlewareRegistry.setErrorManager(mockErrorManager);
    }

    @Test
    public void testInitialize_NotNull() throws Exception {
        when(mockPluginDatabaseSystem.openDatabase(testPluginId, testPluginId.toString())).thenReturn(mockDatabase);
        walletContactsMiddlewareRegistry.initialize();
    }
}