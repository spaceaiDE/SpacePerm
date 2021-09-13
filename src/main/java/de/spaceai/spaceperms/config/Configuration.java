package de.spaceai.spaceperms.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.spaceai.spaceperms.SpacePerms;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

@Getter
public class Configuration {

    @Getter
    @AllArgsConstructor
    public static class ConfigPath {
        private final String path;
        private final String fileName;
    }

    private final ConfigPath configPath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File configFile;
    private final SpacePerms spacePerms;

    @SneakyThrows
    public Configuration(ConfigPath configPath, SpacePerms spacePerms) {
        this.configPath = configPath;
        this.spacePerms = spacePerms;
        this.configFile = new File((configPath.getPath().endsWith("/")) ? configPath.getPath()+configPath.getFileName()
                : configPath.getPath()+"/"+configPath.getFileName());
        if(!new File(configPath.getPath()).exists())
            new File(configPath.getPath()).mkdirs();
        if(!this.configFile.exists()) {
            this.configFile.createNewFile();

            JsonObject jsonObject = new JsonObject();
            JsonObject mysqlData = new JsonObject();
            mysqlData.addProperty("host", "127.0.0.1");
            mysqlData.addProperty("database", "database");
            mysqlData.addProperty("username", "root");
            mysqlData.addProperty("password", "password");
            jsonObject.add("mysql", mysqlData);

            FileWriter fileWriter = new FileWriter(this.configFile);
            fileWriter.write(gson.toJson(jsonObject));
            fileWriter.flush();
            fileWriter.close();
            this.spacePerms.getLogger().log("Creating new configuration file");
        }
    }

    @SneakyThrows
    public <T>T get(String key, Class<T> tClass) {
        JsonElement jsonObject = gson.fromJson(new FileReader(this.configFile), JsonElement.class);
        return gson.fromJson(jsonObject.getAsJsonObject().get(key).getAsString(), tClass);
    }

    @SneakyThrows
    public JsonObject get(String key) {
        JsonObject jsonObject = gson.fromJson(new FileReader(this.configFile), JsonObject.class);
        return jsonObject.get(key).getAsJsonObject();
    }

}
