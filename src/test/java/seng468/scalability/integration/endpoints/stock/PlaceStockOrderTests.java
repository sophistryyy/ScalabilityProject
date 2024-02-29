package seng468.scalability.integration.endpoints.stock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import seng468.scalability.authentication.JwtUtil;
import seng468.scalability.matchingEngine.MatchingEngineOrdersRepository;
import seng468.scalability.models.entity.PortfolioEntry;
import seng468.scalability.models.entity.Stock;
import seng468.scalability.models.entity.StockOrder;
import seng468.scalability.models.entity.User;
import seng468.scalability.models.entity.Wallet;
import seng468.scalability.models.entity.WalletTX;
import seng468.scalability.models.entity.StockOrder.OrderStatus;
import seng468.scalability.models.entity.StockOrder.OrderType;
import seng468.scalability.repositories.PortfolioRepository;
import seng468.scalability.repositories.StockRepository;
import seng468.scalability.repositories.UserRepository;
import seng468.scalability.repositories.WalletRepository;
import seng468.scalability.repositories.WalletTXRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlaceStockOrderTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MatchingEngineOrdersRepository matchingEngineOrdersRepository;

    @Autowired
    private WalletTXRepository walletTXRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired 
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired 
    private PortfolioRepository portfolioRepository;

    @Autowired
    private StockRepository stockRepository;

    private Stock stock1;
    private Stock stock2;

    private User companyUser;
    private User user;

    @BeforeAll
    void setupBeforeAll() throws Exception {
    
        System.out.println(stockRepository.findAll());
        System.out.println("HOW MANNYY");
        stock1 = new Stock("Google");
        stockRepository.saveNewStock(stock1);

        // stock2 = new Stock("Apple");
        // stockRepository.saveNewStock(stock2);

        companyUser =  new User("VanguardETviwF", "Vang@123", "Vanguard Corp.");
        userRepository.save(companyUser);

        user =  new User("FinanceGuru", "Fguru@2024", "The Finance Guru");
        userRepository.save(user);
    } 

    @BeforeEach
    void setupBeforeEach() throws Exception {
        portfolioRepository.deleteAll();
        walletRepository.deleteAll();
        walletTXRepository.deleteAll();
        matchingEngineOrdersRepository.deleteAll();
    }

    @AfterAll
    void breakdownAfterAll() {
        stockRepository.deleteAll();
        userRepository.deleteAll();
        walletRepository.deleteAll();
        walletTXRepository.deleteAll();
        matchingEngineOrdersRepository.deleteAll();
    }


    @Test
    public void testPlaceOrderInitialLimitSellOrder() throws Exception {
        Wallet userWallet = new Wallet(companyUser.getUsername());
        walletRepository.save(userWallet);
        PortfolioEntry initialPortfolioEntry = new PortfolioEntry(stock1.getId(), "Google", companyUser.getUsername(), 100);
        portfolioRepository.save(initialPortfolioEntry);

        String jwtToken = jwtUtil.generateToken(companyUser.getUsername());

        String requestBody = "{\"stock_id\": " + stock1.getId() + ", \"is_buy\": false, \"order_type\": \"LIMIT\", \"quantity\": 100, \"price\": 80}";

        MvcResult res = mvc.perform(post("/placeStockOrder")
        .contentType(MediaType.APPLICATION_JSON)
        .header("token", jwtToken)
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true)) 
        .andExpect(jsonPath("$.data").doesNotExist()) 
        .andReturn();

        // Check if stock order transaction entries was updated correctly
        List<StockOrder> entries = matchingEngineOrdersRepository.findAllByUsername(companyUser.getUsername());
        assertEquals(1, entries.size());
        StockOrder entry = entries.get(0);
        StockOrder expected = new StockOrder(null, null, stock1.getId(), false, OrderType.LIMIT, 100, 80, OrderStatus.IN_PROGRESS, companyUser.getUsername());
        assertStockTX(expected, entry);

        // No wallet transactions for creating sell order
        List<WalletTX> walletTXEntries = walletTXRepository.findAllByUsername(user.getUsername());
        assertEquals(0, walletTXEntries.size());

        // All stocks of stock1.getId() were sold, so entry should be deleted
        PortfolioEntry portfolioEntry = portfolioRepository.findEntryByStockIdAndUsername(stock1.getId(), companyUser.getUsername());
        assertNull(portfolioEntry);
    }

    @Test
    public void testPlaceOrderInitialLimitBuyOrder() throws Exception {
        Wallet userWallet = new Wallet(user.getUsername());
        userWallet.incrementBalance(8000);
        walletRepository.save(userWallet);
        PortfolioEntry initialPortfolioEntry = new PortfolioEntry(1, "Google", user.getUsername(), 0);
        portfolioRepository.save(initialPortfolioEntry);

        String jwtToken = jwtUtil.generateToken(user.getUsername());

        String requestBody = "{\"stock_id\": " + stock1.getId() + ", \"is_buy\": true, \"order_type\": \"LIMIT\", \"quantity\": 100, \"price\": 80}";

        MvcResult res = mvc.perform(post("/placeStockOrder")
        .contentType(MediaType.APPLICATION_JSON)
        .header("token", jwtToken)
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true)) 
        .andExpect(jsonPath("$.data").doesNotExist()) 
        .andReturn();

        // Check if stock order transaction entries was updated correctly
        List<StockOrder> stockOrderEntries = matchingEngineOrdersRepository.findAllByUsername(user.getUsername());
        assertEquals(1, stockOrderEntries.size());
        StockOrder stockOrderEntry = stockOrderEntries.get(0);
        StockOrder expectedOrderEntry = new StockOrder(null, 1, stock1.getId(), true, OrderType.LIMIT, 100, 80, OrderStatus.IN_PROGRESS, user.getUsername());
        assertStockTX(expectedOrderEntry, stockOrderEntry);

        // Check if wallet transaction entries was updated correctly
        List<WalletTX> walletTXEntries = walletTXRepository.findAllByUsername(user.getUsername());
        assertEquals(1, walletTXEntries.size());
        WalletTX walletTXEntry = walletTXEntries.get(0);
        WalletTX expectedWalletTXEntry = new WalletTX(stockOrderEntry.getWalletTXid(), user.getUsername(), stockOrderEntry.getStock_tx_id(), true, 100*80);
        assertWalletTX(expectedWalletTXEntry, walletTXEntry);

        // Stock should not have a portfolio entry on buy order creation
        PortfolioEntry portfolioEntry = portfolioRepository.findEntryByStockIdAndUsername(stock1.getId(), companyUser.getUsername());
        assertNull(portfolioEntry);
    }

    private void assertStockTX(StockOrder expected, StockOrder actual) {
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getParent_stock_tx_id(), actual.getParent_stock_tx_id());
        assertEquals(expected.getStockId(), actual.getStockId());
        assertEquals(expected.getIs_buy(), actual.getIs_buy());
        assertEquals(expected.getOrderType(), actual.getOrderType());
        assertEquals(expected.getQuantity(), actual.getQuantity());
        assertEquals(expected.getPrice(), actual.getPrice());
        assertEquals(expected.getOrderStatus(), actual.getOrderStatus());
        assertEquals(expected.getWalletTXid(), actual.getWalletTXid());
    }

    private void assertWalletTX(WalletTX expected, WalletTX actual) {
        assertEquals(expected.getWalletTXId(), actual.getWalletTXId());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getStockTXId(), actual.getStockTXId());
        assertEquals(expected.getIsDebit(), actual.getIsDebit());
        assertEquals(expected.getAmount(), actual.getAmount());
    }
}