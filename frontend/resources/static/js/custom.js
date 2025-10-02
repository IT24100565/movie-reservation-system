/**
 * Session Management and Security JavaScript
 * Handles session timeout, activity tracking, and automatic logout
 */

// Session timeout configuration (30 minutes in milliseconds)
const SESSION_TIMEOUT = 30 * 60 * 1000; // 30 minutes
const WARNING_TIME = 5 * 60 * 1000;     // 5 minutes before timeout
const CHECK_INTERVAL = 60 * 1000;       // Check every minute

let lastActivityTime = Date.now();
let sessionTimeoutWarningShown = false;
let timeoutWarningTimer = null;
let sessionCheckTimer = null;

// Initialize session management when page loads
document.addEventListener('DOMContentLoaded', function() {
    if (isAdminPage()) {
        initializeSessionManagement();
    }
});

/**
 * Initialize session management for admin pages
 */
function initializeSessionManagement() {
    // Track user activity
    trackUserActivity();
    
    // Start session monitoring
    startSessionMonitoring();
    
    // Handle page visibility changes (browser tab focus/blur)
    handlePageVisibility();
    
    // Intercept AJAX requests to handle session timeout responses
    interceptAjaxRequests();
}

/**
 * Check if current page is an admin page
 */
function isAdminPage() {
    return window.location.pathname.startsWith('/admin');
}

/**
 * Track user activity to reset session timeout
 */
function trackUserActivity() {
    const events = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click'];
    
    events.forEach(event => {
        document.addEventListener(event, function() {
            updateLastActivity();
        }, { passive: true });
    });
}

/**
 * Update last activity time
 */
function updateLastActivity() {
    lastActivityTime = Date.now();
    sessionTimeoutWarningShown = false;
    
    // Clear any existing warning
    clearTimeoutWarning();
}

/**
 * Start monitoring session timeout
 */
function startSessionMonitoring() {
    sessionCheckTimer = setInterval(function() {
        checkSessionTimeout();
    }, CHECK_INTERVAL);
}

/**
 * Check if session has timed out
 */
function checkSessionTimeout() {
    const currentTime = Date.now();
    const inactiveTime = currentTime - lastActivityTime;
    
    // Show warning 5 minutes before timeout
    if (inactiveTime >= (SESSION_TIMEOUT - WARNING_TIME) && !sessionTimeoutWarningShown) {
        showTimeoutWarning();
        sessionTimeoutWarningShown = true;
    }
    
    // Logout if session has timed out
    if (inactiveTime >= SESSION_TIMEOUT) {
        handleSessionTimeout();
    }
}

/**
 * Show session timeout warning
 */
function showTimeoutWarning() {
    const modal = createTimeoutWarningModal();
    document.body.appendChild(modal);
    
    // Auto-logout after warning period
    timeoutWarningTimer = setTimeout(function() {
        handleSessionTimeout();
    }, WARNING_TIME);
}

/**
 * Create timeout warning modal
 */
function createTimeoutWarningModal() {
    const modal = document.createElement('div');
    modal.id = 'sessionTimeoutModal';
    modal.className = 'modal fade show';
    modal.style.display = 'block';
    modal.style.backgroundColor = 'rgba(0,0,0,0.5)';
    
    modal.innerHTML = `
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header bg-warning text-dark">
                    <h5 class="modal-title">
                        <i class="fas fa-exclamation-triangle"></i> Session Timeout Warning
                    </h5>
                </div>
                <div class="modal-body">
                    <p>Your session will expire in <strong>5 minutes</strong> due to inactivity.</p>
                    <p>Click "Stay Logged In" to extend your session, or you will be automatically logged out.</p>
                    <div class="text-center mt-3">
                        <span id="timeoutCountdown" class="badge bg-warning text-dark fs-6">5:00</span>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" onclick="extendSession()">Stay Logged In</button>
                    <button type="button" class="btn btn-secondary" onclick="logoutNow()">Logout Now</button>
                </div>
            </div>
        </div>
    `;
    
    // Start countdown timer
    startCountdown();
    
    return modal;
}

/**
 * Start countdown timer in the warning modal
 */
