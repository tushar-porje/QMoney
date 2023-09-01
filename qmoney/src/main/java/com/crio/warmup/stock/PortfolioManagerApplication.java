
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {
  public static RestTemplate restTemplate = new RestTemplate();
  public static PortfolioManager portfolioManager = PortfolioManagerFactory.getPortfolioManager(restTemplate);

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Task:
  //       - Read the json file provided in the argument[0], The file is available in the classpath.
  //       - Go through all of the trades in the given file,
  //       - Prepare the list of all symbols a portfolio has.
  //       - if "trades.json" has trades like
  //         [{ "symbol": "MSFT"}, { "symbol": "AAPL"}, { "symbol": "GOOGL"}]
  //         Then you should return ["MSFT", "AAPL", "GOOGL"]
  //  Hints:
  //    1. Go through two functions provided - #resolveFileFromResources() and #getObjectMapper
  //       Check if they are of any help to you.
  //    2. Return the list of all symbols in the same order as provided in json.

  //  Note:
  //  1. There can be few unused imports, you will need to fix them to make the build pass.
  //  2. You can use "./gradlew build" to check if your code builds successfully.
//++++++++++++++++++++++++++++++++++++++++++++
  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
     //System.out.println(args[0]);
    ObjectMapper ob=getObjectMapper();
    List<String> l=new ArrayList<String>();
    PortfolioTrade[] pf=ob.readValue(resolveFileFromResources(args[0]),PortfolioTrade[].class);
    for(PortfolioTrade p:pf){
      l.add(p.getSymbol());
    }
    //  return Collections.emptyList();
    return l;
  }
//+++++++++++++++++++++++











//   // TODO: CRIO_TASK_MODULE_REST_API
//   //  Find out the closing price of each stock on the end_date and return the list
//   //  of all symbols in ascending order by its close value on end date.

//   // Note:
//   // 1. You may have to register on Tiingo to get the api_token.
//   // 2. Look at args parameter and the module instructions carefully.
//   // 2. You can copy relevant code from #mainReadFile to parse the Json.
//   // 3. Use RestTemplate#getForObject in order to call the API,
//   //    and deserialize the results in List<Candle>



  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


//   // TODO: CRIO_TASK_MODULE_JSON_PARSING
//   //  Follow the instructions provided in the task documentation and fill up the correct values for
//   //  the variables provided. First value is provided for your reference.
//   //  A. Put a breakpoint on the first line inside mainReadFile() which says
//   //    return Collections.emptyList();
//   //  B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
//   //  following the instructions to run the test.
//   //  Once you are able to run the test, perform following tasks and record the output as a
//   //  String in the function below.
//   //  Use this link to see how to evaluate expressions -
//   //  https://code.visualstudio.com/docs/editor/debugging#_data-inspection
//   //  1. evaluate the value of "args[0]" and set the value
//   //     to the variable named valueOfArgument0 (This is implemented for your reference.)
//   //  2. In the same window, evaluate the value of expression below and set it
//   //  to resultOfResolveFilePathArgs0
//   //     expression ==> resolveFileFromResources(args[0])
//   //  3. In the same window, evaluate the value of expression below and set it
//   //  to toStringOfObjectMapper.
//   //  You might see some garbage numbers in the output. Dont worry, its expected.
//   //    expression ==> getObjectMapper().toString()
//   //  4. Now Go to the debug window and open stack trace. Put the name of the function you see at
//   //  second place from top to variable functionNameFromTestFileInStackTrace
//   //  5. In the same window, you will see the line number of the function in the stack trace window.
//   //  assign the same to lineNumberFromTestFileInStackTrace
//   //  Once you are done with above, just run the corresponding test and
//   //  make sure its working as expected. use below command to do the same.
//   //  ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

     String valueOfArgument0 = "trades.json";
     String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/tusharporje13091999-ME_QMONEY_V2/qmoney/bin/main/trades.json"
     ;
     String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@1573f9fc";
     String functionNameFromTestFileInStackTrace = "mainReadFile()";
     String lineNumberFromTestFileInStackTrace = "29";


    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
        lineNumberFromTestFileInStackTrace});
  }

// // Note:
//   // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    String filename =args[0];
    LocalDate endDate = LocalDate.parse(args[1]);
    List<TotalReturnsDto> totalReturnsDtos=new ArrayList<>();
    List<String> sortedStockByEnddateList=new ArrayList<String>();
    //Deserialize stock data from JSON file into a List of PortfolioTrade objects
    List<PortfolioTrade> pf=readTradesFromJson(filename);

    // for(PortfolioTrade p:pf){
    //   URI uri=new URI(prepareUrl(p,endDate,getToken()));
    //   TiingoCandle[] respoCandle = restTemplate.getForObject(uri,TiingoCandle[].class);
    //   TiingoCandle endDateCandle=respoCandle[respoCandle.length-1];
    //   totalReturnsDtos.add(new TotalReturnsDto(p.getSymbol(),endDateCandle.getClose()));
    // }
    
    for(PortfolioTrade p:pf){
      List<Candle> respoCandles=fetchCandles(p, endDate,getToken());
      Candle endDateCandle=respoCandles.get(respoCandles.size()-1);
      totalReturnsDtos.add(new TotalReturnsDto(p.getSymbol(),endDateCandle.getClose()));
    }
    Collections.sort(totalReturnsDtos,(p1,p2) -> Double.compare(p1.getClosingPrice(), p2.getClosingPrice()));
    // sortedStockByEnddateList = totalReturnsDtos.stream().sorted((p1,p2) -> p1.getClosingPrice().compareTo(p2.getClosingPrice()))
    //                                 .map(c -> c.getSymbol())
    //                                 .collect(Collectors.toList());
    for(TotalReturnsDto a:totalReturnsDtos){
      sortedStockByEnddateList.add(a.getSymbol());
    }
    return sortedStockByEnddateList;
  }
