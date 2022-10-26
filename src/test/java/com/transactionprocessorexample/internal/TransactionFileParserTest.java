package com.transactionprocessorexample.internal;

import com.transactionprocessorexample.TransactionFileHelper;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionFileParserTest {
    @Test
    public void parseTransactionFile_headersReadCorrectly() throws IOException {
        List<CSVRecord> csvRecords = TransactionFileHelper.parseFile("/TRANSACTIONS.csv");
        assertEquals("BOULEVARD CAFE", csvRecords.get(0).get(TransactionFileHelper.HEADER.MERCHANT_DESCRIPTION));
    }

    @Test
    public void parseTransactionFile_MissingFile() {
        assertThrows(RuntimeException.class, () -> {
            List<CSVRecord> csvRecords = TransactionFileHelper.parseFile("/TRANSACTIONS_MISSING.csv");
            assertEquals("BOULEVARD CAFE", csvRecords.get(0).get(TransactionFileHelper.HEADER.MERCHANT_DESCRIPTION));
        });
    }
}
