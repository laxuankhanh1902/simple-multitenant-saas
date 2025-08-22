package io.conduktor.saas.common.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class SearchRequest {

    private String query;
    private int page = 0;
    private int size = 20;
    private List<String> sortBy;
    private Sort.Direction sortDirection = Sort.Direction.ASC;

    public SearchRequest() {}

    public SearchRequest(String query) {
        this.query = query;
    }

    public SearchRequest(String query, int page, int size) {
        this.query = query;
        this.page = page;
        this.size = size;
    }

    // Getters and Setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = Math.max(0, page); }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = Math.min(Math.max(1, size), 100); }

    public List<String> getSortBy() { return sortBy; }
    public void setSortBy(List<String> sortBy) { this.sortBy = sortBy; }

    public Sort.Direction getSortDirection() { return sortDirection; }
    public void setSortDirection(Sort.Direction sortDirection) { this.sortDirection = sortDirection; }

    public Pageable toPageable() {
        if (sortBy != null && !sortBy.isEmpty()) {
            return PageRequest.of(page, size, Sort.by(sortDirection, sortBy.toArray(new String[0])));
        }
        return PageRequest.of(page, size);
    }

    public boolean hasQuery() {
        return query != null && !query.trim().isEmpty();
    }
}