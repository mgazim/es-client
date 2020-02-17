package com.sys.elsearchclient.index;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MappingSchema {

    private static final Logger LOG = Logger.getLogger(MappingSchema.class.getName());

    private final XContentBuilder schemaBuilder;

    private final List<Property> properties;

    public MappingSchema(List<Property> properties) {
        this.properties = Collections.unmodifiableList(properties);
        try {
            this.schemaBuilder = XContentFactory.jsonBuilder();
        }
        catch (IOException e) {
            final String message = "Error initializing jsonBuilder";
            LOG.log(Level.SEVERE, message);
            throw new ElasticsearchException(message);
        }
    }

    public List<Property> getProperties() {
        return this.properties;
    }

    public XContentBuilder asMappingSchema() throws IOException {
        schemaBuilder.startObject();
        schemaBuilder.startObject("properties");
        for (Property property : properties) {
            analyseProperty(property);
        }
        schemaBuilder.endObject(); // end of properties
        schemaBuilder.endObject(); // end of mappings
        return schemaBuilder;
    }

    private void analyseProperty(Property property) throws IOException {
        schemaBuilder.startObject(property.getName());
        Property.Type type = property.getType();
        schemaBuilder.field("type", type);
        String format = property.getFormat();
        if (format != null) {
            schemaBuilder.field("format", format);
        }
        if (type == Property.Type.NESTED) {
            schemaBuilder.startObject("properties");
            for (Property innerProperty : property.getInnerProperties()) {
                analyseProperty(innerProperty);
            }
            schemaBuilder.endObject(); // end of properties
        }
        schemaBuilder.endObject(); // end of property
    }
}
