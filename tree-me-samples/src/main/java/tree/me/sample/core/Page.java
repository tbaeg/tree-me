package tree.me.sample.core;

import com.google.common.base.Objects;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.requireNonNull;

public class Page {

    private String id;
    private String name;
    private String markdown;
    private List<Enrichment> enrichment;

    // NOTE: This constructor is ONLY for jackson..
    // should be fixed with a serde module
    public Page() {
        this("", "", "", newArrayList());
    }

    public Page(String id, String name) {
        this(id, name, "", newArrayList());
    }

    public Page(String id, String name, String markdown) {
        this(id, name, markdown, newArrayList());
    }

    public Page(String id, String name, String markdown, List<Enrichment> enrichment) {
        requireNonNull(id);
        requireNonNull(name);
        requireNonNull(markdown);
        requireNonNull(enrichment);

        this.id = id;
        this.name = name;
        this.markdown = markdown;
        this.enrichment = enrichment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }

    public List<Enrichment> getEnrichment() {
        return enrichment;
    }

    public void setEnrichment(List<Enrichment> enrichment) {
        this.enrichment = enrichment;
    }

    public static PageBuilder builder() {
        return new PageBuilder();
    }

    public static class PageBuilder {

        private String id;
        private String name;
        private String markdown;
        private List<Enrichment> enrichment;

        public PageBuilder() {
        }

        public PageBuilder id(String id) {
            this.id = id;
            return this;
        }

        public PageBuilder name(String name) {
            this.name = name;
            return this;
        }

        public PageBuilder markdown(String markdown) {
            this.markdown = markdown;
            return this;
        }

        public PageBuilder enrichment(List<Enrichment> enrichment) {
            this.enrichment = enrichment;
            return this;
        }

        public Page build() {
            return new Page(id, name, markdown, enrichment);
        }
    }

    @Override
    public String toString() {
        return "Page{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", markdown='" + markdown + '\'' +
                ", enrichment=" + enrichment +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return Objects.equal(id, page.id) &&
                Objects.equal(name, page.name) &&
                Objects.equal(markdown, page.markdown) &&
                Objects.equal(enrichment, page.enrichment);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, markdown, enrichment);
    }
}
