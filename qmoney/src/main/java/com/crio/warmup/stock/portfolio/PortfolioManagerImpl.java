
package com.crio.warmup.stock.portfolio;

// import static java.time.temporal.ChronoUnit.DAYS;
// import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.crio.warmup.stock.exception.StockQuoteServiceException;

// import com.crio.warmup.stock.dto.TiingoCandle;
// import com.crio.warmup.stock.exception.StockQuoteServiceException;
// import com.crio.warmup.stock.quotes.StockQuotesService;
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
// import java.time.LocalDate;
// import java.time.temporal.ChronoUnit;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.Comparator;
// import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
// import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
// import java.util.concurrent.TimeUnit;
// import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  private RestTemplate restTemplate;
  private StockQuotesService stockQuotesService;

  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService=stockQuotesService;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF
  // public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
  //       PortfolioTrade trade, Double buyPrice, Double sellPrice) {
  //         double totalReturn = (sellPrice - buyPrice) / buyPrice;
  //         double total_num_years = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate)/365.2422;
  //         double annualized_returns = Math.pow((1 + totalReturn),(1 / total_num_years)) - 1;
  //       return new AnnualizedReturn(trade.getSymbol(), annualized_returns, totalReturn);
  //   }
      @Override
    public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,LocalDate endDate) throws JsonProcessingException, StockQuoteServiceException{
         
      List<AnnualizedReturn> annualizedReturnsList=new ArrayList<>();
          
        for(PortfolioTrade pf:portfolioTrades){
          // List<Candle> candle=getStockQuote(pf.getSymbol(), pf.getPurchaseDate(),endDate);
          // double totalReturn = (getClosingPriceOnEndDate(candle) - getOpeningPriceOnStartDate(candle)) 
          //                         / getOpeningPriceOnStartDate(candle);
          // double total_num_years = ChronoUnit.DAYS.between(pf.getPurchaseDate(), endDate)/365.2422;
          // double annualized_returns = Math.pow((1 + totalReturn),(1 / total_num_years)) - 1;
          annualizedReturnsList.add(getAnnualizedAndTotalReturn(pf, endDate));
        }
        Collections.sort(annualizedReturnsList,getComparator());
                   
    return annualizedReturnsList;
    }


  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonProcessingException, StockQuoteServiceException {  
    return stockQuotesService.getStockQuote(symbol, from, to);
  }

  // protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
  //      String uriTemplate = "https:api.tiingo.com/tiingo/daily/"+symbol+"/prices?"
  //           + "startDate="+startDate+"&endDate="+endDate+"&token="+getToken();
  //       return uriTemplate;
  // }

  protected static Double getClosingPriceOnEndDate(List<Candle> candles) {
    for(int i=candles.size()-1;i>=0;i--){
      if(candles.get(i).getClose()!=null){
        return candles.get(i).getClose();
      }
    }
  return null;
  }

  protected static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    for(int i=0;i<candles.size();i++){
      if(candles.get(i)!=null){
        return candles.get(i).getOpen();
      }
    }
  return null;
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturnParallel(List<PortfolioTrade> portfolioTrades,
     LocalDate endDate, int numThreads) throws InterruptedException, StockQuoteServiceException {

      List<AnnualizedReturn> annualizedReturnsList=new ArrayList<>();
      List<Future<AnnualizedReturn>> futureAnnualizedReturn=new ArrayList<>();
      final ExecutorService pool=Executors.newFixedThreadPool(numThreads);

      for(PortfolioTrade pf:portfolioTrades){
        // List<Candle> candle=getStockQuote(pf.getSymbol(), pf.getPurchaseDate(),endDate);
        // double totalReturn = (getClosingPriceOnEndDate(candle) - getOpeningPriceOnStartDate(candle)) 
        //                         / getOpeningPriceOnStartDate(candle);
        // double total_num_years = ChronoUnit.DAYS.between(pf.getPurchaseDate(), endDate)/365.2422;
        // double annualized_returns = Math.pow((1 + totalReturn),(1 / total_num_years)) - 1;
        // annualizedReturnsList.add(new AnnualizedReturn(pf.getSymbol(), annualized_returns, totalReturn));
        Callable<AnnualizedReturn> callableTask = () -> {
          return getAnnualizedAndTotalReturn(pf, endDate);
        };
        Future<AnnualizedReturn> futureReturns = pool.submit(callableTask);
        futureAnnualizedReturn.add(futureReturns);
      }

      for (Future<AnnualizedReturn> futureReturn:futureAnnualizedReturn) {
        try {
          AnnualizedReturn returns = futureReturn.get();
          annualizedReturnsList.add(returns);
        } catch (ExecutionException e) {
          throw new StockQuoteServiceException("Error when calling the API", e);
    
        }
      }
      Collections.sort(annualizedReturnsList,getComparator());              
    return annualizedReturnsList;
  }

  private AnnualizedReturn getAnnualizedAndTotalReturn(PortfolioTrade trade,LocalDate endDate) throws JsonProcessingException, StockQuoteServiceException{
          List<Candle> candle=getStockQuote(trade.getSymbol(), trade.getPurchaseDate(),endDate);
          double totalReturn = (getClosingPriceOnEndDate(candle) - getOpeningPriceOnStartDate(candle)) 
                                  / getOpeningPriceOnStartDate(candle);
          double total_num_years = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate)/365.2422;
          double annualized_returns = Math.pow((1 + totalReturn),(1 / total_num_years)) - 1;
          return new AnnualizedReturn(trade.getSymbol(), annualized_returns, totalReturn);
  }
  
  // public static String getToken(){
  //   return "e1e1f51f982170a0d40fd5b182f1c0760f49e48e";
  //   // return "59cd0363a623b4be341575d7db6a77caf6a33c15";
  // }

  
}
