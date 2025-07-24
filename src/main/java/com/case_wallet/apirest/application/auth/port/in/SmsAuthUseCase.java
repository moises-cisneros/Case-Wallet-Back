package com.case_wallet.apirest.application.auth.port.in;

import com.case_wallet.apirest.application.auth.dto.*;

public interface SmsAuthUseCase {
    void requestSms(RequestSmsRequest request);
    void verifySms(VerifySmsRequest request);
    AuthResponseDTO completeRegistration(CompleteRegistrationRequest request);
    AuthResponseDTO loginWithPhone(PhoneLoginRequest request);
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
}
