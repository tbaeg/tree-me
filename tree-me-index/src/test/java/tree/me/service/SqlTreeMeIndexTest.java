package tree.me.service;

import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tree.me.core.index.Node;
import tree.me.core.index.Path;
import tree.me.service.exception.TreeMeSaveException;
import tree.me.service.sql.SqlTreeMeIndex;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.sql.DriverManager.getConnection;
import static org.junit.Assert.*;

public class SqlTreeMeIndexTest {

    private static final Logger LOG = LoggerFactory.getLogger(SqlTreeMeIndexTest.class);

    private static final String IN_MEMORY_JDBC_URL = "jdbc:h2:mem:nodes;MODE=PostgreSQL";
    private static final String IN_MEMORY_JDBC_USER = "h2";
    private static final String IN_MEMORY_JDBC_PASSWORD = "h2";

    private static final String PATH_DELIM = "/";
    private static final String TYPE = "ATT&CK";
    private static final String GROUP = "group";

    private static Connection connection;
    private SqlTreeMeIndex treeMeIndex;

    @BeforeClass
    public static void beforeClass() throws Exception {
        connection = getConnection(IN_MEMORY_JDBC_URL, IN_MEMORY_JDBC_USER, IN_MEMORY_JDBC_PASSWORD);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        connection.close();
    }

    @Before
    public void before() throws Exception {
        treeMeIndex = new SqlTreeMeIndex(connection);
    }

    @After
    public void after() throws Exception {
        connection.prepareStatement("DROP TABLE nodes;")
                .executeUpdate();
    }

    @Test
    public void testSaveAndGetByPath() {
        Node expected = createRootNode();
        treeMeIndex.save(expected);

        Optional<Node> saved = treeMeIndex.getByPath(Path.from(expected.getPath()), expected.getType());
        assertTrue(saved.isPresent());

        Node actual = saved.get();
        assertEquals(expected, actual);
    }

    @Test
    public void testSaveUpdateAndGetByPath() {
        Node expected = createRootNode();
        expected.setValue("some value");
        treeMeIndex.save(expected);

        expected.setValue("other value");
        treeMeIndex.save(expected);

        Optional<Node> saved = treeMeIndex.getByPath(Path.from(expected.getPath()), expected.getType());
        assertTrue(saved.isPresent());

        Node actual = saved.get();
        assertEquals(expected, actual);
    }

    @Test
    public void testSaveAndGetChildrenByPath() {
        Node root = createRootNode();
        Node group = createGroupNode();
        Node resource1 = createNodeUnderGroup("resource1");
        Node resource2 = createNodeUnderGroup("resource2");
        Node resource3 = createNodeUnderGroup("resource3");
        Node resource4 = createNodeUnderGroup("resource4");
        Node resource5 = createNodeUnderGroup("resource5");
        Node resource6 = createNodeUnderGroup("resource6");
        Node nestedGroup = Node.builder()
                .path(Path.from(GROUP).to("nested group").getPath())
                .type(TYPE)
                .name("nested group")
                .leaf(false)
                .value("nested group")
                .build();
        Node nestedResource = Node.builder()
                .path(Path.from(GROUP).to("nested group").to("nested resource").getPath())
                .type(TYPE)
                .name("nested resource")
                .leaf(true)
                .value("nested resource")
                .build();

        treeMeIndex.save(root);
        treeMeIndex.save(group);
        treeMeIndex.save(resource1);
        treeMeIndex.save(resource2);
        treeMeIndex.save(resource3);
        treeMeIndex.save(resource4);
        treeMeIndex.save(resource5);
        treeMeIndex.save(resource6);
        treeMeIndex.save(nestedGroup);
        treeMeIndex.save(nestedResource);

        Path groupPath = Path.from(group.getPath());

        Optional<Node> savedGroup = treeMeIndex.getByPath(groupPath, group.getType());
        assertTrue(savedGroup.isPresent());

        Node actualGroup = savedGroup.get();
        assertEquals(group, actualGroup);

        Optional<Iterable<Node>> savedGroupChildren = treeMeIndex.getChildrenByPath(groupPath, group.getType());
        assertTrue(savedGroupChildren.isPresent());
        List<Node> actualGroupChildren = newArrayList(savedGroupChildren.get());
        assertEquals(7, actualGroupChildren.size());

        List<String> possiblePaths = newArrayList(
                resource1.getPath(),
                resource2.getPath(),
                resource3.getPath(),
                resource4.getPath(),
                resource5.getPath(),
                resource6.getPath(),
                nestedGroup.getPath()
        );
        actualGroupChildren.forEach(node -> {
            String actualPath = node.getPath();
            assertTrue(possiblePaths.contains(actualPath));
            // Remove to ensure no duplicate nodes
            possiblePaths.remove(node.getPath());
        });
    }

