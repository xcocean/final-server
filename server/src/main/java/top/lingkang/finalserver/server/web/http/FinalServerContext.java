package top.lingkang.finalserver.server.web.http;

/**
 * @author lingkang
 * Created by 2022/12/6
 */
public class FinalServerContext {
    private Request request;
    private Response response;

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
