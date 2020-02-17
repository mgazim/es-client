package com.sys.elsearchclient;

import com.sys.elsearchclient.model.OCPMetrics;
import org.junit.Test;

public class ELClientTest {

    @Test
    public void test() {

        ElasticClient ocpMetrics = ElasticClient.getClient(OCPMetrics.class);
        if (ocpMetrics.indexExists()) {
            ocpMetrics.deleteIndex();
        }
        ocpMetrics.createIndex();
    }
}
