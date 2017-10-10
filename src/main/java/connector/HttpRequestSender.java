package connector;


import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;

public class HttpRequestSender {
    private int defaultResponseSize = 1000;
    HttpClient client = HttpClientBuilder.create().disableAutomaticRetries().build();
    RequestConfig config;
    private Header[] headers;

    public HttpRequestSender(Integer connectionTimeout, Integer requestTimeout, Integer socketTimeout) {
        this.config = RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setConnectionRequestTimeout(requestTimeout)
                .setSocketTimeout(socketTimeout)
                .build();
    }

    public void setHeaders(Map<String, String> headers) {
        if (headers!=null) {
            this.headers = new Header[headers.size()];
            int i = 0;
            for (Map.Entry<String, String> header : headers.entrySet()) {
                this.headers[i] = new BasicHeader(header.getKey(), header.getValue());
                i++;
            }
        }
    }

    public Optional<String> doRequest(String url) {
        HttpGet httpRequest = new HttpGet(url);
        httpRequest.setHeaders(headers);
        httpRequest.setConfig(config);
        HttpResponse response = null;
        try {
            response = client.execute(httpRequest);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return deserializeResponse(response);
    }

    public Optional<String> deserializeResponse(HttpResponse response){
        StringBuilder result = null;
        try {
            int size = response.getEntity().getContentLength()>0?Math.toIntExact(response.getEntity().getContentLength()):defaultResponseSize;
            result = new StringBuilder(size);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(result.toString());
    }
}
