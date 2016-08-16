/*
 * \\\ Example: SQLite database
 *
 * Use DataBase queries to add and retrieve content
 */

// opens the db if exists otherwise creates one
var db = fileio.openSqlLite('mydb.db')

// if db exists drop the existing table (reset)
db.execSql('DROP TABLE IF EXISTS veggies;')

// create and insert data
db.execSql('CREATE TABLE veggies ' + ' ( type TEXT, color TEXT);')
db.execSql('INSERT INTO veggies (type, color) VALUES ("carrot","orange");')
db.execSql('INSERT INTO veggies (type, color) VALUES ("lettuce","green");')

// check results
var columns = ['type', 'color']
var c = db.query('veggies', columns)

// go through results
console.log('we got ' + c.getCount() + ' results'); 
while (c.moveToNext()) {
  console.log(c.getString(0) + " " + c.getString(1))
}

db.close()
