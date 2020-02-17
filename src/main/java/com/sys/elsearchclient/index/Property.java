package com.sys.elsearchclient.index;

import java.util.List;
import java.util.Objects;

public class Property {

    private String name;
    private Type   type;
    private String format;

    private List<Property> innerProperties;

    public Property() {
    }

    public Property(String name) {
        this(name, null);
    }

    public Property(String name, Type type) {
        Objects.requireNonNull(name);
        this.name = name;
        this.type = Objects.requireNonNullElse(type, Type.TEXT);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Property> getInnerProperties() {
        return innerProperties;
    }

    public void setInnerProperties(List<Property> innerProperties) {
        this.innerProperties = innerProperties;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public enum Type {
        DATE,
        NESTED,
        LONG,
        INTEGER,
        TEXT;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    @Override
    public String toString() {
        return "Property{" + "name='" + name + '\'' + ", type=" + type + ", format=" + format + (innerProperties != null ?
                ", innerProperties=" + innerProperties :
                "") + '}';
    }

}
