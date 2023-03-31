package top.lingkang.finalserver.server.core;

import top.lingkang.finalserver.server.web.http.FinalServerContext;

import java.io.File;

public interface ReturnStaticFileHandler {
    void returnStaticFile(File file, FinalServerContext context) throws Exception;
}
