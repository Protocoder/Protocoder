#= require app/main
#= require_tree ./config
#= require_tree ./app
#= require_self
Zepto ($) ->
  hljs.tabReplace = '  '
  hljs.initHighlightingOnLoad()
  $(document).foundation()