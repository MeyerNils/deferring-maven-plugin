def buildLogFile = new File( basedir, "build.log");

assert buildLogFile.isFile()
assert buildLogFile.getText("UTF-8") =~ /Read and set artifact version 1\.0\.0-SNAPSHOT/