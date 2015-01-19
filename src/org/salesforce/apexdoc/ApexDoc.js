    // global variables
    var listAllScope = ["global", "public", "private", "testMethod", "webService"];
    
    // document ready function
    $(function () {  
    	readScopeCookie();
    	
    	hideAllScopes();
    	
    	showScopes();
    	      		
    });
    
    function expandListToClass(liClass) {
        var cl = $('#mynavbar').collapsibleList('.header', {search: false, animate: false});
        var clist = $('#mynavbar').data('collapsibleList');
        clist.collapseAll();  
        $('.nav-selected').each(function() { $(this).removeClass('nav-selected'); });

        if (liClass != null) {
        	// liClass is a jquery object.
            liClass.addClass('nav-selected');
            clist.expandToElementListScope(liClass, getListScope());        
        } else {
	        // get just the filename without extension from the url
	        var i = location.pathname.lastIndexOf("/");
	        var filename = location.pathname.substring(i + 1, location.pathname.length - 5);
	        // select the filename in the list
	        node = document.getElementById('idMenu' + filename);
	        if (node != null) {
	            node.classList.add('nav-selected');
	            var li = $('#idMenu'+filename);
	            clist.expandToElementListScope(li, getListScope());
	        }
	    }    
    }  
    
    function getListScope() {
    	var list = [];
    	$('input:checkbox').each(function(index, elem) {
    		if (elem.checked) {
    			var str = elem.id;
    			str = str.replace('cbx', '');
    			list.push(str);
    		}
    	});
    	return list;
    }
    
    function showScopes() {
    	var list = getListScope();
    	for (var i = 0; i < list.length; i++) {
    		ToggleScope(list[i], true);
    	}
    }
    
    function showAllScopes() {
    	for (var i = 0; i < listAllScope.length; i++) {
    		ToggleScope(listAllScope[i], true);
    	}    
    }

    function hideAllScopes() {
    	for (var i = 0; i < listAllScope.length; i++) {
    		ToggleScope(listAllScope[i], false);
    	}    
    }
    
    function setScopeCookie() {
    	var list = getListScope();
    	var strScope = '';
    	var comma = '';
    	for (var i = 0; i < list.length; i++) {
    		strScope += comma + list[i];
    		comma = ',';
    	}
    	document.cookie = 'scope=' + strScope + '; path=/';
    }
    
    function readScopeCookie() {
    	var strScope = getCookie('scope');
    	if (strScope != null && strScope != '') {
    		
    		// first clear all the scope checkboxes
    		$('input:checkbox').each(function(index, elem) {
				elem.checked = false;
    		});
    		
    		// now check the appropriate scope checkboxes
		    var list = strScope.split(',');
		    for (var i = 0; i < list.length; i++) {
		    	var id = 'cbx' + list[i];
				$('#' + id).prop('checked', true);
		    }
		} else {
			showAllScopes();
		}    
    }

	function getCookie(cname) {
	    var name = cname + "=";
	    var ca = document.cookie.split(';');
	    for(var i=0; i<ca.length; i++) {
	        var c = ca[i];
	        while (c.charAt(0)==' ') c = c.substring(1);
	        if (c.indexOf(name) == 0) return c.substring(name.length, c.length);
	    }
	    return "";
	}

    function gotomenu(url, event) {
    	if (document.location.href.toLowerCase().indexOf(url.toLowerCase()) == -1)
			document.location.href = url;
		else {
	        var clist = $('#mynavbar').data('collapsibleList');
			var filename = url.replace('.html', '');
            //var li = $('#idMenu'+filename);
            var li = $(event.currentTarget.parentNode);
            var isCollapsed = li.hasClass('collapsed');
            if (isCollapsed) {
        	    expandListToClass(li);
        	    event.stopImmediatePropagation();
			} else {
				clist.collapseElement(li);
				event.stopImmediatePropagation();		
			}
		}
    }
        
    function ToggleScope(scope, isShow) {
    	setScopeCookie();
    	if (isShow == true) {
	    	// show all properties of the given scope
	    	$('.propertyscope' + scope).show();

	    	// show all methods of the given scope
	    	$('.methodscope' + scope).show();
	    	
			// redisplay the class list
			expandListToClass();						
		} else {
	    	// hide all properties of the given scope
	    	$('.propertyscope' + scope).hide();

	    	// hide all methods of the given scope
	    	$('.methodscope' + scope).hide();
	    	
	    	// hide all classes of the given scope
	    	$('.classscope' + scope).hide();
		}    	
    }                
    
