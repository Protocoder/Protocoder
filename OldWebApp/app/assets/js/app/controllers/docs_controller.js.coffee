app.controller "DocsController", ['$scope', '$http', '$timeout', ($scope, $http, $timeout) ->
  
  $scope.entries = []
  searchTimeout = undefined
  cache = []
  converter = new Showdown.converter()
    
  $http.get('/docs.json').then (res) ->
    for name, entry of res.data.entries
      unless cache[name]
        cache[name] =
          name: name
          desc: converter.makeHtml(entry.desc)
          usage: converter.makeHtml(entry.usage)
          
      cache[name] ||= entry
  
  updateSearch = () ->
    search = $scope.search
    rank = (term, entries) ->
      rankings = []
      term = term.toLowerCase()
      
      for name, entry of entries
        keywords = entry.usage.split(" ")
        ranking =
          rank: 1
          entry: entry
        if name.toLowerCase().indexOf(term) >= 0
          ranking.rank += 60
        for keyword in keywords
          if keyword.indexOf(term) >= 0
            ranking.rank += 1
          
          rankings.push ranking
      
      bestMatch =
        rank: 0
      for ranking in rankings
        if ranking.rank > bestMatch.rank
          bestMatch = ranking
      
      bestMatch.entry
    
    if search
      matches = rank(search, cache)
      searchTimeout = $timeout () ->
        $scope.bestMatch = matches
    else
      $scope.bestMatch = null
    
  $scope.$watch 'search', updateSearch
  ]