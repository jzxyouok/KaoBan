package network;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by lsy on 15-1-30.
 */
public class MyHttpclient2 {

    private static final String TESTHTTPCLIENT = "HttpClientTest";

    private static DefaultHttpClient myHttpClient;
    // Ｃｏｏｋｉｅ相关变量
    private static CookieStore cookieStore = null;
    //private static List<Cookie> listCookies = new ArrayList<Cookie>();

    private MyHttpclient2() {

    }

    /**
     * 单例模式获取HttpClient类
     *
     * @return HttpClient类的实例
     */
    public static synchronized DefaultHttpClient getHttpClient() {
        if (myHttpClient == null) {
            HttpParams params = new BasicHttpParams();
            // 设置一些基本参数
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1); // Http版本
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8); // 设置编码
            HttpProtocolParams.setUseExpectContinue(params, true); /**
             It should be false in most cases.
             Expect-Continue is only needed when your request is large
             (like file uploading) and the server may have authorization requirement.
             You don't want send a huge file and get a Access Denied error.
             So you just send the headers first and if the server says continue, you will then send the whole request. */
            HttpProtocolParams.setUserAgent(params,
                    "Mozilla/5.0(X11;Ubuntu;Linux i686:rv:35.0) Gecko/20100101 FireFox/35.0");
            // 设置用户代理,通过浏览器的方式
            // 超时设置
            ConnManagerParams.setTimeout(params, 1000); // 从连接池中取得连接的超时时间
            HttpConnectionParams.setConnectionTimeout(params, 2000); // 连接超时
            HttpConnectionParams.setSoTimeout(params, 4000); //请求超时
            // 设置支持ＨＴＴＰＳ和ＨＴＴＰ
            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            // schReg.register(new Scheme("https", org.apache.http.conn.ssl.SSLSocketFactory.getSocketFactory(), 443));
            // 线程安全设置
            ClientConnectionManager conManager = new ThreadSafeClientConnManager(params, schReg);
            myHttpClient = new DefaultHttpClient(conManager, params);
        }
        return myHttpClient;
    }

    /**
     * 进行ｐｏｓｔ请求,进行登陆和需要提交cookie的时候使用
     *
     * @param client       HttpClient实例
     * @param url          　链接地址
     * @param list         需要提交的数据的Ｌｉｓｔ
     * @param isSaveCookie true 保存登陆时的ｃｏｏｋｉｅ
     *                     false 添加cookie并进行请求
     * @return 请求后返回的信息
     */
    public static String doPost(DefaultHttpClient client, String url,
                                List<NameValuePair> list, boolean isSaveCookie) {
        if (isSaveCookie) {
            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, HTTP.UTF_8); // 设置编码
                HttpPost post = new HttpPost(url.trim()); // 进行ｐｏｓｔ请求
                post.setEntity(entity); // 加入请求头
                /*Log.d(TESTHTTPCLIENT,"请求头内容："+post.getAllHeaders().toString()+"::"
                        +post.getHeaders("email").toString()+"::"+post.getEntity().getContent().toString());*/
                HttpResponse response = client.execute(post); // 执行ｐｏｓｔ请求
                int codeReturn = response.getStatusLine().getStatusCode(); //　获取返回码
                if (codeReturn == HttpStatus.SC_OK) {
                    Log.d(TESTHTTPCLIENT, "请求成功");
                    HttpEntity httpEntity = response.getEntity();
                    // 判断是否保存Cookie
                    if (isSaveCookie) {
                        // 获取Ｃｏｏｋｉｅ
                        cookieStore = client.getCookieStore();
                        if (cookieStore == null) {
                            Log.d("TestApp", "未获取到Ｃｏｏｋｉｅ");
                        }
                    }
                    return httpEntity == null ? null : EntityUtils.toString(httpEntity);
                } else {
                    Log.d(TESTHTTPCLIENT, "请求失败::" + codeReturn);
                    return null;
                }
            } catch (UnsupportedEncodingException e) {
                return null;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            if (list != null) {
                // 提交Ｃｏｏｋｉｅ和表单
                // 创建Ｈｔｔｐ的上下文
                HttpContext httpContext = new BasicHttpContext();
                try {
                    //UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,HTTP.UTF_8); // 设置编码
                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                    for (int index = 0; index < list.size(); index++) {
                        if (list.get(index).getName().equalsIgnoreCase("img") ||
                                list.get(index).getName().equalsIgnoreCase("headImg") ||
                                list.get(index).getName().equalsIgnoreCase("doc")) {
                            // 将NameValuePair中的地址取出，通过地址获取文件并上传
                            entity.addPart(list.get(index).getName(),
                                    new FileBody(new File(list.get(index).getValue())));
                            Log.d("TestApp", "已经提交文件！");
                        } else {
                            // 普通文字
                            entity.addPart(list.get(index).getName(),
                                    new StringBody(list.get(index).getValue()));
                        }
                    }
                    // 取出Cookie并添加到请求头
                    if (cookieStore != null) {
                        client.setCookieStore(cookieStore);
                        Log.d("TestApp", "提交Ｃｏｏｋie完毕！" + cookieStore.toString());
                    } else {
                        Log.d("TestApp", "提交Ｃｏｏｋｉｅ时，未获取到ｃｏｏｋｉｅ");
                    }
                    HttpPost post = new HttpPost(url.trim()); // 进行ｐｏｓｔ请求
                    post.setEntity(entity); // 加入请求头
                    HttpResponse response = client.execute(post, httpContext); // 执行ｐｏｓｔ请求
                    int codeReturn = response.getStatusLine().getStatusCode(); //　获取返回码
                    if (codeReturn == HttpStatus.SC_OK) {
                        Log.d(TESTHTTPCLIENT, "请求成功");
                        HttpEntity httpEntity = response.getEntity();
                        return httpEntity == null ? null : EntityUtils.toString(httpEntity);
                    } else {
                        Log.d(TESTHTTPCLIENT, "请求失败");
                        return null;
                    }
                } catch (UnsupportedEncodingException e) {
                    return null;
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                // 只提交Ｃｏｏｋｉｅ
                try {
                    // UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,HTTP.UTF_8); // 设置编码
                    // 取出Cookie并添加到请求头
                    if (cookieStore != null) {
                        client.setCookieStore(cookieStore);
                        Log.d("TestApp", "提交Ｃｏｏｋie完毕！" + cookieStore.toString());
                    } else {
                        Log.d("TestApp", "提交Ｃｏｏｋｉｅ时，未获取到ｃｏｏｋｉｅ");
                    }
                    HttpPost post = new HttpPost(url.trim()); // 进行ｐｏｓｔ请求
                    //post.setEntity(entity); // 加入请求头
               /* Log.d(TESTHTTPCLIENT,"请求头内容："+post.getAllHeaders().toString()+"::"
                        +post.getHeaders("email").toString()+"::"+post.getEntity().getContent().toString());*/
                    HttpResponse response = client.execute(post); // 执行ｐｏｓｔ请求
                    int codeReturn = response.getStatusLine().getStatusCode(); //　获取返回码
                    if (codeReturn == HttpStatus.SC_OK) {
                        Log.d(TESTHTTPCLIENT, "请求成功");
                        HttpEntity httpEntity = response.getEntity();
                        return httpEntity == null ? null : EntityUtils.toString(httpEntity);
                    } else {
                        Log.d(TESTHTTPCLIENT, "请求失败");
                        return null;
                    }
                } catch (UnsupportedEncodingException e) {
                    return null;
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

    }

    /**
     * 进行ｐｏｓｔ请求,进行登陆和需要提交cookie的时候使用
     *
     * @param client HttpClient实例
     * @param url    　链接地址
     * @param list   需要提交的数据的Ｌｉｓｔ
     * @return 请求后返回的信息
     */
    public static String doPost(DefaultHttpClient client, String url,
                                List<NameValuePair> list) {
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, HTTP.UTF_8); // 设置编码
            HttpPost post = new HttpPost(url.trim()); // 进行ｐｏｓｔ请求
            post.setEntity(entity); // 加入请求头
            Log.d(TESTHTTPCLIENT, "请求头内容：" + post.getAllHeaders().toString() + "::"
                    + post.getHeaders("email").toString() + "::" + post.getEntity().getContent().toString());
            HttpResponse response = client.execute(post); // 执行ｐｏｓｔ请求
            int codeReturn = response.getStatusLine().getStatusCode(); //　获取返回码
            if (codeReturn == HttpStatus.SC_OK) {
                Log.d(TESTHTTPCLIENT, "请求成功");
                HttpEntity httpEntity = response.getEntity();
                return httpEntity == null ? null : EntityUtils.toString(httpEntity);
            } else {
                Log.d(TESTHTTPCLIENT, "请求失败");
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            return null;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 执行GET请求
     *
     * @param client     HttpClient实例
     * @param url        请求的链接
     * @param map        需要提交的参数，放在ｍａｐ中
     * @param sendCookie 是否需要提交Ｃｏｏｋｉｅ
     *                   true 需要　false　不需要
     * @return 获取ｘｍｌ信息
     */
    public static String doGet(DefaultHttpClient client, String url, Map<String, String> map, boolean sendCookie) {
        StringBuilder sb = null;
        if (map != null) {
            // url重新构造，UTF-8编码
            sb = new StringBuilder(url);
            sb.append('?');
            // ?page=1&tags="计算机"(全部则为all)
            for (Map.Entry<String, String> entry : map.entrySet()) {
                try {
                    sb.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                            .append('&');
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            Log.d("TestApp", "构造的ＵＲＬ为：" + sb.toString());
        } else {
            sb = new StringBuilder(url);
        }


        // 开始请求操作
        try {
            HttpGet httpGet = new HttpGet(sb.toString());
            if (sendCookie) {
                Log.d ("cout","" + cookieStore.toString());
                if (cookieStore != null) {
                    client.setCookieStore(cookieStore);
                    Log.d("TestApp", "提交ｃｏｏｋｉｅ");
                } else {
                    Log.d("TestApp", "不需要提交ｃｏｏｋｉｅ");
                }
            }
            HttpResponse response = client.execute(httpGet);
            // Header[] headers = response.getHeaders("Content-Disposition");
            int codeReturn = response.getStatusLine().getStatusCode(); //　获取返回码
            if (codeReturn == HttpStatus.SC_OK) {
                Log.d(TESTHTTPCLIENT, "请求成功");
                HttpEntity httpEntity = response.getEntity();
                return httpEntity == null ? null : EntityUtils.toString(httpEntity);
            } else {
                Log.d(TESTHTTPCLIENT, "请求失败");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean doGetFileDownload(DefaultHttpClient client, String url) {
        if (cookieStore != null) {
            client.setCookieStore(cookieStore);
            Log.d("TestApp", "提交ｃｏｏｋｉｅ");
        } else {
            Log.d("TestApp", "不需要提交ｃｏｏｋｉｅ");
        }

        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = client.execute(httpGet);
            Header[] headers = response.getHeaders("Content-Disposition");
            // 输出所有Header的内容
            for (Header header : headers) {
                Log.d("TestApp", header.getName() + header.getValue() + header.getElements());
            }
            int codeReturn = response.getStatusLine().getStatusCode(); //　获取返回码
            if (codeReturn == HttpStatus.SC_OK) {
                Log.d(TESTHTTPCLIENT, "请求成功");
                String filePath = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/1.doc"; // 文件路径
                Log.d(TESTHTTPCLIENT, "文件保存路径：" + filePath);

                File file = new File(filePath);
                FileOutputStream outputStream = new FileOutputStream(file);
                InputStream inputStream = response.getEntity()
                        .getContent();
                byte b[] = new byte[1024];
                int j = 0;
                while ((j = inputStream.read(b)) != -1) {
                    outputStream.write(b, 0, j);
                }
                outputStream.flush();
                outputStream.close();
                return true;
            } else {
                Log.d(TESTHTTPCLIENT, "请求失败");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 多线程下载用的方法
     *
     * @param client   　DefaultHttpClient实例
     * @param url      　下载链接
     * @param file     　文件存储路径
     * @param startPos 　开始下载的位置
     * @param endPos   　结束的位置
     * @return　下载的进度
     */
    public static int doGetDownload(DefaultHttpClient client,
                                    String url, File file, int startPos, int endPos) {
        BufferedInputStream bis = null;
        RandomAccessFile raf = null;
        boolean isComplete = false;
        byte[] buffer = new byte[1024];
        int len = -1;

        HttpGet httpGet = new HttpGet(url);
        // 提交cookie
        if (cookieStore != null) {
            client.setCookieStore(cookieStore);
            Log.d("TestApp", "提交ｃｏｏｋｉｅ");
        } else {
            Log.d("TestApp", "ｃｏｏｋｉｅ为空！");
        }
        // 设置下载的起点和终点
        httpGet.addHeader("Ranges", "bytes=" + startPos + "-" + endPos);
        try {
            HttpResponse response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Log.d(TESTHTTPCLIENT, "请求成功");
                bis = new BufferedInputStream(response.getEntity().getContent());
                // Log.d(TESTHTTPCLIENT,"");
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(startPos);
                while ((len = bis.read(buffer, 0, 1024)) != -1) {
                    raf.write(buffer, 0, len);
                }
            } else {
                Log.d(TESTHTTPCLIENT, "请求失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return len;
    }


    /**
     * 已输入流的形式创建文件
     * @param inputStream 文件输入流
     * @return 返回需要的文件
     */
    /*private static File createFileFromInputStream(InputStream inputStream) {

        try{
            File f = new File("picture_test.jpg");
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[4096];
            int length = 0;

            while((length=inputStream.read(buffer)) > 0) {
                outputStream.write(buffer,0,length);
            }

            outputStream.close();
            inputStream.close();

            return f;
        }catch (IOException e) {
            //Logging exception
        }

        return null;
    }*/

    /**
     * 进行ｐｏｓｔ请求,进行登陆和需要提交cookie的时候使用
     * @param client HttpClient实例
     * @param url　链接地址
     * @param list 需要提交的数据的Ｌｉｓｔ
     * @param isSaveCookie true 保存登陆时的ｃｏｏｋｉｅ
     *                     false 添加cookie并进行请求
     * @return 请求后返回的信息
     */
   /* public static String doPost(DefaultHttpClient client, String url,
                                List<NameValuePair> list,boolean isSaveCookie)*//*{
        if(isSaveCookie){
            try{
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,HTTP.UTF_8); // 设置编码
                HttpPost post = new HttpPost(url.trim()); // 进行ｐｏｓｔ请求
                post.setEntity(entity); // 加入请求头
                *//*Log.d(TESTHTTPCLIENT,"请求头内容："+post.getAllHeaders().toString()+"::"
                        +post.getHeaders("email").toString()+"::"+post.getEntity().getContent().toString());*//*
                HttpResponse response = client.execute(post); // 执行ｐｏｓｔ请求
                int codeReturn = response.getStatusLine().getStatusCode(); //　获取返回码
                if(codeReturn == HttpStatus.SC_OK){
                    Log.d(TESTHTTPCLIENT,"请求成功");
                    HttpEntity httpEntity = response.getEntity();
                    // 判断是否保存Cookie
                    if(isSaveCookie){
                        // 获取Ｃｏｏｋｉｅ
                        cookieStore = client.getCookieStore();
                        if(cookieStore == null){
                            Log.d("TestApp","未获取到Ｃｏｏｋｉｅ");
                        }
                    }
                    return httpEntity==null?null: EntityUtils.toString(httpEntity);
                } else {
                    Log.d(TESTHTTPCLIENT,"请求失败::"+codeReturn);
                    return null;
                }
            } catch (UnsupportedEncodingException e){
                return null;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            if (list != null) {
                // 提交Ｃｏｏｋｉｅ和表单
                try{
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,HTTP.UTF_8); // 设置编码
                    // 取出Cookie并添加到请求头
                    if (cookieStore != null) {
                        client.setCookieStore(cookieStore);
                        Log.d("TestApp", "提交Ｃｏｏｋie完毕！" + cookieStore.toString());
                    } else {
                        Log.d("TestApp", "提交Ｃｏｏｋｉｅ时，未获取到ｃｏｏｋｉｅ");
                    }
                    HttpPost post = new HttpPost(url.trim()); // 进行ｐｏｓｔ请求
                    post.setEntity(entity); // 加入请求头
               *//* Log.d(TESTHTTPCLIENT,"请求头内容："+post.getAllHeaders().toString()+"::"
                        +post.getHeaders("email").toString()+"::"+post.getEntity().getContent().toString());*//*
                    HttpResponse response = client.execute(post); // 执行ｐｏｓｔ请求
                    int codeReturn = response.getStatusLine().getStatusCode(); //　获取返回码
                    if (codeReturn == HttpStatus.SC_OK) {
                        Log.d(TESTHTTPCLIENT, "请求成功");
                        HttpEntity httpEntity = response.getEntity();
                        return httpEntity == null ? null : EntityUtils.toString(httpEntity);
                    } else {
                        Log.d(TESTHTTPCLIENT, "请求失败");
                        return null;
                    }
                }catch(UnsupportedEncodingException e){
                    return null;
                }catch(ClientProtocolException e){
                    e.printStackTrace();
                    return null;
                }catch(IOException e){
                    e.printStackTrace();
                    return null;
                }
            } else {
                // 只提交Ｃｏｏｋｉｅ
                try{
                    // UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,HTTP.UTF_8); // 设置编码
                    // 取出Cookie并添加到请求头
                    if (cookieStore != null) {
                        client.setCookieStore(cookieStore);
                        Log.d("TestApp", "提交Ｃｏｏｋie完毕！" + cookieStore.toString());
                    } else {
                        Log.d("TestApp", "提交Ｃｏｏｋｉｅ时，未获取到ｃｏｏｋｉｅ");
                    }
                    HttpPost post = new HttpPost(url.trim()); // 进行ｐｏｓｔ请求
                    //post.setEntity(entity); // 加入请求头
               *//* Log.d(TESTHTTPCLIENT,"请求头内容："+post.getAllHeaders().toString()+"::"
                        +post.getHeaders("email").toString()+"::"+post.getEntity().getContent().toString());*//*
                    HttpResponse response = client.execute(post); // 执行ｐｏｓｔ请求
                    int codeReturn = response.getStatusLine().getStatusCode(); //　获取返回码
                    if (codeReturn == HttpStatus.SC_OK) {
                        Log.d(TESTHTTPCLIENT, "请求成功");
                        HttpEntity httpEntity = response.getEntity();
                        return httpEntity == null ? null : EntityUtils.toString(httpEntity);
                    } else {
                        Log.d(TESTHTTPCLIENT, "请求失败");
                        return null;
                    }
                }catch(UnsupportedEncodingException e){
                    return null;
                }catch(ClientProtocolException e){
                    e.printStackTrace();
                    return null;
                }catch(IOException e){
                    e.printStackTrace();
                    return null;
                }
            }
        }

    }*/
}
