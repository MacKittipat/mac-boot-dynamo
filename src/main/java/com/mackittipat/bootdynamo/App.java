package com.mackittipat.bootdynamo;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.mackittipat.bootdynamo.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootApplication
public class App implements CommandLineRunner {

    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        AWSCredentialsProvider awsCredentialsProvider = new ClasspathPropertiesFileCredentialsProvider();

        AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .withRegion(Regions.AP_SOUTHEAST_1)
                .build();

        amazonDynamoDB.listTables().getTableNames().forEach(t -> {
            log.info("Table = " + t);
        });

        // -------------------------------------------------------------------------------------------------------------

        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
        Message message = dynamoDBMapper.load(Message.class, "mac", 1581155404L);
        log.info("Load Message = {}", message.toString());

        // -------------------------------------------------------------------------------------------------------------

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":val1", new AttributeValue().withS("mac"));

        DynamoDBQueryExpression<Message> dynamoDBQueryExpression = new DynamoDBQueryExpression<Message>()
                .withKeyConditionExpression("username = :val1")
                .withExpressionAttributeValues(eav);

        List<Message> messageList = dynamoDBMapper.query(Message.class, dynamoDBQueryExpression);
        messageList.forEach(m -> {
            log.info("Query Message = {}", m.toString());
        });
    }
}
