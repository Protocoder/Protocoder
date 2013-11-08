/*
Supersized - Fullscreen Slideshow jQuery Plugin
Version : Core 3.2.1
Site	: www.buildinternet.com/project/supersized
Author	: Sam Dunn
Company : One Mighty Roar (www.onemightyroar.com)
License : MIT License / GPL License
*/
(function(a){a(document).ready(function(){a("body").append('<div id="supersized-loader"></div><div id="supersized"></div>')});a.supersized=function(b){var c="#supersized",d=this;d.$el=a(c);d.el=c;vars=a.supersized.vars;d.$el.data("supersized",d);api=d.$el.data("supersized");d.init=function(){a.supersized.vars.options=a.extend({},a.supersized.defaultOptions,b);d.options=a.supersized.vars.options;d._build()};d._build=function(){d._start()};d._start=function(){if(d.options.start_slide){vars.current_slide=d.options.start_slide-1}else{vars.current_slide=Math.floor(Math.random()*d.options.slides.length)}var f=d.options.new_window?' target="_blank"':"";imageLink=(api.getField("url"))?"href='"+api.getField("url")+"'":"";var e=a('<img src="'+api.getField("image")+'"/>');e.appendTo(d.el).wrap('<a class="image-loading activeslide" '+imageLink+f+"></a>").css("visibility","hidden");e.load(function(){d._origDim(a(this));d.resizeNow();d.launch()});d.$el.css("visibility","hidden")};d.launch=function(){d.$el.css("visibility","visible");a("#supersized-loader").hide();a(window).resize(function(){d.resizeNow()})};d.resizeNow=function(){return d.$el.each(function(){a("img",d.el).each(function(){thisSlide=a(this);var f=(thisSlide.data("origHeight")/thisSlide.data("origWidth")).toFixed(2);var e=d.$el.width(),h=d.$el.height(),i;if(d.options.fit_always){if((h/e)>f){g()}else{j()}}else{if((h<=d.options.min_height)&&(e<=d.options.min_width)){if((h/e)>f){d.options.fit_landscape&&f<1?g(true):j(true)}else{d.options.fit_portrait&&f>=1?j(true):g(true)}}else{if(e<=d.options.min_width){if((h/e)>f){d.options.fit_landscape&&f<1?g(true):j()}else{d.options.fit_portrait&&f>=1?j():g(true)}}else{if(h<=d.options.min_height){if((h/e)>f){d.options.fit_landscape&&f<1?g():j(true)}else{d.options.fit_portrait&&f>=1?j(true):g()}}else{if((h/e)>f){d.options.fit_landscape&&f<1?g():j()}else{d.options.fit_portrait&&f>=1?j():g()}}}}}function g(k){if(k){if(thisSlide.width()<e||thisSlide.width()<d.options.min_width){if(thisSlide.width()*f>=d.options.min_height){thisSlide.width(d.options.min_width);thisSlide.height(thisSlide.width()*f)}else{j()}}}else{if(d.options.min_height>=h&&!d.options.fit_landscape){if(e*f>=d.options.min_height||(e*f>=d.options.min_height&&f<=1)){thisSlide.width(e);thisSlide.height(e*f)}else{if(f>1){thisSlide.height(d.options.min_height);thisSlide.width(thisSlide.height()/f)}else{if(thisSlide.width()<e){thisSlide.width(e);thisSlide.height(thisSlide.width()*f)}}}}else{thisSlide.width(e);thisSlide.height(e*f)}}}function j(k){if(k){if(thisSlide.height()<h){if(thisSlide.height()/f>=d.options.min_width){thisSlide.height(d.options.min_height);thisSlide.width(thisSlide.height()/f)}else{g(true)}}}else{if(d.options.min_width>=e){if(h/f>=d.options.min_width||f>1){thisSlide.height(h);thisSlide.width(h/f)}else{if(f<=1){thisSlide.width(d.options.min_width);thisSlide.height(thisSlide.width()*f)}}}else{thisSlide.height(h);thisSlide.width(h/f)}}}if(thisSlide.parent().hasClass("image-loading")){a(".image-loading").removeClass("image-loading")}if(d.options.horizontal_center){a(this).css("left",(e-a(this).width())/2)}if(d.options.vertical_center){a(this).css("top",(h-a(this).height())/2)}});if(d.options.image_protect){a("img",d.el).bind("contextmenu mousedown",function(){return false})}return false})};d._origDim=function(e){e.data("origWidth",e.width()).data("origHeight",e.height()).css("visibility","visible")};d.getField=function(e){return d.options.slides[vars.current_slide][e]};d.init()};a.supersized.vars={current_slide:0,options:{}};a.supersized.defaultOptions={start_slide:1,new_window:1,image_protect:1,min_width:0,min_height:0,vertical_center:1,horizontal_center:1,fit_always:0,fit_portrait:1,fit_landscape:0};a.fn.supersized=function(b){return this.each(function(){(new a.supersized(b))})}})(jQuery);/*! jQuery JSON plugin 2.4.0 | code.google.com/p/jquery-json */
(function($){'use strict';var escape=/["\\\x00-\x1f\x7f-\x9f]/g,meta={'\b':'\\b','\t':'\\t','\n':'\\n','\f':'\\f','\r':'\\r','"':'\\"','\\':'\\\\'},hasOwn=Object.prototype.hasOwnProperty;$.toJSON=typeof JSON==='object'&&JSON.stringify?JSON.stringify:function(o){if(o===null){return'null';}
var pairs,k,name,val,type=$.type(o);if(type==='undefined'){return undefined;}
if(type==='number'||type==='boolean'){return String(o);}
if(type==='string'){return $.quoteString(o);}
if(typeof o.toJSON==='function'){return $.toJSON(o.toJSON());}
if(type==='date'){var month=o.getUTCMonth()+1,day=o.getUTCDate(),year=o.getUTCFullYear(),hours=o.getUTCHours(),minutes=o.getUTCMinutes(),seconds=o.getUTCSeconds(),milli=o.getUTCMilliseconds();if(month<10){month='0'+month;}
if(day<10){day='0'+day;}
if(hours<10){hours='0'+hours;}
if(minutes<10){minutes='0'+minutes;}
if(seconds<10){seconds='0'+seconds;}
if(milli<100){milli='0'+milli;}
if(milli<10){milli='0'+milli;}
return'"'+year+'-'+month+'-'+day+'T'+
hours+':'+minutes+':'+seconds+'.'+milli+'Z"';}
pairs=[];if($.isArray(o)){for(k=0;k<o.length;k++){pairs.push($.toJSON(o[k])||'null');}
return'['+pairs.join(',')+']';}
if(typeof o==='object'){for(k in o){if(hasOwn.call(o,k)){type=typeof k;if(type==='number'){name='"'+k+'"';}else if(type==='string'){name=$.quoteString(k);}else{continue;}
type=typeof o[k];if(type!=='function'&&type!=='undefined'){val=$.toJSON(o[k]);pairs.push(name+':'+val);}}}
return'{'+pairs.join(',')+'}';}};$.evalJSON=typeof JSON==='object'&&JSON.parse?JSON.parse:function(str){return eval('('+str+')');};$.secureEvalJSON=typeof JSON==='object'&&JSON.parse?JSON.parse:function(str){var filtered=str.replace(/\\["\\\/bfnrtu]/g,'@').replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,']').replace(/(?:^|:|,)(?:\s*\[)+/g,'');if(/^[\],:{}\s]*$/.test(filtered)){return eval('('+str+')');}
throw new SyntaxError('Error parsing JSON, source is not valid.');};$.quoteString=function(str){if(str.match(escape)){return'"'+str.replace(escape,function(a){var c=meta[a];if(typeof c==='string'){return c;}
c=a.charCodeAt();return'\\u00'+Math.floor(c/16).toString(16)+(c%16).toString(16);})+'"';}
return'"'+str+'"';};}(jQuery));/*!
clippy-jquery: <http://github.com/jimmysawczuk/clippy-jquery>
(c) 2011-2012; MIT License, see README.md for full license information and acknowledgements
*/
(function($)
{
var _opts = { // default options
'width': 14,
'height': 14,
'clippy_path': 'clippy.swf',
'keep_text': false,
'force_load' : false,
'flashvars'  : {}
};
$.fn.clippy = function(opts) 
{
opts = $.extend(_opts, opts);
var hasFlash = false;
try
{
var fo = new ActiveXObject('ShockwaveFlash.ShockwaveFlash');
if (fo)
{
hasFlash = true;
}
}
catch(e)
{
if (navigator.mimeTypes ["application/x-shockwave-flash"] != undefined)
{
hasFlash = true;
}
}
// if browser has Flash support or manual override set...
if (hasFlash || opts.force_load) 
{ 
// for every element matched...
$.each($(this), function(idx, val)
{
var text = "";
if (typeof opts.text != "undefined")
{
text = opts.text;
}
else if ($(val).data('text') && $.trim($(val).data('text')) != '')
{
text = $(val).data('text');
}
else
{
text = $.trim($(val).text());
}
// text should be URI-encoded, per https://github.com/mojombo/clippy/pull/9
text = encodeURIComponent(text);
var id = "";
if (typeof $(val).attr('id') === "undefined" || $.trim($(val).attr('id')) === "")
{
var id_suffix = Math.round(Math.random() * 10240).toString(16);
id = 'clippy-' + id_suffix;
$(val).attr('id', id);
}
else
{
id = $(val).attr('id');
}
if (!opts.keep_text)
{
$(val).html('');
}
var flashvars = $.extend({}, opts.flashvars, {text: text});
swfobject.embedSWF(opts.clippy_path, id, opts.width, opts.height, 
'10', false, flashvars, {scale: "noscale"});
});
}
else
{
// hide all the clippies so unwanted text is not displayed when Flash is not supported
$.each(this, function(idx, val)
{
$(val).css({'display': 'none'});
});
}
};
})(jQuery);/*!    SWFObject v2.3.20120118 <http://github.com/swfobject/swfobject>
is released under the MIT License <http://www.opensource.org/licenses/mit-license.php>
*/
var swfobject = function() {
var UNDEF = "undefined",
OBJECT = "object",
SHOCKWAVE_FLASH = "Shockwave Flash",
SHOCKWAVE_FLASH_AX = "ShockwaveFlash.ShockwaveFlash",
FLASH_MIME_TYPE = "application/x-shockwave-flash",
EXPRESS_INSTALL_ID = "SWFObjectExprInst",
ON_READY_STATE_CHANGE = "onreadystatechange",
win = window,
doc = document,
nav = navigator,
plugin = false,
domLoadFnArr = [],
regObjArr = [],
objIdArr = [],
listenersArr = [],
storedFbContent,
storedFbContentId,
storedCallbackFn,
storedCallbackObj,
isDomLoaded = false,
isExpressInstallActive = false,
dynamicStylesheet,
dynamicStylesheetMedia,
autoHideShow = true,
encodeURI_enabled = false,
/* Centralized function for browser feature detection
- User agent string detection is only used when no good alternative is possible
- Is executed directly for optimal performance
*/
ua = function() {
var w3cdom = typeof doc.getElementById != UNDEF && typeof doc.getElementsByTagName != UNDEF && typeof doc.createElement != UNDEF,
u = nav.userAgent.toLowerCase(),
p = nav.platform.toLowerCase(),
windows = p ? /win/.test(p) : /win/.test(u),
mac = p ? /mac/.test(p) : /mac/.test(u),
webkit = /webkit/.test(u) ? parseFloat(u.replace(/^.*webkit\/(\d+(\.\d+)?).*$/, "$1")) : false, // returns either the webkit version or false if not webkit
ie = nav.appName === "Microsoft Internet Explorer",
playerVersion = [0,0,0],
d = null;
if (typeof nav.plugins != UNDEF && typeof nav.plugins[SHOCKWAVE_FLASH] == OBJECT) {
d = nav.plugins[SHOCKWAVE_FLASH].description;
// nav.mimeTypes["application/x-shockwave-flash"].enabledPlugin indicates whether plug-ins are enabled or disabled in Safari 3+
if (d && (typeof nav.mimeTypes != UNDEF && nav.mimeTypes[FLASH_MIME_TYPE] && nav.mimeTypes[FLASH_MIME_TYPE].enabledPlugin)){
plugin = true;
ie = false; // cascaded feature detection for Internet Explorer
d = d.replace(/^.*\s+(\S+\s+\S+$)/, "$1");
playerVersion[0] = toInt(d.replace(/^(.*)\..*$/, "$1"));
playerVersion[1] = toInt(d.replace(/^.*\.(.*)\s.*$/, "$1"));
playerVersion[2] = /[a-zA-Z]/.test(d) ? toInt(d.replace(/^.*[a-zA-Z]+(.*)$/, "$1")) : 0;
}
}
else if (typeof win.ActiveXObject != UNDEF) {
try {
var a = new ActiveXObject(SHOCKWAVE_FLASH_AX);
if (a) { // a will return null when ActiveX is disabled
d = a.GetVariable("$version");
if (d) {
ie = true; // cascaded feature detection for Internet Explorer
d = d.split(" ")[1].split(",");
playerVersion = [toInt(d[0]), toInt(d[1]), toInt(d[2])];
}
}
}
catch(e) {}
}
return { w3:w3cdom, pv:playerVersion, wk:webkit, ie:ie, win:windows, mac:mac };
}(),
/* Cross-browser onDomLoad
- Will fire an event as soon as the DOM of a web page is loaded
- Internet Explorer workaround based on Diego Perini's solution: http://javascript.nwbox.com/IEContentLoaded/
- Regular onload serves as fallback
*/
onDomLoad = function() {
if (!ua.w3) { return; }
if ((typeof doc.readyState != UNDEF && doc.readyState == "complete") || (typeof doc.readyState == UNDEF && (doc.getElementsByTagName("body")[0] || doc.body))) { // function is fired after onload, e.g. when script is inserted dynamically
callDomLoadFunctions();
}
if (!isDomLoaded) {
if (typeof doc.addEventListener != UNDEF) {
doc.addEventListener("DOMContentLoaded", callDomLoadFunctions, false);
}
if (ua.ie) {
doc.attachEvent(ON_READY_STATE_CHANGE, function detach() {
if (doc.readyState == "complete") {
doc.detachEvent(ON_READY_STATE_CHANGE, detach);
callDomLoadFunctions();
}
});
if (win == top) { // if not inside an iframe
(function checkDomLoadedIE(){
if (isDomLoaded) { return; }
try {
doc.documentElement.doScroll("left");
}
catch(e) {
setTimeout(checkDomLoadedIE, 0);
return;
}
callDomLoadFunctions();
}());
}
}
if (ua.wk) {
(function checkDomLoadedWK(){
if (isDomLoaded) { return; }
if (!/loaded|complete/.test(doc.readyState)) {
setTimeout(checkDomLoadedWK, 0);
return;
}
callDomLoadFunctions();
}());
}
}
}();
function callDomLoadFunctions() {
if (isDomLoaded || !document.getElementsByTagName("body")[0]) { return; }
try { // test if we can really add/remove elements to/from the DOM; we don't want to fire it too early
var t, span = createElement("span");
span.style.display = "none"; //hide the span in case someone has styled spans via CSS
t = doc.getElementsByTagName("body")[0].appendChild(span);
t.parentNode.removeChild(t);
t = null; //clear the variables
span = null;
}
catch (e) { return; }
isDomLoaded = true;
var dl = domLoadFnArr.length;
for (var i = 0; i < dl; i++) {
domLoadFnArr[i]();
}
}
function addDomLoadEvent(fn) {
if (isDomLoaded) {
fn();
}
else {
domLoadFnArr[domLoadFnArr.length] = fn; // Array.push() is only available in IE5.5+
}
}
/* Cross-browser onload
- Based on James Edwards' solution: http://brothercake.com/site/resources/scripts/onload/
- Will fire an event as soon as a web page including all of its assets are loaded
*/
function addLoadEvent(fn) {
if (typeof win.addEventListener != UNDEF) {
win.addEventListener("load", fn, false);
}
else if (typeof doc.addEventListener != UNDEF) {
doc.addEventListener("load", fn, false);
}
else if (typeof win.attachEvent != UNDEF) {
addListener(win, "onload", fn);
}
else if (typeof win.onload == "function") {
var fnOld = win.onload;
win.onload = function() {
fnOld();
fn();
};
}
else {
win.onload = fn;
}
}
/* Detect the Flash Player version for non-Internet Explorer browsers
- Detecting the plug-in version via the object element is more precise than using the plugins collection item's description:
a. Both release and build numbers can be detected
b. Avoid wrong descriptions by corrupt installers provided by Adobe
c. Avoid wrong descriptions by multiple Flash Player entries in the plugin Array, caused by incorrect browser imports
- Disadvantage of this method is that it depends on the availability of the DOM, while the plugins collection is immediately available
*/
function testPlayerVersion() {
var b = doc.getElementsByTagName("body")[0];
var o = createElement(OBJECT);
o.setAttribute("style", "visibility: hidden;");
o.setAttribute("type", FLASH_MIME_TYPE);
var t = b.appendChild(o);
if (t) {
var counter = 0;
(function checkGetVariable(){
if (typeof t.GetVariable != UNDEF) {
try {
var d = t.GetVariable("$version");
if (d) {
d = d.split(" ")[1].split(",");
ua.pv = [toInt(d[0]), toInt(d[1]), toInt(d[2])];
}
} catch(e){
//t.GetVariable("$version") is known to fail in Flash Player 8 on Firefox
//If this error is encountered, assume FP8 or lower. Time to upgrade.
ua.pv = [8,0,0];
}
}
else if (counter < 10) {
counter++;
setTimeout(checkGetVariable, 10);
return;
}
b.removeChild(o);
t = null;
matchVersions();
}());
}
else {
matchVersions();
}
}
/* Perform Flash Player and SWF version matching; static publishing only
*/
function matchVersions() {
var rl = regObjArr.length;
if (rl > 0) {
for (var i = 0; i < rl; i++) { // for each registered object element
var id = regObjArr[i].id;
var cb = regObjArr[i].callbackFn;
var cbObj = {success:false, id:id};
if (ua.pv[0] > 0) {
var obj = getElementById(id);
if (obj) {
if (hasPlayerVersion(regObjArr[i].swfVersion) && !(ua.wk && ua.wk < 312)) { // Flash Player version >= published SWF version: Houston, we have a match!
setVisibility(id, true);
if (cb) {
cbObj.success = true;
cbObj.ref = getObjectById(id);
cbObj.id = id;
cb(cbObj);
}
}
else if (regObjArr[i].expressInstall && canExpressInstall()) { // show the Adobe Express Install dialog if set by the web page author and if supported
var att = {};
att.data = regObjArr[i].expressInstall;
att.width = obj.getAttribute("width") || "0";
att.height = obj.getAttribute("height") || "0";
if (obj.getAttribute("class")) { att.styleclass = obj.getAttribute("class"); }
if (obj.getAttribute("align")) { att.align = obj.getAttribute("align"); }
// parse HTML object param element's name-value pairs
var par = {};
var p = obj.getElementsByTagName("param");
var pl = p.length;
for (var j = 0; j < pl; j++) {
if (p[j].getAttribute("name").toLowerCase() != "movie") {
par[p[j].getAttribute("name")] = p[j].getAttribute("value");
}
}
showExpressInstall(att, par, id, cb);
}
else { // Flash Player and SWF version mismatch or an older Webkit engine that ignores the HTML object element's nested param elements: display fallback content instead of SWF
displayFbContent(obj);
if (cb) { cb(cbObj); }
}
}
}
else {    // if no Flash Player is installed or the fp version cannot be detected we let the HTML object element do its job (either show a SWF or fallback content)
setVisibility(id, true);
if (cb) {
var o = getObjectById(id); // test whether there is an HTML object element or not
if (o && typeof o.SetVariable != UNDEF) {
cbObj.success = true;
cbObj.ref = o;
cbObj.id = o.id;
}
cb(cbObj);
}
}
}
}
}
/* Main function
- Will preferably execute onDomLoad, otherwise onload (as a fallback)
*/
domLoadFnArr[0] = function (){
if (plugin) {
testPlayerVersion();
}
else {
matchVersions();
}
};
function getObjectById(objectIdStr) {
var r = null,
o = getElementById(objectIdStr);
if (o && o.nodeName.toUpperCase() === "OBJECT") {
//If targeted object is valid Flash file
if (typeof o.SetVariable !== UNDEF){
r = o;
} else {
//If SetVariable is not working on targeted object but a nested object is
//available, assume classic nested object markup. Return nested object.
//If SetVariable is not working on targeted object and there is no nested object,
//return the original object anyway. This is probably new simplified markup.
r = o.getElementsByTagName(OBJECT)[0] || o;
}
}
return r;
}
/* Requirements for Adobe Express Install
- only one instance can be active at a time
- fp 6.0.65 or higher
- Win/Mac OS only
- no Webkit engines older than version 312
*/
function canExpressInstall() {
return !isExpressInstallActive && hasPlayerVersion("6.0.65") && (ua.win || ua.mac) && !(ua.wk && ua.wk < 312);
}
/* Show the Adobe Express Install dialog
- Reference: http://www.adobe.com/cfusion/knowledgebase/index.cfm?id=6a253b75
*/
function showExpressInstall(att, par, replaceElemIdStr, callbackFn) {
var obj = getElementById(replaceElemIdStr);
//Ensure that replaceElemIdStr is really a string and not an element
replaceElemIdStr = getId(replaceElemIdStr);
isExpressInstallActive = true;
storedCallbackFn = callbackFn || null;
storedCallbackObj = {success:false, id:replaceElemIdStr};
if (obj) {
if (obj.nodeName.toUpperCase() == "OBJECT") { // static publishing
storedFbContent = abstractFbContent(obj);
storedFbContentId = null;
}
else { // dynamic publishing
storedFbContent = obj;
storedFbContentId = replaceElemIdStr;
}
att.id = EXPRESS_INSTALL_ID;
if (typeof att.width == UNDEF || (!/%$/.test(att.width) && toInt(att.width) < 310)) { att.width = "310"; }
if (typeof att.height == UNDEF || (!/%$/.test(att.height) && toInt(att.height) < 137)) { att.height = "137"; }
doc.title = doc.title.slice(0, 47) + " - Flash Player Installation";
var pt = ua.ie ? "ActiveX" : "PlugIn",
fv = "MMredirectURL=" + encodeURIComponent(win.location.toString().replace(/&/g,"%26")) + "&MMplayerType=" + pt + "&MMdoctitle=" + doc.title;
if (typeof par.flashvars != UNDEF) {
par.flashvars += "&" + fv;
}
else {
par.flashvars = fv;
}
// IE only: when a SWF is loading (AND: not available in cache) wait for the readyState of the object element to become 4 before removing it,
// because you cannot properly cancel a loading SWF file without breaking browser load references, also obj.onreadystatechange doesn't work
if (ua.ie && obj.readyState != 4) {
var newObj = createElement("div");
replaceElemIdStr += "SWFObjectNew";
newObj.setAttribute("id", replaceElemIdStr);
obj.parentNode.insertBefore(newObj, obj); // insert placeholder div that will be replaced by the object element that loads expressinstall.swf
obj.style.display = "none";
removeSWF(obj); //removeSWF accepts elements now
}
createSWF(att, par, replaceElemIdStr);
}
}
/* Functions to abstract and display fallback content
*/
function displayFbContent(obj) {
if (ua.ie && obj.readyState != 4) {
// IE only: when a SWF is loading (AND: not available in cache) wait for the readyState of the object element to become 4 before removing it,
// because you cannot properly cancel a loading SWF file without breaking browser load references, also obj.onreadystatechange doesn't work
obj.style.display = "none";
var el = createElement("div");
obj.parentNode.insertBefore(el, obj); // insert placeholder div that will be replaced by the fallback content
el.parentNode.replaceChild(abstractFbContent(obj), el);
removeSWF(obj); //removeSWF accepts elements now
}
else {
obj.parentNode.replaceChild(abstractFbContent(obj), obj);
}
}
function abstractFbContent(obj) {
var ac = createElement("div");
if (ua.win && ua.ie) {
ac.innerHTML = obj.innerHTML;
}
else {
var nestedObj = obj.getElementsByTagName(OBJECT)[0];
if (nestedObj) {
var c = nestedObj.childNodes;
if (c) {
var cl = c.length;
for (var i = 0; i < cl; i++) {
if (!(c[i].nodeType == 1 && c[i].nodeName == "PARAM") && !(c[i].nodeType == 8)) {
ac.appendChild(c[i].cloneNode(true));
}
}
}
}
}
return ac;
}
function createIeObject(url, param_str){
var div = createElement("div");
div.innerHTML = "<object classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000'><param name='movie' value='" +url + "'>" + param_str + "</object>";
return div.firstChild;
}
/* Cross-browser dynamic SWF creation
*/
function createSWF(attObj, parObj, id) {
var r, el = getElementById(id);
id = getId(id); // ensure id is truly an ID and not an element
if (ua.wk && ua.wk < 312) { return r; }
if (el) {
var o = (ua.ie) ? createElement("div") : createElement(OBJECT),
attr,
attr_lower,
param;
if (typeof attObj.id == UNDEF) { // if no 'id' is defined for the object element, it will inherit the 'id' from the fallback content
attObj.id = id;
}
//Add params
for (param in parObj) {
//filter out prototype additions from other potential libraries and IE specific param element
if (parObj.hasOwnProperty(param) && param.toLowerCase() !== "movie") {
createObjParam(o, param, parObj[param]);
}
}
//Create IE object, complete with param nodes
if(ua.ie){ o = createIeObject(attObj.data, o.innerHTML); }
//Add attributes to object
for (attr in attObj) {
if (attObj.hasOwnProperty(attr)) { // filter out prototype additions from other potential libraries
attr_lower = attr.toLowerCase();
// 'class' is an ECMA4 reserved keyword
if (attr_lower === "styleclass") {
o.setAttribute("class", attObj[attr]);
} else if (attr_lower !== "classid" && attr_lower !== "data") {
o.setAttribute(attr, attObj[attr]);
}
}
}
if (ua.ie) {
objIdArr[objIdArr.length] = attObj.id; // stored to fix object 'leaks' on unload (dynamic publishing only)
} else {
o.setAttribute("type", FLASH_MIME_TYPE);
o.setAttribute("data", attObj.data);
}
el.parentNode.replaceChild(o, el);
r = o;
}
return r;
}
function createObjParam(el, pName, pValue) {
var p = createElement("param");
p.setAttribute("name", pName);
p.setAttribute("value", pValue);
el.appendChild(p);
}
/* Cross-browser SWF removal
- Especially needed to safely and completely remove a SWF in Internet Explorer
*/
function removeSWF(id) {
var obj = getElementById(id);
if (obj && obj.nodeName.toUpperCase() == "OBJECT") {
if (ua.ie) {
obj.style.display = "none";
(function removeSWFInIE(){
if (obj.readyState == 4) {
//This step prevents memory leaks in Internet Explorer
for (var i in obj) {
if (typeof obj[i] == "function") {
obj[i] = null;
}
}
obj.parentNode.removeChild(obj);
} else {
setTimeout(removeSWFInIE, 10);
}
}());
}
else {
obj.parentNode.removeChild(obj);
}
}
}
function isElement(id){
return (id && id.nodeType && id.nodeType === 1);
}
function getId(thing){
return (isElement(thing)) ? thing.id : thing;
}
/* Functions to optimize JavaScript compression
*/
function getElementById(id) {
//Allow users to pass an element OR an element's ID
if(isElement(id)){ return id; }
var el = null;
try {
el = doc.getElementById(id);
}
catch (e) {}
return el;
}
function createElement(el) {
return doc.createElement(el);
}
//To aid compression; replaces 14 instances of pareseInt with radix
function toInt(str){
return parseInt(str, 10);
}
/* Updated attachEvent function for Internet Explorer
- Stores attachEvent information in an Array, so on unload the detachEvent functions can be called to avoid memory leaks
*/
function addListener(target, eventType, fn) {
target.attachEvent(eventType, fn);
listenersArr[listenersArr.length] = [target, eventType, fn];
}
/* Flash Player and SWF content version matching
*/
function hasPlayerVersion(rv) {
rv += ""; //Coerce number to string, if needed.
var pv = ua.pv, v = rv.split(".");
v[0] = toInt(v[0]);
v[1] = toInt(v[1]) || 0; // supports short notation, e.g. "9" instead of "9.0.0"
v[2] = toInt(v[2]) || 0;
return (pv[0] > v[0] || (pv[0] == v[0] && pv[1] > v[1]) || (pv[0] == v[0] && pv[1] == v[1] && pv[2] >= v[2])) ? true : false;
}
/* Cross-browser dynamic CSS creation
- Based on Bobby van der Sluis' solution: http://www.bobbyvandersluis.com/articles/dynamicCSS.php
*/
function createCSS(sel, decl, media, newStyle) {
var h = doc.getElementsByTagName("head")[0];
if (!h) { return; } // to also support badly authored HTML pages that lack a head element
var m = (typeof media == "string") ? media : "screen";
if (newStyle) {
dynamicStylesheet = null;
dynamicStylesheetMedia = null;
}
if (!dynamicStylesheet || dynamicStylesheetMedia != m) {
// create dynamic stylesheet + get a global reference to it
var s = createElement("style");
s.setAttribute("type", "text/css");
s.setAttribute("media", m);
dynamicStylesheet = h.appendChild(s);
if (ua.ie && typeof doc.styleSheets != UNDEF && doc.styleSheets.length > 0) {
dynamicStylesheet = doc.styleSheets[doc.styleSheets.length - 1];
}
dynamicStylesheetMedia = m;
}
// add style rule
if(dynamicStylesheet){
if (typeof dynamicStylesheet.addRule != UNDEF) {
dynamicStylesheet.addRule(sel, decl);
} else if (typeof doc.createTextNode != UNDEF) {
dynamicStylesheet.appendChild(doc.createTextNode(sel + " {" + decl + "}"));
}
}
}
function setVisibility(id, isVisible) {
if (!autoHideShow) { return; }
var v = isVisible ? "visible" : "hidden",
el = getElementById(id);
if (isDomLoaded && el) {
el.style.visibility = v;
} else if(typeof id === "string"){
createCSS("#" + id, "visibility:" + v);
}
}
/* Filter to avoid XSS attacks
*/
function urlEncodeIfNecessary(s) {
var regex = /[\\\"<>\.;]/;
var hasBadChars = regex.exec(s) != null;
return hasBadChars && typeof encodeURIComponent != UNDEF ? encodeURIComponent(s) : s;
}
/* Release memory to avoid memory leaks caused by closures, fix hanging audio/video threads and force open sockets/NetConnections to disconnect (Internet Explorer only)
*/
var cleanup = function() {
if (ua.ie) {
window.attachEvent("onunload", function() {
// remove listeners to avoid memory leaks
var ll = listenersArr.length;
for (var i = 0; i < ll; i++) {
listenersArr[i][0].detachEvent(listenersArr[i][1], listenersArr[i][2]);
}
// cleanup dynamically embedded objects to fix audio/video threads and force open sockets and NetConnections to disconnect
var il = objIdArr.length;
for (var j = 0; j < il; j++) {
removeSWF(objIdArr[j]);
}
// cleanup library's main closures to avoid memory leaks
for (var k in ua) {
ua[k] = null;
}
ua = null;
for (var l in swfobject) {
swfobject[l] = null;
}
swfobject = null;
});
}
}();
return {
/* Public API
- Reference: http://code.google.com/p/swfobject/wiki/documentation
*/
registerObject: function(objectIdStr, swfVersionStr, xiSwfUrlStr, callbackFn) {
if (ua.w3 && objectIdStr && swfVersionStr) {
var regObj = {};
regObj.id = objectIdStr;
regObj.swfVersion = swfVersionStr;
regObj.expressInstall = xiSwfUrlStr;
regObj.callbackFn = callbackFn;
regObjArr[regObjArr.length] = regObj;
setVisibility(objectIdStr, false);
}
else if (callbackFn) {
callbackFn({success:false, id:objectIdStr});
}
},
getObjectById: function(objectIdStr) {
if (ua.w3) {
return getObjectById(objectIdStr);
}
},
embedSWF: function(swfUrlStr, replaceElemIdStr, widthStr, heightStr, swfVersionStr, xiSwfUrlStr, flashvarsObj, parObj, attObj, callbackFn) {
var id = getId(replaceElemIdStr),
callbackObj = {success:false, id:id};
if (ua.w3 && !(ua.wk && ua.wk < 312) && swfUrlStr && replaceElemIdStr && widthStr && heightStr && swfVersionStr) {
setVisibility(id, false);
addDomLoadEvent(function() {
widthStr += ""; // auto-convert to string
heightStr += "";
var att = {};
if (attObj && typeof attObj === OBJECT) {
for (var i in attObj) { // copy object to avoid the use of references, because web authors often reuse attObj for multiple SWFs
att[i] = attObj[i];
}
}
att.data = swfUrlStr;
att.width = widthStr;
att.height = heightStr;
var par = {};
if (parObj && typeof parObj === OBJECT) {
for (var j in parObj) { // copy object to avoid the use of references, because web authors often reuse parObj for multiple SWFs
par[j] = parObj[j];
}
}
if (flashvarsObj && typeof flashvarsObj === OBJECT) {
for (var k in flashvarsObj) { // copy object to avoid the use of references, because web authors often reuse flashvarsObj for multiple SWFs
if(flashvarsObj.hasOwnProperty(k)){
var key = (encodeURI_enabled) ? encodeURIComponent(k) : k,
value = (encodeURI_enabled) ? encodeURIComponent(flashvarsObj[k]) : flashvarsObj[k];
if (typeof par.flashvars != UNDEF) {
par.flashvars += "&" + key + "=" + value;
}
else {
par.flashvars = key + "=" + value;
}
}
}
}
if (hasPlayerVersion(swfVersionStr)) { // create SWF
var obj = createSWF(att, par, replaceElemIdStr);
if (att.id == id) {
setVisibility(id, true);
}
callbackObj.success = true;
callbackObj.ref = obj;
callbackObj.id = obj.id;
}
else if (xiSwfUrlStr && canExpressInstall()) { // show Adobe Express Install
att.data = xiSwfUrlStr;
showExpressInstall(att, par, replaceElemIdStr, callbackFn);
return;
}
else { // show fallback content
setVisibility(id, true);
}
if (callbackFn) { callbackFn(callbackObj); }
});
}
else if (callbackFn) { callbackFn(callbackObj);    }
},
switchOffAutoHideShow: function() {
autoHideShow = false;
},
enableUriEncoding: function (bool) {
encodeURI_enabled = (typeof bool === UNDEF) ? true : bool;
},
ua: ua,
getFlashPlayerVersion: function() {
return { major:ua.pv[0], minor:ua.pv[1], release:ua.pv[2] };
},
hasFlashPlayerVersion: hasPlayerVersion,
createSWF: function(attObj, parObj, replaceElemIdStr) {
if (ua.w3) {
return createSWF(attObj, parObj, replaceElemIdStr);
}
else {
return undefined;
}
},
showExpressInstall: function(att, par, replaceElemIdStr, callbackFn) {
if (ua.w3 && canExpressInstall()) {
showExpressInstall(att, par, replaceElemIdStr, callbackFn);
}
},
removeSWF: function(objElemIdStr) {
if (ua.w3) {
removeSWF(objElemIdStr);
}
},
createCSS: function(selStr, declStr, mediaStr, newStyleBoolean) {
if (ua.w3) {
createCSS(selStr, declStr, mediaStr, newStyleBoolean);
}
},
addDomLoadEvent: addDomLoadEvent,
addLoadEvent: addLoadEvent,
getQueryParamValue: function(param) {
var q = doc.location.search || doc.location.hash;
if (q) {
if (/\?/.test(q)) { q = q.split("?")[1]; } // strip question mark
if (param == null) {
return urlEncodeIfNecessary(q);
}
var pairs = q.split("&");
for (var i = 0; i < pairs.length; i++) {
if (pairs[i].substring(0, pairs[i].indexOf("=")) == param) {
return urlEncodeIfNecessary(pairs[i].substring((pairs[i].indexOf("=") + 1)));
}
}
}
return "";
},
// For internal usage only
expressInstallCallback: function() {
if (isExpressInstallActive) {
var obj = getElementById(EXPRESS_INSTALL_ID);
if (obj && storedFbContent) {
obj.parentNode.replaceChild(storedFbContent, obj);
if (storedFbContentId) {
setVisibility(storedFbContentId, true);
if (ua.ie) { storedFbContent.style.display = "block"; }
}
if (storedCallbackFn) { storedCallbackFn(storedCallbackObj); }
}
isExpressInstallActive = false;
}
},
version: "2.3"
};
}();
/*
* Slides, A Slideshow Plugin for jQuery
* Intructions: http://slidesjs.com
* By: Nathan Searles, http://nathansearles.com
* Version: 1.1.9
* Updated: September 5th, 2011
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
(function(a){a.fn.slides=function(b){return b=a.extend({},a.fn.slides.option,b),this.each(function(){function w(g,h,i){if(!p&&o){p=!0,b.animationStart(n+1);switch(g){case"next":l=n,k=n+1,k=e===k?0:k,r=f*2,g=-f*2,n=k;break;case"prev":l=n,k=n-1,k=k===-1?e-1:k,r=0,g=0,n=k;break;case"pagination":k=parseInt(i,10),l=a("."+b.paginationClass+" li."+b.currentClass+" a",c).attr("href").match("[^#/]+$"),k>l?(r=f*2,g=-f*2):(r=0,g=0),n=k}h==="fade"?b.crossfade?d.children(":eq("+k+")",c).css({zIndex:10}).fadeIn(b.fadeSpeed,b.fadeEasing,function(){b.autoHeight?d.animate({height:d.children(":eq("+k+")",c).outerHeight()},b.autoHeightSpeed,function(){d.children(":eq("+l+")",c).css({display:"none",zIndex:0}),d.children(":eq("+k+")",c).css({zIndex:0}),b.animationComplete(k+1),p=!1}):(d.children(":eq("+l+")",c).css({display:"none",zIndex:0}),d.children(":eq("+k+")",c).css({zIndex:0}),b.animationComplete(k+1),p=!1)}):d.children(":eq("+l+")",c).fadeOut(b.fadeSpeed,b.fadeEasing,function(){b.autoHeight?d.animate({height:d.children(":eq("+k+")",c).outerHeight()},b.autoHeightSpeed,function(){d.children(":eq("+k+")",c).fadeIn(b.fadeSpeed,b.fadeEasing)}):d.children(":eq("+k+")",c).fadeIn(b.fadeSpeed,b.fadeEasing,function(){a.browser.msie&&a(this).get(0).style.removeAttribute("filter")}),b.animationComplete(k+1),p=!1}):(d.children(":eq("+k+")").css({left:r,display:"block"}),b.autoHeight?d.animate({left:g,height:d.children(":eq("+k+")").outerHeight()},b.slideSpeed,b.slideEasing,function(){d.css({left:-f}),d.children(":eq("+k+")").css({left:f,zIndex:5}),d.children(":eq("+l+")").css({left:f,display:"none",zIndex:0}),b.animationComplete(k+1),p=!1}):d.animate({left:g},b.slideSpeed,b.slideEasing,function(){d.css({left:-f}),d.children(":eq("+k+")").css({left:f,zIndex:5}),d.children(":eq("+l+")").css({left:f,display:"none",zIndex:0}),b.animationComplete(k+1),p=!1})),b.pagination&&(a("."+b.paginationClass+" li."+b.currentClass,c).removeClass(b.currentClass),a("."+b.paginationClass+" li:eq("+k+")",c).addClass(b.currentClass))}}function x(){clearInterval(c.data("interval"))}function y(){b.pause?(clearTimeout(c.data("pause")),clearInterval(c.data("interval")),u=setTimeout(function(){clearTimeout(c.data("pause")),v=setInterval(function(){w("next",i)},b.play),c.data("interval",v)},b.pause),c.data("pause",u)):x()}a("."+b.container,a(this)).children().wrapAll('<div class="slides_control"/>');var c=a(this),d=a(".slides_control",c),e=d.children().size(),f=d.children().outerWidth(),g=d.children().outerHeight(),h=b.start-1,i=b.effect.indexOf(",")<0?b.effect:b.effect.replace(" ","").split(",")[0],j=b.effect.indexOf(",")<0?i:b.effect.replace(" ","").split(",")[1],k=0,l=0,m=0,n=0,o,p,q,r,s,t,u,v;if(e<2)return a("."+b.container,a(this)).fadeIn(b.fadeSpeed,b.fadeEasing,function(){o=!0,b.slidesLoaded()}),a("."+b.next+", ."+b.prev).fadeOut(0),!1;if(e<2)return;h<0&&(h=0),h>e&&(h=e-1),b.start&&(n=h),b.randomize&&d.randomize(),a("."+b.container,c).css({overflow:"hidden",position:"relative"}),d.children().css({position:"absolute",top:0,left:d.children().outerWidth(),zIndex:0,display:"none"}),d.css({position:"relative",width:f*3,height:g,left:-f}),a("."+b.container,c).css({display:"block"}),b.autoHeight&&(d.children().css({height:"auto"}),d.animate({height:d.children(":eq("+h+")").outerHeight()},b.autoHeightSpeed));if(b.preload&&d.find("img:eq("+h+")").length){a("."+b.container,c).css({background:"url("+b.preloadImage+") no-repeat 50% 50%"});var z=d.find("img:eq("+h+")").attr("src")+"?"+(new Date).getTime();a("img",c).parent().attr("class")!="slides_control"?t=d.children(":eq(0)")[0].tagName.toLowerCase():t=d.find("img:eq("+h+")"),d.find("img:eq("+h+")").attr("src",z).load(function(){d.find(t+":eq("+h+")").fadeIn(b.fadeSpeed,b.fadeEasing,function(){a(this).css({zIndex:5}),a("."+b.container,c).css({background:""}),o=!0,b.slidesLoaded()})})}else d.children(":eq("+h+")").fadeIn(b.fadeSpeed,b.fadeEasing,function(){o=!0,b.slidesLoaded()});b.bigTarget&&(d.children().css({cursor:"pointer"}),d.children().click(function(){return w("next",i),!1})),b.hoverPause&&b.play&&(d.bind("mouseover",function(){x()}),d.bind("mouseleave",function(){y()})),b.generateNextPrev&&(a("."+b.container,c).after('<a href="#" class="'+b.prev+'">Prev</a>'),a("."+b.prev,c).after('<a href="#" class="'+b.next+'">Next</a>')),a("."+b.next,c).click(function(a){a.preventDefault(),b.play&&y(),w("next",i)}),a("."+b.prev,c).click(function(a){a.preventDefault(),b.play&&y(),w("prev",i)}),b.generatePagination?(b.prependPagination?c.prepend("<ul class="+b.paginationClass+"></ul>"):c.append("<ul class="+b.paginationClass+"></ul>"),d.children().each(function(){a("."+b.paginationClass,c).append('<li><a href="#'+m+'">'+(m+1)+"</a></li>"),m++})):a("."+b.paginationClass+" li a",c).each(function(){a(this).attr("href","#"+m),m++}),a("."+b.paginationClass+" li:eq("+h+")",c).addClass(b.currentClass),a("."+b.paginationClass+" li a",c).click(function(){return b.play&&y(),q=a(this).attr("href").match("[^#/]+$"),n!=q&&w("pagination",j,q),!1}),a("a.link",c).click(function(){return b.play&&y(),q=a(this).attr("href").match("[^#/]+$")-1,n!=q&&w("pagination",j,q),!1}),b.play&&(v=setInterval(function(){w("next",i)},b.play),c.data("interval",v))})},a.fn.slides.option={preload:!1,preloadImage:"/img/loading.gif",container:"slides_container",generateNextPrev:!1,next:"next",prev:"prev",pagination:!0,generatePagination:!0,prependPagination:!1,paginationClass:"pagination",currentClass:"current",fadeSpeed:350,fadeEasing:"",slideSpeed:350,slideEasing:"",start:1,effect:"slide",crossfade:!1,randomize:!1,play:0,pause:0,hoverPause:!1,autoHeight:!1,autoHeightSpeed:350,bigTarget:!1,animationStart:function(){},animationComplete:function(){},slidesLoaded:function(){}},a.fn.randomize=function(b){function c(){return Math.round(Math.random())-.5}return a(this).each(function(){var d=a(this),e=d.children(),f=e.length;if(f>1){e.hide();var g=[];for(i=0;i<f;i++)g[g.length]=i;g=g.sort(c),a.each(g,function(a,c){var f=e.eq(c),g=f.clone(!0);g.show().appendTo(d),b!==undefined&&b(f,g),f.remove()})}})}})(jQuery)/***********************************************************************
*
*  Coda Slider 3
*  Kevin Batdorf
*
*  http://kevinbatdorf.github.com/codaslider
*
*  GPL license & MIT license
*
************************************************************************/
if(typeof Object.create!=="function"){Object.create=function(b){function a(){}a.prototype=b;return new a()}}(function(d,c,a,e){var b={init:function(g,h){var f=this;d("body").removeClass("coda-slider-no-js");d(".coda-slider").prepend('<p class="loading">Loading...<br /><img src="./img/ajax-loader.gif" width="220" height="19" alt="loading..." /></p>');f.elem=h;f.$elem=d(h);f.sliderId="#"+(f.$elem).attr("id");f.options=d.extend({},d.fn.codaSlider.options,g);f.sliderId="#"+(f.$elem).attr("id");f.build();if(f.options.autoSlide){f.autoSlide()}f.events();d("p.loading").remove()},build:function(){var f=this;if(d(f.sliderId).parent().attr("class")!="coda-slider-wrapper"){d(f.sliderId).wrap('<div id="'+(f.$elem).attr("id")+'-wrapper" class="coda-slider-wrapper"></div>')}d(f.sliderId+" > div").addClass("panel");f.panelClass=f.sliderId+" .panel";d(f.panelClass).wrapAll('<div class="panel-container"></div>');if(d(f.panelClass).children().attr("class")!="panel-wrapper"){d(f.panelClass).wrapInner('<div class="panel-wrapper"></div>')}f.panelContainer=(d(f.panelClass).parent());if(f.options.hashLinking){f.hash=(c.location.hash);f.hashPanel=(f.hash).replace("#","")}f.currentTab=(f.options.hashLinking&&f.hash)?f.hashPanel-1:f.options.firstPanelToLoad-1;if(f.options.autoHeight){d(f.sliderId).css("height",d(d(f.panelContainer).children()[f.currentTab]).height()+d(f.sliderId+"-wrapper .coda-nav-right").height())}if(f.options.dynamicTabs){f.addNavigation()}if(f.options.dynamicArrows){f.addArrows()}f.totalSliderWidth=d(f.sliderId).outerWidth(true)+d(d(f.sliderId).parent()).children("[class^=coda-nav-left]").outerWidth(true)+d(d(f.sliderId).parent()).children("[class^=coda-nav-right]").outerWidth(true);d(d(f.sliderId).parent()).css("width",f.totalSliderWidth);if(f.options.dynamicTabs){f.alignNavigation()}if(f.options.continuous){d(f.panelContainer).prepend(d(f.panelContainer).children().last().clone());d(f.panelContainer).append(d(f.panelContainer).children().eq(1).clone())}f.clickable=true;f.panelCount=d(f.panelClass).length;f.panelWidth=d(f.panelClass).outerWidth();f.totalWidth=f.panelCount*f.panelWidth;f.pSign="px";f.slideWidth=d(f.sliderId).width();d(f.panelContainer).css("margin-left",(-f.slideWidth*~~(f.options.continuous))+(-f.slideWidth*f.currentTab));f.setCurrent(f.currentTab);d(f.sliderId+" .panel-container").css("width",f.totalWidth)},addNavigation:function(){var f=this;var g='<div class="coda-nav"><ul></ul></div>';if(f.options.dynamicTabsPosition==="bottom"){d(f.sliderId).after(g)}else{d(f.sliderId).before(g)}d.each((f.$elem).find(f.options.panelTitleSelector),function(h){d(d(f.sliderId).parent()).find(".coda-nav ul").append('<li class="tab'+(h+1)+'"><a href="#'+(h+1)+'" title="'+d(this).text()+'">'+d(this).text()+"</a></li>")})},alignNavigation:function(){var f=this;f.totalNavWidth=0;var g="";if(f.options.dynamicArrowsGraphical){g="-arrow"}if(f.options.dynamicTabsAlign!="center"){d(d(f.sliderId).parent()).find(".coda-nav ul").css("margin-"+f.options.dynamicTabsAlign,d(d(f.sliderId).parent()).find(".coda-nav-"+f.options.dynamicTabsAlign+g).outerWidth(true)+parseInt(d(f.sliderId).css("margin-"+f.options.dynamicTabsAlign),10));d(d(f.sliderId).parent()).find(".coda-nav ul").css("float",f.options.dynamicTabsAlign)}else{d(d(f.sliderId).parent()).find(".coda-nav li a").each(function(){f.totalNavWidth+=d(this).outerWidth(true)});if(d.browser.msie){f.totalNavWidth=f.totalNavWidth+(5)}d(d(f.sliderId).parent()).find(".coda-nav ul").css("width",f.totalNavWidth+1)}},addArrows:function(){var f=this;d(f.sliderId).parent().addClass("arrows");if(f.options.dynamicArrowsGraphical){d(f.sliderId).before('<div class="coda-nav-left-arrow" data-dir="prev" title="Slide left"><a href="#"></a></div>');d(f.sliderId).after('<div class="coda-nav-right-arrow" data-dir="next" title="Slide right"><a href="#"></a></div>')}else{d(f.sliderId).before('<div class="coda-nav-left" data-dir="prev" title="Slide left"><a href="#">'+f.options.dynamicArrowLeftText+"</a></div>");d(f.sliderId).after('<div class="coda-nav-right" data-dir="next" title="Slide right"><a href="#">'+f.options.dynamicArrowRightText+"</a></div>")}},events:function(){var f=this;d(d(f.sliderId).parent()).find("[class^=coda-nav-]").on("click",function(g){if(!f.clickable&&f.options.continuous){return false}f.setCurrent(d(this).attr("class").split("-")[2]);if(f.options.continuous){f.clickable=false}return false});d(d(f.sliderId).parent()).find("[class^=coda-nav] li").on("click",function(g){if(!f.clickable&&f.options.continuous){return false}f.setCurrent(parseInt(d(this).attr("class").split("tab")[1],10)-1);if(f.options.continuous){f.clickable=false}return false});d("[data-ref*="+(f.sliderId).split("#")[1]+"]").on("click",function(g){if(!f.clickable&&f.options.continuous){return false}if(f.options.autoSlideControls){if(d(this).attr("name")==="stop"){d(this).html(f.options.autoSlideStartText).attr("name","start");clearTimeout(f.autoslideTimeout);return false}if(d(this).attr("name")==="start"){d(this).html(f.options.autoSlideStopText).attr("name","stop");f.setCurrent(f.currentTab+1);f.autoSlide();return false}}f.setCurrent(parseInt(d(this).attr("href").split("#")[1]-1,10));if(f.options.continuous){f.clickable=false}if(f.options.autoSlideStopWhenClicked){clearTimeout(f.autoslideTimeout)}return false});d(d(f.sliderId).parent()).find("*").on("click",function(g){if(f.options.autoSlideControls&&autoSlideStopWhenClicked){d("body").find("[data-ref*="+(f.sliderId).split("#")[1]+"][name=stop]").html(f.options.autoSlideStartText);clearTimeout(f.autoslideTimeout)}if(!f.clickable&&f.options.continuous){if(f.options.autoSlideStopWhenClicked){clearTimeout(f.autoslideTimeout)}return false}if(f.options.autoSlide){if(f.options.autoSlideStopWhenClicked){clearTimeout(f.autoslideTimeout)}else{f.autoSlide(clearTimeout(f.autoslideTimeout));f.clickable=true}}if(f.options.continuous){clearTimeout(f.continuousTimeout)}})},setCurrent:function(g){var f=this;if(f.clickable){if(typeof g=="number"){f.currentTab=g}else{f.currentTab+=(~~(g==="right")||-1);if(!f.options.continuous){f.currentTab=(f.currentTab<0)?this.panelCount-1:(f.currentTab%this.panelCount)}}if(f.options.continuous){f.panelHeightCount=f.currentTab+1;if(f.currentTab===f.panelCount-2){f.setTab=0}else{if(f.currentTab===-1){f.setTab=f.panelCount-3}else{f.setTab=f.currentTab}}}else{f.panelHeightCount=f.currentTab;f.setTab=f.currentTab}d(d(f.sliderId).parent()).find(".tab"+(f.setTab+1)+" a:first").addClass("current").parent().siblings().children().removeClass("current");if(f.options.hashLinking){if(f.options.continuous){if(f.currentTab===f.panelCount-2){c.location.hash=1}else{if(f.currentTab===-1){c.location.hash=f.panelCount-2}else{c.location.hash=f.currentTab+1}}}else{c.location.hash=f.currentTab+1}}this.transition()}},transition:function(){var f=this;if(f.options.autoHeight){d(f.panelContainer).parent().animate({height:d(d(f.panelContainer).children()[f.panelHeightCount]).height()},{easing:f.options.autoHeightEaseFunction,duration:f.options.autoHeightEaseDuration,queue:false})}if(f.options.continuous){f.marginLeft=-(f.currentTab*f.slideWidth)-f.slideWidth}else{f.marginLeft=-(f.currentTab*f.slideWidth)}(f.panelContainer).animate({"margin-left":f.marginLeft+f.pSign},{easing:f.options.slideEaseFunction,duration:f.options.slideEaseDuration,queue:false,complete:f.continuousSlide(f.options.slideEaseDuration+50)})},autoSlide:function(){var f=this;if(f.options.autoSlideInterval<f.options.slideEaseDuration){f.options.autoSlideInterval=(f.options.slideEaseDuration>f.options.autoHeightEaseDuration)?f.options.slideEaseDuration:f.options.autoHeightEaseDuration}if(f.options.continuous){f.clickable=false}f.autoslideTimeout=setTimeout(function(){f.setCurrent(f.options.autoSliderDirection);f.autoSlide()},f.options.autoSlideInterval)},continuousSlide:function(g){var f=this;if(f.options.continuous){f.continuousTimeout=setTimeout(function(){if(f.currentTab===f.panelCount-2){d(f.panelContainer).css("margin-left",-f.slideWidth+f.pSign);f.currentTab=0}else{if(f.currentTab===-1){d(f.panelContainer).css("margin-left",-(((f.slideWidth*f.panelCount)-(f.slideWidth*2)))+f.pSign);f.currentTab=(f.panelCount-3)}}f.clickable=true},g)}else{f.clickable=true}}};d.fn.codaSlider=function(f){return this.each(function(){var g=Object.create(b);g.init(f,this)})};d.fn.codaSlider.options={autoHeight:true,autoHeightEaseDuration:1500,autoHeightEaseFunction:"easeInOutExpo",autoSlide:false,autoSliderDirection:"right",autoSlideInterval:7000,autoSlideControls:false,autoSlideStartText:"Start",autoSlideStopText:"Stop",autoSlideStopWhenClicked:true,continuous:true,crossLinking:true,dynamicArrows:true,dynamicArrowsGraphical:false,dynamicArrowLeftText:"&#171; left",dynamicArrowRightText:"right &#187;",dynamicTabs:true,dynamicTabsAlign:"center",dynamicTabsPosition:"top",externalTriggerSelector:"a.xtrig",firstPanelToLoad:1,hashLinking:false,panelTitleSelector:"h2.title",slideEaseDuration:1500,slideEaseFunction:"easeInOutExpo"}})(jQuery,window,document);