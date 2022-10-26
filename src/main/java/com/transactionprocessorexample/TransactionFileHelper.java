package com.transactionprocessorexample;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class TransactionFileHelper {

    @Inject
    static
    Logger logger;

    public enum HEADER {
        USER_ID,
        TRANSACTION_TYPE,
        TRANSACTION_ID,
        TRAN_AMT,
        TRAN_DT,
        MERCHANT_DESCRIPTION,
        MERCHANT_CITY,
        MERCHANT_STATE,
        MERCHANT_ID,
        MCC,
        REF_NUM
    }

    public static List<CSVRecord> parseFile(String filename) throws IOException {
        String[] headers = Arrays.stream(HEADER.values()).map(HEADER::name).toArray(String[]::new);

        try(InputStream in = TransactionFileHelper.class.getResourceAsStream(filename)) {
            return CSVFormat.DEFAULT.builder()
                    .setHeader(headers)
                    .setSkipHeaderRecord(true)
                    .setAllowMissingColumnNames(false)
                    .build()
                    .parse(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .getRecords();
        } catch (Exception e) {
            String msg = "error calculating points";
            logger.error(msg);
            throw new RuntimeException(msg, e);
        }
    }
}
