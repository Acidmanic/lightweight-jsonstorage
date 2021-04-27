/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acidmanic.lightweight.jsonstorage.jsondirectorydb;

import com.acidmanic.lightweight.jsonstorage.JsonStorageBase;
import com.acidmanic.lightweight.jsonstorage.utility.TwoWayConverter;
import com.acidmanic.lightweight.logger.Logger;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author diego
 * @param <TModel> The type of model to be stored
 * @param <TId> The type of Id field in the model
 */
public abstract class JsonModelStorage<TModel, TId> {

    /**
     * Eases the construction of storage for repositories model type
     */
    private class ModelStorage extends JsonStorageProxied<TModel> {

        public ModelStorage(File xmlFile, Class<TModel> modelType) {
            super(xmlFile, modelType);
        }

    }

    /**
     * Makes it possible to use save and load methods
     *
     * @param <TSubmodel>
     */
    private class JsonStorageProxied<TSubmodel> extends JsonStorageBase<TSubmodel> {

        public JsonStorageProxied(File xmlFile, Class<TSubmodel> modelType) {
            super(xmlFile, modelType, logger);
        }

        @Override
        public boolean save(TSubmodel model) {
            return super.save(model); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public TSubmodel load() {
            return super.load(); //To change body of generated methods, choose Tools | Templates.
        }

    }

    protected abstract TId getId(TModel model);

    protected abstract void setId(TModel model, TId id);

    protected abstract TId generateNewId(TModel model);

    protected TwoWayConverter<TId, String> getIdConvertor() {
        return new TwoWayConverter<TId, String>() {
            @Override
            public TId from(String value) {
                try {
                    return (TId) value;
                } catch (Exception e) {
                }
                return null;
            }

            @Override
            public String to(TId value) {
                return value.toString();
            }
        };
    }

    protected abstract boolean allowsRealDeletion();

    private final Logger logger;
    private final Path directory;
    private final Class modelType;
    private final IdFilenameMap<TId> index;
    //private final TwoWayConverter<TId, String> idConvertor;

    public JsonModelStorage(Class modelType, Logger logger) {
        this.logger = logger;
        this.directory = new File(".").toPath()
                .resolve("JsonModelStorage")
                .resolve(this.getClass().getSimpleName())
                .toAbsolutePath().normalize();

        this.directory.toFile().mkdirs();

        this.modelType = modelType;

        this.index = new IdFilenameMap<>(getIdConvertor());

        loadStoredIndexes();

    }

    public TModel create(TModel model) {

        TId id = generateNewId(model);

        String fileName = UUID.randomUUID().toString();

        this.index.put(id, fileName);

        File modelFile = this.directory.resolve(fileName).toFile();

        ModelStorage storage = new ModelStorage(modelFile, this.modelType);

        setId(model, id);

        storage.save(model);

        updateindexes();

        return model;
    }

    private ModelStorage getStorage(TId id) {

        if (this.index.containsKey(id)) {

            String fileName = this.index.get(id);

            File modelFile = this.directory.resolve(fileName).toFile();

            if (modelFile.exists()) {
                ModelStorage storage = new ModelStorage(modelFile, this.modelType);

                return storage;
            }
        }
        return null;
    }

    public TModel read(TId id) {

        ModelStorage storage = getStorage(id);

        if (storage != null) {
            TModel model = storage.load();

            return model;
        }
        return null;
    }

    public <TsubModel> List<TsubModel> readAllAs(Class submodelType) {
        List<TsubModel> models = new ArrayList<>();

        for (String fileName : this.index.values()) {

            File modelFile = this.directory.resolve(fileName).toFile();

            if (modelFile.exists()) {
                JsonStorageProxied<TsubModel> storage = new JsonStorageProxied<>(modelFile, submodelType);

                TsubModel submodel = storage.load();

                if (submodel != null) {
                    models.add(submodel);
                }
            }
        }
        return models;
    }

    public List<TModel> readAll() {
        return readAllAs(this.modelType);
    }

    public TModel update(TModel model) {
        TId id = getId(model);

        ModelStorage storage = getStorage(id);

        if (storage != null) {

            storage.save(model);

            return model;
        }
        return null;
    }

    public boolean delete(TId id) {

        if (index.containsKey(id)) {

            if (allowsRealDeletion()) {

                String fileName = this.index.get(id);

                File modelFile = this.directory.resolve(fileName).toFile();

                try {
                    modelFile.delete();

                } catch (Exception e) {
                }
            }

            this.index.remove(id);

            updateindexes();

            return true;
        }
        return false;
    }

    private void updateindexes() {

        File indexFile = this.directory.resolve("index.json").toFile();

        JsonStorageProxied<IdDictionary> storage
                = new JsonStorageProxied<>(indexFile, IdDictionary.class);

        storage.save(index);

    }

    private void loadStoredIndexes() {

        try {
            File indexFile = this.directory.resolve("index.json").toFile();

            JsonStorageProxied<IdDictionary> storage
                    = new JsonStorageProxied(indexFile, IdDictionary.class);

            IdDictionary seed = storage.load();

            this.index.loadIds(seed);

        } catch (Exception e) {
        }
    }

}
