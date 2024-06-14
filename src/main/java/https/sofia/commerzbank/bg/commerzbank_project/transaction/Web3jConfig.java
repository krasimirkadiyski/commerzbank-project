package https.sofia.commerzbank.bg.commerzbank_project.transaction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;

@Configuration
public class Web3jConfig {
    private static final String WEB3J_SERVICE_URL = "http://localhost:8545";
    private static final String GANACHE_PRIVATE_KEY = "4c0883a69102937d6231471b5dbb6204fe512961708279dc6a5cceab47dfacf2";

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(WEB3J_SERVICE_URL));
    }

    @Bean
    public Credentials credentials() {
        return Credentials.create(GANACHE_PRIVATE_KEY);
    }

    @Bean
    public RawTransactionManager rawTransactionManager(Web3j web3j, Credentials credentials) {
        return new RawTransactionManager(web3j, credentials);
    }
}