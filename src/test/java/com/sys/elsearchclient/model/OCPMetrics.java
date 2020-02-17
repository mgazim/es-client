package com.sys.elsearchclient.model;

import com.sys.elsearchclient.annotation.ElasticDateFormat;
import com.sys.elsearchclient.annotation.ElasticIndex;
import com.sys.elsearchclient.annotation.ElasticProperty;
import com.sys.elsearchclient.annotation.NotAProperty;
import com.sys.elsearchclient.index.Index;

import java.util.Date;

@ElasticIndex("ocp_metrics")
public class OCPMetrics implements Index {

    private String component;

    @ElasticProperty("total_rqsts")
    private int requests;

    @ElasticDateFormat(ElasticDateFormat.BASIC_DATE)
    private Date timestamp;

    @ElasticProperty("rest_methods")
    private RestMethods restMethods;

    @NotAProperty
    private String trans;
}