function startCountdown() {
    let remainingTime = WARNING_TIME / 1000; // Convert to seconds
    
    const countdownInterval = setInterval(function() {
        const minutes = Math.floor(remainingTime / 60);
        const seconds = remainingTime % 60;
        
        const countdownElement = document.getElementById('timeoutCountdown');
        if (countdownElement) {
            countdownElement.textContent = `${minutes}:${seconds.toString().padStart(2, '0')}`;
        }
        
        remainingTime--;
        
        if (remainingTime < 0) {
            clearInterval(countdownInterval);
            handleSessionTimeout();
        }
    }, 1000);
}

/**
 * Extend session by updating activity
 */
function extendSession() {
    updateLastActivity();
    clearTimeoutWarning();
    
    // Make a lightweight request to server to refresh session
    fetch('/admin/dashboard', {
        method: 'HEAD',
        headers: {
            'Cache-Control': 'no-cache'
        }
    }).catch(function() {
        // If request fails, user is likely already logged out
        handleSessionTimeout();
    });
}

/**
 * Logout immediately
 */
function logoutNow() {
    clearTimeoutWarning();
    window.location.href = '/logout';
}

/**
 * Handle session timeout - redirect to login
 */
function handleSessionTimeout() {
    clearTimeoutWarning();
    clearInterval(sessionCheckTimer);
    
    // Clear any remaining timers
    if (timeoutWarningTimer) {
        clearTimeout(timeoutWarningTimer);
    }
    
    // Redirect to login with session expired flag
    window.location.href = '/login?sessionExpired=true';
}

/**
 * Clear timeout warning modal
 */
function clearTimeoutWarning() {
    const modal = document.getElementById('sessionTimeoutModal');
    if (modal) {
        modal.remove();
    }
    
    if (timeoutWarningTimer) {
        clearTimeout(timeoutWarningTimer);
        timeoutWarningTimer = null;
    }
}

/**
 * Handle page visibility changes (tab switching, browser minimize)
 */
function handlePageVisibility() {
    document.addEventListener('visibilitychange', function() {
        if (document.visibilityState === 'visible') {
            // Page became visible - check session status
            checkSessionStatus();
        }
    });
    
    // Handle window focus/blur events
    window.addEventListener('focus', function() {
        checkSessionStatus();
    });
}

/**
 * Check session status with server
 */
function checkSessionStatus() {
    if (!isAdminPage()) return;
    
    fetch('/admin/dashboard', {
        method: 'HEAD',
        headers: {
            'Cache-Control': 'no-cache'
        }
    }).then(function(response) {
        if (response.status === 401 || response.redirected) {
            // Session expired, redirect to login
            window.location.href = '/login?sessionExpired=true';
        }
    }).catch(function() {
        // Network error or session issue
        setTimeout(checkSessionStatus, 5000); // Retry in 5 seconds
    });
}

/**
 * Intercept AJAX requests to handle session timeouts
 */
function interceptAjaxRequests() {
    // Override fetch function
    const originalFetch = window.fetch;
    window.fetch = function(...args) {
        return originalFetch.apply(this, args)
            .then(function(response) {
                if (response.status === 401) {
                    // Session expired
                    response.json().then(function(data) {
                        if (data.redirect) {
                            window.location.href = data.redirect + '?sessionExpired=true';
                        } else {
                            window.location.href = '/login?sessionExpired=true';
                        }
                    }).catch(function() {
                        window.location.href = '/login?sessionExpired=true';
                    });
                }
                return response;
            });
    };
    
    // Override XMLHttpRequest
    const originalOpen = XMLHttpRequest.prototype.open;
    const originalSend = XMLHttpRequest.prototype.send;
    
    XMLHttpRequest.prototype.open = function(method, url, async, user, password) {
        this._url = url;
        return originalOpen.apply(this, arguments);
    };
    
    XMLHttpRequest.prototype.send = function(data) {
        const xhr = this;
        
        xhr.addEventListener('readystatechange', function() {
            if (xhr.readyState === 4 && xhr.status === 401) {
                try {
                    const response = JSON.parse(xhr.responseText);
                    if (response.redirect) {
                        window.location.href = response.redirect + '?sessionExpired=true';
                    } else {
                        window.location.href = '/login?sessionExpired=true';
                    }
                } catch (e) {
                    window.location.href = '/login?sessionExpired=true';
                }
            }
        });
        
        return originalSend.apply(this, arguments);
    };
}

/**
 * Cleanup when page is unloaded
 */
window.addEventListener('beforeunload', function() {
    if (sessionCheckTimer) {
        clearInterval(sessionCheckTimer);
    }
    if (timeoutWarningTimer) {
        clearTimeout(timeoutWarningTimer);
    }
});