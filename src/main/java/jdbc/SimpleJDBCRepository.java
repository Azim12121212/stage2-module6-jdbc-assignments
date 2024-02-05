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

    private static Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "insert into myusers(firstname, lastname, age) values (?,?,?)";
    private static final String updateUserSQL = "update myusers set firstname=?, lastname=?, age=? where id=?";
    private static final String deleteUserSQL = "delete from myusers where id=?";
    private static final String findUserByIdSQL = "select * from myusers where id=?";
    private static final String findUserByNameSQL = "select * from myusers where firstname=?";
    private static final String findAllUserSQL = "select * from myusers";

    public Long createUser(User user) {
        Long userId = null;
        try {
            connection = CustomDataSource.getInstance().getConnection();
            if (connection != null) {
                System.out.println("Connection established!");
                ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getFirstName());
                ps.setString(2, user.getLastName());
                ps.setInt(3, user.getAge());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    userId = rs.getLong(1);
                }
                ps.close();
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userId;
    }

    public User findUserById(Long userId) {
        User user = new User();

        try {
            connection = CustomDataSource.getInstance().getConnection();
            if (connection != null) {
                System.out.println("Connection established!");
                ps = connection.prepareStatement(findUserByIdSQL);
                ps.setLong(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                    user.setFirstName(rs.getString(2));
                    user.setLastName(rs.getString(3));
                    user.setAge(rs.getInt(4));
                }
                ps.close();
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public User findUserByName(String userName) {
        User user = new User();

        try {
            connection = CustomDataSource.getInstance().getConnection();
            if (connection != null) {
                System.out.println("Connection established!");
                ps = connection.prepareStatement(findUserByNameSQL);
                ps.setString(1, userName);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                    user.setFirstName(rs.getString(2));
                    user.setLastName(rs.getString(3));
                    user.setAge(rs.getInt(4));
                }
                ps.close();
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> usersList = new ArrayList<>();

        try {
            connection = CustomDataSource.getInstance().getConnection();
            if (connection != null) {
                System.out.println("Connection established!");
                st = connection.createStatement();
                ResultSet rs = st.executeQuery(findAllUserSQL);
                while (rs.next()) {
                    Long id = rs.getLong(1);
                    String fname = rs.getString(2);
                    String lname = rs.getString(3);
                    int age = rs.getInt(4);
                    usersList.add(new User(id, fname, lname, age));
                }
                st.close();
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return usersList;
    }

    public User updateUser(User user) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            if (connection != null) {
                System.out.println("Connection established!");
                ps = connection.prepareStatement(updateUserSQL);
                ps.setString(1, user.getFirstName());
                ps.setString(2, user.getLastName());
                ps.setInt(3, user.getAge());
                ps.setLong(4, user.getId());
                int rowAffected = ps.executeUpdate();
                if (rowAffected>0) {
                    ps.close();
                    connection.close();
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void deleteUser(Long userId) {
        try {
            User user = findUserById(userId);
            if (user!=null) {
                connection = CustomDataSource.getInstance().getConnection();
                if (connection != null) {
                    System.out.println("Connection established!");
                    ps = connection.prepareStatement(deleteUserSQL);
                    ps.setLong(1, userId);
                    int rowAffected = ps.executeUpdate();
                    if (rowAffected>0) {
                        System.out.println("User: " + user.getId() + " " + user.getFirstName() + " " + user.getLastName() +
                                " " + user.getAge() + " has been successfully deleted!");
                    } else {
                        System.out.println("User delete failed!");
                    }
                    ps.close();
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SimpleJDBCRepository repository = new SimpleJDBCRepository();

        //User user = new User(2L, "Sarah", "Connor", 55);
        //System.out.println("createUser() method output: " + repository.createUser(user));

        //User user = repository.findUserById(8L);
        //System.out.println("findUserById() method output: " + user.getId() + " " + user.getFirstName() + " " + user.getLastName() + " " + user.getAge());

        //User user = repository.findUserByName("Sarah");
        //System.out.println("findUserByName() method output: " + user.getId() + " " + user.getFirstName() + " " + user.getLastName() + " " + user.getAge());

//        List<User> userList = repository.findAllUser();
//        System.out.println("findAllUser() method output: ");
//        if (userList!=null) {
//            for (User user: userList) {
//                System.out.println(user.getId() + " " + user.getFirstName() + " " + user.getLastName() + " " + user.getAge());
//            }
//        }

//        User user = new User(11L, "Josh", "Turner", 45);
//        System.out.println("updateUser() method output:");
//        if (repository.updateUser(user)!=null) {
//            System.out.println("User: " + user.getId() + " " + user.getFirstName() + " " + user.getLastName() + " " + user.getAge());
//            System.out.println("has been updated!");
//        } else {
//            System.out.println("User update failed!");
//        }

        //System.out.println("deleteUser() method execution");
        //repository.deleteUser(6L);
    }
}