//   // TODO:
//   //  After refactor, make sure that the tests pass by using these two commands
//   //  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
//   //  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    PortfolioTrade[] pf=getObjectMapper().readValue(resolveFileFromResources(filename),PortfolioTrade[].class);
    return Arrays.asList(pf);
  }


//   // TODO:
//   //  Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
     return "https://api.tiingo.com/tiingo/daily/"+trade.getSymbol()+"/prices?startDate="+trade.getPurchaseDate()
            +"&endDate="+endDate+"&token="+token;
  }

  public static String getToken(){
    return "e1e1f51f982170a0d40fd5b182f1c0760f49e48e";
    // return "59cd0363a623b4be341575d7db6a77caf6a33c15";
  }
//   // TODO:
//   //  Ensure all tests are passing using below command
//   //  ./gradlew test --tests ModuleThreeRefactorTest
  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    for(int i=0;i<candles.size();i++){
      if(candles.get(i)!=null){
        return candles.get(i).getOpen();
      }
    }
    return null;
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    for(int i=candles.size()-1;i>=0;i--){
      if(candles.get(i).getClose()!=null){
        return candles.get(i).getClose();
      }
    }
     return null;
  }


  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    RestTemplate restTemplate=new RestTemplate();
    String uri=prepareUrl(trade,endDate,getToken());
    TiingoCandle[] respoCandle = restTemplate.getForObject(uri,TiingoCandle[].class);
    return Arrays.stream(respoCandle).collect(Collectors.toList());
  }

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
      LocalDate endDate = LocalDate.parse(args[1]);
      List<PortfolioTrade> pf=readTradesFromJson(args[0]);
      List<AnnualizedReturn> annualizedReturnsList=new ArrayList<>();
      for(PortfolioTrade p:pf){
        List<Candle> respoCandles=fetchCandles(p, endDate,getToken());
        annualizedReturnsList.add(calculateAnnualizedReturns(endDate, p, getOpeningPriceOnStartDate(respoCandles), getClosingPriceOnEndDate(respoCandles)));
      }

      //annualizedReturnsList.stream().sorted((p1,p2) -> p2.getAnnualizedReturn().compareTo(p1.getAnnualizedReturn()));
      Collections.sort(annualizedReturnsList, new Comparator<AnnualizedReturn>(){
        public int compare(AnnualizedReturn s1,AnnualizedReturn s2){  
          return s2.getAnnualizedReturn().compareTo(s1.getAnnualizedReturn());  
          }  
      });
     return annualizedReturnsList;
  }

 

//   // TODO: CRIO_TASK_MODULE_CALCULATIONS
//   //  Return the populated list of AnnualizedReturn for all stocks.
//   //  Annualized returns should be calculated in two steps:
//   //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
//   //      1.1 Store the same as totalReturns
//   //   2. Calculate extrapolated annualized returns by scaling the same in years span.
//   //      The formula is:
//   //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
//   //      2.1 Store the same as annualized_returns
//   //  Test the same using below specified command. The build should be successful.
//   //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
        double totalReturn = (sellPrice - buyPrice) / buyPrice;
        double total_num_years = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate)/365.2422;
        double annualized_returns = Math.pow((1 + totalReturn),(1 / total_num_years)) - 1;
      return new AnnualizedReturn(trade.getSymbol(), annualized_returns, totalReturn);
  }














//   public static void main(String[] args) throws Exception {
//     Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
//     ThreadContext.put("runId", UUID.randomUUID().toString());
    
    
//     printJsonObject(mainReadQuotes(args));


//     printJsonObject(mainCalculateSingleReturn(args));






  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  private static String readFileAsString(String filename)
      throws UnsupportedEncodingException, IOException, URISyntaxException {
    return new String(Files.readAllBytes(resolveFileFromResources(filename).toPath()), "UTF-8");
  }

  // public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
  //     throws Exception {
  //      String file = args[0];
  //      LocalDate endDate = LocalDate.parse(args[1]);
  //      String contents = readFileAsString(file);
  //      ObjectMapper objectMapper = getObjectMapper();
  //      return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
  // }

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
    String file = args[0];
    LocalDate endDate = LocalDate.parse(args[1]);
    String contents = readFileAsString(file);
    ObjectMapper objectMapper = getObjectMapper();
    // return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
    return portfolioManager.calculateAnnualizedReturn(
        Arrays.asList(objectMapper.readValue(contents, PortfolioTrade[].class)), endDate);
  }


  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());




    printJsonObject(mainCalculateReturnsAfterRefactor(args));
  }
}

