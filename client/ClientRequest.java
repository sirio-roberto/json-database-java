package client;

public class ClientRequest {
    private String type;
    private String key;
    private String value;

    public ClientRequest(String type, String key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }
}