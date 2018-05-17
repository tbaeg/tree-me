# Tree Me

A tool that let's you store and access your data like a tree with ease.

### Reasoning

Tree Me was inspired from the need to access resources via arbitrary paths (similar to file systems).
Storing resources into groups, sub-groups and sub-sub-groups should be an easy thing. The thought of having 
to re-implement this sort of logic for anything that wanted first class support for nested structures 
was awful; thus Tree Me was created.

### Features

* Simple API and design.
* Distinction between a `resource` vs. `group` for paths (file vs. folder).
* Cascade delete (deleting `/1` will delete `/1/2`).
* Built in path existence checks (saving `/1/2` does not work if `/1` does not exist).
* Allows you to write your DAO implementation as extremely simple get/save/delete operations.

### Examples

Below is an example of a page service that has extended the `AbstractTreeMeApi` and thus has
it's resources accessible via paths through the `TreeMeApi` interface.
```
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
```

The interface shown below.
```
public interface TreeMeApi<T> {

    Iterable<ANode> getTree();

    Optional<AValueHolder> get(Path path);

    Optional<T> getResource(Path path);

    Optional<Iterable<ANode>> getGroup(Path path);

    void saveResource(Path path, String name, String resourceId, T resource);

    void saveGroup(Path path, String name);

    void delete(Path path);
}
```

Now you can access and save your resources via paths!
```
TreeMeApi treeMeApi = new TreeMePageApi();

// create group path (i.e. - /group1)
Path group1Path = Path.from("group1");

// create a group
treeMeApi.saveGroup(group1Path, "Page Group 1");

// create a resource
Page page1 = new Page();

// create resource path
Path page1Path = Path.from(group1Path).to("page1");

// save your resource under "group1"
treeMeApi.save(page1Path, "Page 1 Name!", page1.getId(), page1);

// fetch your page you've saved
Optional<AValueHolder> pageHolder = treeMeApi.get(page1Path);
assertTrue(pageHolder.isPresent());
assertEquals(RESOURCE, pageHolder.get().getType())
assertEquals(page1, pageHolder.get().getValue())
```