package de.spaceai.spaceperms.database;

import de.spaceai.spaceperms.SpacePerms;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.sql.*;

@Getter
@Setter
public class MySQL {

    private String host, database, username, password;

    private Connection connection;

    private final SpacePerms spacePerms;

    public MySQL(SpacePerms spacePerms, String host, String database, String username, String password) {
        this.spacePerms = spacePerms;
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public void connect() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://"+this.host+":3306/"+this.database, this.username,
                    this.password);
            this.spacePerms.getLogger().log("Connection established");
        } catch (Exception e) {
            this.spacePerms.getLogger().log("Connection cannot be established");
        }
    }

    public void disconnect() {
        if(isConnected()) {
            try {
                this.connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void update(String query) {
        try {
            Statement statement = this.connection.createStatement();
            statement.execute(query);
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ResultSet get(String query) {
        ResultSet rs = null;
        try {
            Statement st = connection.createStatement();
            rs = st.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    public boolean hasElement(String table, String identifier, String value) {
        ResultSet resultSet = get("SELECT * FROM "+table+" WHERE "+identifier+"='"+value+"'");
        try {
            if(resultSet.next())
                return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public Object getSingleElement(String query, String column) {
        ResultSet rs = null;
        try {
            Statement st = connection.createStatement();
            rs = st.executeQuery(query);
            return rs.getObject(column);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @SneakyThrows
    public boolean isConnected() {
        return this.connection != null || this.connection.isClosed();
    }

    public void createTable(String tableName, String... parameter) {
        String tableParams = "";
        for (String s : parameter) {
            tableParams+=s+", ";
        }
        tableParams = tableParams.substring(0, tableParams.length()-2);
        update("CREATE TABLE IF NOT EXISTS "+tableName+"("+tableParams+")");
    }

}
