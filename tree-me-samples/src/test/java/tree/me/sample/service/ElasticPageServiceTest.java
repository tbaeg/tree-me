package tree.me.sample.service;

import org.junit.Before;
import org.junit.Test;
import tree.me.sample.core.Page;
import tree.me.sample.service.elastic.ElasticPageService;
import tree.me.test.IronhideTest;

import java.util.Optional;

import static org.junit.Assert.*;

public class ElasticPageServiceTest extends IronhideTest {

    private ElasticPageService pageService;

    @Before
    public void before() {
        pageService = new ElasticPageService(client);
    }

    @Test
    public void testGet() {
        Page expected = new Page("id", "name");
        pageService.save(expected);

        Optional<Page> fetched = pageService.get("id");
        assertTrue(fetched.isPresent());

        Page actual = fetched.get();
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    public void testMissing() {
        Page saved = new Page("id", "name");
        pageService.save(saved);

        Optional<Page> fetched = pageService.get("missing");
        assertFalse(fetched.isPresent());
    }

    @Test
    public void testSave() {
        String markdown = "# Markdown Title\n" +
                "\n" +
                "- list1\n" +
                "- list2\n" +
                "- list3\n" +
                "\n" +
                "Something something <unity-query>IP_SRC: 120.123.4.5</unity-query>.\n" +
                "\n" +
                "Other thing <unity-query>IP_SRC: 120.123.4.6</unity-query>.";

        Page expected = new Page("id", "name", markdown);
        pageService.save(expected);

        Optional<Page> fetched = pageService.get("id");
        assertTrue(fetched.isPresent());

        Page actual = fetched.get();
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getMarkdown(), actual.getMarkdown());
    }

}
