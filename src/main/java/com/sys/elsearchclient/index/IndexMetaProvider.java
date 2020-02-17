package com.sys.elsearchclient.index;

import com.sys.elsearchclient.annotation.ElasticDateFormat;
import com.sys.elsearchclient.annotation.ElasticIndex;
import com.sys.elsearchclient.annotation.ElasticNestedProperty;
import com.sys.elsearchclient.annotation.ElasticProperty;
import com.sys.elsearchclient.annotation.NotAProperty;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class IndexMetaProvider<T extends Index> {

    private final Class<T> clazz;

    private final String indexName;

    public IndexMetaProvider(Class<T> clazz) {
        this.clazz = clazz;
        this.indexName = retrieveIndexName();
    }

    public String indexName() {
        return this.indexName;
    }

    private String retrieveIndexName() {
        if (clazz.isAnnotationPresent(ElasticIndex.class)) {
            ElasticIndex elIdx = clazz.getAnnotation(ElasticIndex.class);
            return elIdx.value();
        }
        return clazz.getSimpleName();
    }

    public List<Property> getProperties() {
        List<Property> properties = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(NotAProperty.class)) {
                Property property = createProperty(field);
                properties.add(property);
            }
        }
        return properties;
    }

    private Property createProperty(Field field) {
        Property property = new Property();
        ElasticProperty ann = field.getAnnotation(ElasticProperty.class);
        if (ann == null || ann.value().isEmpty()) {
            property.setName(field.getName());
        } else {
            property.setName(ann.value());
        }
        Property.Type type = mapType(field.getType());
        property.setType(type);
        if (type == Property.Type.NESTED) {
            List<Property> inner = new ArrayList<>();
            for (Field innerField : field.getType().getDeclaredFields()) {
                if (!innerField.isAnnotationPresent(NotAProperty.class)) {
                    inner.add(createProperty(innerField));
                }
            }
            property.setInnerProperties(inner);
        } else if (type == Property.Type.DATE) {
            ElasticDateFormat format = field.getAnnotation(ElasticDateFormat.class);
            if (format != null) {
                property.setFormat(format.value());
            }
        }
        return property;
    }

    private boolean checkIfNested(Class type) {
        return type.isAnnotationPresent(ElasticNestedProperty.class);
    }

    private Property.Type mapType(Class type) {
        String className = type.getSimpleName();
        if (className.equalsIgnoreCase("int")) {
            return Property.Type.INTEGER;
        } else if (className.equalsIgnoreCase("long")) {
            return Property.Type.LONG;
        } else if (className.equalsIgnoreCase("Date")) {
            return Property.Type.DATE;
        } else if (checkIfNested(type)) {
            return Property.Type.NESTED;
        } else if (className.equalsIgnoreCase("String")) {
            return Property.Type.TEXT;
        } else {
            return Property.Type.TEXT;
        }
    }
}
