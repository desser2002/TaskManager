package domen.infrastructure.database;
import domen.domain.TaskRepository;
import domen.domain.model.Task;
import domen.domain.model.TaskStatus;

import java.sql.*;
import java.util.*;

public class TaskRepositorySQL implements TaskRepository {
    private static final String SAVE_STATEMENT = "INSERT INTO task (id, title, description, status, start_date_time, finish_date_time)" +
            " VALUES (?::uuid,?,?,?::task_status,?::timestamp,?::timestamp)";
    private static final String SELECT_BY_ID_STATEMENT = "SELECT id,title,description,status,start_date_time,finish_date_time FROM task WHERE id = ?::uuid";
    private static final String UPDATE_STATEMENT = "UPDATE task SET title=?, description=?, status=?, start_date_time=?,finish_date_time=? WHERE id=?";
    private final Connection externalConnection;
    private static final String SELECT_ALL_STATEMENT = "SELECT id,title,description,status,start_date_time,finish_date_time FROM task";

    public TaskRepositorySQL() {
        this.externalConnection = null;
    }

    public TaskRepositorySQL(Connection connection) {
        this.externalConnection = connection;
    }

    @Override
    public void save(Task task) {
        try (Connection connection = getConnection();
             PreparedStatement ps = prepareStatementForSaveOrUpdate(task, connection, SAVE_STATEMENT, true)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error during saving task to database", e);
        }
    }

    @Override
    public void update(Task task) {
        try (Connection connection = getConnection();
             PreparedStatement ps = prepareStatementForSaveOrUpdate(task, connection, UPDATE_STATEMENT, false)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error during updating task in database", e);
        }
    }

    @Override
    public Optional<Task> findById(String id) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_STATEMENT)) {
            ps.setObject(1, UUID.fromString(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Task task = mapRowToTask(rs);
                    return Optional.of(task);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error during getting task info to database", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Task> getAll() {
        List<Task> tasks = new ArrayList<>();
        try (
                Connection connection = getConnection();
                PreparedStatement ps = connection.prepareStatement(SELECT_ALL_STATEMENT);
                ResultSet rs = ps.executeQuery()
        ) {
            {
                while (rs.next()) {
                    Task task = mapRowToTask(rs);
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tasks;
    }

    private Connection getConnection() throws SQLException {
        if (externalConnection != null) {
            return externalConnection;
        }
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/TaskManager",
                "admin",
                "admin"
        );
    }

    private void bindTaskFields(PreparedStatement ps, Task task, int offset) throws SQLException {
        ps.setString(offset, task.title());
        ps.setString(offset + 1, task.description());
        ps.setString(offset + 2, task.status().toString());
        if (task.startDateTime() != null) {
            ps.setTimestamp(offset + 3, Timestamp.valueOf(task.startDateTime()));
        } else {
            ps.setNull(offset + 3, Types.TIMESTAMP);
        }
        if (task.finishDateTime() != null) {
            ps.setTimestamp(offset + 4, Timestamp.valueOf(task.finishDateTime()));
        } else {
            ps.setNull(offset + 4, Types.TIMESTAMP);
        }
    }

    private PreparedStatement prepareStatementForSaveOrUpdate(Task task, Connection connection, String sql, boolean idFirst) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        if (idFirst) {
            ps.setObject(1, UUID.fromString(task.id()));
            bindTaskFields(ps, task, 2);
        } else {
            bindTaskFields(ps, task, 1);
            ps.setObject(6, UUID.fromString(task.id()));
        }
        return ps;
    }

    private Task mapRowToTask(ResultSet rs) throws SQLException {
        return new Task(
                rs.getObject("id", UUID.class).toString(),
                rs.getString("title"),
                rs.getString("description"),
                TaskStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("start_date_time") != null ? rs.getTimestamp("start_date_time").toLocalDateTime() : null,
                rs.getTimestamp("finish_date_time") != null ? rs.getTimestamp("finish_date_time").toLocalDateTime() : null,
                new LinkedHashSet<>()
        );
    }
}
