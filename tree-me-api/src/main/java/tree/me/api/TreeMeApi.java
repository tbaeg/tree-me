package tree.me.api;

import tree.me.core.api.ANode;
import tree.me.core.api.AValueHolder;
import tree.me.core.index.Path;

import java.util.Optional;

public interface TreeMeApi<T> {

    /**
     * Fetch an iterable of {@link ANode} which represents a tree view of
     * all your {@link T} resources and groups.
     *
     * @return
     */
    Iterable<ANode> getTree();

    /**
     * Fetch a single {@link T} resource.
     *
     * @param path
     * @return
     */
    Optional<AValueHolder> get(Path path);

    /**
     * Fetch a single {@link T} resource.
     *
     * @param path
     * @return
     */
    Optional<T> getResource(Path path);

    /**
     * Fetch all direct children under the {@link Path}. It's important to node, despite the fact
     * the {@link ANode} has a {@link ANode#getChildren()} method; when invoking this method
     * the nodes do NOT have children information.
     *
     * @param path
     * @return
     */
    Optional<Iterable<ANode>> getGroup(Path path);

    /**
     * Save a single {@link T} resource at {@link Path} with the name, resource ID and
     * the resource itself. There are several reasons for such a verbose
     * {@link #saveResource(Path, String, String, Object)} method.
     * <p>
     * Both resource {@link T} and {@link Path} are fairly self explanatory.
     * <p>
     * Less obvious, the name is used for display/readability purposes. When
     * a set of {@link ANode} end up returned (from the method {@link #get(Path)}),
     * it is common case for it to be at least discernible outside the context of an ID.
     * <p>
     * The resource ID is important as the ID that is passed to the {@link AbstractTreeMeApi#onGetResource(String)}
     * method in the {@link AbstractTreeMeApi} is in fact this particular ID.
     *
     * @param path
     * @param name
     * @param resourceId
     * @param resource
     */
    void saveResource(Path path, String name, String resourceId, T resource);

    /**
     * Save a group at {@link Path}.
     *
     * @param path
     * @param name
     */
    void saveGroup(Path path, String name);

    /**
     * Delete by {@link Path}.
     *
     * @param path
     */
    void delete(Path path);
}
