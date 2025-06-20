package com.case_wallet.apirest.application.wallet.service;

import com.case_wallet.apirest.application.wallet.dto.WalletCreationDetails;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.MnemonicUtils;

import java.security.SecureRandom;

@Service
public class WalletGenerationService {

    private static final int[] BIP44_PATH = {44 | Bip32ECKeyPair.HARDENED_BIT, 60 | Bip32ECKeyPair.HARDENED_BIT, 0 | Bip32ECKeyPair.HARDENED_BIT, 0, 0};

    public WalletCreationDetails generateWallet() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] initialEntropy = new byte[16];
        secureRandom.nextBytes(initialEntropy);
        String mnemonic = MnemonicUtils.generateMnemonic(initialEntropy);
        Bip32ECKeyPair masterKeyPair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic, null));
        Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, BIP44_PATH);
        Credentials credentials = Credentials.create(derivedKeyPair);
        String address = credentials.getAddress();
        return new WalletCreationDetails(mnemonic, address);
    }
} 