/*
* ----------------------------------------------------------------------------------------
Author       : onepageboss
Template Name: Lean - One Page MultiPurpose Template
Version      : 1.0                                          
* ----------------------------------------------------------------------------------------
*/

(function (jQuery) {
    'use strict';

    jQuery(document).ready(function () {

        /*
         * ----------------------------------------------------------------------------------------
         *  PRELOADER JS
         * ----------------------------------------------------------------------------------------
         */
        jQuery(window).on("load", function () {
            jQuery('.spinner').fadeOut();
            jQuery('.preloader-area').delay(350).fadeOut('slow');
        });

        /*
         * ----------------------------------------------------------------------------------------
         *  CHANGE MENU BACKGROUND JS
         * ----------------------------------------------------------------------------------------
         */
        jQuery(window).on('scroll', function () {
            if (jQuery(window).scrollTop() > 200) {
                jQuery('.header-top-area').addClass('menu-bg');
            } else {
                jQuery('.header-top-area').removeClass('menu-bg');
            }
        });



        /*
         * ----------------------------------------------------------------------------------------
         *  SMOTH SCROOL JS
         * ----------------------------------------------------------------------------------------
         */

        jQuery('a.smoth-scroll').on("click", function (e) {
            var anchor = jQuery(this);
            jQuery('html, body').stop().animate({
                scrollTop: jQuery(anchor.attr('href')).offset().top - 50
            }, 1000);
            e.preventDefault();
        });


        /*
         * ----------------------------------------------------------------------------------------
         *  MAGNIFIC POPUP JS
         * ----------------------------------------------------------------------------------------
         */

        var magnifPopup = function () {
            jQuery('.work-popup').magnificPopup({
                type: 'image',
                removalDelay: 300,
                mainClass: 'mfp-with-zoom',
                gallery: {
                    enabled: true
                },
                zoom: {
                    enabled: true, // By default it's false, so don't forget to enable it

                    duration: 300, // duration of the effect, in milliseconds
                    easing: 'ease-in-out', // CSS transition easing function

                    // The "opener" function should return the element from which popup will be zoomed in
                    // and to which popup will be scaled down
                    // By defailt it looks for an image tag:
                    opener: function (openerElement) {
                        // openerElement is the element on which popup was initialized, in this case its <a> tag
                        // you don't need to add "opener" option if this code matches your needs, it's defailt one.
                        return openerElement.is('img') ? openerElement : openerElement.find('img');
                    }
                }
            });
        };
        // Call the functions 
        magnifPopup();
        /*
         * ----------------------------------------------------------------------------------------
         *  PARALLAX JS
         * ----------------------------------------------------------------------------------------
         */

        jQuery(window).stellar({
            responsive: true,
            positionProperty: 'position',
            horizontalScrolling: false
        });

        /*
         * ----------------------------------------------------------------------------------------
         *  COUNTER UP JS
         * ----------------------------------------------------------------------------------------
         */

        jQuery('.counter-num').counterUp();


        /*
         * ----------------------------------------------------------------------------------------
         *  USER & SCREENSHOTS JS
         * ----------------------------------------------------------------------------------------
         */

        jQuery(".testimonial-list").owlCarousel({
            items: 2,
            autoPlay: true,
            navigation: false,
            itemsDesktop: [1199, 1],
            itemsDesktopSmall: [980, 2],
            itemsTablet: [768, 2],
            itemsTabletSmall: false,
            itemsMobile: [479, 1],
            pagination: true,
            autoHeight: true,
        });

       


        /*
         * ----------------------------------------------------------------------------------------
         *  EXTRA JS
         * ----------------------------------------------------------------------------------------
         */
        jQuery(document).on('click', '.navbar-collapse.in', function (e) {
            if (jQuery(e.target).is('a') && jQuery(e.target).attr('class') != 'dropdown-toggle') {
                jQuery(this).collapse('hide');
            }
        });
        jQuery('body').scrollspy({
            target: '.navbar-collapse',
            offset: 195
        });


        jQuery('ul.nav li.dropdown').hover(function() {
        jQuery(this).find('.dropdown-menu').stop(true, true).delay(200).fadeIn(200);
        }, function() {
        jQuery(this).find('.dropdown-menu').stop(true, true).delay(200).fadeOut(200);
        });
        
        /*
         * ----------------------------------------------------------------------------------------
         *  SCROOL TO UP JS
         * ----------------------------------------------------------------------------------------
         */
        jQuery(window).on("scroll", function () {
            if (jQuery(this).scrollTop() > 250) {
                jQuery('.scrollup').fadeIn();
            } else {
                jQuery('.scrollup').fadeOut();
            }
        });
        jQuery('.scrollup').on("click", function () {
            jQuery("html, body").animate({
                scrollTop: 0
            }, 800);
            return false;
        });

        /*
         * ----------------------------------------------------------------------------------------
         *  WOW JS
         * ----------------------------------------------------------------------------------------
         */
        new WOW().init();

    });

    jQuery(document).ready(function(){
        jQuery('.welcome-slider-area').css('height', jQuery(window).height());
        // Comma, not colon ----^
    });
    jQuery(window).resize(function(){
        jQuery('.welcome-slider-area').css('height', jQuery(window).height());
        // Comma, not colon ----^
    });

    jQuery(document).ready(function(){
        jQuery('.welcome-image-area').css('height', jQuery(window).height());
        // Comma, not colon ----^
    });
    jQuery(window).resize(function(){
        jQuery('.welcome-image-area').css('height', jQuery(window).height());
        // Comma, not colon ----^
    });

    jQuery(document).ready(function(){
        jQuery('.welcome-video-area').css('height', jQuery(window).height());
        // Comma, not colon ----^
    });
    jQuery(window).resize(function(){
        jQuery('.welcome-video-area').css('height', jQuery(window).height());
        // Comma, not colon ----^
    });

})(jQuery);