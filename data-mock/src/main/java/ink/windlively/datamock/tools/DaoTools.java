package ink.windlively.datamock.tools;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DaoTools {

    public static List<Map<String, Object>> execSelect(DataSource dataSource, String sql){
        long t = System.currentTimeMillis();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            List<Map<String, Object>> result = new ArrayList<>();
            int c = metaData.getColumnCount();
            while (resultSet.next()){
                Map<String, Object> obj = new HashMap<>(c);
                for (int i = 1; i <= c; i++) {
                    obj.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                }
                result.add(obj);
            }
            log.debug("查询耗时: {}ms, SQL: [{}]", System.currentTimeMillis() - t, sql);
            return result;
        } catch (SQLException throwables) {
            throw new IllegalStateException(String.format("exception in sql [%s], message: %s", sql, throwables.getMessage()), throwables);
        }
    }

    public static Map<String, Object> selectOne(DataSource dataSource, String sql){
        List<Map<String, Object>> maps = execSelect(dataSource, sql);
        if(maps.size() == 0) return null;
        if(maps.size() > 1) throw new IllegalStateException(String.format("except one result, but found %d, sql [%s]", maps.size(), sql));
        return maps.get(0);
    }

    public static int execUpdate(DataSource dataSource, String sql){
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            return statement.executeUpdate();
        } catch (SQLException throwables) {
            throw new IllegalStateException(String.format("exception in sql [%s]", sql), throwables);
        }
    }

}
