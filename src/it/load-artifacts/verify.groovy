def buildLogFile = new File( basedir, "build.log");

assert buildLogFile.isFile()
assert buildLogFile.getText("UTF-8") =~ /Read and set artifact file save-artifacts-it-1\.0-SNAPSHOT\.jar/