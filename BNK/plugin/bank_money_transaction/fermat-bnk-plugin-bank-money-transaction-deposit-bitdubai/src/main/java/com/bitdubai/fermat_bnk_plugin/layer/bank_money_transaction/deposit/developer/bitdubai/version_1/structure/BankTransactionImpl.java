package com.bitdubai.fermat_bnk_plugin.layer.bank_money_transaction.deposit.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.layer.all_definition.enums.FiatCurrency;
import com.bitdubai.fermat_bnk_api.all_definition.bank_money_transaction.BankTransaction;
import com.bitdubai.fermat_bnk_api.all_definition.enums.BankOperationType;
import com.bitdubai.fermat_bnk_api.all_definition.enums.BankTransactionStatus;
import com.bitdubai.fermat_bnk_api.all_definition.enums.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by memo on 25/11/15.
 */
public class BankTransactionImpl implements BankTransaction {


    private UUID transactionId;

    private String publicKeyPlugin;

    private String publicKeyWallet;


    private BigDecimal amount;

    private String accountNumber;

    private FiatCurrency fiatCurrency;

    private String memo;

    private BankOperationType bankOperationType;

    private TransactionType transactionType;

    private long timestamp;

    private BankTransactionStatus status;

    public BankTransactionImpl(UUID transactionId, String publicKeyPlugin, String publicKeyWallet, BigDecimal amount, String accountNumber, FiatCurrency fiatCurrency, String memo, BankOperationType bankOperationType, TransactionType transactionType, long timestamp, BankTransactionStatus status) {
        this.transactionId = transactionId;
        this.publicKeyPlugin = publicKeyPlugin;
        this.publicKeyWallet = publicKeyWallet;
        this.amount = amount;
        this.accountNumber = accountNumber;
        this.fiatCurrency = fiatCurrency;
        this.memo = memo;
        this.bankOperationType = bankOperationType;
        this.transactionType = transactionType;
        this.timestamp = timestamp;
        this.status = status;
    }

    @Override
    public UUID getTransactionId() {
        return transactionId;
    }

    @Override
    public String getPublicKeyPlugin() {
        return publicKeyPlugin;
    }

    @Override
    public String getPublicKeyWallet() {
        return publicKeyWallet;
    }

    @Override
    public String getPublicKeyActor() {
        return null;
    }


    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public FiatCurrency getCurrency() {
        return fiatCurrency;
    }

    @Override
    public String getMemo() {
        return memo;
    }

    @Override
    public BankOperationType getBankOperationType() {
        return bankOperationType;
    }

    @Override
    public TransactionType getTransactionType() {
        return transactionType;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public BankTransactionStatus getBankTransactionStatus() {
        return status;
    }
}
