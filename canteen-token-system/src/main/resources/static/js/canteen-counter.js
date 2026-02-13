/**
 * Canteen Counter JavaScript
 * QR Scanning and Token Redemption
 */

let html5QrCode = null;
let currentToken = null;
let isScanning = false;

// Start QR Scanner
function startScanner() {
    const qrReader = document.getElementById('qr-reader');
    
    if (isScanning) return;
    
    html5QrCode = new Html5Qrcode('qr-reader');
    
    const config = {
        fps: 10,
        qrbox: { width: 250, height: 250 }
    };
    
    html5QrCode.start(
        { facingMode: 'environment' },
        config,
        onScanSuccess,
        onScanError
    ).then(() => {
        isScanning = true;
        document.getElementById('startScanBtn').style.display = 'none';
        document.getElementById('stopScanBtn').style.display = 'inline-flex';
        showToast('Scanner started', 'success');
    }).catch(err => {
        console.error('Error starting scanner:', err);
        showToast('Error starting scanner. Please check camera permissions.', 'error');
    });
}

// Stop QR Scanner
function stopScanner() {
    if (html5QrCode && isScanning) {
        html5QrCode.stop().then(() => {
            isScanning = false;
            document.getElementById('startScanBtn').style.display = 'inline-flex';
            document.getElementById('stopScanBtn').style.display = 'none';
            html5QrCode.clear();
        }).catch(err => {
            console.error('Error stopping scanner:', err);
        });
    }
}

// On Scan Success
async function onScanSuccess(qrData) {
    // Stop scanner temporarily
    stopScanner();
    
    try {
        await verifyAndDisplayToken(qrData);
    } catch (error) {
        showToast('Invalid QR code', 'error');
    }
}

// On Scan Error
function onScanError(error) {
    // Ignore scan errors (happens frequently during scanning)
    console.log('Scan error:', error);
}

// Verify and Display Token
async function verifyAndDisplayToken(qrData) {
    try {
        const response = await fetchAPI(`${API_BASE_URL}/tokens/redeem`, {
            method: 'POST',
            body: JSON.stringify({
                qrCodeData: qrData,
                redeemedBy: document.getElementById('redeemedBy')?.value || 'Canteen Staff'
            })
        });
        
        if (response.success && response.data) {
            currentToken = response.data;
            displayRedemptionSuccess(response.data);
            showToast('Token redeemed successfully!', 'success');
            loadRedeemedTokens();
            loadStats();
        } else {
            showToast(response.message || 'Failed to redeem token', 'error');
        }
    } catch (error) {
        // If already redeemed or other error, try to get token details
        try {
            const tokenResponse = await fetchAPI(`${API_BASE_URL}/tokens/verify`, {
                method: 'POST',
                body: JSON.stringify({ qrCodeData: qrData })
            });
            
            if (tokenResponse.success) {
                displayTokenDetails(tokenResponse.data);
            } else {
                showToast(error.message || 'Invalid token', 'error');
            }
        } catch (e) {
            showToast(error.message || 'Invalid token', 'error');
        }
    }
}

// Display Token Details (for verification before redemption)
function displayTokenDetails(token) {
    currentToken = token;
    
    document.getElementById('detailTokenNumber').textContent = token.tokenNumber;
    document.getElementById('detailEmpName').textContent = token.employeeName || '-';
    document.getElementById('detailEmpId').textContent = token.employeeId;
    document.getElementById('detailDept').textContent = token.employeeDepartment || '-';
    document.getElementById('detailMealType').textContent = token.mealType;
    document.getElementById('detailIssueTime').textContent = formatDateTime(token.issueTime);
    document.getElementById('detailStatus').textContent = token.status;
    
    const statusHeader = document.getElementById('tokenStatusHeader');
    const redeemAction = document.getElementById('redeemAction');
    
    if (token.status === 'ISSUED') {
        statusHeader.innerHTML = '<i class="fas fa-check-circle"></i><span>Valid Token - Ready to Redeem</span>';
        statusHeader.className = 'token-status-header valid';
        redeemAction.style.display = 'block';
    } else if (token.status === 'REDEEMED') {
        statusHeader.innerHTML = '<i class="fas fa-info-circle"></i><span>Token Already Redeemed</span>';
        statusHeader.className = 'token-status-header info';
        redeemAction.style.display = 'none';
    } else {
        statusHeader.innerHTML = '<i class="fas fa-exclamation-circle"></i><span>Token ' + token.status + '</span>';
        statusHeader.className = 'token-status-header invalid';
        redeemAction.style.display = 'none';
    }
    
    document.getElementById('tokenDetailsSection').style.display = 'block';
    document.getElementById('redemptionSuccess').style.display = 'none';
}

