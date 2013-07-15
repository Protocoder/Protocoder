%w[unit functional integration performance].each do |type|
  desc "run #{type} tests"
  task "test:#{type}" do
    Dir.glob "./test/#{type}/*_test.rb" do |f|
      require f
    end
  end
end

desc "run all tests"
task "test" do
  Dir.glob "./test/**/*_test.rb" do |f|
    require f
  end
end
