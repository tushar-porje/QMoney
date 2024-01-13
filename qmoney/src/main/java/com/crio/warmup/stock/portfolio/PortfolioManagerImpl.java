
package com.crio.warmup.stock.portfolio;


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
    public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,LocalDate endDate) throws JsonProcessingException{
          List<AnnualizedReturn> annualizedReturnsList=new ArrayList<>();
          // try {
            for(PortfolioTrade pf:portfolioTrades){
              List<Candle> candle=getStockQuote(pf.getSymbol(), pf.getPurchaseDate(),endDate);
              double totalReturn = (getClosingPriceOnEndDate(candle) - getOpeningPriceOnStartDate(candle)) 
                                      / getOpeningPriceOnStartDate(candle);
              double total_num_years = ChronoUnit.DAYS.between(pf.getPurchaseDate(), endDate)/365.2422;
              double annualized_returns = Math.pow((1 + totalReturn),(1 / total_num_years)) - 1;
              annualizedReturnsList.add(new AnnualizedReturn(pf.getSymbol(), annualized_returns, totalReturn));
            }
            Collections.sort(annualizedReturnsList,getComparator());
          // } catch (Exception e) {
          //   e.printStackTrace();
          // }
                
    return annualizedReturnsList;
    }


  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws JsonProcessingException {
        // String uri=buildUri(symbol, from, to);
        // Candle[] candle=restTemplate.getForObject(uri,TiingoCandle[].class);
        // return Arrays.asList(candle);
        return stockQuotesService.getStockQuote(symbol, from, to);
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
       String uriTemplate = "https:api.tiingo.com/tiingo/daily/"+symbol+"/prices?"
            + "startDate="+startDate+"&endDate="+endDate+"&token="+getToken();
        return uriTemplate;
  }

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
  
  public static String getToken(){
    return "e1e1f51f982170a0d40fd5b182f1c0760f49e48e";
    // return "59cd0363a623b4be341575d7db6a77caf6a33c15";
  }

  
}
