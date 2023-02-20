package com.azunitech.search;


import com.azunitech.search.sns.ECSSnsClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ECSSnsClientTest {
    @Autowired
    ECSSnsClient ecsSnsClient;

    @Test
    public void generalTest(){
        String arn = ecsSnsClient.createTopic("test");
        ecsSnsClient.pubTopic( "test", arn);
    }

}
