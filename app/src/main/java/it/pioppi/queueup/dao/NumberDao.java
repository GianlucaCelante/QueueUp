package it.pioppi.queueup.dao;

import com.google.firebase.database.DatabaseReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberDao {


    Logger logger = LoggerFactory.getLogger(NumberDao.class);

    public void writeValue(DatabaseReference database, String number){

        database.child("number").setValue(number);
        logger.info("Value: {} inserted", number);
    }
}
