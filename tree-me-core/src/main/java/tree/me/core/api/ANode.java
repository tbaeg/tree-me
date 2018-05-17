package tree.me.core.api;

import com.google.common.base.Objects;
import tree.me.core.index.Node;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.requireNonNull;

public class ANode extends Node {

    private Collection<ANode> children;

    public ANode() {
        this("", "", "", false, "", newArrayList());
    }

    public ANode(String path, String model, String name, boolean leaf, String value, Collection<ANode> children) {
        super(path, model, name, leaf, value);
        requireNonNull(children);
        this.children = children;
    }

    public Collection<ANode> getChildren() {
        return children;
    }

    public void setChildren(Collection<ANode> children) {
        this.children = children;
    }

    public void addChild(ANode node) {
        children.add(node);
    }

    @Override
    public String toString() {
        return "ANode{" +
                "children=" + children +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ANode that = (ANode) o;
        return Objects.equal(children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), children);
    }
}