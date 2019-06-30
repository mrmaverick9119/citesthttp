package com.mgage.httputil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HTTPRequestUtility {
	private Logger _log = Logger.getLogger(HTTPRequestUtility.class);
	static RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(500).setConnectTimeout(500).build();

	static HashMap<String, RequestConfig> map = new HashMap<String, RequestConfig>();

	public static RequestConfig getSetRequestConfig(String host_port, Map<String, String> config) {
		if (host_port == null) {
			return requestConfig;
		}

		RequestConfig reqconf = map.get(host_port);

		if (reqconf == null) {
			return buildConfig(host_port, config);
		} else
			return reqconf;
	}

	private static synchronized RequestConfig buildConfig(String host_port, Map<String, String> conf) {
		// TODO Auto-generated method stub
		{
			RequestConfig reqconf = null;
			try {
				System.out.println("DOING CONFIG FORV1" + host_port);
				Builder bl = RequestConfig.custom();
				String strconnectTimeOut = conf.get("connectTimeOut");
				if (strconnectTimeOut == null)
					strconnectTimeOut = HTTPClientFactory.configAttributes.getProperty("connectTimeOut", "2000");

				String strsocketTimeOut = conf.get("socketTimeOut");
				if (strsocketTimeOut == null)
					strsocketTimeOut = HTTPClientFactory.configAttributes.getProperty("socketTimeOut", "2000");

				String strproxyEnabled = conf.get("proxyEnabled");
				if (strproxyEnabled == null)
					strproxyEnabled = HTTPClientFactory.configAttributes.getProperty("proxyEnabled", "false");

				boolean proxyEnabled = Boolean.parseBoolean(strproxyEnabled);
				if (proxyEnabled) {
					String strhost = conf.get("proxy_host");
					String strport = conf.get("proxy_port");
					if (strhost == null) {
						strhost = HTTPClientFactory.configAttributes.getProperty("proxy_host", "localhost");
					}
					if (strport == null) {
						strport = HTTPClientFactory.configAttributes.getProperty("proxy_port", "80");
					}

					bl.setProxy(new HttpHost(strhost, Integer.parseInt(strport)));
				}

				String strauthEnabled = conf.get("authEnabled");

				if (strauthEnabled == null)
					strauthEnabled = HTTPClientFactory.configAttributes.getProperty("authEnabled", "false");

				bl.setAuthenticationEnabled(Boolean.parseBoolean(strauthEnabled));
				bl.setSocketTimeout(Integer.parseInt(strsocketTimeOut));
				bl.setConnectTimeout(Integer.parseInt(strconnectTimeOut));
				bl.setConnectionRequestTimeout(Integer.parseInt(strconnectTimeOut));
				reqconf = bl.build();
				map.put(host_port, reqconf);
			} catch (Exception e) {
				System.out.println("Error/Exception Building reqconf" + host_port);
				reqconf = requestConfig;
			}

			return reqconf;
		}

	}

	private CloseableHttpClient getClient(String url) {
		if (url.startsWith("https")) {
			return HTTPClientFactory.getInstance().getHttpsClient();
		} else {
			return HTTPClientFactory.getInstance().getHttpClient();
		}
	}

	public HTTPResult doSendGZipPayloadRequest(String baseurl, Map<String, String> headerMapToPost, String hostPort,
			String dataToPost) {
		HttpPost httppost = new HttpPost(baseurl);

		if (headerMapToPost == null) {
			headerMapToPost = new HashMap<String, String>();
		}

		Iterator it = headerMapToPost.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String name = (String) pairs.getKey();
			String value = (String) pairs.getValue();
			if (value != null) {
				httppost.addHeader(name, value);
			}
		}

		HTTPResult rs = new HTTPResult();

		CloseableHttpResponse response = null;
		ByteArrayOutputStream compressedStream = null;
		GZIPOutputStream gzipOutputStream = null;
		ByteArrayInputStream inputStream = null;

		try {
			compressedStream = new ByteArrayOutputStream();
			gzipOutputStream = new GZIPOutputStream(compressedStream);
			gzipOutputStream.write(dataToPost.getBytes(StandardCharsets.UTF_8));
			gzipOutputStream.close();
			byte data[] = compressedStream.toByteArray();
			inputStream = new ByteArrayInputStream(data);
			InputStreamEntity inputStreamEntity = new InputStreamEntity(inputStream);
			httppost.setHeader("Content-Encoding", "gzip");
			// System.out.println("Uncompresed Length:" + jsonPayload.length());
			// System.out.println("Compressed Data Length:" + data.length);
			httppost.setEntity(inputStreamEntity);
			// HttpResponse httpResponse = httpclient.execute(httpPost);
			// statusCode = httpResponse.getStatusLine().getStatusCode()+"";
			///////

			// entity.setChunked(true);
			// httppost.setEntity(entity);
			httppost.setConfig(getSetRequestConfig(baseurl, headerMapToPost));
			HttpContext context = HttpClientContext.create();

			response = getClient(baseurl).execute(httppost, context);
			HttpEntity respentity = response.getEntity();

			Header[] ht = response.getAllHeaders();

			if (ht != null) {
				HashMap<String, String> resheader = new HashMap<String, String>();
				for (int i = 0; i < ht.length; i++) {
					resheader.put(ht[i].getName(), ht[i].getValue());
				}
				rs.header = resheader;
			}

			rs.status = response.getStatusLine().getStatusCode();
			rs.result = EntityUtils.toString(respentity);
			EntityUtils.consumeQuietly(respentity);
		} catch (SocketTimeoutException socketTimeout) {
			rs.status = -101;
			rs.result = socketTimeout.getMessage();
		} catch (ConnectTimeoutException connectTimeout) {

			rs.status = -102;
			rs.result = connectTimeout.getMessage();

		} catch (Throwable e) {
			rs.result = e.toString();
		} finally {
			closeResource(response);
			closeResource(compressedStream);
			closeResource(gzipOutputStream);
			closeResource(inputStream);
		}
		return rs;
	}

	public HTTPResult doTestGetRequest(String completeurl, Map<String, String> headerMap, String hostPort,
			final long max_time) {

		if (headerMap == null) {
			headerMap = new HashMap<String, String>();
		}

		HttpGet httppost = new HttpGet(completeurl);
		Iterator it = headerMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String name = (String) pairs.getKey();
			String value = (String) pairs.getValue();
			if (value != null) {
				httppost.addHeader(name, value);
			}
		}
		String charset = headerMap.get("charset");

		if (charset == null) {
			charset = "ISO-8859-1";
		}

		HTTPResult rs = new HTTPResult();

		CloseableHttpResponse response = null;

		try {

			HttpContext context = HttpClientContext.create();
			// headerMap.
			httppost.setConfig(getSetRequestConfig(hostPort, headerMap));

			final CloseableHttpClient hc = getClient(completeurl);

			/*
			 * Thread th= new Thread(new Runnable() {
			 * 
			 * @Override public void run() { // TODO Auto-generated method stub //Add Timer
			 * try{ Thread.sleep(max_time); }catch(Exception e){}
			 * 
			 * try{ hc.close(); } catch(Exception e){} } });
			 * 
			 * th.start();
			 */
			response = getClient(completeurl).execute(httppost, context);
			HttpEntity respentity = response.getEntity();

			Header[] ht = response.getAllHeaders();

			if (ht != null) {
				HashMap<String, String> resheader = new HashMap<String, String>();
				for (int i = 0; i < ht.length; i++) {
					resheader.put(ht[i].getName(), ht[i].getValue());
				}
				rs.header = resheader;
			}

			rs.status = response.getStatusLine().getStatusCode();
			rs.result = EntityUtils.toString(respentity);
			EntityUtils.consumeQuietly(respentity);
		} catch (SocketTimeoutException socketTimeout) {
			rs.status = -101;
			rs.result = socketTimeout.getMessage();
		} catch (ConnectTimeoutException connectTimeout) {

			rs.status = -102;
			rs.result = connectTimeout.getMessage();

		} catch (Throwable e) {
			rs.result = e.toString();
		} finally {
			closeResource(response);
		}
		return rs;

	}

	public HTTPResult doGetRequest(String baseurl, Map<String, String> headerMap, Map<String, String> parameters) {

		HttpGet httpget = null;
		String host_port = baseurl;

		if (parameters == null) {
			parameters = new HashMap<String, String>();
		}

		if (headerMap == null) {
			headerMap = new HashMap<String, String>();
		}

		Iterator it = parameters.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String name = (String) pairs.getKey();
			String value = (String) pairs.getValue();

			if (value != null) {
				baseurl = baseurl + encode(name) + "=" + encode(value) + "&";
			}
		}

		if (baseurl.endsWith("&")) {
			baseurl = baseurl.substring(0, baseurl.length() - 1);
		}

		HTTPResult rs = new HTTPResult();

		CloseableHttpResponse response = null;

		try {

			httpget = new HttpGet(baseurl);
			httpget.setConfig(getSetRequestConfig(host_port, parameters));

			it = headerMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				String name = (String) pairs.getKey();
				String value = (String) pairs.getValue();
				if (value != null) {
					httpget.addHeader(name, value);
				}
			}

			String charset = headerMap.get("charset");

			if (charset == null) {
				charset = "ISO-8859-1";
			}

			HttpContext context = HttpClientContext.create();
			CloseableHttpClient hc = getClient(baseurl);

			response = hc.execute(httpget, context);

			Header[] ht = response.getAllHeaders();

			if (ht != null) {
				HashMap<String, String> resheader = new HashMap<String, String>();
				for (int i = 0; i < ht.length; i++) {
					resheader.put(ht[i].getName(), ht[i].getValue());
				}
				rs.header = resheader;
			}

			HttpEntity respentity = response.getEntity();
			// response.get
			rs.status = response.getStatusLine().getStatusCode();
			rs.result = EntityUtils.toString(respentity);
			EntityUtils.consumeQuietly(respentity);
		} catch (SocketTimeoutException socketTimeout) {
			socketTimeout.printStackTrace();
			rs.status = -101;
			rs.result = socketTimeout.getMessage();

		} catch (ConnectTimeoutException connectTimeout) {
			connectTimeout.printStackTrace();
			rs.status = -102;
			rs.result = connectTimeout.getMessage();
			_log.error("ErrorForURL102<>" + baseurl + "<>");
		} catch (Throwable e) {
			_log.error("ErrorForURLOther<>" + baseurl + "<>" + e.getMessage());
			rs.result = e.toString();
		} finally {
			closeResource(response);
		}

		if (rs.status > 0 && (rs.status > 399)) {
			_log.error("ErrorHandover<>" + baseurl + "<>" + rs.status);
		}

		return rs;
	}

	public HTTPResult doGetRequest(String completeurl, Map<String, String> headerMap, String hostPort) {

		if (headerMap == null) {
			headerMap = new HashMap<String, String>();
		}

		HttpGet httppost = new HttpGet(completeurl);
		Iterator it = headerMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String name = (String) pairs.getKey();
			String value = (String) pairs.getValue();
			if (value != null) {
				httppost.addHeader(name, value);
			}
		}
		String charset = headerMap.get("charset");

		if (charset == null) {
			charset = "ISO-8859-1";
		}

		HTTPResult rs = new HTTPResult();

		CloseableHttpResponse response = null;

		try {

			HttpContext context = HttpClientContext.create();
			httppost.setConfig(getSetRequestConfig(hostPort, headerMap));

			response = getClient(completeurl).execute(httppost, context);
			HttpEntity respentity = response.getEntity();

			Header[] ht = response.getAllHeaders();

			if (ht != null) {
				HashMap<String, String> resheader = new HashMap<String, String>();
				for (int i = 0; i < ht.length; i++) {
					resheader.put(ht[i].getName(), ht[i].getValue());
				}
				rs.header = resheader;
			}

			rs.status = response.getStatusLine().getStatusCode();
			rs.result = EntityUtils.toString(respentity);
			EntityUtils.consumeQuietly(respentity);
		} catch (SocketTimeoutException socketTimeout) {
			socketTimeout.printStackTrace();
			rs.status = -101;
			rs.result = socketTimeout.getMessage();
		} catch (ConnectTimeoutException connectTimeout) {
			connectTimeout.printStackTrace();
			rs.status = -102;
			rs.result = connectTimeout.getMessage();

		} catch (Throwable e) {
			System.out.println("Exception Handover " + e.toString());

			rs.result = e.toString();
		} finally {
			closeResource(response);
		}
		return rs;

	}

	public HttpResultInBytes doGetRequestInBytes(String completeurl, Map<String, String> headerMap) {

		if (headerMap == null) {
			headerMap = new HashMap<String, String>();
		}

		HttpGet httppost = new HttpGet(completeurl);
		Iterator it = headerMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String name = (String) pairs.getKey();
			String value = (String) pairs.getValue();
			if (value != null) {
				httppost.addHeader(name, value);
			}
		}
		String charset = headerMap.get("charset");

		if (charset == null) {
			charset = "ISO-8859-1";
		}

		HttpResultInBytes rs = new HttpResultInBytes();

		CloseableHttpResponse response = null;

		try {

			HttpContext context = HttpClientContext.create();
			httppost.setConfig(getSetRequestConfig(completeurl, headerMap));

			response = getClient(completeurl).execute(httppost, context);
			HttpEntity respentity = response.getEntity();

			Header[] ht = response.getAllHeaders();

			if (ht != null) {
				HashMap<String, String> resheader = new HashMap<String, String>();
				for (int i = 0; i < ht.length; i++) {
					resheader.put(ht[i].getName(), ht[i].getValue());
				}
				rs.header = resheader;
			}

			rs.status = response.getStatusLine().getStatusCode();
			rs.result = EntityUtils.toByteArray(respentity);
			EntityUtils.consumeQuietly(respentity);
		} catch (SocketTimeoutException socketTimeout) {
			socketTimeout.printStackTrace();
			rs.status = -101;
			rs.comment = socketTimeout.getMessage();
		} catch (ConnectTimeoutException connectTimeout) {
			connectTimeout.printStackTrace();
			rs.status = -102;
			rs.comment = connectTimeout.getMessage();

		} catch (Throwable e) {
			System.out.println("Exception Handover " + e.toString());
			rs.comment = e.toString();
		} finally {
			closeResource(response);
		}
		return rs;

	}

	public HTTPResult doPostRequest(String baseurl, Map<String, String> headerMapToPost,
			Map<String, String> dataToPost) {

		HttpPost httppost = new HttpPost(baseurl);

		if (headerMapToPost == null) {
			headerMapToPost = new HashMap<String, String>();
		}

		if (dataToPost == null) {
			dataToPost = new HashMap<String, String>();
		}

		Iterator it = headerMapToPost.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String name = (String) pairs.getKey();
			String value = (String) pairs.getValue();
			if (value != null) {
				httppost.addHeader(name, value);
			}
		}

		it = dataToPost.entrySet().iterator();

		List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();

		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String name = (String) pairs.getKey();
			String value = (String) pairs.getValue();

			if (value != null) {
				formparams.add(new BasicNameValuePair(name, value));
			}
		}

		String charset = headerMapToPost.get("charset");

		if (charset == null) {
			charset = "ISO-8859-1";
		}

		HTTPResult rs = new HTTPResult();

		CloseableHttpResponse response = null;

		try {

			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, charset);
			entity.setChunked(true);
			httppost.setEntity(entity);
			HttpContext context = HttpClientContext.create();
			httppost.setConfig(getSetRequestConfig(baseurl, headerMapToPost));

			response = getClient(baseurl).execute(httppost, context);
			HttpEntity respentity = response.getEntity();

			Header[] ht = response.getAllHeaders();

			if (ht != null) {
				HashMap<String, String> resheader = new HashMap<String, String>();
				for (int i = 0; i < ht.length; i++) {
					resheader.put(ht[i].getName(), ht[i].getValue());
				}
				rs.header = resheader;
			}

			rs.status = response.getStatusLine().getStatusCode();
			rs.result = EntityUtils.toString(respentity);
			EntityUtils.consumeQuietly(respentity);
		} catch (SocketTimeoutException socketTimeout) {
			rs.status = -101;
			rs.result = socketTimeout.getMessage();
		} catch (ConnectTimeoutException connectTimeout) {

			rs.status = -102;
			rs.result = connectTimeout.getMessage();

		} catch (Throwable e) {
			rs.result = e.toString();
		} finally {
			closeResource(response);
		}
		return rs;
	}

	public HTTPResult doDeleteRequest(String host, String url, Map<String, String> headerMap)

	{
		HttpDelete httpdelete = new HttpDelete(url);

		if (headerMap == null) {
			headerMap = new HashMap<String, String>();
		}

		Iterator it = headerMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String name = (String) pairs.getKey();
			String value = (String) pairs.getValue();
			if (value != null) {
				httpdelete.addHeader(name, value);
			}
		}

		HTTPResult rs = new HTTPResult();

		CloseableHttpResponse response = null;
		try {
			// request.setEntity(new ByteArrayEntity(body));

			// entity.setChunked(true);

			httpdelete.setConfig(getSetRequestConfig(host, headerMap));
			HttpContext context = HttpClientContext.create();

			response = getClient(url).execute(httpdelete, context);
			HttpEntity respentity = response.getEntity();

			Header[] ht = response.getAllHeaders();

			if (ht != null) {
				HashMap<String, String> resheader = new HashMap<String, String>();
				for (int i = 0; i < ht.length; i++) {
					resheader.put(ht[i].getName(), ht[i].getValue());
				}
				rs.header = resheader;
			}

			rs.status = response.getStatusLine().getStatusCode();
			rs.result = EntityUtils.toString(respentity);
			EntityUtils.consumeQuietly(respentity);
		} catch (SocketTimeoutException socketTimeout) {
			rs.status = -101;
			rs.result = socketTimeout.getMessage();
		} catch (ConnectTimeoutException connectTimeout) {

			rs.status = -102;
			rs.result = connectTimeout.getMessage();

		} catch (Throwable e) {
			rs.result = e.toString();
		} finally {
			closeResource(response);

		}
		return rs;
	}

	public HTTPResult doPostBytesDataRequest(String baseurl, Map<String, String> headerMapToPost, byte[] dataToPost)

	{
		HttpPost httppost = new HttpPost(baseurl);

		if (headerMapToPost == null) {
			headerMapToPost = new HashMap<String, String>();
		}

		Iterator it = headerMapToPost.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String name = (String) pairs.getKey();
			String value = (String) pairs.getValue();
			if (value != null) {
				httppost.addHeader(name, value);
			}
		}

		String content_type = headerMapToPost.get("Content-Type");
		String charset = headerMapToPost.get("charset");

		if (content_type == null) {
			content_type = "application/x-binary";
		}

		if (charset == null) {
			charset = "ISO-8859-1";
		}

		HTTPResult rs = new HTTPResult();

		CloseableHttpResponse response = null;
		try {
			// request.setEntity(new ByteArrayEntity(body));

			// entity.setChunked(true);
			httppost.setEntity(new ByteArrayEntity(dataToPost));
			httppost.setConfig(getSetRequestConfig(baseurl, headerMapToPost));
			HttpContext context = HttpClientContext.create();

			response = getClient(baseurl).execute(httppost, context);
			HttpEntity respentity = response.getEntity();

			Header[] ht = response.getAllHeaders();

			if (ht != null) {
				HashMap<String, String> resheader = new HashMap<String, String>();
				for (int i = 0; i < ht.length; i++) {
					resheader.put(ht[i].getName(), ht[i].getValue());
				}
				rs.header = resheader;
			}

			rs.status = response.getStatusLine().getStatusCode();
			rs.result = EntityUtils.toString(respentity);
			EntityUtils.consumeQuietly(respentity);
		} catch (SocketTimeoutException socketTimeout) {
			rs.status = -101;
			rs.result = socketTimeout.getMessage();
		} catch (ConnectTimeoutException connectTimeout) {

			rs.status = -102;
			rs.result = connectTimeout.getMessage();

		} catch (Throwable e) {
			rs.result = e.toString();
		} finally {
			closeResource(response);

		}
		return rs;
	}

	public HTTPResult doPostRequestV1(String baseurl, Map<String, String> headerMapToPost, String dataToPost)

	{
		HttpPost httppost = new HttpPost(baseurl);

		if (headerMapToPost == null) {
			headerMapToPost = new HashMap<String, String>();
		}

		Iterator it = headerMapToPost.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String name = (String) pairs.getKey();
			String value = (String) pairs.getValue();
			if (value != null) {
				httppost.addHeader(name, value);
			}
		}

		String content_type = headerMapToPost.get("Content-Type");
		String charset = headerMapToPost.get("charset");

		if (content_type == null) {
			content_type = "application/json";
		}

		if (charset == null) {
			charset = "UTF-8";
		}
		HTTPResult rs = new HTTPResult();

		CloseableHttpResponse response = null;
		try {
			StringEntity entity = new StringEntity(dataToPost, ContentType.create(content_type, charset));

			// entity.setChunked(true);
			httppost.setEntity(entity);
			httppost.setConfig(getSetRequestConfig(baseurl, headerMapToPost));
			HttpContext context = HttpClientContext.create();

			response = getClient(baseurl).execute(httppost, context);
			HttpEntity respentity = response.getEntity();

			Header[] ht = response.getAllHeaders();

			if (ht != null) {
				HashMap<String, String> resheader = new HashMap<String, String>();
				for (int i = 0; i < ht.length; i++) {
					resheader.put(ht[i].getName(), ht[i].getValue());
				}
				rs.header = resheader;
			}

			rs.status = response.getStatusLine().getStatusCode();
			rs.result = EntityUtils.toString(respentity);
			EntityUtils.consumeQuietly(respentity);
		} catch (SocketTimeoutException socketTimeout) {
			rs.status = -101;
			rs.result = socketTimeout.getMessage();
		} catch (ConnectTimeoutException connectTimeout) {

			rs.status = -102;
			rs.result = connectTimeout.getMessage();

		} catch (Throwable e) {
			rs.result = e.toString();
		} finally {
			closeResource(response);

		}
		return rs;
	}

	public HTTPResult doPostRequest(String baseurl, Map<String, String> headerMapToPost, String dataToPost) {

		HttpPost httppost = new HttpPost(baseurl);

		if (headerMapToPost == null) {
			headerMapToPost = new HashMap<String, String>();
		}

		Iterator it = headerMapToPost.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String name = (String) pairs.getKey();
			String value = (String) pairs.getValue();
			if (value != null) {
				httppost.addHeader(name, value);
			}
		}

		String content_type = headerMapToPost.get("content_type");
		String charset = headerMapToPost.get("charset");

		if (content_type == null) {
			content_type = "text/xml";
		}

		if (charset == null) {
			charset = "ISO-8859-1";
		}
		HTTPResult rs = new HTTPResult();

		CloseableHttpResponse response = null;
		try {
			StringEntity entity = new StringEntity(dataToPost, ContentType.create(content_type, charset));

			// entity.setChunked(true);
			httppost.setEntity(entity);
			httppost.setConfig(getSetRequestConfig(baseurl, headerMapToPost));
			HttpContext context = HttpClientContext.create();

			response = getClient(baseurl).execute(httppost, context);
			HttpEntity respentity = response.getEntity();

			Header[] ht = response.getAllHeaders();

			if (ht != null) {
				HashMap<String, String> resheader = new HashMap<String, String>();
				for (int i = 0; i < ht.length; i++) {
					resheader.put(ht[i].getName(), ht[i].getValue());
				}
				rs.header = resheader;
			}

			rs.status = response.getStatusLine().getStatusCode();
			rs.result = EntityUtils.toString(respentity);
			EntityUtils.consumeQuietly(respentity);
		} catch (SocketTimeoutException socketTimeout) {
			rs.status = -101;
			rs.result = socketTimeout.getMessage();
		} catch (ConnectTimeoutException connectTimeout) {

			rs.status = -102;
			rs.result = connectTimeout.getMessage();

		} catch (Throwable e) {
			rs.result = e.toString();
		} finally {
			closeResource(response);

		}
		return rs;

	}

	void closeResource(Closeable closeable) {
		try {
			closeable.close();
		} catch (Exception e) {
		}
	}

	public static String encode(String val) {
		try {
			return URLEncoder.encode(val, "UTF-8");
		} catch (Exception e) {
			// TODO: handle exception
		}

		return "";

	}

	public static String decode(String val) {
		try {
			return URLDecoder.decode(val, "UTF-8");
		} catch (Exception e) {
			// TODO: handle exception
		}

		return "";

	}

}
