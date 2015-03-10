#3> <> prov:specializationOf <https://github.com/timrdf/CKANClient-J/blob/master/pom.sh> .
#
# Put the dependencies into local .m2 repository, so that they are available for other local projects that need JCKANCLient

ant dist

#vn install:install-file -DgroupId=org.okfn                  -DartifactId=ckanclient-j    -Dversion=1.7          -Dfile=JCKANClient-1.7.jar           -Dpackaging=jar -DgeneratePom=true
#mvn install:install-file -DgroupId=edu.rpi.tw                -DartifactId=ckanclient-j    -Dversion=1.7-SNAPSHOT -Dfile=dist/lib/JCKANClient-1.7.jar  -Dpackaging=jar -DgeneratePom=true
# ^^ now compiling for java 1.8 (changed build.xml) \\//
mvn install:install-file -DgroupId=edu.rpi.tw                -DartifactId=ckanclient-j    -Dversion=1.78-SNAPSHOT -Dfile=dist/lib/JCKANClient-1.7.jar  -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=commons-logging           -DartifactId=commons-logging -Dversion=1.1.1        -Dfile=lib/commons-logging-1.1.1.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.google.code.gson      -DartifactId=gson            -Dversion=2.2          -Dfile=lib/gson-2.2.jar              -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=org.apache.httpcomponents -DartifactId=httpclient      -Dversion=4.1.3        -Dfile=lib/httpclient-4.1.3.jar      -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=org.apache.httpcomponents -DartifactId=httpcore        -Dversion=4.1.4        -Dfile=lib/httpcore-4.1.4.jar        -Dpackaging=jar -DgeneratePom=true
