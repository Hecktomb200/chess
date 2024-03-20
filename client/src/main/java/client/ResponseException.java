package client;

public class ResponseException extends Exception {
    private int statCode;

    public ResponseException(int statCode, String mes) {
        super(mes);
        this.statCode = statCode;
    }

    public int StatusCode() {
        return statCode;
    }
}