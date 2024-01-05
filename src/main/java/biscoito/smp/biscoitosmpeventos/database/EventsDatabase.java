package biscoito.smp.biscoitosmpeventos.database;

import org.bukkit.entity.Player;

import java.sql.*;

public class EventsDatabase {

    private final Connection connection;

    public EventsDatabase(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS players ( uuid TEXT PRIMARY KEY, username TEXT NOT NULL, participations INTEGER NOT NULL DEFAULT 0, victories INTEGER NOT NULL DEFAULT 0)");

    }


    public void closeConnection() throws SQLException {
        if(connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void addPlayer(Player player) throws SQLException{
        try(PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO players (uuid, username) VALUES (?, ?)")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, player.getName());
            preparedStatement.executeUpdate();
        }
    }

    public boolean playerExists(Player player) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public void updatePlayerParticipations(Player player, int victories) throws SQLException {
        if (!playerExists(player)) {
            addPlayer(player);
        }

        try(PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET participations = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, victories);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public int getParticipations(Player player) throws SQLException{
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT participations FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt("participations");
            } else {
                return 0;
            }
        }
    }

    public void updatePlayerVictories(Player player, int victories) throws SQLException {
        if (!playerExists(player)) {
            addPlayer(player);
        }

        try(PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET victories = ? WHERE uuid = ?")) {
            preparedStatement.setInt(1, victories);
            preparedStatement.setString(2, player.getUniqueId().toString());
            preparedStatement.executeUpdate();
        }
    }

    public int getVictories(Player player) throws SQLException{
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT victories FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt("victories");
            } else {
                return 0;
            }
        }
    }

}
