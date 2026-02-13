/**
 * Employee Management JavaScript
 * CRUD Operations for Employees
 */

let employees = [];
let editingEmployeeId = null;
let deletingEmployeeId = null;

// Load All Employees
async function loadEmployees() {
    try {
        const response = await fetchAPI(`${API_BASE_URL}/employees`);
        
        if (response.success && response.data) {
            employees = response.data;
            displayEmployees(employees);
            updateStats(employees);
        }
    } catch (error) {
        showToast('Error loading employees', 'error');
    }
}

// Display Employees in Table
function displayEmployees(employeeList) {
    const tbody = document.getElementById('employeeTableBody');
    
    if (employeeList.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center">No employees found</td></tr>';
        return;
    }
    
    tbody.innerHTML = employeeList.map(emp => `
        <tr>
            <td>
                <img src="${emp.photoUrl || '/images/default-avatar.png'}" 
                     alt="${emp.fullName}" 
                     class="table-avatar"
                     onerror="this.src='/images/default-avatar.svg'">
            </td>
            <td><strong>${emp.employeeId}</strong></td>
            <td>${emp.fullName}</td>
            <td>${emp.department || '-'}</td>
            <td>${emp.designation || '-'}</td>
            <td>
                ${emp.phone ? `<small><i class="fas fa-phone"></i> ${emp.phone}</small>` : '-'}
            </td>
            <td>${getStatusBadge(emp.isActive ? 'Active' : 'Inactive')}</td>
            <td>
                <div class="action-buttons">
                    <button class="btn btn-sm btn-info" onclick="viewEmployee(${emp.id})" title="View">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-primary" onclick="editEmployee(${emp.id})" title="Edit">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="confirmDelete(${emp.id})" title="Delete">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Update Statistics
function updateStats(employeeList) {
    const total = employeeList.length;
    const active = employeeList.filter(e => e.isActive).length;
    const inactive = total - active;
    
    document.getElementById('totalEmployees').textContent = total;
    document.getElementById('activeEmployees').textContent = active;
    document.getElementById('inactiveEmployees').textContent = inactive;
}

// Search Employees
function searchEmployees() {
    const searchTerm = document.getElementById('searchEmployee').value.toLowerCase();
    
    if (!searchTerm) {
        displayEmployees(employees);
        return;
    }
    
    const filtered = employees.filter(emp => 
        emp.fullName.toLowerCase().includes(searchTerm) ||
        emp.employeeId.toLowerCase().includes(searchTerm) ||
        (emp.department && emp.department.toLowerCase().includes(searchTerm)) ||
        (emp.email && emp.email.toLowerCase().includes(searchTerm))
    );
    
    displayEmployees(filtered);
}

// Open Add Modal
function openAddModal() {
    editingEmployeeId = null;
    document.getElementById('modalTitle').innerHTML = '<i class="fas fa-user-plus"></i> Add Employee';
    document.getElementById('employeeForm').reset();
    document.getElementById('empIdHidden').value = '';
    document.getElementById('empActive').checked = true;
    document.getElementById('employeeModal').style.display = 'flex';
}

// Edit Employee
function editEmployee(id) {
    const employee = employees.find(e => e.id === id);
    if (!employee) return;
    
    editingEmployeeId = id;
    document.getElementById('modalTitle').innerHTML = '<i class="fas fa-edit"></i> Edit Employee';
    
    document.getElementById('empIdHidden').value = employee.id;
    document.getElementById('empId').value = employee.employeeId;
    document.getElementById('empName').value = employee.fullName;
    document.getElementById('empEmail').value = employee.email || '';
    document.getElementById('empPhone').value = employee.phone || '';
    document.getElementById('empDept').value = employee.department || '';
    document.getElementById('empDesignation').value = employee.designation || '';
    document.getElementById('empPhoto').value = employee.photoUrl || '';
    document.getElementById('empActive').checked = employee.isActive;
    
    document.getElementById('employeeModal').style.display = 'flex';
}

// View Employee
function viewEmployee(id) {
    const employee = employees.find(e => e.id === id);
    if (!employee) return;
    
    document.getElementById('viewEmpPhoto').src = employee.photoUrl || '/images/default-avatar.svg';
    document.getElementById('viewEmpId').textContent = employee.employeeId;
    document.getElementById('viewEmpName').textContent = employee.fullName;
    document.getElementById('viewEmpEmail').textContent = employee.email || '-';
    document.getElementById('viewEmpPhone').textContent = employee.phone || '-';
    document.getElementById('viewEmpDept').textContent = employee.department || '-';
    document.getElementById('viewEmpDesignation').textContent = employee.designation || '-';
    document.getElementById('viewEmpStatus').innerHTML = getStatusBadge(employee.isActive ? 'Active' : 'Inactive');
    document.getElementById('viewEmpCreated').textContent = formatDateTime(employee.createdAt);
    
    document.getElementById('viewEditBtn').onclick = function() {
        closeViewModal();
        editEmployee(id);
    };
    
    document.getElementById('viewEmployeeModal').style.display = 'flex';
}

// Save Employee (Create or Update)
async function saveEmployee(event) {
    event.preventDefault();
    
    const employeeData = {
        employeeId: document.getElementById('empId').value.trim(),
        fullName: document.getElementById('empName').value.trim(),
        email: document.getElementById('empEmail').value.trim(),
        phone: document.getElementById('empPhone').value.trim(),
        department: document.getElementById('empDept').value,
        designation: document.getElementById('empDesignation').value.trim(),
        photoUrl: document.getElementById('empPhoto').value.trim(),
        isActive: document.getElementById('empActive').checked
    };
    
    // Validation
    if (!employeeData.employeeId || !employeeData.fullName) {
        showToast('Employee ID and Full Name are required', 'error');
        return;
    }
    
    try {
        let response;
        
        if (editingEmployeeId) {
            // Update existing
            response = await fetchAPI(`${API_BASE_URL}/employees/${editingEmployeeId}`, {
                method: 'PUT',
                body: JSON.stringify(employeeData)
            });
        } else {
            // Create new
            response = await fetchAPI(`${API_BASE_URL}/employees`, {
                method: 'POST',
                body: JSON.stringify(employeeData)
            });
        }
        
        if (response.success) {
            showToast(editingEmployeeId ? 'Employee updated successfully' : 'Employee created successfully', 'success');
            closeModal();
            loadEmployees();
        } else {
            showToast(response.message || 'Operation failed', 'error');
        }
    } catch (error) {
        showToast(error.message || 'Error saving employee', 'error');
    }
}

// Confirm Delete
function confirmDelete(id) {
    deletingEmployeeId = id;
    document.getElementById('deleteModal').style.display = 'flex';
}

// Delete Employee
async function deleteEmployee() {
    if (!deletingEmployeeId) return;
    
    try {
        const response = await fetchAPI(`${API_BASE_URL}/employees/${deletingEmployeeId}`, {
            method: 'DELETE'
        });
        
        if (response.success) {
            showToast('Employee deleted successfully', 'success');
            closeDeleteModal();
            loadEmployees();
        } else {
            showToast(response.message || 'Delete failed', 'error');
        }
    } catch (error) {
        showToast(error.message || 'Error deleting employee', 'error');
    }
}

// Close Modals
function closeModal() {
    document.getElementById('employeeModal').style.display = 'none';
    editingEmployeeId = null;
}

function closeViewModal() {
    document.getElementById('viewEmployeeModal').style.display = 'none';
}

function closeDeleteModal() {
    document.getElementById('deleteModal').style.display = 'none';
    deletingEmployeeId = null;
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
};

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    loadEmployees();
    
    // Form submission
    const form = document.getElementById('employeeForm');
    if (form) {
        form.addEventListener('submit', saveEmployee);
    }
    
    // Delete confirmation
    const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
    if (confirmDeleteBtn) {
        confirmDeleteBtn.addEventListener('click', deleteEmployee);
    }
});
