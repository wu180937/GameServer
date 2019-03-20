package com.wmj.game.engine.manage;

import java.util.*;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/20
 * @Description:
 */
public abstract class AbstractSession implements Session {
    protected boolean close = false;
    protected Set<Runnable> closeRunnableList = Collections.synchronizedSet(new HashSet<>());
    private Map<String, Object> attributeMap = new HashMap<>();

    @Override
    public void addCloseHook(Runnable runnable) {
        this.closeRunnableList.add(runnable);
    }

    @Override
    public synchronized void close() {
        if (close) {
            return;
        }
        close = true;
        this.closeRunnableList.stream().forEach(Runnable::run);
        this.closeRunnableList.clear();
        this.attributeMap.clear();
    }

    @Override
    public synchronized void putAttribute(String key, Object value) {
        this.attributeMap.put(key, value);
    }

    @Override
    public synchronized <T> T getAttribute(String key, Class<T> clazz) {
        return clazz.cast(this.attributeMap.get(key));
    }

    @Override
    public synchronized void removeAttribute(String key) {
        this.attributeMap.remove(key);
    }
}
