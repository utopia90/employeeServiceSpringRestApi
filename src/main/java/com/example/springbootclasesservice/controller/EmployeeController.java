package com.example.springbootclasesservice.controller;

import com.example.springbootclasesservice.model.Employee;
import com.example.springbootclasesservice.repository.EmployeeRepository;
import com.example.springbootclasesservice.service.EmployeeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController // Define que esto es un controlador REST
// @Controller // Define que es un controlador MVC
@RequestMapping("/api") // Prefijo api para todos los resources o endpoints
public class EmployeeController {

    /*

        @GetMapping - recuperar
        @PostMapping - crear
        @PutMapping - actualizar
        @DeleteMapping - borrar

     */
    // atributos
    private final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }


    /**
     * RETRIEVE ALL
     * It return all employees
     * @return List of employees from database
     */
    @GetMapping("/employees") // GET - recupera informacion - recuperar todos los empleados
    @ApiOperation("Encuentra todos los empleados sin filtro ni paginación")
    public List<Employee> findEmployees(){
        log.debug("REST request to find all Employees");
        return employeeService.findEmployees();
    }

    /**
     * RETRIEVE ONE
     * @param id
     * @return
     */
    @GetMapping("/employees/{id}")  // GET - recupera informacion - recuperar un empleado
    @ApiOperation("Encuentra un empleado por su id")
    public ResponseEntity<Employee> findOne(@ApiParam("Clave primaria del empleado en formato Long") @PathVariable Long id){
        log.info("REST request to find one employee by id: {}", id);
        Optional<Employee> employeeOpt = employeeService.findOne(id);
        return employeeOpt.map(employee -> ResponseEntity.ok().body(employee))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * RETRIEVE BY PROPERTY - unique
     * @param email
     * @return
     */
    @GetMapping("/employees/email/{email}")
    @ApiOperation("Encuentra un empleado por su id")
    public ResponseEntity<Employee> filtrarPorEmail(@ApiParam("Correo electrónico en formato cadena de texto") @PathVariable String email){
        log.info("REST request to find one employee by email: {}", email);
        Optional<Employee> employeeOptional = employeeService.findByEmail(email);
        return employeeOptional.map(
                employee -> ResponseEntity.ok().body(employee)).orElseGet(
                        () -> ResponseEntity.notFound().build());

    }

    /**
     * RETRIEVE BY PROPERTY - multiple results
     * @param married
     * @return
     */
    @GetMapping("/employees/married/{married}")
    @ApiOperation("Filtra todos por estado matrimonial")
    public ResponseEntity<List<Employee>> filterByMarried(@ApiParam("Boolean que representa si está casado o no") @PathVariable Boolean married){
        log.debug("Filter all employees by married status: {}", married);

        List<Employee> employees = employeeService.findByMarried(married);

        if (employees.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return ResponseEntity.ok().body(employees);
    }

    // FILTRAR POR AGE
    @GetMapping("/employees/age-greater/{age}")
    @ApiOperation("Filtra todos por edad mayor que")
    public ResponseEntity<List<Employee>> filterByAgeGreater(@PathVariable Integer age){
        log.debug("REST request to filter employees by age: {}", age);

        List<Employee> employees = employeeService.findByAgeGreater(age);
        if (employees.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return ResponseEntity.ok().body(employees);
    }



    // CALCULAR SALARIO
    /*

        1 - Recibir id del empleado sobre el que calcular el salario
        2 - Calculo salario
            if yearsInCompany > 10
                setSalary
        3 - persistir empleado con el nuevo salario en base de datos
        4 - devolver empleado con el salario calculado
     */
    @GetMapping("/employees/calculate-salary/{id}")
    public ResponseEntity<Employee> calculateSalary(@PathVariable Long id){
        return employeeService.processSalary(id).map(
                employee -> ResponseEntity.ok().body(employee))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }



    /**
     * CREATE NEW
     * @param employee
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/employees")  // POST (CREAR) - recibe informacion - crear un nuevo empleado
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) throws URISyntaxException {
        log.debug("REST request to save an Employee: {} ", employee);
        if (employee.getId() != null) // != null means there is an employee in database
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Employee employeeDB = employeeService.createEmployee(employee);
        return ResponseEntity
                .created(new URI("/api/employees/" + employeeDB.getId()))
                .body(employeeDB);
    }

    // UPDATE ONE

    /**
     * It updates one employee
     * @param employee Employee to update
     * @return Updated employee
     */
    @PutMapping("/employees") // PUT (ACTUALIZAR) - recibe informacion - actualiza un empleado existente
    public ResponseEntity<Employee> updateEmployee(@RequestBody Employee employee){
        log.debug("REST request to update an Employee: {}", employee);

        if (employee.getId() == null){ // == null means want to create a new employee
            log.warn("Updating employee without id");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Employee employeeDB = repository.save(employee);
        // return ResponseEntity.ok().body(employeeDB);
        return ResponseEntity.ok().body(employeeService.updateEmployee(employee));
    }

    // DELETE ONE

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id){
        log.debug("REST request to delete an employee by id {}", id);

        return employeeService.deleteById(id);
    }

    // DELETE ALL
    //@GetMapping("/employees/delete-all")
    // @ApiIgnore
    @DeleteMapping("/employees")
    public ResponseEntity<Void> deleteEmployees(){
       return employeeService.deleteEmployees();
    }



}
