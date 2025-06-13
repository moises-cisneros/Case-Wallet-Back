package com.case_wallet.apirest.infrastructure.database.wallet.adapter;

import com.case_wallet.apirest.application.wallet.port.out.LoadWalletPort;
import com.case_wallet.apirest.domain.wallet.model.Transaction;
import com.case_wallet.apirest.domain.wallet.model.Wallet;
import com.case_wallet.apirest.infrastructure.database.wallet.mapper.WalletEntityMapper;
import com.case_wallet.apirest.infrastructure.database.wallet.repository.WalletRepository;
import com.case_wallet.apirest.infrastructure.database.wallet.repository.TransactionRepository;
import com.case_wallet.apirest.application.wallet.mapper.WalletMapper;
import com.case_wallet.apirest.application.wallet.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WalletPersistenceAdapter implements LoadWalletPort {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletMapper walletMapper;
    private final WalletEntityMapper walletEntityMapper;
    private final TransactionMapper transactionMapper;

    @Override
    public Optional<Wallet> loadWalletByUserId(UUID userId) {
        return walletRepository.findByUserId(userId)
                .map(walletMapper::toModel);
    }

    @Override
    public Wallet saveWallet(Wallet wallet) {
        var entity = walletEntityMapper.toEntity(wallet);
        return walletMapper.toModel(walletRepository.save(entity));
    }

    @Override
    public Page<Transaction> loadTransactionsByUserId(UUID userId, Pageable pageable) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(transactionMapper::toModel);
    }

    @Override
    public Transaction saveTransaction(Transaction transaction) {
        var entity = transactionMapper.toEntity(transaction);
        return transactionMapper.toModel(transactionRepository.save(entity));
    }

    @Override
    public Optional<Transaction> loadTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .map(transactionMapper::toModel);
    }
}
