package camelpodcast.config;

import org.apache.camel.converter.crypto.CryptoDataFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.HexFormat;


@Configuration
public class CryptoConfig {

    public static final String AES_FORMAT = "aesFormat";
    private static final String CIPHER = "AES/CTR/NoPadding";

    @Bean(AES_FORMAT)
    public CryptoDataFormat aesFormat(@Value("${app.crypto.aes-key-hex}") String keyHex,
                                      @Value("${app.crypto.aes-iv-hex}") String ivHex) {
        byte[] keyBytes = HexFormat.of().parseHex(keyHex);
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        byte[] iv = HexFormat.of().parseHex(ivHex);

        CryptoDataFormat fmt = new CryptoDataFormat(CIPHER, key);
        fmt.setInitVector(iv);
        fmt.setShouldInlineInitializationVector(true);
        fmt.setShouldAppendHMAC(true);
        return fmt;
    }
}
