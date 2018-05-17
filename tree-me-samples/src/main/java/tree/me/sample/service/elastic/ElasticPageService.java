package tree.me.sample.service.elastic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tree.me.sample.core.Page;
import tree.me.sample.service.PageService;

import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static tree.me.sample.service.elastic.ElasticPageMappings.MAPPING;

public class ElasticPageService implements PageService {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticPageService.class);

    public static final String PAGE_ELASTIC_INDEX = "unifier.dataindex.page";
    public static final String PAGE_ELASTIC_TYPE = "page";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Client client;

    public ElasticPageService(Client client) {
        this.client = client;
        init();
    }

    @Override
    public Optional<Page> get(String id) throws RuntimeException {
        try {
            GetResponse get = client.prepareGet(PAGE_ELASTIC_INDEX, PAGE_ELASTIC_TYPE, id).get();

            if (get.isExists()) {
                Page page = MAPPER.convertValue(get.getSource(), Page.class);

                LOG.info("Found page: " + page.toString());
                return ofNullable(page);
            }

            return ofNullable(null);
        } catch (ElasticsearchException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Page page) throws RuntimeException {
        try {
            Map pageMap = MAPPER.convertValue(page, Map.class);

            UpdateResponse update = client.prepareUpdate(PAGE_ELASTIC_INDEX, PAGE_ELASTIC_TYPE, page.getId())
                    .setDocAsUpsert(true)
                    .setUpsert(pageMap)
                    .setDoc(pageMap)
                    .get();

            if (update.isCreated()) {
                LOG.info("Created new Page: " + page.toString() + ".");
            }
        } catch (ElasticsearchException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String id) {
        DeleteResponse delete = client.prepareDelete(PAGE_ELASTIC_INDEX, PAGE_ELASTIC_TYPE, id).get();

        if (!delete.isFound()) {
            LOG.info("Page of ID: " + id + " not found.");
        }
    }

    private void init() {
        ClusterHealthResponse health = client.admin()
                .cluster()
                .prepareHealth()
                .setWaitForYellowStatus()
                .get();
        ClusterHealthStatus clusterStatus = health.getStatus();

        LOG.info("Elasticsearch cluster is: " + clusterStatus.name());

        client.admin()
                .indices()
                .prepareCreate(PAGE_ELASTIC_INDEX)
                .addMapping(PAGE_ELASTIC_TYPE, MAPPING)
                .get();

        ClusterHealthResponse indexStatus = client.admin()
                .cluster()
                .prepareHealth(PAGE_ELASTIC_INDEX)
                .setWaitForActiveShards(1)
                .get();

        LOG.info("Elasticsearch " + PAGE_ELASTIC_INDEX + " has: " + indexStatus.getActivePrimaryShards() + " active shards.");
    }
}
