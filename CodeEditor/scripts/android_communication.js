var PUSH_CODE = "/push_code"; 
var GET_CODE = "/get_code";
var LIST_APPS = "/list_apps";
var RUN_APP = "/run_app";
var REMOVE_APP = "/remove_app";
var EXECUTE_REMOTE = "/execute_remote_function";

$.get(LIST_APPS, function(data) {
  alert('Load was performed. ' + data);
});