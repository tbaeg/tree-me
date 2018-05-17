package tree.me.service.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tree.me.core.index.Node;
import tree.me.core.index.Path;
import tree.me.service.TreeMeIndex;
import tree.me.service.exception.TreeMeDeleteException;
import tree.me.service.exception.TreeMeGetException;
import tree.me.service.exception.TreeMeSaveException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.ofNullable;
import static tree.me.core.index.Path.ROOT;
import static tree.me.service.sql.SqlTreeIndexStatements.*;

public class SqlTreeMeIndex implements TreeMeIndex {

    private static final Logger LOG = LoggerFactory.getLogger(SqlTreeMeIndex.class);

    private static final String PATH_COLUMN = "path";
    private static final String TYPE_COLUMN = "type";
    private static final String NAME_COLUMN = "name";
    private static final String LEAF_COLUMN = "leaf";
    private static final String VALUE_COLUMN = "value";

    private Connection connection;

    public SqlTreeMeIndex(Connection connection) throws SQLException {
        this.connection = connection;
        connection.setAutoCommit(true);
        init();
    }

    @Override
    public Optional<Node> getByPath(Path path, String type) {
        Node node = null;

        try {
            ResultSet result = getExactPath(connection, path.getPath(), type);
            List<Node> nodes = fromResultSet(result);

            if (nodes.size() > 1) {
                LOG.warn("Weird, looks like there is more than one node for this path." +
                        " You may want to investigate what's going on here!");
                ofNullable(nodes.get(0));
            }

            if (nodes.size() == 0) {
                return ofNullable(node);
            }

            if (nodes.size() == 1) {
                node = nodes.get(0);
            }
        } catch (SQLException e) {
            LOG.error("Failed to fetch node.", e);
            throw new TreeMeGetException(e);
        }

        return ofNullable(node);
    }

    @Override
    public Optional<Iterable<Node>> getChildrenByPath(Path path, String type) {
        try {
            if(!checkPathExists(path, type)) {
                return ofNullable(null);
            }

            return ofNullable(fromResultSet(getChildrenPath(connection, path.getPath(), type)));
        } catch (SQLException e) {
            LOG.error("Failed to fetch children for path " + path.getPath() + ".", e);
            throw new TreeMeGetException(e);
        }
    }

    @Override
    public Optional<Iterable<Node>> getAllByPath(Path path, String type) {
        try {
            if(!checkPathExists(path, type)) {
                return ofNullable(null);
            }

            return ofNullable(fromResultSet(getAllPath(connection, path.getPath(), type)));
        } catch (SQLException e) {
            LOG.error("Failed to fetch all for path " + path.getPath() + ".", e);
            throw new TreeMeGetException(e);
        }
    }

    @Override
    public void save(Node node) {
        try {
            Optional<Node> previous = getByPath(Path.from(node.getPath()), node.getType());

            if (previous.isPresent()) {
                checkBeforeUpdate(previous.get(), node);
                update(connection, node);
            } else {
                Path path = Path.from(node.getPath());

                if (path.isRoot()) {
                    if (node.getLeaf()) {
                        String msg = "Root nodes can't be leaf nodes.";
                        LOG.error(msg);
                        throw new TreeMeSaveException(msg);
                    } else {
                        create(connection, node);
                    }
                } else {
                    checkParentExists(path, node.getType());
                    create(connection, node);
                }
            }
            LOG.debug("Saved node!");
        } catch (SQLException e) {
            LOG.error("Failed to save node.", e);
            throw new TreeMeSaveException(e);
        }
    }

    @Override
    public void deleteByPath(Path path, String type) {
        try {
            Optional<Node> nodeOpt = getByPath(path, type);

            if (!nodeOpt.isPresent()) {
                return;
            }

            boolean cascade = true;

            if (nodeOpt.get().getLeaf()) {
                cascade = false;
            }

            delete(connection, path.getPath(), type, cascade);
        } catch (SQLException e) {
            LOG.error("Failed to delete node.", e);
            throw new TreeMeDeleteException(e);
        }
    }

    private void checkBeforeUpdate(Node previous, Node latest) {
        // Ensure the Node to be save is not converting from non-leaf to a leaf.
        // This would cause inconsistencies in the tree.
        if (!previous.getLeaf() && latest.getLeaf()) {
            String msg = "You're trying to convert a non-leaf to a leaf. " +
                    "This is not allowed to prevent breaking the tree links.";
            LOG.error(msg);
            throw new TreeMeSaveException(msg);
        }

        // Ensure the root Node type does not get changed.
        if (latest.getPath().equals(ROOT)) {
            if (!previous.getType().equals(latest.getType())) {
                String msg = "You're trying to convert change name of a type. This is not allowed.";
                LOG.error(msg);
                throw new TreeMeSaveException(msg);
            }
        }
    }

    private boolean checkPathExists(Path path, String type) throws SQLException {
        return getExactPath(connection, path.getPath(), type).next();
    }

    private void checkParentExists(Path path, String type) {
        Path parentPath = Path.from(path.getParentPath());
        Optional<Node> parent = getByPath(parentPath, type);

        if (!parent.isPresent()) {
            String msg = "Parent of path: " + path.getPath() +
                    " doesn't exist. Searched for parent: " + parentPath.getPath() + ".";
            LOG.error(msg);
            throw new TreeMeSaveException(msg);
        }

        if (parent.get().getLeaf()) {
            String msg = "Parent is a leaf node. Unable to save node.";
            LOG.error(msg);
            throw new TreeMeSaveException(msg);
        }
    }

    private List<Node> fromResultSet(ResultSet result) throws SQLException {
        List<Node> nodes = newArrayList();

        while (result.next()) {
            nodes.add(Node.builder()
                    .path(result.getString(PATH_COLUMN))
                    .type(result.getString(TYPE_COLUMN))
                    .name(result.getString(NAME_COLUMN))
                    .leaf(result.getBoolean(LEAF_COLUMN))
                    .value(result.getString(VALUE_COLUMN))
                    .build());
        }

        return nodes;
    }

    private void init() {
        try {
            LOG.info("Initializing SQL tables...");
            createTables(connection);
            LOG.info("..Initialized SQL tables!");
        } catch (SQLException e) {
            LOG.error("Failed to initialize database.", e.getMessage());
        }
    }
}
