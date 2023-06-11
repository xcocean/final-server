package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author lingkang
 * 2023/1/13
 **/
public class Demo10 {
    public static void main(String[] args) {
        String result = "";
        URL url;
        HttpURLConnection conn;
        try {
            url = new URL("http://baidu.com:" + 80 + "/");
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            //连接主机的超时时间（单位：毫秒）
            conn.setConnectTimeout(5000);
            //从主机读取数据的超时时间（单位：毫秒）
            conn.setReadTimeout(5000);
            //设置请求方式
            conn.setRequestMethod("GET");
            //设置  网络文件的类型和网页的编码
            /*HttpHeaders headers = request.headers();
            if (headers.contains(HttpHeaderNames.CONTENT_TYPE)){
                conn.setRequestProperty(HttpHeaderNames.CONTENT_TYPE.toString(), headers.get(HttpHeaderNames.CONTENT_TYPE));
            }*/

            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            //将数据写入流中
            out.write(new char[0]);
            //刷新流中的数据
            out.flush();
            //关闭流(关闭之前要先刷新)
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            //读一行文字并返回该行字符
            while ((line = in.readLine()) != null) {
                result += line;
            }
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            url = null;
            conn = null;
        }
    }
}
