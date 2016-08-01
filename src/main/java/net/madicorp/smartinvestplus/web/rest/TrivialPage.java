package net.madicorp.smartinvestplus.web.rest;

import groovy.transform.EqualsAndHashCode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * User: sennen
 * Date: 31/07/2016
 * Time: 22:08
 */
@EqualsAndHashCode
public class TrivialPage implements Pageable {
    private final int page;
    private final int size;
    private final Sort sort;

    public TrivialPage(Integer page, Integer size) {
        this(page, size, null);
    }

    public TrivialPage(Integer page, Integer size, Sort sort) {
        this.page = page != null ? page : 0;
        this.size = size != null ? size : 30;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return page;
    }

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public int getOffset() {
        return (page + 1) * size;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new TrivialPage(getPageNumber() + 1, getPageSize(), getSort());
    }

    @Override
    public Pageable previousOrFirst() {
        return getPageNumber() == 0 ? this : new TrivialPage(getPageNumber() - 1, getPageSize(), getSort());
    }

    @Override
    public Pageable first() {
        return new TrivialPage(0, getPageSize(), getSort());
    }

    @Override
    public boolean hasPrevious() {
        return getPageNumber() > 0;
    }
}
