package de.glaubekeinemdev.discordutilities.database;

import java.util.concurrent.ConcurrentHashMap;

public class DataBase {

    private ConcurrentHashMap<String, Object> dataBaseEntry = new ConcurrentHashMap<>();

    public DataBase(final String key, final Object value) {
        dataBaseEntry.put(key, value);
    }

    public DataBase() {
    }

    public boolean contains(final String key) {
        return dataBaseEntry.containsKey(key);
    }

    public DataBase append(final String key, final Object value) {
        dataBaseEntry.put(key, value);
        return this;
    }

    public void remove(final String key) {
        dataBaseEntry.remove(key);
    }

    public Integer getInteger(final String key) {
        if(!contains(key))
            return null;

        if(!(dataBaseEntry.get(key) instanceof Integer)) {
           return null;
        }

        return (int) dataBaseEntry.get(key);
    }

    public Float getFloat(final String key) {
        if(!contains(key))
            return null;

        if(!(dataBaseEntry.get(key) instanceof Float)) {
            return null;
        }

        return (float) dataBaseEntry.get(key);
    }

    public Double getDouble(final String key) {
        if(!contains(key))
            return null;

        if(!(dataBaseEntry.get(key) instanceof Double)) {
            return null;
        }

        return (double) dataBaseEntry.get(key);
    }

    public Boolean getBoolean(final String key) {
        if(!contains(key))
            return null;

        if(!(dataBaseEntry.get(key) instanceof Boolean)) {
            return null;
        }

        return (boolean) dataBaseEntry.get(key);
    }

    public String getString(final String key) {
        if(!contains(key))
            return null;

        if(!(dataBaseEntry.get(key) instanceof String)) {
            return null;
        }

        return (String) dataBaseEntry.get(key);
    }
}
