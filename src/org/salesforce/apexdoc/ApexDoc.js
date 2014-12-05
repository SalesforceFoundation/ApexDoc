    // page init function
    $(function () {        
        var cl = $('#mynavbar').collapsibleList('.header', {search: false, animate: false});
        // start with navbar all collapsed.  couldn't figure out how to call collapseAllHeaders(),
        // but submitting the click on all li's does the trick!
        $('li',cl).trigger('click');
        var clist = $('#mynavbar').data('collapsibleList');
        //clist.collapseAll();  this didn't seem to set the collapsed class correctly.

        // get just the filename without extension from the url
        var i = location.pathname.lastIndexOf("/");
        var filename = location.pathname.substring(i + 1, location.pathname.length - 5);

        // select the filename in the list
        node = document.getElementById('idMenu' + filename);
        if (node != null) {
            node.classList.add('nav-selected');
            var li = $('#idMenu'+filename);
            clist.expandToElement(li);
        }
                
    });
