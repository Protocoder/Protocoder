ENV['RACK_ENV'] ||= "test"
require_relative "../config/boot.rb"

# factory_girl
dir = File.expand_path File.dirname(__FILE__)
Dir.glob "#{dir}/factory/**/*_factory.rb" do |f|
  require f
end

require "minitest/autorun"
require "rack/test"
DatabaseCleaner.strategy = :truncation

class TestCase < MiniTest::Unit::TestCase
  include FactoryGirl::Syntax::Methods
  def initialize *xs
    super
    DatabaseCleaner.clean
  end
end

class FunctionalTestCase < TestCase
  include Rack::Test::Methods
  def app
    Sinatra::Application
  end
end
