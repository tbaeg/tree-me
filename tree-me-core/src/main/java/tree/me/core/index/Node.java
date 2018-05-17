package tree.me.core.index;

import com.google.common.base.Objects;

import static java.util.Objects.requireNonNull;

public class Node {

    private String path;
    private String type;
    private String name;
    private boolean leaf;
    private String value;

    public Node() {
    }

    public Node(String path, String type, String name, boolean leaf, String value) {
        requireNonNull(path);
        requireNonNull(leaf);
        this.path = path;
        this.type = type;
        this.name = name;
        this.leaf = leaf;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static NodeBuilder builder() {
        return new NodeBuilder();
    }

    public static class NodeBuilder {
        private String path;
        private String type;
        private String name;
        private boolean leaf;
        private String value;

        public NodeBuilder() {
        }

        public NodeBuilder path(String path) {
            this.path = path;
            return this;
        }

        public NodeBuilder type(String type) {
            this.type = type;
            return this;
        }

        public NodeBuilder name(String name) {
            this.name = name;
            return this;
        }

        public NodeBuilder leaf(boolean leaf) {
            this.leaf = leaf;
            return this;
        }

        public NodeBuilder value(String value) {
            this.value = value;
            return this;
        }

        public Node build() {
            return new Node(path, type, name, leaf, value);
        }
    }

    @Override
    public String toString() {
        return "Node{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", leaf=" + leaf +
                ", path='" + path + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return leaf == node.leaf &&
                Objects.equal(type, node.type) &&
                Objects.equal(name, node.name) &&
                Objects.equal(path, node.path) &&
                Objects.equal(value, node.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, name, leaf, path, value);
    }
}
