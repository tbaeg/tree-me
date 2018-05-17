package tree.me.test;

import ironhide.client.IronhideClient;
import ironhide.core.test.IronhideTestServer;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.junit.After;
import org.junit.Before;

import java.io.File;

import static ironhide.transport.ConfigConstants.*;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;

public class IronhideTest {
    private IronhideTestServer elasticServer;
    private String dataPath;
    private static String CLUSTERNAME = "TestCluster";

    protected Client client;

    @Before
    public void setUp() throws Exception {
        elasticServer = new IronhideTestServer.IronhideTestServerBuilder()
                .setClusterName(CLUSTERNAME)
                .setHttpEnabled(false)
                .setLocal(false)
                .setUserSettings(
                        settingsBuilder()
                                .put("discovery.zen.ping.multicast.enabled", false)
                                .put(AUTHORIZATION_MODE, "settings")
                                .put(SETTINGSDB_AUTHS + "local_u", "U, FOUO")
                                .put(SERVER_WHITELIST, "elasticsearch")
                                .put(sslSettings(
                                        "./src/test/resources/elasticsearch.jks", "password",
                                        "./src/test/resources/elasticsearch.jks", "password"))
                                .build())
                .build();
        elasticServer.start();

        client = IronhideClient.builder()
                .setClusterName(CLUSTERNAME)
                .setKeystoreFile("./src/test/resources/elasticsearch.jks")
                .setKeystorePassword("password")
                .setTruststoreFile("./src/test/resources/elasticsearch.jks")
                .setTruststorePassword("password")
                .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", elasticServer.getPort()))
                .build();


        dataPath = elasticServer.node().settings().get("path.data");
    }

    @After
    public void tearDown() throws Exception {
        this.client.close();
        this.elasticServer.stop();
        this.elasticServer.node().close();
        this.client = null;
        this.elasticServer = null;
        deleteQuietly(new File(dataPath));
    }

    public void refresh() {
        new RefreshRequestBuilder(client.admin().indices()).get();
    }

    public IronhideTestServer getElasticServer() {
        return elasticServer;
    }
}
