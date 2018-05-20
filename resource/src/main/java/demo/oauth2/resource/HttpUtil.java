package demo.oauth2.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpUtil {
	public static final int LIMIT = 10;
	public static final int MAX_LIMIT = 10000;
	
	public static final int HTTP_REQUEST_SUCCESS = -1;
	public static final int HTTP_REQUEST_ERROR = -2;

	public static String sessionId = null;
	public static boolean needSessionId = false;
	
	public static final String USER_AGENT = "Mozilla/5.0 (compatible; MSIE 6.0; Windows NT)";
	public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
	public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
	public static final String CHARSET = "UTF-8";
	public static final String PREFIX = "--";
	public static final String LINE_END = "\r\n";

	public static String getOauth2ServerBaseUrl() {
		return "http://localhost:8003";
	}
	
	public static String getResponseResult(HttpURLConnection conn) throws IOException {
		int respCode = conn.getResponseCode();
		String result = null;
		
		if(respCode == 200){
			InputStream in = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String tmp = null;
			StringBuffer sb = new StringBuffer();
			while((tmp=br.readLine())!=null){
				sb.append(tmp);
			}
			result = sb.toString();
			br.close();
			in.close();
		} else if(respCode == 301) {
			setSessionId(getSessionId(conn.getHeaderFields()));
			
		} else if(respCode == 500) {
			throw new RuntimeException("服务器错误！");
		} else {
			throw new RuntimeException("服务器状态错误！" + respCode);
		}
		return result;
	}
	
	public static String getSessionId() throws Exception {
		if(sessionId == null) {
			post("", new HashMap<String, String>());
		}
		return sessionId;
	}
	
	public static String getSessionId(Map<String,List<String>> headerFields) {
		Set<String> set = headerFields.keySet();  
        for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {  
            String key = (String) iterator.next();  
            if (key != null && key.equals("Set-Cookie")) { 
                List<String> list = headerFields.get(key); 
                for (String str : list) {  
                	if(str.startsWith("JSESSIONID")) {
                		return str.split(";")[0];
                	} 
                }
            }
        }
        return null;
	}
	
	public static String get(String urlPattern, Map<String, String> params) throws Exception {
		StringBuffer getParams = new StringBuffer();
		getParams.append(urlPattern);
		
		if(params.size() > 0) {
			getParams.append("?");
		}
		for(Entry<String, String> param: params.entrySet()) {
			getParams.append(param.getKey()).append("=").append(param.getValue()).append("&");
		}
		if(getParams.length() > 0) {
			getParams.deleteCharAt(getParams.length() - 1);
		}
		return get(urlPattern);
	}
	
	public static String get(String urlPattern) throws Exception {
		URL uri = new URL(urlPattern);
		if("https".equalsIgnoreCase(uri.getProtocol())){
            ignoreSsl();
        }
		
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		HttpURLConnection.setFollowRedirects(false);
		conn.setInstanceFollowRedirects(false);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		
		conn.setRequestProperty("User-Agent", USER_AGENT);  
		conn.setRequestProperty("Content-Type", CONTENT_TYPE_FORM);
		if(sessionId != null) {
			conn.setRequestProperty("Cookie", sessionId);
		}
		
		String result = getResponseResult(conn);
		
		if(needSessionId) {
			setSessionId(getSessionId(conn.getHeaderFields()));
		}
		
        conn.disconnect();
        
		return result;
	}
	
	public static String post(String urlPattern, Map<String, String> params) throws Exception {
		StringBuffer post = new StringBuffer();
		for(Entry<String, String> param: params.entrySet()) {
			post.append(param.getKey()).append("=").append(param.getValue()).append("&");
		}
		if(post.length() > 0) {
			post.deleteCharAt(post.length() - 1);
		}
		
		return post(urlPattern, post.toString());
	}
	
	public static String post(String urlPattern, String condition) throws Exception {
		URL uri = new URL(urlPattern);
		if("https".equalsIgnoreCase(uri.getProtocol())){
            ignoreSsl();
        }
		
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		HttpURLConnection.setFollowRedirects(false);
		conn.setInstanceFollowRedirects(false);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		
		conn.setRequestProperty("User-Agent", USER_AGENT);  
		conn.setRequestProperty("Content-Type", CONTENT_TYPE_FORM);
		if(sessionId != null) {
			conn.setRequestProperty("Cookie", sessionId);
		}
		
		if(condition != null && condition.length() > 0) {
			PrintStream send = new PrintStream(conn.getOutputStream());  
	        send.print(condition);
	        send.close();
		}
		
		String result = getResponseResult(conn);

		if(needSessionId) {
			setSessionId(getSessionId(conn.getHeaderFields()));
		}
		
        conn.disconnect();
        
		return result;
	}
	
	public static void setSessionId(String session) {
		if(HttpUtil.sessionId == null || session != null && !HttpUtil.sessionId.equals(session)) {
			HttpUtil.sessionId = session;
		}
	}
	
	private static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
    static class miTM implements TrustManager,X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }
        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }
        public void checkServerTrusted(X509Certificate[] certs, String authType)
                throws CertificateException {
            return;
        }
        public void checkClientTrusted(X509Certificate[] certs, String authType)
                throws CertificateException {
            return;
        }
    }
    /**
     * 忽略HTTPS请求的SSL证书，必须在openConnection之前调用
     * @throws Exception
     */
    public static void ignoreSsl() throws Exception{
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                return true;
            }
        };
        trustAllHttpsCertificates();
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }
}
