package com.shiftly.repositories;

import com.shiftly.models.Event;
import com.shiftly.models.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

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
}
