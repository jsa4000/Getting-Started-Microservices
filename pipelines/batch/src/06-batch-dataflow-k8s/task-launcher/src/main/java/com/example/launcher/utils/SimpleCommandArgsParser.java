package com.example.launcher.utils;

import java.util.HashMap;

public class SimpleCommandArgsParser {

    public static HashMap<String, String> parse(String[] args)
    {
        HashMap<String, String> commands = new HashMap<>();
        for ( int i = 0; i < args.length; i++ ) {
            String[] command = args[i].split("=", 2);
            if (command.length > 1) {
                commands.put(command[0].trim(), command[1].trim());
            }
        }
        return commands;
    }

}
