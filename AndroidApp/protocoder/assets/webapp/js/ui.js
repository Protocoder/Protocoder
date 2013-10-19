function initUI() { 

	var pstyle = 'border: 0px solid #dfdfdf; padding: 0px;';

	$('#toolbar').w2toolbar({
	        name: 'toolbar',
	        items: [
	        	/*
	            { type: 'button',  id: 'logo', caption: '', img: 'icon-page' },
				*/ 

	            { type: 'button',  id: 'project_new', caption: 'New Project', img: 'icon-page' },
	            { type: 'break',  id: 'break0' },
	            { type: 'button',  id: 'project_save', caption: 'Save', img: 'icon-save'},
	            
	            { type: 'menu',   id: 'list_projects', caption: 'Projects', img: 'icon-folder', items: [
	          
	            ]},

	            { type: 'menu',   id: 'list_examples', caption: 'Examples', img: 'icon-folder', items: [
	          
	            ]},

   				

	            /*
	            { type: 'button',   id: 'reference', caption: 'Reference', img: 'icon-folder', items: [
	           
	            ]},
	            */

	            
	            { type: 'break', id: 'break1' },
	            { type: 'button',  id: 'project_run',  caption: 'Run', img: 'icon-run', hint: 'Run project' },

	            { type: 'break', id: 'break1' },

	            /*
	            { type: 'button',   id: 'settings', caption: 'Settings', img: 'icon-folder', items: [
	           
	            ]},
	            */

	            { type: 'spacer' }

	        ],
	        onClick: function (target, data) {
	            //this.owner.content('main', target);
	            //console.log(data);
	            
	            switch(target) { 
	            case 'project_new':
	                    openPopup();
	                    break;
	            case 'project_run':
	                if (currentProject.name != 'undefined') { 

	                    currentProject.code = session.getValue();
	                    push_code(currentProject);
	                    removeWidgets();
	                    run_app(currentProject);
	                } else { 
	                    openPopup();
	                }
	                
	                break;
	                
	            case 'project_save':
	                if (currentProject.length != 'undefined') { 

	                    currentProject.code = session.getValue();
	                    push_code(currentProject);
	                } else { 
	                    openPopup();
	                    
	                }

	                break;
	           case 'list_projects':
	                if (data.subItem != 'undefined') { 
	                    fetch_code(data.subItem, target);
	                }

	             
	             //$("").w2overlay('This is an overlay.<br>Can be multi line HTML.');
	                break;
	            case 'list_examples':
	                if (data.subItem != 'undefined') { 
	                    fetch_code(data.subItem, target);  
	                }
	                break;
	            case 'dashboard':
	                    break;
	            default: break;
	        }
	        }
	            
	});

	$('#layout').w2layout({
	    name: 'layout',
	    panels: [
	        { type: 'main', style: pstyle, content: 'main' },
	        { type: 'right', size: 300, style: pstyle, content: 'right', resizable: true, hidden: false}

	    ]
	});

	//w2ui['layout'].hide('right', false);


	$().w2layout({
	    name: 'code_editor',
	    panels: [
	       { type: 'main', style: pstyle, content: 'main', tabs: {
	            active: 'tab1',
	            name: 'tabs',
	            tabs: [
	                { id: 'tab1', caption: 'Code' },
	            ],
	            onClick: function (target, data) {
	            
	                //this.owner.content('main', target);
	            }
	        } },
	        { type: 'bottom', size: "100px", resizable: true, hidden: false, style: pstyle, content: '<div id = "console_wrapper"> <div id = "console"></div> </div>' }
	    ]
	});

	$().w2layout({
	    name: 'right_bar',
	    panels: [
	        { type: 'main', size: '70%', resizable: true, style: pstyle, content: '<div id = "reference_container" style="background:#dfdfdf"> <div id = "reference"><h1 style="color:#777777; font-size:1.25em; margin-bottom: 24px; font-style:normal; font-weight:500; text-shadow: 2px 2px #eeeeee; vertical-align:middle;">REFERENCE</h1></div> </div>' },
	        { type: 'bottom', size: '30%', resizable: true, hidden: false, style: pstyle, content: 'bottom', 
	      	/* TODO add toolbar */ 
	        /*
	        	toolbar: {
				items: [
					{ type: 'button',  id: 'upload',  caption: 'Upload', img: 'icon-save', hint: 'Hint for item 5' },
					{ type: 'button',  id: 'rename',  caption: 'Rename', img: 'icon-save', hint: 'Hint for item 5' },
					{ type: 'button',  id: 'delete',  caption: 'Delete', img: 'icon-save', hint: 'Hint for item 5' }

				],
				onClick: function (target, data) {
					this.owner.content('main', target);
				}
			}
			*/
		}
	    ]
	});

	var gridRendered = false; 
	$('#grid').w2grid({ 
	    name: 'grid', 
	    url: '',
	    onRender: function() { 
	        gridRendered = true;
	    },
	    columns: [              
	        { field: 'file_name', caption: 'File Name', size: '70%' },
	        { field: 'file_size', caption: 'File Size', size: '30%' },
	    ]
	});


	w2ui['right_bar'].content('bottom', w2ui['grid']);
	w2ui['layout'].content('main', w2ui['code_editor']);
	w2ui['layout'].content('right', w2ui['right_bar']);
	w2ui['code_editor'].content('main', '<pre id="editor"></pre>');

	function openPopup () {
	    $().w2form({
	        name: 'foo',
	        style: 'border: 0px; background-color: transparent;',
	        formHTML: 
	            '<div class="w2ui-page page-0">'+
	            '   <div class="w2ui-label"> Name:</div>'+
	            '   <div class="w2ui-field">'+
	            '       <input name="project_name" type="text" size="35"/>'+
	            '   </div>'+
	            '</div>'+
	            '<div class="w2ui-buttons">'+
	            '   <input type="button" value="OK" name="save">'+
	            '   <input type="button" value="Cancel" name="cancel">'+
	            '</div>',
	        fields: [
	            { name: 'project_name', type: 'text', required: true },
	        ],
	        record: { 
	            first_name  : ':)',
	        },
	        actions: {
	            "save": function () { 
	                create_new_project( $("input#project_name").val() );
	                $().w2popup('close'); 
	            },
	            "cancel": function () { 
	                this.clear();  
	                $().w2popup('close'); 
	            },
	        }
	    });
	    $().w2popup('open', {
	        title   : 'Create a new project',
	        body    : '<div id="form" style="width: 100%; height: 100%;"></div>',
	        style   : 'padding: 15px 0px 0px 0px',
	        width   : 400, 
	        height  : 200, 
	        onOpen  : function () {
	            $('#w2ui-popup #form').w2render('foo');
	        }
	    });
	}

		
	// Prevent the backspace key from navigating back.
	$(document).unbind('keydown').bind('keydown', function (event) {
	    var doPrevent = false;
	    if (event.keyCode === 8) {
	        var d = event.srcElement || event.target;
	        if ((d.tagName.toUpperCase() === 'INPUT' && (d.type.toUpperCase() === 'TEXT' || d.type.toUpperCase() === 'PASSWORD' || d.type.toUpperCase() === 'FILE')) 
	             || d.tagName.toUpperCase() === 'TEXTAREA') {
	            doPrevent = d.readOnly || d.disabled;
	        }
	        else {
	            doPrevent = true;
	        }
	    }

	    if (doPrevent) {
	        event.preventDefault();
	    }
	});


	/* 
	* Binding UI 
	*/
	var overlayShow = false;
	$("#overlay #toggle").click(function() { 
		if (overlayShow) {
			hideDashboard();
		} else {
			showDashboard();
		}

		overlayShow ^= true;
	});
	$("#overlay #container #project_list").draggable();
	$("#overlay #container #example_list").draggable();

	$("#overlay #container #device_status").draggable();
	$("#overlay #container #help").draggable();
	$("#overlay #container #connection").draggable();
	$("#error_from_android").draggable();
	$("#remote_log").draggable();

	if (location.hash.indexOf("#dashboard") != -1) {
		showDashboard();
	}
	
	setTimeout(function() { 
	    $("#grid_grid_records").on('dragenter', function(e) {
	        console.log(e.target);

	        if (dropboxEnabled == false) {
	            initUpload();
	            e.preventDefault();
	            e.stopPropagation();
	        }
	    });

	    $("#grid_grid_records").on('dragover', function(e) {
	            e.preventDefault();
	            e.stopPropagation();
	    });

	      $("#grid_grid_records").on('drop', function(e) {
	            e.preventDefault();
	            e.stopPropagation();
	    });

	    

	     $("#grid_grid_records").on('dragend', function(e) {
	        console.log(e.target); 
	        dropboxEnabled = false;
	      	$("#dropbox").remove();
	        //initUpload();
	        e.preventDefault();
	        e.stopPropagation();
	    });
 	}, 2000);

}

function hideDashboard() { 
	$("#overlay #container").fadeOut(200);
} 

function showDashboard() { 
	$("#overlay #container").fadeIn(300);
}


