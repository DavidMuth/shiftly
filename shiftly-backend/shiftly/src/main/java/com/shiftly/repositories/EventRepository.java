package com.shiftly.repositories;

import com.shiftly.models.Event;
import com.shiftly.models.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class EventRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Event> eventRowMapper = (rs, rowNum) ->
            new Event(
                    rs.getInt("user_id"),
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getTimestamp("start_timestamp"),
                    rs.getTimestamp("end_timestamp"),
                    (rs.getBoolean("is_break"))
            );

    public EventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<List<Event>> getEventsFromUser(int userId) {
        String sql = "SELECT * FROM events WHERE user_id = ?";
        List<Event> events = jdbcTemplate.query(sql, eventRowMapper, userId);

        if (events.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(events);
        }
    }

    public Optional<Event> getEventById(int userId, int eventId) {
        try {
            String sql = "SELECT * FROM events WHERE user_id = ? AND id = ?";

            Event event = jdbcTemplate.queryForObject(sql, eventRowMapper, userId, eventId);
            return Optional.ofNullable(event);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean create(Event event) {
        String sql = "INSERT INTO events(name, description, start_timestamp, end_timestamp,is_break , user_id) VALUES (?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, event.getName());
            ps.setString(2, event.getDescription());
            ps.setTimestamp(3, event.getStartTimestamp());
            ps.setTimestamp(4, event.getEndTimestamp());
            ps.setBoolean(5, event.isBreak());
            ps.setInt(6, event.getUserId());
            return ps;
        }, keyHolder);

        return true;
    }

    public Boolean edit(Event event) {
        String sql = "UPDATE events SET name = ?, description = ?, start_timestamp = ?, end_timestamp = ?, is_break = ?, user_id = ? WHERE id = ?";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, event.getName());
            ps.setString(2, event.getDescription());
            ps.setTimestamp(3, event.getStartTimestamp());
            ps.setTimestamp(4, event.getEndTimestamp());
            ps.setBoolean(5, event.isBreak());
            ps.setInt(6, event.getUserId());
            ps.setInt(7, event.getId());
            return ps;
        }, keyHolder);

        return true;
    }
  
    public Boolean delete(int eventId) {
        String sql = "DELETE FROM events WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, eventId);
        return rowsAffected > 0;
    }

    public int startEvent(Event event) {
        String sql = "INSERT INTO events(name, description, start_timestamp, end_timestamp, is_break, user_id) VALUES(?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, event.getName());
            ps.setString(2, event.getDescription());
            ps.setTimestamp(3, event.getStartTimestamp());
            ps.setTimestamp(4, new Timestamp(0)); // Has no default value so needs to be set explicitly, 0 because we do not have an end timestamp yet.
            ps.setBoolean(5, event.isBreak());
            ps.setInt(6, event.getUserId());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public int stopEvent(Event event) {
        String sql = "UPDATE events SET end_timestamp = ? WHERE id = ? and user_id = ?";

        return jdbcTemplate.update(
                sql,
                event.getEndTimestamp(),
                event.getId(),
                event.getUserId()
        );
    } 
}
