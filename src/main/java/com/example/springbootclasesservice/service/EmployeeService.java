package com.example.springbootclasesservice.service;

import com.example.springbootclasesservice.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    // RETRIEVE

    List<Employee> findEmployees();

    Optional<Employee> findOne(Long id);

    List<Employee> findByAgeGreater(Integer age);

    List<Employee> findByMarried(Boolean married);

    Optional<Employee> findByEmail(String email);

    // CREATE
    Employee createEmployee(Employee employee);


    // UPDATE
    Employee updateEmployee(Employee employee);

    // DELETE
    void deleteById(Long id);
    void deleteEmployees();

    // PROCESS
    Optional<Employee> processSalary(Long id);


}
