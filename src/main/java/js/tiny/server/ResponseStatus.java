package js.tiny.server;

public enum ResponseStatus {
    /**
     * Status code (200) indicating the request succeeded normally.
     */
    OK("200 OK"),

    NO_CONTENT("204 No Content"),

    MOVED_PERMANENTLY("301 Moved Permanently"),

    /**
     * Status code (400) indicating the request sent by the client was syntactically incorrect.
     */
    BAD_REQUEST("400 Bad request"),

    /**
     * Status code (404) indicating that the requested resource is not available.
     */
    NO_FOUND("404 Not found"),

    /**
     * Status code (500) indicating an error inside the HTTP sync which prevented it from fulfilling the request.
     */
    INTERNAL_SERVER_ERROR("500 Internal server error");

    private String value;

    ResponseStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
