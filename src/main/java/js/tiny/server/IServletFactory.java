package js.tiny.server;

public interface IServletFactory {
    IServlet createServlet(RequestType requestType);
}
