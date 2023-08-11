package server.responses;

public class ErrorResponse extends AbstractResponse {
    private String reason;
    public ErrorResponse(String response, String reason) {
        super(response);
        this.reason = reason;
    }
}
