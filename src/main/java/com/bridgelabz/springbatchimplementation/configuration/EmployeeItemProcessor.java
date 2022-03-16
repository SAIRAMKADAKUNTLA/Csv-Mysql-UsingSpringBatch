package com.bridgelabz.springbatchimplementation.configuration;

import com.bridgelabz.springbatchimplementation.model.Employee;
import org.springframework.batch.item.ItemProcessor;

public class EmployeeItemProcessor implements ItemProcessor<Employee,Employee> {
    @Override
    public Employee process(Employee employee) throws Exception {
        return employee;
    }
}
