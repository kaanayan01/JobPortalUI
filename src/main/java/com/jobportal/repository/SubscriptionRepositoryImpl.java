package com.jobportal.repository;

import com.jobportal.model.enums.PlanType;
import com.jobportal.model.Subscription;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

    private final JdbcTemplate jdbcTemplate;

    public SubscriptionRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Subscription> subscriptionMapper = new RowMapper<>() {
        @Override
        public Subscription mapRow(ResultSet rs, int rowNum) throws SQLException {
            Subscription s = new Subscription();
            s.setSubscriptionId(rs.getInt("subscription_id"));

            String plan = rs.getString("plan_type");
            s.setPlanType(plan != null ? PlanType.valueOf(plan) : null);

            s.setDuration(rs.getInt("duration"));
            s.setPrice(rs.getBigDecimal("price"));
            return s;
        }
    };

    @Override
    public Subscription save(Subscription subscription) {
        String sql = "INSERT INTO subscription(plan_type, duration, price,user_type) VALUES(?,?,?, ?)";
        jdbcTemplate.update(sql,
                subscription.getPlanType().name(),
                subscription.getDuration(),
                subscription.getPrice(),
                subscription.getUserType().name()
        );

        return jdbcTemplate.queryForObject(
                "SELECT * FROM subscription WHERE plan_type=? AND duration=? AND user_type= ? ORDER BY subscription_id DESC LIMIT 1",
                new Object[]{subscription.getPlanType().name(), subscription.getDuration(), subscription.getUserType().name()},
                subscriptionMapper
        );
    }

    @Override
    public Subscription findById(int subscriptionId) {
        String sql = "SELECT * FROM subscription WHERE subscription_id=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{subscriptionId}, subscriptionMapper);
    }

    @Override
    public List<Subscription> findAll() {
        String sql = "SELECT * FROM subscription";
        return jdbcTemplate.query(sql, subscriptionMapper);
    }

    @Override
    public void update(Subscription subscription) {
        String sql = "UPDATE subscription SET plan_type=?, duration=?, price=? WHERE subscription_id=?";
        jdbcTemplate.update(sql,
                subscription.getPlanType().name(),
                subscription.getDuration(),
                subscription.getPrice(),
                subscription.getSubscriptionId()
        );
    }

    @Override
    public void delete(int subscriptionId) {
        String sql = "DELETE FROM subscription WHERE subscription_id=?";
        jdbcTemplate.update(sql, subscriptionId);
    }
}
