package com.sys.elsearchclient;

import com.sys.elsearchclient.index.Index;
import com.sys.elsearchclient.index.IndexMetaProvider;
import com.sys.elsearchclient.index.MappingSchema;
import com.sys.elsearchclient.index.Property;

import java.util.Map;

public class ElasticClient {

    private ElasticRestClient elasticRestClient;

    private final IndexMetaProvider<? extends Index> indexMetaProvider;

    private final MappingSchema schema;

    public static ElasticClient getClient(Class<? extends Index> idxClass) {
        return new ElasticClient(idxClass);
    }

    private ElasticClient(Class<? extends Index> idxClass) {
        this.elasticRestClient = ElasticRestClient.getInstance();
        this.indexMetaProvider = new IndexMetaProvider<>(idxClass);
        this.schema = new MappingSchema(this.indexMetaProvider.getProperties());
    }

    public void createIndex() {
        this.elasticRestClient.createIndex(indexMetaProvider.indexName(), schema);
    }

    public void deleteIndex() {
        this.elasticRestClient.deleteIndex(indexMetaProvider.indexName());
    }

    public boolean indexExists() {
        return this.elasticRestClient.indexExists(indexMetaProvider.indexName());
    }

    public void deleteDocument(String docId) {
        this.elasticRestClient.deleteDocument(indexMetaProvider.indexName(), docId);
    }

    public String putDocument(Map<Property, String> source) {
        return this.elasticRestClient.putDocument(indexMetaProvider.indexName(), source);
    }

    public boolean documentExists(String docId) {
        return this.elasticRestClient.documentExists(indexMetaProvider.indexName(), docId);
    }

    public String getDocument(String docId) {
        return this.elasticRestClient.getDocument(indexMetaProvider.indexName(), docId);
    }
}
