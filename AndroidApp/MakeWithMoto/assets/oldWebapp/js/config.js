
window._skel_config = {
	prefix: 'css/style',
	resetCSS: true,
	useOrientation: true,
	breakpoints: {
		'mobile': {
			range: '-640',
			lockViewport: true,
			containers: 'fluid',
			grid: {
				collapse: true
			}
		},
		'desktop': {
			range: '641-',
			containers: 1200
		},
		'wide': {
			range: '1201-'
		},
		'narrow': {
			range: '641-1200',
			containers: 960
		},
		'narrower': {
			range: '641-1000'
		}
	}
};

window._skel_panels_config = {
	panels: {
		sidePanel: {
			breakpoints: 'mobile',
			position: 'left',
			style: 'reveal',
			size: '250px',
			html: '<div data-action="moveElement" data-args="sidebar"></div>'			
		},
		sidePanelNarrower: {
			breakpoints: 'narrower',
			position: 'left',
			style: 'reveal',
			size: '300px',
			html: '<div data-action="moveElement" data-args="sidebar"></div>'			
		}
	},
	overlays: {
		titleBar: {
			breakpoints: 'mobile',
			position: 'top-left',
			width: '100%',
			height: 44,
			html: '<div class="toggle " data-action="panelToggle" data-args="sidePanel"></div>' +
				  '<div class="title" data-action="copyHTML" data-args="logo"></div>'
		},
		titleBarNarrower: {
			breakpoints: 'narrower',
			position: 'top-left',
			width: '100%',
			height: 60,
			html: '<div class="toggle " data-action="panelToggle" data-args="sidePanelNarrower"></div>' +
				  '<div class="title" data-action="copyHTML" data-args="logo"></div>'
		}
	}
};