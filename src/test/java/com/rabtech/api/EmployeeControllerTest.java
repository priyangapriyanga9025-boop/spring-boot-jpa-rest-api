package com.rabtech.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabtech.api.dto.DepartmentRequest;
import com.rabtech.api.dto.EmployeeRequest;
import com.rabtech.api.entity.Department;
import com.rabtech.api.entity.Employee;
import com.rabtech.api.repository.DepartmentRepository;
import com.rabtech.api.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@WithMockUser(username = "testuser", roles = "USER")
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    @Test
    void applicationContextLoads() {
        assertThat(true).isTrue();
    }

    @Test
    void getAllEmployeesEndpoint() throws Exception {
        Department department = departmentRepository.save(new Department("Engineering"));
        employeeRepository.save(new Employee("Alice", "alice@example.com", "Developer", 50000.0, department));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice"));
    }

    @Test
    void getEmployeeById() throws Exception {
        Department department = departmentRepository.save(new Department("Engineering"));
        Employee employee = employeeRepository.save(new Employee("Bob", "bob@example.com", "Tester", 60000.0, department));

        mockMvc.perform(get("/api/employees/{id}", employee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("bob@example.com"));
    }

    @Test
    void postEmployee() throws Exception {
        Department department = departmentRepository.save(new Department("Engineering"));
        EmployeeRequest request = new EmployeeRequest();
        request.setName("Charlie");
        request.setEmail("charlie@example.com");
        request.setRole("Developer");
        request.setSalary(70000.0);
        request.setDepartmentId(department.getId());

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Charlie"));
    }

    @Test
    void putEmployee() throws Exception {
        Department department = departmentRepository.save(new Department("Engineering"));
        Employee employee = employeeRepository.save(new Employee("Dana", "dana@example.com", "Developer", 55000.0, department));

        EmployeeRequest request = new EmployeeRequest();
        request.setName("Dana Updated");
        request.setEmail("dana.updated@example.com");
        request.setRole("Lead Developer");
        request.setSalary(80000.0);
        request.setDepartmentId(department.getId());

        mockMvc.perform(put("/api/employees/{id}", employee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("Lead Developer"));
    }

    @Test
    void deleteEmployee() throws Exception {
        Department department = departmentRepository.save(new Department("Engineering"));
        Employee employee = employeeRepository.save(new Employee("Eve", "eve@example.com", "Analyst", 45000.0, department));

        mockMvc.perform(delete("/api/employees/{id}", employee.getId()))
                .andExpect(status().isNoContent());

        assertThat(employeeRepository.findById(employee.getId())).isEmpty();
    }

    @Test
    void validationFailure() throws Exception {
        Department department = departmentRepository.save(new Department("Engineering"));
        EmployeeRequest request = new EmployeeRequest();
        request.setName("");
        request.setEmail("not-an-email");
        request.setRole("Developer");
        request.setSalary(-5.0);
        request.setDepartmentId(department.getId());

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void employeeNotFound() throws Exception {
        mockMvc.perform(get("/api/employees/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void customRepositoryQuery() {
        Department department = departmentRepository.save(new Department("Engineering"));
        employeeRepository.save(new Employee("Frank", "frank@example.com", "Developer", 90000.0, department));
        employeeRepository.save(new Employee("Grace", "grace@example.com", "Manager", 100000.0, department));

        assertThat(employeeRepository.findByRole("Developer")).hasSize(1);
        assertThat(employeeRepository.findEmployeesWithSalaryGreaterThan(95000.0)).hasSize(1);
    }

    @Test
    void departmentRelationship() {
        Department department = departmentRepository.save(new Department("Engineering"));
        employeeRepository.save(new Employee("Henry", "henry@example.com", "Developer", 65000.0, department));

        assertThat(employeeRepository.findByDepartmentName("Engineering")).hasSize(1);
        assertThat(employeeRepository.findByDepartmentName("Engineering").get(0).getDepartment().getName())
                .isEqualTo("Engineering");
    }

    @Test
    void departmentCrud() throws Exception {
        DepartmentRequest request = new DepartmentRequest();
        request.setName("Operations");

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Operations"));

        Department department = departmentRepository.findAll().get(0);
        mockMvc.perform(get("/api/departments/{id}", department.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Operations"));
    }
}
