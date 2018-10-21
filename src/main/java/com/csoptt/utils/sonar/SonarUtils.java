package com.csoptt.utils.sonar;

import com.csoptt.utils.sonar.bean.MeasureComponent;
import com.csoptt.utils.sonar.query.MeasureComponentQuery;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.wsclient.Host;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.connectors.HttpClient4Connector;
import org.sonar.wsclient.services.Measure;

/**
 * 利用sonar查看代码质量的工具类
 *
 * @author qishao
 * @date 2018-09-25
 */
public class SonarUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SonarUtils.class);

    public static void main(String[] args) throws Exception {
        // HttpGet
        String url = "http://10.10.10.37:9000/api/measures/component";

        HttpClientContext context = HttpClientContext.create();
        CredentialsProvider provider = new BasicCredentialsProvider();
        Credentials credentials = new UsernamePasswordCredentials("admin", "admin");
        provider.setCredentials(AuthScope.ANY, credentials);
        context.setCredentialsProvider(provider);

        CloseableHttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        HttpGet get = new HttpGet(url);

//        Host host = new Host("http://10.10.10.37:9000", "admin", "admin");
//        HttpClient4Connector connector = new HttpClient4Connector(host);
//        Sonar sonar = new Sonar(connector);
//        MeasureComponent measureComponent = sonar.find(MeasureComponentQuery.create("competitive", "ncloc"));
//        Measure measure = measureComponent.getComponent().getMeasures().get(0);
//        Double value = measure.getValue();
//        LOGGER.info(String.valueOf(value));
    }
}
