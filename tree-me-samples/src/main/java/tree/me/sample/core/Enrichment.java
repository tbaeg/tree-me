package tree.me.sample.core;

import com.google.common.base.Objects;

public class Enrichment {

    private String key;
    private String value;
    private String source;

    public Enrichment() {
    }

    public Enrichment(String key, String value, String source) {
        this.key = key;
        this.value = value;
        this.source = source;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public static EnrichmentBuilder builder() {
        return new EnrichmentBuilder();
    }

    public static class EnrichmentBuilder {

        private String key;
        private String value;
        private String source;

        public EnrichmentBuilder() {
        }

        public EnrichmentBuilder key(String key) {
            this.key = key;
            return this;
        }

        public EnrichmentBuilder value(String value) {
            this.value = value;
            return this;
        }

        public EnrichmentBuilder source(String source) {
            this.source = source;
            return this;
        }

        public Enrichment build() {
            return new Enrichment(key, value, source);
        }
    }

    @Override
    public String toString() {
        return "Enrichment{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", source='" + source + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enrichment that = (Enrichment) o;
        return Objects.equal(key, that.key) &&
                Objects.equal(value, that.value) &&
                Objects.equal(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key, value, source);
    }
}
