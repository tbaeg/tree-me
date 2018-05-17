package tree.me.core.index;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Objects.requireNonNull;

public class Path {

    public static final String ROOT = "\u0001";
    public static final String DELIM = ROOT;

    private static final Joiner PATH_TO_STRING_JOINER = Joiner.on("/");
    private static final Joiner PATH_JOINER = Joiner.on(DELIM);
    private static final Splitter PATH_SPLITTER = Splitter.on(DELIM).trimResults().omitEmptyStrings();

    private List<String> path;

    public Path() {
        this.path = newArrayList();
    }

    public Path(Path path) {
        this.path = newArrayList(PATH_SPLITTER.split(path.getPath()));
    }

    private Path(String path) {
        this.path = newArrayList(PATH_SPLITTER.split(path));
    }

    private Path(String path, String delimiter) {
        Splitter splitter = Splitter.on(delimiter).trimResults().omitEmptyStrings();
        this.path = newArrayList(splitter.split(path));
    }

    public String getPath() {
        return path.size() == 1 ? ROOT + path.get(0) : ROOT + PATH_JOINER.join(path);
    }

    public String getParentPath() {
        return path.size() == 0 || path.size() == 1 ? ROOT : ROOT + PATH_JOINER.join(path.subList(0, path.size() - 1));
    }

    public boolean isRoot() {
        return path.size() == 0;
    }

    public Path to(String to) {
        requireNonNull(to);

        if (to.equals("")) {
            throw new IllegalArgumentException("Empty string path argument not allowed");
        }

        if (to.contains(DELIM)) {
            throw new IllegalArgumentException("Unicode character not allowed in path: \u0000");
        }

        path.add(to);

        return this;
    }

    public static Path from(Path path) {
        return new Path(path);
    }

    public static Path from(String path) {
        if (path.equals(ROOT)) {
            return new Path();
        }
        return new Path(path);
    }

    public static Path fromDelimited(String path, String delimiter) {
        return new Path(path, delimiter);
    }

    @Override
    public String toString() {
        return "Path{" +
                "path=" + PATH_TO_STRING_JOINER.join(path) +
                '}';
    }
}
