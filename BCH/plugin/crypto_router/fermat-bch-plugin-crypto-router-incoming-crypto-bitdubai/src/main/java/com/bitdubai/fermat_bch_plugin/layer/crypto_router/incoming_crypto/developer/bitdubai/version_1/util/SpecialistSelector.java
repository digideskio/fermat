package com.bitdubai.fermat_bch_plugin.layer.crypto_router.incoming_crypto.developer.bitdubai.version_1.util;

import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_api.layer.all_definition.money.CryptoAddress;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.Specialist;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.crypto_transactions.CryptoTransaction;
import com.bitdubai.fermat_bch_api.layer.crypto_module.crypto_address_book.exceptions.CantGetCryptoAddressBookRecordException;
import com.bitdubai.fermat_bch_api.layer.crypto_module.crypto_address_book.exceptions.CryptoAddressBookRecordNotFoundException;
import com.bitdubai.fermat_bch_api.layer.crypto_module.crypto_address_book.interfaces.CryptoAddressBookManager;
import com.bitdubai.fermat_bch_api.layer.crypto_module.crypto_address_book.interfaces.CryptoAddressBookRecord;
import com.bitdubai.fermat_bch_plugin.layer.crypto_router.incoming_crypto.developer.bitdubai.version_1.exceptions.CantSelectSpecialistException;

/**
 * Created by eze on 12/06/15.

 * The purpose of this class is to indicate the correct
 * destination for a given transaction
 */
public class SpecialistSelector {

    private final CryptoAddressBookManager cryptoAddressBookManager;

    public SpecialistSelector(final CryptoAddressBookManager cryptoAddressBookManager) {

        this.cryptoAddressBookManager = cryptoAddressBookManager;
    }

    public Specialist getSpecialist(CryptoTransaction cryptoTransaction) throws CantSelectSpecialistException {

        CryptoAddress cryptoAddress = new CryptoAddress();

        cryptoAddress.setAddress(cryptoTransaction.getAddressTo().getAddress());
        cryptoAddress.setCryptoCurrency(cryptoTransaction.getCryptoCurrency());

        try {
            CryptoAddressBookRecord cryptoAddressBookRecord = cryptoAddressBookManager.getCryptoAddressBookRecordByCryptoAddress(cryptoAddress);
            /**
             * If I don't have this address registered, then I will set unknown specialist.
             */
            if (cryptoAddressBookRecord == null)
                return Specialist.UNKNOWN_SPECIALIST;
            switch (cryptoAddressBookRecord.getDeliveredToActorType()) {
                case DEVICE_USER:
                    return Specialist.DEVICE_USER_SPECIALIST;
                case INTRA_USER:
                    return Specialist.INTRA_USER_SPECIALIST;
                case EXTRA_USER:
                    return Specialist.EXTRA_USER_SPECIALIST;
                case DAP_ASSET_ISSUER:
                    return Specialist.ASSET_ISSUER_SPECIALIST;
                case DAP_ASSET_USER:
                    return Specialist.ASSET_USER_SPECIALIST;
                case DAP_ASSET_REDEEM_POINT:
                    /**
                     * Incoming transactions delivered to a Redeem Point can be request by an Asset User or internally
                     * by the Asset Issuer. I need to determine this case to select the correct Specialist.
                     */
                    if (cryptoAddressBookRecord.getDeliveredByActorType() == Actors.DAP_ASSET_USER)
                    /**
                     * This address was requested by an Asset_User so we are informing the Redeem Point
                     */
                        return Specialist.REDEEM_POINT_SPECIALIST;
                    if (cryptoAddressBookRecord.getDeliveredByActorType() == Actors.DAP_ASSET_ISSUER)
                    /**
                     * this address was requested and generated by the asset vault in the Asset Issuer,
                     * so I will inform the redemption specialist.
                     */
                        return Specialist.ASSET_REDEMPTION_SPECIALIST;
                default:
                    // Here we have a serious problem
                    throw new CantSelectSpecialistException("NO SPECIALIST FOUND", null, "Actor: " + cryptoAddressBookRecord.getDeliveredToActorType() + " with code " + cryptoAddressBookRecord.getDeliveredToActorType().getCode(), "Actor not considered in switch statement");
            }
        } catch (CantGetCryptoAddressBookRecordException | CryptoAddressBookRecordNotFoundException e) {
            /**
             * If I couldn't get the specialist from the database, then I will continue with an unknown specialist.
             */
            return Specialist.UNKNOWN_SPECIALIST;
        }
    }
}
