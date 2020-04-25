package com.mackittipat.bootdynamo.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;

@Data
@DynamoDBTable(tableName = "UserMessage")
public class UserMessage {

    @DynamoDBHashKey(attributeName = "Username")
    private String username;

    @DynamoDBRangeKey(attributeName = "CreatedTime")
    private Long createdTime;

    @DynamoDBAttribute(attributeName = "Message")
    private String message;

}
