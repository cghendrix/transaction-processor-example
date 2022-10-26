package com.transactionprocessorexample.points;

import com.transactionprocessorexample.TransactionFileHelper;
import com.transactionprocessorexample.model.CardTransaction;
import com.transactionprocessorexample.model.RewardPointMultiplierMapping;
import org.apache.commons.csv.CSVRecord;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.*;

@ApplicationScoped
public class PointService {

    @Inject
    Logger logger;

    @Inject
    PointRepository pointRepository;

    /**
     * Extracts card transactions from data file, calculating the point value and inserting into database
     *
     * @throws   Exception when the csv cannot be parsed or mapping retrieval / tx insertion fails
     */
    public void calculatePoints() throws Exception {
        List<CSVRecord> csvRecords = this.parseTransactionsFileToCSVRecords("/TRANSACTIONS.csv");
        List<CardTransaction> cardTransactions = this.parseCSVRecordsToCardTransactions(csvRecords);
        pointRepository.insertCardTransactions(cardTransactions);
    }

    /**
     * Parses the csv transaction file into a list of CSVRecords
     *
     * @param   filename relative or absolute path of csv transaction file
     * @return   parsed list of csv records
     * @throws   RuntimeException when the specified transaction file is missing or cannot be opened
     * @see CSVRecord
     */
    private List<CSVRecord> parseTransactionsFileToCSVRecords(String filename) throws RuntimeException {
        List<CSVRecord> csvRecords;
        try {
            csvRecords = TransactionFileHelper.parseFile(filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return csvRecords;
    }

    /**
     * Loads the csv records as a list of CardTransactions with calculated points
     *
     * @param   csvRecords list of parsed CSVRecords to generate and calculate CardTransactions
     * @return   calculated list of CardTransactions
     * @throws   RuntimeException when the specified transaction file is missing or cannot be opened
     * @see CSVRecord
     */
    private List<CardTransaction> parseCSVRecordsToCardTransactions(List<CSVRecord> csvRecords) throws RuntimeException {
        List<CardTransaction> cardTransactions = new ArrayList<>();
        Set<String> transactionIds = new HashSet<>();
        try {
            for (CSVRecord csvRecord : csvRecords) {
                CardTransaction cardTransaction = new CardTransaction();
                cardTransaction.setUserId(Long.parseLong(csvRecord.get(TransactionFileHelper.HEADER.USER_ID)));
                String transactionId = csvRecord.get(TransactionFileHelper.HEADER.TRANSACTION_ID);
                if (transactionIds.contains(transactionId)) {
                    String msg = "duplicate transaction found - transactionId: %s ignoring";
                    logger.warnf(msg, transactionId);
                    continue;
                }
                transactionIds.add(transactionId);
                cardTransaction.setTransactionId(transactionId);
                cardTransaction.setMerchantCategoryCode(Integer.parseInt(csvRecord.get(TransactionFileHelper.HEADER.MCC)));
                cardTransaction.setTransactionAmount(new BigDecimal(csvRecord.get(TransactionFileHelper.HEADER.TRAN_AMT)).setScale(0, RoundingMode.DOWN));
                cardTransaction.setPointValue(this.getPointValueForTransaction(cardTransaction));
                cardTransactions.add(cardTransaction);
            }
        } catch(SQLException e) {
            String msg = "error calculating points";
            logger.error(msg);
            throw new RuntimeException(msg, e);
        }
        return cardTransactions;
    }

    /**
     * Returns the point value for a transaction based on point value mappings retrieved from the db
     *
     * @param   cardTransaction card transaction to get the point value for
     * @return   point value of transaction with multiplier applied
     * @throws   SQLException when the mappings cannot be retrieved
     * @see CardTransaction
     */
    private int getPointValueForTransaction(CardTransaction cardTransaction) throws SQLException {
        RewardPointMultiplierMapping mapping = pointRepository.getAvailableRewardMultipliers();
        int multiplier = mapping.getMultiplierForMCC(cardTransaction.getMerchantCategoryCode());
        return cardTransaction.getTransactionAmount().intValue() * multiplier;
    }

    /**
     * Returns the point value for a transaction based on point value mappings retrieved from the db
     *
     * @param   userId id of user to find point balancefor
     * @return   total point balance for user
     * @throws   SQLException when the lookup fails
     * @see CardTransaction
     */
    public long getPointBalance(long userId) throws SQLException {
        return pointRepository.getPointBalance(userId)
                .orElseThrow(NotFoundException::new);
    }
}
