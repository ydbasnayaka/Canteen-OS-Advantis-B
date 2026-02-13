/**
 * Security Desk JavaScript
 * Token Issuance Functionality
 */

let currentEmployee = null;
let currentToken = null;

// Search Employee by ID
async function searchEmployee() {
    const employeeId = document.getElementById('employeeIdInput').value.trim();
    
    if (!employeeId) {
        showToast('Please enter an Employee ID', 'error');
        return;
    }
    
    try {
        const response = await fetchAPI(`${API_BASE_URL}/employees/emp-id/${employeeId}`);
        
        if (response.success && response.data) {
            displayEmployeeDetails(response.data);
        } else {
            showToast('Employee not found', 'error');
            hideEmployeeDetails();
        }
    } catch (error) {
        showToast(error.message || 'Error searching employee', 'error');
        hideEmployeeDetails();
    }
}

// Search Employee by Name
async function searchByName() {
    const name = document.getElementById('employeeNameInput').value.trim();
    
    if (!name) {
        showToast('Please enter a name to search', 'error');
        return;
    }
    
    try {
        const response = await fetchAPI(`${API_BASE_URL}/employees/search?query=${encodeURIComponent(name)}`);
        
        if (response.success && response.data && response.data.length > 0) {
            displaySearchResults(response.data);
        } else {
            showToast('No employees found', 'error');
            document.getElementById('searchResults').style.display = 'none';
        }
    } catch (error) {
        showToast(error.message || 'Error searching employees', 'error');
    }
}

// Display Search Results
function displaySearchResults(employees) {
    const resultsDiv = document.getElementById('searchResults');
    const listDiv = document.getElementById('resultsList');
    
    listDiv.innerHTML = employees.map(emp => `
        <div class="result-item" onclick="selectEmployee('${emp.employeeId}')">
            <div class="result-photo">
                <img src="${emp.photoUrl || '/images/default-avatar.svg'}" alt="${emp.fullName}">
            </div>
            <div class="result-info">
                <h4>${emp.fullName}</h4>
                <p>${emp.employeeId} | ${emp.department || 'N/A'}</p>
            </div>
            <i class="fas fa-chevron-right"></i>
        </div>
    `).join('');
    
    resultsDiv.style.display = 'block';
}

// Select Employee from Search Results
async function selectEmployee(employeeId) {
    document.getElementById('employeeIdInput').value = employeeId;
    document.getElementById('searchResults').style.display = 'none';
    await searchEmployee();
}

// Display Employee Details
function displayEmployeeDetails(employee) {
    currentEmployee = employee;
    
    document.getElementById('empPhoto').src = employee.photoUrl || '/images/default-avatar.svg';
    document.getElementById('empName').textContent = employee.fullName;
    document.getElementById('empId').textContent = employee.employeeId;
    document.getElementById('empDept').textContent = employee.department || 'N/A';
    document.getElementById('empDesignation').textContent = employee.designation || 'N/A';
    document.getElementById('empEmail').textContent = employee.email || 'N/A';
    document.getElementById('empPhone').textContent = employee.phone || 'N/A';
    
    const statusBadge = document.getElementById('empStatus');
    statusBadge.textContent = employee.isActive ? 'Active' : 'Inactive';
    statusBadge.className = `status-badge ${employee.isActive ? 'active' : 'inactive'}`;
    
    document.getElementById('employeeDetails').style.display = 'block';
    
    if (employee.isActive) {
        document.getElementById('tokenSection').style.display = 'block';
    } else {
        document.getElementById('tokenSection').style.display = 'none';
        showToast('Employee is inactive. Cannot issue token.', 'error');
    }
}

// Hide Employee Details
function hideEmployeeDetails() {
    document.getElementById('employeeDetails').style.display = 'none';
    document.getElementById('tokenSection').style.display = 'none';
    document.getElementById('tokenDisplay').style.display = 'none';
    currentEmployee = null;
    currentToken = null;
}

// Issue Token
async function issueToken() {
    if (!currentEmployee) {
        showToast('Please search for an employee first', 'error');
        return;
    }
    
    const mealType = document.querySelector('input[name="mealType"]:checked').value;
    const issuedBy = document.getElementById('issuedBy').value || 'Security Desk';
    
    try {
        const response = await fetchAPI(`${API_BASE_URL}/tokens/issue`, {
            method: 'POST',
            body: JSON.stringify({
                employeeId: currentEmployee.employeeId,
                mealType: mealType,
                issuedBy: issuedBy
            })
        });
        
        if (response.success && response.data) {
            currentToken = response.data;
            displayToken(response.data);
            showToast('Token issued successfully!', 'success');
            loadTodayTokens();
        } else {
            showToast(response.message || 'Failed to issue token', 'error');
        }
    } catch (error) {
        showToast(error.message || 'Error issuing token', 'error');
    }
}

