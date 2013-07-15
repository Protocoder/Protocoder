app.filter("newlines", ->
  (text) ->
    text.replace /\n/g, "<br/>"
).filter "noHTML", ->
  (text) ->
    text.replace(/&/g, "&amp;").replace(/>/g, "&gt;").replace /</g, "&lt;"

