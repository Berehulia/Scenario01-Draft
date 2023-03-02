package com.lineup.function.main;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.UUID;

public class Function implements RequestHandler < String, String > {

  private static final DynamoDbClient DYNAMO_DB = DynamoDbClient.create();

  @Override
  public String handleRequest(
      String message,
      Context context
  ) {

    String tableName = System.getenv( "DYNAMO_DB_TABLE_NAME" );

    var item = Map.of(
        "id",
        AttributeValue.fromS( UUID.randomUUID().toString() ),
        "message",
        AttributeValue.fromS( message )
    );

    var response = DYNAMO_DB.putItem(
        request -> request
            .tableName( tableName )
            .item( item )
    );

    return response
        .sdkHttpResponse()
        .statusText()
        .orElseThrow();
  }

}