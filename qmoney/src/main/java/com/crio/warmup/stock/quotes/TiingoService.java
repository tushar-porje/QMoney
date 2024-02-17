
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {


  private RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)throws JsonProcessingException, StockQuoteServiceException{

        List<Candle> stocksStartToEndDate=new ArrayList<>();
        if(from.compareTo(to)>=0){
          throw new RuntimeException();
        }
        String uri=buildUri(symbol, from, to);
        // Candle[] candle=restTemplate.getForObject(uri,TiingoCandle[].class);
        try {
          String apiResponse=restTemplate.getForObject(uri,String.class);
          ObjectMapper mapper =getObjectMapper();
          Candle[] candle=mapper.readValue(apiResponse, TiingoCandle[].class);

          if(candle!=null){
            stocksStartToEndDate= Arrays.asList(candle);
          }else{
            stocksStartToEndDate= Arrays.asList(new Candle[0]);
          }
        } catch (Exception e) {
          throw new StockQuoteServiceException("Error occured when requesting response form tiingo api");
        }
        
        // for(int i=0;i<candle.length;i++){
        //   stocksStartToEndDate.add(candle[i]);
        // }
        return stocksStartToEndDate;
        
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate = "https:api.tiingo.com/tiingo/daily/"+symbol+"/prices?"
         + "startDate="+startDate+"&endDate="+endDate+"&token="+getToken();
     return uriTemplate;
  }

  public static String getToken(){
    return "e1e1f51f982170a0d40fd5b182f1c0760f49e48e";
    // return "59cd0363a623b4be341575d7db6a77caf6a33c15";
  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.

}
