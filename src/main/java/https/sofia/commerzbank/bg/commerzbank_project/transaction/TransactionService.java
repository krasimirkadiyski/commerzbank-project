package https.sofia.commerzbank.bg.commerzbank_project.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionManager transactionManager;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, TransactionManager transactionManager) {
        this.transactionRepository = transactionRepository;
        this.transactionManager = transactionManager;
    }

    public Transaction createTransaction(String addressFrom, String addressTo, String amount) throws Exception {
        BigInteger value = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();

        EthSendTransaction ethSendTransaction = transactionManager.sendTransaction(
                DefaultGasProvider.GAS_PRICE,
                DefaultGasProvider.GAS_LIMIT,
                addressTo, "",
                value
        );

        if (ethSendTransaction.hasError()) {
            throw new TransactionException(ethSendTransaction.getError());
        }

        String transactionHash = ethSendTransaction.getTransactionHash();

        Transaction transaction = new Transaction();
        transaction.setAddressFrom(addressFrom);
        transaction.setAddressTo(addressTo);
        transaction.setAmount(amount);
        transaction.setTransactionHash(transactionHash);

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}


