package com.case_wallet.apirest.domain.wallet.model;

public enum TransactionType {
    DEPOSIT,
    TRANSFER,
    SWAP,
    WITHDRAWAL,
    LOCAL_TRANSFER,
    CRYPTO_TRANSFER,
    LOCAL_TO_CRYPTO_SWAP,
    CRYPTO_TO_LOCAL_SWAP
}
