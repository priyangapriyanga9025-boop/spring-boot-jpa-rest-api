package com.rabtech.api.service;

import com.rabtech.api.dto.DepartmentRequest;
import com.rabtech.api.entity.Department;
import com.rabtech.api.exception.ResourceNotFoundException;
import com.rabtech.api.repository.DepartmentRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
    }

    public Department createDepartment(DepartmentRequest request) {
        Department department = new Department();
        department.setName(request.getName());
        return departmentRepository.save(department);
    }

    public Department updateDepartment(Long id, DepartmentRequest request) {
        Department existingDepartment = getDepartmentById(id);
        existingDepartment.setName(request.getName());
        return departmentRepository.save(existingDepartment);
    }

    public void deleteDepartment(Long id) {
        Department department = getDepartmentById(id);
        departmentRepository.delete(department);
    }
}
