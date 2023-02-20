package com.azunitech.search.sqs;

import com.azunitech.search.sqs.ECSSqsClient;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

@Log4j2
@SpringBootTest
public class SQSClientTest {
    @Autowired
    ECSSqsClient ecsSqsClient;

    @Test
    public void generalTest(){
        String queueUrl= ecsSqsClient.createQueue("queueName2" );
        ecsSqsClient.listQueues();
        ecsSqsClient.listQueuesFilter(queueUrl);
        List<Message> messages = ecsSqsClient.receiveMessages(queueUrl);
        messages.forEach(x -> log.info("****->{}", x.body()));
        ecsSqsClient.sendBatchMessages(queueUrl);
        ecsSqsClient.changeMessages(queueUrl, messages);
        ecsSqsClient.deleteMessages(queueUrl, messages) ;
        ecsSqsClient.close();
    }
}
