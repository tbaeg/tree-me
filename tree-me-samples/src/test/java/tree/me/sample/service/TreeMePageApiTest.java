package tree.me.sample.service;

import org.junit.*;
import tree.me.api.TreeMeApi;
import tree.me.core.api.ANode;
import tree.me.core.api.AValueHolder;
import tree.me.core.index.Path;
import tree.me.sample.TreeMePageApi;
import tree.me.sample.core.Page;
import tree.me.sample.service.elastic.ElasticPageService;
import tree.me.service.sql.SqlTreeMeIndex;
import tree.me.test.IronhideTest;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Thread.sleep;
import static java.sql.DriverManager.getConnection;
import static org.junit.Assert.*;
import static tree.me.core.api.AValueHolder.AValueType.RESOURCE;
import static tree.me.sample.TreeMePageApi.TREE_ME_PAGE;

public class TreeMePageApiTest extends IronhideTest {

    private static final String IN_MEMORY_JDBC_URL = "jdbc:h2:mem:nodes;MODE=PostgreSQL";
    private static final String IN_MEMORY_JDBC_USER = "h2";
    private static final String IN_MEMORY_JDBC_PASSWORD = "h2";

    private static Connection connection;
    private static SqlTreeMeIndex treeMeIndex;
    private static ElasticPageService pageService;
    private static TreeMeApi<Page> treeMePageApi;

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
        pageService = new ElasticPageService(client);
        treeMePageApi = new TreeMePageApi(treeMeIndex, pageService);
    }

    @After
    public void after() throws Exception {
        connection.prepareStatement("DROP TABLE nodes;")
                .executeUpdate();
    }

    @Test
    public void testGet() throws Exception {
        Page expected = new Page("id", "name");
        Path path = Path.from("page1");
        treeMePageApi.saveResource(path, "name", expected.getId(), expected);
        sleep(250);

        Optional<AValueHolder> fetched = treeMePageApi.get(path);
        assertTrue(fetched.isPresent());

        AValueHolder actual = fetched.get();
        assertEquals(RESOURCE, actual.getType());
        assertEquals(expected, actual.getValue());
    }

    @Test
    public void testGetNested() throws Exception {
        String DELIM = "/";
        Path path1 = Path.fromDelimited("/one", DELIM);
        Path path2 = Path.fromDelimited("/one/two", DELIM);
        Path path3 = Path.fromDelimited("/one/two/three", DELIM);
        Path path4 = Path.fromDelimited("/one/two/three/four", DELIM);
        Path path5 = Path.fromDelimited("/one/two/three/four/five", DELIM);
        Path pagePath = Path.fromDelimited("/one/two/three/four/five/page", DELIM);

        Page expected = new Page();

        treeMePageApi.saveGroup(path1, "path1");
        treeMePageApi.saveGroup(path2, "path2");
        treeMePageApi.saveGroup(path3, "path3");
        treeMePageApi.saveGroup(path4, "path4");
        treeMePageApi.saveGroup(path5, "path5");
        treeMePageApi.saveResource(pagePath, "page", expected.getId(), expected);
        sleep(250);

        Optional<AValueHolder> fetched = treeMePageApi.get(pagePath);
        assertTrue(fetched.isPresent());

        AValueHolder actual = fetched.get();
        assertEquals(RESOURCE, actual.getType());
        assertEquals(expected, actual.getValue());
    }

    @Test
    public void testGetResource() throws Exception {
        Page expected = new Page("id", "name");
        Path path = Path.from("page1");
        treeMePageApi.saveResource(path, "name", expected.getId(), expected);
        sleep(250);

        Optional<Page> fetched = treeMePageApi.getResource(path);
        assertTrue(fetched.isPresent());

        Page actual = fetched.get();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetGroup() throws Exception {
        Page page1 = new Page("id1", "name1");
        Page page8 = new Page("id8", "name8");
        Page page01 = new Page("id01", "name01");
        Page page100 = new Page("id100", "name100");
        Page page101 = new Page("id101", "name101");
        Page pagea = new Page("ida", "namea");

        treeMePageApi.saveGroup(Path.from("group1"), "group1");
        treeMePageApi.saveGroup(Path.from("group2"), "group2");
        treeMePageApi.saveGroup(Path.from("group10"), "group10");

        Path path1 = Path.from("page1");
        Path path8 = Path.from("page8");
        Path path01 = Path.from("page01");
        Path path100 = Path.from("page100");
        Path path101 = Path.from("group1").to("page101");
        Path patha = Path.from("pagea");

        treeMePageApi.saveResource(path1, "page1", page1.getId(), page1);
        treeMePageApi.saveResource(path8, "page8", page8.getId(), page8);
        treeMePageApi.saveResource(path01, "page01", page01.getId(), page01);
        treeMePageApi.saveResource(path100, "page100", page100.getId(), page100);
        treeMePageApi.saveResource(path101, "page101", page101.getId(), page101);
        treeMePageApi.saveResource(patha, "pagea", pagea.getId(), pagea);
        sleep(250);

        Optional<Iterable<ANode>> optNodes = treeMePageApi.getGroup(Path.from("group1"));
        assertTrue(optNodes.isPresent());
        List<ANode> nodes = newArrayList(optNodes.get());
        assertEquals(1, nodes.size());

        ANode node = nodes.get(0);

        assertEquals(path101.getPath(), node.getPath());
        assertEquals(TREE_ME_PAGE, node.getType());
        assertEquals("page101", node.getName());
        assertTrue(node.getLeaf());
        assertEquals(page101.getId(), node.getValue());
        assertEquals(0, node.getChildren().size());
    }

    @Test
    public void testGetTreeWithOrder() throws Exception {
        Page page1 = new Page("id1", "name1");
        Page page8 = new Page("id8", "name8");
        Page page01 = new Page("id01", "name01");
        Page page100 = new Page("id100", "name100");
        Page page101 = new Page("id101", "name101");
        Page pagea = new Page("ida", "namea");

        treeMePageApi.saveGroup(Path.from("group1"), "group1");
        treeMePageApi.saveGroup(Path.from("group2"), "group2");
        treeMePageApi.saveGroup(Path.from("group10"), "group10");

        Path path1 = Path.from("page1");
        Path path8 = Path.from("page8");
        Path path01 = Path.from("page01");
        Path path100 = Path.from("page100");
        Path path101 = Path.from("group1").to("page101");
        Path patha = Path.from("pagea");

        treeMePageApi.saveResource(path1, "page1", page1.getId(), page1);
        treeMePageApi.saveResource(path8, "page8", page8.getId(), page8);
        treeMePageApi.saveResource(path01, "page01", page01.getId(), page01);
        treeMePageApi.saveResource(path100, "page100", page100.getId(), page100);
        treeMePageApi.saveResource(path101, "page101", page101.getId(), page101);
        treeMePageApi.saveResource(patha, "pagea", pagea.getId(), pagea);
        sleep(250);

        List<ANode> nodes = newArrayList(treeMePageApi.getTree());
    }

    @Test
    public void testDelete() throws Exception {
        Page page = new Page("id", "name");
        Path path = Path.from("page1");
        treeMePageApi.saveResource(path, "name", page.getId(), page);
        sleep(250);

        Optional<AValueHolder> saved = treeMePageApi.get(path);
        assertTrue(saved.isPresent());

        treeMePageApi.delete(path);

        Optional<AValueHolder> deleted = treeMePageApi.get(path);
        assertFalse(deleted.isPresent());
    }

}
