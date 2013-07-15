require "yaml"

namespace :db do

  database_url = YAML.load_file("./config/database.yml")["default"]["url"]

  desc "Perform migration reset (full erase and migration up)"
  task :reset do
    puts `sequel -Etm ./db/migrations -M 0 #{database_url}`
    puts `sequel -Etm ./db/migrations #{database_url}`
    puts "<= rake db:reset executed"
  end

  desc "Perform migration up/down to VERSION"
  task :version do
    version = ENV['VERSION'].to_i
    raise "No VERSION was provided" if version.nil?
    puts `sequel -Etm db/migrations -M #{version} #{database_url}`
    puts "<= rake db:version version=[#{version}] executed"
  end

  desc "Perform migration up to latest migration available"
  task :migrate do
    puts `sequel -Etm ./db/migrations #{database_url}`
    puts "<= rake db:migrate executed"
  end

  desc "Perform migration down (erase all data)"
  task :rollback do
    puts `sequel -Etm ./db/migrations -M 0 #{database_url}`
    puts "<= rake db:rollback executed"
  end

  desc "Perform schema dump (dumping the current schema as a migration)"
  task :dump do
    raise "No FILENAME was provided" if ENV["FILENAME"].nil?
    puts `sequel -Etd #{database_url} > ./db/migrations/#{ENV["FILENAME"]}`
    puts "<= rake db:dump executed"
  end
end
