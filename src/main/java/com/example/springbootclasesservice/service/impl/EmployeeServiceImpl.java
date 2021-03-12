package com.example.springbootclasesservice.service.impl;

import com.example.springbootclasesservice.model.Employee;
import com.example.springbootclasesservice.repository.EmployeeRepository;
import com.example.springbootclasesservice.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private EmployeeRepository repository;

    public EmployeeServiceImpl(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Employee> findEmployees() {
        log.info("findEmployees");
        return repository.findAll();
    }

    @Override
    public Optional<Employee> findOne(Long id) {
        log.info("findOne");
        if (id == null)
            return Optional.empty();

        return repository.findById(id);
    }

    @Override
    public List<Employee> findByAgeGreater(Integer age) {
        log.info("findByAgeGreater");
        if (age == null ||  age < 0){
            log.warn("Filtering by wrong age");
            return new ArrayList<>();
        }

        return repository.findAllByAgeAfter(age);
    }

    @Override
    public List<Employee> findByMarried(Boolean married) {
        return null;
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Employee createEmployee(Employee employee) {
        return null;
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void deleteEmployees() {

    }

    @Override
    public Optional<Employee> processSalary(Long id) {
        log.info("Request to process salary for employee with id {}", id);
        Optional<Employee> employeeOptional = this.findOne(id);

        if(employeeOptional.isEmpty() || employeeOptional.get().getYearsInCompany() == null)
            return employeeOptional;

        Employee employee = employeeOptional.get();

        calculateSalary(employee);

        repository.save(employee);

        return Optional.of(employee);
    }

    /**
     * Calculo de salario
     * @param employee
     */
    private void calculateSalary(Employee employee) {
        if (employee.getYearsInCompany() <5){
            employee.setSalary(24000D);
        }else if(employee.getYearsInCompany() > 5 && employee.getYearsInCompany() < 20){
            employee.setSalary(40000D);
        }else{
            employee.setSalary(60000D);
        }
    }
}
