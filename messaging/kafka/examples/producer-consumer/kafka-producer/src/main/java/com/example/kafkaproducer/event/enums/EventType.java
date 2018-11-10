package com.example.kafkaproducer.event.enums;

import com.example.kafkaproducer.event.role.RoleCreated;
import com.example.kafkaproducer.event.role.RoleDeleted;
import com.example.kafkaproducer.event.role.RoleModified;
import com.example.kafkaproducer.event.user.UserCreated;
import com.example.kafkaproducer.event.user.UserDeleted;
import com.example.kafkaproducer.event.user.UserModified;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum EventType {
    ROLE_CREATED("ROLE_CREATED", "1.0.0", RoleCreated.class  ),
    ROLE_MODIFIED("ROLE_MODIFIED", "1.0.0", RoleModified.class  ),
    ROLE_DELETED("ROLE_DELETED", "1.0.0", RoleDeleted.class  ),
    USER_CREATED("USER_CREATED", "1.0.0", UserCreated.class  ),
    USER_MODIFIED("USER_MODIFIED", "1.0.0", UserModified.class  ),
    USER_DELETED("USER_DELETED", "1.0.0", UserDeleted.class  );

    // Pattern from Joshua Bloch, Effective Java
    private static final Map<String,EventType> ENUM_MAP;

    @Getter private String name;
    @Getter private String version;
    @Getter private Class eventClass;

    EventType(String name, String version, Class eventClass) {
        this.name = name;
        this.version = version;
        this.eventClass = eventClass;
    }

    static {
        Map<String,EventType> map = new ConcurrentHashMap<>();
        for (EventType instance : EventType.values()) map.put(instance.eventClass.getSimpleName(), instance);
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static EventType fromClass (Class instance) {
        return ENUM_MAP.get(instance.getSimpleName());
    }
}
