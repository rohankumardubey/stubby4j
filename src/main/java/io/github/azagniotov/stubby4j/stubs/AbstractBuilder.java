package io.github.azagniotov.stubby4j.stubs;


import io.github.azagniotov.stubby4j.yaml.ConfigurableYAMLProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.github.azagniotov.generics.TypeSafeConverter.as;

public abstract class AbstractBuilder<T extends ReflectableStub> {

    protected final Map<ConfigurableYAMLProperty, Object> fieldNameAndValues;

    public AbstractBuilder() {
        this.fieldNameAndValues = new HashMap<>();
    }

    public <E> E getStaged(final Class<E> clazzor, final ConfigurableYAMLProperty property, E defaultValue) {
        return fieldNameAndValues.containsKey(property) ? as(clazzor, fieldNameAndValues.get(property)) : defaultValue;
    }

    public void stage(final ConfigurableYAMLProperty fieldName, final Optional<Object> fieldValueOptional) {
        fieldValueOptional.ifPresent(value -> fieldNameAndValues.put(fieldName, value));
    }

    public abstract String yamlFamilyName();

    public abstract T build();
}
