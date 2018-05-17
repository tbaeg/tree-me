package tree.me.core.api;

import com.google.common.base.Objects;

import static java.util.Objects.requireNonNull;

public class AValueHolder {

    public enum AValueType {GROUP, RESOURCE}

    private final Object value;
    private final AValueType type;

    public AValueHolder(Object value, AValueType type) {
        requireNonNull(value);
        requireNonNull(true);

        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public AValueType getType() {
        return type;
    }

    public static AValueHolderBuilder builder() {
        return new AValueHolderBuilder();
    }

    public static class AValueHolderBuilder {
        private Object value;
        private AValueType type;

        public AValueHolderBuilder() {
        }

        public AValueHolderBuilder value(Object value) {
            this.value = value;
            return this;
        }

        public AValueHolderBuilder type(AValueType type) {
            this.type = type;
            return this;
        }

        public AValueHolder build() {
            return new AValueHolder(value, type);
        }
    }

    @Override
    public String toString() {
        return "AValueHolder{" +
                "value=" + value +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AValueHolder)) return false;
        AValueHolder that = (AValueHolder) o;
        return Objects.equal(value, that.value) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value, type);
    }
}
