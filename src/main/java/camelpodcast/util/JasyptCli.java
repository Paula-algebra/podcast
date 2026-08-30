package camelpodcast.util;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.iv.NoIvGenerator;

public final class JasyptCli {

    public static final String ALGORITHM = "PBEWithMD5AndDES";

    private static final int POOL_SIZE = 1;

    private JasyptCli() {
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: JasyptCli <encrypt|decrypt> <value> <masterPassword>");
            System.exit(1);
        }

        String command = args[0];
        String value = args[1];
        String master = args[2];

        PooledPBEStringEncryptor encryptor = newEncryptor(master);

        switch (command) {
            case "encrypt" -> {
                String encrypted = encryptor.encrypt(value);
                System.out.println("Plaintext  : " + value);
                System.out.println("Encrypted  : ENC(" + encrypted + ")");
                System.out.println("Round-trip : " + encryptor.decrypt(encrypted));
            }
            case "decrypt" -> {
                String decrypted = encryptor.decrypt(value);
                System.out.println("Encrypted  : " + value);
                System.out.println("Decrypted  : " + decrypted);
            }
            default -> {
                System.err.println("Unknown command: " + command);
                System.exit(1);
            }
        }
    }

    public static PooledPBEStringEncryptor newEncryptor(String masterPassword) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(masterPassword);
        config.setAlgorithm(ALGORITHM);
        config.setKeyObtentionIterations("1000");
        config.setPoolSize(String.valueOf(POOL_SIZE));
        config.setIvGenerator(new NoIvGenerator());
        config.setStringOutputType("base64");

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(config);
        return encryptor;
    }
}