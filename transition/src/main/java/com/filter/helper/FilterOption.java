package com.filter.helper;

import java.util.Objects;

public class FilterOption {
    private MagicFilterType filter;
    private int resource;

    public FilterOption(MagicFilterType filter, int resource) {
        this.filter = filter;
        this.resource = resource;
    }

    public MagicFilterType getFilter() {
        return filter;
    }

    public void setFilter(MagicFilterType filter) {
        this.filter = filter;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilterOption that = (FilterOption) o;
        return filter == that.filter;
    }
}
