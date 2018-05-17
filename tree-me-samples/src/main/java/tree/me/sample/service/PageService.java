package tree.me.sample.service;

import tree.me.sample.core.Page;

import java.util.Optional;

public interface PageService {

    /**
     * Fetch a {@link Page} by ID.
     *
     * @param id
     * @return
     */
    Optional<Page> get(String id);

    /**
     * Save a {@link Page}.
     *
     * @param page
     */
    void save(Page page);


    /**
     * Delete a {@link Page} by ID.
     *
     * @param id
     */
    void delete(String id);

}
