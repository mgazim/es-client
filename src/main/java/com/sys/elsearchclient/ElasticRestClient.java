package com.sys.elsearchclient;

import com.sys.elsearchclient.index.MappingSchema;
import com.sys.elsearchclient.index.Property;
import com.sys.elsearchclient.util.ElasticClientException;
import com.sys.elsearchclient.util.Logger;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class ElasticRestClient implements AutoCloseable {

    private static final Logger LOG = Logger.getLogger(ElasticRestClient.class);

    private final RestHighLevelClient client;

    private static ElasticRestClient elasticRestClient;

    static ElasticRestClient getInstance() {
        if (elasticRestClient == null) {
            elasticRestClient = new ElasticRestClient();
        }
        return elasticRestClient;
    }

    private ElasticRestClient() {
        this.client = createRestClient();
    }

    private RestHighLevelClient createRestClient() {
        String protocol = "http";
        String url = "localhost";
        int port = 9200;

        try (InputStream elProps = ElasticRestClient.class.getClassLoader().getResourceAsStream("elastic.properties")) {
            if (elProps == null) {
                LOG.warn("Unable to load elastic.properties, using default values");
            } else {
                Properties elasticProperties = new Properties();
                elasticProperties.load(elProps);
                protocol = elasticProperties.getProperty("protocol", "http");
                url = elasticProperties.getProperty("url", "localhost");
                port = Integer.parseInt(elasticProperties.getProperty("port", "9200"));
            }
        }
        catch (IOException e) {
            LOG.error("Error loading elastic.properties", e);
        }
        return new RestHighLevelClient(RestClient.builder(new HttpHost(url, port, protocol)));
    }

    void createIndex(String idxName, MappingSchema mappingSchema) {
        try {
            CreateIndexRequest request = new CreateIndexRequest(idxName).mapping(mappingSchema.asMappingSchema());
            client.indices().create(request, RequestOptions.DEFAULT);
        }
        catch (IOException e) {
            LOG.error("Error creating index", e);
            throw new ElasticClientException("Error creating index", e);
        }
    }

    void deleteIndex(String idxName) {
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(idxName);
            client.indices().delete(request, RequestOptions.DEFAULT);
        }
        catch (IOException e) {
            LOG.error("Error deleting index", e);
            throw new ElasticClientException("Error deleting index", e);
        }
    }

    void deleteDocument(String idxName, String docId) {
        try {
            DeleteRequest request = new DeleteRequest(idxName).id(docId);
            client.delete(request, RequestOptions.DEFAULT);
        }
        catch (IOException e) {
            LOG.error("Error deleting index [" + docId + "]", e);
            throw new ElasticClientException("Error deleting index [" + docId + "]", e);
        }
    }

    String putDocument(String idxName, Map<Property, String> source) {
        try {
            IndexRequest request = new IndexRequest(idxName).source(toElMap(source));
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            if (response.getResult() == DocWriteResponse.Result.CREATED) {
                return response.getId();
            } else {
                throw new ElasticClientException("Not indexed!");
            }
        }
        catch (IOException e) {
            LOG.error("Error indexing!", e);
            throw new ElasticClientException("Not indexed!", e);
        }
    }

    String getDocument(String idxName, String docId) {
        try {
            GetRequest request = new GetRequest(idxName).id(docId);
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            if (response.isExists()) {
                return response.getSourceAsString();
            } else {
                throw new ElasticClientException("Index with id [" + docId + "] not found");
            }
        }
        catch (IOException e) {
            LOG.error("Error retrieving index [" + docId + "]", e);
            throw new ElasticClientException("Error retrieving index [" + docId + "]", e);
        }
    }

    boolean indexExists(String idxName) {
        try {
            GetIndexRequest request = new GetIndexRequest(idxName);
            return client.indices().exists(request, RequestOptions.DEFAULT);
        }
        catch (IOException e) {
            LOG.error("Error checking if index [" + idxName + "] exists", e);
            throw new ElasticClientException("Error checking if index [" + idxName + "] exists", e);
        }
    }

    boolean documentExists(String idxName, String docId) {
        try {
            GetRequest request = new GetRequest(idxName).id(docId);
            return client.exists(request, RequestOptions.DEFAULT);
        }
        catch (IOException e) {
            LOG.error("Error checking if document [" + docId + "] exists", e);
            throw new ElasticClientException("Error checking if document [" + docId + "] exists", e);
        }
    }

    private Map<String, Object> toElMap(Map<Property, String> source) {
        Map<String, Object> elMap = new HashMap<>();
        for (Map.Entry<Property, String> entry : source.entrySet()) {
            elMap.put(entry.getKey().getName(), entry.getValue());
        }
        return elMap;
    }

    @Override
    public void close() {
        if (client != null) {
            try {
                client.close();
            }
            catch (IOException e) {
                LOG.error("Error closing client", e);
            }
        }
    }
}
