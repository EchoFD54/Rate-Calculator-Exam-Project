package BLL;

import BE.User;
import DAL.UserDAO;

import java.sql.SQLException;
import java.util.List;

public class UserManager {
    UserDAO userDAO = new UserDAO();

    public List<User> getAllUsers() throws SQLException {
        return userDAO.getUsers();
    }
}
