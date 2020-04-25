package com.mackittipat.bootdynamo;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.mackittipat.bootdynamo.model.UserMessage;
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

        // List all tables
        amazonDynamoDB.listTables().getTableNames().forEach(t -> {
            log.info("Table = " + t);
        });

        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        // -------------------------------------------------------------------------------------------------------------

        // Insert
//        UserMessage userMessage = new UserMessage();
//        userMessage.setUsername("Mac");
//        userMessage.setCreatedTime(System.currentTimeMillis());
//        userMessage.setMessage("Hello");
//        dynamoDBMapper.save(userMessage);

        // -------------------------------------------------------------------------------------------------------------

        // Get
        UserMessage userMessage = dynamoDBMapper.load(UserMessage.class, "Mac", 1587807661974L);
        log.info("Load Message = {}", userMessage.toString());

        // -------------------------------------------------------------------------------------------------------------

        // Query
        Map<String, AttributeValue> attMap = new HashMap<>();
        attMap.put(":username", new AttributeValue().withS("Mac"));
        attMap.put(":createdTime", new AttributeValue().withN("1587807661974"));
        attMap.put(":message", new AttributeValue().withS("o"));

        DynamoDBQueryExpression<UserMessage> dynamoDBQueryExpression = new DynamoDBQueryExpression<UserMessage>()
                .withKeyConditionExpression("Username = :username AND CreatedTime > :createdTime")
                // Use filter with non-key.
                // A QueryFilter is applied after the items have already been read;
                // the process of filtering does not consume any additional read capacity units.
                .withFilterExpression("contains(Message, :message)")
                .withExpressionAttributeValues(attMap);

        List<UserMessage> userMessageList = dynamoDBMapper.query(UserMessage.class, dynamoDBQueryExpression);
        userMessageList.forEach(m -> {
            log.info("Query Message = {}", m.toString());
        });

    }
}
