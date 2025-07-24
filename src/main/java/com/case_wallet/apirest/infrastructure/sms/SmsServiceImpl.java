package com.case_wallet.apirest.infrastructure.sms;

import com.case_wallet.apirest.application.auth.port.out.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Override
    public void sendSms(String phoneNumber, String message) {
        // En un entorno real, aquí se integraría con un proveedor de SMS como Twilio, AWS SNS, etc.
        // Por ahora, solo registramos el mensaje en el log
        log.info("SMS enviado a {}: {}", phoneNumber, message);
        
        // Simulación de envío exitoso
        // En producción, aquí se manejarían las excepciones del proveedor de SMS
    }
}
