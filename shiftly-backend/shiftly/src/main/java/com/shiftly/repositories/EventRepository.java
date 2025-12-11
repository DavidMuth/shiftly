package com.shiftly.repositories;

import com.shiftly.models.Event;
import com.shiftly.models.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class EventRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Event> eventRowMapper = (rs, rowNum) ->
            new Event(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getTimestamp("start_timestamp"),
                    rs.getTimestamp("end_timestamp"),
                    rs.getBoolean("is_break")
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

    public boolean create(Event event, int userId) {
        String sql = "INSERT INTO events(name, description, start_timestamp, end_timestamp,is_break , user_id) VALUES (?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, event.getName());
            ps.setString(2, event.getDescription());
            ps.setTimestamp(3, event.getStartTimestamp());
            ps.setTimestamp(4, event.getEndTimestamp());
            ps.setBoolean(5, event.isBreak());
            ps.setInt(6, userId);
            return ps;
        }, keyHolder);

        return true;
    }
}
