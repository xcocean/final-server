package top.lingkang.finalserver.server.web.http;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import io.netty.handler.codec.http.multipart.FileUpload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author lingkang
 * 2023/1/6
 **/
public class MultipartFile {
    private FileUpload fileUpload;
    private File file;

    public MultipartFile(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }

    public FileUpload getFileUpload() {
        return fileUpload;
    }

    public File getFile() throws IOException {
        if (file != null)
            return file;
        if (isInMemory()) {
            file = File.createTempFile("FUp_MF_", "-" + fileUpload.getFilename());
            FileUtil.writeBytes(fileUpload.get(), file);
        } else {
            file = fileUpload.getFile();
        }
        return file;
    }

    public boolean isInMemory() {
        return fileUpload.isInMemory();
    }

    /**
     * 获取文件原名
     */
    public String getFileName() {
        return fileUpload.getFilename();
    }

    /**
     * 获取文件长度
     */
    public long getLength() {
        return fileUpload.length();
    }

    public String getParamName() {
        return fileUpload.getName();
    }

    public String getContentType() {
        return fileUpload.getContentType();
    }

    public String getCharset() {
        return fileUpload.getCharset().name();
    }

    public String getHttpDataType() {
        return fileUpload.getContentType();
    }

    public byte[] getBytes() throws IOException{
        return fileUpload.get();
    }

    /**
     * 将上传的文件保存到
     */
    public void transferTo(File dest) throws IOException {
        if (fileUpload.isInMemory()) {
            FileUtil.writeBytes(fileUpload.get(), dest);
        } else {
            FileChannel sourceChannel = null;
            FileChannel destChannel = null;
            try {
                sourceChannel = new FileInputStream(fileUpload.getFile()).getChannel();
                destChannel = new FileOutputStream(dest).getChannel();
                destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            } finally {
                IoUtil.close(sourceChannel);
                IoUtil.close(destChannel);
            }
        }
    }

    @Override
    public String toString() {
        return "MultipartFile{" +
                "fileUpload=" + fileUpload +
                ",\n paramName=" + getParamName() +
                ",\n filename=" + getFileName() +
                ",\n content-type=" + getContentType() +
                ",\n charset=" + getCharset() +
                ",\n content-length=" + getLength() +
                '}';
    }
}
