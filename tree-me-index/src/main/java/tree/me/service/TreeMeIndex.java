package tree.me.service;

import tree.me.core.index.Node;
import tree.me.core.index.Path;

import java.util.Optional;

public interface TreeMeIndex {

    /**
     * Fetch a {@link Node} by {@link Path} and model.
     *
     * @param path
     * @param type
     * @return
     */
    Optional<Node> getByPath(Path path, String type);

    /**
     * Fetch all direct children {@link Node} under the {@link Node} with specified {@link Path}.
     *
     * @param path
     * @param type
     * @return
     */
    Optional<Iterable<Node>> getChildrenByPath(Path path, String type);

    /**
     * Fetch ALL children {@link Node} under the {@link Node} with specified {@link Path}.
     *
     * @param path
     * @param type
     * @return
     */
    Optional<Iterable<Node>> getAllByPath(Path path, String type);

    /**
     * Save a {@link Node}. Calls to save overwrite existing {@link Node}.
     *
     * @param node
     */
    void save(Node node);

    /**
     * /**
     * Delete a {@link Node} by {@link Path}.
     *
     * @param path
     */
    void deleteByPath(Path path, String type);

}