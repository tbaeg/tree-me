package tree.me.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tree.me.core.api.ANode;
import tree.me.core.api.AValueHolder;
import tree.me.core.api.AValueHolder.AValueHolderBuilder;
import tree.me.core.index.Node;
import tree.me.core.index.Path;
import tree.me.service.TreeMeIndex;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static tree.me.core.api.AValueHolder.AValueType.GROUP;
import static tree.me.core.api.AValueHolder.AValueType.RESOURCE;
import static tree.me.core.index.Path.ROOT;

public abstract class AbstractTreeMeApi<T> implements TreeMeApi<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTreeMeApi.class);

    private TreeMeIndex treeMeIndex;

    public AbstractTreeMeApi(TreeMeIndex treeMeIndex) {
        this.treeMeIndex = treeMeIndex;
        init();
    }

    /**
     * Callback executed on invocation of {@link #get(Path)} through the {@link TreeMeApi}.
     * This is your underlying get implementation for your {@link T} resource.
     *
     * @param id
     * @return
     */
    protected abstract T onGetResource(String id);

    /**
     * Callback executed on invocation of {@link #saveResource(Path, String, String, Object)}
     * through the {@link TreeMeApi}. This is your underlying save implementation for
     * your {@link T} resource.
     *
     * @param resource
     */
    protected abstract void onSaveResource(T resource);

    /**
     * Callback executed on invocation of {@link #saveGroup(Path, String)}
     * through the {@link TreeMeApi}. This will mostly for convenience and
     * is not required.
     *
     * @param path
     * @param name
     */
    protected abstract void onSaveGroup(Path path, String name);

    /**
     * Callback executed on invocation of {@link #delete(Path)} through the
     * {@link TreeMeApi} for a non-group {@link Path}. This is your underlying
     * delete implementation for your {@link T} resource.
     *
     * @param id
     */
    protected abstract void onResourceDelete(String id);

    /**
     * Callback executed on invocation of {@link #delete(Path)} through the
     * {@link TreeMeApi} for a group {@link Path}. You are provided all the IDs
     * that were underneath the given {@link Path}.
     *
     * @param resourceIdsToDelete
     */
    protected abstract void onGroupDelete(Iterable<String> resourceIdsToDelete);

    /**
     * Unique string representation of your type associated to your {@link TreeMeApi}.
     *
     * @return
     */
    protected abstract String getType();

    /**
     * Set of base initialization code for bootstrapping your tree.
     */
    protected void init() {
        LOG.info("Initializing root node: " + getType());
        treeMeIndex.save(new Node(ROOT, getType(), getType(), false, ""));
        LOG.info("Initialized root node: " + getType());
    }

    @Override
    public Iterable<ANode> getTree() {
        Optional<Iterable<Node>> optNodes = treeMeIndex.getAllByPath(new Path(), getType());

        if (!optNodes.isPresent()) {
            return newArrayList();
        }

        List<Node> nodes = newArrayList(optNodes.get());

        // build tree
        Map<String, ANode> nodeMap = newHashMap();

        nodeMap.put(ROOT, new ANode(ROOT, getType(), "", false, "", newArrayList()));
        nodes.forEach(node -> nodeMap.put(node.getPath(), toANode(node)));

        for (ANode node : nodeMap.values()) {
            Path path = Path.from(node.getPath());

            if (path.isRoot()) {
                continue;
            }

            nodeMap.get(path.getParentPath()).addChild(node);
        }

        return nodeMap.get(ROOT).getChildren();
    }

    @Override
    public Optional<AValueHolder> get(Path path) {
        Optional<Node> optNode = treeMeIndex.getByPath(path, getType());

        if (!optNode.isPresent()) {
            return ofNullable(null);
        }

        AValueHolderBuilder holderBuilder = AValueHolder.builder();
        Node node = optNode.get();

        if (!node.getLeaf()) {
            holderBuilder.type(GROUP);
            holderBuilder.value(getGroup(path).get());
            LOG.debug("Requested path was a group.");
        } else {
            holderBuilder.type(RESOURCE);
            holderBuilder.value(onGetResource(node.getValue()));
            LOG.debug("Requested path was a resource.");
        }

        return ofNullable(holderBuilder.build());
    }

    @Override
    public Optional<T> getResource(Path path) {
        Optional<Node> optNode = treeMeIndex.getByPath(path, getType());

        if (!optNode.isPresent()) {
            return ofNullable(null);
        }

        return ofNullable(onGetResource(optNode.get().getValue()));
    }

    @Override
    public Optional<Iterable<ANode>> getGroup(Path path) {
        Optional<Iterable<Node>> children = treeMeIndex.getChildrenByPath(path, getType());

        if (!children.isPresent()) {
            ofNullable(null);
        }

        return ofNullable(newArrayList(children.get()).stream()
                .map(child -> toANode(child))
                .collect(toList()));
    }

    @Override
    public void saveResource(Path path, String name, String resourceId, T resource) {
        Optional<Node> optNode = treeMeIndex.getByPath(path, getType());

        if (optNode.isPresent()) {
            if (optNode.get().getLeaf()) {
                throw new RuntimeException("Path you are trying to save the resource to is a group. Path: " + path.getPath());
            }
        } else {
            treeMeIndex.save(new Node(path.getPath(), getType(), name, true, resourceId));
        }

        onSaveResource(resource);
    }

    @Override
    public void saveGroup(Path path, String name) {
        treeMeIndex.save(new Node(path.getPath(), getType(), name, false, ""));
        onSaveGroup(path, name);
    }

    @Override
    public void delete(Path path) {
        Node node = treeMeIndex.getByPath(path, getType()).orElseThrow(invalidPath);

        if (node.getLeaf()) {
            onResourceDelete(node.getValue());
        } else {
            // We already have path existence checks done above, so we know there will be a value
            // for the call to getAllByPath
            onGroupDelete(newArrayList(treeMeIndex.getAllByPath(path, getType()).get()).stream()
                    .filter(n -> !n.getLeaf())
                    .map(n -> n.getValue())
                    .collect(toList()));
        }
        treeMeIndex.deleteByPath(path, getType());
    }

    /**
     * Helper to convert between the two node types.
     *
     * @param node
     * @return
     */
    private static ANode toANode(Node node) {
        return new ANode(node.getPath(), node.getType(), node.getName(), node.getLeaf(), node.getValue(), newArrayList());
    }

    private Supplier<RuntimeException> invalidPath = () -> new RuntimeException("Invalid path.");
}