// Confirm Redemption
async function confirmRedeem() {
    if (!currentToken) return;
    
    const redeemedBy = document.getElementById('redeemedBy').value || 'Canteen Staff';
    
    try {
        const response = await fetchAPI(`${API_BASE_URL}/tokens/redeem`, {
            method: 'POST',
            body: JSON.stringify({
                qrCodeData: currentToken.qrCodeData,
                redeemedBy: redeemedBy
            })
        });
        
        if (response.success && response.data) {
            displayRedemptionSuccess(response.data);
            showToast('Token redeemed successfully!', 'success');
            loadRedeemedTokens();
            loadStats();
        } else {
            showToast(response.message || 'Failed to redeem token', 'error');
        }
    } catch (error) {
        showToast(error.message || 'Error redeeming token', 'error');
    }
}

// Display Redemption Success
function displayRedemptionSuccess(token) {
    document.getElementById('successTokenNumber').textContent = token.tokenNumber;
    document.getElementById('successEmpName').textContent = token.employeeName || token.employeeId;
    document.getElementById('successTime').textContent = formatTime(token.redeemTime);
    
    document.getElementById('tokenDetailsSection').style.display = 'none';
    document.getElementById('redemptionSuccess').style.display = 'block';
}

// Reset Redemption
function resetRedemption() {
    currentToken = null;
    document.getElementById('redemptionSuccess').style.display = 'none';
    document.getElementById('tokenDetailsSection').style.display = 'none';
    document.getElementById('manualTokenNumber').value = '';
    
    // Restart scanner if needed
    // startScanner();
}

// Redeem Manual Token
async function redeemManualToken() {
    const tokenNumber = document.getElementById('manualTokenNumber').value.trim();
    
    if (!tokenNumber) {
        showToast('Please enter a token number', 'error');
        return;
    }
    
    try {
        // First get token details
        const tokenResponse = await fetchAPI(`${API_BASE_URL}/tokens/${tokenNumber}`);
        
        if (tokenResponse.success && tokenResponse.data) {
            const token = tokenResponse.data;
            
            if (token.status !== 'ISSUED') {
                showToast(`Token is already ${token.status}`, 'error');
                return;
            }
            
            // Redeem the token
            const redeemResponse = await fetchAPI(`${API_BASE_URL}/tokens/redeem-by-number`, {
                method: 'POST',
                body: JSON.stringify({
                    tokenNumber: tokenNumber,
                    redeemedBy: document.getElementById('redeemedBy')?.value || 'Canteen Staff'
                })
            });
            
            if (redeemResponse.success && redeemResponse.data) {
                displayRedemptionSuccess(redeemResponse.data);
                showToast('Token redeemed successfully!', 'success');
                loadRedeemedTokens();
                loadStats();
            } else {
                showToast(redeemResponse.message || 'Failed to redeem token', 'error');
            }
        } else {
            showToast('Token not found', 'error');
        }
    } catch (error) {
        showToast(error.message || 'Error redeeming token', 'error');
    }
}

// Load Redeemed Tokens
async function loadRedeemedTokens() {
    try {
        const response = await fetchAPI(`${API_BASE_URL}/tokens/today/REDEEMED`);
        
        if (response.success && response.data) {
            displayRedeemedTokens(response.data);
        }
    } catch (error) {
        console.error('Error loading redeemed tokens:', error);
    }
}

// Display Redeemed Tokens
function displayRedeemedTokens(tokens) {
    const tbody = document.getElementById('redeemedTokensTable');
    
    if (tokens.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">No tokens redeemed today</td></tr>';
        return;
    }
    
    tbody.innerHTML = tokens.map(token => `
        <tr>
            <td><strong>${token.tokenNumber}</strong></td>
            <td>${token.employeeName || token.employeeId}</td>
            <td><i class="fas fa-${getMealIcon(token.mealType)}"></i> ${token.mealType}</td>
            <td>${formatTime(token.redeemTime)}</td>
            <td>${token.redeemedBy || '-'}</td>
        </tr>
    `).join('');
}

// Load Statistics
async function loadStats() {
    try {
        const response = await fetchAPI(`${API_BASE_URL}/tokens/stats/today`);
        
        if (response.success && response.data) {
            const stats = response.data;
            document.getElementById('statTotalIssued').textContent = stats.totalIssued;
            document.getElementById('statTotalRedeemed').textContent = stats.totalRedeemed;
            document.getElementById('statPending').textContent = stats.totalIssued - stats.totalRedeemed;
        }
    } catch (error) {
        console.error('Error loading stats:', error);
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    loadRedeemedTokens();
    loadStats();
    
    // Handle Enter key on manual token input
    const manualInput = document.getElementById('manualTokenNumber');
    if (manualInput) {
        manualInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                redeemManualToken();
            }
        });
    }
});
