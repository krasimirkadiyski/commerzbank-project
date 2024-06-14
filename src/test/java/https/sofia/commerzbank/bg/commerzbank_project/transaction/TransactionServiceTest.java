package https.sofia.commerzbank.bg.commerzbank_project.transaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    TransactionRepository transactionRepository;

    @Mock
    TransactionManager transactionManager;

    @InjectMocks
    TransactionService transactionService;

    @Test
    void shouldThrowTransactionExceptionWhenTransactionHasError() throws IOException {
        final String errorMessage = "Error creating transaction: sender doesn't have enough funds to send tx.";
        final String addressFrom = "0x627306090abaB3A6e1400e9345bC60c78a8BEf57";
        final String addressTo = "0xf17f52151EbEF6C7334FAD080c5704D77216b732";
        final String amount = "1";
        BigInteger value = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
        EthSendTransaction ethSendTransaction = mock(EthSendTransaction.class);

        when(ethSendTransaction.hasError()).thenReturn(true);
        when(ethSendTransaction.getError()).thenReturn(new Response.Error(1, errorMessage));
        when(transactionManager.sendTransaction(
                DefaultGasProvider.GAS_PRICE,
                DefaultGasProvider.GAS_LIMIT,
                addressTo, "",
                value
        )).thenReturn(ethSendTransaction);

        Exception exception = assertThrows(TransactionException.class, () -> transactionService.createTransaction(addressFrom, addressTo, amount));
        assertTrue(exception.getMessage().contains(errorMessage));
    }

    @Test
    void shouldCreateTransactionSuccessfully() throws Exception {
        final String addressFrom = "0x627306090abaB3A6e1400e9345bC60c78a8BEf57";
        final String addressTo = "0xf17f52151EbEF6C7334FAD080c5704D77216b732";
        final String amount = "1";
        BigInteger value = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
        EthSendTransaction ethSendTransaction = mock(EthSendTransaction.class);
        Transaction expectedTransaction = new Transaction();

        when(ethSendTransaction.hasError()).thenReturn(false);
        when(ethSendTransaction.getTransactionHash()).thenReturn("0x7e26bb631d8801a98f5bf8d80209824850be2e23800b8bf3a7b8e4f3c865f696");
        when(transactionManager.sendTransaction(
                DefaultGasProvider.GAS_PRICE,
                DefaultGasProvider.GAS_LIMIT,
                addressTo, "",
                value
        )).thenReturn(ethSendTransaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(expectedTransaction);

        Transaction actualTransaction = transactionService.createTransaction(addressFrom, addressTo, amount);

        assertEquals(expectedTransaction, actualTransaction);
    }

    @Test
    void shouldGetAllTransactionsSuccessfully() {
        Transaction transaction1 = new Transaction();
        transaction1.setAddressFrom("0x627306090abaB3A6e1400e9345bC60c78a8BEf57");
        transaction1.setAddressTo("0xf17f52151EbEF6C7334FAD080c5704D77216b732");
        transaction1.setAmount("1");
        transaction1.setTransactionHash("hash1");

        Transaction transaction2 = new Transaction();
        transaction2.setAddressFrom("0xf17f52151EbEF6C7334FAD080c5704D77216b732");
        transaction2.setAddressTo("0x627306090abaB3A6e1400e9345bC60c78a8BEf57");
        transaction2.setAmount("2");
        transaction2.setTransactionHash("hash2");

        List<Transaction> expectedTransactions = Arrays.asList(transaction1, transaction2);

        when(transactionRepository.findAll()).thenReturn(expectedTransactions);

        List<Transaction> actualTransactions = transactionService.getAllTransactions();

        assertEquals(expectedTransactions.size(), actualTransactions.size());
        for (int i = 0; i < expectedTransactions.size(); i++) {
            assertEquals(expectedTransactions.get(i).getAddressFrom(), actualTransactions.get(i).getAddressFrom());
            assertEquals(expectedTransactions.get(i).getAddressTo(), actualTransactions.get(i).getAddressTo());
            assertEquals(expectedTransactions.get(i).getAmount(), actualTransactions.get(i).getAmount());
            assertEquals(expectedTransactions.get(i).getTransactionHash(), actualTransactions.get(i).getTransactionHash());
        }
    }

}