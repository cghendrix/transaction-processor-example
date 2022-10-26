package com.transactionprocessorexample.points;

import com.transactionprocessorexample.model.CardTransaction;
import com.transactionprocessorexample.model.RewardPointMultiplierMapping;
import io.quarkus.cache.CacheResult;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@ApplicationScoped
public class PointRepository {
    @Inject
    Logger logger;

    @Inject
    DataSource db;

    /**
     * Inserts a collection of card_transactions in a batch
     *
     * @param    transactions  an absolute URL giving the base location of the image
     * @return   an array of update counts containing one element for each command in the batch.
     * @throws   SQLException when the prepared statement cannot execute
     */
    @Transactional
    public int[] insertCardTransactions(Collection<CardTransaction> transactions) throws SQLException {
        String sql =
                "INSERT INTO card_transaction (user_id, transaction_id, amount, points, mcc) " +
                "VALUES(?, ?, ?, ?, ?) ";

        try(Connection dbConnection = db.getConnection();
            PreparedStatement preparedStatement = dbConnection.prepareStatement(sql)
        ) {
            for (CardTransaction transaction : transactions) {
                preparedStatement.setLong(1, transaction.getUserId());
                preparedStatement.setString(2, transaction.getTransactionId());
                preparedStatement.setBigDecimal(3, transaction.getTransactionAmount());
                preparedStatement.setInt(4, transaction.getPointValue());
                preparedStatement.setInt(5, transaction.getMerchantCategoryCode());

                preparedStatement.addBatch();
            }

            return preparedStatement.executeBatch();
        }
    }

    /**
     * Returns the total balance of points for a customer (by userId)
     *
     * @param    userId  primary id for the user to search for
     * @return   an optional with a user's total points or an empty optional when no results found
     * @throws   SQLException when the prepared statement cannot execute
     */
    public Optional<Long> getPointBalance(long userId) throws SQLException {
        String sql =
                " SELECT " +
                "   SUM(points) AS points " +
                " FROM " +
                "   card_transaction " +
                " WHERE " +
                "   user_id = ? ";

        try(Connection dbConnection = db.getConnection();
            PreparedStatement preparedStatement = dbConnection.prepareStatement(sql)
        ) {

            preparedStatement.setLong(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    logger.debugf("no points found for user %s", userId);
                    return Optional.empty();
                }
                resultSet.next();

                return Optional.of(resultSet.getLong("points"));
            }
        }
    }

    /**
     * Returns the available point multiplier mappings (MCC -> Point multiplier)
     *
     * @return   all available point multiplier mappings in the database
     * @throws   SQLException when the prepared statement cannot execute
     */
    @CacheResult(cacheName = "point-mapping-cache")
    public RewardPointMultiplierMapping getAvailableRewardMultipliers() throws SQLException {
        RewardPointMultiplierMapping mapping = new RewardPointMultiplierMapping();
        String sql =
                " SELECT " +
                "   merchant_category_code, reward_points_multiplier " +
                " FROM " +
                "   reward_mapping ";

        try(Connection dbConnection = db.getConnection();
            PreparedStatement preparedStatement = dbConnection.prepareStatement(sql)
        ) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.isBeforeFirst()) {
                    return mapping;
                }
                resultSet.next();

                mapping.addMapping(
                        resultSet.getInt("merchant_category_code"),
                        resultSet.getInt("reward_points_multiplier"));
            }
        }
        return mapping;
    }
}
