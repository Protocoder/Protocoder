namespace :metric do

  desc "project statistics"
  task 'stat' do
    puts "All:"
    stat_files Dir.glob('**/*.{rb,slim,coffee,scss}')
    puts "\nRuby:"
    stat_files Dir.glob('**/*.rb') - Dir.glob('test/**/*.rb')
  end
end

private

def stat_files fs
  c = 0
  fc = 0
  total_size = 0.0
  fs.each do |f|
    fc += 1
    data = File.binread f
    c += data.count "\n"
    total_size += data.bytesize
  end
  puts "files: #{fc}"
  puts "lines: #{c}"
  puts "chars: #{total_size.to_i}"
end
