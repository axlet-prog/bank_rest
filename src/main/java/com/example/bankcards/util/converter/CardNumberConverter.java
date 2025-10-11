package com.example.bankcards.util.converter;


import com.example.bankcards.config.properties.SecurityProperties;
import com.example.bankcards.exception.InternalServiceException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Base64;
import javax.crypto.Cipher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Converter
@Component
@RequiredArgsConstructor
public class CardNumberConverter implements AttributeConverter<String, String> {

    private final SecurityProperties securityProperties;

    @Override
    public String convertToDatabaseColumn(String cardNumber) {
        try {
            Cipher cipher = ConverterUtil.initCipher(
                securityProperties.dataEncoding().cardNumberSecretKey(),
                Cipher.ENCRYPT_MODE
            );
            return Base64.getEncoder().encodeToString(cipher.doFinal(cardNumber.getBytes()));
        } catch (Exception e) {
            throw new InternalServiceException("Exception while converting card number");
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            Cipher cipher = ConverterUtil.initCipher(
                securityProperties.dataEncoding().cardNumberSecretKey(),
                Cipher.DECRYPT_MODE
            );
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            throw new InternalServiceException("Exception while converting card number");
        }
    }
}
