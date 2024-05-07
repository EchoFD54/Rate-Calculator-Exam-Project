package DAL;

import BE.Team;
import BE.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final ConnectionManager connectionManager;

    public UserDAO() {
        this.connectionManager =  new ConnectionManager();
    }

public List<User> getUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
            int userId = resultSet.getInt("user_id");
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            User user = new User(userId, username, password);
            users.add(user);

        }
    } catch (SQLException e) {
        throw new RuntimeException("Error retrieving user list: " + e.getMessage(), e);
    }
    return users;

}

}
