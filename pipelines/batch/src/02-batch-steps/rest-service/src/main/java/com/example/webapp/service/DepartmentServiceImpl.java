package com.example.webapp.service;

import com.example.webapp.model.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentServiceImpl implements DepartmentService {

    public enum DepartmentType {
        Unknown(0, "Unknown"),
        Production(1, "Production"),
        Purchasing(2, "Purchasing"),
        Marketing(3, "Marketing"),
        HumanResourceManagement(4, "Human Resource Management"),
        AccountingAndFinance(5, "Accounting and Finance");

        private final int value;
        private final String name;

        DepartmentType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        public static DepartmentType getById(int id) {
            for(DepartmentType e : values()) {
                if(e.getValue() == id) return e;
            }
            return Unknown;
        }
    }

    public Department getById(int id) {
        final DepartmentType type = DepartmentType.getById(id);
        return new Department(type.getValue(), type.getName());
    }
    
}
