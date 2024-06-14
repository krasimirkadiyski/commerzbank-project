package https.sofia.commerzbank.bg.commerzbank_project.transaction;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestParam String addressFrom, @RequestParam String addressTo, @RequestParam String amount){
        try {
            Transaction transaction = transactionService.createTransaction(addressFrom, addressTo, amount);
            return ResponseEntity.status(202).body(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating transaction: " + e.getMessage());
        }
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }
}
