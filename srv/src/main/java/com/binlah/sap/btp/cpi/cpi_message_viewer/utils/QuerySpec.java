package com.binlah.sap.btp.cpi.cpi_message_viewer.utils;

import java.util.*;

public class QuerySpec {
    private final List<String> select = new ArrayList<>();
    private final StringBuilder filterRaw = new StringBuilder();
    private final StringBuilder expandRaw = new StringBuilder();
    private Integer top;
    private Integer skip;
    private final Map<String, String> passthrough = new LinkedHashMap<>();

    public List<String> getSelect() {
        return select;
    }

    public Optional<String> getFilterRaw() {
        return Optional.ofNullable(filterRaw.length() == 0 ? null : filterRaw.toString());
    }

    public Optional<String> getExpandRaw() {
        return Optional.ofNullable(expandRaw.length() == 0 ? null : expandRaw.toString());
    }

    public Optional<Integer> getTop() {
        return Optional.ofNullable(top);
    }

    public Optional<Integer> getSkip() {
        return Optional.ofNullable(skip);
    }

    public Map<String, String> getPassthrough() {
        return passthrough;
    }

    public QuerySpec setTop(Integer top) {
        this.top = top;
        return this;
    }

    public QuerySpec setSkip(Integer skip) {
        this.skip = skip;
        return this;
    }

    public QuerySpec addSelect(String property) {
        if (property != null && !property.isEmpty())
            select.add(property);
        return this;
    }

    public QuerySpec setFilterRaw(String raw) {
        filterRaw.setLength(0);
        if (raw != null)
            filterRaw.append(raw);
        return this;
    }

    public QuerySpec setExpandRaw(String raw) {
        expandRaw.setLength(0);
        if (raw != null)
            expandRaw.append(raw);
        return this;
    }

    public QuerySpec putRaw(String key, String value) {
        if (key != null && value != null)
            passthrough.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "QuerySpec{select=" + select +
                ", filter=" + getFilterRaw().orElse(null) +
                ", expand=" + getExpandRaw().orElse(null) +
                ", top=" + top + ", skip=" + skip +
                ", passthrough=" + passthrough + "}";
    }

}
