package tree.me.service.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tree.me.core.index.Node;

import java.sql.*;

import static tree.me.core.index.Path.DELIM;
import static tree.me.core.index.Path.ROOT;

public class SqlTreeIndexStatements {

    private static final Logger LOG = LoggerFactory.getLogger(SqlTreeIndexStatements.class);

    private static final String WILDCARD = "%";

    public static ResultSet getExactPath(Connection connection, String path, String type) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM nodes WHERE path=? AND type=?;");

        statement.setString(1, path);
        statement.setString(2, type);

        LOG.debug("Query getExactPath: " + statement.toString());

        return statement.executeQuery();
    }

    public static ResultSet getChildrenPath(Connection connection, String path, String type) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM nodes WHERE path LIKE ? AND type=? AND depth=?;");

        statement.setString(1, path + DELIM + WILDCARD);
        statement.setString(2, type);
        statement.setInt(3, getDepth(path) + 1);

        LOG.debug("Query getChildrenPath: " + statement.toString());

        return statement.executeQuery();
    }

    public static ResultSet getAllPath(Connection connection, String path, String type) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM nodes WHERE path LIKE ? AND type=?;");

        statement.setString(1, path + WILDCARD);
        statement.setString(2, type);

        LOG.debug("Query getAllPath: " + statement.toString());

        return statement.executeQuery();
    }

    public static void create(Connection connection, Node node) throws SQLException {
        String query = "INSERT INTO nodes(path, type, name, leaf, value, depth) " +
                "VALUES (?, ?, ?, ?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setString(1, node.getPath());
        statement.setString(2, node.getType());
        statement.setString(3, node.getName());
        statement.setBoolean(4, node.getLeaf());
        statement.setString(5, node.getValue());
        statement.setInt(6, getDepth(node.getPath()));

        LOG.debug("Query create: " + statement.toString());

        statement.executeUpdate();
    }

    public static void update(Connection connection, Node node) throws SQLException {
        String query = "UPDATE nodes " +
                "SET path=?, type=?, name=?, leaf=?, value=?, depth=? " +
                "WHERE path=? AND type=?;";

        PreparedStatement statement = connection.prepareStatement(query);

        statement.setString(1, node.getPath());
        statement.setString(2, node.getType());
        statement.setString(3, node.getName());
        statement.setBoolean(4, node.getLeaf());
        statement.setString(5, node.getValue());
        statement.setInt(6, getDepth(node.getPath()));
        statement.setString(7, node.getPath());
        statement.setString(8, node.getType());

        LOG.debug("Query update: " + statement.toString());

        statement.executeUpdate();
    }

    public static void delete(Connection connection, String path, String type, boolean cascade) throws SQLException {
        String query = "DELETE FROM nodes WHERE path=? AND type=?;";

        if (cascade) {
            path += WILDCARD;
            query = "DELETE FROM nodes WHERE path LIKE ? AND type=?;";
        }

        PreparedStatement statement = connection.prepareStatement(query);

        statement.setString(1, path);
        statement.setString(2, type);

        LOG.debug("Query delete: " + statement.toString());

        statement.executeUpdate();
    }

    public static void createTables(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.closeOnCompletion();

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS nodes (\n" +
                "  path VARCHAR(1000) NOT NULL, \n" +
                "  type VARCHAR(1000) NOT NULL,\n" +
                "  name VARCHAR(1000) NOT NULL, \n" +
                "  leaf BOOLEAN,\n" +
                "  value VARCHAR(1000) NOT NULL,\n" +
                "  depth INTEGER NOT NULL,\n" +
                "  PRIMARY KEY (path, type)" +
                ");");
        statement.closeOnCompletion();
    }

    private static int getDepth(String path) {
        if (path.equals(ROOT)) {
            return 0;
        }
        return path.length() - path.replace(DELIM, "").length();
    }

}
