package com.transactionprocessorexample.internal;

import com.transactionprocessorexample.model.CardTransaction;
import com.transactionprocessorexample.model.RewardPointMultiplierMapping;
import com.transactionprocessorexample.points.PointRepository;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
@TestTransaction
public class PointRepositoryTest {
    @Inject
    PointRepository repository;

    @Test
    public void insertCardTransaction() throws Exception {
        CardTransaction cardTransaction = new CardTransaction();
        cardTransaction.setUserId(1);
        cardTransaction.setTransactionId("transaction1");
        cardTransaction.setTransactionAmount(BigDecimal.TEN);
        cardTransaction.setPointValue(10);
        cardTransaction.setMerchantCategoryCode(1234);

        repository.insertCardTransactions(List.of(cardTransaction));
    }

    @Test
    public void insertDuplicateCardTransaction() throws SQLException {
        CardTransaction cardTransaction = new CardTransaction();
        cardTransaction.setUserId(1);
        cardTransaction.setTransactionId("transaction1");
        cardTransaction.setTransactionAmount(BigDecimal.TEN);
        cardTransaction.setPointValue(10);
        cardTransaction.setMerchantCategoryCode(1234);

        try {
            repository.insertCardTransactions(List.of(cardTransaction, cardTransaction));
            fail("Transaction ID uniqueness constraint should have been triggered");
        } catch (BatchUpdateException e) {
            assert(e.getMessage()).contains("duplicate key value violates unique constraint \"card_transaction_transaction_id_key\"");
        }
    }

    @Test
    public void getDiningMultiplier() throws SQLException {
        RewardPointMultiplierMapping mapping = repository.getAvailableRewardMultipliers();
        assertEquals(mapping.getMultiplierForMCC(5812), 3);
    }

    @Test
    public void getDefaultMultiplier() throws SQLException {
        RewardPointMultiplierMapping mapping = repository.getAvailableRewardMultipliers();
        assertEquals(mapping.getMultiplierForMCC(123), 1);
    }
}