// Display Issued Token
function displayToken(token) {
    document.getElementById('tokenNumber').textContent = token.tokenNumber;
    document.getElementById('tokenEmpName').textContent = token.employeeName || currentEmployee.fullName;
    document.getElementById('tokenEmpId').textContent = token.employeeId;
    document.getElementById('tokenDept').textContent = token.employeeDepartment || currentEmployee.department || 'N/A';
    document.getElementById('tokenDate').textContent = formatDate(token.issueDate);
    document.getElementById('tokenTime').textContent = formatTime(token.issueTime);
    document.getElementById('tokenMealType').textContent = token.mealType;
    
    if (token.qrCodeBase64) {
        document.getElementById('tokenQR').src = `data:image/png;base64,${token.qrCodeBase64}`;
    }
    
    document.getElementById('tokenDisplay').style.display = 'block';
    document.getElementById('tokenSection').style.display = 'none';
    
    // Scroll to token display
    document.getElementById('tokenDisplay').scrollIntoView({ behavior: 'smooth' });
}

// Print Token
function printToken() {
    if (!currentToken) return;
    
    const printContent = `
        <div class="token-print">
            <h2>MEAL TOKEN</h2>
            <h3>${currentToken.mealType}</h3>
            <img src="data:image/png;base64,${currentToken.qrCodeBase64}" alt="QR Code">
            <div class="token-info">
                <p><strong>Token:</strong> ${currentToken.tokenNumber}</p>
                <p><strong>Employee:</strong> ${currentToken.employeeName || currentEmployee.fullName}</p>
                <p><strong>ID:</strong> ${currentToken.employeeId}</p>
                <p><strong>Date:</strong> ${formatDate(currentToken.issueDate)}</p>
                <p><strong>Time:</strong> ${formatTime(currentToken.issueTime)}</p>
            </div>
        </div>
    `;
    
    window.printContent(printContent);
}

// Reset Form
function resetForm() {
    document.getElementById('employeeIdInput').value = '';
    document.getElementById('employeeNameInput').value = '';
    hideEmployeeDetails();
    document.getElementById('tokenDisplay').style.display = 'none';
    document.getElementById('searchResults').style.display = 'none';
}

// Load Today's Tokens
async function loadTodayTokens() {
    try {
        const response = await fetchAPI(`${API_BASE_URL}/tokens/today`);
        
        if (response.success && response.data) {
            displayTodayTokens(response.data);
        }
    } catch (error) {
        console.error('Error loading today\'s tokens:', error);
    }
}

// Display Today's Tokens
function displayTodayTokens(tokens) {
    const tbody = document.getElementById('todayTokensTable');
    
    if (tokens.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">No tokens issued today</td></tr>';
        return;
    }
    
    tbody.innerHTML = tokens.map(token => `
        <tr>
            <td><strong>${token.tokenNumber}</strong></td>
            <td>${token.employeeName || token.employeeId}</td>
            <td><i class="fas fa-${getMealIcon(token.mealType)}"></i> ${token.mealType}</td>
            <td>${formatTime(token.issueTime)}</td>
            <td>${getStatusBadge(token.status)}</td>
            <td>
                <button class="btn btn-sm btn-secondary" onclick="viewTokenDetails('${token.tokenNumber}')">
                    <i class="fas fa-eye"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

// View Token Details
async function viewTokenDetails(tokenNumber) {
    try {
        const response = await fetchAPI(`${API_BASE_URL}/tokens/${tokenNumber}`);
        
        if (response.success && response.data) {
            // Could open a modal with token details
            showToast(`Token ${tokenNumber} - Status: ${response.data.status}`, 'info');
        }
    } catch (error) {
        showToast('Error loading token details', 'error');
    }
}

// Handle Enter Key on Employee ID Input
document.addEventListener('DOMContentLoaded', function() {
    const employeeIdInput = document.getElementById('employeeIdInput');
    if (employeeIdInput) {
        employeeIdInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                searchEmployee();
            }
        });
    }
    
    const employeeNameInput = document.getElementById('employeeNameInput');
    if (employeeNameInput) {
        employeeNameInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                searchByName();
            }
        });
    }
    
    // Load today's tokens on page load
    loadTodayTokens();
});
