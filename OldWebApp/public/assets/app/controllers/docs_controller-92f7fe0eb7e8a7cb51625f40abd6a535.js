(function() {
  app.controller("DocsController", [
    '$scope', '$http', '$state', '$stateParams', '$timeout', function($scope, $http, $state, $stateParams, $timeout) {
      var cache, converter, searchTimeout, updateSearch;

      $scope.entries = [];
      searchTimeout = void 0;
      cache = [];
      converter = new Showdown.converter();
      $http.get('/docs.json').then(function(res) {
        var entry, name, _ref, _results;

        _ref = res.data.entries;
        _results = [];
        for (name in _ref) {
          entry = _ref[name];
          if (!cache[name]) {
            cache[name] = {
              name: name,
              desc: converter.makeHtml(entry.desc),
              usage: converter.makeHtml(entry.usage)
            };
          }
          _results.push(cache[name] || (cache[name] = entry));
        }
        return _results;
      });
      updateSearch = function() {
        var matches, rank, search;

        search = $scope.search;
        rank = function(term, entries) {
          var bestMatch, entry, keyword, keywords, name, ranking, rankings, _i, _j, _len, _len1;

          rankings = [];
          term = term.toLowerCase();
          for (name in entries) {
            entry = entries[name];
            keywords = entry.usage.split(" ");
            ranking = {
              rank: 1,
              entry: entry
            };
            if (name.toLowerCase().indexOf(term) >= 0) {
              ranking.rank += 60;
            }
            for (_i = 0, _len = keywords.length; _i < _len; _i++) {
              keyword = keywords[_i];
              if (keyword.indexOf(term) >= 0) {
                ranking.rank += 1;
              }
              rankings.push(ranking);
            }
          }
          bestMatch = {
            rank: 0
          };
          for (_j = 0, _len1 = rankings.length; _j < _len1; _j++) {
            ranking = rankings[_j];
            if (ranking.rank > bestMatch.rank) {
              bestMatch = ranking;
            }
          }
          return bestMatch.entry;
        };
        if (search) {
          matches = rank(search, cache);
          return searchTimeout = $timeout(function() {
            return $scope.bestMatch = matches;
          });
        } else {
          return $scope.bestMatch = null;
        }
      };
      return $scope.$watch('search', updateSearch);
    }
  ]);

}).call(this);
