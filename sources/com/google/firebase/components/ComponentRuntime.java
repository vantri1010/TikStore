package com.google.firebase.components;

import com.google.firebase.events.Publisher;
import com.google.firebase.events.Subscriber;
import com.google.firebase.inject.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

/* compiled from: com.google.firebase:firebase-components@@16.0.0 */
public class ComponentRuntime extends AbstractComponentContainer {
    private static final Provider<Set<Object>> EMPTY_PROVIDER = ComponentRuntime$$Lambda$5.lambdaFactory$();
    private final Map<Component<?>, Lazy<?>> components = new HashMap();
    private final EventBus eventBus;
    private final Map<Class<?>, Lazy<?>> lazyInstanceMap = new HashMap();
    private final Map<Class<?>, Lazy<Set<?>>> lazySetMap = new HashMap();

    public /* bridge */ /* synthetic */ Object get(Class cls) {
        return super.get(cls);
    }

    public /* bridge */ /* synthetic */ Set setOf(Class cls) {
        return super.setOf(cls);
    }

    public ComponentRuntime(Executor defaultEventExecutor, Iterable<ComponentRegistrar> registrars, Component<?>... additionalComponents) {
        this.eventBus = new EventBus(defaultEventExecutor);
        List<Component<?>> componentsToAdd = new ArrayList<>();
        componentsToAdd.add(Component.of(this.eventBus, EventBus.class, Subscriber.class, Publisher.class));
        for (ComponentRegistrar registrar : registrars) {
            componentsToAdd.addAll(registrar.getComponents());
        }
        for (Component<?> additionalComponent : additionalComponents) {
            if (additionalComponent != null) {
                componentsToAdd.add(additionalComponent);
            }
        }
        CycleDetector.detect(componentsToAdd);
        for (Component<?> component : componentsToAdd) {
            this.components.put(component, new Lazy<>(ComponentRuntime$$Lambda$1.lambdaFactory$(this, component)));
        }
        processInstanceComponents();
        processSetComponents();
    }

    private void processInstanceComponents() {
        for (Map.Entry<Component<?>, Lazy<?>> entry : this.components.entrySet()) {
            Component<?> component = entry.getKey();
            if (component.isValue()) {
                Lazy<?> lazy = entry.getValue();
                for (Class<? super Object> anInterface : component.getProvidedInterfaces()) {
                    this.lazyInstanceMap.put(anInterface, lazy);
                }
            }
        }
        validateDependencies();
    }

    private void processSetComponents() {
        Map<Class<?>, Set<Lazy<?>>> setIndex = new HashMap<>();
        for (Map.Entry<Component<?>, Lazy<?>> entry : this.components.entrySet()) {
            Component<?> component = entry.getKey();
            if (!component.isValue()) {
                Lazy<?> lazy = entry.getValue();
                for (Class<?> anInterface : component.getProvidedInterfaces()) {
                    if (!setIndex.containsKey(anInterface)) {
                        setIndex.put(anInterface, new HashSet());
                    }
                    setIndex.get(anInterface).add(lazy);
                }
            }
        }
        for (Map.Entry<Class<?>, Set<Lazy<?>>> entry2 : setIndex.entrySet()) {
            this.lazySetMap.put(entry2.getKey(), new Lazy(ComponentRuntime$$Lambda$4.lambdaFactory$(entry2.getValue())));
        }
    }

    static /* synthetic */ Set lambda$processSetComponents$1(Set lazies) {
        Set<Object> set = new HashSet<>();
        Iterator it = lazies.iterator();
        while (it.hasNext()) {
            set.add(((Lazy) it.next()).get());
        }
        return Collections.unmodifiableSet(set);
    }

    public <T> Provider<T> getProvider(Class<T> anInterface) {
        Preconditions.checkNotNull(anInterface, "Null interface requested.");
        return this.lazyInstanceMap.get(anInterface);
    }

    public <T> Provider<Set<T>> setOfProvider(Class<T> anInterface) {
        Lazy<Set<?>> lazy = this.lazySetMap.get(anInterface);
        if (lazy != null) {
            return lazy;
        }
        return EMPTY_PROVIDER;
    }

    public void initializeEagerComponents(boolean isDefaultApp) {
        for (Map.Entry<Component<?>, Lazy<?>> entry : this.components.entrySet()) {
            Component<?> component = entry.getKey();
            Lazy<?> lazy = entry.getValue();
            if (component.isAlwaysEager() || (component.isEagerInDefaultApp() && isDefaultApp)) {
                lazy.get();
            }
        }
        this.eventBus.enablePublishingAndFlushPending();
    }

    private void validateDependencies() {
        for (Component<?> component : this.components.keySet()) {
            Iterator<Dependency> it = component.getDependencies().iterator();
            while (true) {
                if (it.hasNext()) {
                    Dependency dependency = it.next();
                    if (dependency.isRequired() && !this.lazyInstanceMap.containsKey(dependency.getInterface())) {
                        throw new MissingDependencyException(String.format("Unsatisfied dependency for component %s: %s", new Object[]{component, dependency.getInterface()}));
                    }
                }
            }
        }
    }
}
