package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "insert into myusers(firstname, lastname, age) values (?,?,?);";
    private static final String updateUserSQL = "update myusers set firstname=?, lastname=?, age=? where id=?;";
    private static final String deleteUser = "delete from myusers where id=?;";
    private static final String findUserByIdSQL = "select * from myusers where id=?;";
    private static final String findUserByNameSQL = "select * from myusers where firstname=?;";
    private static final String findAllUserSQL = "select * from myusers;";

    public Long createUser(User user) {
        Long userId = null;
        try (Connection conn = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(createUserSQL, st.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.execute();
            try (ResultSet resultSet = ps.getGeneratedKeys()) {
                if (resultSet.next()) {
                    userId = resultSet.getLong(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return userId;
    }

    public User findUserById(Long userId) {
        User user = null;
        try (Connection conn = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(findUserByIdSQL)) {
            ps.setLong(1, userId);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong(1);
                    String firstName = resultSet.getString(2);
                    String lastName = resultSet.getString(3);
                    int age = resultSet.getInt(4);
                    user = new User(id, firstName, lastName, age);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return user;
    }

    public User findUserByName(String userName) {
        User user = null;
        try (Connection conn = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(findUserByNameSQL)) {
            ps.setString(1, userName);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong(1);
                    String firstName = resultSet.getString(2);
                    String lastName = resultSet.getString(3);
                    int age = resultSet.getInt(4);
                    user = new User(id, firstName, lastName, age);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();
        try (Connection conn = CustomDataSource.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet resultSet = st.executeQuery(findAllUserSQL)) {
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                int age = resultSet.getInt(4);
                users.add(new User(id, firstName, lastName, age));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return users;
    }

    public User updateUser(User user) {
        try (Connection conn = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(updateUserSQL)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());
            ps.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return user;
    }

    private void deleteUser(Long userId) {
        try (Connection conn = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteUser)) {
            ps.setLong(1, userId);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
