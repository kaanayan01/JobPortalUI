package com.jobportal.repository;

import com.jobportal.model.Payment;

import com.jobportal.model.enums.PaymentMethod;
import com.jobportal.model.enums.PaymentStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final JdbcTemplate jdbcTemplate;

    public PaymentRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Payment> paymentMapper = new RowMapper<>() {
        @Override
        public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Payment p = new Payment();
            p.setPaymentId(rs.getInt("payment_id"));
            p.setUserId(rs.getInt("user_id"));
            p.setSubscriptionId(rs.getInt("subscription_id"));
            p.setAmount(rs.getBigDecimal("amount"));

            String method = rs.getString("payment_method");
            p.setPaymentMethod(method != null ? PaymentMethod.valueOf(method) : null);

            p.setOrderId(rs.getString("order_id"));
            p.setTransactionId(rs.getString("transaction_id"));

            String status = rs.getString("status");
            p.setStatus(status != null ? PaymentStatus.valueOf(status) : PaymentStatus.PENDING);

            Timestamp ts = rs.getTimestamp("payment_date");
            if (ts != null) {
                p.setPaymentDate(ts.toLocalDateTime());
            }

            return p;
        }
    };

    @Override
    public Payment save(Payment payment) {
        String sql = "INSERT INTO payments(user_id, subscription_id, amount, payment_method, order_id, transaction_id, status) VALUES(?,?,?,?,?,?,?)";
        jdbcTemplate.update(sql,
                payment.getUserId(),
                payment.getSubscriptionId(),
                payment.getAmount(),
                payment.getPaymentMethod().name(),
                payment.getOrderId(),
                payment.getTransactionId(),
                payment.getStatus().name()
        );

        return jdbcTemplate.queryForObject(
                "SELECT * FROM payments WHERE user_id=? AND subscription_id=? ORDER BY payment_id DESC LIMIT 1",
                new Object[]{payment.getUserId(), payment.getSubscriptionId()},
                paymentMapper
        );
    }

    @Override
    public Payment findById(int paymentId) {
        String sql = "SELECT * FROM payments WHERE payment_id=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{paymentId}, paymentMapper);
    }

    @Override
    public List<Payment> findAll() {
        String sql = "SELECT * FROM payments";
        return jdbcTemplate.query(sql, paymentMapper);
    }

    @Override
    public List<Payment> findByUserId(int userId) {
        String sql = "SELECT * FROM payments WHERE user_id=?";
        return jdbcTemplate.query(sql, new Object[]{userId}, paymentMapper);
    }

    @Override
    public void update(Payment payment) {
        String sql = "UPDATE payments SET amount=?, payment_method=?, order_id=?, transaction_id=?, status=? WHERE payment_id=?";
        jdbcTemplate.update(sql,
                payment.getAmount(),
                payment.getPaymentMethod().name(),
                payment.getOrderId(),
                payment.getTransactionId(),
                payment.getStatus().name(),
                payment.getPaymentId()
        );
    }

    @Override
    public void delete(int paymentId) {
        String sql = "DELETE FROM payments WHERE payment_id=?";
        jdbcTemplate.update(sql, paymentId);
    }
}
