package com.case_wallet.apirest.application.wallet.mapper;

import com.case_wallet.apirest.domain.wallet.model.Transaction;
import com.case_wallet.apirest.infrastructure.database.wallet.entity.TransactionEntity;
import com.case_wallet.apirest.infrastructure.database.user.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction toModel(TransactionEntity entity) {
        if (entity == null) return null;
        return Transaction.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .type(entity.getType())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .status(entity.getStatus())
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public TransactionEntity toEntity(Transaction model) {
        if (model == null) return null;

        TransactionEntity entity = new TransactionEntity();
        entity.setId(model.getId());

        UserEntity userEntity = new UserEntity();
        userEntity.setId(model.getUserId());
        entity.setUser(userEntity);

        entity.setType(model.getType());
        entity.setAmount(model.getAmount());
        entity.setCurrency(model.getCurrency());
        entity.setStatus(model.getStatus());
        entity.setMetadata(model.getMetadata());
        entity.setCreatedAt(model.getCreatedAt());

        return entity;
    }
}
