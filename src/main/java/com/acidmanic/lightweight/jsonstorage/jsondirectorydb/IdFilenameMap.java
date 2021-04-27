/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acidmanic.lightweight.jsonstorage.jsondirectorydb;

import com.acidmanic.lightweight.jsonstorage.utility.TwoWayConverter;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class injects conversion logic into a string dictionary, it proxies the
 * behavior to be seen as a TId,String hashmap but , its still cast-able to
 * String,String dictionary for persistent purposes
 *
 * @author diego
 * @param <TId> Generic Type of Id object
 */
public class IdFilenameMap<TId> extends IdDictionary {

    private final TwoWayConverter<TId, String> idConverter;

    public IdFilenameMap(TwoWayConverter<TId, String> idConverter) {
        this.idConverter = idConverter;
    }

    public IdFilenameMap(TwoWayConverter<TId, String> idConverter, IdDictionary dictionary) {
        this.idConverter = idConverter;
        this.loadIds(dictionary);
    }

    public void loadIds(IdDictionary dictionary) {
        this.data.putAll(dictionary.data);
    }

    public boolean containsKey(TId id) {
        if (id == null) {
            return false;
        }
        String key = this.idConverter.to(id);

        return this.data.containsKey(key);
    }

    public String get(TId id) {
        if (id == null) {
            return null;
        }
        String key = this.idConverter.to(id);
        return this.data.get(key);
    }

    public String put(TId id, String value) {
        if (id == null) {
            return null;
        }
        String key = this.idConverter.to(id);

        return this.data.put(key, value);
    }

    public String remove(TId id) {
        if (id == null) {
            return null;
        }
        String key = this.idConverter.to(id);

        return this.data.remove(key);
    }

    public Collection<String> values() {
        return this.data.values();
    }

    public Set<TId> keySet() {

        Set<String> keys = this.data.keySet();

        Set<TId> ids = new LinkedHashSet<>();

        for (String key : keys) {
            TId id = this.idConverter.from(key);

            ids.add(id);
        }

        return ids;
    }
}
