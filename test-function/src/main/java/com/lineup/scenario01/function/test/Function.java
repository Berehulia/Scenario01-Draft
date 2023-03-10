package com.lineup.scenario01.function.test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public class Function implements RequestHandler < APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent > {

  private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

  private static final URI MAIN_FUNCTION_URI = URI.create( System.getenv( "MAIN_FUNCTION_URL" ) );

  @Override
  public APIGatewayProxyResponseEvent handleRequest( APIGatewayProxyRequestEvent event, Context context ) {

    IntStream.range( 0, 1000 )
        .parallel()
        .mapToObj( Function::buildHttpRequest )
        .map( Function::sendAsyncRequest )
        .forEach( CompletableFuture::join );

    return new APIGatewayProxyResponseEvent()
        .withStatusCode( 200 );
  }

  private static HttpRequest buildHttpRequest( int i ) {

    return HttpRequest
        .newBuilder( MAIN_FUNCTION_URI )
        .header( "content-type", "application/json" )
        .POST( HttpRequest.BodyPublishers.ofString( UUID.randomUUID().toString() ) )
        .build();
  }

  private static CompletableFuture < HttpResponse < String > > sendAsyncRequest( HttpRequest request ) {

    return HTTP_CLIENT.sendAsync( request, HttpResponse.BodyHandlers.ofString() );
  }

}