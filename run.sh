## This is the script file to run Java with C-lightning node
## Look here to see how setting a plugin in C-lightning ... TODO ADD link

DIR_JAR="build/libs/"
NAME_JAR="lightning-rest.jar"

# If the directory not exist I compile the jar
# and only after I run it.
if [ ! -d "$DIR_JAR" ]; then
  ./gradlew clean && ./gradlew jar
fi
java -jar $DIR_JAR$NAME_JAR