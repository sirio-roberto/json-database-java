package server.responses;

public class OkResponse extends AbstractResponse {
    private String value;
    public OkResponse(String response, String value) {
        super(response);
        this.value = value;
    }
}
