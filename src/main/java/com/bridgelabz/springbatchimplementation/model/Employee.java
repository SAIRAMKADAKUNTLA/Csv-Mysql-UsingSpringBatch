package com.bridgelabz.springbatchimplementation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private Long empId;
    private String namePrefix;
    private String firstName;
    private String lastName;
    private String gender;

}
