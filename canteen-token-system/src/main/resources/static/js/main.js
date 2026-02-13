/**
 * Canteen Token Management System
 * Main JavaScript File
 */

// API Base URL
const API_BASE_URL = '/api';

// Toast Notification Function
function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    if (!toast) return;
    
    toast.innerHTML = `<i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'}"></i> ${message}`;
    toast.className = `toast ${type} show`;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// Format Date
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        day: '2-digit',
        month: 'short',
        year: 'numeric'
    });
}

// Format Time
function formatTime(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Format DateTime
function formatDateTime(dateString) {
    if (!dateString) return '-';
    return `${formatDate(dateString)} ${formatTime(dateString)}`;
}

// Get Meal Type Icon
function getMealIcon(mealType) {
    const icons = {
        'BREAKFAST': 'coffee',
        'LUNCH': 'hamburger',
        'DINNER': 'moon',
        'SNACKS': 'cookie-bite'
    };
    return icons[mealType] || 'utensils';
}

// Get Status Badge HTML
function getStatusBadge(status) {
    const statusClasses = {
        'ISSUED': 'ISSUED',
        'REDEEMED': 'REDEEMED',
        'EXPIRED': 'EXPIRED',
        'CANCELLED': 'CANCELLED'
    };
    return `<span class="status-badge ${statusClasses[status] || status}">${status}</span>`;
}

// Fetch API Helper
async function fetchAPI(url, options = {}) {
    try {
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });
        
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.message || 'Something went wrong');
        }
        
        return data;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// Confirm Dialog
function confirmAction(message, callback) {
    if (confirm(message)) {
        callback();
    }
}

// Print Function
function printContent(content) {
    const printWindow = window.open('', '_blank');
    printWindow.document.write(`
        <!DOCTYPE html>
        <html>
        <head>
            <title>Print Token</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    padding: 20px;
                    text-align: center;
                }
                .token-print {
                    border: 2px dashed #333;
                    padding: 20px;
                    max-width: 300px;
                    margin: 0 auto;
                }
                .token-print h2 {
                    margin-bottom: 10px;
                }
                .token-print img {
                    max-width: 200px;
                    margin: 15px 0;
                }
                .token-info {
                    text-align: left;
                    margin-top: 15px;
                }
                .token-info p {
                    margin: 5px 0;
                }
                @media print {
                    body { padding: 0; }
                }
            </style>
        </head>
        <body>
            ${content}
            <script>
                window.onload = function() {
                    setTimeout(function() {
                        window.print();
                        window.close();
                    }, 200);
                };
            <\/script>
        </body>
        </html>
    `);
    printWindow.document.close();
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    // Add active class to current nav item
    const currentPath = window.location.pathname;
    document.querySelectorAll('.nav-menu a').forEach(link => {
        if (link.getAttribute('href') === currentPath) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });
});
