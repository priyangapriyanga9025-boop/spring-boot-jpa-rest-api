package com.rabtech.api.service;

import com.rabtech.api.dto.EmployeeRequest;
import com.rabtech.api.entity.Department;
import com.rabtech.api.entity.Employee;
import com.rabtech.api.exception.ResourceNotFoundException;
import com.rabtech.api.repository.DepartmentRepository;
import com.rabtech.api.repository.EmployeeRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    public Employee createEmployee(EmployeeRequest request) {
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId()));

        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setRole(request.getRole());
        employee.setSalary(request.getSalary());
        employee.setDepartment(department);

        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, EmployeeRequest request) {
        Employee existingEmployee = getEmployeeById(id);
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId()));

        existingEmployee.setName(request.getName());
        existingEmployee.setEmail(request.getEmail());
        existingEmployee.setRole(request.getRole());
        existingEmployee.setSalary(request.getSalary());
        existingEmployee.setDepartment(department);

        return employeeRepository.save(existingEmployee);
    }

    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);
    }

    public List<Employee> getEmployeesByRole(String role) {
        return employeeRepository.findByRole(role);
    }

    public List<Employee> getEmployeesByDepartment(String departmentName) {
        return employeeRepository.findByDepartmentName(departmentName);
    }

    public List<Employee> getEmployeesAboveSalary(Double salary) {
        return employeeRepository.findEmployeesWithSalaryGreaterThan(salary);
    }
}
