package de.glaubekeinemdev.discordutilities.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.HashMap;

public class DataBaseManager {

    private final HashMap<String, DataBase> dataBases = new HashMap<>();
    private final File folderFile = new File("databases");

    public void initialize() {
        if(!folderFile.exists())
            folderFile.mkdir();

        if(folderFile.listFiles().length == 0)
            return;

        final Gson gson = new Gson();

        for(File eachFile : folderFile.listFiles()) {
            try {
                dataBases.put(eachFile.getName(), gson.fromJson(new FileReader(eachFile), DataBase.class));
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveAllDataBases() {
        dataBases.keySet().forEach(this::saveDataBase);
    }

    public void saveDataBase(final String dataBaseName) {
        if(!dataBases.containsKey(dataBaseName)) {
            System.out.println("Unable to find database to " + dataBaseName);
            return;
        }
        final DataBase dataBase = dataBases.get(dataBaseName);

        final File dataBaseFile = new File(folderFile, dataBaseName + ".json");

        try {
            final FileWriter fileWriter = new FileWriter(dataBaseFile);
            fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(dataBase));
            fileWriter.flush();
            fileWriter.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

    }

    public DataBase getDataBase(final String name) {
        if(!dataBases.containsKey(name)) {
            final DataBase dataBase = new DataBase();

            dataBases.put(name, dataBase);
            return dataBase;
        }

        return dataBases.get(name);
    }

    public boolean existsDataBase(final String name) {
        return dataBases.containsKey(name);
    }

    public DataBase createDataBase(final String name) {
        if(dataBases.containsKey(name)) {
            return dataBases.get(name);
        }

        final DataBase dataBase = new DataBase();

        dataBases.put(name, dataBase);
        return dataBase;
    }

}
