
/* =========================================
    Hamburger menu toggle functionality
    Handles mobile navigation menu open/close interactions
   ========================================= */
(function() {
    'use strict';
    
    // Cache DOM elements
    const hamburger = document.querySelector(".nav__hamburger");
    const menu = document.querySelector(".nav__menu");
    const body = document.body;
    
    // Early return if required elements don't exist
    if (!hamburger || !menu) {
        console.warn('Navigation elements not found');
        return;
    }
    
    /**
     * Toggles the mobile navigation menu state
     * @param {Event} event - Click event
     */
    function toggleMenu(event) {
        const isOpen = menu.classList.contains("nav__menu--open");
        
        // Toggle menu visibility
        menu.classList.toggle("nav__menu--open");
        hamburger.classList.toggle("nav__cross");
        body.classList.toggle("no-scroll");
        
        // Update ARIA attribute for accessibility
        hamburger.setAttribute('aria-expanded', !isOpen ? 'true' : 'false');
        hamburger.setAttribute('aria-label', isOpen ? 'Otevřít menu' : 'Zavřít menu');
    }
    
    /**
     * Closes the mobile menu
     */
    function closeMenu() {
        if (menu.classList.contains("nav__menu--open")) {
            menu.classList.remove("nav__menu--open");
            hamburger.classList.remove("nav__cross");
            body.classList.remove("no-scroll");
            hamburger.setAttribute('aria-expanded', 'false');
            hamburger.setAttribute('aria-label', 'Otevřít menu');
        }
    }
    
    /**
     * Closes menu when Escape key is pressed
     * @param {KeyboardEvent} event - Keyboard event
     */
    function handleEscapeKey(event) {
        if (event.key === 'Escape') {
            closeMenu();
        }
    }
    
    // Initialize ARIA attributes
    hamburger.setAttribute('aria-expanded', 'false');
    
    // Event listeners
    hamburger.addEventListener("click", toggleMenu);
    document.addEventListener("keydown", handleEscapeKey);
    
    // Close menu when clicking on a menu link (better mobile UX)
    const menuLinks = menu.querySelectorAll('.nav__link');
    menuLinks.forEach(link => {
        link.addEventListener('click', closeMenu);
    });
})();

/* =========================================
    Match filter functionality
   ========================================= */
(function() {
    'use strict';

    const filterBtns = document.querySelectorAll('.filter-btn[data-filter]');
    if (!filterBtns.length) return; // Not on zapasy page

    const groups = document.querySelectorAll('.matches__group[data-group]');

    filterBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const filter = this.dataset.filter;

            // Update active button
            filterBtns.forEach(b => b.classList.remove('filter-btn--active'));
            this.classList.add('filter-btn--active');

            // Show/hide groups
            groups.forEach(group => {
                if (filter === 'all' || group.dataset.group === filter) {
                    group.style.display = '';
                } else {
                    group.style.display = 'none';
                }
            });
        });
    });
})();
