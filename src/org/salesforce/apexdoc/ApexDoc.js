    // global variables
    var listAllScope = ["global", "public", "private", "testMethod", "webService"];
    
    // page init function
    $(function () {  
    	readScopeCookie();
    	
    	hideAllScopes();
    	
    	showScopes();
    	      		
		// start with all properties and methods collapsed
    	//$('.toggle_container').hide();
    	// decided to try default expanded 

		// set the click handler for the methods
    	$('h2.trigger').click(function() {
            $(this).toggleClass('active').next().slideToggle('fast');
            ToggleBtnLabel(this.firstChild);
            return false;
    	});

    });
    
    $(document).ready(function() {
    
    
    });
    
    function expandListToClass() {
        var cl = $('#mynavbar').collapsibleList('.header', {search: false, animate: false});
        var clist = $('#mynavbar').data('collapsibleList');
        clist.collapseAll();  

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
    	if (strScope != null) {
    		
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

    function gotomenu(url) {
		document.location.href = url;
    }
    
    function ToggleBtnLabel(ctrl) {
		ctrl.value = (ctrl.value=='+' ? '-' : '+');
    }
    
    function IsExpanded(ctrl) {
		return (ctrl.value == '-');
    }
            
    function ToggleAll() {
		var cExpanded = 0;
        $('h2.trigger').each(function() {
        	if (!IsExpanded(this.firstChild)) {
				$(this).toggleClass('active').next().slideToggle('fast');
                ToggleBtnLabel(this.firstChild);
                cExpanded++;
             }
        });
        if (cExpanded == 0) {
        	$('h2.trigger').each(function() { 
            	$(this).toggleClass('active').next().slideToggle('fast');
                ToggleBtnLabel(this.firstChild);
            });
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
    
