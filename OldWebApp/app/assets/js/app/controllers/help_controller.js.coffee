app.controller "HelpController", ($scope) ->
  $("pre code").each (i, e) ->
    hljs.highlightBlock e
  