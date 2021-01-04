package js.tiny.server;

interface IServlet {
    void service(Request request, Response response) throws Exception;
}
