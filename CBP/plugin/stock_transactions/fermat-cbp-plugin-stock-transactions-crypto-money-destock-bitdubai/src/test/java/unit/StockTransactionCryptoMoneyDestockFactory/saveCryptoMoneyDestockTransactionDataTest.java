package unit.StockTransactionCryptoMoneyDestockFactory;


import com.bitdubai.fermat_cbp_api.all_definition.business_transaction.CryptoMoneyTransaction;
import com.bitdubai.fermat_cbp_plugin.layer.stock_transactions.crypto_money_destock.developer.bitdubai.version_1.structure.StockTransactionCryptoMoneyDestockFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;

/**
 * Created by Jose Vilchez on 18/01/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class saveCryptoMoneyDestockTransactionDataTest {

    @Test
    public void saveCryptoMoneyDestockTransactionData() throws Exception {
        StockTransactionCryptoMoneyDestockFactory stockTransactionCryptoMoneyDestockFactory = mock(StockTransactionCryptoMoneyDestockFactory.class, Mockito.RETURNS_DEEP_STUBS);
        doCallRealMethod().when(stockTransactionCryptoMoneyDestockFactory).saveCryptoMoneyDestockTransactionData(Mockito.any(CryptoMoneyTransaction.class));
    }

}
