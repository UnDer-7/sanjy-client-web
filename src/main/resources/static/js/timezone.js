/**
 * Timezone Manager
 * Handles timezone detection, storage, and automatic timezone header injection for all requests
 */
(function() {
    'use strict';

    const STORAGE_KEY = 'sanjy-user-timezone';
    const COOKIE_NAME = 'sanjy-user-timezone';
    const HEADER_NAME = 'X-User-Timezone';

    /**
     * Timezone Manager Object
     */
    const TimezoneManager = {
        /**
         * Get the current timezone (from localStorage or browser default)
         * @returns {string} IANA timezone identifier
         */
        getTimezone: function() {
            // Try to get from localStorage first
            const stored = localStorage.getItem(STORAGE_KEY);
            if (stored) {
                return stored;
            }

            // Fallback to browser default
            return Intl.DateTimeFormat().resolvedOptions().timeZone;
        },

        /**
         * Set the user's timezone preference
         * @param {string} timezone - IANA timezone identifier
         */
        setTimezone: function(timezone) {
            if (!timezone) {
                console.error('Timezone cannot be empty');
                return;
            }
            localStorage.setItem(STORAGE_KEY, timezone);

            // Also save to cookie for backend access
            this.setTimezoneCookie(timezone);

            console.log('Timezone set to:', timezone);
        },

        /**
         * Clear the timezone preference (will fallback to browser default)
         */
        clearTimezone: function() {
            localStorage.removeItem(STORAGE_KEY);
            this.deleteTimezoneCookie();
            console.log('Timezone preference cleared');
        },

        /**
         * Get the timezone header name
         * @returns {string}
         */
        getHeaderName: function() {
            return HEADER_NAME;
        },

        /**
         * Set timezone in cookie (for backend access on direct URL navigation)
         * @param {string} timezone - IANA timezone identifier
         */
        setTimezoneCookie: function(timezone) {
            // Set cookie for 365 days
            const expires = new Date();
            expires.setDate(expires.getDate() + 365);
            document.cookie = `${COOKIE_NAME}=${timezone}; expires=${expires.toUTCString()}; path=/; SameSite=Lax`;
        },

        /**
         * Delete timezone cookie
         */
        deleteTimezoneCookie: function() {
            document.cookie = `${COOKIE_NAME}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
        }
    };

    // Expose to global scope
    window.TimezoneManager = TimezoneManager;

    /**
     * Intercept all XMLHttpRequest and fetch requests to add timezone header
     */

    // Intercept XMLHttpRequest
    const originalXHROpen = XMLHttpRequest.prototype.open;
    const originalXHRSend = XMLHttpRequest.prototype.send;

    XMLHttpRequest.prototype.open = function() {
        this._url = arguments[1];
        return originalXHROpen.apply(this, arguments);
    };

    XMLHttpRequest.prototype.send = function() {
        const timezone = TimezoneManager.getTimezone();
        console.debug("Intercepting XMLHttpRequest. timezone: ", timezone);
        if (timezone && this._url) {
            // Only add header for same-origin requests
            if (!this._url.startsWith('http') || this._url.startsWith(window.location.origin)) {
                this.setRequestHeader(HEADER_NAME, timezone);
            }
        }
        return originalXHRSend.apply(this, arguments);
    };

    // Intercept fetch API
    const originalFetch = window.fetch;
    window.fetch = function(url, options) {
        options = options || {};
        options.headers = options.headers || {};

        const timezone = TimezoneManager.getTimezone();
        console.debug("Intercepting FetchApi. timezone: ", timezone);

        // Add timezone header for same-origin requests
        const urlString = typeof url === 'string' ? url : url.url;
        if (timezone && (!urlString.startsWith('http') || urlString.startsWith(window.location.origin))) {
            if (options.headers instanceof Headers) {
                options.headers.append(HEADER_NAME, timezone);
            } else {
                options.headers[HEADER_NAME] = timezone;
            }
        }

        return originalFetch.apply(this, [url, options]);
    };

    /**
     * Form submission interceptor to add hidden timezone input
     */
    document.addEventListener('DOMContentLoaded', function() {
        // Initialize timezone in cookie on page load
        const timezone = TimezoneManager.getTimezone();
        TimezoneManager.setTimezoneCookie(timezone);

        // Intercept all form submissions
        document.addEventListener('submit', function(event) {
            const form = event.target;
            const timezone = TimezoneManager.getTimezone();

            // Check if timezone input already exists
            let timezoneInput = form.querySelector('input[name="userTimezone"]');
            if (!timezoneInput) {
                // Create hidden input for timezone
                timezoneInput = document.createElement('input');
                timezoneInput.type = 'hidden';
                timezoneInput.name = 'userTimezone';
                timezoneInput.value = timezone;
                form.appendChild(timezoneInput);
            } else {
                // Update existing input
                timezoneInput.value = timezone;
            }
            console.debug("Intercepting Form submission. timezone: ", timezone);
        });

        console.log('Timezone Manager initialized. Current timezone:', TimezoneManager.getTimezone());
    });

    /**
     * Add timezone to URL query string
     * Use this function to add timezone parameter to links
     */
    TimezoneManager.addTimezoneToUrl = function(url) {
        if (!url) return url;

        const timezone = this.getTimezone();
        if (!timezone) return url;

        const urlObj = new URL(url, window.location.origin);
        urlObj.searchParams.set('userTimezone', timezone);
        return urlObj.toString();
    };

    /**
     * Navigate to URL with timezone
     * Use this function instead of window.location.href for navigation
     */
    TimezoneManager.navigateTo = function(url) {
        window.location.href = this.addTimezoneToUrl(url);
    };

    /**
     * Utility functions for datetime conversion (client-side)
     */
    TimezoneManager.convertToUTC = function(localDateTimeString, timezone) {
        if (!localDateTimeString) return null;

        timezone = timezone || this.getTimezone();

        // Parse the datetime string as if it's in the user's timezone
        // Format expected: "2025-01-03T14:30" (datetime-local format)
        const date = new Date(localDateTimeString);

        // Return ISO string (UTC)
        return date.toISOString();
    };

    TimezoneManager.convertFromUTC = function(utcDateTimeString, timezone) {
        if (!utcDateTimeString) return null;

        timezone = timezone || this.getTimezone();

        const date = new Date(utcDateTimeString);

        // Format for datetime-local input: YYYY-MM-DDTHH:mm
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');

        return `${year}-${month}-${day}T${hours}:${minutes}`;
    };

})();
