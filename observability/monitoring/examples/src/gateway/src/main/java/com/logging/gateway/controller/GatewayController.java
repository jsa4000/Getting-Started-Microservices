package com.logging.gateway.controller;

import com.logging.gateway.model.Customer;
import io.micrometer.core.instrument.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/")
public class GatewayController {

    private final Logger logger = LoggerFactory.getLogger(GatewayController.class);

    @Value("${spring.application.name:'gateway-service'}")
    private String serviceName;

    private Random rand = new Random();
    private MeterRegistry registry;

    private String[] requestUris = {"get:/status","get:/customer","get:/order"};
    private HashMap<String,Counter> requestCounters = new HashMap<>();
    private HashMap<String,Counter> requestErrorCounters = new HashMap<>();
    private HashMap<String,Timer> requestTimers = new HashMap<>();
    private HashMap<String,Gauge> requestGauges = new HashMap<>();
    private HashMap<String,AtomicInteger> currentRequestCounter = new HashMap<>();

    private void createRequestCounters(String counterName, String counterDescription ) {
        Arrays.stream(requestUris).forEach(uri -> requestCounters.put(uri, Counter
                .builder(counterName).description(counterDescription)
                .tag("verb", uri.split(":")[0])
                .tag("uri",uri.split(":")[1])
                .register(registry)));
    }

    private void createErrorRequestCounters(String counterName, String counterDescription ) {
        Arrays.stream(requestUris).forEach(uri -> requestErrorCounters.put(uri, Counter
                .builder(counterName).description(counterDescription)
                .tag("verb", uri.split(":")[0])
                .tag("uri",uri.split(":")[1])
                .register(registry)));
    }

    private void createRequestTimers(String timerName, String timerDescription ) {
        Arrays.stream(requestUris).forEach(uri -> requestTimers.put(uri, Timer
                .builder(timerName).description(timerDescription)
                .tag("verb", uri.split(":")[0])
                .tag("uri",uri.split(":")[1])
                .register(registry)));
    }

    private void createRequestGauges(String gaugeName, String gaugeDescription ) {

        Arrays.stream(requestUris).forEach(uri -> currentRequestCounter.put(uri, new AtomicInteger()));
        Arrays.stream(requestUris).forEach(uri -> requestGauges.put(uri, Gauge
                .builder(gaugeName,currentRequestCounter.get(uri),AtomicInteger::get).description(gaugeDescription)
                .tag("verb", uri.split(":")[0])
                .tag("uri",uri.split(":")[1])
                .register(registry)));
    }

    @PostConstruct
    private void init()
    {
        // Get the global registry to create the metric using Micrometer
        registry = Metrics.globalRegistry;
        // Create generic Tags
        registry.config().commonTags("service", serviceName);
        // Create Counters for requests
        createRequestCounters("com.logging.request","Indicates the number of total requests performed by uri");
        // Create Counters for errors
        createErrorRequestCounters("com.logging.request.error","Indicates the number of total error in requests performed by uri");
        // Create Timers for requests
        createRequestTimers("com.logging.request.timing","Measure the latency per requests by uri");
        // Create Timers for requests
        createRequestGauges("com.logging.request.gauges","Get the current request being currently processed per uri");
    }

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        requestCounters.get("get:/status").increment(1.0);
        currentRequestCounter.get("get:/status").incrementAndGet();

        ResponseEntity<String> result = requestTimers.get("get:/status").record(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(rand.nextInt(250));
                String statusResult = String.format("Server %s is UP", this.getClass().getSimpleName());
                logger.info(statusResult);

                return ResponseEntity.ok(statusResult);
            }
            catch(Exception ex)
            {
                requestErrorCounters.get("get:/status").increment(1.0);
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });

        currentRequestCounter.get("get:/status").decrementAndGet();
        return result;
    }

    @GetMapping("/customer")
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<Customer> customer() {
        requestCounters.get("get:/customer").increment(1.0);
        currentRequestCounter.get("get:/customer").incrementAndGet();

        ResponseEntity<Customer> result = requestTimers.get("get:/customer").record(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(rand.nextInt(500));
                final Customer customer = new Customer(1,"Javier", "Perez",35);
                logger.info(String.format("Customer %s",customer.toString()));

                return ResponseEntity.ok(customer);
            }
            catch(Exception ex)
            {
                requestErrorCounters.get("get:/customer").increment(1.0);
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });

        currentRequestCounter.get("get:/customer").decrementAndGet();
        return result;
    }

    @GetMapping("/order")
    public ResponseEntity<String> order() throws Exception {
        requestCounters.get("get:/order").increment(1.0);
        currentRequestCounter.get("get:/order").incrementAndGet();

        ResponseEntity<String> result = requestTimers.get("get:/order").record(() -> {
            try {
                // Force random error using this API
                if (rand.nextInt(1000) % 2 == 0) {
                    TimeUnit.MILLISECONDS.sleep(rand.nextInt(5000));
                    throw new Exception("Order Exception");
                }

                String orderName = "order123";
                logger.info(String.format("Order %s",orderName));

                return ResponseEntity.ok(orderName);
            }
            catch(Exception ex)
            {
                requestErrorCounters.get("get:/order").increment(1.0);
                return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });

        currentRequestCounter.get("get:/order").decrementAndGet();
        return result;
    }
}