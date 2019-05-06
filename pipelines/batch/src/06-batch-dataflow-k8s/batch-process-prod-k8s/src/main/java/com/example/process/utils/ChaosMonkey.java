package com.example.process.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class ChaosMonkey {

    private static Random rand = new Random();

    /**
     * Performs a Chaos Monkey action
     *
     * @param failurePercentage
     * @throws Exception
     */
    public static void check(String title, int failurePercentage) throws Exception {
        log.info("Failure chance [" + title + "]: " + failurePercentage);
        if (failurePercentage <= 0) {
            return;
        }
        int chance = rand.nextInt(100);
        log.info("Failure random " + chance + " <= " + failurePercentage);
        if (chance <= failurePercentage) {
            log.info("Chaos Monkey!! [" + title + "]");
            throw new Exception("Failure percentage: " + failurePercentage);
        }
    }
}
