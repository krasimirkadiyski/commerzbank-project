package https.sofia.commerzbank.bg.commerzbank_project.transaction;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    public void cleanUp() {
        transactionRepository.deleteAll();
    }

    @Test
    public void shouldFailWhenSenderDoesNotHaveEnoughFunds() throws Exception {
        String addressFrom = "0x627306090abaB3A6e1400e9345bC60c78a8BEf57";
        String addressTo = "0xf17f52151EbEF6C7334FAD080c5704D77216b732";
        String amount = "2000";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions")
                        .param("addressFrom", addressFrom)
                        .param("addressTo", addressTo)
                        .param("amount", amount)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Error creating transaction: sender doesn't have enough funds to send tx.")));
    }

    @Test
    public void testCreateTransaction() throws Exception {
        String addressFrom = "0x627306090abaB3A6e1400e9345bC60c78a8BEf57";
        String addressTo = "0xf17f52151EbEF6C7334FAD080c5704D77216b732";
        String amount = "0.001";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("addressFrom", addressFrom)
                        .param("addressTo", addressTo)
                        .param("amount", amount))
                .andExpect(status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$.addressFrom").value(addressFrom))
                .andExpect(MockMvcResultMatchers.jsonPath("$.addressTo").value(addressTo))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(amount));
    }

    @Test
    public void testCreateTransactionFail() throws Exception {
        String addressFrom = "0x627306090abaB3A6e1400e9345bC60c78a8BEf57";
        String addressTo = "0xf17f52151EbEF6C7334FAD080c5704D77216b732";
        String amount = "2000";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("addressFrom", addressFrom)
                        .param("addressTo", addressTo)
                        .param("amount", amount))
                .andExpect(MockMvcResultMatchers.status().is(500))
                .andExpect(MockMvcResultMatchers.content().string(containsString("Error creating transaction: sender doesn't have enough funds to send tx.")));
    }


    @Test
    public void testGetAllTransactions() throws Exception {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(1L,"0x627306090abaB3A6e1400e9345bC60c78a8BEf57", "0xf17f52151EbEF6C7334FAD080c5704D77216b732", "0.001", "TestHash"));
        transactions.add(new Transaction(2l,"0x627306090abaB3A6e1400e9345bC60c78a8BEf57", "0xf17f52151EbEF6C7334FAD080c5704D77216b732", "0.002","TestHash"));

        transactionRepository.saveAll(transactions);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/transactions"))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        List<Transaction> actualTransactions = mapper.readValue(contentAsString, new TypeReference<List<Transaction>>(){});

        assertEquals(transactions, actualTransactions);
    }
}
