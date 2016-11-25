/**
 * A plugin targeted at a 2 levels lists DOM to collapse and search the nested lists.
 *
 * Expected html structure is as follows:
 * =======================================
 *
 * <ul id="mylist">
 *     <li class="header">Actors</li>
 *     <ul>
 *         <li>Tom Cruise</li>
 *         <li>Nicolas Cage</li>
 *     </ul>
 *     <li class="header">Producers</li>
 *     <ul>
 *         <li>Steven Spielberg</li>
 *         <li>...</li>
 *     </ul>
 * </ul>
 *
 * Usage with the previous example:
 * ================================
 *
 * $('#mylist').collapsibleList('li.header');
 *
 *
 *
 * Author: Sebastien Roch - http://github.com/sebastien-roch
 */
(function ($) {
    $.fn.collapsibleList = function (headerSelector, opts) {
        var ESCAPE_KEY = 27;
        var defaults = {
            search: false,
            animate: true
        };
        var options = $.extend(defaults, $.fn.collapsibleList.defaults, opts);

        // case insensitive "contains" selector
        jQuery.expr[':'].cicontains = function(a,i,m) {
            return (a.textContent || a.innerText || "").toUpperCase().indexOf(m[3].toUpperCase()) >= 0;
        };

        return this.each(function () {
            var headers,
                allElements,
                searchField,
                mainUl = $(this);

            function getHeaders() {
                var headers = mainUl.find('> ' + headerSelector);
                headers.css('cursor', 'pointer');

                return headers;
            }

            function setHeadersClickHandler() {
                headers.on('click', function() {
                    toggleCollapse($(this));
                });
            }

            function hideListElements(liElems) {
                if (options.animate) {
                    return liElems.slideUp().promise();
                }
                return liElems.hide().promise();
            }

            function showListElements(liElems) {
                if (options.animate) {
                    return liElems.slideDown().promise();
                }
                return liElems.show().promise();
            }

            function collapseAllHeaders() {
            	$('.header').addClass('collapsed');
                return hideListElements(allElements);
                
            }

            function expandAllHeaders() {
                return showListElements(allElements);
            }

            function collapseHeader(header) {
                return hideListElements(findHeadersList(header).find('> li'))
                    .done(function() {
                        header.addClass('collapsed');
                    });
            }

            function expandHeader(header) {
                return showListElements(findHeadersList(header).find('> li'))
                    .done(function() {
                        header.removeClass('collapsed');
                    });
            }

            function toggleCollapse(header) {
                return isHeaderCollapsed(header) ? expandHeader(header) : collapseHeader(header);
            }

            function isHeaderCollapsed(header) {
                return true === header.hasClass('collapsed');
            }

            function findHeadersList(header) {
                return header.next('ul, ol');
            }

            function setSearchField() {
                if (options.search === true) {
                    searchField = $('<input class="collapsible-search"/>');
                } else if (options.search instanceof jQuery) {
                    searchField = $(options.search);
                } else {
                    throw "invalid search option passed: must be true, false or a jQuery object";
                }

                searchField.on('keyup', function(e) {
                    if (e.which === ESCAPE_KEY) {
                        return quitSearch();
                    }
                    doSearch(searchField.val());
                });

                // append the field if it's not already in the DOM
                if (searchField.parents('body').length === 0) {
                    mainUl.prepend(searchField);
                }

                return searchField;
            }

            function quitSearch() {
                searchField.val('');
                doSearch('');
                searchField.blur();
            }

            function isEmptyInput(rawInput) {
                return '' === rawInput;
            }

            function doSearch(input) {
                if (isEmptyInput(input)) {
                    restoreMainListState().done(showHeaders);
                    return;
                }

                var p1 = hideListElements(allElements.filter(':not(:cicontains(' + input + '))'));
                var p2 = showListElements(allElements.filter(':cicontains(' + input + ')'));
                $.when(p1, p2).done(hideHeadersWithEmptyList);
            }

            function hideHeadersWithEmptyList() {
                headers.each(function(){
                    if ($(this).next('ul').find('li:visible').length === 0) {
                        $(this).hide();
                    } else {
                        $(this).show();
                    }
                });
            }

            function showHeaders() {
                headers.show();
            }

            function restoreMainListState() {
                var promises = [];
                headers.each(function() {
                    var header = $(this);
                    if (isHeaderCollapsed(header)) {
                        promises.push(hideListElements(findHeadersList(header).find('> li')));
                    } else {
                        // hide the list elements that matched the last search
                        promises.push(showListElements(findHeadersList(header).find('> li')));
                    }
                });

                return $.when.apply(this, promises);
            }

            function setApi() {
                mainUl.data('collapsibleList', {
                    collapseAll: function() {
                    	collapseAllHeaders();
                    },
                    
                    expandAll: expandAllHeaders,
                    
                    expandToElement: function(elem) {
                        // expand our parent so we are visible (along with our siblings)
                        showListElements(elem.parent().children());
                        // mark our parent as expanded
                        elem.parent().prev().removeClass('collapsed');
                        // in case we are a parent, ensure our children are shown
                        //showListElements(elem.children());
                        //elem.removeClass('collapsed');
                        expandHeader(elem);
                    },
                    
                    expandToElementListScope: function(elem, listScope) {
                    	var scope = '';
                    	var i;
                    	var comma = '';
                    	for (i = 0; i < listScope.length; i++) {
                    		scope += comma + '.classscope' + listScope[i];
                    		comma = ', ';
                    	}
                        // expand our parent so we are visible (along with our siblings)
                        showListElements(elem.parent().children(scope));
                        
                        // mark our parent as expanded
                        elem.parent().prev().removeClass('collapsed');
                        
                        // in case we are a parent, ensure our children are shown                        
                        showListElements(elem.next().children(scope));
                        elem.removeClass('collapsed');
                        
                    },
                    
                    collapseElement: function(elem) {
                    	collapseHeader(elem);
					},
					
                });
            }

            function init() {
                allElements = mainUl.find('> ul > li, ol > li');
                headers = getHeaders();

                setHeadersClickHandler();
                setApi();
                if (options.search !== false) {
                    setSearchField();
                }
            }

            init();
        });
    };
})(jQuery);