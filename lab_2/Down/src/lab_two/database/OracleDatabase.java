package lab_two.database;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.sun.rowset.JdbcRowSetImpl;

import javax.sql.rowset.JdbcRowSet;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

final public class OracleDatabase {

    private static final String connectionString = "jdbc:mysql://localhost:3306/tests?serverTimezone=UTC&user=root&password=12012000";

    private static MysqlConnectionPoolDataSource source;

    public static MysqlConnectionPoolDataSource getPool() {
        if (source == null)  {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
                source = new MysqlConnectionPoolDataSource();
                source.setUrl(connectionString);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return source;
    }

    public static <T> List<T> selectData(Connection connection, String table, SetFieldsCallback<T> callback)
            throws SQLException {
        return selectData(connection, table, -1, null, callback);
    }

    public static <T> List<T> selectData(Connection connection, String table, int id, String columnId, SetFieldsCallback<T> callback)
            throws SQLException {
        JdbcRowSet rowSet = new JdbcRowSetImpl(connection);
        if (columnId != null) {
            rowSet.setCommand(String.format("SELECT * FROM %s WHERE %s = %d", table, columnId, id));
        } else {
            rowSet.setCommand("SELECT * FROM " + table);
        }
        rowSet.execute();
        ArrayList<T> data = new ArrayList<>();
        while (rowSet.next()) data.add(callback.setFields(rowSet));

        return data;
    }

    public static int selectLastInsertedId(Connection conn) throws SQLException {
        try (Statement s = conn.createStatement()) {
            try (ResultSet set = s.executeQuery("SELECT LAST_INSERT_ID();")) {
                set.next();
                return set.getInt(1);
            }
        }
    }

    public interface SetFieldsCallback<T> {
        T setFields(JdbcRowSet rowSet) throws SQLException;
    }
}
//класс для работы с бд
