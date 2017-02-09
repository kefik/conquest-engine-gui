rm -rf bin
find . -type f \( -iname "*.java" \) > sources.txt
mkdir bin
javac -d bin @sources.txt
rm sources.txt
mkdir -p bin/conquest/view/resources/images
cp src/conquest/view/resources/images/* bin/conquest/view/resources/images/

# max number of rounds | bot command timeout | bot 1 init | bot 2 init | visualization | replay file
java -cp bin conquest.Conquest 100 5000 "internal:conquest.bot.BotStarter" "process:java -cp bin conquest.bot.BotStarter" true replay.log
