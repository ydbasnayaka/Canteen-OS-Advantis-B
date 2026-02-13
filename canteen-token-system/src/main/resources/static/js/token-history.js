/**
 * Token History JavaScript
 * View and filter token history
 */

let allTokens = [];
let filteredTokens = [];
let currentPage = 1;
const itemsPerPage = 20;

// Load Token History
async function loadTokenHistory() {
    try {
        const response = await fetchAPI(`${API_BASE_URL}/tokens/today`);
        
        if (response.success && response.data) {
            allTokens = response.data;
            filteredTokens = [...allTokens];
            displayHistory();
            updateSummary();
        }
    } catch (error) {
        console.error('Error loading token history:', error);
        showToast('Error loading token history', 'error');
    }
}

// Display History with Pagination
function displayHistory() {
    const tbody = document.getElementById('historyTableBody');
    
    if (filteredTokens.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center">No tokens found</td></tr>';
        updatePagination();
        return;
    }
    
    const start = (currentPage - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    const pageTokens = filteredTokens.slice(start, end);
    
    tbody.innerHTML = pageTokens.map(token => `
        <tr>
            <td><strong>${token.tokenNumber}</strong></td>
            <td>${token.employeeName || token.employeeId}</td>
            <td><i class="fas fa-${getMealIcon(token.mealType)}"></i> ${token.mealType}</td>
            <td>${formatDate(token.issueDate)}</td>
            <td>${formatTime(token.issueTime)}</td>
            <td>${getStatusBadge(token.status)}</td>
            <td>${token.redeemTime ? formatTime(token.redeemTime) : '-'}</td>
            <td>
                <button class="btn btn-sm btn-info" onclick="viewTokenDetail('${token.tokenNumber}')">
                    <i class="fas fa-eye"></i> View
                </button>
            </td>
        </tr>
    `).join('');
    
    updatePagination();
}

// Update Summary Cards
function updateSummary() {
    const total = filteredTokens.length;
    const issued = filteredTokens.filter(t => t.status === 'ISSUED').length;
    const redeemed = filteredTokens.filter(t => t.status === 'REDEEMED').length;
    const pending = issued - redeemed;
    
    document.getElementById('summaryTotal').textContent = total;
    document.getElementById('summaryIssued').textContent = issued;
    document.getElementById('summaryRedeemed').textContent = redeemed;
    document.getElementById('summaryPending').textContent = pending;
}

// Update Pagination
function updatePagination() {
    const totalPages = Math.ceil(filteredTokens.length / itemsPerPage);
    
    document.getElementById('pageInfo').textContent = `Page ${currentPage} of ${totalPages || 1}`;
    document.getElementById('prevBtn').disabled = currentPage <= 1;
    document.getElementById('nextBtn').disabled = currentPage >= totalPages;
}

// Previous Page
function prevPage() {
    if (currentPage > 1) {
        currentPage--;
        displayHistory();
    }
}

// Next Page
function nextPage() {
    const totalPages = Math.ceil(filteredTokens.length / itemsPerPage);
    if (currentPage < totalPages) {
        currentPage++;
        displayHistory();
    }
}

// Apply Filters
function applyFilters() {
    const date = document.getElementById('filterDate').value;
    const mealType = document.getElementById('filterMealType').value;
    const status = document.getElementById('filterStatus').value;
    const search = document.getElementById('filterSearch').value.toLowerCase();
    
    filteredTokens = allTokens.filter(token => {
        let match = true;
        
        if (mealType && token.mealType !== mealType) {
            match = false;
        }
        
        if (status && token.status !== status) {
            match = false;
        }
        
        if (search) {
            const searchMatch = 
                token.tokenNumber.toLowerCase().includes(search) ||
                token.employeeId.toLowerCase().includes(search) ||
                (token.employeeName && token.employeeName.toLowerCase().includes(search));
            if (!searchMatch) match = false;
        }
        
        return match;
    });
    
    currentPage = 1;
    displayHistory();
    updateSummary();
}

// Reset Filters
function resetFilters() {
    document.getElementById('filterDate').value = '';
    document.getElementById('filterMealType').value = '';
    document.getElementById('filterStatus').value = '';
    document.getElementById('filterSearch').value = '';
    
    filteredTokens = [...allTokens];
    currentPage = 1;
    displayHistory();
    updateSummary();
}

// View Token Detail
async function viewTokenDetail(tokenNumber) {
    try {
        const response = await fetchAPI(`${API_BASE_URL}/tokens/${tokenNumber}`);
        
        if (response.success && response.data) {
            const token = response.data;
            
            document.getElementById('modalTokenNumber').textContent = token.tokenNumber;
            document.getElementById('modalEmpId').textContent = token.employeeId;
            document.getElementById('modalEmpName').textContent = token.employeeName || '-';
            document.getElementById('modalDept').textContent = token.employeeDepartment || '-';
            document.getElementById('modalMealType').textContent = token.mealType;
            document.getElementById('modalIssueDate').textContent = formatDate(token.issueDate);
            document.getElementById('modalIssueTime').textContent = formatTime(token.issueTime);
            document.getElementById('modalIssuedBy').textContent = token.issuedBy || '-';
            document.getElementById('modalRedeemTime').textContent = token.redeemTime ? formatDateTime(token.redeemTime) : '-';
            document.getElementById('modalRedeemedBy').textContent = token.redeemedBy || '-';
            document.getElementById('modalQRData').textContent = token.qrCodeData;
            document.getElementById('modalRemarks').textContent = token.remarks || '-';
            
            const statusHeader = document.getElementById('modalTokenStatus');
            statusHeader.innerHTML = getStatusBadge(token.status);
            
            document.getElementById('tokenDetailModal').style.display = 'flex';
        }
    } catch (error) {
        showToast('Error loading token details', 'error');
    }
}

// Close Token Modal
function closeTokenModal() {
    document.getElementById('tokenDetailModal').style.display = 'none';
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('tokenDetailModal');
    if (event.target === modal) {
        modal.style.display = 'none';
    }
};

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    loadTokenHistory();
    
    // Set today's date as default
    document.getElementById('filterDate').valueAsDate = new Date();
});
