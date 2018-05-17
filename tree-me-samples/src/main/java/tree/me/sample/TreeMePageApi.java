package tree.me.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tree.me.api.AbstractTreeMeApi;
import tree.me.core.index.Path;
import tree.me.sample.core.Page;
import tree.me.sample.service.PageService;
import tree.me.service.TreeMeIndex;

import static com.google.common.collect.Lists.newArrayList;

public class TreeMePageApi extends AbstractTreeMeApi<Page> {

    private static final Logger LOG = LoggerFactory.getLogger(TreeMePageApi.class);

    public static final String TREE_ME_PAGE = "tree.me.page";

    private PageService pageService;

    public TreeMePageApi(TreeMeIndex treeMeIndex, PageService pageService) {
        super(treeMeIndex);
        this.pageService = pageService;
    }

    @Override
    public Page onGetResource(String id) {
        return pageService.get(id).orElse(null);
    }

    @Override
    public void onSaveResource(Page page) {
        pageService.save(page);
    }

    @Override
    public void onSaveGroup(Path path, String name) {
    }

    @Override
    public void onResourceDelete(String id) {
        pageService.delete(id);
    }

    @Override
    public void onGroupDelete(Iterable<String> resourceIdsToDelete) {
        newArrayList(resourceIdsToDelete).stream()
                .forEach(id -> pageService.delete(id));
    }

    @Override
    public String getType() {
        return TREE_ME_PAGE;
    }
}
