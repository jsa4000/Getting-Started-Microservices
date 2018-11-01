package com.example.devices.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ArrayUtils {

    public static <T> List<T[]> chunk(T[] input, int chunkSize) {
        return  IntStream.iterate(0, i -> i + chunkSize)
                .limit((long) Math.ceil((double) input.length / chunkSize))
                .mapToObj(j -> Arrays.copyOfRange(input, j, j + chunkSize > input.length ? input.length : j + chunkSize))
                .collect(Collectors.toList());
    }

}
