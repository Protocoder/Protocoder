(function() {
  app.controller("HelpController", function($scope) {
    return $("pre code").each(function(i, e) {
      return hljs.highlightBlock(e);
    });
  });

}).call(this);
