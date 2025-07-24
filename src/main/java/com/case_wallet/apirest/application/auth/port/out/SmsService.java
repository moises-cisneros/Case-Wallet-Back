package com.case_wallet.apirest.application.auth.port.out;

public interface SmsService {
    void sendSms(String phoneNumber, String message);
}