    @Test
    public void testSaveAndGetAllByPath() {
        Node root = createRootNode();
        Node group = createGroupNode();
        Node resource1 = createNodeUnderGroup("resource1");
        Node resource2 = createNodeUnderGroup("resource2");
        Node resource3 = createNodeUnderGroup("resource3");
        Node resource4 = createNodeUnderGroup("resource4");
        Node resource5 = createNodeUnderGroup("resource5");
        Node resource6 = createNodeUnderGroup("resource6");
        Node nestedGroup = Node.builder()
                .path(Path.from(GROUP).to("nested group").getPath())
                .type(TYPE)
                .name("nested group")
                .leaf(false)
                .value("nested group")
                .build();
        Node nestedResource = Node.builder()
                .path(Path.from(GROUP).to("nested group").to("nested resource").getPath())
                .type(TYPE)
                .name("nested resource")
                .leaf(true)
                .value("nested resource")
                .build();

        treeMeIndex.save(root);
        treeMeIndex.save(group);
        treeMeIndex.save(resource1);
        treeMeIndex.save(resource2);
        treeMeIndex.save(resource3);
        treeMeIndex.save(resource4);
        treeMeIndex.save(resource5);
        treeMeIndex.save(resource6);
        treeMeIndex.save(nestedGroup);
        treeMeIndex.save(nestedResource);

        Optional<Iterable<Node>> allNodes = treeMeIndex.getAllByPath(Path.from(root.getPath()), root.getType());
        assertTrue(allNodes.isPresent());
        List<Node> nodes = newArrayList(allNodes.get());
        assertEquals(10, nodes.size());

        List<String> possiblePaths = newArrayList(
                root.getPath(),
                group.getPath(),
                resource1.getPath(),
                resource2.getPath(),
                resource3.getPath(),
                resource4.getPath(),
                resource5.getPath(),
                resource6.getPath(),
                nestedGroup.getPath(),
                nestedResource.getPath()
        );
        nodes.forEach(node -> {
            String actualPath = node.getPath();
            assertTrue(possiblePaths.contains(actualPath));
            // Remove to ensure no duplicate nodes
            possiblePaths.remove(node.getPath());
        });
    }

    @Test
    public void testSaveAndDeleteByPath() {
        Node root = createRootNode();
        Node group = createGroupNode();
        Node resource1 = createNodeUnderGroup("resource1");
        Node resource2 = createNodeUnderGroup("resource2");
        Node resource3 = createNodeUnderGroup("resource3");
        Node resource4 = createNodeUnderGroup("resource4");
        Node resource5 = createNodeUnderGroup("resource5");
        Node resource6 = createNodeUnderGroup("resource6");
        Node nestedGroup = Node.builder()
                .path(Path.from(GROUP).to("nested group").getPath())
                .type(TYPE)
                .name("nested group")
                .leaf(false)
                .value("nested group")
                .build();
        Node nestedResource = Node.builder()
                .path(Path.from(GROUP).to("nested group").to("nested resource").getPath())
                .type(TYPE)
                .name("nested resource")
                .leaf(true)
                .value("nested resource")
                .build();

        treeMeIndex.save(root);
        treeMeIndex.save(group);
        treeMeIndex.save(resource1);
        treeMeIndex.save(resource2);
        treeMeIndex.save(resource3);
        treeMeIndex.save(resource4);
        treeMeIndex.save(resource5);
        treeMeIndex.save(resource6);
        treeMeIndex.save(nestedGroup);
        treeMeIndex.save(nestedResource);

        Optional<Iterable<Node>> allNodes = treeMeIndex.getAllByPath(Path.from(root.getPath()), root.getType());
        assertTrue(allNodes.isPresent());
        assertEquals(10, newArrayList(allNodes.get()).size());

        treeMeIndex.deleteByPath(Path.from(root.getPath()), root.getType());

        Optional<Iterable<Node>> allNodesAfterDelete = treeMeIndex.getAllByPath(Path.from(root.getPath()), root.getType());
        assertFalse(allNodesAfterDelete.isPresent());
    }

    @Test(expected = TreeMeSaveException.class)
    public void testUpdateNonLeafNodeAsLeaf() {
        // save root node
        Node expected = createRootNode();
        treeMeIndex.save(expected);

        // update root node
        expected.setLeaf(true);
        treeMeIndex.save(expected);

        Optional<Node> saved = treeMeIndex.getByPath(Path.from(expected.getPath()), expected.getType());
        assertTrue(saved.isPresent());

        Node actual = saved.get();
        assertEquals(expected.getPath(), actual.getPath());
        assertEquals(expected.getType(), actual.getType());
        assertFalse(actual.getLeaf());
        assertEquals(expected.getValue(), actual.getValue());
    }

    @Test(expected = TreeMeSaveException.class)
    public void testSaveRootNodeAsLeaf() {
        // save root node
        Node expected = createRootNode();
        expected.setLeaf(true);
        treeMeIndex.save(expected);

        Optional<Node> saved = treeMeIndex.getByPath(Path.from(expected.getPath()), expected.getType());
        assertTrue(!saved.isPresent());
    }

    private static final Node createRootNode() {
        return Node.builder()
                .path(Path.fromDelimited("/", PATH_DELIM).getPath())
                .type(TYPE)
                .name("Root Node")
                .leaf(false)
                .value("")
                .build();
    }

    private static Node createGroupNode() {
        return Node.builder()
                .path(Path.fromDelimited("/group", PATH_DELIM).getPath())
                .type(TYPE)
                .name(GROUP)
                .leaf(false)
                .value(GROUP)
                .build();
    }

    private static Node createNodeUnderGroup(String name) {
        return Node.builder()
                .path(Path.fromDelimited("/group/" + name, PATH_DELIM).getPath())
                .type(TYPE)
                .name(name)
                .leaf(false)
                .value(name)
                .build();
    }
}
