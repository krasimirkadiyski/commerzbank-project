package https.sofia.commerzbank.bg.commerzbank_project.transaction;

import org.web3j.protocol.core.Response;

public class TransactionException extends Exception {
    private final Response.Error error;

    public TransactionException(Response.Error error) {
        super(error.getMessage());
        this.error = error;
    }

    public Response.Error getError() {
        return this.error;
    }
}
